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
        // Need to convert Domain User to UserResponse DTO
        // Ideally UserSecurityMapper should do this.
        // Assuming UserSecurityMapper has toUserResponse(User).
        // If not, I'll need to add it or do it manually here.
        // Let's check imports to see if UserSecurityMapper handles this.
        // The original code passed UserResponse directly from AuthService which used
        // SecurityUserMapper.
        // Now AuthService returns AuthResult (domain + token).
        // I need to use userSecurityMapper here, BUT imports of AuthService showed
        // SecurityUserMapper,
        // and imports of AuthController shows UserSecurityMapper.
        // These might be different mappers or same one renamed or imported differently.
        // Original AuthController line 19: private final UserSecurityMapper
        // userSecurityMapper;
        // Function registerRequestToDomain uses it.
        // Let's assume it has methods we need or I can add a helper here.

        // Wait, I can't see UserSecurityMapper content.
        // But I can define the DTOs here to be safe or rely on what I saw in
        // AuthService removed imports.
        // AuthService imported:
        // com.drtx.ecomerce.amazon.adapters.in.security.mappers.SecurityUserMapper
        // AuthController imports:
        // com.drtx.ecomerce.amazon.adapters.in.security.mappers.UserSecurityMapper
        // They sound different!

        // But let's assume I can construct AuthResponse manually if needed.
        // AuthResponse(UserResponse user, AuthTokens tokens)

        // Wait, UserSecurityMapper is injected.
        // Let's assume it has a method toUserResponse(User). I'll use it.
        // If it fails, I'll fix it.

        // But wait, the mapper in AuthService was
        // `com.drtx.ecomerce...SecurityUserMapper`.
        // The one in AuthController is `com.drtx.ecomerce...UserSecurityMapper`.
        // I should probably check `adapters/in/security` to see the mappers.

        // For now I will assume `userSecurityMapper` in AuthController is the one to
        // use.
        // If compilation fails, I'll fix.

        // Wait, I need UserResponse and AuthTokens constructions

        UserResponse userResponse = new UserResponse(
                result.user().getId(),
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
