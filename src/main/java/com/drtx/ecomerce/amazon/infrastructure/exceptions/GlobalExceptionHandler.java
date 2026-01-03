package com.drtx.ecomerce.amazon.infrastructure.exceptions;

import com.drtx.ecomerce.amazon.core.model.exceptions.DomainException;
import com.drtx.ecomerce.amazon.core.model.exceptions.EntityNotFoundException;
import org.springframework.http.*;
import org.springframework.validation.FieldError;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.net.URI;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

/**
 * Global Exception Handler that standardizes error responses using RFC 7807
 * (Problem Detail).
 */
@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    private static final String TIMESTAMP = "timestamp";
    private static final String ERROR_CODE = "errorCode";

    @ExceptionHandler(EntityNotFoundException.class)
    public ProblemDetail handleEntityNotFound(EntityNotFoundException ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
        problemDetail.setTitle("Resource Not Found");
        problemDetail.setType(URI.create("https://api.amazon.com/errors/not-found"));
        problemDetail.setProperty(TIMESTAMP, Instant.now());
        problemDetail.setProperty(ERROR_CODE, "RESOURCE_NOT_FOUND");
        return problemDetail;
    }

    @ExceptionHandler(jakarta.persistence.EntityNotFoundException.class)
    public ProblemDetail handleJakartaEntityNotFound(jakarta.persistence.EntityNotFoundException ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
        problemDetail.setTitle("Database Resource Not Found");
        problemDetail.setType(URI.create("https://api.amazon.com/errors/not-found"));
        problemDetail.setProperty(TIMESTAMP, Instant.now());
        problemDetail.setProperty(ERROR_CODE, "DB_RESOURCE_NOT_FOUND");
        return problemDetail;
    }

    @ExceptionHandler(DomainException.class)
    public ProblemDetail handleDomainException(DomainException ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, ex.getMessage());
        problemDetail.setTitle("Business Rule Violation");
        problemDetail.setType(URI.create("https://api.amazon.com/errors/business-rule"));
        problemDetail.setProperty(TIMESTAMP, Instant.now());
        problemDetail.setProperty(ERROR_CODE, "BUSINESS_RULE_VIOLATION");
        return problemDetail;
    }

    @ExceptionHandler({ com.drtx.ecomerce.amazon.core.model.exceptions.NotificationException.class,
            com.drtx.ecomerce.amazon.core.model.exceptions.StorageException.class })
    public ProblemDetail handleInfrastructureException(DomainException ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_GATEWAY, ex.getMessage());
        problemDetail.setTitle("External Service Error");
        problemDetail.setType(URI.create("https://api.amazon.com/errors/external-service-error"));
        problemDetail.setProperty(TIMESTAMP, Instant.now());
        problemDetail.setProperty(ERROR_CODE, "EXTERNAL_SERVICE_ERROR");
        return problemDetail;
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ProblemDetail handleAccessDenied(AccessDeniedException ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.FORBIDDEN,
                "You do not have permission to access this resource.");
        problemDetail.setTitle("Access Denied");
        problemDetail.setType(URI.create("https://api.amazon.com/errors/forbidden"));
        problemDetail.setProperty(TIMESTAMP, Instant.now());
        problemDetail.setProperty(ERROR_CODE, "ACCESS_DENIED");
        return problemDetail;
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ProblemDetail handleBadCredentials(BadCredentialsException ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.UNAUTHORIZED,
                "Invalid username or password.");
        problemDetail.setTitle("Authentication Failed");
        problemDetail.setType(URI.create("https://api.amazon.com/errors/unauthorized"));
        problemDetail.setProperty(TIMESTAMP, Instant.now());
        problemDetail.setProperty(ERROR_CODE, "INVALID_CREDENTIALS");
        return problemDetail;
    }

    @ExceptionHandler(org.springframework.dao.DataIntegrityViolationException.class)
    public ProblemDetail handleDataIntegrityViolation(org.springframework.dao.DataIntegrityViolationException ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT,
                "A user with this email already exists.");
        problemDetail.setTitle("Resource Conflict");
        problemDetail.setType(URI.create("https://api.amazon.com/errors/conflict"));
        problemDetail.setProperty(TIMESTAMP, Instant.now());
        problemDetail.setProperty(ERROR_CODE, "RESOURCE_CONFLICT");
        return problemDetail;
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {

        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            if (error instanceof FieldError fieldError) {
                errors.put(fieldError.getField(), fieldError.getDefaultMessage());
            } else {
                errors.put(error.getObjectName(), error.getDefaultMessage());
            }
        });

        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST,
                "Validation failed for the request.");
        problemDetail.setTitle("Invalid Request Parameters");
        problemDetail.setType(URI.create("https://api.amazon.com/errors/validation-error"));
        problemDetail.setProperty("invalidParams", errors);
        problemDetail.setProperty(TIMESTAMP, Instant.now());
        problemDetail.setProperty(ERROR_CODE, "VALIDATION_ERROR");

        return ResponseEntity.status(status).body(problemDetail);
    }

    @ExceptionHandler(Exception.class)
    public ProblemDetail handleGeneralException(Exception ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR,
                "An unexpected server error occurred. Please contact support if the problem persists.");
        problemDetail.setTitle("Internal Server Error");
        problemDetail.setType(URI.create("https://api.amazon.com/errors/internal-error"));
        problemDetail.setProperty(TIMESTAMP, Instant.now());
        problemDetail.setProperty(ERROR_CODE, "INTERNAL_SERVER_ERROR");
        // En un entorno real, aquí deberíamos registrar la excepción (logging)
        return problemDetail;
    }
}
