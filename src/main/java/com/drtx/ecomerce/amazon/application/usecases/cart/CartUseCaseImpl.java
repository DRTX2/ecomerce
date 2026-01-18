package com.drtx.ecomerce.amazon.application.usecases.cart;

import com.drtx.ecomerce.amazon.core.model.exceptions.DomainExceptionFactory;
import com.drtx.ecomerce.amazon.core.model.order.Cart;
import com.drtx.ecomerce.amazon.core.ports.in.rest.CartUseCasePort;
import com.drtx.ecomerce.amazon.core.ports.out.persistence.CartRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CartUseCaseImpl implements CartUseCasePort {
    private final CartRepositoryPort repositort;

    @Override
    public Cart createCart(Cart cart) {
        return repositort.save(cart);
    }

    @Override
    public Optional<Cart> getCartById(Long id) {
        return repositort.findById(id);
    }

    @Override
    public Optional<Cart> getCartByUuid(UUID uuid) {
        return repositort.findByUuid(uuid);
    }

    @Override
    public List<Cart> getAllCarts(Long userId) {
        return repositort.findAll(userId);
    }

    @Override
    public Cart updateCart(Long id, Cart cart) {
        // Verify cart exists
        repositort.findById(id)
                .orElseThrow(() -> DomainExceptionFactory.cartNotFound(id));

        return repositort.update(cart);
    }

    @Override
    public Cart updateCartByUuid(UUID uuid, Cart cart) {
        // Verify cart exists
        repositort.findByUuid(uuid)
                .orElseThrow(() -> DomainExceptionFactory.cartNotFound(uuid));

        return repositort.updateByUuid(uuid, cart);
    }

    @Override
    public void deleteCart(Long id) {
        // Verify cart exists before deleting
        repositort.findById(id).orElseThrow(() -> DomainExceptionFactory.cartNotFound(id));

        repositort.delete(id);
    }

    @Override
    public void deleteCartByUuid(UUID uuid) {
        // Verify cart exists before deleting
        repositort.findByUuid(uuid)
                .orElseThrow(() -> DomainExceptionFactory.cartNotFound(uuid));

        repositort.deleteByUuid(uuid);
    }
}
