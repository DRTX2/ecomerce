package com.drtx.ecomerce.amazon.core.ports.in.rest;

import com.drtx.ecomerce.amazon.core.model.order.Cart;
import com.drtx.ecomerce.amazon.core.model.user.User;

import java.util.List;
import java.util.Optional;

public interface CartUseCasePort {
    Cart createCart(Cart cart, User actor);
    Optional<Cart> getCartById(Long id, User actor);
    List<Cart> getAllCarts(User actor);
    Cart updateCart(Long id, Cart cart, User actor);
    void deleteCart(Long id, User actor);
}
