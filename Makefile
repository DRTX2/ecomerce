# Makefile for E-commerce Backend Project
# Provides convenient shortcuts for common development tasks

.PHONY: help build test test-unit test-integration coverage coverage-verify \
security clean run run-dev run-prod docker-build docker-up docker-down \
docker-logs docker-restart db-migrate db-clean db-info lint format deps \
deps-updates ci-local install env-example watch-test actuator actuator-metrics

# Default target
.DEFAULT_GOAL := help

help: ## Show this help message
	@echo "📋 Available commands:"
	@grep -E '^[a-zA-Z_-]+:.*?## .*$$' $(MAKEFILE_LIST) | awk 'BEGIN {FS = ":.*?## "}; {printf "  \033[36m%-20s\033[0m %s\n", $$1, $$2}'

build: ## Build the project without tests
	@echo "🔨 Building project..."
	./gradlew clean build -x test

test: ## Run all tests
	@echo "🧪 Running tests..."
	./gradlew test

test-unit: ## Run only unit tests
	@echo "🧪 Running unit tests..."
	./gradlew test --tests '*Test'

test-integration: ## Run only integration tests
	@echo "🧪 Running integration tests..."
	./gradlew test --tests '*IT'

coverage: ## Generate test coverage report
	@echo "📊 Generating coverage report..."
	./gradlew jacocoTestReport
	@echo "✅ Coverage report generated at: build/reports/jacoco/test/html/index.html"

coverage-verify: ## Verify coverage meets threshold
	@echo "🎯 Verifying coverage threshold..."
	./gradlew jacocoTestCoverageVerification

security: ## Run security vulnerability check
	@echo "🔒 Running security scan..."
	./gradlew dependencyCheckAnalyze
	@echo "✅ Security report generated at: build/reports/dependency-check-report.html"

clean: ## Clean build artifacts
	@echo "🧹 Cleaning build artifacts..."
	./gradlew clean
	rm -rf build/

run: ## Run the application locally
	@echo "🚀 Starting application..."
	./gradlew bootRun

run-dev: ## Run with dev profile
	@echo "🚀 Starting application (dev profile)..."
	SPRING_PROFILES_ACTIVE=dev ./gradlew bootRun

run-prod: ## Run with prod profile
	@echo "🚀 Starting application (prod profile)..."
	SPRING_PROFILES_ACTIVE=prod ./gradlew bootRun

docker-build: ## Build Docker image
	@echo "🐳 Building Docker image..."
	docker build -t ecomerce-backend:latest .

docker-up: ## Start all services with Docker Compose
	@echo "🐳 Starting Docker services..."
	docker-compose up -d
	@echo "✅ Services started. Application available at http://localhost:8080"

docker-down: ## Stop all Docker services
	@echo "🐳 Stopping Docker services..."
	docker-compose down

docker-logs: ## Show Docker logs
	@echo "📋 Docker logs..."
	docker-compose logs -f

docker-restart: docker-down docker-up ## Restart Docker services

db-migrate: ## Run database migrations
	@echo "🗄️ Running database migrations..."
	./gradlew flywayMigrate

db-clean: ## Clean database
	@echo "🗄️ Cleaning database..."
	./gradlew flywayClean

db-info: ## Show migration info
	@echo "🗄️ Database migration info..."
	./gradlew flywayInfo

lint: ## Run code style checks (if configured)
	@echo "📝 Running code style checks..."
	# ./gradlew checkstyleMain checkstyleTest
	@echo "⚠️  Linting not configured yet"

format: ## Format code (if configured)
	@echo "✨ Formatting code..."
	# ./gradlew spotlessApply
	@echo "⚠️  Code formatting not configured yet"

deps: ## Show dependency tree
	@echo "📦 Dependency tree..."
	./gradlew dependencies

deps-updates: ## Check for dependency updates
	@echo "📦 Checking for dependency updates..."
	./gradlew dependencyUpdates

ci-local: clean build test coverage-verify security ## Run full CI pipeline locally
	@echo "✅ Local CI pipeline completed successfully!"

install: ## Initial project setup
	@echo "⚙️ Setting up project..."
	@chmod +x ./gradlew
	@./gradlew wrapper --gradle-version=8.5
	@echo "✅ Project setup complete!"

env-example: ## Create .env.example from .env
	@echo "📝 Creating .env.example..."
	@if [ -f .env ]; then \
		sed 's/=.*/=/' .env > .env.example; \
		echo "✅ .env.example created"; \
	else \
		echo "❌ .env file not found"; \
	fi

# Development helpers
watch-test: ## Run tests in watch mode
	@echo "👀 Watching tests..."
	./gradlew test --continuous

actuator: ## Show actuator endpoints
	@echo "🏥 Actuator health check..."
	@curl -s http://localhost:8080/actuator/health | jq . || echo "❌ Application not running"

actuator-metrics: ## Show application metrics
	@echo "📊 Application metrics..."
	@curl -s http://localhost:8080/actuator/metrics | jq . || echo "❌ Application not running"

