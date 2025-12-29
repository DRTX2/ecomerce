# Refactorización del Módulo de Email - Arquitectura Hexagonal

## 📋 Resumen

Se refactorizó el módulo de email para alinearlo completamente con la arquitectura hexagonal del proyecto, separando las plantillas HTML del código Java.

## 🔄 Cambios Realizados

### 1. Capa Core (Dominio)

#### Puerto de Salida
- **Creado**: `EmailPort` en `core.ports.out.notification`
- **Propósito**: Define el contrato para envío de correos sin depender de tecnologías específicas
- **Métodos**:
  - `sendSimpleEmail(String to, String subject, String text)`
  - `sendHtmlEmail(String to, String subject, String htmlContent)`
  - `sendPasswordResetEmail(String to, String token, String name)`
  - `sendWelcomeEmail(String to, String name)`
  - `sendOrderConfirmationEmail(String to, String name, String orderNumber)`

#### Excepción de Dominio
- **Creado**: `NotificationException` en `core.model.exceptions`
- **Propósito**: Encapsular errores de envío de correo como excepción de dominio
- **Beneficio**: Desacopla el dominio de excepciones técnicas (Jakarta Mail, Spring Mail)

### 2. Capa de Infraestructura

#### Adaptador de Email
- **Renombrado**: `EmailService` → `EmailAdapter`
- **Implementa**: `EmailPort`
- **Responsabilidades**:
  - Envío de correos usando `JavaMailSender`
  - Conversión de excepciones técnicas a `NotificationException`
  - Coordinación con `EmailTemplateLoader` para cargar plantillas

#### Cargador de Plantillas
- **Creado**: `EmailTemplateLoader`
- **Responsabilidades**:
  - Cargar archivos HTML desde `src/main/resources/templates/email/`
  - Reemplazar variables con formato `{{variable}}`
  - Manejo de errores de carga de plantillas

#### Plantillas HTML
- **Ubicación**: `src/main/resources/templates/email/`
- **Archivos creados**:
  - `password-reset.html` - Restablecimiento de contraseña
  - `welcome.html` - Bienvenida a nuevos usuarios
  - `order-confirmation.html` - Confirmación de pedidos

### 3. Tests

#### Tests de Integración
- **Renombrado**: `EmailServiceIntegrationTest` → `EmailAdapterIntegrationTest`
- **Actualizado**: Mockeo de `EmailTemplateLoader`
- **Verificaciones**: Incluyen llamadas al template loader

#### Tests Unitarios
- **Creado**: `EmailTemplateLoaderTest`
- **Cobertura**:
  - Carga exitosa de plantillas
  - Reemplazo correcto de variables
  - Manejo de plantillas inexistentes
  - Comportamiento con variables vacías

### 4. Documentación
- **Actualizado**: `README.md` del módulo de email
- **Nuevo contenido**:
  - Sección de arquitectura
  - Guía de uso de plantillas
  - Instrucciones para añadir nuevas plantillas

## 🎯 Beneficios de la Refactorización

### Separación de Responsabilidades
- ✅ **Código Java**: Lógica de negocio y envío
- ✅ **Plantillas HTML**: Diseño y contenido visual
- ✅ **Fácil mantenimiento**: Diseñadores pueden editar HTML sin tocar Java

### Arquitectura Hexagonal
- ✅ **Puerto definido**: `EmailPort` en el core
- ✅ **Adaptador implementado**: `EmailAdapter` en infrastructure
- ✅ **Regla de dependencia**: Core no depende de infrastructure

### Testabilidad
- ✅ **Mockeo simple**: `EmailTemplateLoader` fácil de mockear
- ✅ **Tests independientes**: Template loader tiene sus propios tests
- ✅ **Verificación completa**: Se verifica carga de plantillas y envío

### Extensibilidad
- ✅ **Nuevas plantillas**: Solo crear archivo HTML y llamar al loader
- ✅ **Nuevos proveedores**: Implementar `EmailPort` con otro proveedor
- ✅ **Nuevos formatos**: Agregar soporte para otros formatos de plantillas

## 📁 Estructura Final

```
back/
├── src/main/java/com/drtx/ecomerce/amazon/
│   ├── core/
│   │   ├── model/exceptions/
│   │   │   └── NotificationException.java          [NUEVO]
│   │   └── ports/out/notification/
│   │       └── EmailPort.java                      [NUEVO]
│   └── infrastructure/email/
│       ├── EmailAdapter.java                       [REFACTORIZADO]
│       ├── EmailTemplateLoader.java                [NUEVO]
│       └── README.md                               [ACTUALIZADO]
├── src/main/resources/templates/email/
│   ├── password-reset.html                         [NUEVO]
│   ├── welcome.html                                [NUEVO]
│   └── order-confirmation.html                     [NUEVO]
└── src/test/java/com/drtx/ecomerce/amazon/infrastructure/email/
    ├── EmailAdapterIntegrationTest.java            [REFACTORIZADO]
    ├── EmailTemplateLoaderTest.java                [NUEVO]
    └── EmailTestConfig.java                        [EXISTENTE]
```

## ✅ Verificación

Todos los tests pasan exitosamente:

```bash
./gradlew test --tests "com.drtx.ecomerce.amazon.infrastructure.email.*"
# BUILD SUCCESSFUL
```

## 🔜 Próximos Pasos Sugeridos

1. **Integrar con casos de uso**: Actualizar servicios que usen email para inyectar `EmailPort`
2. **Plantillas adicionales**: Crear plantillas para otros eventos (envío de pedido, cancelación, etc.)
3. **Internacionalización**: Agregar soporte para múltiples idiomas en plantillas
4. **Motor de plantillas**: Considerar usar Thymeleaf o FreeMarker para plantillas más complejas
5. **Caché de plantillas**: Implementar caché para evitar leer archivos en cada envío

## 📝 Notas de Implementación

- Las plantillas usan un sistema simple de reemplazo `{{variable}}`
- El `EmailTemplateLoader` lee archivos desde el classpath
- Las excepciones técnicas se envuelven en `NotificationException`
- Los tests mockean el template loader para evitar dependencias de archivos
