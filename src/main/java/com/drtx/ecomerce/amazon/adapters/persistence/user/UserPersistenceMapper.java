package com.drtx.ecomerce.amazon.adapters.persistence.user;

import com.drtx.ecomerce.amazon.adapters.rest.user.dto.UserUpdateRequest;
import com.drtx.ecomerce.amazon.core.models.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface UserPersistenceMapper {
    UserPersistenceMapper INSTANCE = Mappers.getMapper(UserPersistenceMapper.class);

    User toDomain(UserEntity entity);

    UserEntity toEntity(User domain);

    @Mapping(target = "id", ignore = true)
    void updateEntityromRequest(@MappingTarget UserEntity entity, UserUpdateRequest request);
}
