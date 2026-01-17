# CI/CD Pipeline Documentation

## Overview

This project uses **GitHub Actions** for Continuous Integration and Continuous Deployment. The CI/CD pipeline ensures code quality, security, and reliability through automated testing and analysis.

## Workflows

### 1. Main CI/CD Pipeline (`ci.yml`)

**Trigger**: Push or PR to `main` or `develop` branches

**Jobs**:

#### Build & Compile
- Validates Gradle wrapper integrity
- Compiles Java source code and tests
- Caches Gradle dependencies for faster builds
- Uploads build artifacts

#### Run Tests
- Spins up PostgreSQL 16 and Redis 7 containers
- Runs unit and integration tests
- Generates JaCoCo coverage reports
- Minimum coverage threshold: **60%**
- Uploads test results and coverage reports as artifacts

#### Code Quality & Security Analysis
- Runs **OWASP Dependency Check** for known vulnerabilities
- Fails build if CVE with CVSS ≥ 7 is found
- Generates security reports
- (Optional) Code style checks

#### Docker Build Validation
- Only runs on `main` branch pushes
- Validates Dockerfile builds successfully
- Uses build cache for optimization
- Does NOT push to registry (validation only)

#### CI Summary
- Aggregates results from all jobs
- Provides quick pass/fail status
- Fails if critical jobs (build/test) fail

---

### 2. CodeQL Security Analysis (`codeql.yml`)

**Trigger**: 
- Push to `main`/`develop`
- PRs to `main`
- Scheduled weekly (Mondays at 00:00 UTC)

**Purpose**: 
- Static application security testing (SAST)
- Detects security vulnerabilities in Java code
- Runs GitHub's advanced security queries
- Results appear in Security tab

---

### 3. PR Validation (`pr-validation.yml`)

**Trigger**: Pull request events (open, sync, reopen)

**Checks**:
- ✅ **Conventional Commits**: PR titles must follow semantic versioning
  - `feat:`, `fix:`, `docs:`, `refactor:`, etc.
- ✅ **PR Size**: Warns if PR changes >1000 lines
- ✅ **Merge Conflicts**: Blocks PRs with conflicts
- ✅ **CI Status**: Ensures CI builds pass before merge

---

## Environment Variables

### Required for CI

These are set automatically in GitHub Actions:

```yaml
SPRING_PROFILES_ACTIVE: test
SPRING_DATASOURCE_URL: jdbc:postgresql://localhost:5432/ecomerce_test
SPRING_DATASOURCE_USERNAME: testuser
SPRING_DATASOURCE_PASSWORD: testpass
SPRING_DATA_REDIS_HOST: localhost
SPRING_DATA_REDIS_PORT: 6379
JWT_SECRET_KEY: test-secret-key-minimum-256-bits-required-for-hs256-algorithm
JWT_ACCESS_EXPIRATION: 900000    # 15 minutes
JWT_REFRESH_EXPIRATION: 86400000 # 24 hours
```

### For Production Deployment

You need to configure these as **GitHub Secrets**:

- `JWT_SECRET_KEY`: Production JWT secret (≥256 bits)
- `DB_PASSWORD`: PostgreSQL password
- `AZURE_COMMUNICATION_CONNECTION_STRING`: Azure email service
- `AZURE_STORAGE_CONNECTION_STRING`: Azure blob storage
- Other sensitive credentials from `.env`

---

## Code Coverage

- **Tool**: JaCoCo
- **Minimum Coverage**: 60% overall
- **Class-level minimum**: 50% line coverage
- **Excluded from coverage**:
  - Configuration classes (`**.config.**`)
  - DTOs (`**.dto.**`)
  - Mappers (`**.mapper.**`)
  - Entities (`**.entity.**`)
  - Main application class
  - Exception classes

### Viewing Coverage Reports

After CI runs:
1. Go to Actions tab
2. Select the workflow run
3. Download `coverage-reports` artifact
4. Open `index.html` in browser

---

## Security Scanning

### OWASP Dependency Check

Scans all dependencies for known CVEs.

**Configuration**:
- Severity threshold: CVSS ≥ 7 (High/Critical)
- Report format: HTML
- Suppressions file: `owasp-suppressions.xml`

**To suppress false positives**:

Edit `owasp-suppressions.xml`:

```xml
<suppress>
    <notes><![CDATA[
        Reason: This vulnerability only affects Windows environments
    ]]></notes>
    <cve>CVE-2024-12345</cve>
</suppress>
```

### CodeQL

GitHub's semantic code analysis engine.

- Detects SQL injection, XSS, command injection, etc.
- Uses security-extended and quality queries
- Results in **Security → Code scanning alerts**

---

## Best Practices

### For Contributors

1. **Run tests locally** before pushing:
   ```bash
   ./gradlew clean test
   ```

2. **Check coverage**:
   ```bash
   ./gradlew jacocoTestReport
   open build/reports/jacoco/test/html/index.html
   ```

3. **Scan for vulnerabilities**:
   ```bash
   ./gradlew dependencyCheckAnalyze
   open build/reports/dependency-check-report.html
   ```

4. **Conventional Commits** for PR titles:
   - ✅ `feat: add user profile endpoint`
   - ✅ `fix: resolve JWT expiration bug`
   - ✅ `refactor: improve order service architecture`
   - ❌ `updated stuff`
   - ❌ `fix bug`

5. **Keep PRs small**: Aim for <500 lines changed

---

## Troubleshooting

### CI Build Fails - "Cannot find symbol"

**Cause**: Lombok/MapStruct annotation processors not running

**Fix**: 
```bash
./gradlew clean build --refresh-dependencies
```

### Test Failures - Database Connection

**Cause**: PostgreSQL service not starting in CI

**Fix**: Check `services` section in `ci.yml` has health checks

### Coverage Below Threshold

**Cause**: New code not sufficiently tested

**Fix**: Add unit tests for uncovered classes/methods

### OWASP Fails - CVE Detected

**Options**:
1. Update dependency to patched version
2. If false positive, add to `owasp-suppressions.xml`
3. If risk accepted, adjust `failBuildOnCVSS` threshold

---

## Performance Optimizations

- ✅ Gradle wrapper validation prevents malicious wrappers
- ✅ Dependency caching reduces build time by ~60%
- ✅ Parallel job execution where possible
- ✅ Docker layer caching for faster image builds
- ✅ Artifacts retained only 1-7 days to save storage

---

## Metrics & Monitoring

After each CI run, review:

1. **Build time** (target: <5 minutes)
2. **Test success rate** (target: 100%)
3. **Coverage trend** (target: increasing over time)
4. **Vulnerability count** (target: 0 high/critical)

---

## Future Enhancements

Planned improvements:

- [ ] Integration with SonarQube for advanced code quality metrics
- [ ] Performance testing with JMeter/Gatling
- [ ] Automated deployment to staging environment
- [ ] Slack/Discord notifications for build failures
- [ ] Mutation testing with PIT
- [ ] Contract testing with Pact
- [ ] E2E testing with Testcontainers

---

## Support

For CI/CD issues:
1. Check workflow logs in Actions tab
2. Review this documentation
3. Open an issue with `ci/cd` label
4. Contact DevOps team

---

**Last updated**: January 2026

