package com.drtx.ecomerce.amazon.application.usecases.order;

import com.drtx.ecomerce.amazon.core.model.exceptions.DomainExceptionFactory;
import com.drtx.ecomerce.amazon.core.model.order.Order;
import com.drtx.ecomerce.amazon.core.ports.out.persistence.OrderRepositoryPort;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class OrderUseCaseImpl implements com.drtx.ecomerce.amazon.core.ports.in.rest.OrderUseCasePort {
    private final OrderRepositoryPort repository;

    @Override
    public Order createOrder(Order order) {
        return repository.save(order);
    }

    @Override
    public Optional<Order> getOrderById(Long id) {
        return repository.findById(id);
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
}
