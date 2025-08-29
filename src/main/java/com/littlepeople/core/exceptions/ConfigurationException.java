package com.littlepeople.core.exceptions;

/**
 * Exception thrown when configuration-related errors occur.
 *
 * <p>This exception is thrown when there are problems with loading,
 * parsing, validating, or applying configuration settings. This includes
 * issues with configuration files, invalid configuration values, or
 * missing required configuration parameters.</p>
 *
 * @since 1.0.0
 * @version 1.0.0
 */
public class ConfigurationException extends SimulationException {

    private static final long serialVersionUID = 1L;

    /**
     * Constructs a new configuration exception with null as its detail message.
     */
    public ConfigurationException() {
        super();
    }

    /**
     * Constructs a new configuration exception with the specified detail message.
     *
     * @param message the detail message
     */
    public ConfigurationException(String message) {
        super(message);
    }

    /**
     * Constructs a new configuration exception with the specified detail message
     * and cause.
     *
     * @param message the detail message
     * @param cause   the cause
     */
    public ConfigurationException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs a new configuration exception with the specified cause.
     *
     * @param cause the cause
     */
    public ConfigurationException(Throwable cause) {
        super(cause);
    }
}
