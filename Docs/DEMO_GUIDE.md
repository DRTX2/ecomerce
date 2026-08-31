# Demo Guide

## Arranque

1. Inicia PostgreSQL y Redis con Docker Compose.
2. Ejecuta el backend con `./gradlew bootRun`.
3. Ejecuta el storefront con `npm run storefront:dev`.
4. Abre `http://localhost:3000`.

## Recorrido

1. Explora catalogo, busqueda y filtro por categoria.
2. Abre un producto activo y agregalo a la bolsa.
3. Inicia sesion desde la bolsa. Los tokens permanecen en cookies `HttpOnly` administradas por el BFF de Next.js.
4. Confirma el pedido. El backend reserva stock, crea la orden, registra auditoria y publica un evento Outbox en una transaccion.
5. Abre `/account` para consultar solo las ordenes del usuario autenticado.

## Evidencia tecnica

- `npm run storefront:build`
- `./gradlew compileJava`
- `./gradlew test --tests 'com.drtx.ecomerce.amazon.application.usecases.OrderUseCaseImplTest'`

La demo no procesa pagos. Los pedidos quedan en `PENDING_CONFIRMATION` y no se almacenan datos financieros.
