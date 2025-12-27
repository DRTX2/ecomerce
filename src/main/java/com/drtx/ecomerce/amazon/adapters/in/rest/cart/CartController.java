package com.drtx.ecomerce.amazon.adapters.in.rest.cart;

import com.drtx.ecomerce.amazon.adapters.in.rest.cart.dtos.CartRequest;
import com.drtx.ecomerce.amazon.adapters.in.rest.cart.dtos.CartResponse;
import com.drtx.ecomerce.amazon.adapters.in.rest.cart.mappers.CartRestMapper;

import com.drtx.ecomerce.amazon.core.model.order.Cart;
import com.drtx.ecomerce.amazon.core.ports.in.rest.CartUseCasePort;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    public ResponseEntity<CartResponse> createCart(@RequestBody @jakarta.validation.Valid CartRequest cart) {
        Cart newCart = mapper.toDomain(cart);
        return ResponseEntity.ok(mapper.toResponse(
                cartService.createCart(newCart)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CartResponse> getCartById(@PathVariable Long id) {
        return cartService.getCartById(id).map(mapper::toResponse).map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<CartResponse> updateCart(@PathVariable Long id,
            @RequestBody @jakarta.validation.Valid CartRequest cartRequest) {
        Cart cart = mapper.toDomain(cartRequest);
        return ResponseEntity.ok(mapper.toResponse(
                cartService.updateCart(id, cart)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCart(@PathVariable Long id) {
        cartService.deleteCart(id);
        return ResponseEntity.noContent().build();
    }
}
