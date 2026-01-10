package com.drtx.ecomerce.amazon.adapters.out.persistence.security;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.Optional;

public interface RefreshTokenJpaRepository extends JpaRepository<RefreshTokenEntity, Long> {
    Optional<RefreshTokenEntity> findByToken(String token);

    Optional<RefreshTokenEntity> findByUserEmail(String userEmail);

    void deleteByUserEmail(String userEmail);

    void deleteByToken(String token);

    @Modifying
    @Query("UPDATE RefreshTokenEntity rt SET rt.revoked = true WHERE rt.userEmail = :userEmail")
    void revokeByUserEmail(@Param("userEmail") String userEmail);

    /**
     * Elimina tokens expirados (expiry_date < now).
     */
    @Modifying
    @Query("DELETE FROM RefreshTokenEntity rt WHERE rt.expiryDate < :now")
    int deleteByExpiryDateBefore(@Param("now") Instant now);

    /**
     * Elimina todos los tokens revocados.
     */
    @Modifying
    @Query("DELETE FROM RefreshTokenEntity rt WHERE rt.revoked = true")
    int deleteByRevokedTrue();
}

