package com.drtx.ecomerce.amazon.adapters.in.security.mappers;

import com.drtx.ecomerce.amazon.adapters.in.security.dto.AuthResponse;
import com.drtx.ecomerce.amazon.adapters.in.security.dto.AuthTokens;
import org.springframework.stereotype.Component;
//it works?
@Component
public class AuthResponseMapper {
    public AuthResponse fromToken(String token) {
        return new AuthResponse(null,
                new AuthTokens(token, "", 86400000L));
    }
}
