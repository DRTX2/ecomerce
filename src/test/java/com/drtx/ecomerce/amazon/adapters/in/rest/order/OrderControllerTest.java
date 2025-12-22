package com.drtx.ecomerce.amazon.adapters.in.rest.order;

import com.drtx.ecomerce.amazon.adapters.in.rest.order.dto.OrderRequest;
import com.drtx.ecomerce.amazon.adapters.in.rest.order.dto.OrderResponse;
import com.drtx.ecomerce.amazon.adapters.in.rest.order.mappers.OrderRestMapper;
import com.drtx.ecomerce.amazon.core.model.Order;
import com.drtx.ecomerce.amazon.core.model.OrderState;
import com.drtx.ecomerce.amazon.core.model.Product;
import com.drtx.ecomerce.amazon.core.model.User;
import com.drtx.ecomerce.amazon.core.ports.in.rest.OrderUseCasePort;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Order Controller Tests (Standalone)")
class OrderControllerTest {

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @Mock
    private OrderUseCasePort orderUseCasePort;

    @Mock
    private OrderRestMapper orderMapper;

    private Order testOrder;
    private OrderRequest testOrderRequest;
    private OrderResponse testOrderResponse;

    @BeforeEach
    void setUp() {
        OrderController controller = new OrderController(orderUseCasePort, orderMapper);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new com.fasterxml.jackson.datatype.jsr310.JavaTimeModule());

        User testUser = new User();
        testUser.setId(1L);
        testUser.setName("John Doe");

        Product testProduct = new Product();
        testProduct.setId(1L);
        testProduct.setName("Test Product");
        testProduct.setPrice(new BigDecimal("99.99"));

        testOrder = new Order(
                1L,
                testUser,
                Arrays.asList(testProduct),
                new BigDecimal("99.99"),
                OrderState.PENDING,
                LocalDateTime.now(),
                null,
                "CREDIT_CARD");

        testOrderRequest = new OrderRequest(
                1L,
                testUser,
                Arrays.asList(testProduct),
                new BigDecimal("99.99"),
                OrderState.PENDING,
                LocalDateTime.now(),
                null,
                "CREDIT_CARD");

        testOrderResponse = new OrderResponse();
    }

    @Test
    @DisplayName("GET /api/orders - Should return all orders")
    void testGetAllOrders() throws Exception {
        // Given
        List<Order> orders = Arrays.asList(testOrder);
        when(orderUseCasePort.getAllOrders()).thenReturn(orders);
        when(orderMapper.toResponse(any(Order.class))).thenReturn(testOrderResponse);

        // When & Then
        mockMvc.perform(get("/api/orders")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(orderUseCasePort, times(1)).getAllOrders();
    }

    @Test
    @DisplayName("POST /api/orders - Should create new order")
    void testCreateOrder() throws Exception {
        // Given
        when(orderMapper.toDomain(any(OrderRequest.class))).thenReturn(testOrder);
        when(orderUseCasePort.createOrder(any(Order.class))).thenReturn(testOrder);
        when(orderMapper.toResponse(testOrder)).thenReturn(testOrderResponse);

        // When & Then
        mockMvc.perform(post("/api/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testOrderRequest)))
                .andExpect(status().isOk());

        verify(orderUseCasePort, times(1)).createOrder(any(Order.class));
    }

    @Test
    @DisplayName("POST /api/orders - Should return 400 when user is null")
    void testCreateOrder_ValidationFail_NullUser() throws Exception {
        // Given
        OrderRequest invalidRequest = new OrderRequest(
                1L,
                null, // User is null, @NotNull violation
                Arrays.asList(new Product()),
                new BigDecimal("99.99"),
                OrderState.PENDING,
                LocalDateTime.now(),
                null,
                "CREDIT_CARD");

        // When & Then
        mockMvc.perform(post("/api/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());

        verify(orderUseCasePort, never()).createOrder(any(Order.class));
    }

    @Test
    @DisplayName("POST /api/orders - Should return 400 when products list is empty")
    void testCreateOrder_ValidationFail_EmptyProducts() throws Exception {
        // Given
        OrderRequest invalidRequest = new OrderRequest(
                1L,
                new User(),
                new ArrayList<>(), // Products empty, @NotEmpty violation
                new BigDecimal("99.99"),
                OrderState.PENDING,
                LocalDateTime.now(),
                null,
                "CREDIT_CARD");

        // When & Then
        mockMvc.perform(post("/api/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());

        verify(orderUseCasePort, never()).createOrder(any(Order.class));
    }

    @Test
    @DisplayName("GET /api/orders/{id} - Should return order when found")
    void testGetOrderById_Found() throws Exception {
        // Given
        when(orderUseCasePort.getOrderById(1L)).thenReturn(Optional.of(testOrder));
        when(orderMapper.toResponse(testOrder)).thenReturn(testOrderResponse);

        // When & Then
        mockMvc.perform(get("/api/orders/{id}", 1L)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(orderUseCasePort, times(1)).getOrderById(1L);
    }

    @Test
    @DisplayName("GET /api/orders/{id} - Should return 404 when order not found")
    void testGetOrderById_NotFound() throws Exception {
        // Given
        when(orderUseCasePort.getOrderById(999L)).thenReturn(Optional.empty());

        // When & Then
        mockMvc.perform(get("/api/orders/{id}", 999L)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(orderUseCasePort, times(1)).getOrderById(999L);
    }

    @Test
    @DisplayName("PUT /api/orders/{id} - Should update order")
    void testUpdateOrder() throws Exception {
        // Given
        when(orderMapper.toDomain(any(OrderRequest.class))).thenReturn(testOrder);
        when(orderUseCasePort.updateOrder(any(Order.class))).thenReturn(testOrder);
        when(orderMapper.toResponse(testOrder)).thenReturn(testOrderResponse);

        // When & Then
        mockMvc.perform(put("/api/orders/{id}", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testOrderRequest)))
                .andExpect(status().isOk());

        verify(orderUseCasePort, times(1)).updateOrder(any(Order.class));
    }

    @Test
    @DisplayName("DELETE /api/orders/{id} - Should delete order")
    void testDeleteOrder() throws Exception {
        // Given
        doNothing().when(orderUseCasePort).deleteOrder(1L);

        // When & Then
        mockMvc.perform(delete("/api/orders/{id}", 1L)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verify(orderUseCasePort, times(1)).deleteOrder(1L);
    }
}
