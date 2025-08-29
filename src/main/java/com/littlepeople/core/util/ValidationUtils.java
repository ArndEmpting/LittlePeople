package com.littlepeople.core.util;

import java.util.Collection;
import java.util.UUID;

/**
 * Utility class for common validation operations.
 *
 * <p>This class provides static methods for validating parameters and data
 * throughout the simulation system. It helps ensure consistent validation
 * logic and error messaging across all components.</p>
 *
 * @since 1.0.0
 * @version 1.0.0
 */
public final class ValidationUtils {

    /**
     * Private constructor to prevent instantiation.
     */
    private ValidationUtils() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }

    /**
     * Validates that the specified object is not null.
     *
     * @param obj the object to validate
     * @param paramName the name of the parameter (for error messages)
     * @throws IllegalArgumentException if the object is null
     */
    public static void requireNonNull(Object obj, String paramName) {
        if (obj == null) {
            throw new IllegalArgumentException(paramName + " cannot be null");
        }
    }

    /**
     * Validates that the specified string is not null or empty.
     *
     * @param str the string to validate
     * @param paramName the name of the parameter (for error messages)
     * @throws IllegalArgumentException if the string is null or empty
     */
    public static void requireNonEmpty(String str, String paramName) {
        requireNonNull(str, paramName);
        if (str.trim().isEmpty()) {
            throw new IllegalArgumentException(paramName + " cannot be empty");
        }
    }

    /**
     * Validates that the specified collection is not null or empty.
     *
     * @param collection the collection to validate
     * @param paramName the name of the parameter (for error messages)
     * @throws IllegalArgumentException if the collection is null or empty
     */
    public static void requireNonEmpty(Collection<?> collection, String paramName) {
        requireNonNull(collection, paramName);
        if (collection.isEmpty()) {
            throw new IllegalArgumentException(paramName + " cannot be empty");
        }
    }

    /**
     * Validates that the specified number is positive.
     *
     * @param number the number to validate
     * @param paramName the name of the parameter (for error messages)
     * @throws IllegalArgumentException if the number is not positive
     */
    public static void requirePositive(int number, String paramName) {
        if (number <= 0) {
            throw new IllegalArgumentException(paramName + " must be positive, but was: " + number);
        }
    }

    /**
     * Validates that the specified number is positive.
     *
     * @param number the number to validate
     * @param paramName the name of the parameter (for error messages)
     * @throws IllegalArgumentException if the number is not positive
     */
    public static void requirePositive(long number, String paramName) {
        if (number <= 0) {
            throw new IllegalArgumentException(paramName + " must be positive, but was: " + number);
        }
    }

    /**
     * Validates that the specified number is non-negative.
     *
     * @param number the number to validate
     * @param paramName the name of the parameter (for error messages)
     * @throws IllegalArgumentException if the number is negative
     */
    public static void requireNonNegative(int number, String paramName) {
        if (number < 0) {
            throw new IllegalArgumentException(paramName + " cannot be negative, but was: " + number);
        }
    }

    /**
     * Validates that the specified number is within the given range (inclusive).
     *
     * @param number the number to validate
     * @param min the minimum allowed value (inclusive)
     * @param max the maximum allowed value (inclusive)
     * @param paramName the name of the parameter (for error messages)
     * @throws IllegalArgumentException if the number is outside the range
     */
    public static void requireInRange(int number, int min, int max, String paramName) {
        if (number < min || number > max) {
            throw new IllegalArgumentException(
                paramName + " must be between " + min + " and " + max + " (inclusive), but was: " + number);
        }
    }

    /**
     * Validates that the specified UUID is not null and not the nil UUID.
     *
     * @param uuid the UUID to validate
     * @param paramName the name of the parameter (for error messages)
     * @throws IllegalArgumentException if the UUID is null or nil
     */
    public static void requireValidUUID(UUID uuid, String paramName) {
        requireNonNull(uuid, paramName);
        if (uuid.equals(new UUID(0L, 0L))) {
            throw new IllegalArgumentException(paramName + " cannot be the nil UUID");
        }
    }
}
