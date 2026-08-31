# Fase 0: Alcance y Flujos de Negocio

## Proposito

Definir el producto que se construira antes de modificar comportamiento. Este documento diferencia de forma explicita entre lo implementado, lo comprometido para las proximas fases y lo fuera de alcance.

## Vision

Commerce Platform es un marketplace B2C con catalogo, carrito, confirmacion de pedido, inventario, soporte y operacion administrativa. El producto se presentara como una plataforma operable y observable, no solo como una coleccion de endpoints.

## Actores

| Actor | Necesidad principal | Permisos objetivo |
|---|---|---|
| Visitante | Descubrir catalogo publico | Leer productos y categorias activas |
| Comprador (`USER`) | Gestionar su compra y soporte | Su cuenta, carrito, pedidos, favoritos e incidencias propios |
| Vendedor (`SELLER`) | Publicar y operar su catalogo | Sus productos, imagenes y stock asignado |
| Moderador (`MODERATOR`) | Resolver contenido o conflictos | Incidencias y apelaciones asignadas |
| Administrador (`ADMIN`) | Operar la plataforma | Gestion de categorias, usuarios, pedidos y auditoria |

## Flujos comprometidos

### Catalogo publico

1. El visitante lista, busca y filtra productos `ACTIVE`.
2. El visitante consulta el detalle, imagenes y categoria del producto.
3. Las mutaciones de catalogo requieren un rol y una regla de propiedad explicitos.

### Carrito y pedido sin pago

1. Un comprador autenticado crea o recupera su carrito propio.
2. Agrega, actualiza o elimina lineas de carrito.
3. Al confirmar, el sistema valida producto, precio y stock; crea un pedido inmutable en `PENDING_CONFIRMATION` y reserva inventario.
4. Un administrador confirma, despacha, completa o cancela el pedido. Una cancelacion libera la reserva de inventario.
5. Cada transicion relevante deja una entrada de auditoria y un evento de dominio.

`PENDING_CONFIRMATION` no significa pago aprobado. No se integran PSP, tarjetas, webhooks de pago ni conciliacion durante este alcance.

### Soporte y moderacion

1. Un usuario autenticado abre una incidencia sobre un producto o pedido propio.
2. Un moderador revisa y resuelve la incidencia.
3. Un vendedor afectado puede presentar una apelacion.
4. La resolucion y apelacion deben registrar actor, estado anterior, estado final y motivo.

### Operacion administrativa

1. Un administrador administra categorias y consulta pedidos.
2. Un vendedor administra solo sus productos y sus imagenes.
3. Los operadores consultan auditoria y fallos de procesamiento, pero no secretos, tokens ni datos personales innecesarios.

## Fuera de alcance actual

- Procesamiento de pagos, PSP, datos de tarjetas, reembolsos financieros o webhooks de pago.
- Marketplace financiero con liquidacion a vendedores.
- Kubernetes local, Kafka local y OpenSearch local.
- Microservicios sin limite de dominio ni necesidad de despliegue independiente.
- IA que escriba en pedidos, inventario, usuarios o soporte.

## Estado actual y brechas que deben cerrarse

| Area | Estado actual | Decision de Fase 0 |
|---|---|---|
| Autorizacion | Varias rutas solo requieren autenticacion; falta ownership | Tratarlo como riesgo P0 antes de exponer frontend |
| Carrito | CRUD por `userId` o ID; no hay API de lineas ni ownership consistente | Redisenar contrato en Fase 1 |
| Pedido | CRUD generico; no consume carrito ni reserva stock | Construir flujo de confirmacion en Fase 2 |
| Inventario, envio, devolucion | Tablas y adaptadores presentes, sin interfaz de entrada completa | No declararlos como feature terminada |
| Eventos | Outbox y auditoria disponibles para confirmacion de pedido | Extender a los demas cambios de estado |
| Pagos | Tablas/scaffolding presentes | Congelado: no ampliar ni integrar |

## SLOs iniciales

Estas metas se validan primero en local con datos reproducibles y despues en `staging`.

| Indicador | Objetivo inicial | Medicion |
|---|---|---|
| Catalogo de lectura p95 | Menor a 300 ms | k6 + trazas |
| Escritura p95 sin servicios externos | Menor a 800 ms | k6 + trazas |
| Errores HTTP 5xx | Menor a 0.5% | Metricas de aplicacion |
| Procesamiento de evento p95 | Menor a 60 s | Edad del evento Outbox |
| Recuperacion de worker | Sin perdida; reintento idempotente | Prueba de fallo controlado |

## Criterios transversales de aceptacion

- Ningun endpoint autenticado permite acceder o modificar recursos de otro usuario sin privilegio administrativo explicito.
- Todo cambio de estado de pedido, inventario, incidencia o apelacion es auditable.
- Las operaciones de escritura criticas tienen idempotencia o una semantica de reintento definida.
- Las listas publicas y administrativas son paginadas, ordenadas y limitadas.
- Los contratos REST se publican en OpenAPI y los cambios incompatibles se versionan.
