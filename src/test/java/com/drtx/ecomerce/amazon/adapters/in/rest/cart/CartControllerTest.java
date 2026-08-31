package com.drtx.ecomerce.amazon.adapters.in.rest.cart;

import com.drtx.ecomerce.amazon.adapters.in.rest.cart.dtos.CartResponse;
import com.drtx.ecomerce.amazon.adapters.in.rest.cart.mappers.CartRestMapper;
import com.drtx.ecomerce.amazon.adapters.in.security.SecurityUserDetails;
import com.drtx.ecomerce.amazon.core.model.order.Cart;
import com.drtx.ecomerce.amazon.core.model.user.User;
import com.drtx.ecomerce.amazon.core.model.user.UserRole;
import com.drtx.ecomerce.amazon.core.ports.in.rest.CartUseCasePort;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class CartControllerTest {
    @Test
    void getAllCartsUsesTheAuthenticatedUserInsteadOfARequestParameter() {
        CartUseCasePort cartUseCase = mock(CartUseCasePort.class);
        CartRestMapper mapper = mock(CartRestMapper.class);
        CartController controller = new CartController(cartUseCase, mapper);
        User user = new User(1L, "User", "user@example.com", "password", null, null, UserRole.USER);
        Cart cart = new Cart(2L, user, List.of());

        when(cartUseCase.getAllCarts(user)).thenReturn(List.of(cart));
        when(mapper.toResponse(cart)).thenReturn(new CartResponse(2L, List.of()));

        var response = controller.getAllCarts(new SecurityUserDetails(user));

        assertThat(response.getBody()).hasSize(1);
        verify(cartUseCase).getAllCarts(user);
    }
}
