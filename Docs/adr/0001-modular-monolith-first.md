# ADR-0001: Monolito Modular Antes de Microservicios

- Estado: Aceptado
- Fecha: 2026-08-30

## Contexto

El proyecto ya usa una arquitectura hexagonal en un unico proceso Spring Boot. Los modulos aun comparten una base de datos y algunos flujos de comercio no estan completos. Extraer servicios ahora multiplicaria despliegues, contratos, observabilidad, CI/CD y consistencia distribuida antes de que existan limites operacionales reales.

## Decision

Mantener un monolito modular. Cada bounded context tendra casos de uso, puertos y adaptadores propios; la comunicacion entre contextos se expresara con eventos de dominio y contratos, no mediante dependencias arbitrarias entre controladores o repositorios.

Los candidatos futuros se extraen solo si cumplen al menos una condicion: escalado independiente medido, tecnologia especializada, ownership de datos independiente, aislamiento de fallo o ciclo de despliegue claramente distinto.

## Consecuencias

- Menor costo local y menor complejidad de operacion.
- Entregas mas rapidas mientras se estabilizan seguridad y flujos de negocio.
- Se requiere disciplina para evitar acoplamiento entre modulos.
- `notification-worker`, `search-indexer` y `ai-service` son candidatos, no servicios comprometidos.
