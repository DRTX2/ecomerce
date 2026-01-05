package com.drtx.ecomerce.amazon.application.usecases.auth;

import com.drtx.ecomerce.amazon.core.model.security.AuthResult;
import com.drtx.ecomerce.amazon.core.model.security.LoginCommand;
import com.drtx.ecomerce.amazon.core.model.user.User;
import com.drtx.ecomerce.amazon.core.ports.in.rest.security.AuthUseCasePort;
import com.drtx.ecomerce.amazon.core.ports.out.persistence.UserRepositoryPort;
import com.drtx.ecomerce.amazon.core.ports.out.security.AuthenticationFacade;
import com.drtx.ecomerce.amazon.core.ports.out.security.PasswordService;
import com.drtx.ecomerce.amazon.core.ports.out.security.TokenProvider;
import com.drtx.ecomerce.amazon.core.ports.out.security.TokenRevocationPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService implements AuthUseCasePort {
    private final UserRepositoryPort repository;
    private final PasswordService passwordService;
    private final TokenProvider tokenProvider;
    private final AuthenticationFacade authenticationFacade;
    private final TokenRevocationPort tokenRevocationPort;

    @Override
    public AuthResult register(User user) {
        if (user.getPassword().toLowerCase().contains(user.getEmail().toLowerCase())) {
            throw new com.drtx.ecomerce.amazon.core.model.exceptions.DomainException(
                    "Password cannot contain the email address.");
        }
        String encodedPassword = passwordService.encode(user.getPassword());
        user.setPassword(encodedPassword);
        User savedUser = repository.save(user);

        var jwt = tokenProvider.generateToken(savedUser);

        // Assuming refresh tokens are implemented or placeholder as before
        return new AuthResult(savedUser, jwt, "", 86400000L);
    }

    @Override
    public AuthResult login(LoginCommand command) {
        authenticationFacade.authenticate(command.email(), command.password());

        var user = repository.findByEmail(command.email())
                .orElseThrow(
                        () -> new RuntimeException("User not found with email: " + command.email()));

        var jwt = tokenProvider.generateToken(user);

        return new AuthResult(user, jwt, "", 86400000L);
    }

    @Override
    public void logout(String token) {
        tokenRevocationPort.invalidate(token);
    }
}