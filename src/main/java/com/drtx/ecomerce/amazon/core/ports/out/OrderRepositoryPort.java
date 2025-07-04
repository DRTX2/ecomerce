package com.drtx.ecomerce.amazon.core.ports.out;

import com.drtx.ecomerce.amazon.core.model.Order;

import java.util.List;
import java.util.Optional;

public interface OrderRepositoryPort {
    Order save(Order order);
    Optional<Order> findById(Long id);
    List<Order> findAll();
    Order updateById(Long id, Order order);
    void delete(Long id);
}
