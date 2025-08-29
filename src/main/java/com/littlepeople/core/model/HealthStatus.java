package com.littlepeople.core.model;

/**
 * Enumeration representing the health status of a person in the simulation.
 *
 * <p>This enumeration defines different health states that affect
 * a person's mortality risk, behavior patterns, and life outcomes.
 * Health status can change over time due to aging, events, or
 * random factors in the simulation.</p>
 *
 * @since 1.0.0
 * @version 1.0.0
 */
public enum HealthStatus {

    /**
     * Person is in good health with no significant issues.
     *
     * <p>Represents optimal health status with normal mortality
     * risk and full participation in simulation activities.
     * This is the default health status for new persons.</p>
     */
    HEALTHY(1.0),

    /**
     * Person has minor health issues.
     *
     * <p>Represents temporary or manageable health problems
     * that slightly increase mortality risk and may affect
     * some activities. Can recover to healthy status.</p>
     */
    SICK(1.5),

    /**
     * Person has serious health issues with increased mortality risk.
     *
     * <p>Represents severe health conditions that significantly
     * increase mortality risk and limit participation in
     * simulation activities. May progress to death.</p>
     */
    CRITICALLY_ILL(3.0);

    private final double mortalityMultiplier;

    /**
     * Creates a health status with the specified mortality multiplier.
     *
     * @param mortalityMultiplier the factor by which base mortality risk is multiplied
     */
    HealthStatus(double mortalityMultiplier) {
        this.mortalityMultiplier = mortalityMultiplier;
    }

    /**
     * Returns the mortality risk multiplier for this health status.
     *
     * <p>This multiplier is applied to base mortality calculations
     * to determine the increased risk associated with poor health.</p>
     *
     * @return the mortality multiplier (1.0 = normal risk)
     */
    public double getMortalityMultiplier() {
        return mortalityMultiplier;
    }
}
