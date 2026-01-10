package com.drtx.ecomerce.amazon.core.model;

import com.drtx.ecomerce.amazon.core.model.product.Category;
import com.drtx.ecomerce.amazon.core.model.product.Product;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ProductTest {

    @Test
    public void testCreateProduct() {
        Category cat = new Category(1L, "Electronics", "Electronic items", null);

        Product product = new Product(
                1L,
                "Test Product",
                "This is a test product",
                new BigDecimal("19.99"),
                cat,
                new BigDecimal("4.5"),
                List.of("img1.jpg", "img2.jpg"),
                "TEST-SKU",
                100,
                com.drtx.ecomerce.amazon.core.model.product.ProductStatus.ACTIVE,
                "test-product",
                java.time.LocalDateTime.now(),
                java.time.LocalDateTime.now());

        assertEquals(1L, product.getId());
        assertEquals("Test Product", product.getName());
        assertEquals("This is a test product", product.getDescription());
        assertEquals(new BigDecimal("19.99"), product.getPrice());
        assertEquals(new BigDecimal("4.5"), product.getAverageRating());
    }
}
