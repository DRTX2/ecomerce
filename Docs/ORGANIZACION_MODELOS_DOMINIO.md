# Organización de Modelos del Dominio

## ✅ Estado: ARQUITECTURA HEXAGONAL CUMPLIDA

### 🎯 Corrección Aplicada
Se eliminó la dependencia de `jakarta.persistence.EntityNotFoundException` del dominio y se creó `EntityNotFoundException` propia en `core.model.exceptions`, cumpliendo así con el principio de inversión de dependencias de la arquitectura hexagonal.

---

## 📦 Módulos del Dominio (core/model)

### 1️⃣ **PRODUCTO (product)** - Catálogo y Reviews
```
📁 core/model/product/
   ├── Product.java          → Producto del catálogo (nombre, descripción, precio, imágenes, rating)
   ├── Category.java         → Categoría de productos
   ├── Inventory.java        → Control de inventario (stock, ubicación, último restock)
   └── Review.java           → Reseñas de productos (rating, comentario, usuario)
```

**Orden de revisión:**
1. `Category` (base, sin dependencias)
2. `Product` (depende de Category)
3. `Inventory` (depende de Product)
4. `Review` (depende de Product y User)

---

### 2️⃣ **USUARIO (user)** - Gestión de usuarios
```
📁 core/model/user/
   ├── User.java             → Usuario del sistema (nombre, email, dirección, teléfono, role)
   ├── UserRole.java         → Enum: CUSTOMER, ADMIN, SELLER
   └── Favorite.java         → Productos favoritos del usuario
```

**Orden de revisión:**
1. `UserRole` (enum simple)
2. `User` (entidad principal)
3. `Favorite` (depende de User y Product)

---

### 3️⃣ **CARRITO Y ORDEN (order)** - Proceso de compra
```
📁 core/model/order/
   ├── Cart.java             → Carrito temporal del usuario (items, total)
   ├── CartItem.java         → Item del carrito (producto, cantidad, precio actual)
   ├── Order.java            → Orden confirmada (estado, total, fecha entrega, items)
   ├── OrderItem.java        → Item de la orden (producto, cantidad, precio capturado)
   └── OrderState.java       → Enum: PENDING, PROCESSING, SHIPPED, DELIVERED, CANCELLED
```

**Diferencias clave:**
- **Cart**: Temporal, modificable, se vacía al hacer checkout
- **CartItem**: Precio actual del producto (puede cambiar)
- **Order**: Inmutable, registro histórico, con datos de pago/envío
- **OrderItem**: Precio congelado al momento de la compra

**Orden de revisión:**
1. `OrderState` (enum)
2. `CartItem` (depende de Product)
3. `Cart` (depende de User y CartItem)
4. `OrderItem` (depende de Product)
5. `Order` (depende de User, OrderItem, Payment, Shipping)

---

### 4️⃣ **PAGO (payment)** - Transacciones
```
📁 core/model/payment/
   ├── Payment.java          → Pago asociado a una orden (monto, método, estado, transactionId)
   ├── PaymentMethod.java    → Enum: CREDIT_CARD, DEBIT_CARD, PAYPAL, TRANSFER
   └── PaymentStatus.java    → Enum: PENDING, COMPLETED, FAILED, REFUNDED
```

**Orden de revisión:**
1. `PaymentMethod` (enum)
2. `PaymentStatus` (enum)
3. `Payment` (depende de Order)

---

### 5️⃣ **ENVÍO (shipping)** - Logística
```
📁 core/model/shipping/
   ├── Shipping.java         → Info de envío (dirección, carrier, tracking, fecha estimada)
   └── ShippingStatus.java   → Enum: PENDING, IN_TRANSIT, DELIVERED, RETURNED
```

**Orden de revisión:**
1. `ShippingStatus` (enum)
2. `Shipping` (depende de Order)

---

### 6️⃣ **DEVOLUCIONES (returns)** - Gestión de retornos
```
📁 core/model/returns/
   ├── Return.java           → Solicitud de devolución (orden, razón, estado)
   └── ReturnStatus.java     → Enum: REQUESTED, APPROVED, REJECTED, COMPLETED
```

**Orden de revisión:**
1. `ReturnStatus` (enum)
2. `Return` (depende de Order y User)

---

### 7️⃣ **DESCUENTOS (discount)** - Promociones
```
📁 core/model/discount/
   ├── Discount.java         → Descuento/cupón (código, valor, tipo, fechas validez)
   └── DiscountType.java     → Enum: PERCENTAGE, FIXED_AMOUNT, FREE_SHIPPING
```

**Orden de revisión:**
1. `DiscountType` (enum)
2. `Discount` (puede relacionarse con Product u Order)

---

### 8️⃣ **INCIDENCIAS (issues)** - Reportes y apelaciones
```
📁 core/model/issues/
   ├── Incidence.java        → Incidencia reportada (descripción, estado, decisión)
   ├── IncidenceStatus.java  → Enum: OPEN, IN_REVIEW, RESOLVED, CLOSED
   ├── IncidenceDecision.java→ Enum: PENDING, APPROVED, REJECTED
   ├── Appeal.java           → Apelación a una incidencia (justificación, estado)
   ├── AppealStatus.java     → Enum: SUBMITTED, UNDER_REVIEW, RESOLVED
   ├── AppealDecision.java   → Enum: PENDING, APPROVED, REJECTED
   ├── Report.java           → Reporte de contenido/usuario (razón, fuente)
   └── ReportSource.java     → Enum: PRODUCT, REVIEW, USER
```

**Orden de revisión:**
1. `IncidenceStatus`, `IncidenceDecision` (enums)
2. `Incidence` (depende de User, Order o Product)
3. `AppealStatus`, `AppealDecision` (enums)
4. `Appeal` (depende de Incidence)
5. `ReportSource` (enum)
6. `Report` (depende del tipo de fuente)

---

### 9️⃣ **NOTIFICACIONES (notifications)** - Alertas al usuario
```
📁 core/model/notifications/
   ├── Notification.java     → Notificación al usuario (mensaje, tipo, estado)
   ├── NotificationType.java → Enum: ORDER_UPDATE, SHIPPING, PROMOTION, ALERT
   └── NotificationStatus.java→ Enum: UNREAD, READ
```

**Orden de revisión:**
1. `NotificationType`, `NotificationStatus` (enums)
2. `Notification` (depende de User)

---

### 🔟 **PROVEEDOR (supplier)** - Gestión de proveedores
```
📁 core/model/supplier/
   └── Supplier.java         → Proveedor de productos (nombre, contacto, dirección)
```

**Orden de revisión:**
1. `Supplier` (puede relacionarse con Product)

---

### 1️⃣1️⃣ **SEGURIDAD (security)** - Autenticación
```
📁 core/model/security/
   └── Token.java            → Token JWT (usuario, token, expiración)
```

**Orden de revisión:**
1. `Token` (depende de User)

---

### 1️⃣2️⃣ **EXCEPCIONES (exceptions)** - Manejo de errores del dominio
```
📁 core/model/exceptions/
   ├── DomainException.java          → Excepción base del dominio
   └── EntityNotFoundException.java  → Excepción cuando no se encuentra una entidad
```

**✅ ARQUITECTURA HEXAGONAL:** Estas excepciones son propias del dominio y NO dependen de frameworks externos (Jakarta, Spring, etc.)

---

## 🔄 Orden Completo de Revisión por Dependencias

### **Nivel 1 - Sin dependencias (Enums y Value Objects)**
1. UserRole
2. OrderState
3. PaymentMethod, PaymentStatus
4. ShippingStatus
5. ReturnStatus
6. DiscountType
7. IncidenceStatus, IncidenceDecision, AppealStatus, AppealDecision, ReportSource
8. NotificationType, NotificationStatus

### **Nivel 2 - Entidades básicas**
9. User
10. Category
11. Supplier

### **Nivel 3 - Productos y relacionados**
12. Product (depende: Category)
13. Inventory (depende: Product)
14. Review (depende: Product, User)
15. Discount (puede depender: Product)
16. Favorite (depende: Product, User)

### **Nivel 4 - Carrito**
17. CartItem (depende: Product)
18. Cart (depende: User, CartItem)

### **Nivel 5 - Orden y transacciones**
19. OrderItem (depende: Product)
20. Payment (depende: Order - ver nota*)
21. Shipping (depende: Order - ver nota*)
22. Order (depende: User, OrderItem, Payment, Shipping)

*Nota: Order, Payment y Shipping tienen referencias circulares, pero se resuelven con IDs en lugar de objetos completos.

### **Nivel 6 - Post-orden**
23. Return (depende: Order, User)
24. Notification (depende: User)
25. Token (depende: User)

### **Nivel 7 - Incidencias**
26. Report (depende: Product/Review/User)
27. Incidence (depende: User, Order o Product)
28. Appeal (depende: Incidence)

---

## 🎯 Resumen de Correcciones Aplicadas

### ✅ Arquitectura Hexagonal Cumplida:
1. **Creada excepción del dominio**: `EntityNotFoundException` en `core.model.exceptions`
2. **Eliminada dependencia de Jakarta**: Todos los ports y adaptadores ahora usan la excepción del dominio
3. **GlobalExceptionHandler actualizado**: Maneja ambas excepciones (dominio + Jakarta) para compatibilidad
4. **Compilación exitosa**: ✅ BUILD SUCCESSFUL

### 📊 Archivos modificados:
- ✅ `core/model/exceptions/EntityNotFoundException.java` (creado)
- ✅ `core/ports/out/persistence/CategoryRepositoryPort.java`
- ✅ `adapters/out/persistence/category/CategoryRepositoryAdapter.java`
- ✅ `adapters/out/persistence/payment/PaymentRepositoryAdapter.java`
- ✅ `adapters/out/persistence/cart/CartRepositoryAdapter.java`
- ✅ `adapters/out/persistence/order/OrderRepositoryAdapter.java`
- ✅ `adapters/out/persistence/product/ProductRepositoryAdapter.java`
- ✅ `adapters/out/persistence/notification/NotificationRepositoryAdapter.java`
- ✅ `adapters/out/persistence/user/UserRepositoryPortAdapter.java`
- ✅ `infrastructure/exceptions/GlobalExceptionHandler.java`

---

## 🔍 Verificación Final

```bash
# NO hay dependencias de Jakarta en el core
grep -r "import jakarta" src/main/java/com/drtx/ecomerce/amazon/core/
# Resultado: ✅ Sin resultados

# Compilación exitosa
./gradlew build -x test
# Resultado: ✅ BUILD SUCCESSFUL
```

---

**Fecha de corrección**: 2024-12-25  
**Estado**: ✅ ARQUITECTURA HEXAGONAL CUMPLIDA

