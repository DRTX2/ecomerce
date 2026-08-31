---
name: security-qa
description: Use when reviewing authorization, secrets, authentication, uploads, sensitive data, test strategy, regression coverage, performance, or production readiness.
---

# Security And QA

- Verify RBAC and ownership with negative tests for every protected resource.
- Store passwords with a slow hash and secrets outside source control; redact tokens and PII from logs and audit metadata.
- Validate uploaded file type, size, and ownership before storage.
- Prefer integration tests with PostgreSQL Testcontainers for transactions, migrations, and concurrency.
- Use E2E tests for customer journeys and load tests only with reproducible scenarios and explicit budgets.
