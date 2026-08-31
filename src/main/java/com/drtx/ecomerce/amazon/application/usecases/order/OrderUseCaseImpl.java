package com.drtx.ecomerce.amazon.application.usecases.order;

import com.drtx.ecomerce.amazon.core.model.exceptions.DomainExceptionFactory;
import com.drtx.ecomerce.amazon.core.model.audit.AuditLog;
import com.drtx.ecomerce.amazon.core.model.events.OutboxEvent;
import com.drtx.ecomerce.amazon.core.model.order.Cart;
import com.drtx.ecomerce.amazon.core.model.order.CartItem;
import com.drtx.ecomerce.amazon.core.model.order.Order;
import com.drtx.ecomerce.amazon.core.model.order.OrderItem;
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
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.Instant;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@AllArgsConstructor
public class OrderUseCaseImpl implements com.drtx.ecomerce.amazon.core.ports.in.rest.OrderUseCasePort {
    private final OrderRepositoryPort repository;
    private final CartRepositoryPort cartRepository;
    private final ProductRepositoryPort productRepository;
    private final AuditLogPort auditLogPort;
    private final OutboxPort outboxPort;

    @Override
    public Order createOrder(Order order) {
        return repository.save(order);
    }

    @Override
    public Optional<Order> getOrderById(Long id, User actor) {
        return repository.findById(id).map(order -> requireOwnerOrAdmin(order, actor));
    }

    @Override
    public List<Order> getMyOrders(User actor) {
        return repository.findByUserId(actor.getId());
    }

    @Override
    @Transactional
    public Order confirmCart(Long cartId, User actor) {
        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> DomainExceptionFactory.cartNotFound(cartId));
        requireCartOwner(cart, actor);

        if (cart.getItems() == null || cart.getItems().isEmpty()) {
            throw DomainExceptionFactory.emptyCart(cartId);
        }

        var quantities = new LinkedHashMap<Long, Integer>();
        for (CartItem item : cart.getItems()) {
            if (item.getProduct() == null || item.getProduct().getId() == null || item.getQuantity() == null
                    || item.getQuantity() <= 0) {
                throw DomainExceptionFactory.invalidCartItem(cartId);
            }
            quantities.merge(item.getProduct().getId(), item.getQuantity(), Integer::sum);
        }

        List<OrderItem> items = new ArrayList<>();
        BigDecimal total = BigDecimal.ZERO;
        for (var entry : quantities.entrySet()) {
            Product product = productRepository.findById(entry.getKey())
                    .orElseThrow(() -> DomainExceptionFactory.productNotFound(entry.getKey()));
            if (product.getStatus() != ProductStatus.ACTIVE) {
                throw DomainExceptionFactory.productUnavailable(product.getId());
            }
            if (!productRepository.reserveStock(product.getId(), entry.getValue())) {
                throw DomainExceptionFactory.insufficientStock(product.getName(), product.getStockQuantity());
            }

            BigDecimal lineTotal = product.getPrice().multiply(BigDecimal.valueOf(entry.getValue()));
            items.add(new OrderItem(null, null, product, entry.getValue(), product.getPrice()));
            total = total.add(lineTotal);
        }

        Order order = new Order(null, cart.getUser(), items, total, OrderState.PENDING_CONFIRMATION,
                LocalDateTime.now(), null, List.of());
        Order savedOrder = repository.save(order);
        cartRepository.delete(cartId);
        Instant now = Instant.now();
        auditLogPort.append(new AuditLog(UUID.randomUUID(), actor.getId(), "ORDER_CONFIRMED", "ORDER",
                savedOrder.getId().toString(), "{\"cartId\":" + cartId + ",\"total\":\""
                        + savedOrder.getTotal() + "\"}", now));
        outboxPort.append(new OutboxEvent(UUID.randomUUID(), "ORDER", savedOrder.getId().toString(),
                "OrderConfirmed", "{\"orderId\":" + savedOrder.getId() + ",\"userId\":"
                        + actor.getId() + ",\"total\":\"" + savedOrder.getTotal() + "\"}", now, now));
        return savedOrder;
    }

    @Override
    public List<Order> getAllOrders() {
        return repository.findAll();
    }

    @Override
    public Order updateOrder(Order order) {
        // Verify order exists
        repository.findById(order.getId())
                .orElseThrow(() -> DomainExceptionFactory.orderNotFound(order.getId()));

        return repository.updateById(order);
    }

    @Override
    public void deleteOrder(Long id) {
        // Verify order exists before deleting
        repository.findById(id)
                .orElseThrow(() -> DomainExceptionFactory.orderNotFound(id));

        repository.delete(id);
    }

    private Order requireOwnerOrAdmin(Order order, User actor) {
        if (actor == null || order.getUser() == null
                || (!actor.getId().equals(order.getUser().getId()) && actor.getRole() != UserRole.ADMIN)) {
            throw DomainExceptionFactory.orderNotFound(order.getId());
        }
        return order;
    }

    private void requireCartOwner(Cart cart, User actor) {
        if (actor == null || cart.getUser() == null || !actor.getId().equals(cart.getUser().getId())) {
            throw DomainExceptionFactory.cartNotFound(cart.getId());
        }
    }
}
