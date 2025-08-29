package com.littlepeople.core.model;

/**
 * Enumeration representing the cause of death for a person in the simulation.
 *
 * <p>This enumeration defines different reasons why a person might die
 * in the simulation, which affects mortality statistics and can
 * influence family dynamics and population health trends.</p>
 *
 * @since 1.0.0
 * @version 1.0.0
 */
public enum DeathCause {

    /**
     * Death due to disease or illness.
     *
     * <p>Represents deaths caused by various medical conditions,
     * infections, chronic diseases, or acute illnesses.
     * This is one of the most common causes of death.</p>
     */
    DISEASE("Disease"),

    /**
     * Natural death due to old age.
     *
     * <p>Represents deaths from natural aging processes
     * when the person reaches advanced age without
     * specific disease or accident causes.</p>
     */
    NATURAL_OLD_AGE("Natural Old Age"),

    /**
     * Death due to accidental causes.
     *
     * <p>Represents deaths from unintentional accidents
     * such as vehicle crashes, falls, drowning, or
     * other mishaps not involving violence.</p>
     */
    ACCIDENT("Accident"),

    /**
     * Death due to violent causes.
     *
     * <p>Represents deaths from intentional violence
     * including homicide, suicide, or other
     * deliberate harmful acts.</p>
     */
    VIOLENT("Violence"),

    /**
     * Death during or shortly after birth.
     *
     * <p>Represents infant deaths that occur during
     * childbirth or in the immediate postnatal period
     * due to birth complications or congenital issues.</p>
     */
    BIRTH_COMPLICATION("Birth Complication"),

    /**
     * Death from unknown or unexplained causes.
     *
     * <p>Represents deaths where the specific cause
     * cannot be determined or is not specified
     * in the simulation context.</p>
     */
    UNEXPLAINED("Unexplained");

    private final String description;

    /**
     * Creates a death cause with the specified description.
     *
     * @param description human-readable description of the death cause
     */
    DeathCause(String description) {
        this.description = description;
    }

    /**
     * Returns the human-readable description of this death cause.
     *
     * @return the description string
     */
    public String getDescription() {
        return description;
    }
}
