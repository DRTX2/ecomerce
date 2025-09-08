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

## 4. Resumen de pasos para tus modelos

1. **Crea una clase de test para cada modelo** en el paquete de test correspondiente.
2. **Cubre constructores, getters/setters, métodos auxiliares y validaciones.**
3. **Asegúrate de probar casos normales y casos límite/errores.**
4. **Usa aserciones claras y descriptivas.**
5. **No uses mocks a menos que el modelo tenga dependencias externas (raro en modelos puros).**

---

Excelente observación 👌.
En hexagonal, los **tests del core (modelos + reglas de negocio)** ya los tienes.
El siguiente paso es pensar:

### 🔑 ¿Dónde más puedes testear?

1. **Casos de uso (application layer)**

    * Aquí es donde orquestas lógica con los puertos.
    * Los tests deberían ser unitarios con **mocks de los puertos** (`CartRepositoryPort`, `UserRepositoryPort`, etc.).
    * Ejemplo: probar que `CartService.addProduct()` llama a `cartRepository.save()` y retorna lo esperado.

2. **Adaptadores de entrada (API, controllers, CLI, etc.)**

    * Se prueban con **tests de integración** (por ejemplo con `@SpringBootTest` o `WebMvcTest` en Spring).
    * Validar endpoints, request/response, serialización JSON.

3. **Adaptadores de salida (implementaciones de los ports)**

    * Repositorios JPA → **tests de integración con base de datos embebida** (H2, Testcontainers).
    * Seguridad → tests de `PasswordService`, `TokenProvider`, etc.

---

### 📌 Estrategia de Tests por capa

* **Core (ya hecho):**

    * Modelos, enums, validaciones (`Product.validate`).

* **Application (casos de uso):**

    * Mockear puertos con Mockito.
    * Verificar comportamiento, flujos de negocio y excepciones.

* **Infraestructura (adaptadores):**

    * Tests de integración contra DB (H2).
    * Tests de seguridad (tokens válidos/expirados, passwords).

---

### 📌 Ejemplo: Test de Caso de Uso con Puerto

Supongamos que tienes un `CartService` en el core que depende de `CartRepositoryPort`:

```java
class CartService {
    private final CartRepositoryPort cartRepository;

    CartService(CartRepositoryPort cartRepository) {
        this.cartRepository = cartRepository;
    }

    public Cart createCart(Cart cart) {
        return cartRepository.save(cart);
    }
}
```

El test unitario sería:

```java
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class CartServiceTest {

    @Test
    void testCreateCart() {
        CartRepositoryPort repo = mock(CartRepositoryPort.class);
        CartService service = new CartService(repo);

        Cart cart = new Cart();
        cart.setId(1L);

        when(repo.save(cart)).thenReturn(cart);

        Cart result = service.createCart(cart);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(repo, times(1)).save(cart);
    }
}
```

---

👉 Resumen:
Ya terminaste con **modelos del core**, ahora toca:

1. **Casos de uso con mocks de puertos.**
2. **Tests de integración para adaptadores (JPA, seguridad, REST).**

¿Quieres que te arme un **ejemplo de caso de uso completo (service + test con mocks)** para, digamos, `UserService` que use `UserRepositoryPort` y `PasswordService`?

---

Muy buena pregunta 👌.
En **hexagonal** hay dos tipos de tests relevantes para los casos de uso:

---

### 🔹 1. **Tests unitarios de casos de uso**

* Se recomienda usar **mocks/stubs** de los *ports* (repositorios, seguridad, etc.).
* Ventaja:

    * Ejecutan rápido.
    * Aíslan la lógica de negocio sin depender de DB, Spring, ni infra.
* Ejemplo: probar que `UserService.register()`

    * Encripta la contraseña usando `PasswordService`.
    * Llama a `UserRepositoryPort.save()`.
    * Lanza excepción si el email ya existe.

👉 Aquí **sí usas mocks**.

---

### 🔹 2. **Tests de integración (con implementación real de los ports)**

* Aquí pruebas los *adapters* concretos (por ejemplo `UserRepositoryAdapter` que usa JPA).
* Ventaja: validas la conexión real con DB, mappings, consultas, etc.
* Generalmente se hace con **H2 o Testcontainers** para no afectar la base real.
* Ejemplo: guardar un `User` en el repositorio y luego recuperarlo.

👉 Aquí **no usas mocks**, sino la implementación real.

---

### 📌 Resumen claro

* **Mocks → Unit tests de casos de uso (application layer).**
* **Implementación real → Integration tests (infra layer).**

---

👉 Si ahora quieres enfocarte en **casos de uso (application layer)**, lo correcto es usar **mocks** para todos los puertos (`UserRepositoryPort`, `PasswordService`, etc.).

¿Quieres que te prepare un ejemplo completo de **UserService con su test usando mocks**?



---

Ah, perfecto, David. Ahora entiendo mejor tu proyecto y tu estructura hexagonal. 🚀

En tu caso, lo correcto sería **no mezclar los tests con el código fuente** (`src/main/java`). Los tests deben ir en `src/test/java` y, efectivamente, se suele **replicar la misma estructura de paquetes** que en `main` para que sea fácil localizar los tests.

Con tu estructura actual:

```
src/main/java/com/drtx/ecomerce/amazon/
 ├─ adapters/in
 ├─ adapters/out
 ├─ application/usecases
 ├─ core/model
 ├─ core/ports
 └─ infrastructure/
```

Los tests irían así:

```
src/test/java/com/drtx/ecomerce/amazon/
 ├─ adapters/in        <-- tests de integración de controladores, endpoints, etc.
 ├─ adapters/out       <-- tests de integración de repositorios y servicios externos
 ├─ application/usecases
 │   └─ UserServiceTest.java
 │   └─ CartServiceTest.java
 │   └─ OrderServiceTest.java
 ├─ core/model         <-- tus tests unitarios de modelos (ProductTest, CartTest, etc.)
 └─ core/ports         <-- opcional, si quieres testear mocks o implementaciones de puertos
```

---

### 📌 Reglas importantes

1. **Misma jerarquía de paquetes:** facilita que cualquier IDE encuentre el test correspondiente.
2. **Nombres claros:** `<Clase>Test.java`.
3. **Tests unitarios → application/usecases**: mocks de puertos, rápido y aislado.
4. **Tests de integración → adapters**: usan implementaciones reales, DB embebida, REST mocks.

---

Si quieres, puedo **dibujarte un esquema visual completo de tu repo con todos los tests ubicados en la carpeta `test`**, listo para copiar y organizar. Esto te da un roadmap para tener todos tus servicios testeados.

¿Quieres que haga eso?
