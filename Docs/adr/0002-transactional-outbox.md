# ADR-0002: Eventos con Transactional Outbox

- Estado: Aceptado
- Fecha: 2026-08-30

## Contexto

Los cambios de pedido, inventario y soporte deben producir efectos asincronos sin perder eventos si la aplicacion falla entre confirmar PostgreSQL y publicar. Un broker local permanente excede el presupuesto de recursos del equipo.

## Decision

Guardar eventos de dominio en una tabla `outbox_events` dentro de la misma transaccion del agregado. Un worker idempotente los reclama, publica y registra resultado.

En local el worker procesa desde PostgreSQL. En AWS, un adaptador publica a EventBridge y enruta a SQS con DLQ. El dominio solo depende de un puerto de publicacion de eventos.

## Consecuencias

- No hay perdida por dual-write entre base de datos y mensajeria.
- Los consumidores deben ser idempotentes y manejar reintentos.
- Se agrega latencia eventual; los casos de uso HTTP no deben depender de notificaciones completadas.
- El worker y la tabla Outbox requieren metricas de edad, intentos, errores y eventos en fallo.
