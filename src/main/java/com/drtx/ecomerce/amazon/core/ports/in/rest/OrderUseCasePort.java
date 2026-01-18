package com.drtx.ecomerce.amazon.core.ports.in.rest;

import com.drtx.ecomerce.amazon.core.model.order.Order;
import com.drtx.ecomerce.amazon.core.model.order.OrderState;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface OrderUseCasePort {
    Order createOrder(Order order);

    Order createOrderFromCart(Long cartId, String userEmail);

    Optional<Order> getOrderById(Long id);

    Optional<Order> getOrderByUuid(UUID uuid);

    List<Order> getAllOrders();

    List<Order> getOrdersByUserId(Long userId);

    Order updateOrder(Order order);

    Order updateOrderByUuid(UUID uuid, Order order);

    Order updateOrderState(Long orderId, OrderState newState);

    Order updateOrderStateByUuid(UUID uuid, OrderState newState);

    void deleteOrder(Long id);

    void deleteOrderByUuid(UUID uuid);
}
