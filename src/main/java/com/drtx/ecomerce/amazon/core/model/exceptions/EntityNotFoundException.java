package com.drtx.ecomerce.amazon.core.model.exceptions;

public class EntityNotFoundException extends DomainException {

    public EntityNotFoundException(String entityType, Object id) {
        super(String.format("%s with id '%s' not found", entityType, id));
    }

    public EntityNotFoundException(String message) {
        super(message);
    }
}
