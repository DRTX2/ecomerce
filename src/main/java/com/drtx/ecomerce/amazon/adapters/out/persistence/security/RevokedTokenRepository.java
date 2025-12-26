package com.drtx.ecomerce.amazon.adapters.out.persistence.security;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

public interface RevokedTokenRepository extends JpaRepository<RevokedToken, String> {
    boolean existsByToken(String token);

    @Modifying
    @Transactional
    /*
    @Query("DELETE FROM RevokedToken r where r.expiresAt <= :now")
    void deleteByExpiresAtBefore(@Param("now") Instant now);
    */
    void deleteByExpiresAtBefore(Instant now);
}