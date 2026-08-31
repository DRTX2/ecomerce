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
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Tag(name = "Autenticación", description = "Endpoints de autenticación y autorización (públicos)")
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthUseCasePort authService;
    private final UserSecurityMapper userSecurityMapper;

    @Operation(summary = "Registrar nuevo usuario", description = "Crea una nueva cuenta de usuario y devuelve tokens de acceso")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Usuario registrado exitosamente",
                    content = @Content(schema = @Schema(implementation = AuthResponse.class))),
            @ApiResponse(responseCode = "400", description = "Datos de registro inválidos"),
            @ApiResponse(responseCode = "409", description = "Email ya registrado")
    })
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody @Valid RegisterRequest request) {
        User user = userSecurityMapper.registerRequestToDomain(request);
        AuthResult result = authService.register(user);
        return ResponseEntity.ok(toAuthResponse(result));
    }

    @Operation(summary = "Iniciar sesión", description = "Autentica un usuario y devuelve tokens JWT de acceso y refresh")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Login exitoso",
                    content = @Content(schema = @Schema(implementation = AuthResponse.class))),
            @ApiResponse(responseCode = "400", description = "Credenciales inválidas"),
            @ApiResponse(responseCode = "401", description = "Email o contraseña incorrectos"),
            @ApiResponse(responseCode = "423", description = "Cuenta bloqueada")
    })
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody @Valid AuthRequest request) {
        LoginCommand command = new LoginCommand(request.email(), request.password());
        AuthResult result = authService.login(command);
        return ResponseEntity.ok(toAuthResponse(result));
    }

    @Operation(summary = "Cerrar sesión", description = "Invalida el token de acceso actual (requiere autenticación)")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Logout exitoso"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "400", description = "Token inválido")
    })
    @SecurityRequirement(name = "bearerAuth")
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(
            @Parameter(description = "Bearer token", required = true)
            @RequestHeader("Authorization") String authHeader) {
        String token = authHeader.replace("Bearer ", "");
        authService.logout(token);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Refrescar token de acceso", description = "Obtiene un nuevo access token usando el refresh token")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Token refrescado exitosamente",
                    content = @Content(schema = @Schema(implementation = AuthResponse.class))),
            @ApiResponse(responseCode = "400", description = "Refresh token inválido o expirado"),
            @ApiResponse(responseCode = "401", description = "Refresh token revocado")
    })
    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refreshToken(@RequestBody RefreshTokenRequest request) {
        AuthResult result = authService.refreshToken(request.refreshToken());
        return ResponseEntity.ok(toAuthResponse(result));
    }

    private AuthResponse toAuthResponse(AuthResult result) {
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
