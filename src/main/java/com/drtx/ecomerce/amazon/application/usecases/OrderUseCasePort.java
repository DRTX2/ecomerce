package com.drtx.ecomerce.amazon.application.usecases;

import com.drtx.ecomerce.amazon.core.model.Order;
import com.drtx.ecomerce.amazon.core.ports.in.OrderServicePort;
import com.drtx.ecomerce.amazon.core.ports.out.OrderRepositoryPort;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class OrderUseCasePort implements OrderServicePort {
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
        return repository.updateById(order);
    }

    @Override
    public void deleteOrder(Long id) {
        repository.delete(id);
    }
}
