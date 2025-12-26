package com.drtx.ecomerce.amazon.adapters.out.persistence.security;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Entity
@Table(name = "revoked_tokens")
@Getter
@NoArgsConstructor
// posteriormente para pruebas se puede añadir @EqualsAndHashCode / AllArgsConstructor
public class RevokedToken {

    @Id
    private String token;

    @Column(nullable = false)
    private Instant revokedAt;

    @Column(nullable = false)
    private Instant expiresAt;

    public RevokedToken(String token, Instant expiresAt) {
        this.token = token;
        this.revokedAt = Instant.now();
        this.expiresAt = expiresAt;
    }

}