package com.drtx.ecomerce.amazon.adapters.out.persistence.returns;

import com.drtx.ecomerce.amazon.adapters.out.persistence.order.OrderPersistenceMapper;
import com.drtx.ecomerce.amazon.adapters.out.persistence.user.UserPersistenceMapper;
import com.drtx.ecomerce.amazon.core.model.Return;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = { UserPersistenceMapper.class, OrderPersistenceMapper.class })
public interface ReturnPersistenceMapper {
    Return toDomain(ReturnEntity entity);

    ReturnEntity toEntity(Return domain);
}
