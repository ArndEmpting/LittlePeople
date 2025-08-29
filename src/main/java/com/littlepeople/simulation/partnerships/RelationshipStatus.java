package com.littlepeople.simulation.partnerships;

/**
 * Enumeration of possible relationship statuses in the partnership system.
 *
 * <p>This enum tracks the progression of romantic relationships from initial
 * attraction through marriage and potential dissolution. Each status represents
 * a distinct phase in the relationship lifecycle with different behavioral
 * implications and event possibilities.</p>
 *
 * <h2>Relationship Progression</h2>
 * <p>Typical relationship progression follows this pattern:</p>
 * <pre>
 * SINGLE → DATING → ENGAGED → MARRIED
 *    ↓        ↓        ↓        ↓
 * SINGLE ← SINGLE ← SINGLE ← DIVORCED/WIDOWED
 * </pre>
 *
 * @since 1.0.0
 * @version 1.0.0
 */
public enum RelationshipStatus {
    /**
     * Person is not in any romantic relationship.
     * Eligible for new partnership formation.
     */
    SINGLE,

    /**
     * Person is in a dating relationship but not yet engaged.
     * May progress to engagement or end relationship.
     */
    DATING,

    /**
     * Person is engaged to be married.
     * Will typically progress to marriage within a defined timeframe.
     */
    ENGAGED,

    /**
     * Person is legally married.
     * Eligible for having children and shared household management.
     */
    MARRIED,

    /**
     * Person was married but relationship ended in divorce.
     * May remarry after a cooling-off period.
     */
    DIVORCED,

    /**
     * Person's partner died while married.
     * May remarry after a mourning period.
     */
    WIDOWED;

    /**
     * Determines if a person with this status is available for new partnerships.
     *
     * @return true if the person can form new romantic relationships
     */
    public boolean isAvailableForPartnership() {
        return this == SINGLE || this == DIVORCED || this == WIDOWED;
    }

    /**
     * Determines if a person with this status is in a committed relationship.
     *
     * @return true if the person is in an exclusive romantic relationship
     */
    public boolean isInRelationship() {
        return this == DATING || this == ENGAGED || this == MARRIED;
    }

    /**
     * Determines if a person with this status can legally marry.
     *
     * @return true if the person can enter into marriage
     */
    public boolean canMarry() {
        return this == SINGLE || this == DIVORCED || this == WIDOWED || this == ENGAGED;
    }

    /**
     * Determines if a person with this status can have children.
     * Based on social conventions where marriage typically precedes childbearing.
     *
     * @return true if the person can have children in their current status
     */
    public boolean canHaveChildren() {
        return this == MARRIED;
    }
}
