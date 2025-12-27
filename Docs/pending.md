# ✅ COMPLETADO - Refactoring de Nomenclatura de Casos de Uso

**Fecha de Completación:** 2025-12-19

Se completó el refactoring de las implementaciones de casos de uso para seguir el patrón `*Impl`.
Ver detalles en: [REFACTORING_SUMMARY.md](./REFACTORING_SUMMARY.md)

---

## 📝 Notas Anteriores sobre Auth y User

Perfecto, ya veo el panorama. Lo que quieres hacer es separar el **registro de usuarios** del módulo general de `User` y ponerlo en `Auth`, manteniendo la arquitectura hexagonal. El problema principal que veo es **la confusión de mappers** y referencias entre `RegisterRequest`, `User`, `UserRestMapper` y `UserSecurityMapper`.

Te hago un resumen de los pasos y ajustes necesarios para que tu `register` funcione limpio:

---

### 1️⃣ DTO y Mapper

Actualmente en `AuthController` haces:

```java
User user = userRestMapper.toDomain(request);
```

Pero `userRestMapper` probablemente espera un `UserRequest` (no un `RegisterRequest`). Lo correcto es usar tu mapper de seguridad para auth:

```java
User user = userSecurityMapper.registerRequestToDomain(request);
```

Y para la respuesta:

```java
AuthResponse response = userSecurityMapper.entityToResponse(authService.register(user));
```

Esto evita que mezcles `UserRestMapper` con `AuthController`.

---

### 2️⃣ Servicio

Tu `AuthService.register` ya está correcto:

```java
public User register(User user){
    String encodedPassword = passwordService.encode(user.getPassword());
    user.setPassword(encodedPassword);
    return repository.save(user);
}
```

No necesitas cambiar el repositorio, porque `UserRepositoryPort` ya tiene `save(User)`.

---

### 3️⃣ Repositorio

`UserRepositoryPortAdapter` ya maneja la validación de email único, así que no requiere cambio.

---

### 4️⃣ Controlador final

Tu `AuthController.register` debería quedar así:

```java
@PostMapping("/register")
public ResponseEntity<AuthResponse> register(@RequestBody RegisterRequest request){
    User user = userSecurityMapper.registerRequestToDomain(request);
    User createdUser = authService.register(user);
    AuthResponse response = userSecurityMapper.entityToResponse(createdUser);
    return ResponseEntity.status(HttpStatus.CREATED).body(response);
}
```

* Usas `UserSecurityMapper` en lugar de `UserRestMapper`.
* Devuelves `201 Created` en lugar de `200 OK` (más correcto para creación).

---

### 5️⃣ Bonus: separación de User vs UserDetails

Tu `SecurityUserDetails` está bien y no se cruza con `User` del core.
Esto asegura que no dependes de Spring Security dentro del dominio.

---

Si quieres, puedo hacerte un **diagrama de flujo rápido** de cómo pasa `RegisterRequest → User → AuthService → UserRepository → AuthResponse` en esta nueva arquitectura. Así ves claramente cómo queda todo separado.

¿Quieres que haga eso?

---


Perfecto, aquí tienes un diagrama de flujo conceptual de cómo pasa un registro de usuario en tu arquitectura hexagonal separando **Auth** de **User**:

```
[Cliente] 
   |
   | POST /auth/register
   v
[AuthController] 
   - Recibe RegisterRequest
   - Usa UserSecurityMapper.registerRequestToDomain
   v
[UserSecurityMapper]
   - Convierte RegisterRequest → User (dominio)
   v
[AuthService.register(User)]
   - Codifica password
   - Llama a UserRepositoryPort.save(User)
   v
[UserRepositoryPortAdapter] 
   - Convierte User → UserEntity
   - Valida email único
   - Persiste en BD
   - Convierte UserEntity → User
   v
[AuthService] 
   - Recibe User creado
   v
[UserSecurityMapper.entityToResponse(User)]
   - Convierte User → AuthResponse
   v
[AuthController]
   - Retorna ResponseEntity<AuthResponse> con status 201
   v
[Cliente] 
   - Recibe datos del usuario registrado
```

💡 Notas importantes:

* Todo el dominio (`User`) permanece independiente de Spring Security.
* `SecurityUserDetails` solo se usa cuando se autentica un usuario, no para registro.
* Los mappers (`UserSecurityMapper`) actúan de frontera entre DTOs de auth y el dominio.
* Esto mantiene la **separación de módulos**: `auth` maneja registro/login, `user` solo maneja CRUD general.

Si quieres, puedo hacer también un **mini esquema visual tipo “caja y flechas”** que puedas mostrar como diagrama de arquitectura. Esto ayuda mucho a explicar a otros desarrolladores. ¿Quieres que haga eso también?

