package com.drtx.ecomerce.amazon.infrastructure.exceptions.handlers;

import com.drtx.ecomerce.amazon.infrastructure.exceptions.ProblemDetailBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Fallback handler para excepciones no manejadas.
 * Última línea de defensa - captura cualquier excepción no manejada.
 */
@RestControllerAdvice
@Order(Ordered.LOWEST_PRECEDENCE)
public class FallbackExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(FallbackExceptionHandler.class);

    @ExceptionHandler(Exception.class)
    public ProblemDetail handleGeneralException(Exception ex) {
        logger.error("Unhandled exception occurred", ex);

        return ProblemDetailBuilder.internalServerError(
                "An unexpected server error occurred. Please contact support if the problem persists");
    }
}
