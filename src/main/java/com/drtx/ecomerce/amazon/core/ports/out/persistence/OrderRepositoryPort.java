package com.drtx.ecomerce.amazon.core.ports.out.persistence;

import com.drtx.ecomerce.amazon.core.model.order.Order;
import com.drtx.ecomerce.amazon.core.model.order.OrderSearchCriteria;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface OrderRepositoryPort {
    Order save(Order order);

    Optional<Order> findById(Long id);

    Optional<Order> findByUuid(UUID uuid);

    Page<Order> searchOrders(OrderSearchCriteria searchCriteria);

    List<Order> findByUserId(Long userId);

    Order updateById(Order order);

    Order updateByUuid(UUID uuid, Order order);

    void delete(Long id);

    void deleteByUuid(UUID uuid);
}
