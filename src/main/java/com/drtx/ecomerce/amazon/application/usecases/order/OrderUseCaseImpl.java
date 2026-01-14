package com.drtx.ecomerce.amazon.application.usecases.order;

import com.drtx.ecomerce.amazon.core.model.exceptions.DomainExceptionFactory;
import com.drtx.ecomerce.amazon.core.model.order.*;
import com.drtx.ecomerce.amazon.core.model.user.User;
import com.drtx.ecomerce.amazon.core.ports.in.rest.OrderUseCasePort;
import com.drtx.ecomerce.amazon.core.ports.out.persistence.CartRepositoryPort;
import com.drtx.ecomerce.amazon.core.ports.out.persistence.OrderRepositoryPort;
import com.drtx.ecomerce.amazon.core.ports.out.persistence.UserRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderUseCaseImpl implements OrderUseCasePort {
    private final OrderRepositoryPort orderRepository;
    private final CartRepositoryPort cartRepository;
    private final UserRepositoryPort userRepository;

    // Valid state transitions map
    private static final Map<OrderState, Set<OrderState>> VALID_TRANSITIONS = Map.of(
            OrderState.PENDING, Set.of(OrderState.SENT, OrderState.CANCELED),
            OrderState.SENT, Set.of(OrderState.DELIVERED, OrderState.CANCELED),
            OrderState.DELIVERED, Set.of(), // Terminal state
            OrderState.CANCELED, Set.of() // Terminal state
    );

    @Override
    @Transactional
    public Order createOrder(Order order) {
        // Initialize order state and timestamps
        if (order.getOrderState() == null) {
            order.setOrderState(OrderState.PENDING);
        }
        if (order.getCreatedAt() == null) {
            order.setCreatedAt(LocalDateTime.now());
        }
        return orderRepository.save(order);
    }

    @Override
    @Transactional
    public Order createOrderFromCart(Long cartId, String userEmail) {
        // Find the cart
        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> DomainExceptionFactory.cartNotFound(cartId));

        // Verify user owns the cart
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> DomainExceptionFactory.userNotFound(userEmail));

        if (cart.getUser() == null || !cart.getUser().getId().equals(user.getId())) {
            throw DomainExceptionFactory.unauthorized("You are not authorized to checkout this cart");
        }

        // Validate cart has items
        if (cart.getItems() == null || cart.getItems().isEmpty()) {
            throw DomainExceptionFactory.invalidOperation("Cart is empty. Add items before checkout.");
        }

        // Convert cart items to order items (order will be set later during
        // persistence)
        List<OrderItem> orderItems = cart.getItems().stream()
                .map(cartItem -> {
                    OrderItem orderItem = new OrderItem();
                    orderItem.setProduct(cartItem.getProduct());
                    orderItem.setQuantity(cartItem.getQuantity());
                    orderItem.setPriceAtPurchase(cartItem.getProduct().getPrice());
                    return orderItem;
                })
                .collect(Collectors.toList());

        // Calculate total
        BigDecimal total = orderItems.stream()
                .map(item -> item.getPriceAtPurchase().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Create the order
        Order order = new Order(
                null,
                user,
                orderItems,
                total,
                OrderState.PENDING,
                LocalDateTime.now(),
                null,
                new ArrayList<>());

        Order savedOrder = orderRepository.save(order);

        // Clear the cart after successful order
        cartRepository.delete(cartId);

        return savedOrder;
    }

    @Override
    public Optional<Order> getOrderById(Long id) {
        return orderRepository.findById(id);
    }

    @Override
    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    @Override
    public List<Order> getOrdersByUserId(Long userId) {
        return orderRepository.findByUserId(userId);
    }

    @Override
    @Transactional
    public Order updateOrder(Order order) {
        // Verify order exists
        orderRepository.findById(order.getId())
                .orElseThrow(() -> DomainExceptionFactory.orderNotFound(order.getId()));

        return orderRepository.updateById(order);
    }

    @Override
    @Transactional
    public Order updateOrderState(Long orderId, OrderState newState) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> DomainExceptionFactory.orderNotFound(orderId));

        OrderState currentState = order.getOrderState();

        // Validate state transition
        if (!isValidTransition(currentState, newState)) {
            throw DomainExceptionFactory.invalidStateTransition(
                    currentState.name(),
                    newState.name(),
                    VALID_TRANSITIONS.get(currentState).toString());
        }

        order.setOrderState(newState);

        // Set delivered timestamp if transitioning to DELIVERED
        if (newState == OrderState.DELIVERED) {
            order.setDeliveredAt(LocalDateTime.now());
        }

        return orderRepository.updateById(order);
    }

    @Override
    @Transactional
    public void deleteOrder(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> DomainExceptionFactory.orderNotFound(id));

        // Only allow deletion of PENDING or CANCELED orders
        if (order.getOrderState() != OrderState.PENDING && order.getOrderState() != OrderState.CANCELED) {
            throw DomainExceptionFactory.invalidOperation(
                    "Cannot delete order in state: " + order.getOrderState().name() +
                            ". Only PENDING or CANCELED orders can be deleted.");
        }

        orderRepository.delete(id);
    }

    private boolean isValidTransition(OrderState from, OrderState to) {
        return VALID_TRANSITIONS.getOrDefault(from, Set.of()).contains(to);
    }
}
