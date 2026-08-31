package com.drtx.ecomerce.amazon.application.usecases.cart;

import com.drtx.ecomerce.amazon.core.model.exceptions.DomainExceptionFactory;
import com.drtx.ecomerce.amazon.core.model.order.Cart;
import com.drtx.ecomerce.amazon.core.model.user.User;
import com.drtx.ecomerce.amazon.core.ports.out.persistence.CartRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CartUseCaseImpl implements com.drtx.ecomerce.amazon.core.ports.in.rest.CartUseCasePort {
    private final CartRepositoryPort repositort;

    @Override
    public Cart createCart(Cart cart, User actor) {
        cart.setUser(actor);
        return repositort.save(cart);
    }

    @Override
    public Optional<Cart> getCartById(Long id, User actor) {
        return repositort.findById(id).map(cart -> requireOwner(cart, actor));
    }

    @Override
    public List<Cart> getAllCarts(User actor) {
        return repositort.findAll(actor.getId());
    }

    @Override
    public Cart updateCart(Long id, Cart cart, User actor) {
        Cart existing = repositort.findById(id)
                .orElseThrow(() -> DomainExceptionFactory.cartNotFound(id));

        requireOwner(existing, actor);
        cart.setId(id);
        cart.setUser(existing.getUser());
        return repositort.update(cart);
    }

    @Override
    public void deleteCart(Long id, User actor) {
        Cart existing = repositort.findById(id)
                .orElseThrow(() -> DomainExceptionFactory.cartNotFound(id));

        requireOwner(existing, actor);
        repositort.delete(id);
    }

    private Cart requireOwner(Cart cart, User actor) {
        if (cart.getUser() == null || actor == null || !cart.getUser().getId().equals(actor.getId())) {
            // Return 404 to avoid revealing whether another user's cart exists.
            throw DomainExceptionFactory.cartNotFound(cart.getId());
        }
        return cart;
    }
}
