# ⚙️ Operations & Deployment Guide

This guide ensures a smooth setup for both local development and production environments, leveraging Docker for consistency.

## 🚀 Quick Start (Docker)

The fastest way to get the system running.

### Prerequisites
- Docker & Docker Compose
- RAM: Minimum 2GB allocated to Docker

### Steps
1. **Clone & Configure**:
   ```bash
   git clone <repo-url>
   cd ecomerce-backend
   cp .env.example .env
   # Edit .env with your secrets
   ```

2. **Launch**:
   ```bash
   docker-compose up --build -d
   ```
   - backend: `http://localhost:8080`
   - postgres: `localhost:5432`

3. **Verify Health**:
   ```bash
   curl http://localhost:8080/actuator/health
   # Should return {"status":"UP"}
   ```

## 🛠️ Local Development (Manual)

For active development and debugging.

### Prerequisites
- Java 21 LTS (Eclipse Temurin recommended)
- PostgreSQL 16
- Gradle 8+ (Wrapper included)

### Configuration
Update `src/main/resources/application.yml` or set env vars:
```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/ecomerce
    username: ${DB_USERNAME}
    password: ${DB_PASSWORD}
```

### Running the App
```bash
./gradlew bootRun
```

## 📦 Production Considerations

### Optimization
- **Multistage Docker Build**: The `Dockerfile` uses a multistage process.
    - *Stage 1*: Build with JDK (compilation).
    - *Stage 2*: Runtime with JRE (smaller, secure image).
- **JVM Flags**: In production, consider tuning `-Xmx` and `-Xms` based on container limits.

### Monitoring
- **Actuator**: The app exposes endpoints like `/health` and `/info`.
- **Security**: Be sure to restrict access to Actuator endpoints in `SecurityConfig` for production builds.

### CI/CD Pipeline Suggestion
1. **Commit**: Trigger GitHub Action.
2. **Test**: Run `./gradlew test`.
3. **Quality**: SonarQube scan.
4. **Build**: `docker build -t ecomerce-backend .`.
5. **Deploy**: Push to registry and update Kubernetes/ECS.
