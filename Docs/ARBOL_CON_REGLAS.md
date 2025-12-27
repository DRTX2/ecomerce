# 🌳 ÁRBOL ESTRUCTURAL CON REGLAS CLARAS

## Estructura Recomendada: QUÉ VA Y QUÉ NO VA

```
com/drtx/ecomerce/amazon/
│
├─ 🔵 CORE/ (Dominio Puro - Núcleo de Negocio)
│  │
│  ├─ model/
│  │  │
│  │  ├─ ✅ AQUÍ VA:
│  │  │  ├─ Entidades de dominio (POJOS)
│  │  │  ├─ Value Objects
│  │  │  ├─ Agregados
│  │  │  ├─ Enums (OrderState, PaymentStatus, etc)
│  │  │  ├─ Excepciones de dominio
│  │  │  └─ Logica de negocio pura
│  │  │
│  │  ├─ ❌ NO VA AQUÍ:
│  │  │  ├─ @Entity (anotaciones JPA)
│  │  │  ├─ @RestController
│  │  │  ├─ @Service
│  │  │  ├─ Inyección de dependencias (Spring)
│  │  │  ├─ Llamadas a BD directas
│  │  │  ├─ HTTP requests/responses
│  │  │  └─ Cualquier framework específico
│  │  │
│  │  └─ Ejemplo estructura:
│  │     order/
│  │     ├─ Cart.java          ✅ Clase pura
│  │     ├─ CartItem.java      ✅ Clase pura
│  │     ├─ Order.java         ✅ Clase pura
│  │     ├─ OrderItem.java     ✅ Clase pura
│  │     ├─ OrderState.java    ✅ Enum
│  │     └─ OrderException.java ✅ Excepción
│  │
│  └─ ports/ (Abstracciones/Contratos)
│     │
│     ├─ ✅ AQUÍ VA:
│     │  ├─ Interfaces (puertos)
│     │  ├─ Definición de contratos
│     │  ├─ Qué necesita el dominio (abstracto)
│     │  ├─ @FunctionalInterface
│     │  └─ Métodos sin implementación
│     │
│     ├─ ❌ NO VA AQUÍ:
│     │  ├─ Implementaciones (@Component)
│     │  ├─ Detalles de persistencia
│     │  ├─ Detalles de presentación
│     │  └─ Lógica de negocio específica
│     │
│     └─ Ejemplo estructura:
│        in/rest/
│        ├─ CartUseCasePort.java      ✅ Interface
│        ├─ OrderUseCasePort.java     ✅ Interface
│        └─ ProductUseCasePort.java   ✅ Interface
│        
│        out/persistence/
│        ├─ CartRepositoryPort.java   ✅ Interface
│        ├─ OrderRepositoryPort.java  ✅ Interface
│        └─ ProductRepositoryPort.java ✅ Interface
│
│
├─ 📦 APPLICATION/ (Orquestación de Use Cases)
│  │
│  └─ usecases/
│     │
│     ├─ ✅ AQUÍ VA:
│     │  ├─ Implementaciones de puertos IN (@Service)
│     │  ├─ Orquestación de lógica
│     │  ├─ Transacciones @Transactional
│     │  ├─ Validaciones de negocio complejas
│     │  ├─ Llamadas a múltiples repositorios
│     │  ├─ Coordinación entre agregados
│     │  └─ Inyección de dependencias (puertos OUT)
│     │
│     ├─ ❌ NO VA AQUÍ:
│     │  ├─ @RestController
│     │  ├─ @Entity
│     │  ├─ HttpServletRequest/Response
│     │  ├─ DTOs (excepto como parámetros)
│     │  ├─ Anotaciones JPA
│     │  └─ Detalles HTTP específicos
│     │
│     └─ Ejemplo estructura:
│        ├─ CartUseCaseImpl.java
│        │  ├─ @Service ✅
│        │  ├─ @RequiredArgsConstructor ✅
│        │  ├─ implements CartUseCasePort ✅
│        │  ├─ private CartRepositoryPort ✅
│        │  └─ public Cart createCart(Cart) ✅
│        │
│        ├─ OrderUseCaseImpl.java
│        │  ├─ @Service ✅
│        │  ├─ @Transactional ✅
│        │  ├─ private CartRepositoryPort ✅
│        │  ├─ private OrderRepositoryPort ✅
│        │  └─ public Order createOrderFromCart() ✅
│        │
│        └─ auth/
│           └─ AuthUseCaseImpl.java ✅
│
│
├─ 🔌 ADAPTERS/ (Pluggable - Intercambiables)
│  │
│  ├─ in/ (ENTRADA - Presentación)
│  │  │
│  │  ├─ rest/ (HTTP/REST)
│  │  │  │
│  │  │  ├─ ✅ AQUÍ VA:
│  │  │  │  ├─ @RestController
│  │  │  │  ├─ @RequestMapping, @GetMapping, etc
│  │  │  │  ├─ Métodos que delegan a puertos IN
│  │  │  │  ├─ @RequestBody, @PathVariable, @RequestParam
│  │  │  │  ├─ ResponseEntity<?>
│  │  │  │  ├─ Inyección de CartUseCasePort (puerto IN)
│  │  │  │  ├─ Validaciones con @Valid
│  │  │  │  └─ Mapeo DTO ↔ Domain
│  │  │  │
│  │  │  ├─ ❌ NO VA AQUÍ:
│  │  │  │  ├─ Lógica de negocio compleja
│  │  │  │  ├─ Llamadas directas a BD
│  │  │  │  ├─ @Entity, @Autowired de repositorios
│  │  │  │  ├─ SQL directo
│  │  │  │  └─ Inyección de RepositoryPort
│  │  │  │
│  │  │  └─ Ejemplo estructura:
│  │  │     product/
│  │  │     ├─ ProductController.java
│  │  │     │  ├─ @RestController ✅
│  │  │     │  ├─ @RequestMapping("/products") ✅
│  │  │     │  ├─ @GetMapping ✅
│  │  │     │  ├─ private ProductUseCasePort ✅
│  │  │     │  ├─ private ProductRestMapper ✅
│  │  │     │  └─ return mapper.toResponse() ✅
│  │  │     │
│  │  │     ├─ dto/
│  │  │     │  ├─ ProductRequest.java ✅
│  │  │     │  └─ ProductResponse.java ✅
│  │  │     │
│  │  │     └─ mappers/
│  │  │        └─ ProductRestMapper.java ✅
│  │  │
│  │  └─ security/ (Seguridad)
│  │     │
│  │     ├─ ✅ AQUÍ VA:
│  │     │  ├─ AuthController (REST endpoint)
│  │     │  ├─ JwtAuthFilter (Spring Filter)
│  │     │  ├─ JpaUserDetailsService (UserDetailsService)
│  │     │  ├─ SecurityUserDetails (UserDetails)
│  │     │  ├─ DTOs (AuthRequest, AuthResponse)
│  │     │  ├─ Mappers (SecurityUserMapper)
│  │     │  └─ Validaciones de entrada
│  │     │
│  │     ├─ ❌ NO VA AQUÍ:
│  │     │  ├─ Generación de JWT (va en infrastructure)
│  │     │  ├─ Encriptación de contraseñas (va en infrastructure)
│  │     │  ├─ Configuración de Spring Security (va en infrastructure)
│  │     │  └─ Token revocation persistence (va en out/persistence)
│  │     │
│  │     └─ Ejemplo estructura:
│  │        ├─ AuthController.java ✅
│  │        ├─ JwtAuthFilter.java ✅
│  │        ├─ JpaUserDetailsService.java ✅
│  │        ├─ SecurityUserDetails.java ✅
│  │        ├─ dto/
│  │        │  ├─ AuthRequest.java ✅
│  │        │  ├─ AuthResponse.java ✅
│  │        │  └─ RegisterRequest.java ✅
│  │        └─ mappers/
│  │           └─ SecurityUserMapper.java ✅
│  │
│  └─ out/ (SALIDA - Infraestructura)
│     │
│     └─ persistence/ (Acceso a Datos)
│        │
│        ├─ ✅ AQUÍ VA:
│        │  ├─ Implementación del puerto OUT (RepositoryAdapter)
│        │  ├─ @Entity (JPA entities)
│        │  ├─ PersistenceMapper (Entity ↔ Domain)
│        │  ├─ Spring Data Repository (JpaRepository)
│        │  ├─ @Repository
│        │  ├─ Queries JPA/HQL
│        │  ├─ Anotaciones JPA (@Entity, @Column, etc)
│        │  └─ Relaciones @ManyToOne, @OneToMany, etc
│        │
│        ├─ ❌ NO VA AQUÍ:
│        │  ├─ Lógica de negocio
│        │  ├─ @Service
│        │  ├─ @RestController
│        │  ├─ SQL directo (raw)
│        │  └─ Llamadas HTTP
│        │
│        └─ Ejemplo estructura:
│           product/
│           ├─ ProductEntity.java
│           │  ├─ @Entity ✅
│           │  ├─ @Table("products") ✅
│           │  ├─ @Id @GeneratedValue ✅
│           │  ├─ @Column, @ManyToOne, etc ✅
│           │  └─ Constructores, getters, setters ✅
│           │
│           ├─ ProductPersistenceMapper.java
│           │  ├─ toDomain(ProductEntity) ✅
│           │  └─ toEntity(Product) ✅
│           │
│           ├─ ProductPersistenceRepository.java
│           │  ├─ extends JpaRepository ✅
│           │  ├─ @Repository ✅
│           │  └─ custom queries ✅
│           │
│           └─ ProductRepositoryAdapter.java
│              ├─ @Component ✅
│              ├─ implements ProductRepositoryPort ✅
│              ├─ private ProductPersistenceRepository ✅
│              ├─ private ProductPersistenceMapper ✅
│              └─ public Product save(Product) ✅
│
│
└─ ⚙️ INFRASTRUCTURE/ (Configuración y Servicios Técnicos)
   │
   ├─ ✅ AQUÍ VA:
   │  ├─ Configuración de Spring (@Configuration)
   │  ├─ Beans globales (@Bean)
   │  ├─ Servicios técnicos (@Component)
   │  ├─ Excepciones handler (@ControllerAdvice)
   │  ├─ Seguridad (SecurityConfig, JwtService, etc)
   │  ├─ Criptografía (PasswordEncoder)
   │  ├─ Integración con librerías externas
   │  ├─ Utilidades genéricas
   │  └─ Aspectos transversales (logging, etc)
   │
   ├─ ❌ NO VA AQUÍ:
   │  ├─ Lógica de negocio
   │  ├─ @RestController
   │  ├─ @Entity
   │  ├─ Repositorios específicos
   │  ├─ Mappers de entidades
   │  └─ Use cases
   │
   ├─ exceptions/
   │  │
   │  ├─ ✅ AQUÍ VA:
   │  │  ├─ GlobalExceptionHandler.java ✅
   │  │  ├─ @ControllerAdvice ✅
   │  │  ├─ @ExceptionHandler ✅
   │  │  ├─ ErrorResponse DTO ✅
   │  │  └─ Manejo centralizado de errores ✅
   │  │
   │  ├─ ❌ NO VA AQUÍ:
   │  │  ├─ Lanzamiento de excepciones (en domain)
   │  │  ├─ Validaciones (en DTO)
   │  │  └─ Lógica específica (en use cases)
   │  │
   │  └─ Ejemplo:
   │     ├─ GlobalExceptionHandler.java
   │     ├─ user/
   │     │  └─ UserNotFoundException.java
   │     └─ ErrorResponse.java
   │
   └─ security/
      │
      ├─ ✅ AQUÍ VA:
      │  ├─ SecurityConfig.java (@Configuration) ✅
      │  ├─ JwtService.java (@Component) ✅
      │  ├─ BcryptPasswordService.java (@Component) ✅
      │  ├─ TokenRevocationService.java (@Component) ✅
      │  ├─ AuthenticationFacadeAdapter.java (@Component) ✅
      │  ├─ Generación de JWT ✅
      │  ├─ Validación de JWT ✅
      │  ├─ Encriptación de contraseñas ✅
      │  └─ Configuración de filtros ✅
      │
      ├─ ❌ NO VA AQUÍ:
      │  ├─ AuthController (va en adapters/in/security)
      │  ├─ JwtAuthFilter (va en adapters/in/security)
      │  ├─ UserDetailsService (va en adapters/in/security)
      │  ├─ DTOs (van en adapters/in/security/dto)
      │  └─ Persistencia de tokens (va en adapters/out)
      │
      └─ Ejemplo:
         ├─ SecurityConfig.java
         ├─ JwtService.java
         ├─ BcryptPasswordService.java
         ├─ TokenRevocationService.java
         └─ AuthenticationFacadeAdapter.java
```

---

## 📊 TABLA DE DECISIÓN RÁPIDA

| Clase/Interface | ¿Dónde va? | Por qué |
|---|---|---|
| `User`, `Product`, `Order` | `core/model` | Entidades puras |
| `CartUseCasePort` | `core/ports/in` | Contrato de entrada |
| `CartRepositoryPort` | `core/ports/out` | Contrato de salida |
| `CartUseCaseImpl` | `application/usecases` | Implementación de caso de uso |
| `CartController` | `adapters/in/rest` | Entrada HTTP |
| `CartRestMapper` | `adapters/in/rest/mappers` | Mapeo DTO ↔ Domain |
| `CartRequest`, `CartResponse` | `adapters/in/rest/dtos` | DTOs de presentación |
| `CartEntity` | `adapters/out/persistence` | Entidad JPA |
| `CartPersistenceMapper` | `adapters/out/persistence` | Mapeo Entity ↔ Domain |
| `CartPersistenceRepository` | `adapters/out/persistence` | JPA Repository |
| `CartRepositoryAdapter` | `adapters/out/persistence` | Implementación del puerto OUT |
| `AuthController` | `adapters/in/security` | Entrada de seguridad |
| `JwtAuthFilter` | `adapters/in/security` | Filtro de seguridad |
| `JwtService` | `infrastructure/security` | Servicio técnico de JWT |
| `SecurityConfig` | `infrastructure/security` | Configuración de Spring |
| `GlobalExceptionHandler` | `infrastructure/exceptions` | Manejo global de errores |

---

## 🔄 FLUJO DE ACTUALIZACIÓN: Dónde modificar para cada cambio

```
┌─────────────────────────────────────────────────────┐
│ Si necesito cambiar la LÓGICA DE NEGOCIO            │
├─────────────────────────────────────────────────────┤
│ 1. Modificar entidad en core/model/                 │
│ 2. Actualizar caso de uso en application/usecases/  │
│ 3. Actualizar mappers si es necesario               │
│ 4. Recompilar y probar                              │
│ ⚠️ NO tocar controladores ni persistencia           │
└─────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────┐
│ Si necesito cambiar la PRESENTACIÓN (REST API)      │
├─────────────────────────────────────────────────────┤
│ 1. Modificar DTOs en adapters/in/rest/dtos/         │
│ 2. Actualizar mapper en adapters/in/rest/mappers/   │
│ 3. Actualizar controller en adapters/in/rest/       │
│ 4. Validar con @jakarta.validation.Valid            │
│ ⚠️ NO tocar lógica de dominio                       │
└─────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────┐
│ Si necesito cambiar la PERSISTENCIA                 │
├─────────────────────────────────────────────────────┤
│ 1. Modificar Entity en adapters/out/persistence/    │
│ 2. Actualizar mapper en adapters/out/persistence/   │
│ 3. Actualizar repository queries                    │
│ 4. Actualizar adapter si cambia la interface       │
│ ⚠️ NO tocar casos de uso ni dominio                │
└─────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────┐
│ Si necesito cambiar la AUTENTICACIÓN                │
├─────────────────────────────────────────────────────┤
│ 1. AuthController en adapters/in/security/          │
│ 2. JwtAuthFilter en adapters/in/security/           │
│ 3. JwtService en infrastructure/security/           │
│ 4. SecurityConfig en infrastructure/security/       │
│ ⚠️ NO tocar el dominio de usuarios                 │
└─────────────────────────────────────────────────────┘
```

---

## ✅ CHECKLIST DE COLOCACIÓN CORRECTA

Cuando crees una nueva clase, verifica:

```
□ ¿Es una entidad de dominio?
  → core/model/[modulo]/
  
□ ¿Es un puerto (interfaz)?
  → core/ports/in/ o core/ports/out/
  
□ ¿Implementa un puerto de entrada?
  → application/usecases/
  
□ ¿Es un @RestController?
  → adapters/in/rest/[modulo]/
  
□ ¿Es un DTO de presentación?
  → adapters/in/rest/[modulo]/dtos/
  
□ ¿Es un mapper REST?
  → adapters/in/rest/[modulo]/mappers/
  
□ ¿Es un @Entity JPA?
  → adapters/out/persistence/[modulo]/
  
□ ¿Es un mapper de persistencia?
  → adapters/out/persistence/[modulo]/
  
□ ¿Es un JpaRepository?
  → adapters/out/persistence/[modulo]/
  
□ ¿Implementa un puerto de salida?
  → adapters/out/persistence/[modulo]/
  
□ ¿Es configuración de Spring?
  → infrastructure/
  
□ ¿Es un servicio técnico?
  → infrastructure/[categoria]/
```

---

## 🎓 CONCLUSIÓN

Siguiendo esta estructura:
✅ Cada clase tiene un propósito claro
✅ La arquitectura es mantenible
✅ Los cambios están localizados
✅ Fácil de entender para nuevos desarrolladores
✅ Cumple con arquitectura hexagonal
✅ Cumple con DDD

