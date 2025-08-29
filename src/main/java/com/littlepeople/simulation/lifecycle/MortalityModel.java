package com.littlepeople.simulation.lifecycle;

import com.littlepeople.core.model.HealthStatus;

/**
 * Interface for different mortality probability calculation models.
 *
 * @author LittlePeople Development Team
 * @version 1.0
 * @since 1.0
 */
public interface MortalityModel {

    /**
     * Calculates the baseline death probability based on age.
     *
     * @param age the age in years
     * @return the baseline probability of death (0.0 to 1.0)
     */
    double calculateBaselineProbability(int age);

    /**
     * Adjusts the baseline death probability based on health status.
     *
     * @param baselineProbability the baseline probability from age
     * @param healthStatus the person's current health status
     * @return the adjusted probability of death (0.0 to 1.0)
     */
    double adjustForHealth(double baselineProbability, HealthStatus healthStatus);

    /**
     * Gets the name of this mortality model.
     *
     * @return the model name
     */
    String getModelName();

    /**
     * Gets the model type (historical, modern, custom).
     *
     * @return the model type
     */
    MortalityModelType getModelType();
}
