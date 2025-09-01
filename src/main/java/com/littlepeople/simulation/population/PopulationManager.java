package com.littlepeople.simulation.population;

import com.littlepeople.core.model.Person;
import com.littlepeople.core.exceptions.SimulationException;

import java.time.LocalDate;
import java.util.List;

/**
 * Central manager for all population-related operations in the simulation.
 *
 * <p>The PopulationManager coordinates all aspects of population management including
 * initialization, growth control through immigration/emigration, validation, and
 * providing access to population data and statistics.
 *
 * <p>This interface serves as the primary entry point for all population-related
 * operations and integrates with the broader simulation system including the
 * event system, lifecycle management, and family formation systems.
 *
 * @author LittlePeople Development Team
 * @version 1.0
 * @since 1.0
 */
public interface PopulationManager {

    /**
     * Initializes the population based on the provided configuration.
     *
     * <p>This method creates the initial population with realistic demographic
     * distributions including age, gender, and initial family structures.
     * The population size and characteristics are determined by the configuration.
     *
     * @param config the population configuration parameters
     * @return the number of inhabitants created
     * @throws SimulationException if initialization fails due to invalid configuration
     *                            or system constraints
     * @throws IllegalArgumentException if config is null
     * @throws IllegalStateException if population is already initialized
     */
    int initializePopulation(PopulationConfiguration config) throws SimulationException;

    /**
     * Processes immigration based on configured rates and current simulation date.
     *
     * <p>This method creates new immigrants and adds them to the population based on
     * immigration rates, demographic patterns, and the current simulation state.
     * Immigration processing respects demographic balance and family formation rules.
     *
     * @param currentDate the current simulation date
     * @return the number of immigrants added to the population
     * @throws SimulationException if immigration processing fails
     * @throws IllegalArgumentException if currentDate is null
     * @throws IllegalStateException if population is not initialized
     */
    int processImmigration(LocalDate currentDate) throws SimulationException;

    /**
     * Processes emigration based on configured rates and current simulation date.
     *
     * <p>This method identifies candidates for emigration and removes them from
     * the population based on emigration rates, life circumstances, and demographic
     * patterns. Emigration respects family relationships and lifecycle constraints.
     *
     * @param currentDate the current simulation date
     * @return the number of emigrants removed from the population
     * @throws SimulationException if emigration processing fails
     * @throws IllegalArgumentException if currentDate is null
     * @throws IllegalStateException if population is not initialized
     */
    int processEmigration(LocalDate currentDate) throws SimulationException;

    /**
     * Gets the current population size.
     *
     * @return the number of living inhabitants in the population
     * @throws IllegalStateException if population is not initialized
     */
    int getPopulationSize();

    /**
     * Gets the entire living population.
     *
     * <p>Returns an unmodifiable view of all living inhabitants. This method
     * provides access to the complete population for analysis and reporting
     * but prevents external modification of the population structure.
     *
     * @return unmodifiable list of all living inhabitants
     * @throws IllegalStateException if population is not initialized
     */
    List<Person> getPopulation();

    List<Person> getSinglePopulation();

    /**
     * Gets inhabitants matching the specified search criteria.
     *
     * <p>This method provides efficient querying capabilities for finding
     * specific subsets of the population based on various criteria such as
     * age ranges, gender, family status, or other demographic characteristics.
     *
     * @param criteria the search criteria to apply
     * @return list of inhabitants matching the criteria (may be empty)
     * @throws IllegalArgumentException if criteria is null
     * @throws IllegalStateException if population is not initialized
     */
    List<Person> findInhabitants(PopulationSearchCriteria criteria);

    /**
     * Adds a new inhabitant to the population.
     *
     * <p>This method is used for immigration and birth events to add new
     * inhabitants to the population. It ensures proper integration with
     * the population tracking and validation systems.
     *
     * @param person the person to add to the population
     * @throws SimulationException if adding the inhabitant fails
     * @throws IllegalArgumentException if person is null or already in population
     * @throws IllegalStateException if population is not initialized
     */
    void addInhabitant(Person person) throws SimulationException;

    /**
     * Removes an inhabitant from the population.
     *
     * <p>This method is used for emigration and death events to remove
     * inhabitants from the population. It ensures proper cleanup and
     * maintains population consistency.
     *
     * @param person the person to remove from the population
     * @throws SimulationException if removal fails
     * @throws IllegalArgumentException if person is null or not in population
     * @throws IllegalStateException if population is not initialized
     */
    void removeInhabitant(Person person) throws SimulationException;

    /**
     * Validates the current population for demographic consistency.
     *
     * <p>This method performs comprehensive validation of the population to
     * ensure demographic patterns remain realistic and consistent. It checks
     * age distributions, family structures, and other demographic constraints.
     *
     * @return validation results containing any issues found
     * @throws IllegalStateException if population is not initialized
     */
    PopulationValidationResult validatePopulation();

    /**
     * Gets demographic statistics for the current population.
     *
     * <p>This method provides comprehensive demographic analysis including
     * age distributions, gender ratios, family structure statistics, and
     * population growth trends.
     *
     * @return demographic statistics for the current population
     * @throws IllegalStateException if population is not initialized
     */
    DemographicStatistics getStatistics();

    /**
     * Checks if the population has been initialized.
     *
     * @return true if the population has been initialized, false otherwise
     */
    boolean isInitialized();
}
