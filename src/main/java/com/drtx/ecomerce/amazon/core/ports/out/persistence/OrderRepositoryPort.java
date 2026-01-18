package com.drtx.ecomerce.amazon.core.ports.out.persistence;

import com.drtx.ecomerce.amazon.core.model.order.Order;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface OrderRepositoryPort {
    Order save(Order order);

    Optional<Order> findById(Long id);

    Optional<Order> findByUuid(UUID uuid);

    List<Order> findAll();

    List<Order> findByUserId(Long userId);

    Order updateById(Order order);

    Order updateByUuid(UUID uuid, Order order);

    void delete(Long id);

    void deleteByUuid(UUID uuid);
}
