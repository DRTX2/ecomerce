package com.drtx.ecomerce.amazon.infrastructure.exceptions.handlers;

import com.drtx.ecomerce.amazon.core.model.exceptions.DomainException;
import com.drtx.ecomerce.amazon.infrastructure.exceptions.ProblemDetailBuilder;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Manejador de excepciones de dominio (reglas de negocio).
 * Módulo enfocado en excepciones del Core.
 */
@RestControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
public class DomainExceptionHandler {

    @ExceptionHandler(DomainException.class)
    public ProblemDetail handleDomainException(DomainException ex) {
        return ProblemDetailBuilder.businessRuleViolation(ex.getMessage());
    }
}
