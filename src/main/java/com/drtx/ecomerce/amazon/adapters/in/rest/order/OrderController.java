package com.drtx.ecomerce.amazon.adapters.in.rest.order;

import com.drtx.ecomerce.amazon.adapters.in.rest.order.dto.OrderRequest;
import com.drtx.ecomerce.amazon.adapters.in.rest.order.dto.OrderResponse;
import com.drtx.ecomerce.amazon.adapters.in.rest.order.mappers.OrderRestMapper;
import com.drtx.ecomerce.amazon.adapters.in.security.SecurityUserDetails;
import com.drtx.ecomerce.amazon.core.model.order.Order;
import com.drtx.ecomerce.amazon.core.ports.in.rest.OrderUseCasePort;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Órdenes", description = "Gestión de órdenes de compra")
@RestController
@RequestMapping("/orders")
@AllArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class OrderController {
    private final OrderUseCasePort orderUseCasePort;
    private final OrderRestMapper mapper;

    @Operation(summary = "Listar todas las órdenes", description = "Obtiene todas las órdenes del sistema. Requiere rol ADMIN.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista de órdenes",
                    content = @Content(schema = @Schema(implementation = OrderResponse.class))),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "403", description = "No autorizado - requiere ADMIN")
    })
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<OrderResponse>> getAllOrders() {
        return ResponseEntity.ok(
                orderUseCasePort.getAllOrders()
                        .stream()
                        .map(mapper::toResponse)
                        .toList());
    }

    @GetMapping("/mine")
    public ResponseEntity<List<OrderResponse>> getMyOrders(@AuthenticationPrincipal SecurityUserDetails principal) {
        return ResponseEntity.ok(orderUseCasePort.getMyOrders(principal.getUser()).stream().map(mapper::toResponse).toList());
    }

    @Operation(summary = "Confirmar carrito", description = "Crea una orden desde un carrito propio y reserva el stock disponible")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Orden creada",
                    content = @Content(schema = @Schema(implementation = OrderResponse.class))),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "400", description = "Carrito inválido o vacío"),
            @ApiResponse(responseCode = "409", description = "Stock insuficiente o carrito vacío")
    })
    @PostMapping("/from-cart/{cartId}")
    public ResponseEntity<OrderResponse> confirmCart(
            @PathVariable Long cartId,
            @AuthenticationPrincipal SecurityUserDetails principal) {
        Order createdOrder = orderUseCasePort.confirmCart(cartId, principal.getUser());
        return ResponseEntity.ok(mapper.toResponse(createdOrder));
    }

    @Operation(summary = "Obtener orden por ID", description = "Busca una orden específica por su identificador")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Orden encontrada",
                    content = @Content(schema = @Schema(implementation = OrderResponse.class))),
            @ApiResponse(responseCode = "404", description = "Orden no encontrada"),
            @ApiResponse(responseCode = "401", description = "No autenticado")
    })
    @GetMapping("/{id}")
    public ResponseEntity<OrderResponse> findProductById(
            @Parameter(description = "ID de la orden", example = "1", required = true)
            @PathVariable Long id,
            @AuthenticationPrincipal SecurityUserDetails principal) {
        return orderUseCasePort.getOrderById(id, principal.getUser())
                .map(mapper::toResponse)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Actualizar orden", description = "Actualiza una orden existente. Requiere rol ADMIN.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Orden actualizada",
                    content = @Content(schema = @Schema(implementation = OrderResponse.class))),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "403", description = "No autorizado - requiere ADMIN"),
            @ApiResponse(responseCode = "404", description = "Orden no encontrada"),
            @ApiResponse(responseCode = "400", description = "Datos de orden inválidos")
    })
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<OrderResponse> updateOrder(
            @Parameter(description = "ID de la orden", example = "1", required = true)
            @PathVariable Long id,
            @RequestBody @Valid OrderRequest orderRequest) {
        Order orderToUpdate = mapper.toDomain(orderRequest);
        orderToUpdate.setId(id);
        Order updatedOrder = orderUseCasePort.updateOrder(orderToUpdate);
        return ResponseEntity.ok(mapper.toResponse(updatedOrder));
    }

    @Operation(summary = "Eliminar orden", description = "Elimina una orden del sistema. Requiere rol ADMIN.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Orden eliminada"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "403", description = "No autorizado - requiere ADMIN"),
            @ApiResponse(responseCode = "404", description = "Orden no encontrada")
    })
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteOrderById(
            @Parameter(description = "ID de la orden", example = "1", required = true)
            @PathVariable Long id) {
        orderUseCasePort.deleteOrder(id);
        return ResponseEntity.noContent().build();
    }
}
