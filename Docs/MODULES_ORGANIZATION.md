# Organización de Módulos - Arquitectura Hexagonal

## 📋 Estructura General del Proyecto

```
src/main/java/com/drtx/ecomerce/amazon/
├── 📦 core/                          # DOMINIO (sin dependencias externas)
│   ├── model/                        # Modelos de dominio
│   │   ├── user/                     # Módulo de Usuario
│   │   ├── product/                  # Módulo de Producto
│   │   ├── category/                 # Módulo de Categoría
│   │   ├── cart/                     # Módulo de Carrito
│   │   ├── order/                    # Módulo de Pedidos
│   │   ├── payment/                  # Módulo de Pagos
│   │   ├── shipping/                 # Módulo de Envío
│   │   ├── review/                   # Módulo de Reseñas
│   │   ├── notification/             # Módulo de Notificaciones
│   │   ├── incidence/                # Módulo de Incidencias
│   │   ├── security/                 # Módulo de Seguridad (dominio)
│   │   └── exceptions/               # Excepciones de dominio
│   │
│   └── ports/                        # Interfaces (contratos)
│       ├── in/                       # Puertos de entrada (casos de uso)
│       │   ├── rest/                 # Para API REST
│       │   └── graphql/              # Para GraphQL
│       │
│       └── out/                      # Puertos de salida (servicios externos)
│           ├── persistence/          # Repositorios
│           ├── messaging/            # Mensajería (email, notificaciones)
│           ├── payment/              # Servicios de pago
│           ├── storage/              # Almacenamiento (Azure, S3)
│           └── security/             # Servicios de seguridad
│
├── ⚙️ application/                    # CASOS DE USO (lógica de aplicación)
│   └── usecases/
│       ├── user/                     # Casos de uso de usuario
│       ├── product/                  # Casos de uso de producto
│       ├── category/                 # Casos de uso de categoría
│       ├── cart/                     # Casos de uso de carrito
│       ├── order/                    # Casos de uso de pedidos
│       ├── payment/                  # Casos de uso de pagos
│       ├── review/                   # Casos de uso de reseñas
│       ├── notification/             # Casos de uso de notificaciones
│       ├── incidence/                # Casos de uso de incidencias
│       └── auth/                     # Casos de uso de autenticación
│
├── 🌐 adapters/                       # ADAPTADORES (entrada/salida)
│   ├── in/                           # Adaptadores de entrada
│   │   ├── rest/                     # Controllers REST
│   │   │   ├── user/
│   │   │   ├── product/
│   │   │   ├── category/
│   │   │   ├── cart/
│   │   │   ├── order/
│   │   │   ├── payment/
│   │   │   ├── review/
│   │   │   ├── notification/
│   │   │   └── incidence/
│   │   │
│   │   ├── graphql/                  # Resolvers GraphQL
│   │   │   ├── product/
│   │   │   └── category/
│   │   │
│   │   └── security/                 # Seguridad (controllers, filters, DTOs)
│   │       ├── AuthController.java
│   │       ├── JwtAuthFilter.java
│   │       ├── SecurityUserDetails.java
│   │       ├── dto/
│   │       └── mappers/
│   │
│   └── out/                          # Adaptadores de salida
│       ├── persistence/              # Persistencia JPA
│       │   ├── user/
│       │   ├── product/
│       │   ├── category/
│       │   ├── cart/
│       │   ├── order/
│       │   ├── payment/
│       │   ├── review/
│       │   ├── notification/
│       │   ├── incidence/
│       │   └── security/
│       │
│       ├── messaging/                # Email, SMS
│       │   └── email/
│       │
│       ├── payment/                  # Servicios de pago externos
│       │   └── stripe/
│       │
│       └── storage/                  # Almacenamiento (Azure Blob)
│           └── azure/
│
└── 🏗️ infrastructure/                 # CONFIGURACIÓN TÉCNICA
    ├── config/                       # Configuraciones generales
    ├── security/                     # Configuración de seguridad
    ├── persistence/                  # Configuración de JPA/Hibernate
    ├── messaging/                    # Configuración de email
    ├── storage/                      # Configuración de Azure Storage
    └── graphql/                      # Configuración de GraphQL
```

---

## 🗂️ Módulos Organizados por Contexto

### 1. 👤 Módulo de Usuario (User)
**Responsabilidad**: Gestión de usuarios del sistema

**Core (Dominio)**:
- `core/model/user/User.java` - Entidad de dominio
- `core/model/user/UserRole.java` - Roles del sistema
- `core/model/user/Address.java` - Dirección del usuario

**Ports**:
- `core/ports/in/rest/user/UserUseCasePort.java` - Casos de uso
- `core/ports/out/persistence/UserRepositoryPort.java` - Repositorio

**Application**:
- `application/usecases/user/UserService.java` - Lógica de negocio

**Adapters IN**:
- `adapters/in/rest/user/UserController.java` - API REST
- `adapters/in/rest/user/dto/UserRequest.java` - DTOs
- `adapters/in/rest/user/mappers/UserMapper.java` - Mappers

**Adapters OUT**:
- `adapters/out/persistence/user/UserEntity.java` - Entidad JPA
- `adapters/out/persistence/user/UserJpaRepository.java` - JPA Repo
- `adapters/out/persistence/user/UserPersistenceAdapter.java` - Adaptador

---

### 2. 🛍️ Módulo de Producto (Product)
**Responsabilidad**: Catálogo de productos

**Core (Dominio)**:
- `core/model/product/Product.java`
- `core/model/product/ProductStock.java`

**Ports**:
- `core/ports/in/rest/product/ProductUseCasePort.java`
- `core/ports/out/persistence/ProductRepositoryPort.java`

**Application**:
- `application/usecases/product/ProductService.java`

**Adapters IN**:
- `adapters/in/rest/product/ProductController.java`
- `adapters/in/graphql/product/ProductResolver.java`
- `adapters/in/rest/product/dto/ProductRequest.java`

**Adapters OUT**:
- `adapters/out/persistence/product/ProductEntity.java`
- `adapters/out/persistence/product/ProductPersistenceAdapter.java`

---

### 3. 📁 Módulo de Categoría (Category)
**Responsabilidad**: Organización de productos en categorías

**Core (Dominio)**:
- `core/model/category/Category.java`

**Ports**:
- `core/ports/in/rest/category/CategoryUseCasePort.java`
- `core/ports/out/persistence/CategoryRepositoryPort.java`

**Application**:
- `application/usecases/category/CategoryService.java`

**Adapters IN**:
- `adapters/in/rest/category/CategoryController.java`
- `adapters/in/graphql/category/CategoryResolver.java`

**Adapters OUT**:
- `adapters/out/persistence/category/CategoryEntity.java`
- `adapters/out/persistence/category/CategoryPersistenceAdapter.java`

---

### 4. 🛒 Módulo de Carrito (Cart)
**Responsabilidad**: Gestión del carrito de compras

**Core (Dominio)**:
- `core/model/cart/Cart.java` - Carrito del usuario
- `core/model/cart/CartItem.java` - Item individual en el carrito

**Diferencia con Order**:
- **Cart**: Temporal, mientras el usuario selecciona productos
- **Order**: Permanente, después de confirmar la compra

**Ports**:
- `core/ports/in/rest/cart/CartUseCasePort.java`
- `core/ports/out/persistence/CartRepositoryPort.java`

**Application**:
- `application/usecases/cart/CartService.java`

**Adapters IN**:
- `adapters/in/rest/cart/CartController.java`

**Adapters OUT**:
- `adapters/out/persistence/cart/CartEntity.java`
- `adapters/out/persistence/cart/CartItemEntity.java`
- `adapters/out/persistence/cart/CartPersistenceAdapter.java`

---

### 5. 📦 Módulo de Pedidos (Order)
**Responsabilidad**: Gestión de pedidos confirmados

**Core (Dominio)**:
- `core/model/order/Order.java` - Pedido confirmado
- `core/model/order/OrderItem.java` - Item del pedido
- `core/model/order/OrderStatus.java` - Estado del pedido

**Diferencia con Cart**:
- **Order**: Se crea DESPUÉS de confirmar el carrito
- **Order**: Tiene estados (PENDING, CONFIRMED, SHIPPED, DELIVERED)
- **Order**: Asociado a Payment y Shipping

**Ports**:
- `core/ports/in/rest/order/OrderUseCasePort.java`
- `core/ports/out/persistence/OrderRepositoryPort.java`

**Application**:
- `application/usecases/order/OrderService.java`

**Adapters IN**:
- `adapters/in/rest/order/OrderController.java`

**Adapters OUT**:
- `adapters/out/persistence/order/OrderEntity.java`
- `adapters/out/persistence/order/OrderItemEntity.java`
- `adapters/out/persistence/order/OrderPersistenceAdapter.java`

---

### 6. 💳 Módulo de Pagos (Payment)
**Responsabilidad**: Procesamiento de pagos

**Core (Dominio)**:
- `core/model/payment/Payment.java`
- `core/model/payment/PaymentStatus.java`
- `core/model/payment/PaymentMethod.java`

**Ports**:
- `core/ports/in/rest/payment/PaymentUseCasePort.java`
- `core/ports/out/persistence/PaymentRepositoryPort.java`
- `core/ports/out/payment/PaymentGatewayPort.java` - Para Stripe/PayPal

**Application**:
- `application/usecases/payment/PaymentService.java`

**Adapters IN**:
- `adapters/in/rest/payment/PaymentController.java`

**Adapters OUT**:
- `adapters/out/persistence/payment/PaymentEntity.java`
- `adapters/out/payment/stripe/StripePaymentAdapter.java` - Integración Stripe

---

### 7. 🚚 Módulo de Envío (Shipping)
**Responsabilidad**: Gestión de envíos y seguimiento

**Core (Dominio)**:
- `core/model/shipping/Shipping.java`
- `core/model/shipping/ShippingStatus.java`
- `core/model/shipping/TrackingInfo.java`

**Ports**:
- `core/ports/in/rest/shipping/ShippingUseCasePort.java`
- `core/ports/out/persistence/ShippingRepositoryPort.java`

**Application**:
- `application/usecases/shipping/ShippingService.java`

**Adapters IN**:
- `adapters/in/rest/shipping/ShippingController.java`

**Adapters OUT**:
- `adapters/out/persistence/shipping/ShippingEntity.java`
- `adapters/out/persistence/shipping/ShippingPersistenceAdapter.java`

---

### 8. ⭐ Módulo de Reseñas (Review)
**Responsabilidad**: Reseñas y calificaciones de productos

**Core (Dominio)**:
- `core/model/review/Review.java`
- `core/model/review/Rating.java`

**Ports**:
- `core/ports/in/rest/review/ReviewUseCasePort.java`
- `core/ports/out/persistence/ReviewRepositoryPort.java`

**Application**:
- `application/usecases/review/ReviewService.java`

**Adapters IN**:
- `adapters/in/rest/review/ReviewController.java`

**Adapters OUT**:
- `adapters/out/persistence/review/ReviewEntity.java`
- `adapters/out/persistence/review/ReviewPersistenceAdapter.java`

---

### 9. 🔔 Módulo de Notificaciones (Notification)
**Responsabilidad**: Envío de notificaciones (email, SMS, push)

**Core (Dominio)**:
- `core/model/notification/Notification.java`
- `core/model/notification/NotificationType.java`

**Ports**:
- `core/ports/in/rest/notification/NotificationUseCasePort.java`
- `core/ports/out/messaging/EmailServicePort.java`
- `core/ports/out/messaging/SmsServicePort.java`

**Application**:
- `application/usecases/notification/NotificationService.java`

**Adapters IN**:
- `adapters/in/rest/notification/NotificationController.java`

**Adapters OUT**:
- `adapters/out/messaging/email/AzureEmailAdapter.java`
- `adapters/out/messaging/email/JavaMailEmailAdapter.java`

---

### 10. 🚨 Módulo de Incidencias (Incidence)
**Responsabilidad**: Gestión de problemas/tickets de soporte

**Core (Dominio)**:
- `core/model/incidence/Incidence.java`
- `core/model/incidence/IncidenceStatus.java`
- `core/model/incidence/IncidenceType.java`

**Ports**:
- `core/ports/in/rest/incidence/IncidenceUseCasePort.java`
- `core/ports/out/persistence/IncidenceRepositoryPort.java`

**Application**:
- `application/usecases/incidence/IncidenceService.java`

**Adapters IN**:
- `adapters/in/rest/incidence/IncidenceController.java`

**Adapters OUT**:
- `adapters/out/persistence/incidence/IncidenceEntity.java`
- `adapters/out/persistence/incidence/IncidencePersistenceAdapter.java`

---

### 11. 🔐 Módulo de Seguridad (Security)
**Responsabilidad**: Autenticación, autorización, tokens

Ver archivo: [SECURITY_MODULE_ARCHITECTURE.md](Docs/SECURITY_MODULE_ARCHITECTURE.md)

**Core (Dominio)**:
- `core/model/security/AuthResult.java`
- `core/model/security/RefreshToken.java`
- `core/model/security/LoginCommand.java`

**Ports**:
- `core/ports/in/rest/security/AuthUseCasePort.java`
- `core/ports/out/security/TokenProvider.java`
- `core/ports/out/security/RefreshTokenRepositoryPort.java`

**Application**:
- `application/usecases/auth/AuthService.java`
- `application/usecases/auth/RefreshTokenService.java`

**Adapters IN**:
- `adapters/in/security/AuthController.java`
- `adapters/in/security/JwtAuthFilter.java`
- `adapters/in/security/SecurityUserDetails.java`

**Adapters OUT**:
- `adapters/out/persistence/security/RefreshTokenEntity.java`
- `adapters/out/persistence/security/RefreshTokenPersistenceAdapter.java`

**Infrastructure**:
- `infrastructure/security/JwtService.java`
- `infrastructure/security/SecurityConfig.java`

---

## 📊 Orden de Revisión Recomendado

### Fase 1: Fundamentos
1. **Core/Model** - Entender las entidades de dominio
2. **Core/Ports** - Entender los contratos (interfaces)
3. **Exceptions** - Sistema de manejo de errores

### Fase 2: Seguridad (Base para todo)
4. **Security Module** - Autenticación y autorización
5. **User Module** - Gestión de usuarios

### Fase 3: Catálogo
6. **Category Module** - Categorías de productos
7. **Product Module** - Productos del e-commerce

### Fase 4: Compras
8. **Cart Module** - Carrito de compras
9. **Order Module** - Pedidos confirmados
10. **Payment Module** - Procesamiento de pagos
11. **Shipping Module** - Gestión de envíos

### Fase 5: Engagement
12. **Review Module** - Reseñas de productos
13. **Notification Module** - Comunicación con usuarios
14. **Incidence Module** - Soporte y tickets

### Fase 6: Infraestructura
15. **Infrastructure/Config** - Configuraciones
16. **Infrastructure/Security** - Seguridad técnica
17. **Infrastructure/Storage** - Almacenamiento Azure

---

## ✅ Cumplimiento de Arquitectura Hexagonal

### ✔️ Independencia de Frameworks
- Core no depende de Spring, JPA, JWT
- Fácil cambiar de framework sin afectar lógica de negocio

### ✔️ Inversión de Dependencias
- Application depende de Ports (interfaces)
- Adapters implementan Ports
- Core no conoce detalles técnicos

### ✔️ Testabilidad
- Cada capa es testeable independientemente
- Mocks fáciles usando interfaces (Ports)
- Tests sin necesidad de BD o HTTP

### ✔️ Separación de Responsabilidades
- **Core**: Lógica de negocio pura
- **Application**: Orquestación de casos de uso
- **Adapters**: Detalles técnicos de entrada/salida
- **Infrastructure**: Configuración y wiring

### ✔️ Flexibilidad
- Cambiar de BD (PostgreSQL → MongoDB)
- Cambiar API (REST → gRPC)
- Cambiar storage (Azure → AWS S3)
- Todo sin tocar el Core

---

## 🎯 Diferencias Clave Entre Módulos

### Cart vs Order vs OrderItem
- **Cart**: Temporal, usuario puede modificar
- **CartItem**: Item en el carrito (puede eliminarse)
- **Order**: Permanente, después de confirmar compra
- **OrderItem**: Item del pedido (inmutable después de crear)

### Shipping vs Payment
- **Payment**: Transacción financiera (puede fallar, reintentarse)
- **Shipping**: Entrega física (depende de payment exitoso)
- **Relación**: 1 Order → 1 Payment → 1 Shipping

### Notification vs Incidence
- **Notification**: Comunicación proactiva (emails automáticos)
- **Incidence**: Comunicación reactiva (usuario reporta problema)

---

## 🗄️ Base de Datos

### Tablas Principales
- `users` - Usuarios del sistema
- `products` - Catálogo de productos
- `categories` - Categorías
- `carts` - Carritos de compra
- `cart_items` - Items del carrito
- `orders` - Pedidos confirmados
- `order_items` - Items del pedido
- `payments` - Pagos procesados
- `shippings` - Envíos
- `reviews` - Reseñas de productos
- `notifications` - Notificaciones enviadas
- `incidences` - Incidencias/tickets
- `refresh_tokens` - Tokens de refresh
- `revoked_tokens` - Tokens revocados

---

Última actualización: 2025-12-27

