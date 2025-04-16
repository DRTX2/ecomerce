package com.drtx.ecomerce.amazon.adapters.outbound.persistence.user;

import com.drtx.ecomerce.amazon.adapters.inbound.rest.user.dto.UserUpdateRequest;
import com.drtx.ecomerce.amazon.core.models.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface UserPersistenceMapper {

    User toDomain(UserEntity entity);

    UserEntity toEntity(User domain);

    @Mapping(target = "id", ignore = true)
    void updateEntityFromRequest(@MappingTarget UserEntity entity, UserUpdateRequest request);
}
