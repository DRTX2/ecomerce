package com.drtx.ecomerce.amazon.adapters.out.persistence.order;

import com.drtx.ecomerce.amazon.adapters.out.persistence.payment.PaymentEntity;
import com.drtx.ecomerce.amazon.adapters.out.persistence.payment.PaymentMapper;
import com.drtx.ecomerce.amazon.adapters.out.persistence.product.ProductEntity;
import com.drtx.ecomerce.amazon.adapters.out.persistence.product.ProductPersistenceMapper;
import com.drtx.ecomerce.amazon.core.model.Order;
import com.drtx.ecomerce.amazon.core.model.Payment;
import com.drtx.ecomerce.amazon.core.ports.out.OrderRepositoryPort;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@AllArgsConstructor
public class OrderRepositoryAdapter implements OrderRepositoryPort {
    private final OrderPersistenceRepository repository;
    private final OrderPersistenceMapper orderMapper;
    private final OrderUpdater updater;

    @Override
    public Order save(Order order) {
        OrderEntity orderToSave = orderMapper.toEntity(order);
        orderToSave = repository.save(orderToSave);
        return orderMapper.toDomain(orderToSave);
    }

    @Override
    public Optional<Order> findById(Long id) {
        return repository.findById(id).map(orderMapper::toDomain);
    }

    @Override
    public List<Order> findAll() {
        return repository.findAll().stream().map(orderMapper::toDomain).toList();
    }

    @Override
    public Order updateById(Order order) {
        OrderEntity orderToUpdate = repository.findById(order.getId()).
                orElseThrow(
                        () -> new EntityNotFoundException("Order not found with id: " + order.getId())
                );
        updater.applyChanges(order,orderToUpdate);

        OrderEntity updated= repository.save(orderToUpdate);
        return orderMapper.toDomain(updated);
    }

    @Override
    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new EntityNotFoundException("Order not found with id: " + id);
        }
        repository.deleteById(id);
    }
}
