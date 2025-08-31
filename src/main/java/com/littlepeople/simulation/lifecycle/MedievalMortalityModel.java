package com.littlepeople.simulation.lifecycle;

import com.littlepeople.core.model.HealthStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A medieval mortality model based on historical demographic data from the Middle Ages.
 * This model reflects the harsh realities of medieval life with high infant mortality,
 * frequent disease outbreaks, and lower life expectancy.
 *
 * Key characteristics:
 * - High infant mortality (up to 30%)
 * - Significant child mortality (ages 1-15)
 * - Lower life expectancy (average 35-40 years)
 * - Higher mortality rates across all age groups
 * - Greater impact of health status on survival
 *
 * @author LittlePeople Development Team
 * @version 1.0
 * @since 1.0
 */
public class MedievalMortalityModel implements MortalityModel {

    private static final Logger logger = LoggerFactory.getLogger(MedievalMortalityModel.class);

    // Medieval mortality parameters
    private double infantMortalityRate = 0.25; // 25% infant mortality (very high for medieval times)
    private double childMortalityRate = 0.15;  // 15% mortality for ages 1-5
    private double adolescentMortalityRate = 0.08; // 8% mortality for ages 6-15

    // Adult mortality follows a steeper curve than modern times
    private double baseAdultMortality = 0.01;     // 1% base mortality for young adults
    private double mortalityIncreaseFactor = 0.1; // Faster increase with age than modern
    private double maxMortalityRate = 0.95;       // Maximum mortality rate

    // Disease and warfare factors
    private double plagueYearMultiplier = 3.0;    // 3x mortality during plague years
    private double warYearMultiplier = 2.0;       // 2x mortality during war years

    /**
     * Creates a medieval mortality model with default parameters.
     */
    public MedievalMortalityModel() {
        logger.info("Initialized medieval mortality model with default parameters");
    }

    /**
     * Creates a medieval mortality model with custom parameters.
     *
     * @param infantMortalityRate mortality rate for infants under 1 year
     * @param childMortalityRate mortality rate for children ages 1-5
     * @param adolescentMortalityRate mortality rate for adolescents ages 6-15
     * @param baseAdultMortality base mortality for young adults
     * @param mortalityIncreaseFactor rate at which mortality increases with age
     */
    public MedievalMortalityModel(
            double infantMortalityRate,
            double childMortalityRate,
            double adolescentMortalityRate,
            double baseAdultMortality,
            double mortalityIncreaseFactor) {

        this.infantMortalityRate = infantMortalityRate;
        this.childMortalityRate = childMortalityRate;
        this.adolescentMortalityRate = adolescentMortalityRate;
        this.baseAdultMortality = baseAdultMortality;
        this.mortalityIncreaseFactor = mortalityIncreaseFactor;

        logger.info("Initialized medieval mortality model with custom parameters");
    }

    @Override
    public double calculateBaselineProbability(int age) {
        return calculateBaselineProbability(age, TimeUnit.YEAR);
    }

    /**
     * Calculates the baseline mortality probability for a given age and time unit.
     *
     * @param age the age in years
     * @param timeUnit the time unit for the probability calculation
     * @return the baseline mortality probability for the specified time period
     */
    public double calculateBaselineProbability(int age, TimeUnit timeUnit) {
        if (age < 0) {
            throw new IllegalArgumentException("Age cannot be negative: " + age);
        }

        double annualProbability = calculateAnnualProbability(age);

        // Convert to requested time unit
        return convertProbabilityToTimeUnit(annualProbability, timeUnit);
    }

    /**
     * Calculates the baseline mortality probability for a given age in months and time unit.
     *
     * @param ageInMonths the age in months
     * @param timeUnit the time unit for the probability calculation
     * @return the baseline mortality probability for the specified time period
     */
    public double calculateBaselineProbabilityByMonths(int ageInMonths, TimeUnit timeUnit) {
        if (ageInMonths < 0) {
            throw new IllegalArgumentException("Age in months cannot be negative: " + ageInMonths);
        }

        double annualProbability = calculateAnnualProbabilityByMonths(ageInMonths);

        // Convert to requested time unit
        return convertProbabilityToTimeUnit(annualProbability, timeUnit);
    }

    /**
     * Internal method to calculate annual probability for a given age in years.
     */
    private double calculateAnnualProbability(int age) {
        // Infants (0-1 years) - extremely high mortality
        if (age == 0) {
            return infantMortalityRate;
        }

        // Young children (1-5 years) - high mortality
        if (age >= 1 && age <= 5) {
            return childMortalityRate * (1.0 - (age - 1) * 0.1); // Decreases slightly with age
        }

        // Adolescents (6-15 years) - moderate mortality
        if (age >= 6 && age <= 15) {
            return adolescentMortalityRate * (1.0 - (age - 6) * 0.05); // Decreases with age
        }

        // Adults (16+ years) - exponential increase with age
        // Using a modified exponential model that reflects medieval conditions
        double ageFactor = Math.max(0, age - 30); // Start counting from age 16
        double probability = baseAdultMortality * Math.exp(ageFactor * mortalityIncreaseFactor);

        // Medieval people rarely lived past 60-70, so increase mortality rapidly after 50
        if (age >= 70) {
            probability *= 1.5 + (age - 70) * 0.2; // Rapidly increasing mortality
        }

        // Cap at maximum mortality rate
        return Math.min(probability, maxMortalityRate);
    }

    /**
     * Internal method to calculate annual probability for a given age in months.
     */
    private double calculateAnnualProbabilityByMonths(int ageInMonths) {
        double ageInYears = ageInMonths / 12.0;

        // Special handling for first year of life (month by month)
        if (ageInMonths < 12) {
            // Very high mortality in first months, gradually decreasing
            double monthFactor = 1.0 - (ageInMonths * 0.04); // Decrease by 4% each month
            return infantMortalityRate * Math.max(monthFactor, 0.5);
        }

        // Young children (1-5 years) - high mortality
        if (ageInYears >= 1 && ageInYears <= 5) {
            return childMortalityRate * (1.0 - (ageInYears - 1) * 0.1); // Decreases slightly with age
        }

        // Adolescents (6-15 years) - moderate mortality
        if (ageInYears >= 6 && ageInYears <= 15) {
            return adolescentMortalityRate * (1.0 - (ageInYears - 6) * 0.05); // Decreases with age
        }

        // Adults (16+ years) - exponential increase with age
        double ageFactor = Math.max(0, ageInYears - 30);
        double probability = baseAdultMortality * Math.exp(ageFactor * mortalityIncreaseFactor);

        // Medieval people rarely lived past 60-70, so increase mortality rapidly after 50
        if (ageInYears >= 70) {
            probability *= 1.5 + (ageInYears - 70) * 0.2; // Rapidly increasing mortality
        }

        // Cap at maximum mortality rate
        return Math.min(probability, maxMortalityRate);
    }

    /**
     * Converts annual probability to the specified time unit.
     *
     * @param annualProbability the annual mortality probability
     * @param timeUnit the target time unit
     * @return the probability converted to the specified time unit
     */
    private double convertProbabilityToTimeUnit(double annualProbability, TimeUnit timeUnit) {
        return switch (timeUnit) {
            case YEAR -> annualProbability;
            case MONTH -> {
                // Convert annual probability to monthly using: 1 - (1-p_annual)^(1/12)
                if (annualProbability >= 1.0) {
                    yield 1.0;
                }
                yield 1.0 - Math.pow(1.0 - annualProbability, 1.0/12.0);
            }
            case DAY -> {
                // Convert annual probability to daily using: 1 - (1-p_annual)^(1/365)
                if (annualProbability >= 1.0) {
                    yield 1.0;
                }
                yield 1.0 - Math.pow(1.0 - annualProbability, 1.0/365.0);
            }
        };
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

        // Health had a much greater impact on survival in medieval times
        // due to lack of medical care
        return switch (healthStatus) {
            case EXCELLENT -> baselineProbability * 0.6; // 40% reduction for excellent health
            case HEALTHY -> baselineProbability * 0.8;   // 20% reduction for healthy
            case SICK -> baselineProbability * 2.5;      // 2.5x increase for sick (much higher than modern)
            case CRITICALLY_ILL -> baselineProbability * 8.0; // 8x increase for critically ill
            default -> baselineProbability; // No adjustment for unknown status
        };
    }

    @Override
    public String getModelName() {
        return "Medieval Mortality Model";
    }

    @Override
    public MortalityModelType getModelType() {
        return MortalityModelType.HISTORICAL;
    }

    /**
     * Adjusts mortality for special circumstances like plague or war years.
     * This method can be called to simulate historical events.
     *
     * @param baselineProbability the baseline mortality probability
     * @param isPlagueYear true if this is a plague year
     * @param isWarYear true if this is a war year
     * @return adjusted mortality probability
     */
    public double adjustForHistoricalEvents(double baselineProbability, boolean isPlagueYear, boolean isWarYear) {
        double adjustedProbability = baselineProbability;

        if (isPlagueYear) {
            adjustedProbability *= plagueYearMultiplier;
        }

        if (isWarYear) {
            adjustedProbability *= warYearMultiplier;
        }

        return Math.min(adjustedProbability, maxMortalityRate);
    }

    // Getters and setters for configuration

    public double getInfantMortalityRate() {
        return infantMortalityRate;
    }

    public void setInfantMortalityRate(double infantMortalityRate) {
        this.infantMortalityRate = infantMortalityRate;
    }

    public double getChildMortalityRate() {
        return childMortalityRate;
    }

    public void setChildMortalityRate(double childMortalityRate) {
        this.childMortalityRate = childMortalityRate;
    }

    public double getAdolescentMortalityRate() {
        return adolescentMortalityRate;
    }

    public void setAdolescentMortalityRate(double adolescentMortalityRate) {
        this.adolescentMortalityRate = adolescentMortalityRate;
    }

    public double getBaseAdultMortality() {
        return baseAdultMortality;
    }

    public void setBaseAdultMortality(double baseAdultMortality) {
        this.baseAdultMortality = baseAdultMortality;
    }

    public double getMortalityIncreaseFactor() {
        return mortalityIncreaseFactor;
    }

    public void setMortalityIncreaseFactor(double mortalityIncreaseFactor) {
        this.mortalityIncreaseFactor = mortalityIncreaseFactor;
    }

    public double getPlagueYearMultiplier() {
        return plagueYearMultiplier;
    }

    public void setPlagueYearMultiplier(double plagueYearMultiplier) {
        this.plagueYearMultiplier = plagueYearMultiplier;
    }

    public double getWarYearMultiplier() {
        return warYearMultiplier;
    }

    public void setWarYearMultiplier(double warYearMultiplier) {
        this.warYearMultiplier = warYearMultiplier;
    }


}
