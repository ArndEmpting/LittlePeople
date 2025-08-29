package com.littlepeople.core.exceptions;

/**
 * Exception thrown when an entity cannot be found.
 *
 * <p>This exception is thrown when attempting to retrieve, modify, or
 * reference an entity that does not exist in the simulation. This can
 * occur during database lookups, entity relationships, or other operations
 * that require a specific entity to be present.</p>
 *
 * @since 1.0.0
 * @version 1.0.0
 */
public class EntityNotFoundException extends SimulationException {

    private static final long serialVersionUID = 1L;

    /**
     * Constructs a new entity not found exception with null as its detail message.
     */
    public EntityNotFoundException() {
        super();
    }

    /**
     * Constructs a new entity not found exception with the specified detail message.
     *
     * @param message the detail message
     */
    public EntityNotFoundException(String message) {
        super(message);
    }

    /**
     * Constructs a new entity not found exception with the specified detail message
     * and cause.
     *
     * @param message the detail message
     * @param cause   the cause
     */
    public EntityNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs a new entity not found exception with the specified cause.
     *
     * @param cause the cause
     */
    public EntityNotFoundException(Throwable cause) {
        super(cause);
    }
}
