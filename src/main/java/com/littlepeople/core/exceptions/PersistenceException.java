package com.littlepeople.core.exceptions;

/**
 * Exception thrown when persistence-related errors occur.
 *
 * <p>This exception is thrown when there are problems with data storage,
 * retrieval, or database operations. This includes database connection
 * issues, SQL errors, data integrity violations, or file system problems.</p>
 *
 * @since 1.0.0
 * @version 1.0.0
 */
public class PersistenceException extends SimulationException {

    private static final long serialVersionUID = 1L;

    /**
     * Constructs a new persistence exception with null as its detail message.
     */
    public PersistenceException() {
        super();
    }

    /**
     * Constructs a new persistence exception with the specified detail message.
     *
     * @param message the detail message
     */
    public PersistenceException(String message) {
        super(message);
    }

    /**
     * Constructs a new persistence exception with the specified detail message
     * and cause.
     *
     * @param message the detail message
     * @param cause   the cause
     */
    public PersistenceException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs a new persistence exception with the specified cause.
     *
     * @param cause the cause
     */
    public PersistenceException(Throwable cause) {
        super(cause);
    }
}
