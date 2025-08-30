package com.littlepeople.simulation.population;

/**
 * Factory class for creating PopulationManager instances with various configurations.
 *
 * <p>This factory provides convenient methods for creating population managers
 * with default or custom configurations. It simplifies the initialization process
 * and provides pre-configured instances for common use cases.
 *
 * @author LittlePeople Development Team
 * @version 1.0
 * @since 1.0
 */
public class PopulationManagerFactory {

    /**
     * Creates a default PopulationManager with standard configuration.
     *
     * @return a new PopulationManager instance with default settings
     */
    public static PopulationManager createDefault() {
        return new PopulationManagerImpl();
    }

    /**
     * Creates a PopulationManager with custom components.
     *
     * @param generator the population generator to use
     * @param growthController the growth controller to use
     * @param validator the demographic validator to use
     * @return a new PopulationManager instance with custom components
     */
    public static PopulationManager createCustom(PopulationGenerator generator,
                                                PopulationGrowthController growthController,
                                                DemographicValidator validator) {
        return new PopulationManagerImpl(generator, growthController, validator);
    }

    /**
     * Creates a PopulationManager with a specific random seed for reproducible results.
     *
     * @param seed the random seed to use for generation
     * @return a new PopulationManager instance with seeded randomization
     */
    public static PopulationManager createWithSeed(long seed) {
        PopulationGenerator generator = new PopulationGenerator(seed);
        PopulationGrowthController growthController = new PopulationGrowthController(generator);
        DemographicValidator validator = new DemographicValidator();

        return new PopulationManagerImpl(generator, growthController, validator);
    }

    /**
     * Creates a PopulationManager optimized for small populations.
     *
     * @return a new PopulationManager instance configured for small populations
     */
    public static PopulationManager createForSmallPopulation() {
        PopulationGenerator generator = new PopulationGenerator();
        PopulationGrowthController growthController = new PopulationGrowthController(generator);
        DemographicValidator validator = new DemographicValidator();

        return new PopulationManagerImpl(generator, growthController, validator);
    }

    /**
     * Creates a PopulationConfiguration with realistic default values.
     *
     * @param initialSize the initial population size
     * @return a configured PopulationConfiguration
     */
    public static PopulationConfiguration createDefaultConfiguration(int initialSize) {
        return PopulationConfiguration.builder()
                .initialPopulationSize(initialSize)
                .demographicDistribution(DemographicDistribution.createDefault())
                .growthRates(GrowthRateConfiguration.createDefault())
                .enableFamilyGeneration(true)
                .minPopulationSize(Math.max(50, initialSize / 10))
                .maxPopulationSize(initialSize * 5)
                .immigrationThreshold(0.8)
                .emigrationThreshold(1.2)
                .build();
    }

    /**
     * Creates a PopulationConfiguration for rapid growth scenarios.
     *
     * @param initialSize the initial population size
     * @return a configured PopulationConfiguration with high growth rates
     */
    public static PopulationConfiguration createHighGrowthConfiguration(int initialSize) {
        GrowthRateConfiguration growthRates = GrowthRateConfiguration.builder()
                .immigrationRate(0.05)  // 5% annual immigration
                .emigrationRate(0.01)   // 1% annual emigration
                .immigrationBatchSize(10)
                .emigrationBatchSize(2)
                .enableSeasonalVariation(true)
                .seasonalFactor(1.5)
                .processingFrequencyDays(3)
                .build();

        return PopulationConfiguration.builder()
                .initialPopulationSize(initialSize)
                .demographicDistribution(DemographicDistribution.createDefault())
                .growthRates(growthRates)
                .enableFamilyGeneration(true)
                .minPopulationSize(initialSize / 2)
                .maxPopulationSize(initialSize * 10)
                .immigrationThreshold(0.7)
                .emigrationThreshold(1.5)
                .build();
    }

    /**
     * Creates a PopulationConfiguration for stable population scenarios.
     *
     * @param initialSize the initial population size
     * @return a configured PopulationConfiguration with balanced growth rates
     */
    public static PopulationConfiguration createStableConfiguration(int initialSize) {
        GrowthRateConfiguration growthRates = GrowthRateConfiguration.builder()
                .immigrationRate(0.015)  // 1.5% annual immigration
                .emigrationRate(0.015)   // 1.5% annual emigration (balanced)
                .immigrationBatchSize(3)
                .emigrationBatchSize(3)
                .enableSeasonalVariation(false)
                .processingFrequencyDays(14)
                .build();

        return PopulationConfiguration.builder()
                .initialPopulationSize(initialSize)
                .demographicDistribution(DemographicDistribution.createDefault())
                .growthRates(growthRates)
                .enableFamilyGeneration(true)
                .minPopulationSize((int) (initialSize * 0.8))
                .maxPopulationSize((int) (initialSize * 1.2))
                .immigrationThreshold(0.9)
                .emigrationThreshold(1.1)
                .build();
    }
}
