# Módulo de Seguridad - Arquitectura Hexagonal

## Estructura del Módulo de Seguridad

### 📦 Core (Dominio)

#### `core/model/security/`
- **AuthResult.java** - Resultado de autenticación (AccessToken, RefreshToken, User, expiración)
- **LoginCommand.java** - Comando para login
- **RefreshToken.java** - Modelo de dominio del Refresh Token
- **Token.java** - Modelo de dominio del token

#### `core/ports/in/rest/security/`
- **AuthUseCasePort.java** - Puerto de entrada para casos de uso de autenticación
  - `register(User)` - Registro de usuario
  - `login(LoginCommand)` - Inicio de sesión
  - `logout(String token)` - Cierre de sesión
  - `refreshToken(String refreshToken)` - Renovar access token

#### `core/ports/out/security/`
- **TokenProvider.java** - Puerto para generación y validación de tokens
  - `generateAccessToken(User)` - Genera access token (24h)
  - `generateRefreshToken(User)` - Genera refresh token (7 días)
  - `extractUsername(String token)` - Extrae username del token
  - `isTokenValid(String token, User)` - Valida access token
  - `isRefreshTokenValid(String refreshToken)` - Valida refresh token

- **TokenRevocationPort.java** - Puerto para revocación de tokens
  - `invalidate(String token)` - Invalida un token
  - `isInvalidated(String token)` - Verifica si está invalidado

- **RefreshTokenRepositoryPort.java** - Puerto para persistencia de refresh tokens
  - `save(RefreshToken)` - Guarda refresh token
  - `findByToken(String)` - Busca por token
  - `findByUserEmail(String)` - Busca por email de usuario
  - `deleteByUserEmail(String)` - Elimina por email
  - `deleteByToken(String)` - Elimina por token
  - `revokeByUserEmail(String)` - Revoca por email

- **PasswordService.java** - Puerto para encriptación de contraseñas
- **AuthenticationFacade.java** - Puerto para autenticación con Spring Security

---

### 🔧 Application (Casos de Uso)

#### `application/usecases/auth/`
- **AuthService.java** - Implementación de casos de uso de autenticación
  - Orquesta la lógica de registro, login, logout
  - Genera access y refresh tokens
  - Valida contraseñas y reglas de negocio
  
- **RefreshTokenService.java** - Servicio de aplicación para refresh tokens
  - `createRefreshToken(User)` - Crea un nuevo refresh token
  - `verifyAndGetRefreshToken(String)` - Verifica y obtiene refresh token
  - `revokeRefreshToken(String userEmail)` - Revoca refresh token por email
  - `deleteRefreshToken(String token)` - Elimina refresh token

---

### 🌐 Adapters IN (REST/Controllers)

#### `adapters/in/security/`

**Controllers:**
- **AuthController.java** - Controlador REST para autenticación
  - `POST /auth/register` - Registro de usuario
  - `POST /auth/login` - Inicio de sesión
  - `POST /auth/logout` - Cierre de sesión
  - `POST /auth/refresh` - Renovar access token

**DTOs:**
- **RegisterRequest.java** - Request para registro
- **AuthRequest.java** - Request para login
- **RefreshTokenRequest.java** - Request para refresh token
- **AuthResponse.java** - Response con user + tokens
- **UserResponse.java** - DTO del usuario
- **AuthTokens.java** - DTO de tokens (access, refresh, expiration)

**Security Components:**
- **JwtAuthFilter.java** - Filtro de autenticación JWT
  - Intercepta requests
  - Valida JWT en header Authorization
  - Verifica si el token está revocado
  - Establece autenticación en SecurityContext
  - Skip de rutas públicas (/auth/*, GET /products/*, GET /categories/*)

- **SecurityUserDetails.java** - Adaptador de User a UserDetails
  - Implementa UserDetails de Spring Security
  - Convierte User del dominio a formato de Spring Security
  - Provee authorities basadas en UserRole

- **JpaUserDetailsService.java** - Servicio para cargar usuarios
  - Implementa UserDetailsService de Spring Security
  - Carga usuarios desde repositorio de dominio

**Mappers:**
- **UserSecurityMapper.java** - Mapea entre DTOs y dominio
- **SecurityUserMapper.java** - Mapea User a SecurityUserDetails
- **AuthResponseMapper.java** - Mapea AuthResult a AuthResponse

---

### 💾 Adapters OUT (Persistencia)

#### `adapters/out/persistence/security/`

**Entities:**
- **RevokedToken.java** - Entidad JPA para tokens revocados
- **RefreshTokenEntity.java** - Entidad JPA para refresh tokens

**Repositories:**
- **RevokedTokenRepository.java** - JPA Repository para tokens revocados
- **RefreshTokenJpaRepository.java** - JPA Repository para refresh tokens

**Adapters:**
- **RevokedTokenPersistenceAdapter.java** - Implementa TokenRevocationPort
  - Adapta puerto de dominio a JPA
  - Persiste tokens revocados en BD
  
- **RefreshTokenPersistenceAdapter.java** - Implementa RefreshTokenRepositoryPort
  - Adapta puerto de dominio a JPA
  - Persiste refresh tokens en BD

**Mappers:**
- **RefreshTokenMapper.java** - Mapea entre RefreshToken (dominio) y RefreshTokenEntity (JPA)

---

### 🏗️ Infrastructure (Configuración)

#### `infrastructure/security/`

- **JwtService.java** - Implementa TokenProvider
  - Genera access tokens (24h) y refresh tokens (7 días)
  - Valida tokens usando JJWT
  - Extrae claims de tokens
  - Usa secret key configurable

- **SecurityConfig.java** - Configuración de Spring Security
  - Define SecurityFilterChain
  - Configura CORS
  - Define rutas públicas y protegidas
  - Configura AuthenticationProvider
  - Configura PasswordEncoder (BCrypt)
  - Agrega JwtAuthFilter antes de UsernamePasswordAuthenticationFilter

- **TokenRevocationService.java** - Implementa TokenRevocationPort
  - Servicio para revocar tokens (usando Redis o base de datos)

- **CustomAuthenticationEntryPoint.java** - Manejo de errores de autenticación
  - Personaliza respuestas para errores 401

- **SpringAuthenticationFacade.java** - Implementa AuthenticationFacade
  - Adapta AuthenticationManager de Spring Security
  - Realiza autenticación con email/password

- **BCryptPasswordService.java** - Implementa PasswordService
  - Encripta contraseñas usando BCrypt

---

## Flujo de Autenticación

### 1. Registro (Register)
```
Client → POST /auth/register
  ↓
AuthController (IN)
  ↓
AuthService (Application)
  ↓ valida contraseña
  ↓ encode password → PasswordService (OUT)
  ↓ save user → UserRepositoryPort (OUT)
  ↓ generateAccessToken → TokenProvider (OUT)
  ↓ createRefreshToken → RefreshTokenService (Application)
     ↓ generateRefreshToken → TokenProvider (OUT)
     ↓ save → RefreshTokenRepositoryPort (OUT)
  ↓
AuthResult (access + refresh tokens)
  ↓
AuthController → AuthResponse
  ↓
Client ← 200 OK {user, tokens}
```

### 2. Login
```
Client → POST /auth/login {email, password}
  ↓
AuthController (IN)
  ↓
AuthService (Application)
  ↓ authenticate → AuthenticationFacade (OUT)
  ↓ findByEmail → UserRepositoryPort (OUT)
  ↓ generateAccessToken → TokenProvider (OUT)
  ↓ createRefreshToken → RefreshTokenService
  ↓
AuthResult (access + refresh tokens)
  ↓
Client ← 200 OK {user, tokens}
```

### 3. Request Autenticada
```
Client → GET /products (protected)
Headers: Authorization: Bearer <access_token>
  ↓
JwtAuthFilter (IN)
  ↓ extractToken
  ↓ isInvalidated? → TokenRevocationPort (OUT)
  ↓ extractUsername → TokenProvider (OUT)
  ↓ loadUserByUsername → UserDetailsService (IN)
  ↓ isTokenValid → TokenProvider (OUT)
  ↓ set SecurityContext
  ↓
Controller procesa request
  ↓
Client ← 200 OK
```

### 4. Refresh Token
```
Client → POST /auth/refresh {refreshToken}
  ↓
AuthController (IN)
  ↓
AuthService (Application)
  ↓ refreshToken → RefreshTokenService
     ↓ verifyAndGetRefreshToken
     ↓ findByToken → RefreshTokenRepositoryPort (OUT)
     ↓ validate (not expired, not revoked)
  ↓ findByEmail → UserRepositoryPort (OUT)
  ↓ generateAccessToken → TokenProvider (OUT)
  ↓
AuthResult (new access token, same refresh token)
  ↓
Client ← 200 OK {user, tokens}
```

### 5. Logout
```
Client → POST /auth/logout
Headers: Authorization: Bearer <access_token>
  ↓
AuthController (IN)
  ↓
AuthService (Application)
  ↓ invalidate → TokenRevocationPort (OUT)
  ↓ revokeRefreshToken → RefreshTokenService
     ↓ revokeByUserEmail → RefreshTokenRepositoryPort (OUT)
  ↓
Client ← 204 No Content
```

---

## Cumplimiento de Arquitectura Hexagonal ✅

### ✅ Separación de Capas
- **Core**: Lógica de negocio pura (AuthResult, RefreshToken, LoginCommand)
- **Ports**: Interfaces que definen contratos
- **Application**: Casos de uso (AuthService, RefreshTokenService)
- **Adapters**: Implementaciones concretas (REST, JPA, JWT)
- **Infrastructure**: Configuración técnica (Spring Security)

### ✅ Inversión de Dependencias
- Application depende de Ports (interfaces)
- Adapters implementan Ports
- Core no depende de nada externo

### ✅ Independencia de Frameworks
- Core no conoce Spring Security, JWT, JPA
- Dominio expresado en lenguaje del negocio
- Fácil testear sin frameworks

### ✅ Testabilidad
- Puertos permiten mocks fáciles
- Lógica de negocio aislada
- Casos de uso testeables sin BD o HTTP

---

## Configuración

### application.yml
```yaml
security:
  jwt:
    secret-key: ${JWT_SECRET_KEY}
    expiration: 86400000  # 24 hours in ms
    refresh-expiration: 604800000  # 7 days in ms
```

### Variables de Entorno
- `JWT_SECRET_KEY`: Clave secreta para firmar tokens (mínimo 256 bits)

---

## Base de Datos

### Tabla: refresh_tokens
```sql
CREATE TABLE refresh_tokens (
    id BIGSERIAL PRIMARY KEY,
    token VARCHAR(512) NOT NULL UNIQUE,
    user_email VARCHAR(255) NOT NULL,
    expiry_date TIMESTAMP NOT NULL,
    revoked BOOLEAN NOT NULL DEFAULT false,
    FOREIGN KEY (user_email) REFERENCES users(email) ON DELETE CASCADE
);
```

### Tabla: revoked_tokens
```sql
CREATE TABLE revoked_tokens (
    id BIGSERIAL PRIMARY KEY,
    token VARCHAR(512) NOT NULL UNIQUE,
    revoked_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);
```

---

## Seguridad

### Access Token (JWT)
- **Tiempo de vida**: 24 horas
- **Tipo**: Bearer token en header Authorization
- **Claims**: email (subject), authorities, type=access
- **Uso**: Todas las requests autenticadas

### Refresh Token (JWT)
- **Tiempo de vida**: 7 días
- **Almacenamiento**: Base de datos + cliente
- **Claims**: email (subject), type=refresh
- **Uso**: Solo para endpoint /auth/refresh
- **Seguridad**: Se revoca al hacer logout o al crear uno nuevo

### Tokens Revocados
- Almacenados en BD cuando se hace logout
- Verificados en cada request por JwtAuthFilter
- Limpieza automática de tokens expirados (implementar job)

---

## Mejoras Futuras

1. **Redis para tokens revocados** - Mejor performance que BD
2. **Rate limiting** en endpoints de auth
3. **Cleanup job** para tokens expirados
4. **Rotate refresh tokens** - Emitir nuevo refresh token en cada refresh
5. **2FA** - Autenticación de dos factores
6. **OAuth2** - Login con Google, GitHub, etc.
7. **Audit log** - Registrar todos los logins/logouts

