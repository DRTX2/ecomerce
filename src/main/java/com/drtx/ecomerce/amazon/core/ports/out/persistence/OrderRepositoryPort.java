package com.drtx.ecomerce.amazon.core.ports.out.persistence;

import com.drtx.ecomerce.amazon.core.model.order.Order;

import java.util.List;
import java.util.Optional;

public interface OrderRepositoryPort {
    Order save(Order order);

    Optional<Order> findById(Long id);

    List<Order> findAll();

    List<Order> findByUserId(Long userId);

    Order updateById(Order order);

    void delete(Long id);
}
