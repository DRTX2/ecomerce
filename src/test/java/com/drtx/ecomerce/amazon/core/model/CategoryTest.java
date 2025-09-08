package com.drtx.ecomerce.amazon.core.model;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class CategoryTest {

    @Test
    public void testCategoryCreation() {
        Category category = new Category(1L, "Electronics", "Devices", null);

        assertEquals(1L, category.getId());
        assertEquals("Electronics", category.getName());
        assertEquals("Devices", category.getDescription());
        assertNull(category.getProducts());
    }

    @Test
    public void testSetters() {
        Category category = new Category(2L, "Clothes", "Apparel", List.of());

        category.setName("Updated");
        category.setDescription("Updated Desc");

        assertEquals("Updated", category.getName());
        assertEquals("Updated Desc", category.getDescription());
    }
}
