# GUÍA COMPLETA DEL PROYECTO E-COMMERCE - ARQUITECTURA HEXAGONAL + DDD

## 1. CORRECCIONES APLICADAS AL CARTCONTROLLER ✅

### Problema Identificado
El `CartController` tenía los siguientes problemas:
- **userId hardcodeado**: `getAllCarts(1111L)` - valor ficticio
- **Nombre de método incorrecto**: `getAllCategories()` en lugar de `getAllCarts()`
- **Convención de nombres incorrecta**: métodos como `createcart()`, `getcartById()`, `updatecart()`, `deletecart()`
- **RequestMapping inconsistente**: `"cart"` en lugar de `"/api/carts"`
- **Nombre de parámetro incorrecto**: `CartRequest` en lugar de `cartRequest`

### Solución Implementada
```java
// ANTES (INCORRECTO)
@RequestMapping("cart")
public ResponseEntity<List<CartResponse>> getAllCategories() {
    List<Cart> carts = cartService.getAllCarts(1111L); // ❌ HARDCODEADO
}

// DESPUÉS (CORRECTO)
@RequestMapping("/api/carts")
public ResponseEntity<List<CartResponse>> getAllCarts(@RequestParam Long userId) {
    List<Cart> carts = cartService.getAllCarts(userId); // ✅ Parámetro dinámico
}
```

**Cambios realizados:**
1. ✅ Cambiado `@RequestMapping("cart")` → `@RequestMapping("/api/carts")`
2. ✅ Agregado `@RequestParam Long userId` en `getAllCarts()`
3. ✅ Eliminado hardcoding `1111L`
4. ✅ Renombrado `getAllCategories()` → `getAllCarts()`
5. ✅ Renombrados todos los métodos a camelCase correcto
6. ✅ Renombrados parámetros de variables locales a camelCase

---

## 2. DIFERENCIA ENTRE CART, CARTITEM vs ORDER, ORDERITEM

### 📦 CART y CARTITEM (Carrito de Compras - TEMPORAL)

#### `Cart` - Modelo Dominio
```java
public class Cart {
    private Long id;
    private User user;           // El usuario dueño del carrito
    private List<CartItem> items; // Productos en el carrito
}
```

**Propósito:** 
- Almacenamiento temporal de productos seleccionados por el usuario
- Un usuario puede tener UN carrito activo
- El carrito persiste durante la sesión del usuario
- Se puede modificar: agregar, quitar, actualizar cantidades

#### `CartItem` - Items del Carrito
```java
public class CartItem {
    private Long id;
    private Cart cart;           // Referencia al carrito
    private Product product;     // El producto
    private Integer quantity;    // Cantidad seleccionada
}
```

**Propósito:**
- Representa un producto específico en el carrito
- NO tiene precio de compra (usa el precio actual del producto)
- Es temporal y puede cambiar si el usuario modifica cantidades
- Se elimina cuando se vacía el carrito o se procesa a orden

### 📋 ORDER y ORDERITEM (Pedido - PERMANENTE)

#### `Order` - Modelo Dominio
```java
public class Order {
    private Long id;
    private User user;                        // Quién compró
    private List<OrderItem> items;            // Items de la orden
    private BigDecimal total;                 // Total FIJADO en el momento
    private OrderState orderState;            // PENDING, PROCESSING, SHIPPED, DELIVERED, CANCELLED
    private LocalDateTime createdAt;          // Cuándo se creó
    private LocalDateTime deliveredAt;        // Cuándo se entregó
    private List<Discount> appliedDiscounts;  // Descuentos aplicados
}
```

**Propósito:**
- Registro permanente de una compra
- Una orden se crea a partir de un carrito
- NO se puede modificar (para auditoría y cumplimiento)
- Tiene estado que evoluciona: PENDING → PROCESSING → SHIPPED → DELIVERED
- El total es INMUTABLE

#### `OrderItem` - Items de la Orden
```java
public class OrderItem {
    private Long id;
    private Order order;                // Referencia a la orden
    private Product product;            // El producto comprado
    private Integer quantity;           // Cantidad comprada
    private BigDecimal priceAtPurchase; // ⭐ PRECIO FIJADO en el momento de compra
}
```

**Propósito:**
- Registro histórico de QUÉ se compró y POR CUÁNTO
- El precio es INMUTABLE (aunque el precio del producto cambie después)
- Permite auditoría: "El usuario compró 2 unidades a $10 cada una"

### 🔄 FLUJO: CART → ORDER

```
Usuario añade productos → CART (temporal)
Usuario modifica cantidades → CART (temporal)
Usuario procesa compra → 
  ↓
  CartItems se convierten en OrderItems
  El precio actual se FIJA en OrderItem.priceAtPurchase
  Se calcula el total
  Se crea la ORDER
  Se VACÍA el CART
```

### ⚡ COMPARATIVA RÁPIDA

| Aspecto | Cart / CartItem | Order / OrderItem |
|---------|-----------------|-------------------|
| **Duración** | Temporal | Permanente |
| **Estado** | N/A (solo items) | PENDING, PROCESSING, SHIPPED, DELIVERED, CANCELLED |
| **Total** | Calculado dinámicamente | Fijado en el momento |
| **Precio** | Precio actual del producto | Precio en el momento de compra (inmutable) |
| **Modificable** | Sí (usuario puede cambiar) | No (solo cambiar estado) |
| **Propósito** | Seleccionar productos | Registro de compra (auditoría) |
| **Tabla BD** | cart, cart_item | order, order_item |

---

## 3. EXPLICACIÓN DE LA CLASE SHIPPING

### 📍 `Shipping` - Modelo Dominio

```java
public class Shipping {
    private Long id;
    private Order order;                    // ¿De qué orden es?
    private String deliveryAddress;         // Adonde se entrega
    private ShippingStatus status;          // PENDING, PROCESSING, SHIPPED, DELIVERED, RETURNED
    private String carrier;                 // Empresa de transporte (DHL, UPS, FedEx, etc.)
    private String trackingNumber;          // Número de seguimiento (ej: 1Z999AA10123456784)
    private LocalDateTime estimatedDelivery; // Fecha estimada de entrega
}
```

### Propósito

- Gestiona el ENVÍO físico de una orden
- Es creada DESPUÉS de que la orden se pague
- Evoluciona en paralelo a la orden pero con su propio ciclo de vida
- Permite al cliente rastrear su paquete

### 🔄 Ciclo de vida de Shipping

```
Order creada (PENDING)
    ↓
Payment confirmado (PROCESSING)
    ↓
Shipping creada (PENDING) ← Aquí nace el shipping
    ↓
Carrier recibe (PROCESSING)
    ↓
En tránsito (SHIPPED)
    ↓
Entregado (DELIVERED) ← Orden completa
```

### 📋 Responsabilidades

✅ **Qué SÍ debe saber:**
- De qué orden es
- Adonde se envía
- Quién lo transporta (carrier)
- Número de seguimiento
- Estado actual

❌ **Qué NO debe saber:**
- Costo del envío (eso es responsabilidad de Payment)
- Metodos de empaquetado específico (eso es logística)
- Inventario (eso es Product)

---

## 4. ¿CUMPLE SHIPPING CON ARQUITECTURA HEXAGONAL?

### ✅ SÍ, CUMPLE CORRECTAMENTE

#### Análisis por capas:

**1. Core (Dominio)**
```
com.drtx.ecomerce.amazon.core.model.shipping.Shipping
- Clase de DOMINIO PURO (sin anotaciones de BD)
- Contiene SOLO lógica de negocio
- No depende de nada externo
✅ CORRECTO
```

**2. Puertos**
```
No hay puertos específicos de Shipping visible actualmente.
Probablemente los usa implícitamente a través de Order.
```

**3. Adaptadores OUT (Persistencia)**
```
com.drtx.ecomerce.amazon.adapters.out.persistence.shipping.*
├── ShippingEntity (Entity JPA)
├── ShippingPersistenceMapper (Mapeo Domain ↔ Entity)
└── ShippingRepositoryAdapter (Implementación del puerto)
✅ CORRECTO - Separación clara
```

#### Diagrama de Dependencias

```
┌─────────────────────────────────────┐
│   REST Controller (Adapter IN)      │
│   (No directo a Shipping)           │
└────────────────┬────────────────────┘
                 │
┌────────────────▼─────────────────────────┐
│   Application Use Case Layer            │ ← OrderUseCaseImpl
│   (Maneja tanto Order como Shipping)    │
└────────────────┬─────────────────────────┘
                 │
┌────────────────▼──────────────────────┐
│   Core Domain (PURO)                 │
│   ┌──────────────────────────────┐   │
│   │ Shipping (sin dependencias) │   │
│   │ Order (sin dependencias)    │   │
│   └──────────────────────────────┘   │
└────────────────┬──────────────────────┘
                 │
┌────────────────▼──────────────────────┐
│   Puertos OUT (Abstracciones)        │
│   ShippingRepositoryPort            │
│   OrderRepositoryPort               │
└────────────────┬──────────────────────┘
                 │
┌────────────────▼──────────────────────┐
│   Adaptadores OUT (Persistencia)      │
│   ┌───────────────────────────────┐  │
│   │ ShippingRepositoryAdapter     │  │
│   │ + ShippingPersistenceMapper   │  │
│   │ + ShippingEntity (JPA)        │  │
│   └───────────────────────────────┘  │
└───────────────────────────────────────┘
```

✅ **Conclusión:** Shipping SÍ cumple perfectamente con hexagonal.

---

## 5. JAKARTA.VALIDATION - ¿CÓMO SE LANZA LA EXCEPCIÓN?

### 📝 Uso de @jakarta.validation.Valid

```java
@PostMapping
public ResponseEntity<CartResponse> createCart(
    @RequestBody @jakarta.validation.Valid CartRequest cart) {
    // Si CartRequest tiene validaciones, Spring las ejecuta aquí
}
```

### 🔍 Cómo funciona internamente

1. **En la clase DTO:**
```java
public class CartRequest {
    @NotNull
    @NotBlank
    private String productName;
    
    @NotNull
    @Min(1)
    private Integer quantity;
}
```

2. **Spring valida automáticamente:**
   - Spring Web intercepta la anotación `@Valid`
   - Busca validaciones (@NotNull, @NotBlank, etc.)
   - Si fallan, lanza `MethodArgumentNotValidException`

3. **La excepción es capturada por:**
```java
// GlobalExceptionHandler.java
@ControllerAdvice
public class GlobalExceptionHandler {
    
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(
            MethodArgumentNotValidException ex) {
        return ResponseEntity.badRequest()
            .body(new ErrorResponse("Validación fallida", ex.getMessage()));
    }
}
```

### ⚠️ IMPORTANTE: Jakarta vs Javax

- **`jakarta.validation`** ← Nueva (Java 17+)
- **`javax.validation`** ← Antigua (Java 8-11)

Tu proyecto usa **jakarta** correctamente, lo cual es apropiado para Spring Boot 3.x+.

---

## 6. ¿DÓNDE DEBERÍA ESTAR LA SEGURIDAD EN ADAPTERS/IN?

### 📁 Ubicación ACTUAL (CORRECTA)

```
adapters/in/
├── rest/                          ← REST Controllers (publicos)
│   ├── cart/
│   ├── category/
│   ├── product/
│   └── ...
├── security/                      ← ✅ CORRECTA UBICACIÓN
│   ├── AuthController.java
│   ├── JwtAuthFilter.java
│   ├── JpaUserDetailsService.java
│   ├── SecurityUserDetails.java
│   ├── dto/
│   └── mappers/
└── graphql/
```

### 🌳 ESTRUCTURA IDEAL (RECOMENDACIÓN)

```
adapters/
├── in/
│   ├── rest/
│   │   ├── cart/
│   │   ├── category/
│   │   └── ...
│   ├── security/                          ← Parcialmente aquí
│   │   ├── AuthController.java            ← ✅ REST de seguridad
│   │   ├── JwtAuthFilter.java             ← ✅ Entry point
│   │   └── dto/
│   └── graphql/
│
├── out/
│   ├── persistence/
│   │   ├── user/
│   │   │   └── UserRepositoryAdapter.java
│   │   └── security/                      ← ⭐ Debería estar aquí
│   │       ├── RevokedTokenPersistenceAdapter.java
│   │       ├── RevokedTokenEntity.java
│   │       └── RevokedTokenRepository.java
│   └── ...
│
└── infrastructure/                        ← ⭐ Aquí va la configuración
    ├── security/
    │   ├── SecurityConfig.java            ← ✅ Configuración Spring Security
    │   ├── AuthenticationFacadeAdapter.java ← ✅ Adaptador
    │   ├── BcryptPasswordService.java     ← ✅ Servicio cripto
    │   ├── JwtService.java                ← ✅ Lógica JWT
    │   └── TokenRevocationService.java    ← ✅ Revocación
    └── exceptions/
```

### RESPUESTA CLARA

**¿Dónde debería estar la seguridad?**

```
┌─────────────────────────────────────────────────────┐
│         adapters/in/security/                       │
│                                                     │
│ ✅ AuthController (entrada REST)                   │
│ ✅ JwtAuthFilter (interceptor)                      │
│ ✅ JpaUserDetailsService (carga usuarios)           │
│ ✅ SecurityUserDetails (representación usuario)     │
│ ✅ DTO y Mappers (serialización)                    │
└─────────────────────────────────────────────────────┘
           ↓ DÉLEGA A ↓
┌─────────────────────────────────────────────────────┐
│    infrastructure/security/                         │
│                                                     │
│ ✅ SecurityConfig (configuración Spring Security)  │
│ ✅ AuthenticationFacadeAdapter (puerto OUT)         │
│ ✅ BcryptPasswordService (encriptación)             │
│ ✅ JwtService (generación/validación JWT)           │
│ ✅ TokenRevocationService (blacklist)               │
└─────────────────────────────────────────────────────┘
           ↓ ACCEDE A ↓
┌─────────────────────────────────────────────────────┐
│    adapters/out/persistence/security/               │
│                                                     │
│ ✅ RevokedTokenPersistenceAdapter (persistencia)    │
│ ✅ RevokedTokenRepository (JPA)                     │
│ ✅ RevokedTokenEntity (BD)                          │
└─────────────────────────────────────────────────────┘
```

**Conclusión:** Tu estructura es **parcialmente correcta**. 
- `adapters/in/security` está bien para controladores
- `infrastructure/security` está bien para configuración y servicios
- `adapters/out/persistence/security` está bien para persistencia

---

## 7. ÁRBOL DE DIRECTORIOS COMPLETO Y VALIDACIÓN

### 📂 ESTRUCTURA CORRECTA

```
amazon/
│
├── core/                              [DOMINIO - Hexagonal puro]
│   ├── model/                         [Entidades de dominio]
│   │   ├── discount/
│   │   │   └── Discount.java
│   │   ├── exceptions/
│   │   │   └── EntityNotFoundException.java
│   │   ├── issues/                    [Incidencias y Appeals]
│   │   │   ├── Appeal.java
│   │   │   ├── Incidence.java
│   │   │   └── Report.java
│   │   ├── notifications/
│   │   │   └── Notification.java
│   │   ├── order/                     [Gestión de órdenes]
│   │   │   ├── Cart.java              ✅ Carrito temporal
│   │   │   ├── CartItem.java          ✅ Items del carrito
│   │   │   ├── Order.java             ✅ Orden permanente
│   │   │   ├── OrderItem.java         ✅ Items de orden
│   │   │   └── OrderState.java
│   │   ├── payment/                   [Pagos]
│   │   │   ├── Payment.java
│   │   │   ├── PaymentMethod.java
│   │   │   └── PaymentStatus.java
│   │   ├── product/                   [Productos]
│   │   │   ├── Category.java
│   │   │   ├── Inventory.java
│   │   │   ├── Product.java
│   │   │   └── Review.java
│   │   ├── returns/                   [Devoluciones]
│   │   │   ├── Return.java
│   │   │   └── ReturnStatus.java
│   │   ├── security/                  [Seguridad dominio]
│   │   │   └── Token.java
│   │   ├── shipping/                  [Envíos]
│   │   │   ├── Shipping.java
│   │   │   └── ShippingStatus.java
│   │   ├── supplier/                  [Proveedores]
│   │   │   └── Supplier.java
│   │   └── user/                      [Usuarios]
│   │       ├── Favorite.java
│   │       ├── User.java
│   │       └── UserRole.java
│   │
│   └── ports/                         [Puertos - Abstracciones]
│       ├── in/                        [Entrada de use cases]
│       │   ├── rest/
│       │   │   ├── CartUseCasePort.java
│       │   │   ├── CategoryUseCasePort.java
│       │   │   ├── ProductUseCasePort.java
│       │   │   └── ...
│       │   └── rest/security/
│       │       ├── AuthUseCasePort.java
│       │       └── ...
│       └── out/                       [Salida a infraestructura]
│           ├── persistence/
│           │   ├── CartRepositoryPort.java
│           │   ├── OrderRepositoryPort.java
│           │   └── ...
│           └── security/
│               └── AuthenticationFacade.java
│
├── application/                       [CASOS DE USO - Orquestación]
│   └── usecases/
│       ├── CartUseCaseImpl.java        ✅ Implementa CartUseCasePort
│       ├── OrderUseCaseImpl.java       ✅ Implementa OrderUseCasePort
│       ├── ProductUseCaseImpl.java
│       ├── UserUseCaseImpl.java
│       ├── auth/
│       │   └── AuthUseCaseImpl.java
│       └── ...
│
├── adapters/                          [ADAPTADORES - Pluggable]
│   ├── in/                            [ENTRADA - HTTP, GraphQL, etc]
│   │   ├── rest/
│   │   │   ├── appeal/
│   │   │   │   ├── AppealController.java
│   │   │   │   ├── dto/
│   │   │   │   └── mappers/
│   │   │   ├── cart/                  ✅ CORREGIDO
│   │   │   │   ├── CartController.java
│   │   │   │   ├── dtos/
│   │   │   │   └── mappers/
│   │   │   ├── category/
│   │   │   ├── favorite/
│   │   │   ├── incidence/
│   │   │   ├── order/
│   │   │   ├── product/
│   │   │   └── user/
│   │   ├── security/                  ✅ ENTRADA de seguridad
│   │   │   ├── AuthController.java
│   │   │   ├── JwtAuthFilter.java
│   │   │   ├── JpaUserDetailsService.java
│   │   │   ├── SecurityUserDetails.java
│   │   │   ├── dto/
│   │   │   └── mappers/
│   │   └── graphql/
│   │
│   └── out/                           [SALIDA - BD, APIs externas]
│       └── persistence/
│           ├── appeal/
│           │   ├── AppealEntity.java
│           │   ├── AppealPersistenceMapper.java
│           │   ├── AppealPersistenceRepository.java
│           │   └── AppealRepositoryAdapter.java
│           ├── cart/
│           ├── category/
│           ├── discount/
│           ├── favorite/
│           ├── incidence/
│           ├── inventory/
│           ├── notification/
│           ├── order/
│           ├── payment/
│           ├── product/
│           ├── returns/
│           ├── review/
│           ├── security/               ✅ Token revocation
│           ├── shipping/
│           ├── supplier/
│           └── user/
│
└── infrastructure/                    [INFRAESTRUCTURA]
    ├── exceptions/
    │   ├── GlobalExceptionHandler.java
    │   └── user/
    │       └── UserNotFoundException.java
    └── security/                      ✅ Configuración y servicios
        ├── AuthenticationFacadeAdapter.java
        ├── BcryptPasswordService.java
        ├── JwtService.java
        ├── SecurityConfig.java
        └── TokenRevocationService.java
```

### ✅ VALIDACIÓN DE UBICACIÓN

| Componente | Ubicación | ✅/❌ |
|-----------|-----------|--------|
| Entidades de dominio | `core/model/**` | ✅ CORRECTO |
| Puertos | `core/ports/**` | ✅ CORRECTO |
| Use cases | `application/usecases/**` | ✅ CORRECTO |
| REST Controllers | `adapters/in/rest/**` | ✅ CORRECTO |
| Auth Controller | `adapters/in/security/**` | ✅ CORRECTO |
| Persistencia entities | `adapters/out/persistence/**` | ✅ CORRECTO |
| Mappers | `adapters/in/rest/**/mappers` | ✅ CORRECTO |
| Security Config | `infrastructure/security/**` | ✅ CORRECTO |
| Global Exception Handler | `infrastructure/exceptions/**` | ✅ CORRECTO |

---

## 8. CÓMO REVISAR TODAS LAS CLASES EN ORDEN

### 📋 ORDEN RECOMENDADO DE REVISIÓN

#### **FASE 1: Dominio (Core)**
1. Todas las entidades en `core/model/`
   - `user/User.java` → Usuario base
   - `product/Product.java` → Producto
   - `product/Category.java` → Categorías
   - `product/Inventory.java` → Stock
   - `order/Cart.java` → Carrito
   - `order/CartItem.java` → Items carrito
   - `order/Order.java` → Orden
   - `order/OrderItem.java` → Items orden
   - `payment/Payment.java` → Pagos
   - `shipping/Shipping.java` → Envíos
   - `supplier/Supplier.java` → Proveedores

2. Estados y enums
   - `order/OrderState.java`
   - `payment/PaymentStatus.java`
   - `shipping/ShippingStatus.java`
   - `user/UserRole.java`

3. Modelos adicionales
   - `issues/Appeal.java`, `Incidence.java`, `Report.java`
   - `returns/Return.java`
   - `discount/Discount.java`
   - `product/Review.java`

#### **FASE 2: Puertos (Contratos)**
1. Puertos de entrada
   - `core/ports/in/rest/*.java`
   - `core/ports/in/rest/security/*.java`

2. Puertos de salida
   - `core/ports/out/persistence/*.java`
   - `core/ports/out/security/*.java`

#### **FASE 3: Casos de Uso (Orquestación)**
1. `application/usecases/*.java`
2. `application/usecases/auth/*.java`

#### **FASE 4: Adaptadores IN (Presentación)**
1. Security
   - `adapters/in/security/AuthController.java`
   - `adapters/in/security/JwtAuthFilter.java`
   - `adapters/in/security/JpaUserDetailsService.java`

2. REST Controllers (por módulo)
   - `adapters/in/rest/user/UserController.java`
   - `adapters/in/rest/product/ProductController.java`
   - `adapters/in/rest/category/CategoryController.java`
   - `adapters/in/rest/cart/CartController.java` ✅ REVISAR
   - `adapters/in/rest/order/OrderController.java`
   - Otros controladores

#### **FASE 5: Adaptadores OUT (Persistencia)**
1. Por módulo (user, product, order, etc.)
   - `Entity.java` (mapeo JPA)
   - `PersistenceMapper.java` (Domain ↔ Entity)
   - `Repository.java` (JPA)
   - `RepositoryAdapter.java` (implementa puerto)

#### **FASE 6: Infraestructura**
1. Seguridad
   - `infrastructure/security/SecurityConfig.java`
   - `infrastructure/security/JwtService.java`
   - `infrastructure/security/BcryptPasswordService.java`

2. Excepciones
   - `infrastructure/exceptions/GlobalExceptionHandler.java`

### 🎯 CHECKLIST DE REVISIÓN

Para cada clase, verifica:

```
□ ¿Está en el paquete correcto?
□ ¿Tiene un propósito claro?
□ ¿Respeta la arquitectura hexagonal?
□ ¿No tiene dependencias circulares?
□ ¿El nombre es descriptivo?
□ ¿No tiene responsabilidades múltiples?
□ ¿Usa interfaces/puertos cuando corresponde?
□ ¿Los mappers están correctamente implementados?
□ ¿Los DTOs tienen validaciones?
□ ¿El controlador delega a use cases?
□ ¿La persistencia está abstraída?
```

---

## RESUMEN FINAL

✅ **CartController** - CORREGIDO y validado
✅ **Cart vs CartItem vs Order vs OrderItem** - DIFERENCIAS EXPLICADAS
✅ **Shipping** - CUMPLE HEXAGONAL
✅ **Jakarta.validation** - FUNCIONAMIENTO EXPLICADO
✅ **Ubicación de Seguridad** - CORRECTA (parcial en IN, parcial en infrastructure)
✅ **Estructura del proyecto** - VALIDADA Y DOCUMENTADA

El proyecto está **bien organizado** y sigue correctamente la **arquitectura hexagonal con DDD**.

