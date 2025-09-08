package com.drtx.ecomerce.amazon.core.model;
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
                100,
                cat,
                new BigDecimal("4.5"),
                List.of("img1.jpg", "img2.jpg")
        );

        assertEquals(1, product.getId());
        assertEquals("Test Product", product.getName());
        assertEquals("This is a test product", product.getDescription());
        assertEquals(new BigDecimal("19.99"), product.getPrice());
        assertTrue(product.getStock()>0);
        assertEquals(100, product.getStock());
    }

    @Test
    public void testCartGettersAndSetters() {
        // Preparar datos
        User user = new User(1L, "test@email.com", "username", "password", "Ambato", "0987654321", UserRole.USER);
        Category category = new Category(1L, "Electronics", "Electronic items", null);

        Product p1 = new Product(1L, "Product A", "Desc", new BigDecimal("50"), 100, category, null, List.of("img1.jpg", "img2.jpg"));
        Product p2 = new Product(2L, "Product B", "Desc2", null, 0, null, null, null);

        List<Product> products = List.of(p1, p2);

        Cart cart = new Cart();

        // Setters
        cart.setId(100L);
        cart.setUser(user);
        cart.setProducts(products);

        // Getters
        assertEquals(100L, cart.getId(), "El ID del carrito debería ser 100");
        assertEquals(user, cart.getUser(), "El usuario del carrito debería coincidir");
        assertNotNull(cart.getProducts(), "La lista de productos no debería ser null");
        assertEquals(2, cart.getProducts().size(), "Debería haber 2 productos en el carrito");
        assertIterableEquals(products, cart.getProducts(), "Los productos del carrito deberían coincidir exactamente");

        // Validación extra opcional de nombres individuales
        assertEquals("Product A", cart.getProducts().get(0).getName(), "El primer producto debería ser 'Product A'");
        assertEquals("Product B", cart.getProducts().get(1).getName(), "El segundo producto debería ser 'Product B'");
    }
}
