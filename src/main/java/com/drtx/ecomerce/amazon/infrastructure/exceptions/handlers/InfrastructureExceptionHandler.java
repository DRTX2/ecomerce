package com.drtx.ecomerce.amazon.infrastructure.exceptions.handlers;

import com.drtx.ecomerce.amazon.core.model.exceptions.NotificationException;
import com.drtx.ecomerce.amazon.core.model.exceptions.StorageException;
import com.drtx.ecomerce.amazon.infrastructure.exceptions.ProblemDetailBuilder;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Manejador de excepciones de servicios externos e infraestructura.
 * Módulo enfocado en excepciones de Storage, Email, etc.
 */
@RestControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE + 3)
public class InfrastructureExceptionHandler {

    @ExceptionHandler(StorageException.class)
    public ProblemDetail handleStorageException(StorageException ex) {
        return ProblemDetailBuilder.externalServiceError("Storage error: " + ex.getMessage());
    }

    @ExceptionHandler(NotificationException.class)
    public ProblemDetail handleNotificationException(NotificationException ex) {
        return ProblemDetailBuilder.externalServiceError("Notification error: " + ex.getMessage());
    }
}
