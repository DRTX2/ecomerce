package com.drtx.ecomerce.amazon.adapters.in.rest.order;

import com.drtx.ecomerce.amazon.adapters.in.rest.order.dto.OrderRequest;
import com.drtx.ecomerce.amazon.adapters.in.rest.order.dto.OrderResponse;
import com.drtx.ecomerce.amazon.adapters.in.rest.order.mappers.OrderRestMapper;
import com.drtx.ecomerce.amazon.core.model.order.Order;
import com.drtx.ecomerce.amazon.core.ports.in.rest.OrderUseCasePort;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/orders")
@AllArgsConstructor
public class OrderController {
    private final OrderUseCasePort orderUseCasePort;
    private final OrderRestMapper mapper;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<OrderResponse>> getAllOrders() {
        return ResponseEntity.ok(
                orderUseCasePort.getAllOrders()
                        .stream()
                        .map(mapper::toResponse)
                        .toList());
    }

    @PostMapping
    public ResponseEntity<OrderResponse> createOrder(@RequestBody @Valid OrderRequest orderRequest) {
        Order orderToCreate = mapper.toDomain(orderRequest);
        Order createdOrder = orderUseCasePort.createOrder(orderToCreate);
        return ResponseEntity.ok(mapper.toResponse(createdOrder));
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderResponse> findProductById(@PathVariable Long id) {
        return orderUseCasePort.getOrderById(id)
                .map(mapper::toResponse)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<OrderResponse> updateOrder(@PathVariable Long id,
            @RequestBody @Valid OrderRequest orderRequest) {
        Order orderToUpdate = mapper.toDomain(orderRequest);
        orderToUpdate.setId(id);
        Order updatedOrder = orderUseCasePort.updateOrder(orderToUpdate);
        return ResponseEntity.ok(mapper.toResponse(updatedOrder));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteOrderById(@PathVariable Long id) {
        orderUseCasePort.deleteOrder(id);
        return ResponseEntity.noContent().build();
    }
}
