package com.drtx.ecomerce.amazon.core.ports.in.rest;

import com.drtx.ecomerce.amazon.core.model.order.Order;
import com.drtx.ecomerce.amazon.core.model.user.User;

import java.util.List;
import java.util.Optional;

public interface OrderUseCasePort {
    Order createOrder(Order order);
    Optional<Order> getOrderById(Long id, User actor);
    List<Order> getMyOrders(User actor);
    Order confirmCart(Long cartId, User actor);
    List<Order> getAllOrders();
    Order updateOrder(Order order);
    void deleteOrder(Long id);
}
