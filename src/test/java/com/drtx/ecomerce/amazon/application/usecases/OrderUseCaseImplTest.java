package com.drtx.ecomerce.amazon.application.usecases;

import com.drtx.ecomerce.amazon.application.usecases.order.OrderUseCaseImpl;
import com.drtx.ecomerce.amazon.core.model.exceptions.DomainException;
import com.drtx.ecomerce.amazon.core.model.exceptions.EntityNotFoundException;
import com.drtx.ecomerce.amazon.core.model.order.Cart;
import com.drtx.ecomerce.amazon.core.model.order.CartItem;
import com.drtx.ecomerce.amazon.core.model.order.Order;
import com.drtx.ecomerce.amazon.core.model.order.OrderState;
import com.drtx.ecomerce.amazon.core.model.product.Product;
import com.drtx.ecomerce.amazon.core.model.product.ProductStatus;
import com.drtx.ecomerce.amazon.core.model.user.User;
import com.drtx.ecomerce.amazon.core.model.user.UserRole;
import com.drtx.ecomerce.amazon.core.ports.out.persistence.CartRepositoryPort;
import com.drtx.ecomerce.amazon.core.ports.out.persistence.AuditLogPort;
import com.drtx.ecomerce.amazon.core.ports.out.persistence.OrderRepositoryPort;
import com.drtx.ecomerce.amazon.core.ports.out.persistence.OutboxPort;
import com.drtx.ecomerce.amazon.core.ports.out.persistence.ProductRepositoryPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderUseCaseImplTest {
    @Mock
    private OrderRepositoryPort orderRepository;
    @Mock
    private CartRepositoryPort cartRepository;
    @Mock
    private ProductRepositoryPort productRepository;
    @Mock
    private AuditLogPort auditLogPort;
    @Mock
    private OutboxPort outboxPort;

    @InjectMocks
    private OrderUseCaseImpl orderUseCase;

    private User owner;
    private User otherUser;
    private Product product;
    private Cart cart;

    @BeforeEach
    void setUp() {
        owner = new User(1L, "Owner", "owner@example.com", "password", null, null, UserRole.USER);
        otherUser = new User(2L, "Other", "other@example.com", "password", null, null, UserRole.USER);
        product = new Product();
        product.setId(20L);
        product.setName("Keyboard");
        product.setPrice(new BigDecimal("50.00"));
        product.setStockQuantity(3);
        product.setStatus(ProductStatus.ACTIVE);
        cart = new Cart(10L, owner, List.of(new CartItem(null, null, product, 2)));
    }

    @Test
    void confirmCartCreatesAnOrderFromServerSideProductData() {
        when(cartRepository.findById(cart.getId())).thenReturn(Optional.of(cart));
        when(productRepository.findById(product.getId())).thenReturn(Optional.of(product));
        when(productRepository.reserveStock(product.getId(), 2)).thenReturn(true);
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> {
            Order order = invocation.getArgument(0);
            order.setId(40L);
            return order;
        });

        Order created = orderUseCase.confirmCart(cart.getId(), owner);

        assertThat(created.getUser()).isSameAs(owner);
        assertThat(created.getOrderState()).isEqualTo(OrderState.PENDING_CONFIRMATION);
        assertThat(created.getTotal()).isEqualByComparingTo("100.00");
        assertThat(created.getItems()).singleElement().satisfies(item ->
                assertThat(item.getPriceAtPurchase()).isEqualByComparingTo("50.00"));
        verify(cartRepository).delete(cart.getId());
        verify(auditLogPort).append(any());
        verify(outboxPort).append(any());
    }

    @Test
    void confirmCartLeavesCartUntouchedWhenStockCannotBeReserved() {
        when(cartRepository.findById(cart.getId())).thenReturn(Optional.of(cart));
        when(productRepository.findById(product.getId())).thenReturn(Optional.of(product));
        when(productRepository.reserveStock(product.getId(), 2)).thenReturn(false);

        assertThatThrownBy(() -> orderUseCase.confirmCart(cart.getId(), owner))
                .isInstanceOf(DomainException.class);
        verify(orderRepository, never()).save(any());
        verify(cartRepository, never()).delete(anyLong());
    }

    @Test
    void getOrderRejectsAnotherUser() {
        Order order = new Order(30L, owner, List.of(), BigDecimal.ONE, OrderState.PENDING, null, null, List.of());
        when(orderRepository.findById(order.getId())).thenReturn(Optional.of(order));

        assertThatThrownBy(() -> orderUseCase.getOrderById(order.getId(), otherUser))
                .isInstanceOf(EntityNotFoundException.class);
    }
}
