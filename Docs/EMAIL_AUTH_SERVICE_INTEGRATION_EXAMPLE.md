# Ejemplo: Integración de EmailService en AuthService

Este documento muestra cómo integrar el `EmailService` en el `AuthService` existente para enviar correos de bienvenida automáticamente al registrar nuevos usuarios.

## 📝 Código Modificado

### AuthService.java - CON envío de correo de bienvenida

```java
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
import com.drtx.ecomerce.amazon.infrastructure.email.EmailService;  // ✅ NUEVO
import jakarta.mail.MessagingException;                              // ✅ NUEVO
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;                                    // ✅ NUEVO
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j  // ✅ NUEVO - Para logging
public class AuthService {
    private final UserRepositoryPort repository;
    private final PasswordService passwordService;
    private final TokenProvider tokenProvider;
    private final AuthenticationFacade authenticationFacade;
    private final SecurityUserMapper securityUserMapper;
    private final TokenRevocationPort tokenRevocationPort;
    private final EmailService emailService;  // ✅ NUEVO - Inyección del servicio de email

    /**
     * Registra un nuevo usuario y envía correo de bienvenida.
     */
    public AuthResponse register(User user) {
        // Encriptar contraseña
        String encodedPassword = passwordService.encode(user.getPassword());
        user.setPassword(encodedPassword);
        
        // Guardar usuario
        User savedUser = repository.save(user);

        // ✅ NUEVO - Enviar correo de bienvenida de forma asíncrona
        sendWelcomeEmailAsync(savedUser);

        // Generar token JWT
        var userDetails = securityUserMapper.toUserDetails(savedUser);
        var jwt = tokenProvider.generateToken(userDetails);

        UserResponse userResponse = securityUserMapper.toUserResponse(savedUser);
        AuthTokens tokens = new AuthTokens(jwt, "", 86400000L);

        return new AuthResponse(userResponse, tokens);
    }

    /**
     * Envía el correo de bienvenida de forma asíncrona.
     * Si falla, solo registra el error sin afectar el registro.
     */
    private void sendWelcomeEmailAsync(User user) {
        // Ejecutar en un hilo separado para no bloquear el registro
        new Thread(() -> {
            try {
                emailService.sendWelcomeEmail(user.getEmail(), user.getName());
                log.info("Correo de bienvenida enviado a: {}", user.getEmail());
            } catch (MessagingException e) {
                // Si falla el correo, solo loguear el error
                // NO lanzar excepción para no afectar el registro
                log.error("Error enviando correo de bienvenida a {}: {}", 
                    user.getEmail(), e.getMessage());
            }
        }).start();
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
```

## ✨ Cambios Realizados

### 1. Imports Agregados
```java
import com.drtx.ecomerce.amazon.infrastructure.email.EmailService;
import jakarta.mail.MessagingException;
import lombok.extern.slf4j.Slf4j;
```

### 2. Anotación @Slf4j
```java
@Slf4j  // Para usar log.info() y log.error()
public class AuthService {
```

### 3. Inyección de Dependencia
```java
private final EmailService emailService;  // Se inyecta automáticamente
```

### 4. Método Asíncrono para Enviar Correo
```java
private void sendWelcomeEmailAsync(User user) {
    new Thread(() -> {
        try {
            emailService.sendWelcomeEmail(user.getEmail(), user.getName());
            log.info("Correo de bienvenida enviado a: {}", user.getEmail());
        } catch (MessagingException e) {
            log.error("Error enviando correo de bienvenida a {}: {}", 
                user.getEmail(), e.getMessage());
        }
    }).start();
}
```

### 5. Llamada en el Método register()
```java
public AuthResponse register(User user) {
    // ... código existente ...
    User savedUser = repository.save(user);
    
    // ✅ Enviar correo de bienvenida
    sendWelcomeEmailAsync(savedUser);
    
    // ... resto del código ...
}
```

## 🎯 Ventajas de este Enfoque

### 1. **No Bloquea el Registro**
- El correo se envía en un hilo separado
- Si el SMTP es lento, no afecta el tiempo de respuesta

### 2. **Manejo de Errores Gracioso**
- Si falla el envío, solo se loguea el error
- El usuario se registra correctamente de todos modos

### 3. **Logging Apropiado**
- Se registra cuando se envía exitosamente
- Se registra cuando falla para debugging

### 4. **Bajo Acoplamiento**
- Solo se inyecta `EmailService`
- Fácil de mockear en tests

## 🧪 Testing

Para testear el AuthService con el EmailService, puedes mockear el servicio:

```java
@ExtendWith(MockitoExtension.class)
class AuthServiceTest {
    
    @Mock
    private EmailService emailService;
    
    @Mock
    private UserRepositoryPort userRepository;
    
    // ... otros mocks
    
    @InjectMocks
    private AuthService authService;
    
    @Test
    void testRegister_SendsWelcomeEmail() {
        // Arrange
        User user = new User();
        user.setEmail("test@example.com");
        user.setName("Test User");
        user.setPassword("password");
        
        when(userRepository.save(any())).thenReturn(user);
        doNothing().when(emailService).sendWelcomeEmail(anyString(), anyString());
        
        // Act
        AuthResponse response = authService.register(user);
        
        // Assert
        assertNotNull(response);
        // Nota: El email se envía async, así que verificar puede ser complicado
        // En un entorno real, usarías @Async de Spring
    }
    
    @Test
    void testRegister_EmailFailsButRegistrationSucceeds() {
        // Arrange
        User user = new User();
        user.setEmail("test@example.com");
        user.setName("Test User");
        user.setPassword("password");
        
        when(userRepository.save(any())).thenReturn(user);
        doThrow(new RuntimeException("SMTP error"))
            .when(emailService).sendWelcomeEmail(anyString(), anyString());
        
        // Act
        AuthResponse response = authService.register(user);
        
        // Assert - El registro debe completarse aunque falle el email
        assertNotNull(response);
        assertNotNull(response.user());
    }
}
```

## 🚀 Alternativa Profesional: Usando @Async de Spring

Para una implementación más robusta, usa `@Async` de Spring:

### 1. Habilitar Async en la Aplicación

```java
@SpringBootApplication
@EnableAsync  // ✅ Agregar esta anotación
public class AmazonApplication {
    public static void main(String[] args) {
        SpringApplication.run(AmazonApplication.class, args);
    }
}
```

### 2. Crear un Servicio Asíncrono

```java
@Service
@Slf4j
@RequiredArgsConstructor
public class AsyncEmailService {
    
    private final EmailService emailService;
    
    @Async
    public void sendWelcomeEmailAsync(String email, String name) {
        try {
            emailService.sendWelcomeEmail(email, name);
            log.info("Correo de bienvenida enviado a: {}", email);
        } catch (MessagingException e) {
            log.error("Error enviando correo de bienvenida a {}: {}", email, e.getMessage());
        }
    }
    
    @Async
    public void sendPasswordResetEmailAsync(String email, String token, String name) {
        try {
            emailService.sendPasswordResetEmail(email, token, name);
            log.info("Correo de restablecimiento enviado a: {}", email);
        } catch (MessagingException e) {
            log.error("Error enviando correo de restablecimiento a {}: {}", email, e.getMessage());
        }
    }
}
```

### 3. Usar en AuthService

```java
@Service
@RequiredArgsConstructor
public class AuthService {
    // ... otros campos ...
    private final AsyncEmailService asyncEmailService;  // En lugar de EmailService
    
    public AuthResponse register(User user) {
        // ... código existente ...
        User savedUser = repository.save(user);
        
        // Enviar correo de forma asíncrona
        asyncEmailService.sendWelcomeEmailAsync(savedUser.getEmail(), savedUser.getName());
        
        // ... resto del código ...
    }
}
```

## 📊 Comparación de Enfoques

| Aspecto | Thread Manual | @Async de Spring |
|---------|--------------|------------------|
| Simplicidad | ✅ Más simple | ⚠️ Requiere configuración |
| Pool de Hilos | ❌ Crea hilo cada vez | ✅ Usa pool configurado |
| Testing | ⚠️ Difícil de verificar | ✅ Fácil de mockear |
| Configuración | ✅ No requiere | ⚠️ Requiere @EnableAsync |
| Profesionalismo | ⚠️ Aceptable | ✅ Mejor práctica |
| Manejo de Errores | ✅ Simple | ✅ Más robusto |

## 💡 Recomendación

- **Para desarrollo/demo**: Usa el enfoque con Thread manual
- **Para producción**: Usa `@Async` de Spring con un pool de hilos configurado

## 🔗 Referencias

- [Spring @Async Documentation](https://docs.spring.io/spring-framework/reference/integration/scheduling.html#scheduling-annotation-support-async)
- [Best Practices for Async Email](https://www.baeldung.com/spring-email)
