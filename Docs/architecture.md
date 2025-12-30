# 🏗️ System Architecture & Design Decisions

This document details the architectural choices made for the E-commerce Backend, focusing on modularity, testability, and long-term maintainability.

## 🎯 Architecture Diagram: Hexagonal (Ports & Adapters)

The system is built on the **Hexagonal Architecture** pattern. This ensures that the business logic (Core) remains agnostic of external technologies (Database, API, Email Providers).

```mermaid
graph TD
    subgraph "External World"
        Client[Web & Mobile Client]
        DB[(PostgreSQL)]
        EmailSystem[Azure Communication]
    end

    subgraph "Hexagon (The Application)"
        subgraph "Driving Adapters (In)"
            RestController[REST Controllers]
            GQLController[GraphQL Resolver]
            SecurityFilter[JWT Filter]
        end

        subgraph "Application Layer"
            UseCase[Use Case Implementations]
        end

        subgraph "Core Domain"
            DomainModel[Domain Entities]
            InputPorts[Input Ports (Interfaces)]
            OutputPorts[Output Ports (Interfaces)]
        end

        subgraph "Driven Adapters (Out)"
            JPARepo[JPA Repository Adapter]
            EmailAdapter[Email Adapter]
        end
    end

    Client --> RestController
    Client --> GQLController
    RestController --> InputPorts
    GQLController --> InputPorts
    InputPorts -.-> UseCase
    UseCase --> OutputPorts
    OutputPorts -.-> JPARepo
    OutputPorts -.-> EmailAdapter
    JPARepo --> DB
    EmailAdapter --> EmailSystem
```

## 🧠 Key Design Decisions

### 1. Hexagonal Architecture (Ports & Adapters)
**Why?** To decouple the Core domain from `Spring Boot` and `Hibernate`.
- **Benefit**: We can swap the database (e.g., from Postgres to Mongo) or the API type (REST to gRPC) without touching a single line of business logic.
- **Benefit**: Testing the Core is blazing fast as it requires no Spring Context.

### 2. Domain-Driven Design (DDD) Lightweight
We follow DDD principles by grouping code by feature (Vertical Slicing) rather than technical layer.
- Structure: `com.drtx.ecomerce.amazon.[feature]` (e.g., `product`, `order`).
- **Benefit**: High cohesion. A change in the "Cart" module doesn't accidentally break "User" logic.

### 3. DTO Patterns & MapStruct
We strictly separate **Persistence Entities** (Hibernate), **Domain Models** (Business), and **DTOs** (API).
- **MapStruct**: Used for zero-overhead, type-safe mapping.
- **Why?**:
    - Prevents leaking database details to the frontend (e.g., `@Version` or password hash fields).
    - Allows the internal model to evolve independently of the API contract.

### 4. Global Exception Handling
Centralized error handling using `@ControllerAdvice`.
- **Strategy**: Custom exceptions like `EntityNotFoundException` are thrown from the Core.
- **Handling**: `GlobalExceptionHandler` catches these and translates them into standardized HTTP 404/400 JSON responses.
- **Benefit**: Consistent error format across the entire API.

## 📦 Dependency Inversion

We heavily enforce the Dependency Inversion Principle (DIP).
- The `UseCase` (Application) defines an interface `ProductRepositoryPort`.
- The `Adapter` (Infrastructure) implements it `ProductRepositoryAdapter`.
- **Inversion**: The high-level module (Domain) does not depend on the low-level module (Database); both depend on abstractions.

## 🔄 Request Lifecycle

1. **Request**: `POST /api/v1/products`
2. **Security**: `JwtAuthFilter` validation.
3. **Adapter (In)**: `ProductController` accepts `ProductRequestDTO`.
4. **Mapper**: Converts DTO → `Product` (Domain Model).
5. **Use Case**: `createProduct(Product)` executes business rules.
6. **Adapter (Out)**: `ProductRepositoryAdapter` converts `Product` → `ProductEntity`.
7. **Persistence**: Saved via Spring Data JPA.
8. **Response**: Reverse flow, returning `ProductResponseDTO`.
