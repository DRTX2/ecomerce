# 🧪 Guía de Pruebas - Sistema de Autenticación

## Pre-requisitos

1. **Base de Datos**: PostgreSQL corriendo
2. **Migraciones**: Ejecutadas (incluye V6__Create_refresh_tokens_table.sql)
3. **Variables de Entorno**: JWT_SECRET_KEY configurada
4. **Servidor**: Spring Boot corriendo en puerto 8080

---

## 🚀 Paso 1: Registro de Usuario

### Request
```bash
curl -X POST http://localhost:8080/api/v1/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Test User",
    "email": "test@example.com",
    "password": "SecurePass123!",
    "role": "USER"
  }'
```

### Response Esperado (200 OK)
```json
{
  "user": {
    "id": 1,
    "name": "Test User",
    "email": "test@example.com",
    "role": "USER"
  },
  "tokens": {
    "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "expiresInMs": 86400000
  }
}
```

### ✅ Verificaciones
- [ ] Status code: 200
- [ ] User object presente con id, name, email, role
- [ ] accessToken y refreshToken presentes
- [ ] expiresInMs = 86400000 (24 horas)

---

## 🔐 Paso 2: Login

### Request
```bash
curl -X POST http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "test@example.com",
    "password": "SecurePass123!"
  }'
```

### Response Esperado (200 OK)
```json
{
  "user": {
    "id": 1,
    "name": "Test User",
    "email": "test@example.com",
    "role": "USER"
  },
  "tokens": {
    "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "expiresInMs": 86400000
  }
}
```

### ✅ Verificaciones
- [ ] Status code: 200
- [ ] Nuevos tokens generados (diferentes al registro)
- [ ] Refresh token anterior revocado en BD

### ❌ Caso de Error (Credenciales Inválidas)
```bash
curl -X POST http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "test@example.com",
    "password": "WrongPassword"
  }'
```

**Response**: 401 Unauthorized

---

## 🛡️ Paso 3: Request Protegido con Access Token

### Request
```bash
ACCESS_TOKEN="<tu-access-token-aqui>"

curl -X GET http://localhost:8080/api/v1/products \
  -H "Authorization: Bearer $ACCESS_TOKEN"
```

### Response Esperado (200 OK)
```json
[
  {
    "id": 1,
    "name": "Product 1",
    "price": 99.99,
    ...
  }
]
```

### ✅ Verificaciones
- [ ] Status code: 200
- [ ] Productos retornados
- [ ] JwtAuthFilter procesó el token correctamente

### ❌ Sin Token
```bash
curl -X GET http://localhost:8080/api/v1/products
```

**Response**: 403 Forbidden (si el endpoint requiere auth)

### ❌ Token Inválido
```bash
curl -X GET http://localhost:8080/api/v1/products \
  -H "Authorization: Bearer invalid-token-123"
```

**Response**: 401 Unauthorized

---

## 🔄 Paso 4: Refresh Access Token

### Escenario
El access token expira después de 24h. El cliente usa el refresh token para obtener un nuevo access token sin re-autenticarse.

### Request
```bash
REFRESH_TOKEN="<tu-refresh-token-aqui>"

curl -X POST http://localhost:8080/api/v1/auth/refresh \
  -H "Content-Type: application/json" \
  -d "{
    \"refreshToken\": \"$REFRESH_TOKEN\"
  }"
```

### Response Esperado (200 OK)
```json
{
  "user": {
    "id": 1,
    "name": "Test User",
    "email": "test@example.com",
    "role": "USER"
  },
  "tokens": {
    "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",  // NUEVO
    "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",  // MISMO
    "expiresInMs": 86400000
  }
}
```

### ✅ Verificaciones
- [ ] Nuevo access token generado
- [ ] Refresh token es el mismo
- [ ] User data presente

### ❌ Refresh Token Expirado
```bash
# Después de 7 días
curl -X POST http://localhost:8080/api/v1/auth/refresh \
  -H "Content-Type: application/json" \
  -d "{
    \"refreshToken\": \"$REFRESH_TOKEN\"
  }"
```

**Response**: 400 Bad Request
```json
{
  "error": "Refresh token is invalid or expired"
}
```

### ❌ Refresh Token Inválido
```bash
curl -X POST http://localhost:8080/api/v1/auth/refresh \
  -H "Content-Type: application/json" \
  -d '{
    "refreshToken": "invalid-token"
  }'
```

**Response**: 404 Not Found
```json
{
  "error": "Refresh token not found"
}
```

---

## 🚪 Paso 5: Logout

### Request
```bash
ACCESS_TOKEN="<tu-access-token-aqui>"

curl -X POST http://localhost:8080/api/v1/auth/logout \
  -H "Authorization: Bearer $ACCESS_TOKEN"
```

### Response Esperado (204 No Content)
- Sin body
- Status: 204

### ✅ Verificaciones
- [ ] Access token agregado a `revoked_tokens`
- [ ] Refresh token marcado como `revoked=true` en `refresh_tokens`
- [ ] Intentar usar access token después → 401 Unauthorized

### Verificar Revocación
```bash
# Intentar usar el token después de logout
curl -X GET http://localhost:8080/api/v1/products \
  -H "Authorization: Bearer $ACCESS_TOKEN"
```

**Response**: 401 Unauthorized

---

## 🗄️ Paso 6: Verificar Base de Datos

### Verificar Refresh Tokens
```sql
SELECT * FROM refresh_tokens WHERE user_email = 'test@example.com';
```

**Esperado**:
- 1 registro por usuario (el más reciente)
- `revoked = false` si usuario está activo
- `revoked = true` si hizo logout
- `expiry_date` 7 días desde creación

### Verificar Tokens Revocados
```sql
SELECT * FROM revoked_tokens ORDER BY revoked_at DESC LIMIT 5;
```

**Esperado**:
- Tokens de usuarios que hicieron logout
- Timestamp de revocación

---

## 🔍 Debugging - Logs Útiles

### Habilitar Logs de Seguridad
```yaml
# application.yml
logging:
  level:
    com.drtx.ecomerce.amazon.infrastructure.security: DEBUG
    com.drtx.ecomerce.amazon.adapters.in.security: DEBUG
    org.springframework.security: DEBUG
```

### Logs Esperados

#### En Login:
```
JwtAuthFilter - Request URI: /api/v1/auth/login
JwtAuthFilter - Should skip: true
AuthController - Login request received for: test@example.com
```

#### En Request Protegido:
```
JwtAuthFilter - Request URI: /api/v1/products
JwtAuthFilter - Should skip: false
JwtAuthFilter - Token extracted from header
JwtAuthFilter - Username extracted: test@example.com
JwtAuthFilter - Token is valid, setting authentication
```

#### En Logout:
```
AuthController - Logout request for user: test@example.com
TokenRevocationService - Token invalidated: eyJ...
RefreshTokenService - Refresh token revoked for: test@example.com
```

---

## 🧪 Tests Automatizados (Opcional)

### Test de Integración con REST Assured

```java
@Test
void shouldLoginAndGetAccessToken() {
    // Login
    var response = given()
        .contentType(ContentType.JSON)
        .body("""
            {
                "email": "test@example.com",
                "password": "SecurePass123!"
            }
            """)
        .when()
        .post("/api/v1/auth/login")
        .then()
        .statusCode(200)
        .extract()
        .as(AuthResponse.class);
    
    assertNotNull(response.tokens().accessToken());
    assertNotNull(response.tokens().refreshToken());
    
    // Use access token
    given()
        .header("Authorization", "Bearer " + response.tokens().accessToken())
        .when()
        .get("/api/v1/products")
        .then()
        .statusCode(200);
}

@Test
void shouldRefreshAccessToken() {
    // Login primero
    var loginResponse = login();
    
    // Refresh
    var refreshResponse = given()
        .contentType(ContentType.JSON)
        .body("""
            {
                "refreshToken": "%s"
            }
            """.formatted(loginResponse.tokens().refreshToken()))
        .when()
        .post("/api/v1/auth/refresh")
        .then()
        .statusCode(200)
        .extract()
        .as(AuthResponse.class);
    
    // Nuevo access token debe ser diferente
    assertNotEquals(
        loginResponse.tokens().accessToken(),
        refreshResponse.tokens().accessToken()
    );
    
    // Refresh token debe ser el mismo
    assertEquals(
        loginResponse.tokens().refreshToken(),
        refreshResponse.tokens().refreshToken()
    );
}

@Test
void shouldRevokeTokensOnLogout() {
    var loginResponse = login();
    
    // Logout
    given()
        .header("Authorization", "Bearer " + loginResponse.tokens().accessToken())
        .when()
        .post("/api/v1/auth/logout")
        .then()
        .statusCode(204);
    
    // Access token revocado
    given()
        .header("Authorization", "Bearer " + loginResponse.tokens().accessToken())
        .when()
        .get("/api/v1/products")
        .then()
        .statusCode(401);
    
    // Refresh token revocado
    given()
        .contentType(ContentType.JSON)
        .body("""
            {
                "refreshToken": "%s"
            }
            """.formatted(loginResponse.tokens().refreshToken()))
        .when()
        .post("/api/v1/auth/refresh")
        .then()
        .statusCode(400);
}
```

---

## 📊 Checklist de Pruebas Completo

### Funcionalidad Básica
- [ ] Registro de usuario exitoso
- [ ] Login exitoso
- [ ] Access token válido permite acceso
- [ ] Refresh token genera nuevo access token
- [ ] Logout revoca tokens

### Seguridad
- [ ] Login con contraseña incorrecta falla (401)
- [ ] Request sin token falla (403)
- [ ] Request con token inválido falla (401)
- [ ] Request con token revocado falla (401)
- [ ] Refresh token expirado falla (400)
- [ ] Refresh token revocado falla (400)

### Edge Cases
- [ ] Múltiples logins revoca refresh tokens anteriores
- [ ] Logout sin access token falla (403)
- [ ] Refresh con token de otro usuario falla
- [ ] Password que contiene email falla en registro

### Base de Datos
- [ ] Refresh token se guarda en BD
- [ ] Refresh token se marca como revoked en logout
- [ ] Token revocado se guarda en revoked_tokens
- [ ] Solo 1 refresh token activo por usuario

### Performance
- [ ] Login responde en < 500ms
- [ ] Refresh responde en < 200ms
- [ ] Validación de token en < 50ms

---

## 🎯 Próximos Tests

### 1. Load Testing
```bash
# Apache Bench
ab -n 1000 -c 10 -H "Authorization: Bearer $TOKEN" \
   http://localhost:8080/api/v1/products
```

### 2. Security Testing
- [ ] SQL Injection en login
- [ ] XSS en campos de texto
- [ ] CSRF attacks
- [ ] Brute force protection

### 3. Integration Testing
- [ ] Con PostgreSQL real
- [ ] Con Redis para tokens revocados
- [ ] Con múltiples instancias (clustering)

---

**Última actualización**: 2025-12-27  
**Estado**: ✅ Ready para testing

