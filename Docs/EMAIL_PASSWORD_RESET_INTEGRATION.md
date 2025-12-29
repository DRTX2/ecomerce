# Ejemplo de Integración: Restablecimiento de Contraseña

Este documento muestra cómo integrar el `EmailService` con el módulo de autenticación para implementar el restablecimiento de contraseña.

## 🔄 Flujo del Proceso

1. Usuario solicita restablecer contraseña (proporciona email)
2. Sistema genera un token único y lo guarda con expiración
3. Se envía correo con el token al usuario
4. Usuario ingresa el token y nueva contraseña
5. Sistema valida el token y actualiza la contraseña

## 📝 Implementación

### 1. Crear la Entidad PasswordResetToken

```java
package com.drtx.ecomerce.amazon.core.domain;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "password_reset_tokens")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PasswordResetToken {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, unique = true)
    private String token;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @Column(nullable = false)
    private LocalDateTime expiryDate;
    
    @Column(nullable = false)
    private boolean used = false;
    
    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expiryDate);
    }
}
```

### 2. Crear el Repositorio

```java
package com.drtx.ecomerce.amazon.adapters.out.persistence;

import com.drtx.ecomerce.amazon.core.domain.PasswordResetToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {
    
    Optional<PasswordResetToken> findByToken(String token);
    
    void deleteByUserId(Long userId);
}
```

### 3. Crear el Port de Salida

```java
package com.drtx.ecomerce.amazon.core.ports.out.persistence;

import com.drtx.ecomerce.amazon.core.domain.PasswordResetToken;
import java.util.Optional;

public interface PasswordResetTokenRepositoryPort {
    
    PasswordResetToken save(PasswordResetToken token);
    
    Optional<PasswordResetToken> findByToken(String token);
    
    void deleteByUserId(Long userId);
}
```

### 4. Implementar el Adaptador

```java
package com.drtx.ecomerce.amazon.adapters.out.persistence;

import com.drtx.ecomerce.amazon.core.domain.PasswordResetToken;
import com.drtx.ecomerce.amazon.core.ports.out.persistence.PasswordResetTokenRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class PasswordResetTokenAdapter implements PasswordResetTokenRepositoryPort {
    
    private final PasswordResetTokenRepository repository;
    
    @Override
    public PasswordResetToken save(PasswordResetToken token) {
        return repository.save(token);
    }
    
    @Override
    public Optional<PasswordResetToken> findByToken(String token) {
        return repository.findByToken(token);
    }
    
    @Override
    public void deleteByUserId(Long userId) {
        repository.deleteByUserId(userId);
    }
}
```

### 5. Crear DTOs

```java
// Request para solicitar restablecimiento
public record RequestPasswordResetDto(
    @NotBlank(message = "Email es requerido")
    @Email(message = "Email debe ser válido")
    String email
) {}

// Request para confirmar restablecimiento
public record ConfirmPasswordResetDto(
    @NotBlank(message = "Token es requerido")
    String token,
    
    @NotBlank(message = "Nueva contraseña es requerida")
    @Size(min = 8, message = "La contraseña debe tener al menos 8 caracteres")
    @Pattern(regexp = ".*[A-Z].*", message = "Debe contener al menos una mayúscula")
    @Pattern(regexp = ".*[0-9].*", message = "Debe contener al menos un número")
    String newPassword,
    
    @NotBlank(message = "Confirmación de contraseña es requerida")
    String confirmPassword
) {
    public boolean passwordsMatch() {
        return newPassword.equals(confirmPassword);
    }
}
```

### 6. Actualizar el UseCase de Autenticación

```java
package com.drtx.ecomerce.amazon.application.usecases;

import com.drtx.ecomerce.amazon.core.domain.PasswordResetToken;
import com.drtx.ecomerce.amazon.core.domain.User;
import com.drtx.ecomerce.amazon.core.ports.out.persistence.PasswordResetTokenRepositoryPort;
import com.drtx.ecomerce.amazon.core.ports.out.persistence.UserRepositoryPort;
import com.drtx.ecomerce.amazon.core.ports.out.security.PasswordEncoder;
import com.drtx.ecomerce.amazon.infrastructure.email.EmailService;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;

@Service
@RequiredArgsConstructor
@Slf4j
public class PasswordResetService {
    
    private final UserRepositoryPort userRepository;
    private final PasswordResetTokenRepositoryPort tokenRepository;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;
    
    private static final int TOKEN_EXPIRATION_HOURS = 1;
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();
    
    /**
     * Solicita el restablecimiento de contraseña enviando un token al email del usuario.
     */
    @Transactional
    public void requestPasswordReset(String email) throws MessagingException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        
        // Eliminar tokens anteriores del usuario
        tokenRepository.deleteByUserId(user.getId());
        
        // Generar token seguro de 6 caracteres alfanuméricos
        String token = generateSecureToken();
        
        // Crear y guardar el token con expiración
        PasswordResetToken resetToken = PasswordResetToken.builder()
                .token(token)
                .user(user)
                .expiryDate(LocalDateTime.now().plusHours(TOKEN_EXPIRATION_HOURS))
                .used(false)
                .build();
        
        tokenRepository.save(resetToken);
        
        // Enviar correo con el token
        emailService.sendPasswordResetEmail(user.getEmail(), token, user.getName());
        
        log.info("Token de restablecimiento enviado a: {}", email);
    }
    
    /**
     * Confirma el restablecimiento de contraseña usando el token.
     */
    @Transactional
    public void confirmPasswordReset(String token, String newPassword) {
        PasswordResetToken resetToken = tokenRepository.findByToken(token)
                .orElseThrow(() -> new RuntimeException("Token inválido"));
        
        // Validaciones
        if (resetToken.isUsed()) {
            throw new RuntimeException("El token ya fue utilizado");
        }
        
        if (resetToken.isExpired()) {
            throw new RuntimeException("El token ha expirado");
        }
        
        // Actualizar contraseña
        User user = resetToken.getUser();
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        
        // Marcar token como usado
        resetToken.setUsed(true);
        tokenRepository.save(resetToken);
        
        log.info("Contraseña restablecida exitosamente para usuario: {}", user.getEmail());
    }
    
    /**
     * Genera un token seguro de 6 caracteres alfanuméricos.
     */
    private String generateSecureToken() {
        byte[] randomBytes = new byte[4];
        SECURE_RANDOM.nextBytes(randomBytes);
        String token = Base64.getUrlEncoder()
                .withoutPadding()
                .encodeToString(randomBytes)
                .substring(0, 6)
                .toUpperCase();
        return token;
    }
}
```

### 7. Crear el Controller

```java
package com.drtx.ecomerce.amazon.adapters.in.security;

import com.drtx.ecomerce.amazon.application.usecases.PasswordResetService;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth/password-reset")
@RequiredArgsConstructor
@Slf4j
public class PasswordResetController {
    
    private final PasswordResetService passwordResetService;
    
    /**
     * Endpoint para solicitar restablecimiento de contraseña.
     * POST /auth/password-reset/request
     */
    @PostMapping("/request")
    public ResponseEntity<MessageResponse> requestPasswordReset(
            @Valid @RequestBody RequestPasswordResetDto request
    ) {
        try {
            passwordResetService.requestPasswordReset(request.email());
            return ResponseEntity.ok(
                new MessageResponse("Se ha enviado un correo con las instrucciones")
            );
        } catch (MessagingException e) {
            log.error("Error enviando correo: {}", e.getMessage());
            return ResponseEntity.status(500)
                .body(new MessageResponse("Error al enviar el correo"));
        } catch (RuntimeException e) {
            log.error("Error: {}", e.getMessage());
            // Por seguridad, siempre devolver el mismo mensaje
            return ResponseEntity.ok(
                new MessageResponse("Si el correo existe, recibirás instrucciones")
            );
        }
    }
    
    /**
     * Endpoint para confirmar restablecimiento con el token.
     * POST /auth/password-reset/confirm
     */
    @PostMapping("/confirm")
    public ResponseEntity<MessageResponse> confirmPasswordReset(
            @Valid @RequestBody ConfirmPasswordResetDto request
    ) {
        try {
            if (!request.passwordsMatch()) {
                return ResponseEntity.badRequest()
                    .body(new MessageResponse("Las contraseñas no coinciden"));
            }
            
            passwordResetService.confirmPasswordReset(
                request.token(), 
                request.newPassword()
            );
            
            return ResponseEntity.ok(
                new MessageResponse("Contraseña restablecida exitosamente")
            );
        } catch (RuntimeException e) {
            log.error("Error: {}", e.getMessage());
            return ResponseEntity.badRequest()
                .body(new MessageResponse(e.getMessage()));
        }
    }
    
    record MessageResponse(String message) {}
}
```

### 8. Actualizar SecurityConfig

```java
// En SecurityConfig.java, agregar los nuevos endpoints como públicos:

http
    .authorizeHttpRequests(auth -> auth
        .requestMatchers("/auth/**").permitAll()
        .requestMatchers("/auth/register", "/auth/login").permitAll()
        .requestMatchers("/auth/password-reset/**").permitAll()  // NUEVO
        .anyRequest().authenticated()
    )
```

## 🧪 Pruebas con Postman/Thunder Client

### 1. Solicitar Restablecimiento

```http
POST http://localhost:8080/api/v1/auth/password-reset/request
Content-Type: application/json

{
  "email": "usuario@example.com"
}
```

**Respuesta:**
```json
{
  "message": "Se ha enviado un correo con las instrucciones"
}
```

### 2. Confirmar con Token

```http
POST http://localhost:8080/api/v1/auth/password-reset/confirm
Content-Type: application/json

{
  "token": "ABC123",
  "newPassword": "NuevaPassword123",
  "confirmPassword": "NuevaPassword123"
}
```

**Respuesta:**
```json
{
  "message": "Contraseña restablecida exitosamente"
}
```

## 🎯 Mejoras Futuras

1. **Rate Limiting**: Limitar intentos de restablecimiento por IP/email
2. **Tokens más seguros**: Usar UUID en lugar de tokens cortos
3. **Notificación adicional**: Enviar correo cuando se cambie la contraseña
4. **Historial de contraseñas**: Evitar reutilizar contraseñas anteriores
5. **2FA**: Agregar segundo factor de autenticación

## 📚 Referencias

- [OWASP Password Reset Best Practices](https://cheatsheetseries.owasp.org/cheatsheets/Forgot_Password_Cheat_Sheet.html)
- [Spring Security Documentation](https://docs.spring.io/spring-security/reference/index.html)
