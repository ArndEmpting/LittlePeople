package com.littlepeople.core.exceptions;

/**
 * Base exception for all simulation-related errors.
 *
 * <p>This class serves as the root of the exception hierarchy for the
 * LittlePeople simulation engine. All simulation-specific exceptions
 * should extend from this class to provide consistent error handling
 * throughout the system.</p>
 *
 * <p>This exception supports both checked exception patterns for
 * recoverable errors and provides comprehensive error information
 * for debugging and logging purposes.</p>
 *
 * @since 1.0.0
 * @version 1.0.0
 */
public class SimulationException extends Exception {

    private static final long serialVersionUID = 1L;

    /**
     * Constructs a new simulation exception with null as its detail message.
     */
    public SimulationException() {
        super();
    }

    /**
     * Constructs a new simulation exception with the specified detail message.
     *
     * @param message the detail message (which is saved for later retrieval
     *                by the getMessage() method)
     */
    public SimulationException(String message) {
        super(message);
    }

    /**
     * Constructs a new simulation exception with the specified detail message
     * and cause.
     *
     * @param message the detail message (which is saved for later retrieval
     *                by the getMessage() method)
     * @param cause   the cause (which is saved for later retrieval by the
     *                getCause() method). A null value is permitted, and
     *                indicates that the cause is nonexistent or unknown
     */
    public SimulationException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs a new simulation exception with the specified cause and a
     * detail message of (cause==null ? null : cause.toString()).
     *
     * @param cause the cause (which is saved for later retrieval by the
     *              getCause() method). A null value is permitted, and
     *              indicates that the cause is nonexistent or unknown
     */
    public SimulationException(Throwable cause) {
        super(cause);
    }
}
