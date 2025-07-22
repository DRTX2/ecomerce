package com.drtx.ecomerce.amazon.adapters.out.persistence.cart;

import com.drtx.ecomerce.amazon.core.model.Cart;
import com.drtx.ecomerce.amazon.core.ports.out.persistence.CartRepositoryPort;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@AllArgsConstructor
public class CartRepositoryAdapter implements CartRepositoryPort {
    private final CartPersistenceRepository repository;
    private final CartPersistenceMapper mapper;

    @Override
    public Cart save(Cart cart) {
        CartEntity entity = mapper.toEntity(cart);
        entity=repository.save(entity);
        return mapper.toDomain(entity);
    }

    @Override
    public List<Cart> findAll(Long userId) {
        return repository.findAll().stream().map(mapper::toDomain).toList();
    }

    @Override
    public Optional<Cart> findById(Long id) {
        return repository.findById(id).map(mapper::toDomain);
    }

    @Override
    public Cart update(Cart cart) {
        CartEntity entity= mapper.toEntity(cart);
        entity=repository.save(entity);
        return mapper.toDomain(entity);
    }

    @Override
    public void delete(Long id) {
        if(repository.existsById(id)) throw new EntityNotFoundException("User not found; id="+id);
        repository.deleteById(id);
    }
}
