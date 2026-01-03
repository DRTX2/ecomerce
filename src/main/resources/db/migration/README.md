# Base de Datos - Migraciones Flyway

## 📋 Descripción

Este directorio contiene todas las migraciones SQL de la base de datos usando **Flyway** como gestor de migraciones.

## 🏗️ Estructura

Las migraciones siguen la convención estándar de Flyway:

```
db/migration/
├── V1__Create_users_table.sql          # Schema inicial
├── V2__Add_seller_role_if_missing.sql  # Ajustes de datos
├── V3__Add_audit_timestamps.sql        # Funciones y triggers
└── README.md                           # Este archivo
```

### Nomenclatura

- **Prefijo**: `V` (versión)
- **Número**: Número secuencial (ej: `1`, `2`, `3`)
- **Separador**: `__` (doble guión bajo)
- **Descripción**: Nombre descriptivo en snake_case
- **Extensión**: `.sql`

**Ejemplo**: `V1__Create_users_table.sql`

## 🔄 Cómo funcionan las migraciones

1. **Detección**: Flyway detecta archivos SQL en `src/main/resources/db/migration/`
2. **Validación**: Verifica que no haya migraciones duplicadas o con versiones inválidas
3. **Seguimiento**: Lee tabla `flyway_schema_history` para saber qué ya se ejecutó
4. **Ejecución**: Ejecuta solo las nuevas migraciones en orden
5. **Registro**: Registra cada migración con checksum, timestamp y estado

## 📌 Reglas Importantes

### Nunca Modificar Migraciones Ejecutadas

❌ **Incorrecto**:
```sql
-- V1__Create_users_table.sql (YA FUE EJECUTADA)
-- NO MODIFICAR ESTE ARCHIVO
```

✅ **Correcto**:
```sql
-- V2__Modify_users_table.sql (NUEVA MIGRACIÓN)
ALTER TABLE users ADD COLUMN ...
```

### Mantener Idempotencia

Usa `IF EXISTS` / `IF NOT EXISTS` para que migraciones sean seguras:

```sql
-- Seguro - se puede ejecutar múltiples veces
CREATE TABLE IF NOT EXISTS users (...);
ALTER TABLE IF EXISTS users DROP CONSTRAINT IF EXISTS old_constraint;

-- No es seguro
CREATE TABLE users (...);  -- Falla si ya existe
```

### Versionamiento Secuencial

Siempre incrementa el número:
- ✅ V1, V2, V3, V4, ...
- ❌ V1, V2, V4 (saltarse V3)
- ❌ V1, V3, V2 (desordenado)

## 🚀 Ejecutar Migraciones

### Automático (al iniciar la aplicación)

```bash
./gradlew bootRun
```

Flyway se ejecuta automáticamente antes de que Spring Boot inicie.

### Manual con Gradle

```bash
./gradlew flywayMigrate
```

### Ver estado de migraciones

```bash
./gradlew flywayInfo
```

## 📊 Consulta Histórico de Migraciones

```sql
SELECT * FROM flyway_schema_history ORDER BY installed_rank;
```

Output esperado:
```
installed_rank | version | description                  | type | script                              | checksum | installed_by | installed_on          | execution_time | success
1              | 1       | Create users table            | SQL  | V1__Create_users_table.sql          | 123456   | postgres     | 2026-01-02 10:00:00  | 150        | true
2              | 2       | Add seller role if missing    | SQL  | V2__Add_seller_role_if_missing.sql  | 234567   | postgres     | 2026-01-02 10:05:00  | 200        | true
3              | 3       | Add audit timestamps          | SQL  | V3__Add_audit_timestamps.sql        | 345678   | postgres     | 2026-01-02 10:10:00  | 100        | true
```

## 🔧 Configuración en application.yml

```yaml
spring:
  flyway:
    enabled: true
    locations: classpath:db/migration
    baseline-on-migrate: false
    validate-on-migrate: true
    out-of-order: false
    encoding: UTF-8
```

### Propiedades Importantes

| Propiedad | Valor | Descripción |
|-----------|-------|-------------|
| `enabled` | true | Habilita Flyway |
| `locations` | `classpath:db/migration` | Ruta de migraciones |
| `validate-on-migrate` | true | Valida migraciones antes de ejecutar |
| `out-of-order` | false | Requiere versiones secuenciales |
| `baseline-on-migrate` | false | No permite saltarse migraciones antiguas |

## ⚠️ Troubleshooting

### "Migración pendiente sin ejecutar"

```
Migration V2__... pending
```

**Solución**: Ejecutar `./gradlew flywayMigrate`

### "Checksum mismatch"

```
Checksum of Migration V1 does not match the database
```

**Causa**: Se modificó una migración ya ejecutada

**Solución**: 
1. Revertir cambios al archivo SQL original
2. O crear nueva migración para los cambios deseados

### Limpiar historial (PELIGROSO - Solo desarrollo)

```bash
./gradlew flywayClean
```

⚠️ **Advertencia**: Elimina TODA la base de datos. Solo en desarrollo.

## 📚 Buenas Prácticas

### 1. Descripciones Claras

```sql
-- ✅ Bueno
-- V1__Create_users_table.sql
-- Descripción: Crear tabla users con columnas básicas y constraints

-- ❌ Malo
-- V1__Update.sql
```

### 2. Comentarios Explicativos

```sql
-- Crear índice para búsquedas frecuentes por email
CREATE INDEX idx_users_email ON users(email);
```

### 3. Separar cambios de schema y datos

```sql
-- V1__Create_users_table.sql (schema)
-- V2__Populate_initial_data.sql (datos)
-- V3__Add_seller_role.sql (constraint)
```

### 4. Usar IF EXISTS / IF NOT EXISTS

```sql
-- Seguro
ALTER TABLE IF EXISTS users 
DROP CONSTRAINT IF EXISTS old_check;

-- Documentar intención
CREATE TABLE IF NOT EXISTS users (...);
```

## 🔍 Monitoreo

### Tabla de Historial

```sql
SELECT 
    installed_rank,
    version,
    description,
    installed_on,
    execution_time,
    success
FROM flyway_schema_history
ORDER BY installed_rank DESC;
```

### Últimas Migraciones

```sql
SELECT * FROM flyway_schema_history
WHERE success = true
ORDER BY installed_on DESC
LIMIT 5;
```

## 📝 Crear Nueva Migración

1. Crear archivo con patrón: `V<número>__<descripción>.sql`
2. Escribir SQL idempotente
3. Incluir comentarios explicativos
4. Commit a control de versiones
5. Push a repositorio
6. Deploy (Flyway ejecuta automáticamente)

## 🎯 Ejemplo Completo

```sql
-- V4__Add_user_preferences_table.sql
-- Descripción: Crear tabla para preferencias de usuario
-- Relacionada con: V1__Create_users_table.sql

CREATE TABLE IF NOT EXISTS user_preferences (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL UNIQUE,
    theme VARCHAR(50) DEFAULT 'light',
    language VARCHAR(10) DEFAULT 'es',
    notifications_enabled BOOLEAN DEFAULT true,
    
    CONSTRAINT fk_user_preferences_user_id 
        FOREIGN KEY (user_id) 
        REFERENCES users(id) 
        ON DELETE CASCADE,
    
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_user_preferences_user_id ON user_preferences(user_id);

COMMENT ON TABLE user_preferences IS 'Preferencias y configuraciones del usuario';
```

## 🚨 Checklist Antes de Commit

- [ ] Archivo sigue patrón: `V<número>__<descripción>.sql`
- [ ] SQL es idempotente (IF EXISTS / IF NOT EXISTS)
- [ ] Incluye comentarios explicativos
- [ ] No modifica migraciones anteriores ya ejecutadas
- [ ] Probado localmente: `./gradlew flywayMigrate`
- [ ] Validado: `./gradlew flywayInfo`
- [ ] Documentado en este README si es cambio importante

## 📞 Contacto

Para preguntas sobre migraciones, revisar:
- `application.yml` para configuración Flyway
- [Documentación oficial de Flyway](https://flywaydb.org/documentation/)
- Logs de la aplicación: `spring.flyway.*`
