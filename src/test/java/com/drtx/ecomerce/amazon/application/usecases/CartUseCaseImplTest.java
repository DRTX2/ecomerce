package com.drtx.ecomerce.amazon.application.usecases;

import com.drtx.ecomerce.amazon.application.usecases.cart.CartUseCaseImpl;
import com.drtx.ecomerce.amazon.core.model.exceptions.EntityNotFoundException;
import com.drtx.ecomerce.amazon.core.model.order.Cart;
import com.drtx.ecomerce.amazon.core.model.user.User;
import com.drtx.ecomerce.amazon.core.model.user.UserRole;
import com.drtx.ecomerce.amazon.core.ports.out.persistence.CartRepositoryPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CartUseCaseImplTest {
    @Mock
    private CartRepositoryPort cartRepository;

    @InjectMocks
    private CartUseCaseImpl cartUseCase;

    private User owner;
    private User otherUser;
    private Cart cart;

    @BeforeEach
    void setUp() {
        owner = new User(1L, "Owner", "owner@example.com", "password", null, null, UserRole.USER);
        otherUser = new User(2L, "Other", "other@example.com", "password", null, null, UserRole.USER);
        cart = new Cart(10L, owner, List.of());
    }

    @Test
    void createCartAssignsTheAuthenticatedOwner() {
        when(cartRepository.save(any(Cart.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Cart created = cartUseCase.createCart(new Cart(null, List.of()), owner);

        assertThat(created.getUser()).isSameAs(owner);
    }

    @Test
    void getCartRejectsAnotherUser() {
        when(cartRepository.findById(cart.getId())).thenReturn(Optional.of(cart));

        assertThatThrownBy(() -> cartUseCase.getCartById(cart.getId(), otherUser))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    void updateCartPreservesTheStoredOwner() {
        Cart update = new Cart(null, otherUser, List.of());
        when(cartRepository.findById(cart.getId())).thenReturn(Optional.of(cart));
        when(cartRepository.update(any(Cart.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Cart updated = cartUseCase.updateCart(cart.getId(), update, owner);

        assertThat(updated.getId()).isEqualTo(cart.getId());
        assertThat(updated.getUser()).isSameAs(owner);
    }

    @Test
    void deleteCartRejectsAnotherUser() {
        when(cartRepository.findById(cart.getId())).thenReturn(Optional.of(cart));

        assertThatThrownBy(() -> cartUseCase.deleteCart(cart.getId(), otherUser))
                .isInstanceOf(EntityNotFoundException.class);
        verify(cartRepository, never()).delete(anyLong());
    }
}
