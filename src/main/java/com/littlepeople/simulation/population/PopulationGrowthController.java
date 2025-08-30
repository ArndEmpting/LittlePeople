package com.littlepeople.simulation.population;

import com.littlepeople.core.model.Person;
import com.littlepeople.core.exceptions.SimulationException;
import com.littlepeople.core.model.events.ImmigrationEvent;
import com.littlepeople.core.model.events.EmigrationEvent;

import java.time.LocalDate;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Controls population growth through immigration and emigration processes.
 *
 * <p>This class manages the addition and removal of inhabitants based on
 * configured growth rates, seasonal variations, and economic factors.
 * It ensures realistic migration patterns and respects family relationships.
 *
 * @author LittlePeople Development Team
 * @version 1.0
 * @since 1.0
 */
public class PopulationGrowthController {

    private final PopulationGenerator generator;
    private final Random random;
    private LocalDate lastProcessingDate;

    /**
     * Creates a new PopulationGrowthController.
     *
     * @param generator the population generator to use for immigrants
     */
    public PopulationGrowthController(PopulationGenerator generator) {
        this.generator = generator;
        this.random = ThreadLocalRandom.current();
        this.lastProcessingDate = null;
    }

    /**
     * Processes immigration based on configuration and current population.
     *
     * @param currentPopulation the current population list
     * @param config the population configuration
     * @param currentDate the current simulation date
     * @return immigration event with new immigrants, or null if none
     * @throws SimulationException if immigration processing fails
     */
    public ImmigrationEvent processImmigration(List<Person> currentPopulation,
                                              PopulationConfiguration config,
                                              LocalDate currentDate) throws SimulationException {

        if (shouldSkipProcessing(config.getGrowthRates(), currentDate)) {
            return null;
        }

        int currentSize = currentPopulation.size();
        double immigrationRate = calculateAdjustedImmigrationRate(config, currentSize, currentDate);

        // Calculate number of immigrants based on rate and time elapsed
        int daysElapsed = getDaysElapsed(currentDate);
        double expectedImmigrants = currentSize * immigrationRate * daysElapsed / 365.0;

        int immigrantCount = (int) Math.round(expectedImmigrants);

        // Apply batch sizing and randomization
        if (immigrantCount > 0) {
            int batchSize = config.getGrowthRates().getImmigrationBatchSize();
            immigrantCount = Math.max(1, Math.min(immigrantCount, batchSize));
        }

        if (immigrantCount == 0) {
            return null;
        }

        // Generate immigrants
        List<Person> immigrants = generator.generateImmigrants(
            immigrantCount,
            config.getDemographicDistribution(),
            currentDate
        );

        updateLastProcessingDate(currentDate);

        return new ImmigrationEvent(
            immigrants,
            currentDate,
            "Standard immigration processing",
            immigrationRate
        );
    }

    /**
     * Processes emigration based on configuration and current population.
     *
     * @param currentPopulation the current population list
     * @param config the population configuration
     * @param currentDate the current simulation date
     * @return emigration event with emigrants, or null if none
     * @throws SimulationException if emigration processing fails
     */
    public EmigrationEvent processEmigration(List<Person> currentPopulation,
                                           PopulationConfiguration config,
                                           LocalDate currentDate) throws SimulationException {

        if (shouldSkipProcessing(config.getGrowthRates(), currentDate)) {
            return null;
        }

        int currentSize = currentPopulation.size();
        double emigrationRate = calculateAdjustedEmigrationRate(config, currentSize, currentDate);

        // Calculate number of emigrants based on rate and time elapsed
        int daysElapsed = getDaysElapsed(currentDate);
        double expectedEmigrants = currentSize * emigrationRate * daysElapsed / 365.0;

        int emigrantCount = (int) Math.round(expectedEmigrants);

        // Apply batch sizing
        if (emigrantCount > 0) {
            int batchSize = config.getGrowthRates().getEmigrationBatchSize();
            emigrantCount = Math.max(1, Math.min(emigrantCount, batchSize));
        }

        if (emigrantCount == 0 || currentPopulation.isEmpty()) {
            return null;
        }

        // Select emigrants from current population
        List<Person> emigrants = selectEmigrants(currentPopulation, emigrantCount);

        updateLastProcessingDate(currentDate);

        return new EmigrationEvent(
            emigrants,
            currentDate,
            "Standard emigration processing",
            emigrationRate
        );
    }

    /**
     * Determines if processing should be skipped based on frequency settings.
     *
     * @param growthConfig the growth rate configuration
     * @param currentDate the current date
     * @return true if processing should be skipped
     */
    private boolean shouldSkipProcessing(GrowthRateConfiguration growthConfig, LocalDate currentDate) {
        if (lastProcessingDate == null) {
            return false; // First time processing
        }

        int daysSinceLastProcessing = (int) lastProcessingDate.until(currentDate).getDays();
        return daysSinceLastProcessing < growthConfig.getProcessingFrequencyDays();
    }

    /**
     * Calculates adjusted immigration rate based on various factors.
     *
     * @param config the population configuration
     * @param currentSize the current population size
     * @param currentDate the current date
     * @return the adjusted immigration rate
     */
    private double calculateAdjustedImmigrationRate(PopulationConfiguration config,
                                                   int currentSize,
                                                   LocalDate currentDate) {

        double baseRate = config.getGrowthRates().getImmigrationRate();

        // Apply population pressure adjustment
        double populationRatio = (double) currentSize / config.getInitialPopulationSize();
        double pressureAdjustment = 1.0;

        if (populationRatio < config.getImmigrationThreshold()) {
            // Below threshold - increase immigration
            pressureAdjustment = 1.5;
        } else if (populationRatio > config.getEmigrationThreshold()) {
            // Above emigration threshold - reduce immigration
            pressureAdjustment = 0.3;
        }

        // Apply seasonal variation if enabled
        double seasonalAdjustment = 1.0;
        if (config.getGrowthRates().isSeasonalVariationEnabled()) {
            seasonalAdjustment = calculateSeasonalFactor(currentDate, config.getGrowthRates());
        }

        return baseRate * pressureAdjustment * seasonalAdjustment;
    }

    /**
     * Calculates adjusted emigration rate based on various factors.
     *
     * @param config the population configuration
     * @param currentSize the current population size
     * @param currentDate the current date
     * @return the adjusted emigration rate
     */
    private double calculateAdjustedEmigrationRate(PopulationConfiguration config,
                                                  int currentSize,
                                                  LocalDate currentDate) {

        double baseRate = config.getGrowthRates().getEmigrationRate();

        // Apply population pressure adjustment
        double populationRatio = (double) currentSize / config.getInitialPopulationSize();
        double pressureAdjustment = 1.0;

        if (populationRatio > config.getEmigrationThreshold()) {
            // Above threshold - increase emigration
            pressureAdjustment = 1.5;
        } else if (populationRatio < config.getImmigrationThreshold()) {
            // Below immigration threshold - reduce emigration
            pressureAdjustment = 0.3;
        }

        // Apply seasonal variation if enabled
        double seasonalAdjustment = 1.0;
        if (config.getGrowthRates().isSeasonalVariationEnabled()) {
            seasonalAdjustment = calculateSeasonalFactor(currentDate, config.getGrowthRates());
        }

        return baseRate * pressureAdjustment * seasonalAdjustment;
    }

    /**
     * Calculates seasonal factor based on time of year.
     *
     * @param currentDate the current date
     * @param growthConfig the growth configuration
     * @return the seasonal adjustment factor
     */
    private double calculateSeasonalFactor(LocalDate currentDate, GrowthRateConfiguration growthConfig) {
        int dayOfYear = currentDate.getDayOfYear();

        // Spring/Summer peak (higher migration)
        // Assume peak around day 150 (late May/early June)
        double seasonalCycle = Math.sin(2 * Math.PI * (dayOfYear - 80) / 365.0);

        // Convert to factor between 0.5 and seasonalFactor
        double range = growthConfig.getSeasonalFactor() - 0.5;
        return 0.5 + range * (seasonalCycle + 1) / 2.0;
    }

    /**
     * Selects emigrants from the current population.
     *
     * @param population the current population
     * @param count the number of emigrants to select
     * @return list of selected emigrants
     */
    private List<Person> selectEmigrants(List<Person> population, int count) {
        List<Person> candidates = new ArrayList<>(population);
        Collections.shuffle(candidates, random);

        List<Person> emigrants = new ArrayList<>();

        // Prefer single adults without children for emigration
        for (Person person : candidates) {
            if (emigrants.size() >= count) break;

            if (person.getPartner() == null && person.getChildren().isEmpty()) {
                emigrants.add(person);
            }
        }

        // If we need more emigrants, select from remaining population
        if (emigrants.size() < count) {
            for (Person person : candidates) {
                if (emigrants.size() >= count) break;

                if (!emigrants.contains(person)) {
                    emigrants.add(person);
                }
            }
        }

        return emigrants.subList(0, Math.min(count, emigrants.size()));
    }

    /**
     * Gets the number of days elapsed since last processing.
     *
     * @param currentDate the current date
     * @return the number of days elapsed
     */
    private int getDaysElapsed(LocalDate currentDate) {
        if (lastProcessingDate == null) {
            return 1; // First processing
        }
        return (int) lastProcessingDate.until(currentDate).getDays();
    }

    /**
     * Updates the last processing date.
     *
     * @param currentDate the current date
     */
    private void updateLastProcessingDate(LocalDate currentDate) {
        this.lastProcessingDate = currentDate;
    }
}
