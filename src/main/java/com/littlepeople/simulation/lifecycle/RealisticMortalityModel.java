package com.littlepeople.simulation.lifecycle;

import com.littlepeople.core.model.HealthStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A realistic mortality model based on modern demographic data using the Gompertz-Makeham law.
 *
 * @author LittlePeople Development Team
 * @version 1.0
 * @since 1.0
 */
public class RealisticMortalityModel implements MortalityModel {

    private static final Logger logger = LoggerFactory.getLogger(RealisticMortalityModel.class);

    // Gompertz-Makeham model parameters (realistic human mortality)
    private double alpha = 0.0001; // Base mortality (independent of age)
    private double beta = 0.085;   // Rate of mortality increase with age
    private double gamma = 0.000001; // Initial mortality

    // Infant mortality parameters
    private double infantMortalityRate = 0.004; // 4 per 1000 (modern rate)
    private double childMortalityFactor = 0.3;  // Adjustment for child mortality

    /**
     * Creates a realistic mortality model with default parameters.
     */
    public RealisticMortalityModel() {
        logger.info("Initialized realistic mortality model with default parameters");
    }

    /**
     * Creates a realistic mortality model with custom parameters.
     *
     * @param alpha base mortality rate (independent of age)
     * @param beta rate of mortality increase with age
     * @param gamma initial mortality rate
     * @param infantMortalityRate mortality rate for infants under 1 year
     * @param childMortalityFactor adjustment factor for child mortality
     */
    public RealisticMortalityModel(
            double alpha,
            double beta,
            double gamma,
            double infantMortalityRate,
            double childMortalityFactor) {

        this.alpha = alpha;
        this.beta = beta;
        this.gamma = gamma;
        this.infantMortalityRate = infantMortalityRate;
        this.childMortalityFactor = childMortalityFactor;

        logger.info("Initialized realistic mortality model with custom parameters");
    }

    @Override
    public double calculateBaselineProbability(int age) {
        if (age < 0) {
            throw new IllegalArgumentException("Age cannot be negative: " + age);
        }

        // Special case for infants (under 1 year)
        if (age == 0) {
            return infantMortalityRate;
        }

        // Special case for young children (1-5 years)
        if (age <= 5) {
            return infantMortalityRate * childMortalityFactor * (6 - age) / 5.0;
        }

        // Gompertz-Makeham model for adult mortality
        // q(x) = alpha + gamma * exp(beta * x)
        // where x is age, and q(x) is probability of death
        double probability = alpha + gamma * Math.exp(beta * age);

        // Cap at 1.0 (100% probability)
        return Math.min(probability, 1.0);
    }

    @Override
    public double adjustForHealth(double baselineProbability, HealthStatus healthStatus) {
        if (baselineProbability < 0 || baselineProbability > 1) {
            throw new IllegalArgumentException(
                    "Baseline probability must be between 0 and 1: " + baselineProbability);
        }

        if (healthStatus == null) {
            return baselineProbability; // No adjustment for null health status
        }

        // Adjust based on health status
        return switch (healthStatus) {
            case HEALTHY -> baselineProbability * 0.8; // 20% reduction for healthy individuals
            case SICK -> baselineProbability * 1.5; // 50% increase for sick individuals
            case CRITICALLY_ILL -> baselineProbability * 5.0; // 5x increase for critically ill
            default -> baselineProbability; // No adjustment for unknown status
        };
    }

    @Override
    public String getModelName() {
        return "Realistic Mortality Model";
    }

    @Override
    public MortalityModelType getModelType() {
        return MortalityModelType.MODERN;
    }

    // Getters and setters for configuration

    public double getAlpha() {
        return alpha;
    }

    public void setAlpha(double alpha) {
        this.alpha = alpha;
    }

    public double getBeta() {
        return beta;
    }

    public void setBeta(double beta) {
        this.beta = beta;
    }

    public double getGamma() {
        return gamma;
    }

    public void setGamma(double gamma) {
        this.gamma = gamma;
    }

    public double getInfantMortalityRate() {
        return infantMortalityRate;
    }

    public void setInfantMortalityRate(double infantMortalityRate) {
        this.infantMortalityRate = infantMortalityRate;
    }

    public double getChildMortalityFactor() {
        return childMortalityFactor;
    }

    public void setChildMortalityFactor(double childMortalityFactor) {
        this.childMortalityFactor = childMortalityFactor;
    }
}
