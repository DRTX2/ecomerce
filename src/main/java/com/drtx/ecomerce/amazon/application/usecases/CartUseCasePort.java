package com.drtx.ecomerce.amazon.application.usecases;

import com.drtx.ecomerce.amazon.core.model.Cart;
import com.drtx.ecomerce.amazon.core.ports.in.CartServicePort;
import com.drtx.ecomerce.amazon.core.ports.out.CartRepositoryPort;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class CartUseCasePort implements CartServicePort {
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
    public List<Cart> getAllCarts(Long userId) {
        return repositort.findAll(userId);
    }

    @Override
    public Cart updateCart(Long id, Cart cart) {
        return repositort.update(cart);
    }

    @Override
    public void deleteCart(Long id) {
        repositort.delete(id);
    }
}
