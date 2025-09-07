# Herramientas recomendadas para pruebas

Para asegurar una estrategia de pruebas robusta y eficiente en Java/Spring Boot, especialmente en equipos de bajos recursos, se recomiendan las siguientes herramientas:

| Herramienta         | Propósito                                      | Motivo de recomendación                  |
|---------------------|------------------------------------------------|------------------------------------------|
| JUnit 5             | Pruebas unitarias e integración                 | Estándar, ligero y rápido                |
| Mockito             | Mocking y stubbing en pruebas unitarias         | Ligero, fácil de usar                    |
| Spring Boot Test    | Pruebas de integración con contexto Spring      | Integración nativa, configurable         |
| JaCoCo              | Medición de cobertura de código                 | Bajo consumo, integración con Maven      |
| Rest Assured        | Pruebas de endpoints REST                       | Ligero, sintaxis sencilla                |
| Testcontainers (*)  | Pruebas de integración con servicios reales     | Útil pero opcional, requiere Docker      |
| OWASP Dep-Check (*) | Análisis de dependencias y seguridad            | Opcional, bajo consumo                   |
| JMeter/Gatling (*)  | Pruebas de rendimiento                         | Opcional, usar en modo CLI               |

(*) Opcional: usar solo si el equipo lo permite o en entornos de CI/CD.

**Consejos para equipos de bajos recursos:**
- Ejecuta pruebas desde la terminal (Maven/Gradle CLI) para ahorrar memoria.
- Prioriza pruebas unitarias y de integración rápidas.
- Usa perfiles de Spring para desactivar servicios pesados en tests.
- Evita IDEs pesados durante la ejecución de pruebas.

---

# Recomendaciones de pruebas para cobertura, robustez y calidad

Este documento describe las pruebas recomendadas para el proyecto e-commerce, categorizadas por dificultad, tiempo estimado e importancia. El objetivo es asegurar una alta cobertura, robustez y calidad en el sistema.

## 1. Pruebas unitarias

| Módulo/Componente                | Prueba sugerida                                 | Dificultad | Tiempo | Importancia |
|----------------------------------|-------------------------------------------------|------------|--------|-------------|
| Modelos (Cart, Product, User...) | Métodos y lógica de entidades                   | Baja       | Baja   | Alta        |
| Servicios de dominio             | Lógica de negocio aislada                       | Media      | Media  | Alta        |
| Utilidades y helpers             | Métodos utilitarios                             | Baja       | Baja   | Media       |

## 2. Pruebas de integración

| Módulo/Componente                | Prueba sugerida                                 | Dificultad | Tiempo | Importancia |
|----------------------------------|-------------------------------------------------|------------|--------|-------------|
| Casos de uso (UseCases)          | Flujos de negocio completos                     | Media      | Media  | Alta        |
| Repositorios                     | CRUD y consultas complejas                      | Media      | Media  | Alta        |
| Controladores REST               | Respuestas, validaciones, errores HTTP          | Media      | Media  | Alta        |
| Seguridad (login, JWT, roles)    | Autenticación y autorización                    | Alta       | Alta   | Crítica     |

## 3. Pruebas end-to-end (E2E)

| Flujo                            | Prueba sugerida                                 | Dificultad | Tiempo | Importancia |
|----------------------------------|-------------------------------------------------|------------|--------|-------------|
| Compra completa                  | Simulación de usuario real                      | Alta       | Alta   | Crítica     |
| Registro y login                 | Flujos de autenticación                         | Media      | Media  | Alta        |
| Manejo de errores                | Respuestas ante fallos y excepciones            | Baja       | Baja   | Media       |

## 4. Pruebas de seguridad

| Área                             | Prueba sugerida                                 | Dificultad | Tiempo | Importancia |
|----------------------------------|-------------------------------------------------|------------|--------|-------------|
| Autenticación y autorización     | Acceso a recursos protegidos                    | Alta       | Alta   | Crítica     |
| Protección ante ataques comunes  | Inyección, CSRF, XSS, etc.                      | Alta       | Alta   | Crítica     |

## 5. Pruebas de rendimiento

| Área                             | Prueba sugerida                                 | Dificultad | Tiempo | Importancia |
|----------------------------------|-------------------------------------------------|------------|--------|-------------|
| Checkout y compra                | Respuesta bajo carga                            | Alta       | Alta   | Media       |
| Concurrencia en pedidos          | Simulación de compras simultáneas               | Alta       | Alta   | Media       |

## 6. Pruebas de regresión

- Ejecutar todas las pruebas anteriores tras cada cambio relevante.
- Automatizar en CI/CD.

## 7. Pruebas adicionales recomendadas

| Tipo de prueba                        | Descripción breve                                                      | Dificultad | Tiempo | Importancia |
|---------------------------------------|------------------------------------------------------------------------|------------|--------|-------------|
| Compatibilidad                        | Diferentes versiones de Java, navegadores, bases de datos              | Media      | Media  | Media/Alta  |
| Migración y actualización             | Validar integridad tras migraciones o upgrades                         | Alta       | Alta   | Media       |
| Accesibilidad (si aplica)             | Usabilidad para personas con discapacidades                            | Media      | Media  | Media       |
| Localización/internacionalización     | Soporte de idiomas, monedas, formatos                                  | Media      | Media  | Media       |
| Backup y recuperación                 | Validar mecanismos de backup/restauración                              | Alta       | Alta   | Alta        |
| Logging y monitoreo                   | Verificar logs críticos y alertas de monitoreo                         | Baja       | Baja   | Media/Alta  |
| Límites y validaciones extremas       | Pruebas con datos en los límites permitidos                            | Media      | Media  | Alta        |
| Usabilidad (si aplica)                | Flujos intuitivos y amigables para el usuario                          | Media      | Media  | Media       |

---

### Recomendaciones generales
- Prioriza pruebas unitarias y de integración en los módulos de negocio y seguridad.
- Usa herramientas como JaCoCo para medir cobertura.
- Automatiza la ejecución de pruebas en cada commit (CI/CD).
- Documenta y actualiza los casos de prueba.
- Considera pruebas exploratorias y revisiones de código.

---

**Nota:** Esta lista es una guía inicial. Ajusta y amplía según evolucione el proyecto y los riesgos detectados.
