-- Migration: Add UUID column to users, products, orders, categories, carts, appeals and incidences table
-- Purpose: Add UUID for public API identification while keeping Long id for internal use

CREATE EXTENSION IF NOT EXISTS "pgcrypto";

-- Add UUID to users table
ALTER TABLE users ADD COLUMN IF NOT EXISTS uuid UUID;
UPDATE users SET uuid = gen_random_uuid() WHERE uuid IS NULL;
ALTER TABLE users ALTER COLUMN uuid SET NOT NULL;
ALTER TABLE users ADD CONSTRAINT users_uuid_unique UNIQUE (uuid);
CREATE INDEX IF NOT EXISTS idx_users_uuid ON users(uuid);

-- Add UUID to products table
ALTER TABLE products ADD COLUMN IF NOT EXISTS uuid UUID;
UPDATE products SET uuid = gen_random_uuid() WHERE uuid IS NULL;
ALTER TABLE products ALTER COLUMN uuid SET NOT NULL;
ALTER TABLE products ADD CONSTRAINT products_uuid_unique UNIQUE (uuid);
CREATE INDEX IF NOT EXISTS idx_products_uuid ON products(uuid);

-- Add UUID to orders table
ALTER TABLE orders ADD COLUMN IF NOT EXISTS uuid UUID;
UPDATE orders SET uuid = gen_random_uuid() WHERE uuid IS NULL;
ALTER TABLE orders ALTER COLUMN uuid SET NOT NULL;
ALTER TABLE orders ADD CONSTRAINT orders_uuid_unique UNIQUE (uuid);
CREATE INDEX IF NOT EXISTS idx_orders_uuid ON orders(uuid);

ALTER TABLE categories ADD COLUMN IF NOT EXISTS uuid UUID;
UPDATE categories SET uuid = gen_random_uuid() WHERE uuid IS NULL;
ALTER TABLE categories ALTER COLUMN uuid SET NOT NULL;
ALTER TABLE categories ADD CONSTRAINT categories_uuid_unique UNIQUE (uuid);
CREATE INDEX IF NOT EXISTS idx_categories_uuid ON categories(uuid);

ALTER TABLE carts ADD COLUMN IF NOT EXISTS uuid UUID;
UPDATE carts SET uuid = gen_random_uuid() WHERE uuid IS NULL;
ALTER TABLE carts ALTER COLUMN uuid SET NOT NULL;
ALTER TABLE carts ADD CONSTRAINT carts_uuid_unique UNIQUE (uuid);
CREATE INDEX IF NOT EXISTS idx_carts_uuid ON carts(uuid);

ALTER TABLE appeals ADD COLUMN IF NOT EXISTS uuid UUID;
UPDATE appeals SET uuid = gen_random_uuid() WHERE uuid IS NULL;
ALTER TABLE appeals ALTER COLUMN uuid SET NOT NULL;
ALTER TABLE appeals ADD CONSTRAINT appeals_uuid_unique UNIQUE (uuid);
CREATE INDEX IF NOT EXISTS idx_appeals_uuid ON appeals(uuid);

ALTER TABLE incidences ADD COLUMN IF NOT EXISTS public_ui UUID;
UPDATE incidences SET public_ui = gen_random_uuid() WHERE public_ui IS NULL;
ALTER TABLE incidences ALTER COLUMN public_ui SET NOT NULL;

-- Add unique constraint if it doesn't exist
DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM pg_constraint WHERE conname = 'incidences_public_ui_unique'
    ) THEN
        ALTER TABLE incidences ADD CONSTRAINT incidences_public_ui_unique UNIQUE (public_ui);
    END IF;
END $$;

CREATE INDEX IF NOT EXISTS idx_incidences_public_ui ON incidences(public_ui);