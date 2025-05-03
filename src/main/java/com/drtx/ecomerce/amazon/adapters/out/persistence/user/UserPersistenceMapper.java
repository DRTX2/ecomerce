package com.drtx.ecomerce.amazon.adapters.out.persistence.user;

import com.drtx.ecomerce.amazon.adapters.in.rest.user.dto.UserUpdateRequest;
import com.drtx.ecomerce.amazon.core.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface UserPersistenceMapper {
    User toDomain(UserEntity entity);
    UserEntity toEntity(User domain);
    // When you want to do a partial or controlled update, and you already have an entity loaded from the database.
    void updateEntityFromRequest(@MappingTarget UserEntity entity, UserUpdateRequest request);
}
