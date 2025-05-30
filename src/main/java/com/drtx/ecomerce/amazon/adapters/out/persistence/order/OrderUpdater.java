package com.drtx.ecomerce.amazon.adapters.out.persistence.order;

import com.drtx.ecomerce.amazon.adapters.out.persistence.payment.PaymentEntity;
import com.drtx.ecomerce.amazon.adapters.out.persistence.payment.PaymentMapper;
import com.drtx.ecomerce.amazon.adapters.out.persistence.product.ProductEntity;
import com.drtx.ecomerce.amazon.adapters.out.persistence.product.ProductPersistenceMapper;
import com.drtx.ecomerce.amazon.core.model.Order;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@AllArgsConstructor
public class OrderUpdater {
    private final ProductPersistenceMapper productMapper;
    private final PaymentMapper paymentMapper;

    public void applyChanges(Order domainOrder, OrderEntity entity){
        List<ProductEntity> products=domainOrder.getProducts()
                .stream()
                .map(productMapper::toEntity)
                .toList();

        entity.setProducts(products);
        entity.setOrderState(domainOrder.getOrderState());
        entity.setDeliveredAt(domainOrder.getDeliveredAt());
        entity.setTotal(domainOrder.getTotal());

        PaymentEntity payment= paymentMapper.toEntity(domainOrder.getPayment());
        payment.setOrder(entity);
        entity.setPayment(payment);
    }
}
