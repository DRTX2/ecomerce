package com.drtx.ecomerce.amazon.adapters.out.persistence.security;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;

public interface RevokedTokenRepository extends JpaRepository<RevokedToken, String> {
    boolean existsByToken(String token);

    @Modifying
    @Query("DELETE FROM RevokedToken r WHERE r.revokedAt < :before")
    int deleteByRevokedAtBefore(@Param("before") Instant before);
}