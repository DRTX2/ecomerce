package com.drtx.ecomerce.amazon.adapters.in.security.mappers;

import com.drtx.ecomerce.amazon.adapters.in.security.dto.AuthResponse;
import com.drtx.ecomerce.amazon.adapters.in.security.dto.RegisterRequest;
import com.drtx.ecomerce.amazon.core.model.user.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserSecurityMapper {
    User registerRequestToDomain(RegisterRequest request);
    AuthResponse entityToResponse(User entity);
}
