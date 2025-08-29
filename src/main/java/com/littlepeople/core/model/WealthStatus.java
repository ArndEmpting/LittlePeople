package com.littlepeople.core.model;

/**
 * Enumeration representing the wealth status of a person in the simulation.
 *
 * <p>This enumeration defines different economic levels that affect
 * a person's life outcomes, partnership opportunities, and access
 * to resources. Wealth status can change over time based on
 * employment, inheritance, and life events.</p>
 *
 * @since 1.0.0
 * @version 1.0.0
 */
public enum WealthStatus {

    /**
     * Person has very limited financial resources.
     *
     * <p>Represents individuals struggling with basic needs,
     * limited access to healthcare and education, and
     * increased vulnerability to negative life events.</p>
     */
    POOR(0, 25000),

    /**
     * Person has modest financial resources.
     *
     * <p>Represents working-class individuals with basic
     * financial security but limited savings and
     * moderate access to opportunities.</p>
     */
    LOWER_MIDDLE_CLASS(25001, 50000),

    /**
     * Person has comfortable financial resources.
     *
     * <p>Represents individuals with stable income,
     * good access to healthcare and education,
     * and moderate savings for emergencies.</p>
     */
    MIDDLE_CLASS(50001, 100000),

    /**
     * Person has substantial financial resources.
     *
     * <p>Represents well-off individuals with significant
     * disposable income, excellent access to opportunities,
     * and substantial savings and investments.</p>
     */
    UPPER_MIDDLE_CLASS(100001, 250000),

    /**
     * Person has extensive financial resources.
     *
     * <p>Represents wealthy individuals with minimal
     * financial constraints, premium access to all
     * opportunities, and substantial assets.</p>
     */
    RICH(250001, Integer.MAX_VALUE);

    private final int minIncome;
    private final int maxIncome;

    /**
     * Creates a wealth status with the specified income range.
     *
     * @param minIncome minimum annual income for this wealth level
     * @param maxIncome maximum annual income for this wealth level
     */
    WealthStatus(int minIncome, int maxIncome) {
        this.minIncome = minIncome;
        this.maxIncome = maxIncome;
    }

    /**
     * Returns the minimum annual income for this wealth status.
     *
     * @return the minimum income threshold
     */
    public int getMinIncome() {
        return minIncome;
    }

    /**
     * Returns the maximum annual income for this wealth status.
     *
     * @return the maximum income threshold
     */
    public int getMaxIncome() {
        return maxIncome;
    }

    /**
     * Determines the wealth status based on annual income.
     *
     * @param income the annual income to evaluate
     * @return the corresponding wealth status
     */
    public static WealthStatus fromIncome(int income) {
        for (WealthStatus status : values()) {
            if (income >= status.minIncome && income <= status.maxIncome) {
                return status;
            }
        }
        return POOR; // Default fallback
    }
}
