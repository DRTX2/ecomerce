Entidades con sus propiedades y métodos clave:

### 1. **Usuario (User)**
**Atributos:**
- `id` (UUID)
- `nombre` (String)
- `email` (String)
- `contraseña` (String, encriptada)
- `dirección` (String)
- `teléfono` (String)
- `rol` (Enum: Cliente, Administrador)
- `historial_pedidos` (Lista de Órdenes)

**Métodos:**
- `registrarse(nombre, email, contraseña)`
- `iniciar_sesion(email, contraseña)`
- `actualizar_perfil(datos)`
- `cambiar_contraseña(antigua, nueva)`
- `ver_historial_pedidos()`
- `cerrar_sesion()`

---  

### 2. **Producto (Product)**
**Atributos:**
- `id` (UUID)
- `nombre` (String)
- `descripción` (String)
- `precio` (Decimal)
- `stock` (Entero)
- `categoría` (Categoría)
- `calificación_promedio` (Float)
- `imágenes` (Lista de URLs)

**Métodos:**
- `actualizar_precio(nuevo_precio)`
- `actualizar_stock(nueva_cantidad)`
- `asignar_categoría(categoría)`
- `obtener_reseñas()`

---  

### 3. **Orden (Order)**
**Atributos:**
- `id` (UUID)
- `usuario` (Usuario)
- `productos` (Lista de Productos con cantidad)
- `total` (Decimal)
- `estado` (Enum: Pendiente, Enviado, Entregado, Cancelado)
- `fecha_creación` (Fecha)
- `fecha_entrega` (Fecha)
- `método_pago` (Pago)

**Métodos:**
- `calcular_total()`
- `cambiar_estado(nuevo_estado)`
- `cancelar_orden()`
- `generar_factura()`

---  

### 4. **Carrito de Compras (Cart)**
**Atributos:**
- `id` (UUID)
- `usuario` (Usuario)
- `productos` (Lista de Productos con cantidad)

**Métodos:**
- `agregar_producto(producto, cantidad)`
- `eliminar_producto(producto)`
- `vaciar_carrito()`
- `calcular_total()`
- `convertir_a_orden()`

---  

### 5. **Pago (Payment)**
**Atributos:**
- `id` (UUID)
- `orden` (Orden)
- `monto` (Decimal)
- `estado` (Enum: Pendiente, Completado, Fallido)
- `método` (Enum: Tarjeta, PayPal, Transferencia)
- `fecha_pago` (Fecha)

**Métodos:**
- `procesar_pago()`
- `reembolsar_pago()`

---  

### 6. **Envío (Shipping)**
**Atributos:**
- `id` (UUID)
- `orden` (Orden)
- `dirección_entrega` (String)
- `estado_envío` (Enum: Preparando, Enviado, Entregado)
- `empresa_transportista` (String)
- `número_seguimiento` (String)

**Métodos:**
- `actualizar_estado(nuevo_estado)`
- `obtener_detalles_envío()`

---  

### 7. **Inventario (Inventory)**
**Atributos:**
- `id` (UUID)
- `producto` (Producto)
- `cantidad_disponible` (Entero)
- `ubicación_almacén` (String)

**Métodos:**
- `actualizar_stock(nueva_cantidad)`
- `verificar_disponibilidad()`

---  

### 8. **Reseñas y Calificaciones (Review & Rating)**
**Atributos:**
- `id` (UUID)
- `usuario` (Usuario)
- `producto` (Producto)
- `calificación` (Entero de 1 a 5)
- `comentario` (String)
- `fecha` (Fecha)

**Métodos:**
- `agregar_reseña(calificación, comentario)`
- `editar_reseña(nueva_calificación, nuevo_comentario)`
- `eliminar_reseña()`

---  

### 9. **Categoría (Category)**
**Atributos:**
- `id` (UUID)
- `nombre` (String)
- `descripción` (String)
- `productos` (Lista de Productos)

**Métodos:**
- `agregar_producto(producto)`
- `eliminar_producto(producto)`

---  

### 10. **Proveedor (Supplier/Vendor)**
**Atributos:**
- `id` (UUID)
- `nombre` (String)
- `contacto` (String)
- `productos_suministrados` (Lista de Productos)

**Métodos:**
- `agregar_producto(producto)`
- `actualizar_información(contacto_nuevo)`

---  

### 11. **Descuentos y Promociones (Discounts & Promotions)**
**Atributos:**
- `id` (UUID)
- `código` (String)
- `tipo` (Enum: Porcentaje, Fijo)
- `valor` (Decimal)
- `productos_aplicables` (Lista de Productos)
- `fecha_expiración` (Fecha)

**Métodos:**
- `validar_descuento()`
- `aplicar_descuento(precio_original)`

---  

### 12. **Autenticación y Autorización (Auth & Roles)**
**Atributos:**
- `usuario` (Usuario)
- `token_sesión` (String)
- `rol` (Enum: Cliente, Administrador)

**Métodos:**
- `generar_token(usuario)`
- `verificar_credenciales(email, contraseña)`
- `revocar_token(usuario)`

---  

### 13. **Historial de Compras (Order History)**
**Atributos:**
- `usuario` (Usuario)
- `pedidos` (Lista de Órdenes)

**Métodos:**
- `obtener_historial()`
- `buscar_pedido(id_pedido)`

---  

### 14. **Notificaciones (Notifications)**
**Atributos:**
- `id` (UUID)
- `usuario` (Usuario)
- `mensaje` (String)
- `fecha` (Fecha)
- `tipo` (Enum: Pedido, Envío, Promoción)
- `estado` (Enum: Leído, No Leído)

**Métodos:**
- `enviar_notificación(usuario, mensaje, tipo)`
- `marcar_como_leído()`

---  

### 15. **Soporte y Devoluciones (Support & Returns)**
**Atributos:**
- `id` (UUID)
- `usuario` (Usuario)
- `orden` (Orden)
- `motivo` (String)
- `estado` (Enum: En revisión, Aprobado, Rechazado)

**Métodos:**
- `solicitar_devolución(motivo)`
- `actualizar_estado(nuevo_estado)`

---

## Proximamente

Colas de mensajes

Contadores atómicos

Rate limiters

Sesiones de usuario

Caches de búsqueda

Notificaciones en tiempo real