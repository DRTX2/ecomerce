package com.drtx.ecomerce.amazon.adapters.out.persistence.security;

import com.drtx.ecomerce.amazon.core.model.security.RefreshToken;
import org.springframework.stereotype.Component;

@Component
public class RefreshTokenMapper {

    public RefreshTokenEntity toEntity(RefreshToken domain) {
        if (domain == null) {
            return null;
        }

        return RefreshTokenEntity.builder()
                .id(domain.getId())
                .token(domain.getToken())
                .userEmail(domain.getUserEmail())
                .expiryDate(domain.getExpiryDate())
                .revoked(domain.isRevoked())
                .build();
    }

    public RefreshToken toDomain(RefreshTokenEntity entity) {
        if (entity == null) {
            return null;
        }

        return new RefreshToken(
                entity.getId(),
                entity.getToken(),
                entity.getUserEmail(),
                entity.getExpiryDate(),
                entity.isRevoked());
    }
}
