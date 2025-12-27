package com.drtx.ecomerce.amazon.application.usecases.auth;

import com.drtx.ecomerce.amazon.adapters.in.security.dto.AuthRequest;
import com.drtx.ecomerce.amazon.adapters.in.security.dto.AuthResponse;

import com.drtx.ecomerce.amazon.adapters.in.security.dto.AuthTokens;
import com.drtx.ecomerce.amazon.adapters.in.security.dto.UserResponse;
import com.drtx.ecomerce.amazon.adapters.in.security.mappers.SecurityUserMapper;
import com.drtx.ecomerce.amazon.core.model.user.User;
import com.drtx.ecomerce.amazon.core.ports.out.persistence.UserRepositoryPort;
import com.drtx.ecomerce.amazon.core.ports.out.security.AuthenticationFacade;
import com.drtx.ecomerce.amazon.core.ports.out.security.PasswordService;
import com.drtx.ecomerce.amazon.core.ports.out.security.TokenProvider;
import com.drtx.ecomerce.amazon.core.ports.out.security.TokenRevocationPort;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepositoryPort repository;
    private final PasswordService passwordService;
    private final TokenProvider tokenProvider;
    private final AuthenticationFacade authenticationFacade;
    private final SecurityUserMapper securityUserMapper;
    private final TokenRevocationPort tokenRevocationPort;

    // se q son tipos distintos, luego usare un mapper
    public AuthResponse register(User user) {
        String encodedPassword = passwordService.encode(user.getPassword());
        user.setPassword(encodedPassword);
        User savedUser = repository.save(user);

        var userDetails = securityUserMapper.toUserDetails(savedUser);
        var jwt = tokenProvider.generateToken(userDetails);

        UserResponse userResponse = securityUserMapper.toUserResponse(savedUser);
        AuthTokens tokens = new AuthTokens(jwt, "", 86400000L);

        return new AuthResponse(userResponse, tokens);

    }

    public AuthResponse login(AuthRequest request) {
        authenticationFacade.authenticate(request.email(), request.password());

        var user = repository.findByEmail(request.email())
                .orElseThrow(
                        () -> new UsernameNotFoundException("User not found with email: " + request.email()));
        var userDetails = securityUserMapper.toUserDetails(user);
        var jwt = tokenProvider.generateToken(userDetails);

        UserResponse userResponse = securityUserMapper.toUserResponse(user);
        AuthTokens tokens = new AuthTokens(jwt, "", 86400000L);

        return new AuthResponse(userResponse, tokens);
    }

    public void logout(String authHeader) {
        String token = authHeader.replace("Bearer ", "");
        tokenRevocationPort.invalidate(token);
    }
}