package com.drtx.ecomerce.amazon.infrastructure.exceptions.handlers;

import com.drtx.ecomerce.amazon.infrastructure.exceptions.ProblemDetailBuilder;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.*;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.HashMap;
import java.util.Map;

/**
 * Manejador de excepciones de validación.
 * Módulo enfocado en errores de validación de entrada.
 */
@RestControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE + 4)
public class ValidationExceptionHandler extends ResponseEntityExceptionHandler {

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex,
            HttpHeaders headers,
            HttpStatusCode status,
            WebRequest request) {

        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            if (error instanceof FieldError fieldError) {
                errors.put(fieldError.getField(), fieldError.getDefaultMessage());
            } else {
                errors.put(error.getObjectName(), error.getDefaultMessage());
            }
        });

        ProblemDetail problemDetail = ProblemDetailBuilder
                .create(HttpStatus.BAD_REQUEST, "Validation failed for the request")
                .withTitle("Invalid Request Parameters")
                .withType("validation-error")
                .withErrorCode("VALIDATION_ERROR")
                .withProperty("invalidParams", errors)
                .build();

        return ResponseEntity.status(status).body(problemDetail);
    }
}
