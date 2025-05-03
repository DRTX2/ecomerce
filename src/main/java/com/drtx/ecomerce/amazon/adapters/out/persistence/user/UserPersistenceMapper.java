package com.drtx.ecomerce.amazon.adapters.out.persistence.user;

import com.drtx.ecomerce.amazon.adapters.in.rest.user.dto.UserUpdateRequest;
import com.drtx.ecomerce.amazon.core.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface UserPersistenceMapper {

    User toDomain(UserEntity entity);

    UserEntity toEntity(User domain);

    void updateEntityFromRequest(@MappingTarget UserEntity entity, UserUpdateRequest request);
}
