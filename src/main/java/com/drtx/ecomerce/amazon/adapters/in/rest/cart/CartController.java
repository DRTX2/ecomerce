package com.drtx.ecomerce.amazon.adapters.in.rest.cart;

import com.drtx.ecomerce.amazon.adapters.in.rest.cart.dtos.CartRequest;
import com.drtx.ecomerce.amazon.adapters.in.rest.cart.dtos.CartResponse;
import com.drtx.ecomerce.amazon.adapters.in.rest.cart.mappers.CartRestMapper;
import com.drtx.ecomerce.amazon.adapters.in.security.SecurityUserDetails;

import com.drtx.ecomerce.amazon.core.model.order.Cart;
import com.drtx.ecomerce.amazon.core.ports.in.rest.CartUseCasePort;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Carrito", description = "Gestión del carrito de compras persistente")
@RestController
@RequestMapping("/carts")
@AllArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class CartController {
    private final CartUseCasePort cartService;
    private final CartRestMapper mapper;

    @Operation(summary = "Listar mis carritos", description = "Obtiene los carritos del usuario autenticado")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista de carritos",
                    content = @Content(schema = @Schema(implementation = CartResponse.class))),
            @ApiResponse(responseCode = "401", description = "No autenticado")
    })
    @GetMapping
    public ResponseEntity<List<CartResponse>> getAllCarts(
            @AuthenticationPrincipal SecurityUserDetails principal) {
        List<Cart> carts = cartService.getAllCarts(principal.getUser());
        return ResponseEntity.ok(
                carts.stream().map(mapper::toResponse).toList());
    }

    @Operation(summary = "Crear carrito", description = "Crea un nuevo carrito para el usuario")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Carrito creado",
                    content = @Content(schema = @Schema(implementation = CartResponse.class))),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "400", description = "Datos de carrito inválidos")
    })
    @PostMapping
    public ResponseEntity<CartResponse> createCart(
            @RequestBody @jakarta.validation.Valid CartRequest cart,
            @AuthenticationPrincipal SecurityUserDetails principal) {
        Cart newCart = mapper.toDomain(cart);
        return ResponseEntity.ok(mapper.toResponse(
                cartService.createCart(newCart, principal.getUser())));
    }

    @Operation(summary = "Obtener carrito por ID", description = "Busca un carrito específico por su identificador")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Carrito encontrado",
                    content = @Content(schema = @Schema(implementation = CartResponse.class))),
            @ApiResponse(responseCode = "404", description = "Carrito no encontrado"),
            @ApiResponse(responseCode = "401", description = "No autenticado")
    })
    @GetMapping("/{id}")
    public ResponseEntity<CartResponse> getCartById(
            @Parameter(description = "ID del carrito", example = "1", required = true)
            @PathVariable Long id,
            @AuthenticationPrincipal SecurityUserDetails principal) {
        return cartService.getCartById(id, principal.getUser()).map(mapper::toResponse).map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Actualizar carrito", description = "Actualiza un carrito existente")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Carrito actualizado",
                    content = @Content(schema = @Schema(implementation = CartResponse.class))),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "404", description = "Carrito no encontrado"),
            @ApiResponse(responseCode = "400", description = "Datos de carrito inválidos")
    })
    @PutMapping("/{id}")
    public ResponseEntity<CartResponse> updateCart(
            @Parameter(description = "ID del carrito", example = "1", required = true)
            @PathVariable Long id,
            @RequestBody @jakarta.validation.Valid CartRequest cartRequest,
            @AuthenticationPrincipal SecurityUserDetails principal) {
        Cart cart = mapper.toDomain(cartRequest);
        return ResponseEntity.ok(mapper.toResponse(
                cartService.updateCart(id, cart, principal.getUser())));
    }

    @Operation(summary = "Eliminar carrito", description = "Elimina un carrito del sistema")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Carrito eliminado"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "404", description = "Carrito no encontrado")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCart(
            @Parameter(description = "ID del carrito", example = "1", required = true)
            @PathVariable Long id,
            @AuthenticationPrincipal SecurityUserDetails principal) {
        cartService.deleteCart(id, principal.getUser());
        return ResponseEntity.noContent().build();
    }
}
