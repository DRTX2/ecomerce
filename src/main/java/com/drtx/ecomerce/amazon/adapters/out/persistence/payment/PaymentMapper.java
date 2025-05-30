package com.drtx.ecomerce.amazon.adapters.out.persistence.payment;

import com.drtx.ecomerce.amazon.adapters.out.persistence.product.ProductPersistenceMapper;
import com.drtx.ecomerce.amazon.core.model.Payment;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = ProductPersistenceMapper.class)
public interface PaymentMapper {
    Payment toDomain(PaymentEntity entity);
    PaymentEntity toEntity(Payment payment);
}
