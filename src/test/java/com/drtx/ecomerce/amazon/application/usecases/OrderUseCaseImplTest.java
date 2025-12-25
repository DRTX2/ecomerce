package com.drtx.ecomerce.amazon.application.usecases;

import com.drtx.ecomerce.amazon.core.model.order.Order;
import com.drtx.ecomerce.amazon.core.model.order.OrderState;
import com.drtx.ecomerce.amazon.core.model.user.User;
import com.drtx.ecomerce.amazon.core.model.user.UserRole;
import com.drtx.ecomerce.amazon.core.ports.out.persistence.OrderRepositoryPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("OrderUseCaseImpl Unit Tests")
class OrderUseCaseImplTest {

    @Mock
    private OrderRepositoryPort orderRepositoryPort;

    @InjectMocks
    private OrderUseCaseImpl orderUseCase;

    private Order testOrder;
    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User(
                1L,
                "John Doe",
                "john@example.com",
                "password123",
                "123 Main St",
                "555-0100",
                UserRole.USER);

        testOrder = new Order(
                1L,
                testUser,
                List.of(),
                new BigDecimal("299.99"),
                OrderState.PENDING,
                LocalDateTime.now(),
                null,
                List.of());
    }

    @Test
    @DisplayName("Should create order successfully")
    void shouldCreateOrderSuccessfully() {
        // Given
        Order newOrder = new Order(
                null,
                testUser,
                List.of(),
                new BigDecimal("149.99"),
                OrderState.PENDING,
                LocalDateTime.now(),
                null,
                List.of());

        Order savedOrder = new Order(
                2L,
                testUser,
                List.of(),
                new BigDecimal("149.99"),
                OrderState.PENDING,
                newOrder.getCreatedAt(),
                null,
                List.of());

        when(orderRepositoryPort.save(any(Order.class))).thenReturn(savedOrder);

        // When
        Order result = orderUseCase.createOrder(newOrder);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(2L);
        assertThat(result.getUser()).isEqualTo(testUser);
        assertThat(result.getTotal()).isEqualByComparingTo(new BigDecimal("149.99"));
        verify(orderRepositoryPort, times(1)).save(newOrder);
    }

    @Test
    @DisplayName("Should get order by ID successfully")
    void shouldGetOrderByIdSuccessfully() {
        // Given
        Long orderId = 1L;
        when(orderRepositoryPort.findById(orderId)).thenReturn(Optional.of(testOrder));

        // When
        Optional<Order> result = orderUseCase.getOrderById(orderId);

        // Then
        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(orderId);
        assertThat(result.get().getUser()).isEqualTo(testUser);
        assertThat(result.get().getTotal()).isEqualByComparingTo(new BigDecimal("299.99"));
        verify(orderRepositoryPort, times(1)).findById(orderId);
    }

    @Test
    @DisplayName("Should return empty when order not found by ID")
    void shouldReturnEmptyWhenOrderNotFoundById() {
        // Given
        Long orderId = 999L;
        when(orderRepositoryPort.findById(orderId)).thenReturn(Optional.empty());

        // When
        Optional<Order> result = orderUseCase.getOrderById(orderId);

        // Then
        assertThat(result).isEmpty();
        verify(orderRepositoryPort, times(1)).findById(orderId);
    }

    @Test
    @DisplayName("Should get all orders successfully")
    void shouldGetAllOrdersSuccessfully() {
        // Given
        Order order2 = new Order(
                2L,
                testUser,
                List.of(),
                new BigDecimal("499.99"),
                OrderState.SENT,
                LocalDateTime.now(),
                null,
                List.of());

        Order order3 = new Order(
                3L,
                testUser,
                List.of(),
                new BigDecimal("99.99"),
                OrderState.DELIVERED,
                LocalDateTime.now().minusDays(5),
                LocalDateTime.now(),
                List.of());

        List<Order> orders = Arrays.asList(testOrder, order2, order3);
        when(orderRepositoryPort.findAll()).thenReturn(orders);

        // When
        List<Order> result = orderUseCase.getAllOrders();

        // Then
        assertThat(result).hasSize(3);
        assertThat(result).containsExactlyInAnyOrder(testOrder, order2, order3);
        verify(orderRepositoryPort, times(1)).findAll();
    }

    @Test
    @DisplayName("Should return empty list when no orders exist")
    void shouldReturnEmptyListWhenNoOrdersExist() {
        // Given
        when(orderRepositoryPort.findAll()).thenReturn(List.of());

        // When
        List<Order> result = orderUseCase.getAllOrders();

        // Then
        assertThat(result).isEmpty();
        verify(orderRepositoryPort, times(1)).findAll();
    }

    @Test
    @DisplayName("Should update order successfully")
    void shouldUpdateOrderSuccessfully() {
        // Given
        Order updatedOrder = new Order(
                1L,
                testUser,
                List.of(),
                new BigDecimal("349.99"),
                OrderState.SENT,
                testOrder.getCreatedAt(),
                null,
                List.of());

        when(orderRepositoryPort.updateById(any(Order.class))).thenReturn(updatedOrder);

        // When
        Order result = orderUseCase.updateOrder(updatedOrder);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getTotal()).isEqualByComparingTo(new BigDecimal("349.99"));
        assertThat(result.getOrderState()).isEqualTo(OrderState.SENT);
        verify(orderRepositoryPort, times(1)).updateById(updatedOrder);
    }

    @Test
    @DisplayName("Should delete order successfully")
    void shouldDeleteOrderSuccessfully() {
        // Given
        Long orderId = 1L;
        doNothing().when(orderRepositoryPort).delete(orderId);

        // When
        orderUseCase.deleteOrder(orderId);

        // Then
        verify(orderRepositoryPort, times(1)).delete(orderId);
    }

    @Test
    @DisplayName("Should handle delete for non-existent order")
    void shouldHandleDeleteForNonExistentOrder() {
        // Given
        Long orderId = 999L;
        doNothing().when(orderRepositoryPort).delete(orderId);

        // When
        orderUseCase.deleteOrder(orderId);

        // Then
        verify(orderRepositoryPort, times(1)).delete(orderId);
    }
}
