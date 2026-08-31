# Arquitectura C4

Este documento describe el estado objetivo de Fase 0. Los componentes cloud y el frontend son planificados, no implementados todavia.

## Nivel 1: Contexto

```mermaid
flowchart LR
    Visitor[Visitante / Comprador] --> Platform[Commerce Platform]
    Seller[Vendedor] --> Platform
    Moderator[Moderador] --> Platform
    Admin[Administrador] --> Platform
    Platform --> Email[Proveedor de email]
    Platform --> Storage[Object storage]
    Platform --> Aws[AWS: eventos, observabilidad y hosting]
```

## Nivel 2: Contenedores

```mermaid
flowchart TB
    Web[Storefront y Admin: Next.js\nPlanificado] --> Api[Commerce Core: Spring Boot\nImplementado]
    Api --> Db[(PostgreSQL\nImplementado)]
    Api --> Redis[(Redis\nConfigurado)]
    Api --> Blob[Azure Blob / S3\nAdaptador existente / migracion planificada]
    Api --> Mail[Azure Email o SMTP\nImplementado]
    Api --> Outbox[Outbox Worker\nPlanificado]
    Outbox --> CloudEvents[EventBridge + SQS\nPlanificado]
    CloudEvents --> Workers[Workers de notificacion/analitica\nPlanificado]
```

## Nivel 3: Componentes del Commerce Core

```mermaid
flowchart TB
    Rest[REST Controllers + OpenAPI] --> UseCases[Casos de uso]
    GraphQL[GraphQL Support Adapter] --> UseCases
    Auth[JWT Security Filter] --> Rest
    Auth --> GraphQL
    UseCases --> Domain[Dominio y puertos]
    Domain --> Persistence[JPA adapters]
    Domain --> External[Email, storage, token adapters]
    Persistence --> Db[(PostgreSQL)]
    External --> Providers[Proveedores externos]
```

## Limites de dominio objetivo

| Contexto | Responsabilidad | Estado |
|---|---|---|
| Identity | Registro, login, refresh, revocacion y perfiles | Implementado parcialmente |
| Catalog | Productos, categorias, imagenes y publicacion | Implementado parcialmente |
| Cart | Carrito y sus lineas | Implementado como CRUD; requiere redisenio |
| Orders | Confirmacion, transiciones e historial | CRUD actual; flujo planificado |
| Inventory | Stock y reservas | Persistencia existente; flujo planificado |
| Support | Incidencias, reportes y apelaciones | Implementado parcialmente |
| Notifications | Registro y entrega asincrona | Adaptadores existentes; eventos planificados |
| Analytics | Eventos de negocio y datos de BI | Planificado |
