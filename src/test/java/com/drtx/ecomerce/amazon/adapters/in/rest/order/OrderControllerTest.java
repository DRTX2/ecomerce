package com.drtx.ecomerce.amazon.adapters.in.rest.order;

import com.drtx.ecomerce.amazon.adapters.in.rest.order.dto.OrderResponse;
import com.drtx.ecomerce.amazon.adapters.in.rest.order.mappers.OrderRestMapper;
import com.drtx.ecomerce.amazon.adapters.in.security.SecurityUserDetails;
import com.drtx.ecomerce.amazon.core.model.order.Order;
import com.drtx.ecomerce.amazon.core.model.order.OrderState;
import com.drtx.ecomerce.amazon.core.model.user.User;
import com.drtx.ecomerce.amazon.core.model.user.UserRole;
import com.drtx.ecomerce.amazon.core.ports.in.rest.OrderUseCasePort;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class OrderControllerTest {
    @Test
    void confirmCartUsesTheAuthenticatedUser() {
        OrderUseCasePort orderUseCase = mock(OrderUseCasePort.class);
        OrderRestMapper mapper = mock(OrderRestMapper.class);
        OrderController controller = new OrderController(orderUseCase, mapper);
        User user = new User(1L, "User", "user@example.com", "password", null, null, UserRole.USER);
        Order order = new Order(4L, user, List.of(), BigDecimal.TEN, OrderState.PENDING, null, null, List.of());
        OrderResponse response = new OrderResponse(4L, List.of(), BigDecimal.TEN, OrderState.PENDING, null, null);

        when(orderUseCase.confirmCart(3L, user)).thenReturn(order);
        when(mapper.toResponse(order)).thenReturn(response);

        var result = controller.confirmCart(3L, new SecurityUserDetails(user));

        assertThat(result.getBody()).isSameAs(response);
        verify(orderUseCase).confirmCart(3L, user);
    }
}
