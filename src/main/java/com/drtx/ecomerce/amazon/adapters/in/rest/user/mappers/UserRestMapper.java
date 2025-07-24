package com.drtx.ecomerce.amazon.adapters.in.rest.user.mappers;

import com.drtx.ecomerce.amazon.adapters.in.rest.user.dto.UserRequest;
import com.drtx.ecomerce.amazon.adapters.in.rest.user.dto.UserResponse;
import com.drtx.ecomerce.amazon.core.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserRestMapper {
    User toDomain(UserRequest request);

    @Mapping(target = "id", source = "id")// refers to properties
    UserResponse toResponse(User user);
}
