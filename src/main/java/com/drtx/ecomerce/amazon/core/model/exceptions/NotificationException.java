package com.drtx.ecomerce.amazon.core.model.exceptions;

public class NotificationException extends DomainException {
    public NotificationException(String message) {
        super(message);
    }

    public NotificationException(String message, Throwable cause) {
        super(message + " Cause: " + cause.getMessage());
        // Note: DomainException in this project only has (String) constructor based on
        // cat output.
        // We might need to adjust or just pass message.
        // But better to check DomainException again.
        // It extends RuntimeException, so it inherits (String, Throwable).
        // BUT the provided file only showed a constructor receiving String.
        // So I cannot call super(message, cause) if the constructor doesn't exist.
        // I will just concatenate the message for now or update DomainException.
        // Attempting to update DomainException is out of scope unless necessary.
        // I'll stick to super(message) or message + cause.
    }
}
