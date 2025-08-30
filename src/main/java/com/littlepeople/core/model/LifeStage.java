package com.littlepeople.core.model;

/**
 * Enumeration representing the life stage of a person in the simulation.
 *
 * <p>This enumeration defines different life phases that affect
 * a person's capabilities, behaviors, and available actions.
 * Life stage is determined by age and affects various simulation
 * mechanics including education, employment, and relationships.</p>
 *
 * @since 1.0.0
 * @version 1.0.0
 */
public enum LifeStage {

    /**
     * Infancy stage (0-2 years).
     *
     * <p>Represents the earliest life stage where individuals
     * are completely dependent on caregivers and have
     * limited interaction with the simulation environment.</p>
     */
    INFANT(0, 2),

    /**
     * Childhood stage (3-12 years).
     *
     * <p>Represents the developmental stage where individuals
     * begin basic education and social development but
     * remain dependent on adult caregivers.</p>
     */
    CHILD(3, 12),

    /**
     * Adolescence stage (13-17 years).
     *
     * <p>Represents the teenage years with increased independence,
     * continued education, and developing social relationships
     * but still not considered fully adult.</p>
     */
    ADOLESCENT(13, 17),

    /**
     * Young adult stage (18-25 years).
     *
     * <p>Represents early adulthood with full legal independence,
     * potential for higher education, career start, and
     * beginning of partnership formation.</p>
     */
    YOUNG_ADULT(18, 25),

    /**
     * Adult stage (26-64 years).
     *
     * <p>Represents the primary productive years with
     * career development, family formation, and
     * peak economic and social participation.</p>
     */
    ADULT(26, 54),
    SENIOR(55, 74),
    /**
     * Elder stage (65+ years).
     *
     * <p>Represents the senior years typically associated
     * with retirement, increased health concerns, and
     * grandparent roles in family structures.</p>
     */
    ELDER(75, Integer.MAX_VALUE);

    private final int minAge;
    private final int maxAge;

    /**
     * Creates a life stage with the specified age range.
     *
     * @param minAge minimum age for this life stage (inclusive)
     * @param maxAge maximum age for this life stage (inclusive)
     */
    LifeStage(int minAge, int maxAge) {
        this.minAge = minAge;
        this.maxAge = maxAge;
    }

    /**
     * Returns the minimum age for this life stage.
     *
     * @return the minimum age (inclusive)
     */
    public int getMinAge() {
        return minAge;
    }

    /**
     * Returns the maximum age for this life stage.
     *
     * @return the maximum age (inclusive)
     */
    public int getMaxAge() {
        return maxAge;
    }

    /**
     * Determines the life stage based on age.
     *
     * @param age the age to evaluate
     * @return the corresponding life stage
     * @throws IllegalArgumentException if age is negative
     */
    public static LifeStage fromAge(int age) {
        if (age < 0) {
            throw new IllegalArgumentException("Age cannot be negative: " + age);
        }

        for (LifeStage stage : values()) {
            if (age >= stage.minAge && age <= stage.maxAge) {
                return stage;
            }
        }

        // Should never reach here given the enum definitions
        return ELDER;
    }

    /**
     * Determines if this life stage represents an adult (18+ years).
     *
     * @return true if this stage represents an adult, false otherwise
     */
    public boolean isAdult() {
        return this == YOUNG_ADULT || this == ADULT || this == SENIOR || this == ELDER;
    }
}
