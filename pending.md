Para una demo sólida ya tienes
- Backend Spring con catálogo, carrito, confirmación de pedido, reserva atómica de stock, auditoría y Outbox.
- JWT, refresh, revocación, ownership de recursos críticos y RBAC básico.
- Storefront Next.js con catálogo, filtros, detalle, bolsa, login BFF, checkout y cuenta/pedidos.
- PostgreSQL Testcontainers para reserva de stock.
- Documentación de demo y arquitectura.
Falta para cerrar la fase actual
 1. UX completa de bolsa: abrir/cerrar panel, eliminar líneas, cambiar cantidades y total visible.
 2. Gestión de sesión UX: mostrar usuario autenticado, cerrar sesión desde navegación y feedback consistente.
 3. Pruebas E2E Playwright del recorrido: login, agregar producto, checkout, pedido visible en cuenta.
 4. Pruebas de integración PostgreSQL del flujo completo carrito -> stock -> pedido -> auditoría -> Outbox.
 5. Endpoint de catálogo con búsqueda, filtros, orden y paginación reales en backend. Hoy los filtros son locales sobre el catálogo completo.
 6. API de carrito por líneas (add, update quantity, remove) en lugar de recrear el carrito completo durante checkout.
 7. Worker Outbox con destino real: notificación/email o EventBridge/SQS. Actualmente procesa y registra localmente.
 8. Auditoría administrativa consultable con filtros y paginación.
 9. Corregir la suite completa existente: aún hay pruebas históricas rotas por H2/Flyway, controladores obsoletos y tests de correo real.
10. Resolver las vulnerabilidades transitivas de Next/PostCSS. El upgrade seguro requiere evaluar la migración a Next 16.
Para producción/cloud después
- Terraform AWS: VPC, RDS, ECS, ECR, S3, Secrets Manager, KMS, ALB, WAF, CloudFront, EventBridge y SQS.
- CI/CD GitHub Actions con OIDC, SAST, dependencias, SBOM y escaneo de imágenes.
- OpenTelemetry, logs JSON, métricas, alertas y trazas.
- BI sobre S3/Parquet/Athena.
- Pagos, solo cuando se decida el PSP y el modelo de negocio.
La prioridad inmediata para presentar sería completar los puntos 1 a 4.
