# Fix para Error 403 en Login Endpoint

## Problema Detectado

El endpoint `/api/v1/auth/login` estaba retornando un error **403 Forbidden** en lugar de procesar las solicitudes de login.

## Causa Raíz

El problema se debía a un **desajuste entre las rutas configuradas en Spring Security y las rutas reales** de la aplicación:

1. **Context Path Global**: La aplicación tiene configurado en `application.yml`:
   ```yaml
   spring:
     web:
       servlet:
         context-path: /api/v1
   ```

2. **Controller Mapping**: El `AuthController` está mapeado a `/auth`:
   ```java
   @RequestMapping("/auth")
   public class AuthController {
       @PostMapping("/login")  // Ruta completa: /api/v1/auth/login
   }
   ```

3. **Problema en SecurityConfig**: Los `requestMatchers` estaban buscando rutas con `/api/v1/auth/login`, pero **Spring Security aplica los matchers después de remover el context-path**, por lo que solo ve `/auth/login`.

4. **Problema en JwtAuthFilter**: El filtro estaba usando `request.getRequestURI()` que incluye el context-path, cuando debería usar `request.getServletPath()` que ya lo tiene removido.

## Cambios Realizados

### 1. SecurityConfig.java

**Antes:**
```java
auth.requestMatchers(
    "/api/v1/auth/register",
    "/api/v1/auth/login",
    "/api/v1/auth/**"
).permitAll()
```

**Después:**
```java
auth.requestMatchers(
    "/auth/register",
    "/auth/login"
).permitAll()
```

### 2. JwtAuthFilter.java

**Antes:**
```java
protected boolean shouldNotFilter(HttpServletRequest request) {
    String path = request.getRequestURI();  // Incluye /api/v1
    boolean skip = path.equals("/api/v1/auth/login") || 
                   path.equals("/api/v1/auth/register");
    return skip;
}
```

**Después:**
```java
protected boolean shouldNotFilter(HttpServletRequest request) {
    String path = request.getServletPath();  // Sin context-path
    boolean skip = path.equals("/auth/login") || 
                   path.equals("/auth/register") ||
                   path.startsWith("/auth/");
    return skip;
}
```

## Cómo Funciona Ahora

1. **Petición HTTP**: `POST http://localhost:8080/api/v1/auth/login`
2. **Servlet Container**: Remueve el context-path `/api/v1`
3. **Spring Security**: Ve la ruta como `/auth/login`
4. **SecurityConfig**: Permite el acceso sin autenticación
5. **JwtAuthFilter**: `shouldNotFilter()` retorna `true`, saltando el filtro JWT
6. **Controller**: Procesa la petición normalmente

## Cómo Probar

### 1. Iniciar la Aplicación
```bash
cd /home/david/Desktop/personal/ecomerce-project/back
./gradlew bootRun
```

### 2. Realizar Petición de Login

**Con curl:**
```bash
curl -X POST http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "user@example.com",
    "password": "password123"
  }'
```

**Respuesta Esperada:**
```json
{
  "user": {
    "id": 1,
    "email": "user@example.com",
    "firstName": "John",
    "lastName": "Doe"
  },
  "tokens": {
    "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "refreshToken": "",
    "expiresIn": 86400000
  }
}
```

### 3. Verificar en Logs

Deberías ver en los logs:
```
JwtAuthFilter - Servlet Path: /auth/login
JwtAuthFilter - Request URI: /api/v1/auth/login
JwtAuthFilter - Should skip: true
AuthController - Login request received for: user@example.com
```

## Notas Importantes

1. **Context Path**: Cuando uses `context-path` en Spring Boot, recuerda que Spring Security ve las rutas SIN el context-path.

2. **getServletPath() vs getRequestURI()**:
   - `getRequestURI()`: Incluye context-path → `/api/v1/auth/login`
   - `getServletPath()`: Sin context-path → `/auth/login`

3. **CORS**: La configuración CORS ya está correcta y permite peticiones desde `http://localhost:3000`.

4. **Endpoints Públicos**: Solo `/auth/register` y `/auth/login` están públicos. El endpoint `/auth/logout` requiere autenticación (token JWT).

## Arquitectura Hexagonal

Esta solución mantiene la arquitectura hexagonal:
- **Adaptadores In (security)**: `JwtAuthFilter`, `AuthController`
- **Infraestructura (security)**: `SecurityConfig`, `AuthenticationFacadeAdapter`
- **Application (usecases)**: `AuthService`
- **Core (ports)**: `AuthenticationFacade`, `TokenProvider`

## Próximos Pasos

Si el login sigue fallando:

1. Verifica que el usuario existe en la base de datos
2. Verifica que la contraseña esté hasheada con BCrypt
3. Revisa los logs completos para ver el stack trace completo
4. Verifica que PostgreSQL esté corriendo en `localhost:5432`

