package com.littlepeople.simulation.lifecycle;

import com.littlepeople.core.exceptions.SimulationException;
import com.littlepeople.core.model.HealthStatus;
import com.littlepeople.core.model.Person;

import java.time.LocalDate;
import java.util.List;

/**
 * Interface for processing health-related changes and updates.
 *
 * @author LittlePeople Development Team
 * @version 1.0
 * @since 1.0
 */
public interface HealthProcessor {

    /**
     * Processes health changes for the entire population.
     *
     * @param population the population to process
     * @param currentDate the current simulation date
     * @throws SimulationException if processing fails
     */
    void processHealthChanges(List<Person> population, LocalDate currentDate)
        throws SimulationException;

    /**
     * Updates the health status of a specific person based on age and current health.
     *
     * @param person the person to update
     * @param currentDate the current simulation date
     * @return the new health status
     */
    HealthStatus updatePersonHealth(Person person, LocalDate currentDate);

    /**
     * Calculates the probability of health decline based on age and current health.
     *
     * @param person the person to calculate for
     * @return the probability of health decline (0.0 to 1.0)
     */
    double calculateHealthDeclineProbability(Person person);

    /**
     * Calculates the probability of health improvement based on age and current health.
     *
     * @param person the person to calculate for
     * @return the probability of health improvement (0.0 to 1.0)
     */
    double calculateHealthImprovementProbability(Person person);
}
