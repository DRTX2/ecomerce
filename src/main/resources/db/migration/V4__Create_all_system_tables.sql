-- V4__Create_all_system_tables.sql
-- Descripción: Crear todas las tablas del sistema ecommerce según entidades JPA
-- Autor: Database Team
-- Fecha: 2026-01-02

-- ============================================================================
-- REVOKED TOKENS
-- ============================================================================
CREATE TABLE IF NOT EXISTS revoked_tokens (
    token VARCHAR(1000) PRIMARY KEY,
    revoked_at TIMESTAMP NOT NULL,
    expires_at TIMESTAMP NOT NULL
);

-- ============================================================================
-- CATEGORIES
-- ============================================================================
CREATE TABLE IF NOT EXISTS categories (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255),
    description VARCHAR(255)
);

-- ============================================================================
-- PRODUCTS
-- ============================================================================
CREATE TABLE IF NOT EXISTS products (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description VARCHAR(255) NOT NULL,
    price DECIMAL(19,2),
    category_id BIGINT,
    average_rating DECIMAL(3,2),
    CONSTRAINT fk_product_category FOREIGN KEY (category_id) REFERENCES categories(id)
);

-- ============================================================================
-- PRODUCT IMAGES
-- ============================================================================
CREATE TABLE IF NOT EXISTS product_images (
    id BIGSERIAL PRIMARY KEY,
    url VARCHAR(2000),
    product_id BIGINT,
    CONSTRAINT fk_product_image_product FOREIGN KEY (product_id) REFERENCES products(id)
);

-- ============================================================================
-- CARTS
-- ============================================================================
CREATE TABLE IF NOT EXISTS carts (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    CONSTRAINT fk_cart_user FOREIGN KEY (user_id) REFERENCES users(id)
);

-- ============================================================================
-- CART ITEMS
-- ============================================================================
CREATE TABLE IF NOT EXISTS cart_items (
    id BIGSERIAL PRIMARY KEY,
    cart_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    quantity INTEGER NOT NULL,
    CONSTRAINT fk_cart_item_cart FOREIGN KEY (cart_id) REFERENCES carts(id),
    CONSTRAINT fk_cart_item_product FOREIGN KEY (product_id) REFERENCES products(id)
);

-- ============================================================================
-- DISCOUNTS
-- ============================================================================
CREATE TABLE IF NOT EXISTS discounts (
    id BIGSERIAL PRIMARY KEY,
    code VARCHAR(50) NOT NULL UNIQUE,
    type VARCHAR(50) NOT NULL,
    discount_value DECIMAL(19,2),
    expiration_date TIMESTAMP
);

-- ============================================================================
-- DISCOUNT_PRODUCTS (Join Table)
-- ============================================================================
CREATE TABLE IF NOT EXISTS discount_products (
    discount_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    PRIMARY KEY (discount_id, product_id),
    CONSTRAINT fk_discount_products_discount FOREIGN KEY (discount_id) REFERENCES discounts(id),
    CONSTRAINT fk_discount_products_product FOREIGN KEY (product_id) REFERENCES products(id)
);

-- ============================================================================
-- ORDERS
-- ============================================================================
CREATE TABLE IF NOT EXISTS orders (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT,
    total DECIMAL(19,2),
    order_state VARCHAR(50) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    delivered_at TIMESTAMP,
    CONSTRAINT fk_order_user FOREIGN KEY (user_id) REFERENCES users(id)
);

-- ============================================================================
-- ORDER_DISCOUNTS (Join Table)
-- ============================================================================
CREATE TABLE IF NOT EXISTS order_discounts (
    order_id BIGINT NOT NULL,
    discount_id BIGINT NOT NULL,
    PRIMARY KEY (order_id, discount_id),
    CONSTRAINT fk_order_discounts_order FOREIGN KEY (order_id) REFERENCES orders(id),
    CONSTRAINT fk_order_discounts_discount FOREIGN KEY (discount_id) REFERENCES discounts(id)
);

-- ============================================================================
-- ORDER ITEMS
-- ============================================================================
CREATE TABLE IF NOT EXISTS order_items (
    id BIGSERIAL PRIMARY KEY,
    order_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    quantity INTEGER NOT NULL,
    price_at_purchase DECIMAL(19,2) NOT NULL,
    CONSTRAINT fk_order_item_order FOREIGN KEY (order_id) REFERENCES orders(id),
    CONSTRAINT fk_order_item_product FOREIGN KEY (product_id) REFERENCES products(id)
);

-- ============================================================================
-- PAYMENTS
-- ============================================================================
CREATE TABLE IF NOT EXISTS payments (
    id BIGSERIAL PRIMARY KEY,
    order_id BIGINT NOT NULL,
    amount DECIMAL(19,2) NOT NULL,
    status VARCHAR(50) NOT NULL,
    method VARCHAR(50) NOT NULL,
    payment_date TIMESTAMP NOT NULL,
    CONSTRAINT fk_payment_order FOREIGN KEY (order_id) REFERENCES orders(id)
);

-- ============================================================================
-- SHIPPINGS
-- ============================================================================
CREATE TABLE IF NOT EXISTS shippings (
    id BIGSERIAL PRIMARY KEY,
    order_id BIGINT NOT NULL,
    delivery_address VARCHAR(500) NOT NULL,
    status VARCHAR(50) NOT NULL,
    carrier VARCHAR(100),
    tracking_number VARCHAR(255),
    estimated_delivery TIMESTAMP,
    CONSTRAINT fk_shipping_order FOREIGN KEY (order_id) REFERENCES orders(id)
);

-- ============================================================================
-- REVIEWS
-- ============================================================================
CREATE TABLE IF NOT EXISTS reviews (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT,
    product_id BIGINT,
    rating INTEGER,
    comment VARCHAR(1000),
    created_at TIMESTAMP,
    CONSTRAINT fk_review_user FOREIGN KEY (user_id) REFERENCES users(id),
    CONSTRAINT fk_review_product FOREIGN KEY (product_id) REFERENCES products(id)
);

-- ============================================================================
-- INVENTORIES
-- ============================================================================
CREATE TABLE IF NOT EXISTS inventories (
    id BIGSERIAL PRIMARY KEY,
    product_id BIGINT NOT NULL,
    available_quantity INTEGER NOT NULL,
    warehouse_location VARCHAR(255),
    CONSTRAINT fk_inventory_product FOREIGN KEY (product_id) REFERENCES products(id)
);

-- ============================================================================
-- FAVORITE_PUBLICATIONS
-- ============================================================================
CREATE TABLE IF NOT EXISTS favorite_publications (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    created_at TIMESTAMP NOT NULL,
    CONSTRAINT fk_favorite_user FOREIGN KEY (user_id) REFERENCES users(id),
    CONSTRAINT fk_favorite_product FOREIGN KEY (product_id) REFERENCES products(id),
    CONSTRAINT uk_user_product UNIQUE (user_id, product_id)
);

-- ============================================================================
-- NOTIFICATIONS
-- ============================================================================
CREATE TABLE IF NOT EXISTS notifications (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    message VARCHAR(1000) NOT NULL,
    type VARCHAR(50) NOT NULL,
    status VARCHAR(50) NOT NULL,
    created_at TIMESTAMP,
    CONSTRAINT fk_notification_user FOREIGN KEY (user_id) REFERENCES users(id)
);

-- ============================================================================
-- SUPPLIERS
-- ============================================================================
CREATE TABLE IF NOT EXISTS suppliers (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    contact VARCHAR(255)
);

-- ============================================================================
-- SUPPLIER_PRODUCTS (Join Table)
-- ============================================================================
CREATE TABLE IF NOT EXISTS supplier_products (
    supplier_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    PRIMARY KEY (supplier_id, product_id),
    CONSTRAINT fk_supplier_products_supplier FOREIGN KEY (supplier_id) REFERENCES suppliers(id),
    CONSTRAINT fk_supplier_products_product FOREIGN KEY (product_id) REFERENCES products(id)
);

-- ============================================================================
-- ORDER_RETURNS
-- ============================================================================
CREATE TABLE IF NOT EXISTS order_returns (
    id BIGSERIAL PRIMARY KEY,
    order_id BIGINT NOT NULL,
    reason VARCHAR(500) NOT NULL,
    status VARCHAR(50) NOT NULL,
    requested_at TIMESTAMP,
    CONSTRAINT fk_return_order FOREIGN KEY (order_id) REFERENCES orders(id)
);

-- ============================================================================
-- INCIDENCES
-- ============================================================================
CREATE TABLE IF NOT EXISTS incidences (
    id BIGSERIAL PRIMARY KEY,
    public_ui UUID NOT NULL UNIQUE,
    product_id BIGINT,
    status VARCHAR(50),
    created_at TIMESTAMP NOT NULL,
    autoclosed BOOLEAN,
    moderator_id BIGINT,
    moderator_comment VARCHAR(500),
    decision VARCHAR(50),
    CONSTRAINT fk_incidence_product FOREIGN KEY (product_id) REFERENCES products(id),
    CONSTRAINT fk_incidence_moderator FOREIGN KEY (moderator_id) REFERENCES users(id)
);

-- ============================================================================
-- REPORTS
-- ============================================================================
CREATE TABLE IF NOT EXISTS reports (
    id BIGSERIAL PRIMARY KEY,
    incidence_id BIGINT,
    reporter_id BIGINT,
    reason VARCHAR(100) NOT NULL,
    comment VARCHAR(1000),
    created_at TIMESTAMP NOT NULL,
    source VARCHAR(50),
    CONSTRAINT fk_report_incidence FOREIGN KEY (incidence_id) REFERENCES incidences(id),
    CONSTRAINT fk_report_reporter FOREIGN KEY (reporter_id) REFERENCES users(id)
);

-- ============================================================================
-- APPEALS
-- ============================================================================
CREATE TABLE IF NOT EXISTS appeals (
    id BIGSERIAL PRIMARY KEY,
    incidence_id BIGINT NOT NULL,
    seller_id BIGINT NOT NULL,
    reason VARCHAR(500) NOT NULL,
    created_at TIMESTAMP,
    status VARCHAR(50),
    new_moderator_id BIGINT,
    final_decision VARCHAR(50),
    final_decision_at TIMESTAMP,
    CONSTRAINT fk_appeal_incidence FOREIGN KEY (incidence_id) REFERENCES incidences(id),
    CONSTRAINT fk_appeal_seller FOREIGN KEY (seller_id) REFERENCES users(id),
    CONSTRAINT fk_appeal_moderator FOREIGN KEY (new_moderator_id) REFERENCES users(id)
);

-- ============================================================================
-- INDEXES
-- ============================================================================
CREATE INDEX IF NOT EXISTS idx_products_category ON products(category_id);
CREATE INDEX IF NOT EXISTS idx_products_rating ON products(average_rating);
CREATE INDEX IF NOT EXISTS idx_product_images_product ON product_images(product_id);
CREATE INDEX IF NOT EXISTS idx_cart_items_cart ON cart_items(cart_id);
CREATE INDEX IF NOT EXISTS idx_cart_items_product ON cart_items(product_id);
CREATE INDEX IF NOT EXISTS idx_orders_user ON orders(user_id);
CREATE INDEX IF NOT EXISTS idx_orders_status ON orders(order_state);
CREATE INDEX IF NOT EXISTS idx_order_items_order ON order_items(order_id);
CREATE INDEX IF NOT EXISTS idx_reviews_product ON reviews(product_id);
CREATE INDEX IF NOT EXISTS idx_reviews_user ON reviews(user_id);
CREATE INDEX IF NOT EXISTS idx_favorite_user ON favorite_publications(user_id);
CREATE INDEX IF NOT EXISTS idx_notifications_user ON notifications(user_id);
CREATE INDEX IF NOT EXISTS idx_incidences_product ON incidences(product_id);
