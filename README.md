# E-commerce Backend API

Robust and scalable backend for a modern e-commerce platform. Built with **Java 21** and **Spring Boot 3**, this project implements a pure **Hexagonal Architecture** to ensure separation of concerns, maintainability, and framework independence.

## 📝 Description

This project provides the business logic and infrastructure services required to operate a full-fledged e-commerce system. It manages everything from the product catalog and user authentication to complex order processing and hybrid customer support systems.

It is designed for developers looking for a solid reference in DDD (Domain-Driven Design) and Ports & Adapters Architecture implementations within the Spring ecosystem.

## ✨ Features

- **Authentication & Security**: Complete login/registration system with JWT and Spring Security.
- **Product Catalog**: Full CRUD for products and categories with inventory management.
- **Order Management**: Persistent shopping cart, wishlists, and order processing.
- **Hybrid Support**:
  - **REST**: For standard e-commerce operations.
  - **GraphQL**: Optimized API for the Incidence and Appeals module.
- **Notifications**: Integration with Azure Communication Services for transactional emails.
- **Efficient Mapping**: Uses MapStruct for type-safe DTO-Domain-Entity transformations with zero performance overhead.

## 🛠️ Technologies

### Core
- **Language**: Java 21
- **Framework**: Spring Boot 3.4.4
- **Build Tool**: Gradle

### Infrastructure & Data
- **Database**: PostgreSQL 16
- **Cache/Session**: (Ready for Redis/In-memory)
- **Containerization**: Docker & Docker Compose

### APIs
- **REST**: Spring Web MVC
- **GraphQL**: Spring for GraphQL

## 🔧 Key Tools & Dependencies

- **Lombok**: To reduce boilerplate code.
- **MapStruct**: High-performance, type-safe object mapping.
- **JJWT**: JSON Web Tokens implementation.
- **Azure Communication Email**: Cloud messaging service.
- **Hibernate Validator**: Input validation.

## 🧱 Architecture

This project strictly follows **Hexagonal Architecture (Ports & Adapters)**:

- **Core (Domain)**: Pure entities and business rules (no Spring dependencies).
- **Application (Use Cases)**: Business logic orchestration implementing input ports.
- **Adapters (Infrastructure)**:
  - **In**: REST Controllers and GraphQL Resolvers.
  - **Out**: JPA Repositories and external service adapters (Email).

```mermaid
graph TD
    Client[Web/Mobile Client] --> REST[REST Adapter]
    Client --> GQL[GraphQL Adapter]
    REST --> InputPort[Input Port (UseCase)]
    GQL --> InputPort
    InputPort --> Domain[Domain Logic]
    Domain --> OutputPort[Output Port]
    OutputPort --> Persistence[Persistence Adapter (JPA)]
    OutputPort --> Email[Email Adapter (Azure)]
```

## 🚀 Installation & Execution

### Prerequisites
- Docker and Docker Compose
- Java 21 (optional if using Docker)

### Quick Start (Recommended)

1. **Clone the repository**:
   ```bash
   git clone <repo-url>
   cd back
   ```

2. **Configure environment**:
   Create a `.env` file in the root directory based on `.env.example` (or define environment variables in your system).

3. **Run with Docker Compose**:
   ```bash
   docker-compose up --build
   ```
   The API will be available at `http://localhost:8080/api/v1`.

## 📚 Documentation

For full project documentation (detailed guides, architecture decisions, and API reference):

| 📚 Documentation |
|------------------|
| ➡️ **[Explore Technical Documentation](./Docs/README.md)** |

## 👤 Author

**David** - *Backend Engineer*

🌐 Portfolio: [https://drtx2.github.io/portfolio/](https://drtx2.github.io/portfolio/)
