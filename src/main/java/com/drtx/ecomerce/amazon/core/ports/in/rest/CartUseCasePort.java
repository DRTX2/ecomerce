package com.drtx.ecomerce.amazon.core.ports.in.rest;

import com.drtx.ecomerce.amazon.core.model.order.Cart;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CartUseCasePort {
    Cart createCart(Cart cart);

    Optional<Cart> getCartById(Long id);
    Optional<Cart> getCartByUuid(UUID uuid);

    List<Cart> getAllCarts(Long userId);

    Cart updateCart(Long id, Cart cart);
    Cart updateCartByUuid(UUID uuid, Cart cart);

    void deleteCart(Long id);
    void deleteCartByUuid(UUID uuid);
}
