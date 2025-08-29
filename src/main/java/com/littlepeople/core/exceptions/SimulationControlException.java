package com.littlepeople.core.exceptions;

/**
 * Exception thrown when simulation control operations fail.
 *
 * <p>This exception is thrown when there are problems with starting,
 * stopping, pausing, or resuming simulation components. This includes
 * invalid state transitions, resource conflicts, or operational errors
 * during lifecycle management.</p>
 *
 * @since 1.0.0
 * @version 1.0.0
 */
public class SimulationControlException extends SimulationException {

    private static final long serialVersionUID = 1L;

    /**
     * Constructs a new simulation control exception with null as its detail message.
     */
    public SimulationControlException() {
        super();
    }

    /**
     * Constructs a new simulation control exception with the specified detail message.
     *
     * @param message the detail message
     */
    public SimulationControlException(String message) {
        super(message);
    }

    /**
     * Constructs a new simulation control exception with the specified detail message
     * and cause.
     *
     * @param message the detail message
     * @param cause   the cause
     */
    public SimulationControlException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs a new simulation control exception with the specified cause.
     *
     * @param cause the cause
     */
    public SimulationControlException(Throwable cause) {
        super(cause);
    }
}
