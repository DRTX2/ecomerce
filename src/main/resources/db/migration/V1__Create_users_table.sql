-- V1__Create_users_table.sql
-- Descripción: Crear tabla users con estructura completa y constraint de roles
-- Autor: Database Team
-- Fecha: 2026-01-02

-- ============================================================================
-- TABLA: users
-- ============================================================================
-- Tabla principal para almacenar información de usuarios del sistema
-- Soporta diferentes roles: USER, ADMIN, MODERATOR, SELLER
-- ============================================================================

CREATE TABLE IF NOT EXISTS users (
    -- Columna de identidad
    id BIGSERIAL PRIMARY KEY,
    
    -- Información personal
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    
    -- Información de contacto
    address VARCHAR(500),
    phone VARCHAR(20),
    
    -- Role del usuario (ENUM como VARCHAR por compatibilidad con Hibernate)
    role VARCHAR(50) NOT NULL DEFAULT 'USER' CHECK (role IN ('USER', 'ADMIN', 'MODERATOR', 'SELLER')),
    
    -- Auditoría
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ============================================================================
-- ÍNDICES
-- ============================================================================
CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_users_role ON users(role);

-- ============================================================================
-- COMENTARIOS
-- ============================================================================
COMMENT ON TABLE users IS 'Tabla de usuarios del sistema con soporte para múltiples roles';
COMMENT ON COLUMN users.id IS 'Identificador único del usuario';
COMMENT ON COLUMN users.name IS 'Nombre completo del usuario';
COMMENT ON COLUMN users.email IS 'Correo electrónico único';
COMMENT ON COLUMN users.password IS 'Contraseña hasheada (BCrypt)';
COMMENT ON COLUMN users.address IS 'Dirección de domicilio del usuario';
COMMENT ON COLUMN users.phone IS 'Número de teléfono de contacto';
COMMENT ON COLUMN users.role IS 'Rol asignado (USER, ADMIN, MODERATOR, SELLER)';
COMMENT ON COLUMN users.created_at IS 'Timestamp de creación del registro';
COMMENT ON COLUMN users.updated_at IS 'Timestamp de última actualización';
