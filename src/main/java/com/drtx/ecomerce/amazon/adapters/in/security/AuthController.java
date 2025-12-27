package com.drtx.ecomerce.amazon.adapters.in.security;

import com.drtx.ecomerce.amazon.adapters.in.security.dto.AuthRequest;
import com.drtx.ecomerce.amazon.adapters.in.security.dto.AuthResponse;
import com.drtx.ecomerce.amazon.adapters.in.security.dto.RegisterRequest;
import com.drtx.ecomerce.amazon.adapters.in.security.mappers.UserSecurityMapper;
import com.drtx.ecomerce.amazon.application.usecases.auth.AuthService;
import com.drtx.ecomerce.amazon.core.model.user.User;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;
    private final UserSecurityMapper userSecurityMapper;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody @Valid RegisterRequest request) {
        User user = userSecurityMapper.registerRequestToDomain(request);
        return ResponseEntity.ok(authService.register(user));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody @Valid AuthRequest request) {
        System.out.println("AuthController - Login request received for: " + request.email());
        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@RequestHeader("Authorization") String authHeader) {
        authService.logout(authHeader);
        return ResponseEntity.noContent().build();
    }
}
