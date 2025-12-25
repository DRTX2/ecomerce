package com.drtx.ecomerce.amazon.adapters.in.rest.order;

import com.drtx.ecomerce.amazon.adapters.in.rest.order.dto.OrderRequest;
import com.drtx.ecomerce.amazon.adapters.in.rest.order.dto.OrderResponse;
import com.drtx.ecomerce.amazon.adapters.in.rest.order.mappers.OrderRestMapper;
import com.drtx.ecomerce.amazon.core.model.order.Order;
import com.drtx.ecomerce.amazon.core.ports.in.rest.OrderUseCasePort;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/orders")
@AllArgsConstructor
public class OrderController {
    private final OrderUseCasePort service;
    private final OrderRestMapper mapper;

    @GetMapping
    public ResponseEntity<List<OrderResponse>> getAllOrders() {
        return ResponseEntity.ok(
                service.getAllOrders()
                        .stream()
                        .map(mapper::toResponse)
                        .toList());
    }

    @PostMapping
    public ResponseEntity<OrderResponse> createOrder(@RequestBody @jakarta.validation.Valid OrderRequest orderRequest) {
        Order orderToCreate = mapper.toDomain(orderRequest);
        Order createdOrder = service.createOrder(orderToCreate);
        return ResponseEntity.ok(mapper.toResponse(createdOrder));
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderResponse> findProductById(@PathVariable Long id) {
        return service.getOrderById(id)
                .map(mapper::toResponse)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<OrderResponse> updateOrder(@PathVariable Long id,
            @RequestBody @jakarta.validation.Valid OrderRequest orderRequest) {
        Order orderToUpdate = mapper.toDomain(orderRequest);
        orderToUpdate.setId(id);
        Order updatedOrder = service.updateOrder(orderToUpdate);
        return ResponseEntity.ok(mapper.toResponse(updatedOrder));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOrderById(@PathVariable Long id) {
        service.deleteOrder(id);
        return ResponseEntity.noContent().build();
    }
}
