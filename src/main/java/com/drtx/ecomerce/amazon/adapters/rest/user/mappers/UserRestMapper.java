package com.drtx.ecomerce.amazon.adapters.rest.user.mappers;

import com.drtx.ecomerce.amazon.adapters.rest.user.dto.UserRequest;
import com.drtx.ecomerce.amazon.adapters.rest.user.dto.UserResponse;
import com.drtx.ecomerce.amazon.core.models.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserRestMapper {
    User toDomain(UserRequest request);
    UserResponse toResponse(User user);
}
