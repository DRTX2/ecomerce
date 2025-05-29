package com.drtx.ecomerce.amazon.core.ports.out;

import com.drtx.ecomerce.amazon.core.model.Cart;

import java.util.List;
import java.util.Optional;

public interface CartRepositoryPort {
    Cart save(Cart cart);
    List<Cart> findAll(Long userId);
    Optional<Cart> findById(Long id);
    Cart update(Cart cart);
    void delete(Long id);
}
