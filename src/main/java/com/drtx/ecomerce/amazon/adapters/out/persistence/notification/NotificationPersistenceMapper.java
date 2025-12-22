package com.drtx.ecomerce.amazon.adapters.out.persistence.notification;

import com.drtx.ecomerce.amazon.adapters.out.persistence.user.UserPersistenceMapper;
import com.drtx.ecomerce.amazon.core.model.Notification;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring", uses = { UserPersistenceMapper.class })
public interface NotificationPersistenceMapper {
    Notification toDomain(NotificationEntity entity);

    List<Notification> toDomainList(List<NotificationEntity> entities);

    NotificationEntity toEntity(Notification domain);
}
