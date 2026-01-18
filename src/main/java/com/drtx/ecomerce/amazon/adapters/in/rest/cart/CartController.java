package com.drtx.ecomerce.amazon.adapters.in.rest.cart;

import com.drtx.ecomerce.amazon.adapters.in.rest.cart.dtos.CartRequest;
import com.drtx.ecomerce.amazon.adapters.in.rest.cart.dtos.CartResponse;
import com.drtx.ecomerce.amazon.adapters.in.rest.cart.mappers.CartRestMapper;

import com.drtx.ecomerce.amazon.core.model.order.Cart;
import com.drtx.ecomerce.amazon.core.ports.in.rest.CartUseCasePort;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/carts")
@AllArgsConstructor
public class CartController {
    private final CartUseCasePort cartService;
    private final CartRestMapper mapper;

    @GetMapping
    public ResponseEntity<List<CartResponse>> getAllCarts(@RequestParam Long userId) {
        List<Cart> carts = cartService.getAllCarts(userId);
        return ResponseEntity.ok(
                carts.stream().map(mapper::toResponse).toList());
    }

    @PostMapping
    public ResponseEntity<CartResponse> createCart(@RequestBody @Valid CartRequest cart) {
        Cart newCart = mapper.toDomain(cart);
        return ResponseEntity.ok(mapper.toResponse(
                cartService.createCart(newCart)));
    }

    /**
     * Get cart by UUID (main endpoint)
     */
    @GetMapping("/{uuid}")
    public ResponseEntity<CartResponse> getCartByUuid(@PathVariable UUID uuid) {
        return cartService.getCartByUuid(uuid)
                .map(mapper::toResponse)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Update cart by UUID (main endpoint)
     */
    @PutMapping("/{uuid}")
    public ResponseEntity<CartResponse> updateCartByUuid(@PathVariable UUID uuid,
            @RequestBody @Valid CartRequest cartRequest) {
        Cart cart = mapper.toDomain(cartRequest);
        return ResponseEntity.ok(mapper.toResponse(
                cartService.updateCartByUuid(uuid, cart)));
    }

    /**
     * Delete cart by UUID (main endpoint)
     */
    @DeleteMapping("/{uuid}")
    public ResponseEntity<Void> deleteCartByUuid(@PathVariable UUID uuid) {
        cartService.deleteCartByUuid(uuid);
        return ResponseEntity.noContent().build();
    }

    // Legacy endpoints using Long ID (deprecated)

    /**
     * @deprecated Use {@link #getCartByUuid(UUID)} instead
     */
    @Deprecated
    @GetMapping("/by-id/{id}")
    public ResponseEntity<CartResponse> getCartById(@PathVariable Long id) {
        return cartService.getCartById(id)
                .map(mapper::toResponse)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * @deprecated Use {@link #updateCartByUuid(UUID, CartRequest)} instead
     */
    @Deprecated
    @PutMapping("/by-id/{id}")
    public ResponseEntity<CartResponse> updateCart(@PathVariable Long id,
            @RequestBody @Valid CartRequest cartRequest) {
        Cart cart = mapper.toDomain(cartRequest);
        return ResponseEntity.ok(mapper.toResponse(
                cartService.updateCart(id, cart)));
    }

    /**
     * @deprecated Use {@link #deleteCartByUuid(UUID)} instead
     */
    @Deprecated
    @DeleteMapping("/by-id/{id}")
    public ResponseEntity<Void> deleteCart(@PathVariable Long id) {
        cartService.deleteCart(id);
        return ResponseEntity.noContent().build();
    }
}
