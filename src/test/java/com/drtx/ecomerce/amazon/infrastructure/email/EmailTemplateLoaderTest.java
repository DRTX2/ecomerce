package com.drtx.ecomerce.amazon.infrastructure.email;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class EmailTemplateLoaderTest {

    private EmailTemplateLoader templateLoader;

    @BeforeEach
    void setUp() {
        templateLoader = new EmailTemplateLoader();
    }

    @Test
    void testLoadPasswordResetTemplate_Success() {
        Map<String, String> variables = Map.of(
                "name", "Juan",
                "token", "ABC123XYZ");

        String result = templateLoader.loadTemplate("password-reset", variables);

        assertNotNull(result);
        assertTrue(result.contains("Juan"));
        assertTrue(result.contains("ABC123XYZ"));
        assertTrue(result.contains("Restablecimiento de Contraseña"));
        assertFalse(result.contains("{{name}}"));
        assertFalse(result.contains("{{token}}"));
    }

    @Test
    void testLoadWelcomeTemplate_Success() {
        Map<String, String> variables = Map.of("name", "María");

        String result = templateLoader.loadTemplate("welcome", variables);

        assertNotNull(result);
        assertTrue(result.contains("María"));
        assertTrue(result.contains("¡Bienvenido a Amazon Clone!"));
        assertFalse(result.contains("{{name}}"));
    }

    @Test
    void testLoadOrderConfirmationTemplate_Success() {
        Map<String, String> variables = Map.of(
                "name", "Carlos",
                "orderNumber", "ORD-2024-12345");

        String result = templateLoader.loadTemplate("order-confirmation", variables);

        assertNotNull(result);
        assertTrue(result.contains("Carlos"));
        assertTrue(result.contains("ORD-2024-12345"));
        assertTrue(result.contains("¡Pedido Confirmado!"));
        assertFalse(result.contains("{{name}}"));
        assertFalse(result.contains("{{orderNumber}}"));
    }

    @Test
    void testLoadTemplate_WithNonExistentTemplate_ThrowsException() {
        Map<String, String> variables = Map.of("name", "Test");

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> templateLoader.loadTemplate("non-existent", variables));

        assertTrue(exception.getMessage().contains("Error al cargar plantilla de email"));
    }

    @Test
    void testLoadTemplate_WithEmptyVariables_Success() {
        Map<String, String> variables = Map.of();

        String result = templateLoader.loadTemplate("welcome", variables);

        assertNotNull(result);
        // Las variables no reemplazadas deberían quedar como {{name}}
        assertTrue(result.contains("{{name}}"));
    }
}
