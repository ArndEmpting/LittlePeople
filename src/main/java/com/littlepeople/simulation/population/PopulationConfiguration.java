package com.littlepeople.simulation.population;

import java.util.Objects;

/**
 * Configuration class that defines population parameters for initialization and management.
 *
 * <p>This class encapsulates all configuration settings related to population management
 * including initial population size, demographic distributions, growth rates, and
 * validation constraints. It provides a fluent builder interface for easy configuration.
 *
 * @author LittlePeople Development Team
 * @version 1.0
 * @since 1.0
 */
public class PopulationConfiguration {

    private final int initialPopulationSize;
    private final DemographicDistribution demographicDistribution;
    private final GrowthRateConfiguration growthRates;
    private final boolean enableFamilyGeneration;
    private final int minPopulationSize;
    private final int maxPopulationSize;
    private final double immigrationThreshold;
    private final double emigrationThreshold;

    private PopulationConfiguration(Builder builder) {
        this.initialPopulationSize = builder.initialPopulationSize;
        this.demographicDistribution = builder.demographicDistribution;
        this.growthRates = builder.growthRates;
        this.enableFamilyGeneration = builder.enableFamilyGeneration;
        this.minPopulationSize = builder.minPopulationSize;
        this.maxPopulationSize = builder.maxPopulationSize;
        this.immigrationThreshold = builder.immigrationThreshold;
        this.emigrationThreshold = builder.emigrationThreshold;
    }

    /**
     * Gets the initial population size to generate.
     *
     * @return the number of inhabitants to create initially
     */
    public int getInitialPopulationSize() {
        return initialPopulationSize;
    }

    /**
     * Gets the demographic distribution configuration.
     *
     * @return the demographic distribution settings
     */
    public DemographicDistribution getDemographicDistribution() {
        return demographicDistribution;
    }

    /**
     * Gets the growth rate configuration.
     *
     * @return the immigration and emigration rate settings
     */
    public GrowthRateConfiguration getGrowthRates() {
        return growthRates;
    }

    /**
     * Checks if family generation is enabled during initial population creation.
     *
     * @return true if families should be generated, false otherwise
     */
    public boolean isFamilyGenerationEnabled() {
        return enableFamilyGeneration;
    }

    /**
     * Gets the minimum population size threshold.
     *
     * @return the minimum allowed population size
     */
    public int getMinPopulationSize() {
        return minPopulationSize;
    }

    /**
     * Gets the maximum population size threshold.
     *
     * @return the maximum allowed population size
     */
    public int getMaxPopulationSize() {
        return maxPopulationSize;
    }

    /**
     * Gets the immigration threshold as a ratio of current to target population.
     *
     * @return the immigration threshold (0.0 to 1.0)
     */
    public double getImmigrationThreshold() {
        return immigrationThreshold;
    }

    /**
     * Gets the emigration threshold as a ratio of current to target population.
     *
     * @return the emigration threshold (greater than 1.0)
     */
    public double getEmigrationThreshold() {
        return emigrationThreshold;
    }

    /**
     * Validates the population configuration for consistency and realistic values.
     *
     * @throws IllegalArgumentException if configuration is invalid
     */
    public void validate() {
        if (initialPopulationSize <= 0) {
            throw new IllegalArgumentException("Initial population size must be positive");
        }

        if (minPopulationSize < 0) {
            throw new IllegalArgumentException("Minimum population size cannot be negative");
        }

        if (maxPopulationSize <= minPopulationSize) {
            throw new IllegalArgumentException("Maximum population size must be greater than minimum");
        }

        if (initialPopulationSize < minPopulationSize || initialPopulationSize > maxPopulationSize) {
            throw new IllegalArgumentException("Initial population size must be within min/max bounds");
        }

        if (immigrationThreshold < 0.0 || immigrationThreshold > 1.0) {
            throw new IllegalArgumentException("Immigration threshold must be between 0.0 and 1.0");
        }

        if (emigrationThreshold <= 1.0) {
            throw new IllegalArgumentException("Emigration threshold must be greater than 1.0");
        }

        if (demographicDistribution != null) {
            demographicDistribution.validate();
        }

        if (growthRates != null) {
            growthRates.validate();
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;

        PopulationConfiguration that = (PopulationConfiguration) obj;
        return initialPopulationSize == that.initialPopulationSize &&
               enableFamilyGeneration == that.enableFamilyGeneration &&
               minPopulationSize == that.minPopulationSize &&
               maxPopulationSize == that.maxPopulationSize &&
               Double.compare(that.immigrationThreshold, immigrationThreshold) == 0 &&
               Double.compare(that.emigrationThreshold, emigrationThreshold) == 0 &&
               Objects.equals(demographicDistribution, that.demographicDistribution) &&
               Objects.equals(growthRates, that.growthRates);
    }

    @Override
    public int hashCode() {
        return Objects.hash(initialPopulationSize, demographicDistribution, growthRates,
                           enableFamilyGeneration, minPopulationSize, maxPopulationSize,
                           immigrationThreshold, emigrationThreshold);
    }

    @Override
    public String toString() {
        return "PopulationConfiguration{" +
               "initialSize=" + initialPopulationSize +
               ", demographics=" + demographicDistribution +
               ", growthRates=" + growthRates +
               ", familyGeneration=" + enableFamilyGeneration +
               ", minSize=" + minPopulationSize +
               ", maxSize=" + maxPopulationSize +
               ", immigrationThreshold=" + immigrationThreshold +
               ", emigrationThreshold=" + emigrationThreshold +
               '}';
    }

    /**
     * Creates a new builder for PopulationConfiguration.
     *
     * @return a new builder instance
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * Builder class for creating PopulationConfiguration instances.
     */
    public static class Builder {
        private int initialPopulationSize = 1000;
        private DemographicDistribution demographicDistribution = DemographicDistribution.createDefault();
        private GrowthRateConfiguration growthRates = GrowthRateConfiguration.createDefault();
        private boolean enableFamilyGeneration = true;
        private int minPopulationSize = 100;
        private int maxPopulationSize = 10000;
        private double immigrationThreshold = 0.8;
        private double emigrationThreshold = 1.2;

        private Builder() {}

        /**
         * Sets the initial population size.
         *
         * @param size the number of inhabitants to create initially
         * @return this builder
         */
        public Builder initialPopulationSize(int size) {
            this.initialPopulationSize = size;
            return this;
        }

        /**
         * Sets the demographic distribution configuration.
         *
         * @param distribution the demographic distribution settings
         * @return this builder
         */
        public Builder demographicDistribution(DemographicDistribution distribution) {
            this.demographicDistribution = distribution;
            return this;
        }

        /**
         * Sets the growth rate configuration.
         *
         * @param rates the immigration and emigration rate settings
         * @return this builder
         */
        public Builder growthRates(GrowthRateConfiguration rates) {
            this.growthRates = rates;
            return this;
        }

        /**
         * Sets whether family generation is enabled.
         *
         * @param enabled true to enable family generation, false otherwise
         * @return this builder
         */
        public Builder enableFamilyGeneration(boolean enabled) {
            this.enableFamilyGeneration = enabled;
            return this;
        }

        /**
         * Sets the minimum population size threshold.
         *
         * @param minSize the minimum allowed population size
         * @return this builder
         */
        public Builder minPopulationSize(int minSize) {
            this.minPopulationSize = minSize;
            return this;
        }

        /**
         * Sets the maximum population size threshold.
         *
         * @param maxSize the maximum allowed population size
         * @return this builder
         */
        public Builder maxPopulationSize(int maxSize) {
            this.maxPopulationSize = maxSize;
            return this;
        }

        /**
         * Sets the immigration threshold.
         *
         * @param threshold the immigration threshold (0.0 to 1.0)
         * @return this builder
         */
        public Builder immigrationThreshold(double threshold) {
            this.immigrationThreshold = threshold;
            return this;
        }

        /**
         * Sets the emigration threshold.
         *
         * @param threshold the emigration threshold (greater than 1.0)
         * @return this builder
         */
        public Builder emigrationThreshold(double threshold) {
            this.emigrationThreshold = threshold;
            return this;
        }

        /**
         * Builds the PopulationConfiguration instance.
         *
         * @return a new PopulationConfiguration
         * @throws IllegalArgumentException if the configuration is invalid
         */
        public PopulationConfiguration build() {
            PopulationConfiguration config = new PopulationConfiguration(this);
            config.validate();
            return config;
        }
    }
}
