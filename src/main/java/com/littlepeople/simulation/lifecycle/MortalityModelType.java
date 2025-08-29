package com.littlepeople.simulation.lifecycle;

/**
 * Enumeration representing different types of mortality models.
 *
 * @author LittlePeople Development Team
 * @version 1.0
 * @since 1.0
 */
public enum MortalityModelType {
    /**
     * Modern mortality model based on contemporary demographic data
     */
    MODERN,

    /**
     * Historical mortality model based on historical demographic data
     */
    HISTORICAL,

    /**
     * Custom mortality model with user-defined parameters
     */
    CUSTOM
}
