---
name: aws-terraform
description: Use when adding AWS, Terraform, CI/CD, ECS, RDS, S3, EventBridge, SQS, IAM, KMS, or deployment configuration.
---

# AWS Terraform

- Use Terraform modules and isolated dev, staging, and production environments.
- Prefer GitHub Actions OIDC and least-privilege IAM; never commit cloud credentials.
- Place compute and data services in private subnets; expose only CloudFront, WAF, and ALB as required.
- Encrypt managed stores with KMS and keep secrets in Secrets Manager.
- Apply cost tags and validate plans before apply; local development must not require AWS emulators permanently.
