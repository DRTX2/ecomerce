# GitHub Actions CI/CD - Quick Reference

## 📋 Workflows Overview

| Workflow | Trigger | Purpose | Status |
|----------|---------|---------|--------|
| **CI/CD Pipeline** | Push/PR to main/develop | Build, test, security scan | Required ✅ |
| **CodeQL Analysis** | Push/PR/Schedule (weekly) | Security vulnerability detection | Required ✅ |
| **PR Validation** | Pull request opened | Validate PR format and requirements | Required ✅ |

## 🚀 Quick Commands

### Local Development

```bash
# Run all tests
make test

# Generate coverage report
make coverage

# Run security scan
make security

# Full CI pipeline locally
make ci-local

# Start application
make run
```

### Docker

```bash
# Start all services
make docker-up

# Stop all services
make docker-down

# View logs
make docker-logs

# Restart services
make docker-restart
```

## ✅ Pre-Push Checklist

Before pushing to remote:

```bash
# 1. Clean build
make clean build

# 2. Run tests
make test

# 3. Check coverage
make coverage-verify

# 4. Security scan
make security

# 5. Format code (if configured)
make format
```

## 📊 Coverage Requirements

- **Overall**: ≥60%
- **Per class**: ≥50%
- **Excluded**: config, dto, mapper, entity, exceptions

## 🔒 Security Standards

- **OWASP**: Fails on CVSS ≥7
- **CodeQL**: Weekly scans
- **Dependencies**: Auto-checked on PR

## 📝 Commit Convention

```
feat: add user authentication
fix: resolve JWT token expiration
docs: update API documentation
refactor: improve order service
test: add integration tests for cart
chore: update dependencies
```

## 🔍 Debugging Failed CI

### Build Failure
```bash
# Locally rebuild
./gradlew clean build --stacktrace
```

### Test Failure
```bash
# Run specific test
./gradlew test --tests "ClassName.methodName"

# Run with detailed logs
./gradlew test --info
```

### Coverage Below Threshold
```bash
# Generate report
./gradlew jacocoTestReport

# Open report
open build/reports/jacoco/test/html/index.html
```

### Security Vulnerability
```bash
# Run scan
./gradlew dependencyCheckAnalyze

# View report
open build/reports/dependency-check-report.html

# Update dependency or suppress false positive
```

## 📦 Artifacts Generated

| Artifact | Location | Retention |
|----------|----------|-----------|
| Build artifacts | `build/libs/*.jar` | 1 day |
| Test results | `build/test-results/` | 7 days |
| Coverage reports | `build/reports/jacoco/` | 7 days |
| Security reports | `build/reports/dependency-check-report.html` | 7 days |

## 🎯 Performance Targets

| Metric | Target | Current |
|--------|--------|---------|
| Build time | <5 min | - |
| Test success rate | 100% | - |
| Code coverage | ≥60% | - |
| Security vulns | 0 critical | - |

## 🔄 Workflow Dependencies

```
PR Validation
    ↓
Build & Compile
    ↓
├── Run Tests
├── Code Quality & Security
└── Docker Build (main only)
    ↓
CI Summary
```

## 🛠️ Maintenance Tasks

### Weekly
- Review CodeQL security alerts
- Check dependency updates: `make deps-updates`

### Monthly
- Review and update OWASP suppressions
- Analyze coverage trends
- Optimize slow tests

### Quarterly
- Update Gradle version
- Review and update CI/CD workflows
- Audit security configurations

## 📞 Support

- **CI Issues**: Check [CI/CD Guide](.github/CI_CD_GUIDE.md)
- **Security**: Review [OWASP suppressions](../owasp-suppressions.xml)
- **Coverage**: See [Testing Guide](../Docs/TESTING_GUIDE.md)

---

**Pro Tip**: Run `make help` to see all available commands!

