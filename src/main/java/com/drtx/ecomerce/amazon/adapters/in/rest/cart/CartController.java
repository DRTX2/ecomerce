package com.drtx.ecomerce.amazon.adapters.in.rest.cart;

import com.drtx.ecomerce.amazon.adapters.in.rest.cart.dtos.CartRequest;
import com.drtx.ecomerce.amazon.adapters.in.rest.cart.dtos.CartResponse;
import com.drtx.ecomerce.amazon.adapters.in.rest.cart.mappers.CartRestMapper;

import com.drtx.ecomerce.amazon.core.model.Cart;
import com.drtx.ecomerce.amazon.core.ports.in.rest.CartUseCasePort;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("cart")
@AllArgsConstructor
public class CartController {
    private final CartUseCasePort cartService;
    private final CartRestMapper mapper;

    @GetMapping
    public ResponseEntity<List<CartResponse>> getAllCategories() {
        List<Cart> carts = cartService.getAllCarts(1111L);
        return ResponseEntity.ok(
                carts.stream().map(mapper::toResponse).toList());
    }

    @PostMapping
    public ResponseEntity<CartResponse> createcart(@RequestBody @jakarta.validation.Valid CartRequest cart) {
        Cart newcart = mapper.toDomain(cart);
        return ResponseEntity.ok(mapper.toResponse(
                cartService.createCart(newcart)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CartResponse> getcartById(@PathVariable Long id) {
        return cartService.getCartById(id).map(mapper::toResponse).map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<CartResponse> updatecart(@PathVariable Long id,
            @RequestBody @jakarta.validation.Valid CartRequest CartRequest) {
        Cart cart = mapper.toDomain(CartRequest);
        return ResponseEntity.ok(mapper.toResponse(
                cartService.updateCart(id, cart)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletecart(@PathVariable Long id) {
        cartService.deleteCart(id);
        return ResponseEntity.noContent().build();
    }
}
