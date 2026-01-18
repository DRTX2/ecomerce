package com.drtx.ecomerce.amazon.core.ports.out.persistence;

import com.drtx.ecomerce.amazon.core.model.order.Cart;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CartRepositoryPort {
    Cart save(Cart cart);

    List<Cart> findAll(Long userId);

    Optional<Cart> findById(Long id);
    Optional<Cart> findByUuid(UUID uuid);

    Cart update(Cart cart);
    Cart updateByUuid(UUID uuid, Cart cart);

    void delete(Long id);
    void deleteByUuid(UUID uuid);
}
