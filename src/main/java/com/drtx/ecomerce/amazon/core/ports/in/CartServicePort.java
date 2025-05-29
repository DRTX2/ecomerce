package com.drtx.ecomerce.amazon.core.ports.in;

import com.drtx.ecomerce.amazon.core.model.Cart;

import java.util.List;
import java.util.Optional;

public interface CartServicePort {
    Cart createCart(Cart cart);
    Optional<Cart> getCartById(Long id);
    List<Cart> getAllCarts(Long userId);
    Cart updateCart(Long id, Cart cart);
    void deleteCart(Long id);
}
