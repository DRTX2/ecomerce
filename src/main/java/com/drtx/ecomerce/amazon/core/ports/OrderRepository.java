package com.drtx.ecomerce.amazon.core.ports;

import com.drtx.ecomerce.amazon.core.models.Order;

import java.util.List;
import java.util.Optional;

public interface OrderRepository {
    Order save(Order order);
    Optional<Order> findById(Long id);
    List<Order> findAll();
    Order updateById(Long id, Order order);
    void delete(Long id);
}
