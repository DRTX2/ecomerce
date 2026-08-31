---
name: postgres-migrations
description: Use when creating or reviewing Flyway PostgreSQL migrations, indexes, transactional data changes, locking, Outbox, audit, or database tests.
---

# PostgreSQL Migrations

- Create immutable forward-only Flyway migrations; never edit a migration that may have been applied.
- Add indexes for foreign keys, pending queues, and measured query paths only.
- Use conditional updates or row locking for inventory, never application-side read-modify-write.
- Verify PostgreSQL behavior with Testcontainers, not H2, for JSONB, locks, partial indexes, and migrations.
- Keep OLTP writes narrow and move side effects through the Transactional Outbox.
