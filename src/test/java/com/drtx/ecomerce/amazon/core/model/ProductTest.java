package com.drtx.ecomerce.amazon.core.model;

import com.drtx.ecomerce.amazon.core.model.product.Category;
import com.drtx.ecomerce.amazon.core.model.product.Product;
import com.drtx.ecomerce.amazon.core.model.product.ProductStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ProductTest {

    @Test
    @DisplayName("Should create product correctly")
    void testProductCreation() {
        Category cat = new Category(1L, UUID.randomUUID(), "Electronics", "Electronic items", null);
        UUID productUuid = UUID.randomUUID();
        Product product = new Product(
                1L,
                productUuid,
                "Laptop",
                "Great laptop",
                new BigDecimal("1000.00"),
                cat,
                new BigDecimal("4.5"),
                List.of("image1.jpg"),
                "SKU-123",
                10,
                ProductStatus.ACTIVE,
                "laptop",
                LocalDateTime.now(),
                null);

        assertEquals(1L, product.getId());
        assertEquals(productUuid, product.getUuid());
        assertEquals("Laptop", product.getName());
        assertEquals(cat, product.getCategory());
    }
}
