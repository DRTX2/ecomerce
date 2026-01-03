package com.drtx.ecomerce.amazon.core.model.exceptions;

public class StorageException extends DomainException {
    public StorageException(String message) {
        super(message);
    }

    public StorageException(String message, Throwable cause) {
        super(message + " Cause: " + cause.getMessage());
    }
}
