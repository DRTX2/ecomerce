package com.drtx.ecomerce.amazon.adapters.out.persistence.cart;

import com.drtx.ecomerce.amazon.core.model.exceptions.EntityNotFoundException;
import com.drtx.ecomerce.amazon.core.model.order.Cart;
import com.drtx.ecomerce.amazon.core.ports.out.persistence.CartRepositoryPort;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
@AllArgsConstructor
public class CartRepositoryAdapter implements CartRepositoryPort {
    private final CartPersistenceRepository repository;
    private final CartPersistenceMapper mapper;

    @Override
    public Cart save(Cart cart) {
        CartEntity entity = mapper.toEntity(cart);
        entity = repository.save(entity);
        return mapper.toDomain(entity);
    }

    @Override
    public List<Cart> findAll(Long userId) {
        return repository.findByUserId(userId).stream().map(mapper::toDomain).toList();
    }

    @Override
    public Optional<Cart> findById(Long id) {
        return repository.findById(id).map(mapper::toDomain);
    }

    @Override
    public Optional<Cart> findByUuid(UUID uuid) {
        return repository.findByUuid(uuid).map(mapper::toDomain);
    }

    @Override
    public Cart update(Cart cart) {
        CartEntity entity = mapper.toEntity(cart);
        entity = repository.save(entity);
        return mapper.toDomain(entity);
    }

    @Override
    public Cart updateByUuid(UUID uuid, Cart cart) {
        CartEntity existingEntity = repository.findByUuid(uuid)
                .orElseThrow(() -> new EntityNotFoundException("Cart not found with uuid: " + uuid));

        // Update the existing entity with new values
        CartEntity updatedEntity = mapper.toEntity(cart);
        updatedEntity.setId(existingEntity.getId());
        updatedEntity.setUuid(existingEntity.getUuid());

        updatedEntity = repository.save(updatedEntity);
        return mapper.toDomain(updatedEntity);
    }

    @Override
    public void delete(Long id) {
        if (!repository.existsById(id))
            throw new EntityNotFoundException("Cart not found; id=" + id);
        repository.deleteById(id);
    }

    @Override
    public void deleteByUuid(UUID uuid) {
        CartEntity entity = repository.findByUuid(uuid)
                .orElseThrow(() -> new EntityNotFoundException("Cart not found with uuid: " + uuid));
        repository.delete(entity);
    }
}
