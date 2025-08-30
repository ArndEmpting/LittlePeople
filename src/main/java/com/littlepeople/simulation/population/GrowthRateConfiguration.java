package com.littlepeople.simulation.population;

import java.util.Objects;

/**
 * Configuration class for population growth rates including immigration and emigration.
 *
 * <p>This class defines the rates and patterns for population changes through
 * immigration and emigration. It provides configurable parameters for controlling
 * how the population grows or shrinks over time based on realistic demographic
 * patterns and user-defined scenarios.
 *
 * @author LittlePeople Development Team
 * @version 1.0
 * @since 1.0
 */
public class GrowthRateConfiguration {

    private final double immigrationRate;
    private final double emigrationRate;
    private final int immigrationBatchSize;
    private final int emigrationBatchSize;
    private final boolean seasonalVariation;
    private final double seasonalFactor;
    private final boolean economicFactors;
    private final double economicInfluence;
    private final int processingFrequencyDays;

    private GrowthRateConfiguration(Builder builder) {
        this.immigrationRate = builder.immigrationRate;
        this.emigrationRate = builder.emigrationRate;
        this.immigrationBatchSize = builder.immigrationBatchSize;
        this.emigrationBatchSize = builder.emigrationBatchSize;
        this.seasonalVariation = builder.seasonalVariation;
        this.seasonalFactor = builder.seasonalFactor;
        this.economicFactors = builder.economicFactors;
        this.economicInfluence = builder.economicInfluence;
        this.processingFrequencyDays = builder.processingFrequencyDays;
    }

    /**
     * Gets the annual immigration rate as a percentage of current population.
     *
     * @return the immigration rate (0.0 to 1.0)
     */
    public double getImmigrationRate() {
        return immigrationRate;
    }

    /**
     * Gets the annual emigration rate as a percentage of current population.
     *
     * @return the emigration rate (0.0 to 1.0)
     */
    public double getEmigrationRate() {
        return emigrationRate;
    }

    /**
     * Gets the typical batch size for immigration events.
     *
     * @return the number of immigrants per batch
     */
    public int getImmigrationBatchSize() {
        return immigrationBatchSize;
    }

    /**
     * Gets the typical batch size for emigration events.
     *
     * @return the number of emigrants per batch
     */
    public int getEmigrationBatchSize() {
        return emigrationBatchSize;
    }

    /**
     * Checks if seasonal variation is enabled for migration rates.
     *
     * @return true if seasonal variation is enabled, false otherwise
     */
    public boolean isSeasonalVariationEnabled() {
        return seasonalVariation;
    }

    /**
     * Gets the seasonal variation factor.
     *
     * @return the seasonal factor (multiplier for rates)
     */
    public double getSeasonalFactor() {
        return seasonalFactor;
    }

    /**
     * Checks if economic factors influence migration rates.
     *
     * @return true if economic factors are enabled, false otherwise
     */
    public boolean areEconomicFactorsEnabled() {
        return economicFactors;
    }

    /**
     * Gets the economic influence factor on migration rates.
     *
     * @return the economic influence factor
     */
    public double getEconomicInfluence() {
        return economicInfluence;
    }

    /**
     * Gets the frequency (in days) for processing migration events.
     *
     * @return the processing frequency in days
     */
    public int getProcessingFrequencyDays() {
        return processingFrequencyDays;
    }

    /**
     * Calculates the net growth rate (immigration - emigration).
     *
     * @return the net growth rate
     */
    public double getNetGrowthRate() {
        return immigrationRate - emigrationRate;
    }

    /**
     * Calculates the daily immigration rate based on annual rate.
     *
     * @return the daily immigration rate
     */
    public double getDailyImmigrationRate() {
        return immigrationRate / 365.0;
    }

    /**
     * Calculates the daily emigration rate based on annual rate.
     *
     * @return the daily emigration rate
     */
    public double getDailyEmigrationRate() {
        return emigrationRate / 365.0;
    }

    /**
     * Validates the growth rate configuration for consistency.
     *
     * @throws IllegalArgumentException if configuration is invalid
     */
    public void validate() {
        if (immigrationRate < 0.0 || immigrationRate > 1.0) {
            throw new IllegalArgumentException("Immigration rate must be between 0.0 and 1.0");
        }

        if (emigrationRate < 0.0 || emigrationRate > 1.0) {
            throw new IllegalArgumentException("Emigration rate must be between 0.0 and 1.0");
        }

        if (immigrationBatchSize <= 0) {
            throw new IllegalArgumentException("Immigration batch size must be positive");
        }

        if (emigrationBatchSize <= 0) {
            throw new IllegalArgumentException("Emigration batch size must be positive");
        }

        if (seasonalFactor <= 0.0) {
            throw new IllegalArgumentException("Seasonal factor must be positive");
        }

        if (economicInfluence < 0.0) {
            throw new IllegalArgumentException("Economic influence must be non-negative");
        }

        if (processingFrequencyDays <= 0) {
            throw new IllegalArgumentException("Processing frequency must be positive");
        }
    }

    /**
     * Creates a default growth rate configuration with realistic values.
     *
     * @return a default growth rate configuration
     */
    public static GrowthRateConfiguration createDefault() {
        return builder()
                .immigrationRate(0.02)  // 2% annual immigration
                .emigrationRate(0.015)  // 1.5% annual emigration
                .immigrationBatchSize(5)
                .emigrationBatchSize(3)
                .enableSeasonalVariation(true)
                .seasonalFactor(1.2)
                .enableEconomicFactors(false)
                .economicInfluence(0.1)
                .processingFrequencyDays(7)
                .build();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;

        GrowthRateConfiguration that = (GrowthRateConfiguration) obj;
        return Double.compare(that.immigrationRate, immigrationRate) == 0 &&
               Double.compare(that.emigrationRate, emigrationRate) == 0 &&
               immigrationBatchSize == that.immigrationBatchSize &&
               emigrationBatchSize == that.emigrationBatchSize &&
               seasonalVariation == that.seasonalVariation &&
               Double.compare(that.seasonalFactor, seasonalFactor) == 0 &&
               economicFactors == that.economicFactors &&
               Double.compare(that.economicInfluence, economicInfluence) == 0 &&
               processingFrequencyDays == that.processingFrequencyDays;
    }

    @Override
    public int hashCode() {
        return Objects.hash(immigrationRate, emigrationRate, immigrationBatchSize,
                           emigrationBatchSize, seasonalVariation, seasonalFactor,
                           economicFactors, economicInfluence, processingFrequencyDays);
    }

    @Override
    public String toString() {
        return "GrowthRateConfiguration{" +
               "immigrationRate=" + immigrationRate +
               ", emigrationRate=" + emigrationRate +
               ", immigrationBatch=" + immigrationBatchSize +
               ", emigrationBatch=" + emigrationBatchSize +
               ", seasonal=" + seasonalVariation +
               ", seasonalFactor=" + seasonalFactor +
               ", economic=" + economicFactors +
               ", economicInfluence=" + economicInfluence +
               ", frequency=" + processingFrequencyDays +
               '}';
    }

    /**
     * Creates a new builder for GrowthRateConfiguration.
     *
     * @return a new builder instance
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * Builder class for creating GrowthRateConfiguration instances.
     */
    public static class Builder {
        private double immigrationRate = 0.02;
        private double emigrationRate = 0.015;
        private int immigrationBatchSize = 5;
        private int emigrationBatchSize = 3;
        private boolean seasonalVariation = true;
        private double seasonalFactor = 1.2;
        private boolean economicFactors = false;
        private double economicInfluence = 0.1;
        private int processingFrequencyDays = 7;

        private Builder() {}

        /**
         * Sets the annual immigration rate.
         *
         * @param rate the immigration rate (0.0 to 1.0)
         * @return this builder
         */
        public Builder immigrationRate(double rate) {
            this.immigrationRate = rate;
            return this;
        }

        /**
         * Sets the annual emigration rate.
         *
         * @param rate the emigration rate (0.0 to 1.0)
         * @return this builder
         */
        public Builder emigrationRate(double rate) {
            this.emigrationRate = rate;
            return this;
        }

        /**
         * Sets the immigration batch size.
         *
         * @param size the number of immigrants per batch
         * @return this builder
         */
        public Builder immigrationBatchSize(int size) {
            this.immigrationBatchSize = size;
            return this;
        }

        /**
         * Sets the emigration batch size.
         *
         * @param size the number of emigrants per batch
         * @return this builder
         */
        public Builder emigrationBatchSize(int size) {
            this.emigrationBatchSize = size;
            return this;
        }

        /**
         * Enables or disables seasonal variation.
         *
         * @param enabled true to enable seasonal variation
         * @return this builder
         */
        public Builder enableSeasonalVariation(boolean enabled) {
            this.seasonalVariation = enabled;
            return this;
        }

        /**
         * Sets the seasonal variation factor.
         *
         * @param factor the seasonal factor
         * @return this builder
         */
        public Builder seasonalFactor(double factor) {
            this.seasonalFactor = factor;
            return this;
        }

        /**
         * Enables or disables economic factors.
         *
         * @param enabled true to enable economic factors
         * @return this builder
         */
        public Builder enableEconomicFactors(boolean enabled) {
            this.economicFactors = enabled;
            return this;
        }

        /**
         * Sets the economic influence factor.
         *
         * @param influence the economic influence factor
         * @return this builder
         */
        public Builder economicInfluence(double influence) {
            this.economicInfluence = influence;
            return this;
        }

        /**
         * Sets the processing frequency in days.
         *
         * @param days the frequency in days
         * @return this builder
         */
        public Builder processingFrequencyDays(int days) {
            this.processingFrequencyDays = days;
            return this;
        }

        /**
         * Builds the GrowthRateConfiguration instance.
         *
         * @return a new GrowthRateConfiguration
         * @throws IllegalArgumentException if the configuration is invalid
         */
        public GrowthRateConfiguration build() {
            GrowthRateConfiguration config = new GrowthRateConfiguration(this);
            config.validate();
            return config;
        }
    }
}
