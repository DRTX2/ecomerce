package com.drtx.ecomerce.amazon.infrastructure.security;

import com.drtx.ecomerce.amazon.core.model.user.User;
import com.drtx.ecomerce.amazon.core.ports.out.security.TokenProvider;
import com.drtx.ecomerce.amazon.core.ports.out.security.TokenRevocationPort;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
public class JwtService implements TokenProvider {
    @Value("${security.jwt.secret-key}")
    private String secretKey;

    @Value("${security.jwt.expiration}")
    private long jwtExpirationInMs;

    private SecretKey signingKey;
    private final TokenRevocationPort tokenRevocationPort;

    @PostConstruct
    void init() {
        this.signingKey = Keys.hmacShaKeyFor(secretKey.getBytes());
    }

    @Override
    public String generateToken(User user) {
        return generateToken(new HashMap<>(), user);
    }

    public String generateToken(
            Map<String, Object> extraClaims,
            User user) {
        // Assuming user.getRole() returns UserRole enum, we can map it to authorities
        // string
        String role = user.getRole() != null ? "ROLE_" + user.getRole().name() : "ROLE_USER";
        // Or if you want a list of authorities like: [{authority=ROLE_SELLER}]
        // But simpler is often better. Let's send the role as a claim or just handle it
        // as we prefer.
        // Spring Security expects "Authorities" claim to populate authorities if we use
        // a specific filter.
        // But usually we extract claims and build authorities manually in filter.
        // Let's stick to putting authorities in the token if that's what was there.
        // Previous implementations put `userDetails.getAuthorities()`.

        // Let's emulate a list of simple authority objects
        extraClaims.put("Authorities", java.util.Collections.singletonList(Map.of("authority", role)));

        return Jwts.builder()
                .claims(extraClaims)
                .subject(user.getEmail()) // Assuming email is username
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + jwtExpirationInMs))
                .signWith(signingKey)
                .compact();
    }

    @Override
    public String extractUsername(String token) {
        return extractClaims(token, Claims::getSubject);
    }

    private Date extractExpiration(String token) {
        return extractClaims(token, Claims::getExpiration);
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    @Override
    public boolean isTokenValid(String token, User user) {
        final String userEmail = extractUsername(token);
        // Check blacklist first logic is commented out in original code or handled
        // inside this method?
        // Original code: if(tokenRevocationPort.isInvalidated(token))... wait, original
        // code commented it out?
        // No, original code: if(isTokenInvalidated(token)) return false; (commented
        // out)
        // But JwtAuthFilter checks it.
        // I'll keep the logic simple: verify email + expiration.
        return userEmail.equals(user.getEmail()) && !isTokenExpired(token);
    }

    public <T> T extractClaims(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    public Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(signingKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();

    }
}