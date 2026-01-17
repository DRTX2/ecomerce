# Makefile - E-commerce Backend

.DEFAULT_GOAL := help
.PHONY: help build test coverage clean run docker db ci install

## ───────────────
## Help
## ───────────────
help:
	@echo "📋 Available commands:"
	@grep -E '^[a-zA-Z_-]+:.*?## .*$$' $(MAKEFILE_LIST) | \
	awk 'BEGIN {FS = ":.*?## "}; {printf "  \033[36m%-15s\033[0m %s\n", $$1, $$2}'

## ───────────────
## Build & Test
## ───────────────
build: ## Build project
	./gradlew clean build -x test

test: ## Run all tests
	./gradlew test

coverage: ## Generate coverage report
	./gradlew jacocoTestReport

clean: ## Clean build artifacts
	./gradlew clean

## ───────────────
## Run
## ───────────────
run: ## Run app (default profile)
	./gradlew bootRun

run-dev: ## Run app (dev profile)
	SPRING_PROFILES_ACTIVE=dev ./gradlew bootRun

## ───────────────
## Docker
## ───────────────
docker: ## Build & start containers
	docker compose up -d --build

docker-down: ## Stop containers
	docker compose down

## ───────────────
## Database
## ───────────────
db: ## Run DB migrations
	./gradlew flywayMigrate

## ───────────────
## CI
## ───────────────
ci: clean build test coverage ## Run local CI pipeline
	@echo "✅ CI passed"

## ───────────────
## Setup
## ───────────────
install: ## Initial setup
	chmod +x ./gradlew
