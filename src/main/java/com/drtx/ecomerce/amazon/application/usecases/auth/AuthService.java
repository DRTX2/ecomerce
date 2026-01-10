package com.drtx.ecomerce.amazon.application.usecases.auth;

import com.drtx.ecomerce.amazon.core.model.exceptions.DomainException;
import com.drtx.ecomerce.amazon.core.model.security.AuthResult;
import com.drtx.ecomerce.amazon.core.model.security.LoginCommand;
import com.drtx.ecomerce.amazon.core.model.security.RefreshToken;
import com.drtx.ecomerce.amazon.core.model.user.User;
import com.drtx.ecomerce.amazon.core.ports.in.rest.security.AuthUseCasePort;
import com.drtx.ecomerce.amazon.core.ports.out.persistence.UserRepositoryPort;
import com.drtx.ecomerce.amazon.core.ports.out.security.AuthenticationFacade;
import com.drtx.ecomerce.amazon.core.ports.out.security.PasswordService;
import com.drtx.ecomerce.amazon.core.ports.out.security.TokenProvider;
import com.drtx.ecomerce.amazon.core.ports.out.security.TokenRevocationPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService implements AuthUseCasePort {
    private final UserRepositoryPort repository;
    private final PasswordService passwordService;
    private final TokenProvider tokenProvider;
    private final AuthenticationFacade authenticationFacade;
    private final TokenRevocationPort tokenRevocationPort;
    private final RefreshTokenService refreshTokenService;

    @Override
    public AuthResult register(User user) {
        log.info("Registering new user: {}", user.getEmail());

        if (user.getPassword().toLowerCase().contains(user.getEmail().toLowerCase())) {
            log.warn("Registration failed - password contains email: {}", user.getEmail());
            throw new DomainException("Password cannot contain the email address.");
        }
        String encodedPassword = passwordService.encode(user.getPassword());
        user.setPassword(encodedPassword);
        User savedUser = repository.save(user);

        String accessToken = tokenProvider.generateAccessToken(savedUser);
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(savedUser);

        log.info("User registered successfully: {}", savedUser.getEmail());
        return new AuthResult(savedUser, accessToken, refreshToken.getToken(),
            tokenProvider.getAccessTokenExpirationMs());
    }

    @Override
    public AuthResult login(LoginCommand command) {
        log.info("Login attempt for user: {}", command.email());

        authenticationFacade.authenticate(command.email(), command.password());

        var user = repository.findByEmail(command.email())
                .orElseThrow(() -> {
                    log.error("User not found after successful authentication: {}", command.email());
                    return new RuntimeException("User not found with email: " + command.email());
                });

        String accessToken = tokenProvider.generateAccessToken(user);
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(user);

        log.info("Login successful for user: {}", user.getEmail());
        return new AuthResult(user, accessToken, refreshToken.getToken(),
            tokenProvider.getAccessTokenExpirationMs());
    }

    @Override
    public void logout(String token) {
        String userEmail = tokenProvider.extractUsername(token);
        log.info("Logout request for user: {}", userEmail);

        // Invalidar el access token
        tokenRevocationPort.invalidate(token);

        // Revocar refresh token del usuario
        refreshTokenService.revokeRefreshToken(userEmail);

        log.info("Logout successful for user: {}", userEmail);
    }

    @Override
    public AuthResult refreshToken(String refreshTokenString) {
        RefreshToken refreshToken = refreshTokenService.verifyAndGetRefreshToken(refreshTokenString);

        log.info("Refreshing access token for user: {}", refreshToken.getUserEmail());

        User user = repository.findByEmail(refreshToken.getUserEmail())
                .orElseThrow(() -> {
                    log.error("User not found for refresh token: {}", refreshToken.getUserEmail());
                    return new DomainException("User not found");
                });

        String newAccessToken = tokenProvider.generateAccessToken(user);

        log.info("Access token refreshed for user: {}", user.getEmail());
        return new AuthResult(user, newAccessToken, refreshTokenString,
            tokenProvider.getAccessTokenExpirationMs());
    }
}