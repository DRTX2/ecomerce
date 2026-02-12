package com.drtx.ecomerce.amazon.application.usecases;
import com.drtx.ecomerce.amazon.application.usecases.order.OrderUseCaseImpl;

import com.drtx.ecomerce.amazon.core.model.order.Order;
import com.drtx.ecomerce.amazon.core.model.order.OrderSearchCriteria;
import com.drtx.ecomerce.amazon.core.model.order.OrderState;
import com.drtx.ecomerce.amazon.core.model.pagination.PageRequest;
import com.drtx.ecomerce.amazon.core.model.pagination.PageResponse;
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
import org.springframework.data.domain.PageImpl;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

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
    private UUID orderUuid;

    @BeforeEach
    void setUp() {
        testUser = new User(
                1L,
                UUID.randomUUID(),
                "John Doe",
                "john@example.com",
                "password123",
                "123 Main St",
                "555-0100",
                UserRole.USER,
                true,
                false);

        orderUuid = UUID.randomUUID();
        testOrder = new Order(
                1L,
                orderUuid,
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
                UUID.randomUUID(),
                testUser,
                List.of(),
                new BigDecimal("149.99"),
                OrderState.PENDING,
                LocalDateTime.now(),
                null,
                List.of());

        Order savedOrder = new Order(
                2L,
                newOrder.getUuid(),
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
        when(orderRepositoryPort.findById(1L)).thenReturn(Optional.of(testOrder));

        // When
        Optional<Order> result = orderUseCase.getOrderById(1L);

        // Then
        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(testOrder);
        verify(orderRepositoryPort, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Should return empty when getting non-existent order")
    void shouldReturnEmptyWhenGettingNonExistentOrder() {
        // Given
        when(orderRepositoryPort.findById(99L)).thenReturn(Optional.empty());

        // When
        Optional<Order> result = orderUseCase.getOrderById(99L);

        // Then
        assertThat(result).isEmpty();
        verify(orderRepositoryPort, times(1)).findById(99L);
    }

    @Test
    @DisplayName("Should get all orders paginated")
    void shouldGetAllOrdersPaginated() {
        // Given
        OrderSearchCriteria criteria = new OrderSearchCriteria(null, null, new PageRequest(0, 10, null, null));
        org.springframework.data.domain.Page<Order> ordersPage = new PageImpl<>(List.of(testOrder));
        when(orderRepositoryPort.searchOrders(any(OrderSearchCriteria.class))).thenReturn(ordersPage);

        // When
        PageResponse<Order> result = orderUseCase.getAllOrders(criteria);

        // Then
        assertThat(result.content()).hasSize(1);
        assertThat(result.content().get(0)).isEqualTo(testOrder);
        verify(orderRepositoryPort, times(1)).searchOrders(criteria);
    }

    @Test
    @DisplayName("Should update order successfully")
    void shouldUpdateOrderSuccessfully() {
        // Given
        Order updatedOrder = new Order(
                1L,
                orderUuid,
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
}
