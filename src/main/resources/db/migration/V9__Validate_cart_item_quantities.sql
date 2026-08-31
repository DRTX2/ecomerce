DELETE FROM cart_items WHERE quantity <= 0;

ALTER TABLE cart_items
    ADD CONSTRAINT chk_cart_items_quantity_positive CHECK (quantity > 0);
