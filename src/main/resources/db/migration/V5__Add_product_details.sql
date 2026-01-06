-- Add new columns to products table
ALTER TABLE products 
ADD COLUMN sku VARCHAR(255),
ADD COLUMN stock_quantity INTEGER DEFAULT 0,
ADD COLUMN status VARCHAR(50) DEFAULT 'DRAFT',
ADD COLUMN slug VARCHAR(255),
ADD COLUMN created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
ADD COLUMN updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP;

-- Constraint: SKU and Slug should be unique
ALTER TABLE products ADD CONSTRAINT uk_products_sku UNIQUE (sku);
ALTER TABLE products ADD CONSTRAINT uk_products_slug UNIQUE (slug);

-- Update existing records to avoid null constraints (if any)
UPDATE products SET sku = 'SKU-' || id WHERE sku IS NULL;
UPDATE products SET slug = 'product-' || id WHERE slug IS NULL;
UPDATE products SET stock_quantity = 0 WHERE stock_quantity IS NULL;
UPDATE products SET status = 'ACTIVE' WHERE status IS NULL;

-- Enforce Not Null constraints now
ALTER TABLE products ALTER COLUMN sku SET NOT NULL;
ALTER TABLE products ALTER COLUMN stock_quantity SET NOT NULL;
ALTER TABLE products ALTER COLUMN status SET NOT NULL;
ALTER TABLE products ALTER COLUMN slug SET NOT NULL;
ALTER TABLE products ALTER COLUMN created_at SET NOT NULL;
