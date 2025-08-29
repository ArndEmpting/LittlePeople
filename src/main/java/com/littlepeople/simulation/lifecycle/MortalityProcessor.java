package com.littlepeople.simulation.lifecycle;

import com.littlepeople.core.exceptions.SimulationException;
import com.littlepeople.core.interfaces.EventProcessor;
import com.littlepeople.core.model.DeathCause;
import com.littlepeople.core.model.Person;

import java.time.LocalDate;
import java.util.List;

/**
 * Processes mortality calculations and generates death events.
 *
 * @author LittlePeople Development Team
 * @version 1.0
 * @since 1.0
 */
public interface MortalityProcessor extends EventProcessor {

    /**
     * Calculates death probability for a person based on their age, health, and other factors.
     *
     * @param person the person to calculate for
     * @return the probability of death (0.0 to 1.0)
     */
    double calculateDeathProbability(Person person);

    /**
     * Determines if a person should die based on their death probability.
     *
     * @param person the person to check
     * @return true if the person should die
     */
    boolean shouldDie(Person person);

    /**
     * Processes potential deaths for the entire population.
     *
     * @param population the population to process
     * @param currentDate the current simulation date
     * @throws SimulationException if processing fails
     */
    void processMortality(List<Person> population, LocalDate currentDate)
        throws SimulationException;

    /**
     * Determines the cause of death for a person.
     *
     * @param person the person who died
     * @return the cause of death
     */
    DeathCause determineDeathCause(Person person);

    /**
     * Sets the mortality model to use for calculations.
     *
     * @param model the mortality model to use
     */
    void setMortalityModel(MortalityModel model);

    /**
     * Gets the currently configured mortality model.
     *
     * @return the current mortality model
     */
    MortalityModel getMortalityModel();
}
