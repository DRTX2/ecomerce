# 📚 Technical Documentation Hub

Welcome to the comprehensive technical documentation for the E-commerce Backend. This documentation is designed to provide a deep understanding of the system's architecture, design decisions, and operational guides. It serves as a testament to the engineering quality and best practices applied throughout the project.

## 🗂️ Table of Contents

### 1. 📘 [Detailed System Specifications](./detailed_specs.md)
**The Master Document**. Contains:
- **Full Feature List**: Deep dive into Commerce, Identity, and Support modules.
- **Class Diagrams**: Visual breakdown of the **Hexagonal Architecture** per module.
- **Security Matrix**: Exact breakdown of roles and endpoint protection.
- **Test Inventory**: A list of key test cases and their acceptance criteria.

### 2. 🏗️ [Architecture & Design Decisions](./architecture.md)
A deep dive into the **Hexagonal Architecture** implementation.
- *Why Hexagonal? Benefits for scalability.*
- *Layer breakdown: Core, Ports, Adapters.*
- *Dependency Inversion Principle in action.*
- *Code organization and package structure.*

### 3. 🧪 [Testing Strategy & Quality Assurance](./testing_strategy.md)
How we ensure code reliability and maintainability.
- *Testing Pyramid: Unit, Integration, and E2E.*
- *Tools: JUnit 5, Mockito, AssertJ.*
- *Test coverage and conventions.*

### 4. 🔌 [API & GraphQL Reference](./api_reference.md)
Detailed usage guide for consumers of the API.
- *REST Best Practices (Status codes, Verbs).*
- *Authentication flow (JWT).*
- *GraphQL Schema overview.*
- *Interactive examples.*

### 5. ⚙️ [Operations & Deployment Guide](./setup_guide.md)
From local development to production deployment.
- *Docker & Docker Compose orchestration.*
- *Environment configuration (.env).*
- *CI/CD pipeline considerations.*
- *Database management.*

### 6. 🔒 [Security Implementation](./security.md)
Analysis of the security layer.
- *Stateless Authentication (JWT).*
- *Role-Based Access Control (RBAC).*
- *CORS and CSRF configurations.*

---
> **Note for Recruiters/Reviewers:** This project demonstrates advanced Spring Boot capabilities, clean code principles, and a focus on enterprise-grade architecture. The documentation reflects a commitment to clarity and knowledge sharing.
