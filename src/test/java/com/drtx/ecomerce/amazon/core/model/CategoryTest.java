package com.drtx.ecomerce.amazon.core.model;

import com.drtx.ecomerce.amazon.core.model.product.Category;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class CategoryTest {

    @Test
    @DisplayName("Should create category correctly")
    void testCategoryCreation() {
        UUID categoryUuid = UUID.randomUUID();
        Category category = new Category(1L, categoryUuid, "Electronics", "Devices", null);

        assertEquals(1L, category.getId());
        assertEquals(categoryUuid, category.getUuid());
        assertEquals("Electronics", category.getName());
    }

    @Test
    @DisplayName("Should set products correctly")
    void testSetProducts() {
        UUID categoryUuid = UUID.randomUUID();
        Category category = new Category(2L, categoryUuid, "Clothes", "Apparel", List.of());
        assertTrue(category.getProducts().isEmpty());
    }
}
