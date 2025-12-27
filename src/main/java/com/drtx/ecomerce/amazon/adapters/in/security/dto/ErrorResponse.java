package com.drtx.ecomerce.amazon.adapters.in.security.dto;

import java.time.LocalDateTime;

public record ErrorResponse(
        String message,
        String error,
        int status,
        String path,
        LocalDateTime timestamp
) {
    public ErrorResponse(String message, String error, int status, String path) {
        this(message, error, status, path, LocalDateTime.now());
    }
}

