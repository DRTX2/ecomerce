package com.drtx.ecomerce.amazon.adapters.in.rest.cart;

import com.drtx.ecomerce.amazon.adapters.in.rest.cart.dtos.CartRequest;
import com.drtx.ecomerce.amazon.adapters.in.rest.cart.dtos.CartResponse;
import com.drtx.ecomerce.amazon.adapters.in.rest.cart.mappers.CartRestMapper;
import com.drtx.ecomerce.amazon.core.model.order.Cart;
import com.drtx.ecomerce.amazon.adapters.in.rest.cart.dtos.CartItemDto;
import com.drtx.ecomerce.amazon.core.model.order.CartItem;
import com.drtx.ecomerce.amazon.core.model.product.Product;
import com.drtx.ecomerce.amazon.core.ports.in.rest.CartUseCasePort;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Cart Controller Tests (Standalone)")
class CartControllerTest {

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @Mock
    private CartUseCasePort cartUseCasePort;

    @Mock
    private CartRestMapper cartMapper;

    private Cart testCart;
    private CartRequest testCartRequest;
    private CartResponse testCartResponse;
    private Product testProduct;

    @BeforeEach
    void setUp() {
        CartController cartController = new CartController(cartUseCasePort, cartMapper);
        mockMvc = MockMvcBuilders.standaloneSetup(cartController).build();
        objectMapper = new ObjectMapper();

        testProduct = new Product();
        testProduct.setId(1L);
        testProduct.setUuid(UUID.randomUUID());
        testProduct.setName("Test Product");
        testProduct.setPrice(new BigDecimal("99.99"));

        UUID cartUuid = UUID.randomUUID();
        testCart = new Cart();
        testCart.setId(1L);
        testCart.setUuid(cartUuid);
        CartItem cartItem = new CartItem(1L, testCart, testProduct, 1);
        testCart.setItems(List.of(cartItem));

        CartItemDto cartItemDto = new CartItemDto(1L, 1);
        testCartRequest = new CartRequest(List.of(cartItemDto));

        testCartResponse = new CartResponse(cartUuid, List.of(cartItemDto));
    }

    @Test
    @DisplayName("GET /carts - Should return all carts")
    void testGetAllCarts() throws Exception {
        // Given
        List<Cart> carts = Arrays.asList(testCart);
        // Updated to use the userId parameter from search criteria or simple param
        when(cartUseCasePort.getAllCarts(1111L)).thenReturn(carts);
        when(cartMapper.toResponse(any(Cart.class))).thenReturn(testCartResponse);

        // When & Then
        mockMvc.perform(get("/carts")
                        .param("userId", "1111")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].uuid", is(testCart.getUuid().toString())));

        verify(cartUseCasePort, times(1)).getAllCarts(1111L);
    }

    @Test
    @DisplayName("POST /carts - Should create new cart")
    void testCreateCart() throws Exception {
        // Given
        when(cartMapper.toDomain(any(CartRequest.class))).thenReturn(testCart);
        when(cartUseCasePort.createCart(any(Cart.class))).thenReturn(testCart);
        when(cartMapper.toResponse(testCart)).thenReturn(testCartResponse);

        // When & Then
        mockMvc.perform(post("/carts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testCartRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.uuid", is(testCart.getUuid().toString())));

        verify(cartUseCasePort, times(1)).createCart(any(Cart.class));
    }

    @Test
    @DisplayName("POST /carts - Should return 400 when list of products is empty")
    void testCreateCart_ValidationFail() throws Exception {
        // Given
        CartRequest invalidRequest = new CartRequest(new ArrayList<>()); // Lista vacía

        // When & Then
        mockMvc.perform(post("/carts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());

        verify(cartUseCasePort, never()).createCart(any(Cart.class));
    }

    @Test
    @DisplayName("GET /carts/{uuid} - Should return cart when found")
    void testGetCartByUuid_Found() throws Exception {
        // Given
        UUID uuid = testCart.getUuid();
        when(cartUseCasePort.getCartByUuid(uuid)).thenReturn(Optional.of(testCart));
        when(cartMapper.toResponse(testCart)).thenReturn(testCartResponse);

        // When & Then
        mockMvc.perform(get("/carts/{uuid}", uuid)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.uuid", is(uuid.toString())));

        verify(cartUseCasePort, times(1)).getCartByUuid(uuid);
    }

    @Test
    @DisplayName("GET /carts/{uuid} - Should return 404 when cart not found")
    void testGetCartById_NotFound() throws Exception {
        // Given
        when(cartUseCasePort.getCartById(999L)).thenReturn(Optional.empty());

        // When & Then
        mockMvc.perform(get("/carts/{id}", 999L)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(cartUseCasePort, times(1)).getCartById(999L);
    }

    @Test
    @DisplayName("PUT /carts/{uuid} - Should update cart")
    void testUpdateCartByUuid() throws Exception {
        // Given
        UUID uuid = testCart.getUuid();
        when(cartMapper.toDomain(any(CartRequest.class))).thenReturn(testCart);
        when(cartUseCasePort.updateCartByUuid(eq(uuid), any(Cart.class))).thenReturn(testCart);
        when(cartMapper.toResponse(testCart)).thenReturn(testCartResponse);

        // When & Then
        mockMvc.perform(put("/carts/{uuid}", uuid)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testCartRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.uuid", is(uuid.toString())));

        verify(cartUseCasePort, times(1)).updateCartByUuid(eq(uuid), any(Cart.class));
    }

    @Test
    @DisplayName("DELETE /carts/{uuid} - Should delete cart")
    void testDeleteCartByUuid() throws Exception {
        // Given
        UUID uuid = testCart.getUuid();
        doNothing().when(cartUseCasePort).deleteCartByUuid(uuid);

        // When & Then
        mockMvc.perform(delete("/carts/{uuid}", uuid)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verify(cartUseCasePort, times(1)).deleteCartByUuid(uuid);
    }
}
