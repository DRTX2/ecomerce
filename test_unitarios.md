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

¿Quieres que genere la estructura de tests para todos tus modelos automáticamente? Puedo crear los archivos base para cada uno si lo necesitas.
