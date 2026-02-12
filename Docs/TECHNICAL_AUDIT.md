# Auditoría Técnica del Proyecto Amazon E-commerce

## 1. Análisis por Módulo

### core (Dominio)

El núcleo del dominio está generalmente bien aislado, pero presenta inconsistencias en la completitud de los módulos.

- **Funcionalidades Incompletas (Zombie Code)**: Se detectaron modelos y persistencia para `shipping`, `returns`, `supplier`, `payment` y `discount` que carecen de Casos de Uso (Application Layer) y Controladores (Adapters Layer). Esto representa código muerto o características a medio implementar.
- **Modelado**: El modelo de `Order` y `OrderItems` es correcto, pero la creación de `Order` en el Caso de Uso (`OrderUseCaseImpl`) utiliza un constructor gigante con muchos parámetros `null`, lo cual es frágil (`Code Smell: Telescoping Constructor`). Se recomienda usar el patrón Buider.

### application (Casos de Uso)

- **Implementación Hexagonal**: Correcta en su mayoría. Los casos de uso implementan interfaces de puerto de entrada (`AuthUseCasePort`, `OrderUseCasePort`) y utilizan puertos de salida.
- **Manejo de Excepciones**: Uso consistente de `DomainExceptionFactory` en módulos clave (`Order`, `Incidence`), lo cual es una buena práctica.
- **Auth**: La lógica de autenticación (`AuthService`) es clara y respeta SRP. Sin embargo, mezcla la responsabilidad de usar `RefreshTokenService` directamente en lugar de a través de un puerto si se considerara un servicio de dominio externo (aunque como servicio de aplicación interno es aceptable).

### adapters (Infraestructura y Entrada/Salida)

- **Controladores (Web)**:
  - `AuthController`: Realiza extracción manual del token (`replace("Bearer ", "")`). Esto debería delegarse a un `TokenExtractor` utilitario o manejarse vía filtro si es para lógica de negocio.
  - Mapeo manual en `toAuthResponse`. Aunque simple, podría beneficiarse de un Mapper como en otros módulos.
- **Persistencia**:
  - **Violación Crítica de Rendimiento (N+1)**: En `OrderRepositoryAdapter.findAll`, se recuperan todas las entidades y se mapean al dominio. Dado que `OrderEntity` tiene una relación `@OneToMany` con `items` (Lazy), el mapper invoca `getItems()` para cada orden, disparando una consulta adicional por cada orden recuperada.
  - **Ineficiencia en Actualizaciones**: El método `updateById` en `OrderRepositoryAdapter` limpia la lista de items (`clear()`) y agrega los nuevos (`addAll()`). Esto fuerza a Hibernate a borrar y reinsertar registros innecesariamente, afectando el rendimiento en órdenes grandes.

### infrastructure (Configuración)

- **Seguridad**: Configuración robusta con `SecurityConfig`, CORS externalizado y CSRF deshabilitado para API Stateless.
- **Observabilidad**: Actuator está habilitado y expone `health`, `info`, `mappings`.

---

## 2. Seguridad

| Aspecto                | Estado    | Observación                                                                      |
| :--------------------- | :-------- | :------------------------------------------------------------------------------- |
| **Autenticación**      | ✅ Bien   | Implementación Statelees con JWT y Refresh Tokens.                               |
| **Autorización**       | ✅ Bien   | Uso de `EnableMethodSecurity` y roles en JWT.                                    |
| **Manejo de Secretos** | ✅ Bien   | Uso de variables de entorno para claves JWT y credenciales.                      |
| **Dependencias**       | ✅ Bien   | Plugin OWASP Dependency Check presente en el build.                              |
| **Protección**         | ⚠️ Mejora | Falta Rate Limiting explícito en los endpoints de Auth (riesgo de fuerza bruta). |
| **Validación**         | ✅ Bien   | Uso de `@Valid` en DTOs de entrada.                                              |

---

## 3. Persistencia y Rendimiento

- **🔴 Problema N+1 Detectado**:
  - **Ubicación**: `OrderRepositoryAdapter.findAll()` y `findByUserId`.
  - **Causa**: Mapeo de Entidad a Dominio fuera de una transacción con `JOIN FETCH` explícito.
  - **Impacto**: Degradación severa del rendimiento al listar órdenes.
  - **Solución**: Implementar `@EntityGraph(attributePaths = "items")` en el repositorio JPA o usar consultas JPQL con `JOIN FETCH`.

- **Manejo de Transacciones**:
  - Se utiliza `@Transactional` correctamente en la capa de aplicación (`OrderUseCaseImpl`, `AuthService`).
  - **Optimización**: Faltan transacciones de solo lectura (`@Transactional(readOnly = true)`) en métodos de consulta (`Get methods`) para optimización de rendimiento en base de datos.

- **Paginación**:
  - Implementada correctamente en el módulo de `Product` usando `PageRequest` y Specification. Falta extenderlo a `Order` y otros listados que devuelven `List` completa (riesgo de escalabilidad).

---

## 4. Arquitectura

- **Cumplimiento Hexagonal**: Alto. La separación de capas, puertos y adaptadores es evidente y se respeta en los módulos principales.
- **Code Smells**:
  - **Logging**: Uso de `System.out.println` en `SecurityConfig` y `AuthController`. Esto es **inaceptable** en producción (no rota, no tiene niveles, impacta rendimiento). Debe reemplazarse por SLF4J.
  - **Código Muerto**: Módulos completos en `core/model` y `persistence` (Shipping, Return, Supplier) sin lógica de negocio ni API expuesta.

---

## 5. Testing

- **Cobertura**: Existe infraestructura de tests (`src/test/java`) con integración (JUnit 5, MockMvc).
- **Configuración**: Jacoco configurado con umbral del 60%.
- **Faltantes**:
  - Tests específicos de concurrencia para el stock o actualizaciones de estado de órdenes.
  - Tests de arquitectura (ej. ArchUnit) para asegurar que no se violen las reglas de dependencia (ej. Dominio dependiendo de Infraestructura).

---

## 6. Calidad Empresarial

Para llevar el proyecto a nivel Senior y Production-Ready:

### 🔴 Crítico (Inmediato)

1. **Eliminar N+1 Queries**: Corregir `OrderRepositoryAdapter` para usar `JOIN FETCH` en los items.
2. **Logging Estructurado**: Eliminar todos los `System.out.println` y configurar Logback/Log4j correctamente (JSON logs para producción).
3. **Paginación Global**: No permitir endpoints que devuelvan `findAll()` (como `getAllOrders`) sin paginación. Es un riesgo de denegación de servicio.

### 🟠 Alto (Corto Plazo)

1. **Limpiar Código Zombie**: Eliminar o implementar completamente los módulos de Shipping, Returns, etc.
2. **Optimizar Actualizaciones**: Reescribir la lógica de actualización de órdenes para no borrar/reinsertar items, sino detectar cambios.
3. **Rate Limiting**: Implementar Bucket4j o similar para proteger endpoints de Login/Register.

### 🟡 Medio (Mejora Continua)

1. **Javadoc / OpenAPI**: Generar documentación de API (Swagger/OpenAPI) para facilitar el consumo por el frontend.
2. **Estrategia de Versionado**: Definir estrategia para `/api/v1` en controladores (actualmente hardcoded o en properties).
3. **Health Checks Avanzados**: Añadir chequeos de dependencia (DB, Redis, Azure Storage) en el endpoint de actuator.

### 🟢 Calidad

1. **Linter/Formatter**: Integrar Spotless o Checkstyle en el build de Gradle para asegurar formato consistente.
2. **ArchUnit**: Añadir tests para prevenir violaciones de arquitectura hexagonal futuras.
