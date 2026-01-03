-- V3__Add_audit_timestamps.sql
-- Descripción: Actualizar triggers y funciones para auditoría automática de timestamps
-- Autor: Database Team
-- Fecha: 2026-01-02

-- ============================================================================
-- FUNCIÓN: update_updated_at_column
-- ============================================================================
-- Función que actualiza automáticamente la columna updated_at
-- al modificar un registro
-- ============================================================================

CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- ============================================================================
-- TRIGGER: users_update_updated_at
-- ============================================================================
-- Trigger que ejecuta la función anterior cuando se actualiza users
-- ============================================================================

DROP TRIGGER IF EXISTS users_update_updated_at ON users;

CREATE TRIGGER users_update_updated_at
    BEFORE UPDATE ON users
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();

-- ============================================================================
-- INFORMACIÓN
-- ============================================================================
-- Esta migración:
-- 1. Crea una función PL/pgSQL reutilizable para actualizar timestamps
-- 2. Crea un trigger en la tabla users para aplicarlo automáticamente
-- 3. Es idempotente (usa DROP IF EXISTS)
-- 4. Mejora la auditoría de cambios en la base de datos
-- ============================================================================
