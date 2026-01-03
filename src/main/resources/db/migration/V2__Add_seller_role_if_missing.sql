-- V2__Add_seller_role_if_missing.sql
-- Descripción: Actualizar constraint de roles para asegurar que SELLER esté soportado
-- Autor: Database Team
-- Fecha: 2026-01-02
-- Nota: Idempotente - segura para ejecutar múltiples veces

-- ============================================================================
-- VALIDACIÓN Y CORRECCIÓN DEL CONSTRAINT users_role_check
-- ============================================================================

-- Paso 1: Eliminar el constraint antiguo si existe (idempotente)
ALTER TABLE IF EXISTS users DROP CONSTRAINT IF EXISTS users_role_check;

-- Paso 2: Crear el constraint correcto que incluye SELLER
ALTER TABLE users
ADD CONSTRAINT users_role_check 
CHECK (role IN ('USER', 'ADMIN', 'MODERATOR', 'SELLER'));

-- ============================================================================
-- VALIDACIÓN DE DATOS
-- ============================================================================
-- Verificar que no hay valores inválidos en la columna role
-- Si la consulta retorna resultados, hay un problema de integridad
DO $$
BEGIN
    IF EXISTS (
        SELECT 1 FROM users 
        WHERE role NOT IN ('USER', 'ADMIN', 'MODERATOR', 'SELLER')
    ) THEN
        RAISE WARNING 'Advertencia: Se encontraron roles inválidos en la tabla users';
    END IF;
END $$;

-- ============================================================================
-- INFORMACIÓN DE MIGRACIÓN
-- ============================================================================
-- Esta migración:
-- 1. Elimina cualquier constraint antiguo que no incluya SELLER
-- 2. Crea un nuevo constraint con todos los roles soportados
-- 3. Valida que no haya datos inconsistentes
-- 4. Es totalmente idempotente y segura de ejecutar
-- ============================================================================
