# E-commerce Backend API

![CI/CD Pipeline](https://github.com/YOUR_USERNAME/YOUR_REPO/actions/workflows/ci.yml/badge.svg)
![Java](https://img.shields.io/badge/Java-21-orange?logo=openjdk)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.4.4-brightgreen?logo=spring)
![License](https://img.shields.io/badge/license-MIT-blue.svg)

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
- Make (optional, for convenience commands)

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
   # Using make (recommended)
   make docker-up
   
   # Or using docker-compose directly
   docker-compose up --build
   ```
   The API will be available at `http://localhost:8080/api/v1`.

### Alternative: Local Development

1. **Build the project**:
   ```bash
   make build
   # Or: ./gradlew clean build
   ```

2. **Run tests**:
   ```bash
   make test
   # Or: ./gradlew test
   ```

3. **Start application**:
   ```bash
   make run-dev
   # Or: ./gradlew bootRun
   ```

### 🛠️ Development Commands

We provide a `Makefile` with convenient shortcuts:

```bash
make help              # Show all available commands
make build             # Build without tests
make test              # Run all tests
make coverage          # Generate coverage report
make security          # Run security scan
make docker-up         # Start Docker services
make docker-down       # Stop Docker services
make ci-local          # Run full CI pipeline locally
```

## 🧪 Testing & Quality

### Running Tests

```bash
# All tests
make test

# Unit tests only
make test-unit

# Integration tests only
make test-integration

# With coverage
make coverage
```

### Code Coverage

- **Minimum overall coverage**: 60%
- **Per-class minimum**: 50%
- View reports: `build/reports/jacoco/test/html/index.html`

### Security Scanning

```bash
# Run OWASP dependency check
make security

# View report
open build/reports/dependency-check-report.html
```

## 🔄 CI/CD Pipeline

This project uses **GitHub Actions** for continuous integration and deployment:

- ✅ **Build & Compile**: Validates code compilation
- ✅ **Automated Tests**: Runs unit and integration tests with PostgreSQL & Redis
- ✅ **Code Coverage**: JaCoCo reports with 60% minimum threshold
- ✅ **Security Scanning**: OWASP dependency check + CodeQL analysis
- ✅ **Docker Validation**: Ensures Docker images build successfully
- ✅ **PR Validation**: Enforces conventional commits and quality standards

**Quick Reference**: [CI/CD Guide](.github/CI_QUICK_REFERENCE.md) | [Detailed Documentation](.github/CI_CD_GUIDE.md)

## 📚 Documentation

For full project documentation (detailed guides, architecture decisions, and API reference):

| 📚 Documentation |
|------------------|
| ➡️ **[Explore Technical Documentation](./Docs/README.md)** |

<!-- esto se agrego -->
## 🗺️ Roadmap & Future Improvements

This project is in continuous evolution. Here is the strategic plan for upcoming versions:

- **Performance**:
  - [ ] Implement Redis for caching frequent product queries.
  - [ ] Add Database Indexing optimization.
- **Features**:
  - [ ] Multi-vendor support (Marketplace model).
  - [ ] Payment Gateway Integration (Stripe/PayPal).
  - [ ] AI-powered product recommendations.
- **DevOps**:
  - [x] ✅ CI/CD Pipeline with GitHub Actions (Implemented!)
  - [x] ✅ Automated testing with coverage reports
  - [x] ✅ Security scanning (OWASP + CodeQL)
  - [ ] Kubernetes (K8s) manifests for scalable deployment.
  - [ ] Monitoring & Observability (Prometheus + Grafana)
  - [ ] Automated deployment to cloud (AWS/Azure/GCP)

## �👤 Author

**David** - *Backend Engineer*

🌐 Portfolio: [https://drtx2.github.io/portfolio/](https://drtx2.github.io/portfolio/)
