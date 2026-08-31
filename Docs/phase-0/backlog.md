# Backlog Priorizado

## Convenciones

- `P0`: bloquea seguridad, integridad o el inicio seguro del frontend.
- `P1`: necesario para el primer flujo de producto demostrable.
- `P2`: mejora posterior con una dependencia previa clara.

## P0: Antes del storefront

| ID | Historia | Criterio de aceptacion | Evidencia |
|---|---|---|---|
| P0-01 | Como comprador, solo accedo a mis recursos | Carrito, pedido, perfil, favoritos e incidencias validan identidad y propiedad en el caso de uso | Pruebas negativas 401/403/404 segun contrato |
| P0-02 | Como administrador, opero solo funciones administrativas | Categorias, usuarios y operaciones administrativas exigen `ADMIN` | Pruebas de matriz RBAC |
| P0-03 | Como vendedor, gestiono solo mi catalogo | Crear, editar, archivar y subir imagen exige `SELLER` y ownership | Pruebas de ownership |
| P0-04 | Como equipo, conozco el estado real del sistema | Documentacion de endpoints, roles, esquema y modulos coincide con codigo y migraciones | Revision de docs y OpenAPI |
| P0-05 | Como equipo, pruebo Postgres real | Integracion critica usa Testcontainers PostgreSQL; Redis se agrega a los flujos que lo requieran | Pipeline verde |
| P0-06 | Como operador, diagnostico fallos | Logs estructurados incluyen `traceId`; secretos, JWT y credenciales estan excluidos | Prueba de logs |

## P1: Primer flujo de comercio

| ID | Historia | Criterio de aceptacion | Dependencia |
|---|---|---|---|
| P1-01 | Como comprador, administro lineas de mi carrito | Endpoints semanticos para agregar, cambiar cantidad y eliminar linea; nunca reciben un `userId` controlable | P0-01 |
| P1-02 | Como comprador, confirmo un pedido sin pago | Se crea `PENDING_CONFIRMATION` desde un carrito propio, con precio capturado e idempotencia | P1-01 |
| P1-03 | Como sistema, protejo el stock | Reserva atomica, expiracion y liberacion por cancelacion; no hay sobreventa en prueba concurrente | P1-02 |
| P1-04 | Como operador, gestiono estados de pedido | Transiciones validas y autorizadas, con historial de auditoria | P1-02 |
| P1-05 | Como sistema, publico hechos confiables | Outbox se guarda en la transaccion y el worker reintenta sin duplicar efectos | P1-02 |
| P1-06 | Como usuario, recibo notificaciones no bloqueantes | El worker consume eventos de pedido y registra resultado de envio | P1-05 |

## P1: Frontend y calidad

| ID | Historia | Criterio de aceptacion | Dependencia |
|---|---|---|---|
| P1-07 | Como visitante, exploro el catalogo | Storefront responsive con busqueda, filtros, paginacion, detalle y estados de error | P0-04 |
| P1-08 | Como operador, uso back-office | Admin con inventario, pedidos, soporte y auditoria segun RBAC | P0-02, P1-04 |
| P1-09 | Como equipo, evito regresiones | Unitarias, integracion, E2E y smoke tests ejecutan en CI | P0-05 |
| P1-10 | Como equipo, cumplo objetivos de rendimiento | Escenarios k6 reproducibles para catalogo, carrito y pedido | P1-02 |

## P2: Plataforma y datos

| ID | Historia | Criterio de aceptacion | Dependencia |
|---|---|---|---|
| P2-01 | Como equipo, despliego AWS reproducible | Terraform crea `dev` con tags de costo y sin secretos persistentes | P1-09 |
| P2-02 | Como plataforma, desacoplo eventos cloud | Adaptador EventBridge/SQS, DLQ, alarmas y consumidor idempotente | P1-05 |
| P2-03 | Como negocio, observo operacion | Eventos exportados a S3/Parquet y dashboard de conversion/stock | P1-05 |
| P2-04 | Como usuario, recibo recomendaciones explicables | Ranking no generativo basado en catalogo y eventos anonimizados | P2-03 |
| P2-05 | Como equipo, mejoro busqueda a escala | Evaluar OpenSearch solo con evidencia de limite en PostgreSQL | P1-10 |
