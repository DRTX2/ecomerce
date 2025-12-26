# Análisis de Arquitectura Hexagonal - Adaptadores de Seguridad

## 🔍 PROBLEMAS ENCONTRADOS

### ❌ PROBLEMA CRÍTICO: RevokedTokenJpaAdapter en ubicación incorrecta

**Ubicación actual:** `adapters/in/security/RevokedTokenJpaAdapter.java`

**El problema:**
- `RevokedTokenJpaAdapter` es un **adaptador de SALIDA** (persiste datos en BD)
- Está ubicado en `adapters/in/` que es para adaptadores de **ENTRADA** (REST, listeners, etc.)
- Implementa `RevokedTokenPort` que está en `core/ports/in/rest/security/` (confuso)
- El puerto debería estar en `core/ports/out/security/`

**Flujo correcto en Hexagonal:**
```
JwtAuthFilter (in - entrada HTTP)
    ↓ (usa)
TokenRevocationPort (out - port de salida)
    ↓ (implementado por)
RevokedTokenJpaAdapter (out - adapter de salida)
    ↓ (usa)
RevokedTokenRepository (out - persistence)
    ↓ (maneja)
RevokedToken (entity JPA)
```

---

## 📊 ÁRBOL ACTUAL vs CORRECTO

### ACTUAL (INCORRECTO):
```
adapters/
├── in/security/
│   ├── ✅ AuthController (REST controller - CORRECTO)
│   ├── ✅ JwtAuthFilter (Filter - CORRECTO)
│   ├── ✅ JpaUserDetailsService (UserDetailsService - CORRECTO)
│   ├── ✅ SecurityUserDetails (Implementation - CORRECTO)
│   ├── ❌ RevokedTokenJpaAdapter (DEBERÍA ESTAR EN OUT)
│   ├── dto/
│   │   ├── AuthRequest
│   │   ├── AuthResponse
│   │   └── RegisterRequest
│   └── mappers/
│       ├── UserSecurityMapper
│       ├── AuthResponseMapper
│       └── SecurityUserMapper
│
└── out/persistence/security/
    ├── RevokedToken (entity)
    ├── RevokedTokenRepository
    └── ❌ FALTA: RevokedTokenPersistenceAdapter
```

### CORRECTO:
```
adapters/
├── in/security/
│   ├── AuthController (REST controller)
│   ├── JwtAuthFilter (Filter)
│   ├── JpaUserDetailsService (UserDetailsService)
│   ├── SecurityUserDetails (Implementation)
│   ├── dto/
│   │   ├── AuthRequest
│   │   ├── AuthResponse
│   │   └── RegisterRequest
│   └── mappers/
│       ├── UserSecurityMapper
│       ├── AuthResponseMapper
│       └── SecurityUserMapper
│
└── out/persistence/security/
    ├── RevokedToken (entity)
    ├── RevokedTokenRepository (repository interface)
    ├── RevokedTokenPersistenceAdapter (adapter de persistencia)
    └── JwtTokenProviderAdapter (adapter para TokenProvider)
```

---

## 🔧 PUERTOS (BIEN ESTRUCTURADOS)

### `core/ports/out/security/` ✅

```java
// Para manejo de tokens revocados
TokenRevocationPort
├── invalidate(String token)
├── isInvalidated(String token)
└── deleteExpiredTokens()

// Para generar y validar tokens
TokenProvider
├── generateToken(UserDetails)
├── extractUsername(String token)
└── isTokenValid(String token, UserDetails)

// Para autenticación
AuthenticationFacade
└── authenticate(String username, String password)

// Para contraseñas
PasswordService
├── encode(String rawPassword)
└── matches(String rawPassword, String encodedPassword)
```

### `core/ports/in/rest/security/` ⚠️ CONFUSO

```java
// PROBLEMA: Este puerto está en "in/rest" pero es implementado por 
// un adaptador de SALIDA (persistence)
RevokedTokenPort
├── save(String token)
├── exists(String token)
└── deleteExpiredTokens()
```

**Solución:** Renombrar y mover
- Renombrar: `RevokedTokenPort` → `RevokedTokenPort` (OK nombre)
- Mover: `core/ports/in/rest/security/` → `core/ports/out/security/`

---

## 📋 LISTA DE CAMBIOS REQUERIDOS

### 1️⃣ MOVER RevokedTokenJpaAdapter
```
DESDE: adapters/in/security/RevokedTokenJpaAdapter.java
HACIA: adapters/out/persistence/security/RevokedTokenPersistenceAdapter.java
```

### 2️⃣ MOVER/RENOMBRAR RevokedTokenPort
```
DESDE: core/ports/in/rest/security/RevokedTokenPort.java
HACIA: core/ports/out/security/RevokedTokenPort.java
```

### 3️⃣ ACTUALIZAR IMPORTS
- En `JwtAuthFilter.java`: cambiar import de `RevokedTokenPort`
- En `AuthService.java`: cambiar import de `RevokedTokenPort`
- En `RevokedTokenPersistenceAdapter.java`: actualizar package

### 4️⃣ AÑADIR NUEVO ADAPTER
- Crear `JwtTokenProviderAdapter` en `adapters/out/infrastructure/security/`
- Que implemente `TokenProvider`

### 5️⃣ MÉTODO extractExpirationFromToken()
**Ubicación actual:** Dentro de `RevokedTokenJpaAdapter` ❌
**Debería estar:** En `JwtService` (infrastructure)

---

## ✅ CLASES BIEN POSICIONADAS

### `adapters/in/security/` - CORRECTO
| Clase | Tipo | Estado |
|-------|------|--------|
| `AuthController` | REST Controller | ✅ BIEN |
| `JwtAuthFilter` | OncePerRequestFilter | ✅ BIEN |
| `JpaUserDetailsService` | UserDetailsService | ✅ BIEN |
| `SecurityUserDetails` | UserDetails impl | ✅ BIEN |
| `mappers/` | DTOs mappers | ✅ BIEN |
| `dto/` | Request/Response DTOs | ✅ BIEN |

### `adapters/out/persistence/security/` - PARCIALMENTE CORRECTO
| Clase | Tipo | Estado |
|-------|------|--------|
| `RevokedToken` | JPA Entity | ✅ BIEN |
| `RevokedTokenRepository` | JpaRepository | ✅ BIEN |
| `RevokedTokenJpaAdapter` | **DEBERÍA ESTAR AQUÍ** | ❌ MAL UBICADO |

---

## 🎯 RESUMEN DE ACCIONES

| # | Acción | Prioridad | Archivo |
|---|--------|-----------|---------|
| 1 | Crear `RevokedTokenPersistenceAdapter` en `out/persistence/security/` | 🔴 ALTA | RevokedTokenPersistenceAdapter.java |
| 2 | Mover `RevokedTokenJpaAdapter` | 🔴 ALTA | in/security/RevokedTokenJpaAdapter.java |
| 3 | Crear `TokenProviderJwtAdapter` en `out/infrastructure/security/` | 🟡 MEDIA | TokenProviderJwtAdapter.java |
| 4 | Mover `RevokedTokenPort` a `core/ports/out/security/` | 🔴 ALTA | in/rest/security/RevokedTokenPort.java |
| 5 | Actualizar imports en `JwtAuthFilter` | 🔴 ALTA | in/security/JwtAuthFilter.java |
| 6 | Extraer `extractExpirationFromToken()` a `JwtService` | 🟡 MEDIA | out/infrastructure/security/JwtService.java |

---

## 📐 ARQUITECTURA FINAL

```
Core (Núcleo)
├── model/ (Entidades de dominio)
├── ports/
│   ├── in/ (Puertos de entrada - para casos de uso)
│   │   └── rest/ (No debería haber security aquí)
│   └── out/ (Puertos de salida - para dependencias externas)
│       ├── security/
│       │   ├── TokenProvider
│       │   ├── TokenRevocationPort ← (RevokedTokenPort)
│       │   ├── AuthenticationFacade
│       │   └── PasswordService
│       └── user/
│
└── usecases/
    └── auth/
        └── AuthService

Adapters
├── in/ (Entrada)
│   ├── rest/
│   │   └── user/, product/, order/...
│   └── security/
│       ├── AuthController ✅
│       ├── JwtAuthFilter ✅
│       ├── JpaUserDetailsService ✅
│       └── SecurityUserDetails ✅
│
└── out/ (Salida)
    ├── persistence/
    │   ├── security/
    │   │   ├── RevokedToken (JPA)
    │   │   ├── RevokedTokenRepository
    │   │   └── RevokedTokenPersistenceAdapter ✅
    │   └── user/, product/, order/...
    │
    └── infrastructure/
        └── security/
            ├── JwtService
            ├── TokenProviderJwtAdapter ✅
            └── PasswordServiceImpl

Config
└── security/
    └── SecurityConfig
```

---

## 🚀 ESTADO FINAL ESPERADO

### Cuando termines:
- ✅ `RevokedTokenJpaAdapter` → `RevokedTokenPersistenceAdapter` (en out)
- ✅ `RevokedTokenPort` (en core/ports/out/)
- ✅ Todos los imports actualizados
- ✅ Arquitectura hexagonal limpia y clara
- ✅ Separación clara entre in/out


