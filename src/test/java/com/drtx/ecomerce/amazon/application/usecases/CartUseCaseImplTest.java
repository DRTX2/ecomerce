package com.drtx.ecomerce.amazon.application.usecases;

import com.drtx.ecomerce.amazon.application.usecases.cart.CartUseCaseImpl;
import com.drtx.ecomerce.amazon.core.model.order.Cart;
import com.drtx.ecomerce.amazon.core.model.user.User;
import com.drtx.ecomerce.amazon.core.model.user.UserRole;
import com.drtx.ecomerce.amazon.core.ports.out.persistence.CartRepositoryPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("CartUseCaseImpl Unit Tests")
class CartUseCaseImplTest {

    @Mock
    private CartRepositoryPort cartRepositoryPort;

    @InjectMocks
    private CartUseCaseImpl cartUseCase;

    private Cart testCart;
    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User(
                1L,
                UUID.randomUUID(),
                "John Doe",
                "john@example.com",
                "password123",
                "123 Main St",
                "555-0100",
                UserRole.USER,
                true,
                false);

        testCart = new Cart(1L, UUID.randomUUID(), testUser, List.of());
    }

    @Test
    @DisplayName("Should create cart successfully")
    void shouldCreateCartSuccessfully() {
        // Given
        Cart newCart = new Cart(testUser, List.of());
        Cart savedCart = new Cart(1L, UUID.randomUUID(), testUser, List.of());

        when(cartRepositoryPort.save(any(Cart.class))).thenReturn(savedCart);

        // When
        Cart result = cartUseCase.createCart(newCart);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getUser()).isEqualTo(testUser);
        verify(cartRepositoryPort, times(1)).save(newCart);
    }

    @Test
    @DisplayName("Should get cart by ID successfully")
    void shouldGetCartByIdSuccessfully() {
        // Given
        Long cartId = 1L;
        when(cartRepositoryPort.findById(cartId)).thenReturn(Optional.of(testCart));

        // When
        Optional<Cart> result = cartUseCase.getCartById(cartId);

        // Then
        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(cartId);
        assertThat(result.get().getUser()).isEqualTo(testUser);
        verify(cartRepositoryPort, times(1)).findById(cartId);
    }

    @Test
    @DisplayName("Should return empty when cart not found by ID")
    void shouldReturnEmptyWhenCartNotFoundById() {
        // Given
        Long cartId = 999L;
        when(cartRepositoryPort.findById(cartId)).thenReturn(Optional.empty());

        // When
        Optional<Cart> result = cartUseCase.getCartById(cartId);

        // Then
        assertThat(result).isEmpty();
        verify(cartRepositoryPort, times(1)).findById(cartId);
    }

    @Test
    @DisplayName("Should get all carts for user successfully")
    void shouldGetAllCartsSuccessfully() {
        // Given
        Long userId = 1L;
        Cart cart2 = new Cart(2L, UUID.randomUUID(), testUser, List.of());
        List<Cart> carts = Arrays.asList(testCart, cart2);

        when(cartRepositoryPort.findAll(userId)).thenReturn(carts);

        // When
        List<Cart> result = cartUseCase.getAllCarts(userId);

        // Then
        assertThat(result).hasSize(2);
        assertThat(result).containsExactlyInAnyOrder(testCart, cart2);
        verify(cartRepositoryPort, times(1)).findAll(userId);
    }

    @Test
    @DisplayName("Should return empty list when no carts exist for user")
    void shouldReturnEmptyListWhenNoCartsExistForUser() {
        // Given
        Long userId = 999L;
        when(cartRepositoryPort.findAll(userId)).thenReturn(List.of());

        // When
        List<Cart> result = cartUseCase.getAllCarts(userId);

        // Then
        assertThat(result).isEmpty();
        verify(cartRepositoryPort, times(1)).findAll(userId);
    }

    @Test
    @DisplayName("Should update cart successfully")
    void shouldUpdateCartSuccessfully() {
        // Given
        Long cartId = 1L;
        Cart updatedCart = new Cart(cartId, testCart.getUuid(), testUser, List.of());

        when(cartRepositoryPort.updateByUuid(eq(testCart.getUuid()), any(Cart.class)))
                .thenReturn(updatedCart);

        when(cartRepositoryPort.findByUuid(testCart.getUuid())).thenReturn(Optional.of(testCart));

        // When
        Cart result = cartUseCase.updateCartByUuid(testCart.getUuid(), updatedCart);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(cartId);
        verify(cartRepositoryPort, times(1)).updateByUuid(eq(testCart.getUuid()), any(Cart.class));
    }

    @Test
    @DisplayName("Should delete cart successfully")
    void shouldDeleteCartSuccessfully() {
        // Given
        Long cartId = 1L;
        doNothing().when(cartRepositoryPort).delete(cartId);

        // When
        cartUseCase.deleteCart(cartId);

        // Then
        verify(cartRepositoryPort, times(1)).delete(cartId);
    }

    @Test
    @DisplayName("Should handle delete for non-existent cart")
    void shouldHandleDeleteForNonExistentCart() {
        // Given
        Long cartId = 999L;
        doNothing().when(cartRepositoryPort).delete(cartId);

        // When
        cartUseCase.deleteCart(cartId);

        // Then
        verify(cartRepositoryPort, times(1)).delete(cartId);
    }
}
