package com.drtx.ecomerce.amazon.infrastructure.exceptions.handlers;

import com.drtx.ecomerce.amazon.infrastructure.exceptions.ProblemDetailBuilder;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

/**
 * Manejador de excepciones para rutas no encontradas.
 * Captura cuando se accede a un endpoint que no existe en la API.
 */
@RestControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE + 2)
public class RouteNotFoundExceptionHandler {

    @ExceptionHandler(NoHandlerFoundException.class)
    public ProblemDetail handleNoHandlerFound(NoHandlerFoundException ex) {
        String message = String.format(
                "The endpoint '%s %s' does not exist. Please check the API documentation.",
                ex.getHttpMethod(),
                ex.getRequestURL());
        return ProblemDetailBuilder.create(org.springframework.http.HttpStatus.NOT_FOUND, message)
                .withTitle("Endpoint Not Found")
                .withType("endpoint-not-found")
                .withErrorCode("ENDPOINT_NOT_FOUND")
                .withProperty("method", ex.getHttpMethod())
                .withProperty("path", ex.getRequestURL())
                .build();
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ProblemDetail handleNoResourceFound(NoResourceFoundException ex) {
        String message = String.format(
                "The resource '%s' was not found. Please check if the URL is correct.",
                ex.getResourcePath());
        return ProblemDetailBuilder.create(org.springframework.http.HttpStatus.NOT_FOUND, message)
                .withTitle("Resource Not Found")
                .withType("resource-not-found")
                .withErrorCode("RESOURCE_NOT_FOUND")
                .withProperty("path", ex.getResourcePath())
                .build();
    }
}
