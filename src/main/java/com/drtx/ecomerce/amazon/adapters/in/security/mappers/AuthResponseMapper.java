package com.drtx.ecomerce.amazon.adapters.in.security.mappers;

import com.drtx.ecomerce.amazon.adapters.in.security.dto.AuthResponse;
import org.springframework.stereotype.Component;

@Component
public class AuthResponseMapper {
    public AuthResponse fromToken(String token) {
        return new AuthResponse(null,
                new com.drtx.ecomerce.amazon.adapters.in.security.dto.AuthTokens(token, "", 86400000L));
    }
}
