package com.ecom.api.common;

/**
 * Thrown when a requested resource does not exist.
 * Mapped to HTTP 404 by {@link GlobalExceptionHandler}.
 */
public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String message) {
        super(message);
    }

    public static ResourceNotFoundException of(String entity, String id) {
        return new ResourceNotFoundException(entity + " with id '" + id + "' not found");
    }
}
