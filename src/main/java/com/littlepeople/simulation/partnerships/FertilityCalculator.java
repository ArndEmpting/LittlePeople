package com.littlepeople.simulation.partnerships;

import com.littlepeople.core.model.Person;
import com.littlepeople.core.model.Gender;
import com.littlepeople.core.model.HealthStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Calculates fertility probability and manages reproductive biology for the simulation.
 *
 * <p>This class implements realistic fertility modeling based on age, health status,
 * and other biological factors. It accounts for the natural decline in fertility
 * with age and provides probability calculations for pregnancy occurrence.</p>
 *
 * <h2>Fertility Model</h2>
 * <p>The fertility model is based on real-world demographic data:</p>
 * <ul>
 *   <li><strong>Peak Fertility:</strong> Ages 20-30 (highest probability)</li>
 *   <li><strong>Gradual Decline:</strong> Ages 30-35 (moderate reduction)</li>
 *   <li><strong>Significant Decline:</strong> Ages 35-40 (substantial reduction)</li>
 *   <li><strong>Late Fertility:</strong> Ages 40-45 (very low probability)</li>
 *   <li><strong>Menopause:</strong> After age 45 (zero probability for females)</li>
 * </ul>
 *
 * <h2>Biological Constraints</h2>
 * <ul>
 *   <li>Only females can become pregnant</li>
 *   <li>Both partners must be alive and healthy</li>
 *   <li>Female must be within fertile age range</li>
 *   <li>Health status affects fertility rates</li>
 * </ul>
 *
 * @since 1.0.0
 * @version 1.0.0
 */
public class FertilityCalculator {

    private static final Logger logger = LoggerFactory.getLogger(FertilityCalculator.class);

    // Age-based fertility parameters
    private static final int MIN_FERTILE_AGE = 16;
    private static final int PEAK_FERTILITY_START = 20;
    private static final int PEAK_FERTILITY_END = 30;
    private static final int FERTILITY_DECLINE_START = 30;
    private static final int SIGNIFICANT_DECLINE_START = 35;
    private static final int LATE_FERTILITY_END = 45;
    private static final int MAX_FERTILE_AGE = 50; // Extreme cases

    // Base fertility rates (monthly probability)
    private static final double PEAK_FERTILITY_RATE = 0.20; // 20% chance per month
    private static final double NORMAL_FERTILITY_RATE = 0.15; // 15% chance per month
    private static final double DECLINING_FERTILITY_RATE = 0.10; // 10% chance per month
    private static final double LOW_FERTILITY_RATE = 0.05; // 5% chance per month
    private static final double VERY_LOW_FERTILITY_RATE = 0.01; // 1% chance per month

    /**
     * Calculates the probability of conception for a partnership in a given month.
     *
     * <p>This method considers the female partner's age, health status, and other
     * biological factors to determine the likelihood of pregnancy. The calculation
     * is based on monthly conception rates from demographic studies.</p>
     *
     * @param malePartner the male partner
     * @param femalePartner the female partner
     * @return monthly conception probability (0.0 to 1.0)
     * @throws IllegalArgumentException if partners are invalid or same gender
     */
    public double calculateMonthlyConceptionProbability(Person malePartner, Person femalePartner) {
        if (malePartner == null || femalePartner == null) {
            throw new IllegalArgumentException("Both partners must be non-null");
        }

        // Verify gender assignment
        if (malePartner.getGender() != Gender.MALE || femalePartner.getGender() != Gender.FEMALE) {
            throw new IllegalArgumentException("Invalid gender combination for conception calculation");
        }

        // Check basic fertility requirements
        if (!isFertile(femalePartner)) {
            logger.debug("Female partner {} is not fertile", femalePartner.getId());
            return 0.0;
        }

        if (!isFertile(malePartner)) {
            logger.debug("Male partner {} is not fertile", malePartner.getId());
            return 0.0;
        }

        // Calculate base fertility rate based on female's age
        double baseFertility = calculateAgeFertility(femalePartner.getAge());

        // Apply health modifiers for both partners
        double healthModifier = calculateHealthModifier(malePartner, femalePartner);

        // Final probability calculation
        double finalProbability = baseFertility * healthModifier;

        logger.debug("Conception probability for {} and {}: base={}, health={}, final={}",
                    malePartner.getId(), femalePartner.getId(),
                    baseFertility, healthModifier, finalProbability);

        return Math.max(0.0, Math.min(1.0, finalProbability));
    }

    /**
     * Calculates the probability of conception for a partnership over a full year.
     *
     * @param malePartner the male partner
     * @param femalePartner the female partner
     * @return annual conception probability (0.0 to 1.0)
     */
    public double calculateAnnualConceptionProbability(Person malePartner, Person femalePartner) {
        double monthlyProbability = calculateMonthlyConceptionProbability(malePartner, femalePartner);

        // Calculate probability of NOT conceiving each month for 12 months
        double noConceptionProbability = Math.pow(1.0 - monthlyProbability, 12);

        // Annual conception probability is complement of no conception
        return 1.0 - noConceptionProbability;
    }

    /**
     * Determines if a person is currently fertile.
     *
     * @param person the person to evaluate
     * @return true if the person is fertile
     */
    public boolean isFertile(Person person) {
        if (person == null || !person.isAlive()) {
            return false;
        }

        int age = person.getAge();

        // Age-based fertility check
        if (age < MIN_FERTILE_AGE || age > MAX_FERTILE_AGE) {
            return false;
        }

        // Health-based fertility check
        if (person.getHealthStatus() == HealthStatus.CRITICALLY_ILL ) {
            return false;
        }

        // Gender-specific checks
        if (person.getGender() == Gender.FEMALE && age > LATE_FERTILITY_END) {
            return false; // Post-menopause
        }

        return true;
    }

    /**
     * Calculates the optimal fertility window for family planning.
     *
     * @param person the person to evaluate (typically female)
     * @return number of years remaining in optimal fertility period
     */
    public int calculateOptimalFertilityYearsRemaining(Person person) {
        if (person == null || person.getGender() != Gender.FEMALE) {
            return 0;
        }

        int currentAge = person.getAge();

        if (currentAge >= SIGNIFICANT_DECLINE_START) {
            return 0; // Past optimal fertility
        }

        return Math.max(0, SIGNIFICANT_DECLINE_START - currentAge);
    }

    /**
     * Estimates the total number of children a partnership might have.
     *
     * @param malePartner the male partner
     * @param femalePartner the female partner
     * @param yearsToProject number of years to project forward
     * @return estimated number of children
     */
    public double estimateExpectedChildren(Person malePartner, Person femalePartner, int yearsToProject) {
        if (yearsToProject <= 0) {
            return 0.0;
        }

        double totalExpectedChildren = 0.0;
        int femaleAge = femalePartner.getAge();

        for (int year = 0; year < yearsToProject; year++) {
            int ageInYear = femaleAge + year;

            // Stop if beyond fertile age
            if (ageInYear > MAX_FERTILE_AGE) {
                break;
            }

            // Create temporary person objects with projected age for calculation
            // This is a simplified approach - in practice, health might change
            double yearlyProbability = calculateAgeFertility(ageInYear) *
                                     calculateHealthModifier(malePartner, femalePartner) * 12;

            totalExpectedChildren += yearlyProbability;
        }

        return totalExpectedChildren;
    }

    /**
     * Calculates base fertility rate based on age.
     *
     * @param age the age to evaluate
     * @return base fertility rate for the age
     */
    private double calculateAgeFertility(int age) {
        if (age < MIN_FERTILE_AGE || age > MAX_FERTILE_AGE) {
            return 0.0;
        }

        if (age >= PEAK_FERTILITY_START && age <= PEAK_FERTILITY_END) {
            return PEAK_FERTILITY_RATE; // Peak fertility
        } else if (age < PEAK_FERTILITY_START) {
            // Young fertility (interpolate from low to peak)
            double factor = (double)(age - MIN_FERTILE_AGE) / (PEAK_FERTILITY_START - MIN_FERTILE_AGE);
            return NORMAL_FERTILITY_RATE + (PEAK_FERTILITY_RATE - NORMAL_FERTILITY_RATE) * factor;
        } else if (age <= FERTILITY_DECLINE_START) {
            return NORMAL_FERTILITY_RATE; // Normal fertility
        } else if (age <= SIGNIFICANT_DECLINE_START) {
            // Gradual decline
            double factor = (double)(age - FERTILITY_DECLINE_START) / (SIGNIFICANT_DECLINE_START - FERTILITY_DECLINE_START);
            return NORMAL_FERTILITY_RATE - (NORMAL_FERTILITY_RATE - DECLINING_FERTILITY_RATE) * factor;
        } else if (age <= LATE_FERTILITY_END) {
            // Significant decline
            double factor = (double)(age - SIGNIFICANT_DECLINE_START) / (LATE_FERTILITY_END - SIGNIFICANT_DECLINE_START);
            return DECLINING_FERTILITY_RATE - (DECLINING_FERTILITY_RATE - LOW_FERTILITY_RATE) * factor;
        } else {
            // Late fertility (very low rates)
            double factor = (double)(age - LATE_FERTILITY_END) / (MAX_FERTILE_AGE - LATE_FERTILITY_END);
            return LOW_FERTILITY_RATE - (LOW_FERTILITY_RATE - VERY_LOW_FERTILITY_RATE) * factor;
        }
    }

    /**
     * Calculates health-based modifier for fertility.
     *
     * @param malePartner the male partner
     * @param femalePartner the female partner
     * @return health modifier (typically 0.5 to 1.2)
     */
    private double calculateHealthModifier(Person malePartner, Person femalePartner) {
        double maleModifier = getHealthFertilityModifier(malePartner.getHealthStatus());
        double femaleModifier = getHealthFertilityModifier(femalePartner.getHealthStatus());

        // Take the geometric mean to account for both partners' health
        return Math.sqrt(maleModifier * femaleModifier);
    }

    /**
     * Gets fertility modifier for a specific health status.
     *
     * @param healthStatus the health status to evaluate
     * @return fertility modifier
     */
    private double getHealthFertilityModifier(HealthStatus healthStatus) {
        switch (healthStatus) {
            case EXCELLENT:
                return 1.2; // Boost for excellent health
            case HEALTHY:
                return 1.0; // Normal fertility
            case SICK:
                return 0.7; // Significant reduction
            case CRITICALLY_ILL:
                return 0.2; // Severe reduction
            default:
                return 1.0;
        }
    }
}
