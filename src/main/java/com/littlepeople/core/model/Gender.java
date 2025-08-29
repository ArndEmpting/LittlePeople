package com.littlepeople.core.model;

/**
 * Enumeration representing the gender of a person in the simulation.
 *
 * <p>This enumeration defines the biological gender categories
 * available for Person entities in the LittlePeople simulation.
 * The gender affects various simulation mechanics including
 * partnership formation and biological processes.</p>
 *
 * @since 1.0.0
 * @version 1.0.0
 */
public enum Gender {

    /**
     * Male gender.
     *
     * <p>Represents biological males in the simulation.
     * Males can form partnerships with females and
     * participate in reproduction as fathers.</p>
     */
    MALE,

    /**
     * Female gender.
     *
     * <p>Represents biological females in the simulation.
     * Females can form partnerships with males and
     * participate in reproduction as mothers, including
     * pregnancy and childbirth processes.</p>
     */
    FEMALE
}
