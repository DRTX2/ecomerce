# 🚀 Guía Profesional: Migraciones Flyway

## 📚 Índice
1. [Introducción](#introducción)
2. [Configuración](#configuración)
3. [Mejores Prácticas](#mejores-prácticas)
4. [Patrones Comunes](#patrones-comunes)
5. [Troubleshooting](#troubleshooting)

---

## Introducción

### ¿Qué es Flyway?

**Flyway** es un herramienta de control de versiones para bases de datos que permite:

- 🔄 Automatizar cambios de schema
- 📊 Versionar cambios de BD como código
- 🔒 Garantizar reproducibilidad entre ambientes
- 🛡️ Mantener integridad de datos
- 📝 Auditar cada cambio realizado

### ¿Por qué Flyway en este proyecto?

En este ecommerce:

```yaml
Sin Flyway (Problemático):
├─ Hibernate DDL Auto = "update"  ❌ Impredecible
├─ Cambios manuales SQL            ❌ No versionado
├─ BD inconsistente entre devs     ❌ Divergencia
└─ Rol SELLER nunca fue creado      ❌ Errors en test

Con Flyway (Profesional):
├─ Esquema versionado              ✅ Git-tracked
├─ Cambios reproducibles           ✅ Idempotentes
├─ BD consistente siempre          ✅ Sincronizado
└─ Historial de migración          ✅ Auditoria completa
```

---

## Configuración

### 1. build.gradle

```groovy
dependencies {
    // Flyway Core (motor principal)
    implementation 'org.flywaydb:flyway-core:9.22.3'
    
    // Driver específico para PostgreSQL
    implementation 'org.flywaydb:flyway-database-postgresql:9.22.3'
}
```

### 2. application.yml

```yaml
spring:
  flyway:
    # ¿Habilitar Flyway?
    enabled: true
    
    # ¿Dónde están las migraciones?
    locations: classpath:db/migration
    
    # ¿Validar antes de ejecutar?
    validate-on-migrate: true
    
    # ¿Permitir migraciones fuera de orden?
    out-of-order: false
    
    # ¿Crear baseline si está vacía?
    baseline-on-migrate: false
    
    # Patrón de archivos
    sql-migration-prefix: V
    sql-migration-separator: __
    sql-migration-suffix: .sql
```

### 3. application.yml - Hibernate

```yaml
spring:
  jpa:
    hibernate:
      # IMPORTANTE: cambiar de "update" a "validate"
      ddl-auto: validate
      # Así Hibernate no intenta crear tables, Flyway lo hace
```

---

## Mejores Prácticas

### ✅ Regla #1: Nunca Modificar Migraciones Ejecutadas

```
❌ INCORRECTO:
V1__Create_users_table.sql  (ya ejecutada en BD)
├─ ALTER TABLE users ADD COLUMN email VARCHAR(255)  ❌ MODIFICADA

✅ CORRECTO:
V1__Create_users_table.sql  (no tocar)
V2__Add_email_to_users.sql  (nueva migración)
├─ ALTER TABLE users ADD COLUMN email VARCHAR(255)
```

**Por qué**: Flyway verifica checksum de cada migración. Si modificas una, fallará.

### ✅ Regla #2: Siempre Idempotente

Usar `IF EXISTS` / `IF NOT EXISTS`:

```sql
-- ❌ No es idempotente
CREATE TABLE users (id BIGINT PRIMARY KEY);
-- Falla si ya existe

-- ✅ Es idempotente
CREATE TABLE IF NOT EXISTS users (id BIGINT PRIMARY KEY);
-- Seguro ejecutar 100 veces
```

### ✅ Regla #3: Versionamiento Secuencial

```
✅ Correcto:    V1, V2, V3, V4, V5
❌ Incorrecto:  V1, V2, V4      (saltarse V3)
❌ Incorrecto:  V1, V3, V2      (desordenado)
```

### ✅ Regla #4: Una Responsabilidad por Migración

```sql
-- ❌ Mezclar responsabilidades
V1__Create_users_and_products.sql
├─ CREATE TABLE users (...)
├─ CREATE TABLE products (...)
├─ CREATE INDEX ...
└─ INSERT INTO users VALUES (...)

-- ✅ Separar por responsabilidad
V1__Create_users_table.sql
V2__Create_products_table.sql
V3__Create_indexes.sql
V4__Populate_initial_data.sql
```

### ✅ Regla #5: Documentación Explícita

```sql
-- V1__Create_users_table.sql
-- ============================================================================
-- DESCRIPCIÓN: Crear tabla users con estructura completa
-- AUTOR: Database Team
-- FECHA: 2026-01-02
-- RELACIONADA: Requerimiento #123
-- CAMBIOS:
--   - Crear tabla users
--   - Crear índice en email
--   - Crear función para actualizar updated_at
-- ============================================================================

CREATE TABLE IF NOT EXISTS users (
    id BIGSERIAL PRIMARY KEY,
    -- Comentar campos complejos
    email VARCHAR(255) NOT NULL UNIQUE, -- Email único para login
    role VARCHAR(50) NOT NULL CHECK (role IN ('USER', 'ADMIN', 'MODERATOR', 'SELLER')), -- Roles soportados
    ...
);
```

---

## Patrones Comunes

### Patrón #1: Agregar Columna

```sql
-- V2__Add_phone_to_users.sql
ALTER TABLE IF EXISTS users
ADD COLUMN IF NOT EXISTS phone VARCHAR(20);

-- Agregar constraint si es necesario
ALTER TABLE users
ADD CONSTRAINT users_phone_check CHECK (phone IS NOT NULL OR phone = '');
```

### Patrón #2: Crear Índice

```sql
-- V3__Create_users_indexes.sql
CREATE INDEX IF NOT EXISTS idx_users_email ON users(email);
CREATE INDEX IF NOT EXISTS idx_users_role ON users(role);

-- Índice compuesto (para queries comunes)
CREATE INDEX IF NOT EXISTS idx_users_role_email ON users(role, email);
```

### Patrón #3: Crear Foreign Key

```sql
-- V4__Add_profile_to_users.sql
CREATE TABLE IF NOT EXISTS user_profiles (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL UNIQUE,
    bio TEXT,
    
    CONSTRAINT fk_user_profiles_user_id
        FOREIGN KEY (user_id)
        REFERENCES users(id)
        ON DELETE CASCADE
        ON UPDATE CASCADE
);
```

### Patrón #4: Datos Iniciales

```sql
-- V5__Populate_initial_roles.sql
INSERT INTO roles (name, description) 
VALUES 
    ('USER', 'Usuario normal'),
    ('ADMIN', 'Administrador'),
    ('SELLER', 'Vendedor')
ON CONFLICT (name) DO NOTHING; -- Idempotente
```

### Patrón #5: Función y Trigger

```sql
-- V6__Add_audit_triggers.sql
CREATE OR REPLACE FUNCTION update_updated_at()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

DROP TRIGGER IF EXISTS users_update_timestamp ON users;
CREATE TRIGGER users_update_timestamp
    BEFORE UPDATE ON users
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at();
```

### Patrón #6: Migración Condicional

```sql
-- V7__Fix_invalid_roles.sql
-- Solo ejecutar si hay datos inválidos
DO $$
BEGIN
    IF EXISTS (
        SELECT 1 FROM users 
        WHERE role NOT IN ('USER', 'ADMIN', 'MODERATOR', 'SELLER')
    ) THEN
        -- Corregir datos inválidos
        UPDATE users 
        SET role = 'USER' 
        WHERE role NOT IN ('USER', 'ADMIN', 'MODERATOR', 'SELLER');
        
        RAISE WARNING 'Se corrigieron roles inválidos en users';
    END IF;
END $$;
```

---

## Workflow Típico

### 1. Desarrollo Local

```bash
# Ver estado
./migrate.sh status

# Hacer cambios
# ... editar código, crear nuevas migraciones ...

# Ejecutar migraciones
./migrate.sh migrate

# Validar
./migrate.sh validate

# Probar aplicación
./gradlew bootRun
```

### 2. Commit y Push

```bash
# Git ve las nuevas migraciones
git add src/main/resources/db/migration/V*
git commit -m "feat: Add seller role support"
git push origin feature/seller-role
```

### 3. CI/CD

```yaml
# En GitHub Actions / GitLab CI
- name: Run Database Migrations
  run: ./gradlew flywayMigrate
```

### 4. Deploy a Producción

```bash
# Las migraciones se ejecutan automáticamente
# Spring Boot detecta y ejecuta Flyway antes de iniciar
java -jar app.jar
```

---

## Troubleshooting

### Error: "Migración pendiente sin ejecutar"

```
Detected an applied migration not yet resolved at classpath:db/migration/V1__Create_users_table.sql
```

**Solución**:
```bash
./migrate.sh migrate
```

### Error: "Checksum mismatch"

```
Checksum of V1__Create_users_table does not match the database
```

**Causa**: Se modificó una migración ya ejecutada

**Soluciones**:
```sql
-- Opción 1: Revertir archivo a original
git checkout src/main/resources/db/migration/V1__Create_users_table.sql

-- Opción 2: Crear nueva migración con cambios
-- V2__Modify_users_table.sql
```

### Error: "Out of order"

```
Detected applied migration not yet resolved at classpath:db/migration/V2__...
Resolved migration not applied to the database: V1__...
```

**Causa**: Migraciones desordenadas o faltante

**Solución**:
```bash
# Verificar archivos
ls -la src/main/resources/db/migration/

# Asegurar son secuenciales: V1, V2, V3...
```

### Error: "Column already exists"

```
ALTER TABLE users ADD COLUMN email VARCHAR(255);
ERROR: column "email" already exists
```

**Solución**: Usar `IF NOT EXISTS`

```sql
ALTER TABLE users
ADD COLUMN IF NOT EXISTS email VARCHAR(255);
```

### Error: "Foreign key constraint failed"

```
ERROR: insert or update on table "orders" violates foreign key constraint
```

**Solución**: Verificar que tabla referenciada existe

```sql
-- Verificar orden de migraciones
V1__Create_users_table.sql         -- Crear usuarios PRIMERO
V2__Create_orders_table.sql        -- Crear órdenes DESPUÉS
```

---

## Checklist: Nueva Migración

- [ ] Nombre sigue patrón: `V<número>__<descripción>.sql`
- [ ] Número es secuencial (V1, V2, V3...)
- [ ] SQL es idempotente (`IF EXISTS` / `IF NOT EXISTS`)
- [ ] Incluye comentarios explicativos
- [ ] Probado localmente: `./migrate.sh migrate`
- [ ] Validado: `./migrate.sh validate`
- [ ] No modifica migraciones anteriores
- [ ] Documentado en README si es relevante
- [ ] Commit separado de cambios de código

---

## Referencias

- [Documentación Oficial Flyway](https://flywaydb.org/documentation/)
- [PostgreSQL DDL](https://www.postgresql.org/docs/current/ddl.html)
- [Spring Boot + Flyway](https://spring.io/blog/2021/01/30/spring-boot-2-4-2-available-now)
- [Database Versioning Best Practices](https://www.liquibase.org/get-started/best-practices)

---

## Próximas Migraciones

Para este proyecto, considera crear:

```sql
V4__Create_products_table.sql
V5__Create_orders_table.sql
V6__Create_product_images_table.sql
V7__Create_user_preferences_table.sql
V8__Add_indexes_for_performance.sql
```

Cada una manteniendo las prácticas de este documento.
