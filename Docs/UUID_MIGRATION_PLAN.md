# Plan de Migración a UUID en APIs Públicas

## Estado Actual

### ✅ Completado: User Entity
- **UserController**: Migrado a UUID
- **UserUseCasePort**: Actualizado
- **UserRepositoryPort**: Actualizado con métodos UUID
- **UserEntity**: UUID generado automáticamente con @PrePersist
- **UserResponse**: Usa UUID en lugar de Long id
- **Tests**: Actualizados

---

## 🔴 Pendientes de Migración

### 1. **Product** (Alta Prioridad)
**Controlador**: `ProductController.java`
- `GET /products/{id}` → `GET /products/{uuid}`
- `PUT /products/{id}` → `PUT /products/{uuid}`
- `DELETE /products/{id}` → `DELETE /products/{uuid}`

**Archivos a modificar**:
- `ProductEntity.java` - Agregar campo `UUID uuid`
- `Product.java` - Agregar campo `UUID uuid`
- `ProductController.java` - Cambiar `@PathVariable Long id` → `@PathVariable UUID uuid`
- `ProductUseCasePort.java` - Métodos con UUID
- `ProductRepositoryPort.java` - Métodos `findByUuid`, `updateByUuid`, `deleteByUuid`
- `ProductResponse.java` - Cambiar `Long id` → `UUID uuid`
- Tests relacionados

**Razón**: Los productos son recursos públicos expuestos a clientes externos.

---

### 2. **Order** (Alta Prioridad)
**Controlador**: `OrderController.java`
- `GET /orders/{id}` → `GET /orders/{uuid}`
- `PUT /orders/{id}` → `PUT /orders/{uuid}`
- `DELETE /orders/{id}` → `DELETE /orders/{uuid}`
- `PUT /orders/{id}/status` → `PUT /orders/{uuid}/status`
- `PUT /orders/{id}/cancel` → `PUT /orders/{uuid}/cancel`

**Archivos a modificar**:
- `OrderEntity.java` - Agregar campo `UUID uuid`
- `Order.java` - Agregar campo `UUID uuid`
- `OrderController.java` - Cambiar a UUID
- `OrderUseCasePort.java` - Métodos con UUID
- `OrderRepositoryPort.java` - Métodos UUID
- `OrderResponse.java` - UUID en lugar de Long
- Tests relacionados

**Razón**: Las órdenes son recursos sensibles que no deben ser enumerables.

---

### 3. **Category** (Media Prioridad)
**Controlador**: `CategoryController.java`
- `GET /categories/{id}` → `GET /categories/{uuid}`
- `PUT /categories/{id}` → `PUT /categories/{uuid}`
- `DELETE /categories/{id}` → `DELETE /categories/{uuid}`

**Archivos a modificar**:
- `CategoryEntity.java` - Agregar UUID
- `Category.java` - Agregar UUID
- `CategoryController.java` - Cambiar a UUID
- `CategoryUseCasePort.java` - Métodos UUID
- `CategoryRepositoryPort.java` - Métodos UUID
- `CategoryResponse.java` - UUID
- Tests relacionados

**Razón**: Aunque son menos sensibles, mantener consistencia en la API.

---

### 4. **Cart** (Alta Prioridad)
**Controlador**: `CartController.java`
- `GET /carts/{id}` → `GET /carts/{uuid}`
- `PUT /carts/{id}` → `PUT /carts/{uuid}`
- `DELETE /carts/{id}` → `DELETE /carts/{uuid}`

**Archivos a modificar**:
- `CartEntity.java` - Agregar UUID
- `Cart.java` - Agregar UUID
- `CartController.java` - Cambiar a UUID
- `CartUseCasePort.java` - Métodos UUID
- `CartRepositoryPort.java` - Métodos UUID
- `CartResponse.java` - UUID
- Tests relacionados

**Razón**: Carritos de compra contienen información sensible del usuario.

---

### 5. **Appeal** (Media Prioridad)
**Controlador**: `AppealController.java`
- `GET /appeals/{id}` → `GET /appeals/{uuid}`
- `PUT /appeals/{id}/resolve` → `PUT /appeals/{uuid}/resolve`

**Archivos a modificar**:
- `AppealEntity.java` - Agregar UUID
- `Appeal.java` - Agregar UUID
- `AppealController.java` - Cambiar a UUID
- `AppealUseCasePort.java` - Métodos UUID
- `AppealRepositoryPort.java` - Métodos UUID
- `AppealResponse.java` - UUID
- Tests relacionados

---

### 6. **Incidence** (Media Prioridad)
**Controlador**: `IncidenceController.java`
- `GET /incidences/{id}` → `GET /incidences/{uuid}`
- `PUT /incidences/{id}/resolve` → `PUT /incidences/{uuid}/resolve`

**Archivos a modificar**:
- `IncidenceEntity.java` - Agregar UUID
- `Incidence.java` - Agregar UUID
- `IncidenceController.java` - Cambiar a UUID
- `IncidenceUseCasePort.java` - Métodos UUID
- `IncidenceRepositoryPort.java` - Métodos UUID
- `IncidenceResponse.java` - UUID
- Tests relacionados

---

## 📝 Entidades que NO necesitan UUID público

### 1. **Review**
- **Razón**: Las reviews están subordinadas a productos. Se acceden mediante `/products/{productUuid}/reviews`
- **Recomendación**: Puede seguir usando Long id interno

### 2. **CartItem** / **OrderItem**
- **Razón**: Son entidades subordinadas a Cart/Order respectivamente
- **Recomendación**: Long id interno es suficiente

### 3. **RefreshToken** / **RevokedToken**
- **Razón**: Tokens de seguridad que se identifican por el token string, no por ID
- **Recomendación**: Long id interno

### 4. **Shipping** / **Payment**
- **Razón**: Entidades subordinadas a Order
- **Recomendación**: Long id interno, acceso mediante `/orders/{orderUuid}/shipping`

---

## 🔄 Orden de Implementación Recomendado

1. ✅ **User** - COMPLETADO
2. **Product** - Recurso público más importante
3. **Order** - Información sensible
4. **Cart** - Información sensible
5. **Category** - Consistencia de API
6. **Appeal** - Moderación
7. **Incidence** - Moderación

---

## 🛠️ Template de Migración

Para cada entidad que migres, sigue estos pasos:

### Paso 1: Entidad JPA
```java
@Column(unique = true, nullable = false, updatable = false)
private UUID uuid;

@PrePersist
protected void onCreate() {
    if (this.uuid == null) {
        this.uuid = UUID.randomUUID();
    }
}
```

### Paso 2: Modelo de Dominio
```java
@Value
@Builder
public class EntityName {
    Long id;        // Mantener para uso interno
    UUID uuid;      // Para API pública
    // ... otros campos
}
```

### Paso 3: Repository
```java
// JPA Repository
Optional<EntityNameEntity> findByUuid(UUID uuid);

// Port
Optional<EntityName> findByUuid(UUID uuid);
EntityName updateByUuid(UUID uuid, EntityName entity);
void deleteByUuid(UUID uuid);
```

### Paso 4: Use Case Port
```java
Optional<EntityName> getByUuid(UUID uuid);
EntityName updateByUuid(UUID uuid, EntityName entity);
void deleteByUuid(UUID uuid);
```

### Paso 5: Controller
```java
@GetMapping("/{uuid}")
public ResponseEntity<EntityResponse> getById(@PathVariable UUID uuid) {
    return useCasePort.getByUuid(uuid)
        .map(mapper::toResponse)
        .map(ResponseEntity::ok)
        .orElse(ResponseEntity.notFound().build());
}
```

### Paso 6: Response DTO
```java
public record EntityResponse(
    UUID uuid,      // NO exponer Long id
    String name,
    // ... otros campos
) {}
```

### Paso 7: Tests
```java
UUID testUuid = UUID.randomUUID();
when(useCasePort.getByUuid(testUuid)).thenReturn(...);
```

---

## 📊 Beneficios de la Migración

1. **Seguridad**: No se puede enumerar recursos
2. **Privacidad**: No revela cantidad de registros
3. **Escalabilidad**: Permite sistemas distribuidos
4. **Estándares**: Mejores prácticas de la industria
5. **Compatibilidad**: Facilita integraciones externas

---

## ⚠️ Consideraciones

### Migración de Base de Datos
Si ya tienes datos en producción, necesitarás:
```sql
-- Agregar columna UUID
ALTER TABLE entity_name ADD COLUMN uuid UUID;

-- Generar UUIDs para registros existentes
UPDATE entity_name SET uuid = gen_random_uuid() WHERE uuid IS NULL;

-- Hacer columna NOT NULL y UNIQUE
ALTER TABLE entity_name ALTER COLUMN uuid SET NOT NULL;
ALTER TABLE entity_name ADD CONSTRAINT entity_name_uuid_unique UNIQUE (uuid);
```

### Versionamiento de API
Si tu API ya está en producción:
- Considera crear `/api/v2/` con UUIDs
- Mantener `/api/v1/` con Long IDs temporalmente
- Deprecar v1 gradualmente

### Performance
- UUIDs ocupan más espacio que Long (16 bytes vs 8 bytes)
- Índices en UUID son ligeramente más lentos
- En aplicaciones modernas, la diferencia es negligible

---

## 📅 Cronograma Sugerido

- **Semana 1**: Product + Order
- **Semana 2**: Cart + Category
- **Semana 3**: Appeal + Incidence
- **Semana 4**: Testing completo + Documentación API

---

## ✅ Checklist por Entidad

- [ ] Agregar campo UUID a Entity
- [ ] Agregar @PrePersist para generación automática
- [ ] Actualizar modelo de dominio
- [ ] Agregar método findByUuid en Repository
- [ ] Implementar updateByUuid y deleteByUuid
- [ ] Actualizar Use Case Port
- [ ] Actualizar Use Case Implementation
- [ ] Actualizar Controller endpoints
- [ ] Actualizar Response DTO
- [ ] Actualizar Request DTO (si aplica)
- [ ] Actualizar Tests unitarios
- [ ] Actualizar Tests de integración
- [ ] Actualizar documentación API
- [ ] Migración de base de datos (si aplica)


