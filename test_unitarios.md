# Guía para pruebas unitarias de modelos en el proyecto e-commerce

Esta guía explica cómo y qué probar en los modelos de dominio (Cart, Product, User, Order, etc.) usando JUnit 5 y Mockito en Java/Spring Boot.

---

## 1. ¿Cómo hacer las pruebas unitarias para los modelos?

**Herramientas recomendadas:**
- JUnit 5: Para la estructura de los tests.
- Mockito: Solo si el modelo tiene dependencias externas (raro en modelos puros).
- AssertJ o Hamcrest: Para aserciones más expresivas (opcional).

**Ubicación sugerida:**
Coloca los tests en `src/test/java/com/drtx/ecomerce/amazon/core/model/` siguiendo la estructura de paquetes de tus modelos.

---

## 2. ¿Qué probar en cada modelo?

1. **Constructores y creación de instancias**
   - Que los objetos se creen correctamente con los valores esperados.
2. **Getters y setters**
   - Que los valores se asignen y recuperen correctamente.
3. **Métodos auxiliares**
   - Métodos como `addProduct`, `removeProduct`, `calculateTotal`, etc., si existen.
4. **Validaciones internas**
   - Si hay lógica que impide estados inválidos (por ejemplo, cantidad negativa, precio negativo).
5. **equals, hashCode y toString**
   - Que el comportamiento sea el esperado para comparación y representación.

---

## 3. Ejemplo de pruebas unitarias para un modelo `Product`

Supón que tienes un modelo `Product` así:

```java
public class Product {
    private Long id;
    private String name;
    private double price;
    private int stock;

    // constructor, getters, setters, equals, hashCode, toString
    // método: public void decreaseStock(int amount)
}
```

**Ejemplo de test con JUnit 5:**

```java
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ProductTest {

    @Test
    void testConstructorAndGetters() {
        Product product = new Product(1L, "Laptop", 1000.0, 10);
        assertEquals(1L, product.getId());
        assertEquals("Laptop", product.getName());
        assertEquals(1000.0, product.getPrice());
        assertEquals(10, product.getStock());
    }

    @Test
    void testSetters() {
        Product product = new Product();
        product.setName("Tablet");
        assertEquals("Tablet", product.getName());
    }

    @Test
    void testDecreaseStock() {
        Product product = new Product(1L, "Laptop", 1000.0, 10);
        product.decreaseStock(3);
        assertEquals(7, product.getStock());
    }

    @Test
    void testDecreaseStockNegative() {
        Product product = new Product(1L, "Laptop", 1000.0, 2);
        assertThrows(IllegalArgumentException.class, () -> product.decreaseStock(3));
    }

    @Test
    void testEqualsAndHashCode() {
        Product p1 = new Product(1L, "Laptop", 1000.0, 10);
        Product p2 = new Product(1L, "Laptop", 1000.0, 10);
        assertEquals(p1, p2);
        assertEquals(p1.hashCode(), p2.hashCode());
    }
}
```

---

## 4. Resumen de pasos para tus modelos

1. **Crea una clase de test para cada modelo** en el paquete de test correspondiente.
2. **Cubre constructores, getters/setters, métodos auxiliares y validaciones.**
3. **Asegúrate de probar casos normales y casos límite/errores.**
4. **Usa aserciones claras y descriptivas.**
5. **No uses mocks a menos que el modelo tenga dependencias externas (raro en modelos puros).**

---

¿Quieres que genere la estructura de tests para todos tus modelos automáticamente? Puedo crear los archivos base para cada uno si lo necesitas.
