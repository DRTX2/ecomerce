package com.drtx.ecomerce.amazon.adapters.in.rest.order;

import com.drtx.ecomerce.amazon.adapters.in.rest.order.dto.CheckoutRequest;
import com.drtx.ecomerce.amazon.adapters.in.rest.order.dto.OrderRequest;
import com.drtx.ecomerce.amazon.adapters.in.rest.order.dto.OrderResponse;
import com.drtx.ecomerce.amazon.adapters.in.rest.order.dto.UpdateOrderStateRequest;
import com.drtx.ecomerce.amazon.adapters.in.rest.order.mappers.OrderRestMapper;
import com.drtx.ecomerce.amazon.core.model.order.Order;
import com.drtx.ecomerce.amazon.core.ports.in.rest.OrderUseCasePort;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller para gestión de órdenes.
 * Provee endpoints para CRUD de órdenes, checkout desde carrito,
 * y gestión de estados de orden.
 */
@RestController
@RequestMapping("/orders")
@AllArgsConstructor
public class OrderController {
    private final OrderUseCasePort orderUseCasePort;
    private final OrderRestMapper mapper;

    /**
     * Obtiene todas las órdenes (solo ADMIN)
     */
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<OrderResponse>> getAllOrders() {
        return ResponseEntity.ok(
                orderUseCasePort.getAllOrders()
                        .stream()
                        .map(mapper::toResponse)
                        .toList());
    }

    /**
     * Obtiene las órdenes del usuario autenticado
     */
    @GetMapping("/my-orders")
    public ResponseEntity<List<OrderResponse>> getMyOrders() {
        String userEmail = getAuthenticatedUserEmail();
        // TODO: Obtener userId del email y llamar getOrdersByUserId
        // Por ahora retornamos lista vacía hasta tener la implementación del adapter
        return ResponseEntity.ok(List.of());
    }

    /**
     * Crear una orden directamente (para uso administrativo)
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<OrderResponse> createOrder(@RequestBody @Valid OrderRequest orderRequest) {
        Order orderToCreate = mapper.toDomain(orderRequest);
        Order createdOrder = orderUseCasePort.createOrder(orderToCreate);
        return ResponseEntity.ok(mapper.toResponse(createdOrder));
    }

    /**
     * Checkout: Convierte un carrito en una orden
     * Este es el endpoint principal para crear órdenes desde el frontend
     */
    @PostMapping("/checkout")
    public ResponseEntity<OrderResponse> checkout(@RequestBody @Valid CheckoutRequest request) {
        String userEmail = getAuthenticatedUserEmail();
        Order createdOrder = orderUseCasePort.createOrderFromCart(request.cartId(), userEmail);
        return ResponseEntity.ok(mapper.toResponse(createdOrder));
    }

    /**
     * Obtiene una orden por ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<OrderResponse> findOrderById(@PathVariable Long id) {
        return orderUseCasePort.getOrderById(id)
                .map(mapper::toResponse)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Actualiza una orden completa (solo ADMIN)
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<OrderResponse> updateOrder(@PathVariable Long id,
            @RequestBody @Valid OrderRequest orderRequest) {
        Order orderToUpdate = mapper.toDomain(orderRequest);
        orderToUpdate.setId(id);
        Order updatedOrder = orderUseCasePort.updateOrder(orderToUpdate);
        return ResponseEntity.ok(mapper.toResponse(updatedOrder));
    }

    /**
     * Actualiza el estado de una orden (ADMIN o SELLER)
     * Útil para marcar órdenes como enviadas, entregadas, etc.
     */
    @PatchMapping("/{id}/state")
    @PreAuthorize("hasAnyRole('ADMIN', 'SELLER')")
    public ResponseEntity<OrderResponse> updateOrderState(
            @PathVariable Long id,
            @RequestBody @Valid UpdateOrderStateRequest request) {
        Order updatedOrder = orderUseCasePort.updateOrderState(id, request.newState());
        return ResponseEntity.ok(mapper.toResponse(updatedOrder));
    }

    /**
     * Cancela una orden (usuario puede cancelar sus propias órdenes PENDING)
     */
    @PostMapping("/{id}/cancel")
    public ResponseEntity<OrderResponse> cancelOrder(@PathVariable Long id) {
        Order updatedOrder = orderUseCasePort.updateOrderState(id,
                com.drtx.ecomerce.amazon.core.model.order.OrderState.CANCELED);
        return ResponseEntity.ok(mapper.toResponse(updatedOrder));
    }

    /**
     * Elimina una orden (solo ADMIN)
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteOrderById(@PathVariable Long id) {
        orderUseCasePort.deleteOrder(id);
        return ResponseEntity.noContent().build();
    }

    private String getAuthenticatedUserEmail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()
                && !authentication.getPrincipal().equals("anonymousUser")) {
            return authentication.getName();
        }
        return null;
    }
}
