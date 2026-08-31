package com.drtx.ecomerce.amazon.adapters.out.persistence.cart;

import com.drtx.ecomerce.amazon.core.model.order.Cart;
import com.drtx.ecomerce.amazon.core.ports.out.persistence.CartRepositoryPort;
import com.drtx.ecomerce.amazon.core.model.exceptions.EntityNotFoundException;
import com.drtx.ecomerce.amazon.adapters.out.persistence.product.ProductPersistenceRepository;
import com.drtx.ecomerce.amazon.adapters.out.persistence.user.UserPersistenceRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@AllArgsConstructor
public class CartRepositoryAdapter implements CartRepositoryPort {
    private final CartPersistenceRepository repository;
    private final CartPersistenceMapper mapper;
    private final UserPersistenceRepository userRepository;
    private final ProductPersistenceRepository productRepository;

    @Override
    public Cart save(Cart cart) {
        CartEntity entity = mapper.toEntity(cart);
        attachReferences(entity);
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
    public Cart update(Cart cart) {
        CartEntity entity = mapper.toEntity(cart);
        attachReferences(entity);
        entity = repository.save(entity);
        return mapper.toDomain(entity);
    }

    @Override
    public void delete(Long id) {
        if (!repository.existsById(id))
            throw new EntityNotFoundException("Cart not found; id=" + id);
        repository.deleteById(id);
    }

    private void attachReferences(CartEntity cart) {
        cart.setUser(userRepository.getReferenceById(cart.getUser().getId()));
        if (cart.getItems() != null) {
            cart.getItems().forEach(item -> {
                item.setCart(cart);
                item.setProduct(productRepository.getReferenceById(item.getProduct().getId()));
            });
        }
    }
}
