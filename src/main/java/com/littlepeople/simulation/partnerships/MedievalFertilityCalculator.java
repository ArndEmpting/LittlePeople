package com.littlepeople.simulation.partnerships;

import com.littlepeople.core.model.Person;
import com.littlepeople.core.model.Gender;
import com.littlepeople.core.model.HealthStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Calculates fertility probability for medieval times with historically accurate rates.
 *
 * <p>This class implements medieval fertility modeling based on historical demographic data.
 * Medieval fertility was characterized by early marriage, higher birth rates, longer fertile
 * periods, and significant health impacts on reproductive success.</p>
 *
 * <h2>Medieval Fertility Characteristics</h2>
 * <ul>
 *   <li><strong>Early Fertility:</strong> Ages 14-18 (early marriage and childbearing)</li>
 *   <li><strong>Peak Fertility:</strong> Ages 18-35 (highest probability, longer peak)</li>
 *   <li><strong>Extended Fertility:</strong> Ages 35-45 (still significant rates)</li>
 *   <li><strong>Late Fertility:</strong> Ages 45-50 (lower but still possible)</li>
 *   <li><strong>Higher Overall Rates:</strong> No contraception, cultural pressure for children</li>
 * </ul>
 *
 * <h2>Historical Context</h2>
 * <ul>
 *   <li>Average family size: 5-8 children</li>
 *   <li>Marriage age: 14-16 for women, 16-20 for men</li>
 *   <li>High infant mortality led to more pregnancies</li>
 *   <li>Health status had greater impact due to poor medical care</li>
 *   <li>Nutrition and disease significantly affected fertility</li>
 * </ul>
 *
 * @since 1.0.0
 * @version 1.0.0
 */
public class MedievalFertilityCalculator  implements FertilityCalculatorInterface{

    private static final Logger logger = LoggerFactory.getLogger(MedievalFertilityCalculator.class);

    // Medieval age parameters
    private static final int MIN_FERTILE_AGE_FEMALE = 14; // Earlier menarche, early marriage
    private static final int MIN_FERTILE_AGE_MALE = 16;   // Earlier sexual maturity
    private static final int PEAK_FERTILITY_START = 18;
    private static final int PEAK_FERTILITY_END = 35;     // Longer peak period
    private static final int FERTILITY_DECLINE_START = 35;
    private static final int LATE_FERTILITY_END = 50;     // Extended fertility
    private static final int MAX_FERTILE_AGE_FEMALE = 52; // Occasional late pregnancies
    private static final int MAX_FERTILE_AGE_MALE = 65;   // Extended male fertility

    // Medieval fertility rates (monthly probability) - higher than modern
    private static final double PEAK_FERTILITY_RATE = 0.35;      // 35% chance per month (very high)
    private static final double HIGH_FERTILITY_RATE = 0.28;      // 28% for young adults
    private static final double NORMAL_FERTILITY_RATE = 0.22;    // 22% normal rate
    private static final double DECLINING_FERTILITY_RATE = 0.15; // 15% declining phase
    private static final double LOW_FERTILITY_RATE = 0.08;       // 8% late fertility
    private static final double VERY_LOW_FERTILITY_RATE = 0.03;  // 3% very late

    // Medieval-specific factors
    private static final double MALNUTRITION_FERTILITY_PENALTY = 0.4; // 60% reduction
    private static final double DISEASE_FERTILITY_PENALTY = 0.2;      // 80% reduction
    private static final double WINTER_FERTILITY_REDUCTION = 0.8;     // 20% reduction in winter
    private static final double HARVEST_FAILURE_PENALTY = 0.5;        // 50% reduction during famines

    /**
     * Calculates the probability of conception for a medieval partnership in a given month.
     *
     * @param malePartner the male partner
     * @param femalePartner the female partner
     * @return monthly conception probability (0.0 to 1.0)
     * @throws IllegalArgumentException if partners are invalid
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
        double baseFertility = calculateMedievalAgeFertility(femalePartner.getAge());

        // Apply medieval health modifiers (more severe impact than modern times)
        double healthModifier = calculateMedievalHealthModifier(malePartner, femalePartner);

        // Apply male age factor (medieval men also affected by age earlier)
        double maleAgeFactor = calculateMaleAgeFactor(malePartner.getAge());

        // Final probability calculation
        double finalProbability = baseFertility * healthModifier * maleAgeFactor;

        logger.debug("Medieval conception probability for {} and {}: base={}, health={}, maleAge={}, final={}",
                    malePartner.getId(), femalePartner.getId(),
                    baseFertility, healthModifier, maleAgeFactor, finalProbability);

        return Math.max(0.0, Math.min(1.0, finalProbability));
    }

    /**
     * Calculates the probability of conception over a full year.
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
     * Determines if a person is currently fertile according to medieval standards.
     *
     * @param person the person to evaluate
     * @return true if the person is fertile
     */
    public boolean isFertile(Person person) {
        if (person == null || !person.isAlive()) {
            return false;
        }
        if(person.getChildren().size()>=20){
            return false; // Prevent excessive number of children in medieval context
        }
        int age = person.getAge();

        // Age-based fertility check (more lenient than modern)
        if (person.getGender() == Gender.FEMALE) {
            if (age < MIN_FERTILE_AGE_FEMALE || age > MAX_FERTILE_AGE_FEMALE) {
                return false;
            }
            // person has children and one of it is age 0
            if ( person.getChildren().stream().anyMatch(child -> child.getAge() == 0)) {
                return false;
            }
        } else if (person.getGender() == Gender.MALE) {
            if (age < MIN_FERTILE_AGE_MALE || age > MAX_FERTILE_AGE_MALE) {
                return false;
            }
        }

        // Health-based fertility check (more severe impact than modern)
        if (person.getHealthStatus() == HealthStatus.CRITICALLY_ILL) {
            return false; // Critical illness prevents fertility
        }

        return true;
    }

    /**
     * Estimates expected number of children for a medieval family.
     * Medieval families typically had 5-8 children due to high fertility and no contraception.
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
            if (ageInYear > MAX_FERTILE_AGE_FEMALE) {
                break;
            }

            // Calculate yearly probability
            double monthlyProbability = calculateMedievalAgeFertility(ageInYear) *
                                      calculateMedievalHealthModifier(malePartner, femalePartner) *
                                      calculateMaleAgeFactor(malePartner.getAge() + year);

            // Account for 12 months, but with diminishing returns due to pregnancy/nursing
            double yearlyProbability = 1.0 - Math.pow(1.0 - monthlyProbability, 10); // 10 months accounting for pregnancy

            totalExpectedChildren += yearlyProbability;
        }

        return totalExpectedChildren;
    }

    /**
     * Calculates the optimal fertility window remaining for family planning.
     * In medieval times, this was less relevant as family planning was uncommon.
     *
     * @param person the person to evaluate (typically female)
     * @return number of years remaining in optimal fertility period
     */
    public int calculateOptimalFertilityYearsRemaining(Person person) {
        if (person == null || person.getGender() != Gender.FEMALE) {
            return 0;
        }

        int currentAge = person.getAge();

        if (currentAge >= FERTILITY_DECLINE_START) {
            return Math.max(0, LATE_FERTILITY_END - currentAge); // Still some years left
        }

        return Math.max(0, FERTILITY_DECLINE_START - currentAge);
    }

    /**
     * Calculates base fertility rate based on age for medieval times.
     *
     * @param age the age to evaluate
     * @return base fertility rate for the age
     */
    private double calculateMedievalAgeFertility(int age) {
        if (age < MIN_FERTILE_AGE_FEMALE || age > MAX_FERTILE_AGE_FEMALE) {
            return 0.0;
        }

        if (age < PEAK_FERTILITY_START) {
            // Young fertility (ages 14-18) - moderate to high
            double factor = (double)(age - MIN_FERTILE_AGE_FEMALE) / (PEAK_FERTILITY_START - MIN_FERTILE_AGE_FEMALE);
            return HIGH_FERTILITY_RATE + (PEAK_FERTILITY_RATE - HIGH_FERTILITY_RATE) * factor;
        } else if (age <= PEAK_FERTILITY_END) {
            // Peak fertility (ages 18-35) - highest rates
            return PEAK_FERTILITY_RATE;
        } else if (age <= FERTILITY_DECLINE_START + 5) {
            // Early decline (ages 35-40) - still high
            double factor = (double)(age - PEAK_FERTILITY_END) / 5.0;
            return PEAK_FERTILITY_RATE - (PEAK_FERTILITY_RATE - NORMAL_FERTILITY_RATE) * factor;
        } else if (age <= LATE_FERTILITY_END) {
            // Late fertility (ages 40-50) - declining but still significant
            double factor = (double)(age - (FERTILITY_DECLINE_START + 5)) / (LATE_FERTILITY_END - (FERTILITY_DECLINE_START + 5));
            return NORMAL_FERTILITY_RATE - (NORMAL_FERTILITY_RATE - LOW_FERTILITY_RATE) * factor;
        } else {
            // Very late fertility (ages 50+) - very low rates
            return VERY_LOW_FERTILITY_RATE;
        }
    }

    /**
     * Calculates health-based modifier for fertility in medieval times.
     * Health had a more severe impact due to poor medical care and nutrition.
     *
     * @param malePartner the male partner
     * @param femalePartner the female partner
     * @return health modifier (typically 0.2 to 1.3)
     */
    private double calculateMedievalHealthModifier(Person malePartner, Person femalePartner) {
        double maleModifier = getMedievalHealthFertilityModifier(malePartner.getHealthStatus());
        double femaleModifier = getMedievalHealthFertilityModifier(femalePartner.getHealthStatus());

        // Take the geometric mean to account for both partners' health
        return Math.sqrt(maleModifier * femaleModifier);
    }

    /**
     * Gets fertility modifier for a specific health status in medieval times.
     *
     * @param healthStatus the health status to evaluate
     * @return fertility modifier
     */
    private double getMedievalHealthFertilityModifier(HealthStatus healthStatus) {
        return switch (healthStatus) {
            case EXCELLENT -> 1.3; // Even better boost than modern times
            case HEALTHY -> 1.0;   // Normal fertility
            case SICK -> 0.4;      // Much more severe reduction than modern
            case CRITICALLY_ILL -> 0.1; // Extremely low fertility
            default -> 1.0;
        };
    }

    /**
     * Calculates male age factor for fertility in medieval times.
     * Male fertility declined earlier due to harsh living conditions.
     *
     * @param maleAge the male's age
     * @return age factor (0.5 to 1.0)
     */
    private double calculateMaleAgeFactor(int maleAge) {
        if (maleAge < MIN_FERTILE_AGE_MALE) {
            return 0.5; // Very young, reduced fertility
        } else if (maleAge <= 30) {
            return 1.0; // Peak male fertility
        } else if (maleAge <= 45) {
            // Gradual decline
            double factor = (double)(maleAge - 30) / 15.0;
            return 1.0 - factor * 0.3; // 30% reduction by age 45
        } else if (maleAge <= MAX_FERTILE_AGE_MALE) {
            // Significant decline
            double factor = (double)(maleAge - 45) / (MAX_FERTILE_AGE_MALE - 45);
            return 0.7 - factor * 0.4; // Down to 30% by max age
        } else {
            return 0.0; // Beyond fertile age
        }
    }

    /**
     * Applies seasonal fertility adjustments for medieval times.
     * Fertility was often lower in winter due to cold and food scarcity.
     *
     * @param baseFertility the base fertility rate
     * @param month the month (1-12)
     * @return seasonally adjusted fertility rate
     */
    public double applySeasonalAdjustment(double baseFertility, int month) {
        // Winter months (Dec, Jan, Feb) had lower fertility
        if (month == 12 || month == 1 || month == 2) {
            return baseFertility * WINTER_FERTILITY_REDUCTION;
        }

        // Spring and summer (Mar-Aug) were peak times
        if (month >= 3 && month <= 8) {
            return baseFertility * 1.1; // 10% boost
        }

        // Fall (Sep-Nov) was normal
        return baseFertility;
    }
}
