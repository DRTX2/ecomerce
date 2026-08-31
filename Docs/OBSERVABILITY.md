# Observability

## Signals

- `GET /api/v1/actuator/health`: health and readiness evidence.
- `GET /api/v1/actuator/prometheus`: Prometheus metrics.
- OTLP traces: configure `OTEL_EXPORTER_OTLP_ENDPOINT` with a collector endpoint.
- Production logs are JSON and include Micrometer trace and span identifiers.

The management endpoints remain behind Spring Security. Expose them only through an internal network and scrape them with a dedicated service credential; never add them to CORS or public ingress.

## Initial Alerts

`observability/prometheus-alerts.yml` supplies initial availability, 5xx rate, and write-latency rules. Add database-health and Outbox-age alerts when the production collector exports those dependencies as targets and gauges.

## Local Demo

Use the default console logs and `/actuator/prometheus`. Do not run a collector, Prometheus, Grafana, or Tempo locally unless demonstrating observability; they exceed the normal development resource profile.
