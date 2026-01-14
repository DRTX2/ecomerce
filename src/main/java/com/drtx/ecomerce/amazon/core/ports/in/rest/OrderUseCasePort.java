package com.drtx.ecomerce.amazon.core.ports.in.rest;

import com.drtx.ecomerce.amazon.core.model.order.Order;
import com.drtx.ecomerce.amazon.core.model.order.OrderState;

import java.util.List;
import java.util.Optional;

public interface OrderUseCasePort {
    Order createOrder(Order order);

    Order createOrderFromCart(Long cartId, String userEmail);

    Optional<Order> getOrderById(Long id);

    List<Order> getAllOrders();

    List<Order> getOrdersByUserId(Long userId);

    Order updateOrder(Order order);

    Order updateOrderState(Long orderId, OrderState newState);

    void deleteOrder(Long id);
}
