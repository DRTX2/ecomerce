package com.drtx.ecomerce.amazon.adapters.out.persistence.payment;

import com.drtx.ecomerce.amazon.adapters.out.persistence.order.OrderPersistenceMapper;
import com.drtx.ecomerce.amazon.core.model.Payment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = { OrderPersistenceMapper.class })
public interface PaymentPersistenceMapper {

    Payment toDomain(PaymentEntity entity);

    PaymentEntity toEntity(Payment domain);
}
