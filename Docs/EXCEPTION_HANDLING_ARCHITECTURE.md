# Manejo de Excepciones - Arquitectura Hexagonal (Nivel Senior)

## 📋 Estructura Modular

La nueva arquitectura de manejo de excepciones está organizada de forma modular y escalable:

```
infrastructure/exceptions/
├── ProblemDetailBuilder.java              # Builder para respuestas de error RFC 7807
└── handlers/                              # Handlers modulares por responsabilidad
    ├── DomainExceptionHandler.java        # Reglas de negocio
    ├── PersistenceExceptionHandler.java   # Errores de base de datos
    ├── SecurityExceptionHandler.java      # Autenticación y autorización
    ├── InfrastructureExceptionHandler.java # Servicios externos (Storage, Email)
    ├── ValidationExceptionHandler.java    # Validación de entrada
    └── FallbackExceptionHandler.java      # Catch-all para excepciones no manejadas

core/model/exceptions/
├── DomainException.java                   # Excepción base del dominio
├── EntityNotFoundException.java           # Entidad no encontrada
├── StorageException.java                  # Errores de almacenamiento
├── NotificationException.java             # Errores de notificación
└── DomainExceptionFactory.java           # 🆕 Factory para crear excepciones
```

## 🎯 Ventajas de la Nueva Arquitectura

### 1. **Modularidad**
- Cada handler maneja un tipo específico de excepciones
- Fácil agregar nuevos handlers sin modificar los existentes
- Responsabilidad única (SOLID)

### 2. **Escalabilidad**
- Nuevas excepciones de dominio → `DomainExceptionHandler`
- Nuevos servicios externos → `InfrastructureExceptionHandler`
- Sin modificar `GlobalExceptionHandler` monolítico

### 3. **DRY (Don't Repeat Yourself)**
- `ProblemDetailBuilder` elimina código repetitivo
- `DomainExceptionFactory` centraliza la creación de excepciones

### 4. **Testabilidad**
- Cada handler se puede testear independientemente
- Mock de componentes específicos más fácil

### 5. **Orden de Prioridad**
Los handlers se ejecutan en orden usando `@Order`:
```java
DomainExceptionHandler         → Prioridad HIGHEST
PersistenceExceptionHandler    → Prioridad HIGHEST + 1
SecurityExceptionHandler       → Prioridad HIGHEST + 2
InfrastructureExceptionHandler → Prioridad HIGHEST + 3
ValidationExceptionHandler     → Prioridad HIGHEST + 4
FallbackExceptionHandler       → Prioridad LOWEST (catch-all)
```

## 🔧 Uso del Exception Factory

### ❌ Antes (Repetitivo y propenso a errores)
```java
throw new EntityNotFoundException("Product with id '" + productId + "' not found");
throw new DomainException("Password cannot contain the email address");
throw new StorageException("Failed to upload image: " + fileName, ex);
```

### ✅ Ahora (Limpio y estandarizado)
```java
throw DomainExceptionFactory.productNotFound(productId);
throw DomainExceptionFactory.passwordCannotContainEmail();
throw DomainExceptionFactory.imageUploadFailed(fileName, ex);
```

## 🔧 Uso del ProblemDetail Builder

### ❌ Antes (Código repetitivo)
```java
ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
    HttpStatus.NOT_FOUND, 
    "Product not found");
problemDetail.setTitle("Resource Not Found");
problemDetail.setType(URI.create("https://api.amazon.com/errors/not-found"));
problemDetail.setProperty("timestamp", Instant.now());
problemDetail.setProperty("errorCode", "RESOURCE_NOT_FOUND");
return problemDetail;
```

### ✅ Ahora (Fluido y conciso)
```java
return ProblemDetailBuilder.notFound("Product not found");

// O para casos más complejos:
return ProblemDetailBuilder
    .create(HttpStatus.BAD_REQUEST, "Custom message")
    .withTitle("Custom Title")
    .withType("custom-error")
    .withErrorCode("CUSTOM_ERROR")
    .withProperty("additionalInfo", customData)
    .build();
```

## 📝 Ejemplos de Uso

### En Use Cases (Application Layer)
```java
@Service
public class ProductUseCaseImpl implements ProductUseCasePort {
    
    @Override
    public Product getProductById(Long id) {
        return productRepository.findById(id)
            .orElseThrow(() -> DomainExceptionFactory.productNotFound(id));
    }
    
    @Override
    public void validateStock(Product product, int quantity) {
        if (product.getStock() < quantity) {
            throw DomainExceptionFactory.insufficientStock(
                product.getName(), 
                product.getStock()
            );
        }
    }
}
```

### Agregar Nuevas Excepciones al Factory
```java
// 1. En DomainExceptionFactory.java
public static DomainException invalidQuantity(int quantity) {
    return new DomainException(
        String.format("Invalid quantity: %d. Must be greater than 0", quantity));
}

// 2. En tu Use Case
if (quantity <= 0) {
    throw DomainExceptionFactory.invalidQuantity(quantity);
}
```

### Crear un Nuevo Handler Modular
Si necesitas un handler específico (ej: para excepciones de pagos):

```java
@RestControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE + 5)
public class PaymentExceptionHandler {
    
    @ExceptionHandler(PaymentFailedException.class)
    public ProblemDetail handlePaymentFailed(PaymentFailedException ex) {
        return ProblemDetailBuilder
            .create(HttpStatus.PAYMENT_REQUIRED, ex.getMessage())
            .withTitle("Payment Failed")
            .withType("payment-failed")
            .withErrorCode("PAYMENT_FAILED")
            .build();
    }
}
```

## 🗑️ Archivos Obsoletos (Puedes eliminarlos)

Los siguientes archivos ahora son redundantes:
- ❌ `GlobalExceptionHandler.java` → Reemplazado por handlers modulares
- ❌ `AuthControllerAdvice.java` → Manejado por `SecurityExceptionHandler`

## 🎓 Mejores Prácticas

1. **Siempre usa el Factory** para crear excepciones de dominio
2. **No mezcles lógica de negocio** en los handlers (solo mapeo a HTTP)
3. **Usa logging apropiado** en `FallbackExceptionHandler` para errores inesperados
4. **Mantén los mensajes consistentes** usando el Factory
5. **Agrega tests unitarios** para cada handler

## 📊 Comparación

| Aspecto | Antes (Monolítico) | Ahora (Modular) |
|---------|-------------------|-----------------|
| **Líneas en GlobalExceptionHandler** | 140 | N/A (eliminado) |
| **Archivos de handlers** | 2 (duplicados) | 6 (especializados) |
| **Creación de excepciones** | Manual (repetitiva) | Factory (estandarizada) |
| **Creación de ProblemDetail** | Manual (verbose) | Builder (fluido) |
| **Testabilidad** | Difícil (monolítico) | Fácil (modular) |
| **Escalabilidad** | Limitada | Alta |
| **Mantenibilidad** | Baja | Alta |

## ✅ Checklist de Migración

- [x] Crear `DomainExceptionFactory`
- [x] Crear `ProblemDetailBuilder`
- [x] Crear handlers modulares
- [x] Actualizar `EntityNotFoundException`
- [ ] Refactorizar Use Cases para usar el Factory
- [ ] Eliminar `GlobalExceptionHandler.java`
- [ ] Eliminar `AuthControllerAdvice.java`
- [ ] Agregar tests unitarios para handlers
- [ ] Actualizar documentación del API

---
**Arquitectura:** Hexagonal (Ports & Adapters)  
**Nivel:** Senior  
**Patrón:** Factory + Builder + Separation of Concerns
