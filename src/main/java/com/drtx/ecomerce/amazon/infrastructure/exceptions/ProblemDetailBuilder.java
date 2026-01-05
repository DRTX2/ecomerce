package com.drtx.ecomerce.amazon.infrastructure.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;

import java.net.URI;
import java.time.Instant;

/**
 * Builder para crear ProblemDetail de forma fluida y consistente.
 * Nivel Senior: Centraliza la creación de respuestas de error siguiendo RFC
 * 7807.
 */
public final class ProblemDetailBuilder {

    private static final String BASE_TYPE_URL = "https://api.amazon.com/errors/";
    private static final String TIMESTAMP = "timestamp";
    private static final String ERROR_CODE = "errorCode";

    private final ProblemDetail problemDetail;

    private ProblemDetailBuilder(HttpStatus status, String detail) {
        this.problemDetail = ProblemDetail.forStatusAndDetail(status, detail);
        this.problemDetail.setProperty(TIMESTAMP, Instant.now());
    }

    public static ProblemDetailBuilder create(HttpStatus status, String detail) {
        return new ProblemDetailBuilder(status, detail);
    }

    public ProblemDetailBuilder withTitle(String title) {
        problemDetail.setTitle(title);
        return this;
    }

    public ProblemDetailBuilder withType(String type) {
        problemDetail.setType(URI.create(BASE_TYPE_URL + type));
        return this;
    }

    public ProblemDetailBuilder withErrorCode(String errorCode) {
        problemDetail.setProperty(ERROR_CODE, errorCode);
        return this;
    }

    public ProblemDetailBuilder withProperty(String name, Object value) {
        problemDetail.setProperty(name, value);
        return this;
    }

    public ProblemDetail build() {
        return problemDetail;
    }

    // ========== Convenience Methods for Common Scenarios ==========

    public static ProblemDetail notFound(String entityType, Object id) {
        return create(HttpStatus.NOT_FOUND, String.format("%s with id '%s' not found", entityType, id))
                .withTitle("Resource Not Found")
                .withType("not-found")
                .withErrorCode("RESOURCE_NOT_FOUND")
                .build();
    }

    public static ProblemDetail notFound(String message) {
        return create(HttpStatus.NOT_FOUND, message)
                .withTitle("Resource Not Found")
                .withType("not-found")
                .withErrorCode("RESOURCE_NOT_FOUND")
                .build();
    }

    public static ProblemDetail businessRuleViolation(String message) {
        return create(HttpStatus.BAD_REQUEST, message)
                .withTitle("Business Rule Violation")
                .withType("business-rule")
                .withErrorCode("BUSINESS_RULE_VIOLATION")
                .build();
    }

    public static ProblemDetail externalServiceError(String message) {
        return create(HttpStatus.BAD_GATEWAY, message)
                .withTitle("External Service Error")
                .withType("external-service-error")
                .withErrorCode("EXTERNAL_SERVICE_ERROR")
                .build();
    }

    public static ProblemDetail unauthorized(String message) {
        return create(HttpStatus.UNAUTHORIZED, message)
                .withTitle("Authentication Failed")
                .withType("unauthorized")
                .withErrorCode("INVALID_CREDENTIALS")
                .build();
    }

    public static ProblemDetail forbidden(String message) {
        return create(HttpStatus.FORBIDDEN, message)
                .withTitle("Access Denied")
                .withType("forbidden")
                .withErrorCode("ACCESS_DENIED")
                .build();
    }

    public static ProblemDetail conflict(String message) {
        return create(HttpStatus.CONFLICT, message)
                .withTitle("Resource Conflict")
                .withType("conflict")
                .withErrorCode("RESOURCE_CONFLICT")
                .build();
    }

    public static ProblemDetail validationError(String message) {
        return create(HttpStatus.BAD_REQUEST, message)
                .withTitle("Invalid Request Parameters")
                .withType("validation-error")
                .withErrorCode("VALIDATION_ERROR")
                .build();
    }

    public static ProblemDetail internalServerError(String message) {
        return create(HttpStatus.INTERNAL_SERVER_ERROR, message)
                .withTitle("Internal Server Error")
                .withType("internal-error")
                .withErrorCode("INTERNAL_SERVER_ERROR")
                .build();
    }
}
