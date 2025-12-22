package com.drtx.ecomerce.amazon.adapters.out.persistence.order;

import com.drtx.ecomerce.amazon.adapters.out.persistence.product.ProductEntity;
import com.drtx.ecomerce.amazon.adapters.out.persistence.product.ProductPersistenceMapper;
import com.drtx.ecomerce.amazon.core.model.Order;
import com.drtx.ecomerce.amazon.core.ports.out.persistence.OrderRepositoryPort;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@AllArgsConstructor
public class OrderRepositoryAdapter implements OrderRepositoryPort {
    private final OrderPersistenceRepository repository;
    private final OrderPersistenceMapper orderMapper;
    private final ProductPersistenceMapper productMapper;

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
        return repository.findAll().stream().map(orderMapper::toDomain).collect(Collectors.toList());
    }

    @Override
    public Order updateById(Order order) {
        OrderEntity orderToUpdate = repository.findById(order.getId()).orElseThrow(
                () -> new EntityNotFoundException("Order not found with id: " + order.getId()));
        List<ProductEntity> products = order.getProducts()
                .stream()
                .map(productMapper::toEntity)
                .collect(Collectors.toList()); // Fixed: ensure mutable list

        orderToUpdate.setProducts(products);
        orderToUpdate.setOrderState(order.getState());
        orderToUpdate.setDeliveredAt(order.getDeliveredAt());
        orderToUpdate.setTotal(order.getTotal());
        orderToUpdate.setPaymentType(order.getPaymentType());

        orderToUpdate = repository.save(orderToUpdate);
        return orderMapper.toDomain(orderToUpdate);
    }

    @Override
    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new EntityNotFoundException("Order not found with id: " + id);
        }
        repository.deleteById(id);
    }
}
