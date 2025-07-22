package com.drtx.ecomerce.amazon.adapters.in.security.mappers;

import com.drtx.ecomerce.amazon.adapters.in.security.dto.RegisterRequest;
import com.drtx.ecomerce.amazon.core.model.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserSecurityMapper {
    User registerRequestToDomain(RegisterRequest request);

}
