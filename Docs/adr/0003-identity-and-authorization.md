# ADR-0003: JWT Actual con Autorizacion por Recurso

- Estado: Aceptado con remediacion pendiente
- Fecha: 2026-08-30

## Contexto

El backend ya emite JWT, usa refresh tokens y mantiene revocacion. Tiene roles `USER`, `ADMIN`, `SELLER` y `MODERATOR`, pero varias rutas autenticadas no validan ownership ni el rol descrito por el producto.

## Decision

Mantener JWT durante la estabilizacion del core. Implementar autorizacion como regla de caso de uso para cada recurso propio, no solo como filtro HTTP. Las rutas de mutacion administrativa exigiran rol explicito.

El frontend utilizara una sesion basada en cookies `HttpOnly`, `Secure` y `SameSite` cuando se implemente. No se almacenaran tokens de larga vida en `localStorage`.

Amazon Cognito u otro proveedor OIDC se evaluara antes de produccion publica, no durante la Fase 0.

## Consecuencias

- Se cierra el riesgo de IDOR antes de exponer funcionalidades a usuarios reales.
- La identidad no se reescribe sin una necesidad de negocio o cumplimiento.
- Las pruebas de seguridad deben incluir accesos cruzados por rol y propietario.
