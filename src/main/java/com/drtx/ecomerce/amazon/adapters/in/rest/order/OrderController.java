package com.drtx.ecomerce.amazon.adapters.in.rest.order;

import com.drtx.ecomerce.amazon.adapters.in.rest.order.dto.CheckoutRequest;
import com.drtx.ecomerce.amazon.adapters.in.rest.order.dto.OrderRequest;
import com.drtx.ecomerce.amazon.adapters.in.rest.order.dto.OrderResponse;
import com.drtx.ecomerce.amazon.adapters.in.rest.order.dto.UpdateOrderStateRequest;
import com.drtx.ecomerce.amazon.adapters.in.rest.order.mappers.OrderRestMapper;
import com.drtx.ecomerce.amazon.core.model.order.Order;
import com.drtx.ecomerce.amazon.core.model.pagination.PageResponse;
import com.drtx.ecomerce.amazon.core.ports.in.rest.OrderUseCasePort;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

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
     * Obtiene todas las órdenes (solo ADMIN) con paginación y filtros
     */
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PageResponse<OrderResponse>> getAllOrders(
            @RequestParam(required = false) UUID userUuid,
            @RequestParam(required = false) com.drtx.ecomerce.amazon.core.model.order.OrderState status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "DESC") String sortDirection
    ) {
        var criteria = new com.drtx.ecomerce.amazon.core.model.order.OrderSearchCriteria(
                Optional.ofNullable(userUuid),
                Optional.ofNullable(status),
                null, null, null, null,
                new com.drtx.ecomerce.amazon.core.model.pagination.PageRequest(
                        page,
                        size,
                        Optional.of(new com.drtx.ecomerce.amazon.core.model.pagination.PageRequest.Sort(
                                sortBy,
                                "ASC".equalsIgnoreCase(sortDirection) ? com.drtx.ecomerce.amazon.core.model.pagination.SortDirection.ASC : com.drtx.ecomerce.amazon.core.model.pagination.SortDirection.DESC
                        ))
                )
        );

        PageResponse<Order> ordersPage = orderUseCasePort.getAllOrders(criteria);

        return ResponseEntity.ok(PageResponse.of(
                ordersPage.content().stream().map(mapper::toResponse).toList(),
                ordersPage.page(),
                ordersPage.size(),
                ordersPage.totalElements()
        ));
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
     * Obtiene una orden por UUID
     */
    @GetMapping("/{uuid}")
    public ResponseEntity<OrderResponse> findOrderByUuid(@PathVariable UUID uuid) {
        return orderUseCasePort.getOrderByUuid(uuid)
                .map(mapper::toResponse)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Actualiza una orden completa por UUID (solo ADMIN)
     */
    @PutMapping("/{uuid}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<OrderResponse> updateOrderByUuid(@PathVariable UUID uuid,
            @RequestBody @Valid OrderRequest orderRequest) {
        Order orderToUpdate = mapper.toDomain(orderRequest);
        Order updatedOrder = orderUseCasePort.updateOrderByUuid(uuid, orderToUpdate);
        return ResponseEntity.ok(mapper.toResponse(updatedOrder));
    }

    /**
     * Actualiza el estado de una orden por UUID (ADMIN o SELLER)
     * Útil para marcar órdenes como enviadas, entregadas, etc.
     */
    @PatchMapping("/{uuid}/state")
    @PreAuthorize("hasAnyRole('ADMIN', 'SELLER')")
    public ResponseEntity<OrderResponse> updateOrderStateByUuid(
            @PathVariable UUID uuid,
            @RequestBody @Valid UpdateOrderStateRequest request) {
        Order updatedOrder = orderUseCasePort.updateOrderStateByUuid(uuid, request.newState());
        return ResponseEntity.ok(mapper.toResponse(updatedOrder));
    }

    /**
     * Cancela una orden por UUID (usuario puede cancelar sus propias órdenes PENDING)
     */
    @PostMapping("/{uuid}/cancel")
    public ResponseEntity<OrderResponse> cancelOrderByUuid(@PathVariable UUID uuid) {
        Order updatedOrder = orderUseCasePort.updateOrderStateByUuid(uuid,
                com.drtx.ecomerce.amazon.core.model.order.OrderState.CANCELED);
        return ResponseEntity.ok(mapper.toResponse(updatedOrder));
    }

    /**
     * Elimina una orden por UUID (solo ADMIN)
     */
    @DeleteMapping("/{uuid}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteOrderByUuid(@PathVariable UUID uuid) {
        orderUseCasePort.deleteOrderByUuid(uuid);
        return ResponseEntity.noContent().build();
    }

    // Legacy endpoints using Long ID (deprecated, maintain for backward compatibility)

    /**
     * @deprecated Use {@link #findOrderByUuid(UUID)} instead
     */
    @Deprecated
    @GetMapping("/by-id/{id}")
    public ResponseEntity<OrderResponse> findOrderById(@PathVariable Long id) {
        return orderUseCasePort.getOrderById(id)
                .map(mapper::toResponse)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * @deprecated Use {@link #updateOrderByUuid(UUID, OrderRequest)} instead
     */
    @Deprecated
    @PutMapping("/by-id/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<OrderResponse> updateOrder(@PathVariable Long id,
            @RequestBody @Valid OrderRequest orderRequest) {
        Order orderToUpdate = mapper.toDomain(orderRequest);
        orderToUpdate.setId(id);
        Order updatedOrder = orderUseCasePort.updateOrder(orderToUpdate);
        return ResponseEntity.ok(mapper.toResponse(updatedOrder));
    }

    /**
     * @deprecated Use {@link #updateOrderStateByUuid(UUID, UpdateOrderStateRequest)} instead
     */
    @Deprecated
    @PatchMapping("/by-id/{id}/state")
    @PreAuthorize("hasAnyRole('ADMIN', 'SELLER')")
    public ResponseEntity<OrderResponse> updateOrderState(
            @PathVariable Long id,
            @RequestBody @Valid UpdateOrderStateRequest request) {
        Order updatedOrder = orderUseCasePort.updateOrderState(id, request.newState());
        return ResponseEntity.ok(mapper.toResponse(updatedOrder));
    }

    /**
     * @deprecated Use {@link #cancelOrderByUuid(UUID)} instead
     */
    @Deprecated
    @PostMapping("/by-id/{id}/cancel")
    public ResponseEntity<OrderResponse> cancelOrder(@PathVariable Long id) {
        Order updatedOrder = orderUseCasePort.updateOrderState(id,
                com.drtx.ecomerce.amazon.core.model.order.OrderState.CANCELED);
        return ResponseEntity.ok(mapper.toResponse(updatedOrder));
    }

    /**
     * @deprecated Use {@link #deleteOrderByUuid(UUID)} instead
     */
    @Deprecated
    @DeleteMapping("/by-id/{id}")
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
