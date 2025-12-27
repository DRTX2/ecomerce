# ÁRBOL DE ESTRUCTURA DEL PROYECTO - VISUAL Y PRÁCTICO

## 🌳 ESTRUCTURA VISUAL COMPLETA

```
com/drtx/ecomerce/amazon/
│
├── 🎯 CORE (Dominio Puro - Sin dependencias externas)
│   ├── model/
│   │   ├── discount/
│   │   │   └── Discount.java          # Entity Descuento
│   │   │
│   │   ├── exceptions/
│   │   │   └── EntityNotFoundException # Excepción de dominio
│   │   │
│   │   ├── issues/
│   │   │   ├── Appeal.java            # Apelación
│   │   │   ├── Incidence.java         # Incidencia reportada
│   │   │   └── Report.java            # Reporte
│   │   │
│   │   ├── notifications/
│   │   │   └── Notification.java      # Notificación
│   │   │
│   │   ├── order/ ⭐ MÓDULO CLAVE
│   │   │   ├── Cart.java              # Carrito temporal
│   │   │   ├── CartItem.java          # Item en carrito
│   │   │   ├── Order.java             # Orden permanente
│   │   │   ├── OrderItem.java         # Item en orden
│   │   │   └── OrderState.java        # Enum: PENDING, PROCESSING, SHIPPED, DELIVERED, CANCELLED
│   │   │
│   │   ├── payment/
│   │   │   ├── Payment.java           # Pago
│   │   │   ├── PaymentMethod.java     # Enum: CARD, BANK_TRANSFER, PAYPAL, etc
│   │   │   └── PaymentStatus.java     # Enum: PENDING, APPROVED, REJECTED, REFUNDED
│   │   │
│   │   ├── product/
│   │   │   ├── Category.java          # Categoría de producto
│   │   │   ├── Inventory.java         # Inventario/Stock
│   │   │   ├── Product.java           # Producto
│   │   │   └── Review.java            # Reseña de producto
│   │   │
│   │   ├── returns/
│   │   │   ├── Return.java            # Devolución
│   │   │   └── ReturnStatus.java      # Enum: REQUESTED, APPROVED, REJECTED, COMPLETED
│   │   │
│   │   ├── security/
│   │   │   └── Token.java             # Token (JWT)
│   │   │
│   │   ├── shipping/
│   │   │   ├── Shipping.java          # Envío
│   │   │   └── ShippingStatus.java    # Enum: PENDING, PROCESSING, SHIPPED, DELIVERED, RETURNED
│   │   │
│   │   ├── supplier/
│   │   │   └── Supplier.java          # Proveedor
│   │   │
│   │   └── user/
│   │       ├── User.java              # Usuario
│   │       ├── Favorite.java          # Favorito (relación user-product)
│   │       └── UserRole.java          # Enum: ADMIN, SELLER, CUSTOMER
│   │
│   └── ports/ (Abstracciones/Contratos)
│       ├── in/
│       │   ├── rest/
│       │   │   ├── CartUseCasePort.java
│       │   │   ├── CategoryUseCasePort.java
│       │   │   ├── FavoriteUseCasePort.java
│       │   │   ├── ProductUseCasePort.java
│       │   │   ├── OrderUseCasePort.java
│       │   │   ├── UserUseCasePort.java
│       │   │   ├── AppealUseCasePort.java
│       │   │   ├── IncidenceUseCasePort.java
│       │   │   └── ... más ports
│       │   └── rest/security/
│       │       └── AuthUseCasePort.java
│       │
│       └── out/
│           ├── persistence/
│           │   ├── CartRepositoryPort.java
│           │   ├── OrderRepositoryPort.java
│           │   ├── ProductRepositoryPort.java
│           │   └── ... más puertos
│           └── security/
│               └── AuthenticationFacade.java
│
│
├── 📦 APPLICATION (Orquestación de Casos de Uso)
│   └── usecases/
│       ├── CartUseCaseImpl.java        # ✅ Implementa CartUseCasePort
│       ├── OrderUseCaseImpl.java       # ✅ Implementa OrderUseCasePort
│       ├── CategoryUseCaseImpl.java
│       ├── FavoriteUseCaseImpl.java
│       ├── ProductUseCaseImpl.java
│       ├── UserUseCaseImpl.java
│       ├── AppealUseCaseImpl.java
│       ├── IncidenceUseCaseImpl.java
│       └── auth/
│           └── AuthUseCaseImpl.java
│
│
├── 🔌 ADAPTERS (Pluggable - Intercambiables)
│   ├── in/ (ENTRADA - Presenta al dominio al mundo exterior)
│   │   ├── rest/ (HTTP/REST)
│   │   │   ├── appeal/
│   │   │   │   ├── AppealController.java
│   │   │   │   │   └── Métodos: POST /appeals (crear)
│   │   │   │   │                GET /appeals/{id} (obtener)
│   │   │   │   │                PUT /appeals/{id}/resolve (resolver)
│   │   │   │   ├── dto/
│   │   │   │   │   ├── AppealRequest.java
│   │   │   │   │   ├── AppealResponse.java
│   │   │   │   │   └── ResolveAppealRequest.java
│   │   │   │   └── mappers/
│   │   │   │       └── AppealRestMapper.java
│   │   │   │
│   │   │   ├── cart/ ✅ CORREGIDO
│   │   │   │   ├── CartController.java
│   │   │   │   │   ├── GET /api/carts?userId=X (obtener carrito del usuario)
│   │   │   │   │   ├── POST /api/carts (crear carrito)
│   │   │   │   │   ├── GET /api/carts/{id} (obtener por ID)
│   │   │   │   │   ├── PUT /api/carts/{id} (actualizar)
│   │   │   │   │   └── DELETE /api/carts/{id} (eliminar)
│   │   │   │   ├── dtos/
│   │   │   │   │   ├── CartRequest.java
│   │   │   │   │   └── CartResponse.java
│   │   │   │   └── mappers/
│   │   │   │       └── CartRestMapper.java (Domain ↔ DTO)
│   │   │   │
│   │   │   ├── category/
│   │   │   │   ├── CategoryController.java
│   │   │   │   ├── dto/
│   │   │   │   └── mappers/
│   │   │   │
│   │   │   ├── favorite/
│   │   │   │   ├── FavoriteController.java
│   │   │   │   │   └── Usa SecurityContextHolder para obtener usuario
│   │   │   │   ├── dto/
│   │   │   │   └── mappers/
│   │   │   │
│   │   │   ├── incidence/
│   │   │   │   ├── IncidenceController.java
│   │   │   │   ├── dto/
│   │   │   │   └── mappers/
│   │   │   │
│   │   │   ├── order/
│   │   │   │   ├── OrderController.java
│   │   │   │   │   ├── GET /api/orders (todas)
│   │   │   │   │   ├── POST /api/orders (crear)
│   │   │   │   │   ├── GET /api/orders/{id} (obtener por ID)
│   │   │   │   │   ├── PUT /api/orders/{id} (actualizar estado)
│   │   │   │   │   └── DELETE /api/orders/{id} (cancelar)
│   │   │   │   ├── dto/
│   │   │   │   │   ├── OrderRequest.java
│   │   │   │   │   ├── OrderResponse.java
│   │   │   │   │   └── OrderItemDto.java
│   │   │   │   └── mappers/
│   │   │   │       └── OrderRestMapper.java
│   │   │   │
│   │   │   ├── product/
│   │   │   │   ├── ProductController.java
│   │   │   │   ├── dto/
│   │   │   │   └── mappers/
│   │   │   │
│   │   │   └── user/
│   │   │       ├── UserController.java
│   │   │       ├── dto/
│   │   │       └── mappers/
│   │   │
│   │   ├── security/ ✅ ENTRADA DE SEGURIDAD
│   │   │   ├── AuthController.java
│   │   │   │   ├── POST /auth/register (registrarse)
│   │   │   │   ├── POST /auth/login (iniciar sesión)
│   │   │   │   └── POST /auth/logout (cerrar sesión)
│   │   │   ├── JwtAuthFilter.java (Interceptor de requests)
│   │   │   ├── JpaUserDetailsService.java (Carga usuarios de BD)
│   │   │   ├── SecurityUserDetails.java (UserDetails de Spring)
│   │   │   ├── dto/
│   │   │   │   ├── AuthRequest.java (email + password)
│   │   │   │   ├── AuthResponse.java (token + user)
│   │   │   │   └── RegisterRequest.java (datos para registro)
│   │   │   └── mappers/
│   │   │       ├── AuthResponseMapper.java
│   │   │       ├── SecurityUserMapper.java
│   │   │       └── UserSecurityMapper.java
│   │   │
│   │   └── graphql/
│   │       └── (GraphQL schemas y resolvers si usa GraphQL)
│   │
│   └── out/ (SALIDA - Infraestructura)
│       └── persistence/
│           ├── appeal/
│           │   ├── AppealEntity.java (Mapeo JPA @Entity)
│           │   ├── AppealPersistenceMapper.java (Domain ↔ Entity)
│           │   ├── AppealPersistenceRepository.java (JPA Repository)
│           │   └── AppealRepositoryAdapter.java (Implementa puerto OUT)
│           │
│           ├── cart/
│           │   ├── CartEntity.java
│           │   ├── CartItemEntity.java
│           │   ├── CartPersistenceMapper.java
│           │   ├── CartPersistenceRepository.java
│           │   └── CartRepositoryAdapter.java
│           │
│           ├── category/
│           ├── discount/
│           ├── favorite/
│           ├── incidence/
│           ├── inventory/
│           ├── notification/
│           ├── order/
│           │   ├── OrderEntity.java
│           │   ├── OrderItemEntity.java
│           │   ├── OrderPersistenceMapper.java
│           │   ├── OrderPersistenceRepository.java
│           │   └── OrderRepositoryAdapter.java
│           ├── payment/
│           ├── product/
│           ├── returns/
│           ├── review/
│           ├── security/
│           │   ├── RevokedToken.java (Token revocado/blacklist)
│           │   ├── RevokedTokenRepository.java
│           │   └── RevokedTokenPersistenceAdapter.java
│           ├── shipping/
│           ├── supplier/
│           └── user/
│               ├── UserEntity.java
│               ├── UserPersistenceMapper.java
│               ├── UserPersistenceRepository.java
│               └── UserRepositoryPortAdapter.java
│
│
└── ⚙️ INFRASTRUCTURE (Configuración y Servicios Técnicos)
    ├── exceptions/
    │   ├── GlobalExceptionHandler.java
    │   │   ├── @ExceptionHandler(EntityNotFoundException.class)
    │   │   └── @ExceptionHandler(jakarta.persistence.EntityNotFoundException.class)
    │   └── user/
    │       └── UserNotFoundException.java
    │
    └── security/ ✅ CONFIGURACIÓN Y SERVICIOS DE SEGURIDAD
        ├── SecurityConfig.java
        │   └── Configura Spring Security (filtros, autenticación, etc)
        ├── AuthenticationFacadeAdapter.java
        │   └── Implementa puerto AuthenticationFacade
        ├── BcryptPasswordService.java
        │   └── Encriptación de contraseñas
        ├── JwtService.java
        │   ├── Generar JWT
        │   ├── Validar JWT
        │   └── Extraer claims
        └── TokenRevocationService.java
            └── Revocar tokens (logout)
```

---

## 🔄 FLUJO DE UNA SOLICITUD (Cart)

### Obtener carrito del usuario

```
┌─────────────────────────────────────────────────────────────┐
│ 1️⃣ HTTP Request                                             │
│ GET /api/carts?userId=123 HTTP/1.1                          │
│ Authorization: Bearer eyJhbGciOiJIUzI1NiIs...               │
└──────────────────────┬──────────────────────────────────────┘
                       ↓
┌──────────────────────────────────────────────────────────────┐
│ 2️⃣ JwtAuthFilter (adapters/in/security/)                    │
│    - Extrae token del header Authorization                  │
│    - Valida firma del JWT                                   │
│    - Carga usuario en SecurityContext                       │
└──────────────────────┬──────────────────────────────────────┘
                       ↓
┌──────────────────────────────────────────────────────────────┐
│ 3️⃣ CartController.getAllCarts()                             │
│    - Recibe @RequestParam Long userId                       │
│    - Delega a CartUseCasePort (puerto IN)                   │
└──────────────────────┬──────────────────────────────────────┘
                       ↓
┌──────────────────────────────────────────────────────────────┐
│ 4️⃣ CartUseCaseImpl (application/usecases/)                   │
│    - Orquesta la lógica de negocio                          │
│    - Llama a CartRepositoryPort (puerto OUT)                │
└──────────────────────┬──────────────────────────────────────┘
                       ↓
┌──────────────────────────────────────────────────────────────┐
│ 5️⃣ CartRepositoryAdapter (adapters/out/persistence/)        │
│    - Implementa el puerto OUT                               │
│    - Llama a CartPersistenceRepository (JPA)                │
└──────────────────────┬──────────────────────────────────────┘
                       ↓
┌──────────────────────────────────────────────────────────────┐
│ 6️⃣ CartPersistenceRepository                                │
│    - Ejecuta query JPA: findAll(userId)                     │
│    - Trae CartEntity desde BD                               │
└──────────────────────┬──────────────────────────────────────┘
                       ↓
┌──────────────────────────────────────────────────────────────┐
│ 7️⃣ CartPersistenceMapper                                    │
│    - Convierte CartEntity → Cart (Domain)                   │
└──────────────────────┬──────────────────────────────────────┘
                       ↓
┌──────────────────────────────────────────────────────────────┐
│ 8️⃣ CartRestMapper                                           │
│    - Convierte Cart (Domain) → CartResponse (DTO)           │
└──────────────────────┬──────────────────────────────────────┘
                       ↓
┌──────────────────────────────────────────────────────────────┐
│ 9️⃣ HTTP Response                                            │
│ {                                                            │
│   "id": 123,                                                │
│   "userId": 123,                                            │
│   "items": [                                                │
│     { "id": 1, "productId": 456, "quantity": 2 }           │
│   ],                                                        │
│   "createdAt": "2024-12-26T10:30:00"                        │
│ }                                                            │
└──────────────────────────────────────────────────────────────┘
```

---

## 📊 COMPARATIVA: CART vs ORDER

```
┌─────────────────────────┬──────────────────┬──────────────────┐
│ Aspecto                 │ CART             │ ORDER            │
├─────────────────────────┼──────────────────┼──────────────────┤
│ Duración                │ Temporal          │ Permanente       │
│ Usuario                 │ user_id (FK)     │ user_id (FK)     │
│ Items                   │ CartItem[]       │ OrderItem[]      │
│ Total                   │ Calculado         │ Fijo BigDecimal  │
│ Estado                  │ N/A               │ OrderState enum  │
│ Precio de Items         │ Product.price     │ priceAtPurchase  │
│ Modificable             │ Sí (user)         │ No (solo estado) │
│ Timestamps              │ (generalmente)    │ createdAt,       │
│                         │                   │ deliveredAt      │
│ Descuentos              │ N/A               │ List<Discount>   │
│ Caso de uso             │ Seleccionar       │ Historial de     │
│                         │ productos         │ compra (auditoría)│
└─────────────────────────┴──────────────────┴──────────────────┘
```

### Conversión Cart → Order

```
CART                               ORDER
┌──────────────────────┐           ┌──────────────────────┐
│ id: 100              │           │ id: 50               │
│ user: User(123)      │──────────→│ user: User(123)      │
│ items: [             │           │ items: [             │
│   CartItem {         │           │   OrderItem {        │
│     product: P1      │────────┐  │     product: P1      │
│     quantity: 2      │        │  │     quantity: 2      │
│   }                  │        │  │     priceAtPurchase: │
│ ]                    │        │  │       100.00 (FIJADO)│
│                      │        │  │   }                  │
│                      │        │  │ ]                    │
│                      │        │  │ total: 200.00        │
│                      │        │  │ orderState: PENDING  │
│                      │        │  │ createdAt: NOW       │
│                      │        │  │ appliedDiscounts: [] │
│                      │        │  └──────────────────────┘
│                      │        │
│                      │        └─ Historial fijo
│                      │           para auditoría
└──────────────────────┘
```

---

## 🔐 FLUJO DE SEGURIDAD

### Registro y Login

```
┌──────────────────────────────────────────────────────┐
│ 1. Usuario hace POST /auth/register                  │
│    {                                                 │
│      "email": "user@example.com",                    │
│      "password": "secret123"                         │
│    }                                                 │
└──────────────────┬───────────────────────────────────┘
                   ↓
┌──────────────────────────────────────────────────────┐
│ 2. AuthController (adapters/in/security/)            │
│    - Delega a AuthUseCasePort                        │
└──────────────────┬───────────────────────────────────┘
                   ↓
┌──────────────────────────────────────────────────────┐
│ 3. AuthUseCaseImpl (application/usecases/auth/)       │
│    - Valida datos                                    │
│    - Crea Usuario                                    │
│    - Encripta contraseña                             │
└──────────────────┬───────────────────────────────────┘
                   ↓
┌──────────────────────────────────────────────────────┐
│ 4. BcryptPasswordService (infrastructure/security/)  │
│    - Usa BCrypt para encriptar                       │
└──────────────────┬───────────────────────────────────┘
                   ↓
┌──────────────────────────────────────────────────────┐
│ 5. UserRepositoryAdapter (adapters/out/persistence/)│
│    - Guarda UserEntity en BD                         │
└──────────────────┬───────────────────────────────────┘
                   ↓
                   ✅ Usuario registrado

═══════════════════════════════════════════════════════

┌──────────────────────────────────────────────────────┐
│ 6. Usuario hace POST /auth/login                     │
│    {                                                 │
│      "email": "user@example.com",                    │
│      "password": "secret123"                         │
│    }                                                 │
└──────────────────┬───────────────────────────────────┘
                   ↓
┌──────────────────────────────────────────────────────┐
│ 7. AuthController                                    │
│    - Delega a AuthUseCasePort                        │
└──────────────────┬───────────────────────────────────┘
                   ↓
┌──────────────────────────────────────────────────────┐
│ 8. AuthenticationFacadeAdapter                       │
│    - Crea UsernamePasswordAuthenticationToken        │
│    - Llama authenticationManager.authenticate()      │
└──────────────────┬───────────────────────────────────┘
                   ↓
┌──────────────────────────────────────────────────────┐
│ 9. JpaUserDetailsService                             │
│    - Carga UserDetails por email                     │
│    - Spring verifica contraseña (BCrypt)             │
└──────────────────┬───────────────────────────────────┘
                   ↓
         ✅ Autenticación exitosa
                   ↓
┌──────────────────────────────────────────────────────┐
│ 10. JwtService (infrastructure/security/)            │
│     - Genera JWT token                               │
│     - Signa con secret key                           │
│     - Establece expiration                           │
└──────────────────┬───────────────────────────────────┘
                   ↓
┌──────────────────────────────────────────────────────┐
│ 11. AuthResponse                                     │
│     {                                                │
│       "token": "eyJhbGciOiJIUzI1NiIs...",            │
│       "user": { ... }                                │
│     }                                                │
└──────────────────────────────────────────────────────┘

═══════════════════════════════════════════════════════

┌──────────────────────────────────────────────────────┐
│ 12. Próximas requests incluyen token                 │
│     Authorization: Bearer eyJhbGciOiJIUzI1NiIs...    │
└──────────────────┬───────────────────────────────────┘
                   ↓
┌──────────────────────────────────────────────────────┐
│ 13. JwtAuthFilter intercepta request                 │
│     - Extrae token del header                        │
│     - Valida firma                                   │
│     - Carga usuario en SecurityContext               │
└──────────────────┬───────────────────────────────────┘
                   ↓
        ✅ Request autorizado, se procesa
```

---

## 📋 PUNTOS CRÍTICOS DE IMPLEMENTACIÓN

### ✅ Lo que está bien

1. **Separación clara de capas**
   - Core (modelo) completamente desacoplado
   - Adapters verdaderamente intercambiables
   - Puertos bien definidos

2. **Manejo de seguridad**
   - JWT implementado correctamente
   - BCrypt para contraseñas
   - Token revocation

3. **DTOs y Mappers**
   - Separación DTO ↔ Domain
   - Entity ↔ Domain
   - Validaciones en DTOs

4. **Excepciones**
   - GlobalExceptionHandler
   - Manejo de jakarta.persistence.EntityNotFoundException

### ⚠️ Puntos a vigilar

1. **CartController corregido** ✅
   - Ahora usa userId como parámetro dinámico

2. **SecurityContextHolder**
   - Used en FavoriteController y AppealController
   - Extrae email del usuario autenticado
   - Buen patrón para contexto de usuario

3. **Validaciones**
   - @jakarta.validation.Valid en DTOs
   - Garantiza entrada válida

---

## 🎓 CONCLUSIÓN

Tu proyecto implementa correctamente:
- ✅ **Arquitectura Hexagonal** - Dominio aislado, adapters pluggables
- ✅ **DDD** - Modelos ricos, lenguaje ubicuo
- ✅ **Seguridad** - JWT, BCrypt, token revocation
- ✅ **Separación de responsabilidades** - Cada clase tiene un propósito claro
- ✅ **Abstracción de persistencia** - Cambiar BD sin tocar dominio

El proyecto está **listo para producción** con algunas mejoras menores que ya han sido aplicadas.

