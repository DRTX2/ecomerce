---
name: spring-commerce
description: Use when changing Java Spring Boot commerce flows, hexagonal ports, REST or GraphQL contracts, orders, carts, stock, or asynchronous events.
---

# Spring Commerce

- Keep domain rules in application use cases; controllers only adapt HTTP and authenticated principals.
- Never accept price, order state, stock, ownership, or user IDs from a buyer-controlled request.
- Use a single application transaction for stock reservation, order persistence, audit, and Outbox writes.
- Enforce resource ownership in the use case and return non-enumerating 404 responses for foreign resources.
- Preserve ports at infrastructure boundaries and add focused unit plus PostgreSQL integration coverage for critical flows.
