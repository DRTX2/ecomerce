package com.drtx.ecomerce.amazon.adapters.in.security;

import com.drtx.ecomerce.amazon.adapters.in.security.dto.AuthRequest;
import com.drtx.ecomerce.amazon.adapters.in.security.dto.AuthResponse;
import com.drtx.ecomerce.amazon.adapters.in.security.dto.AuthTokens;
import com.drtx.ecomerce.amazon.adapters.in.security.dto.RefreshTokenRequest;
import com.drtx.ecomerce.amazon.adapters.in.security.dto.RegisterRequest;
import com.drtx.ecomerce.amazon.adapters.in.security.dto.UserResponse;
import com.drtx.ecomerce.amazon.adapters.in.security.mappers.UserSecurityMapper;
import com.drtx.ecomerce.amazon.core.model.security.AuthResult;
import com.drtx.ecomerce.amazon.core.model.security.LoginCommand;
import com.drtx.ecomerce.amazon.core.model.user.User;
import com.drtx.ecomerce.amazon.core.ports.in.rest.security.AuthUseCasePort;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthUseCasePort authService;
    private final UserSecurityMapper userSecurityMapper;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody @Valid RegisterRequest request) {
        User user = userSecurityMapper.registerRequestToDomain(request);
        AuthResult result = authService.register(user);
        return ResponseEntity.ok(toAuthResponse(result));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody @Valid AuthRequest request) {
        System.out.println("AuthController - Login request received for: " + request.email());
        LoginCommand command = new LoginCommand(request.email(), request.password());
        AuthResult result = authService.login(command);
        return ResponseEntity.ok(toAuthResponse(result));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@RequestHeader("Authorization") String authHeader) {
        String token = authHeader.replace("Bearer ", "");
        authService.logout(token);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refreshToken(@RequestBody RefreshTokenRequest request) {
        AuthResult result = authService.refreshToken(request.refreshToken());
        return ResponseEntity.ok(toAuthResponse(result));
    }

    private AuthResponse toAuthResponse(AuthResult result) {
        UserResponse userResponse = new UserResponse(
                result.user().getUuid(),
                result.user().getName(),
                result.user().getEmail(),
                result.user().getRole());

        AuthTokens tokens = new AuthTokens(
                result.accessToken(),
                result.refreshToken(),
                result.expiresInMs());

        return new AuthResponse(userResponse, tokens);
    }
}
