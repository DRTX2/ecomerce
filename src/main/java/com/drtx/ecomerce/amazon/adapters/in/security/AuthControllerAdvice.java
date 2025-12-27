package com.drtx.ecomerce.amazon.adapters.in.security;

import com.drtx.ecomerce.amazon.adapters.in.security.dto.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

@RestControllerAdvice(assignableTypes = AuthController.class)
public class AuthControllerAdvice {

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErrorResponse> handleBadCredentials(
            BadCredentialsException ex,
            WebRequest request
    ) {
        System.out.println("AuthControllerAdvice - BadCredentialsException: " + ex.getMessage());
        ErrorResponse error = new ErrorResponse(
                "Email o contraseña incorrectos",
                "BAD_CREDENTIALS",
                HttpStatus.UNAUTHORIZED.value(),
                request.getDescription(false).replace("uri=", "")
        );
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleUsernameNotFound(
            UsernameNotFoundException ex,
            WebRequest request
    ) {
        System.out.println("AuthControllerAdvice - UsernameNotFoundException: " + ex.getMessage());
        ErrorResponse error = new ErrorResponse(
                "Usuario no encontrado",
                "USER_NOT_FOUND",
                HttpStatus.UNAUTHORIZED.value(),
                request.getDescription(false).replace("uri=", "")
        );
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ErrorResponse> handleAuthenticationException(
            AuthenticationException ex,
            WebRequest request
    ) {
        System.out.println("AuthControllerAdvice - AuthenticationException: " + ex.getMessage());
        ErrorResponse error = new ErrorResponse(
                "Error de autenticación: " + ex.getMessage(),
                "AUTHENTICATION_FAILED",
                HttpStatus.UNAUTHORIZED.value(),
                request.getDescription(false).replace("uri=", "")
        );
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneralException(
            Exception ex,
            WebRequest request
    ) {
        System.out.println("AuthControllerAdvice - Exception: " + ex.getClass().getName() + " - " + ex.getMessage());
        ex.printStackTrace();
        ErrorResponse error = new ErrorResponse(
                "Error interno del servidor",
                "INTERNAL_SERVER_ERROR",
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                request.getDescription(false).replace("uri=", "")
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
}

