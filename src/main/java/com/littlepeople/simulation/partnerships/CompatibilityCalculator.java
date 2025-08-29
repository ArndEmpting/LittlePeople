package com.littlepeople.simulation.partnerships;

import com.littlepeople.core.model.Person;
import com.littlepeople.core.model.PersonalityTrait;
import com.littlepeople.core.model.Gender;
import com.littlepeople.core.model.LifeStage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Period;
import java.util.Map;

/**
 * Calculates compatibility scores between potential romantic partners.
 *
 * <p>This class implements sophisticated compatibility algorithms that consider
 * multiple factors including personality traits, age appropriateness, life stage
 * compatibility, and social preferences. The compatibility score ranges from
 * 0.0 (completely incompatible) to 1.0 (perfect match).</p>
 *
 * <h2>Compatibility Factors</h2>
 * <ul>
 *   <li><strong>Age Compatibility (30%):</strong> Preference for similar ages with
 *       acceptable ranges based on life stages</li>
 *   <li><strong>Personality Compatibility (40%):</strong> Complementary and similar
 *       personality traits using established psychological models</li>
 *   <li><strong>Life Stage Compatibility (20%):</strong> Alignment of life goals
 *       and developmental phases</li>
 *   <li><strong>Social Factors (10%):</strong> Wealth status, health status,
 *       and family background considerations</li>
 * </ul>
 *
 * <h2>Biological and Social Constraints</h2>
 * <ul>
 *   <li>Same-gender partnerships are not supported in this version</li>
 *   <li>Close family relationships are prohibited (parents, siblings, children)</li>
 *   <li>Significant age gaps are discouraged (>15 years difference)</li>
 *   <li>Life stage mismatches reduce compatibility (child/adult pairings impossible)</li>
 * </ul>
 *
 * @since 1.0.0
 * @version 1.0.0
 */
public class CompatibilityCalculator {

    private static final Logger logger = LoggerFactory.getLogger(CompatibilityCalculator.class);

    // Compatibility weights
    private static final double AGE_WEIGHT = 0.30;
    private static final double PERSONALITY_WEIGHT = 0.40;
    private static final double LIFE_STAGE_WEIGHT = 0.20;
    private static final double SOCIAL_WEIGHT = 0.10;

    // Age compatibility parameters
    private static final int IDEAL_AGE_DIFFERENCE = 5; // years
    private static final int MAX_ACCEPTABLE_AGE_DIFFERENCE = 15; // years

    // Minimum ages for partnerships by life stage
    private static final int MIN_PARTNERSHIP_AGE = 16;
    private static final int IDEAL_PARTNERSHIP_AGE = 18;

    /**
     * Calculates the overall compatibility score between two people.
     *
     * <p>This method combines multiple compatibility factors using weighted
     * averages to produce a comprehensive compatibility score. The algorithm
     * first checks for absolute incompatibilities (same gender, family relations,
     * inappropriate ages) before calculating detailed compatibility metrics.</p>
     *
     * @param person1 the first person to evaluate
     * @param person2 the second person to evaluate
     * @return compatibility score from 0.0 (incompatible) to 1.0 (perfect match)
     * @throws IllegalArgumentException if either person is null
     */
    public double calculateCompatibility(Person person1, Person person2) {
        if (person1 == null || person2 == null) {
            throw new IllegalArgumentException("Both persons must be non-null for compatibility calculation");
        }

        // Check for absolute incompatibilities
        if (hasAbsoluteIncompatibility(person1, person2)) {
            logger.debug("Absolute incompatibility found between {} and {}",
                        person1.getId(), person2.getId());
            return 0.0;
        }

        // Calculate individual compatibility components
        double ageCompatibility = calculateAgeCompatibility(person1, person2);
        double personalityCompatibility = calculatePersonalityCompatibility(person1, person2);
        double lifeStageCompatibility = calculateLifeStageCompatibility(person1, person2);
        double socialCompatibility = calculateSocialCompatibility(person1, person2);

        // Weighted average of all compatibility factors
        double overallCompatibility =
            (ageCompatibility * AGE_WEIGHT) +
            (personalityCompatibility * PERSONALITY_WEIGHT) +
            (lifeStageCompatibility * LIFE_STAGE_WEIGHT) +
            (socialCompatibility * SOCIAL_WEIGHT);

        logger.debug("Compatibility between {} and {}: Age={}, Personality={}, LifeStage={}, Social={}, Overall={}",
                    person1.getId(), person2.getId(),
                    ageCompatibility, personalityCompatibility, lifeStageCompatibility,
                    socialCompatibility, overallCompatibility);

        return Math.max(0.0, Math.min(1.0, overallCompatibility));
    }

    /**
     * Checks for absolute incompatibilities that make partnership impossible.
     *
     * @param person1 the first person
     * @param person2 the second person
     * @return true if the partnership is absolutely impossible
     */
    private boolean hasAbsoluteIncompatibility(Person person1, Person person2) {
        // Same person
        if (person1.getId().equals(person2.getId())) {
            return true;
        }

        // Same gender (heterosexual partnerships only in this version)
        if (person1.getGender() == person2.getGender()) {
            return true;
        }

        // Family relationships (parents, children, siblings)
        if (areRelated(person1, person2)) {
            return true;
        }

        // Age appropriateness (no child-adult partnerships)
        if (!areAgeAppropriate(person1, person2)) {
            return true;
        }

        // Already partners
        if (person1.getPartner() != null && person1.getPartner().getId().equals(person2.getId())) {
            return true;
        }

        return false;
    }

    /**
     * Calculates age-based compatibility score.
     *
     * @param person1 the first person
     * @param person2 the second person
     * @return age compatibility score (0.0 to 1.0)
     */
    private double calculateAgeCompatibility(Person person1, Person person2) {
        int age1 = person1.getAge();
        int age2 = person2.getAge();
        int ageDifference = Math.abs(age1 - age2);

        // Perfect score for ideal age difference
        if (ageDifference <= IDEAL_AGE_DIFFERENCE) {
            return 1.0;
        }

        // Declining score up to maximum acceptable difference
        if (ageDifference <= MAX_ACCEPTABLE_AGE_DIFFERENCE) {
            return 1.0 - ((double)(ageDifference - IDEAL_AGE_DIFFERENCE) /
                         (MAX_ACCEPTABLE_AGE_DIFFERENCE - IDEAL_AGE_DIFFERENCE)) * 0.7;
        }

        // Very low compatibility for large age gaps
        return 0.1;
    }

    /**
     * Calculates personality-based compatibility score.
     *
     * @param person1 the first person
     * @param person2 the second person
     * @return personality compatibility score (0.0 to 1.0)
     */
    private double calculatePersonalityCompatibility(Person person1, Person person2) {
        Map<PersonalityTrait, Integer> traits1 = person1.getPersonalityTraits();
        Map<PersonalityTrait, Integer> traits2 = person2.getPersonalityTraits();

        double totalCompatibility = 0.0;
        int traitCount = 0;

        for (PersonalityTrait trait : PersonalityTrait.values()) {
            Integer value1 = traits1.get(trait);
            Integer value2 = traits2.get(trait);

            if (value1 != null && value2 != null) {
                double traitCompatibility = calculateTraitCompatibility(trait, value1, value2);
                totalCompatibility += traitCompatibility;
                traitCount++;
            }
        }

        return traitCount > 0 ? totalCompatibility / traitCount : 0.5;
    }

    /**
     * Calculates compatibility for a specific personality trait.
     *
     * @param trait the personality trait to evaluate
     * @param value1 first person's trait value (1-10)
     * @param value2 second person's trait value (1-10)
     * @return trait compatibility score (0.0 to 1.0)
     */
    private double calculateTraitCompatibility(PersonalityTrait trait, int value1, int value2) {
        int difference = Math.abs(value1 - value2);

        switch (trait) {
            case EXTROVERSION:
                // Moderate difference preferred (complementary personalities)
                return difference >= 2 && difference <= 4 ? 1.0 : 0.5;

            case AGREEABLENESS:
                // Similar levels preferred (both should be agreeable)
                return difference <= 2 ? 1.0 : Math.max(0.2, 1.0 - (difference * 0.2));

            case CONSCIENTIOUSNESS:
                // Similar levels preferred (both responsible or both spontaneous)
                return difference <= 3 ? 1.0 : Math.max(0.3, 1.0 - (difference * 0.15));

            case NEUROTICISM:
                // Lower levels preferred, but some complementarity acceptable
                double avgNeuroticism = (value1 + value2) / 2.0;
                double stabilityBonus = Math.max(0.0, (10.0 - avgNeuroticism) / 10.0);
                double compatibilityPenalty = difference > 4 ? 0.3 : 0.0;
                return Math.max(0.1, stabilityBonus - compatibilityPenalty);

            case OPENNESS:
                // Moderate similarity preferred
                return difference <= 3 ? 1.0 : Math.max(0.4, 1.0 - (difference * 0.1));

            default:
                return 0.5; // Default neutral compatibility
        }
    }

    /**
     * Calculates life stage compatibility score.
     *
     * @param person1 the first person
     * @param person2 the second person
     * @return life stage compatibility score (0.0 to 1.0)
     */
    private double calculateLifeStageCompatibility(Person person1, Person person2) {
        LifeStage stage1 = person1.getLifeStage();
        LifeStage stage2 = person2.getLifeStage();

        if (stage1 == stage2) {
            return 1.0; // Same life stage is ideal
        }

        // Define compatible life stage combinations
        if ((stage1 == LifeStage.YOUNG_ADULT && stage2 == LifeStage.ADULT) ||
            (stage1 == LifeStage.ADULT && stage2 == LifeStage.YOUNG_ADULT)) {
            return 0.8; // Good compatibility
        }

        if ((stage1 == LifeStage.ADULT && stage2 == LifeStage.ELDER) ||
            (stage1 == LifeStage.ELDER && stage2 == LifeStage.ADULT)) {
            return 0.7; // Acceptable compatibility
        }

        if ((stage1 == LifeStage.YOUNG_ADULT && stage2 == LifeStage.ELDER) ||
            (stage1 == LifeStage.ELDER && stage2 == LifeStage.YOUNG_ADULT)) {
            return 0.4; // Lower compatibility due to different life priorities
        }

        // Incompatible combinations (child with any adult stage, etc.)
        return 0.0;
    }

    /**
     * Calculates social compatibility score based on wealth, health, and background.
     *
     * @param person1 the first person
     * @param person2 the second person
     * @return social compatibility score (0.0 to 1.0)
     */
    private double calculateSocialCompatibility(Person person1, Person person2) {
        double wealthCompatibility = calculateWealthCompatibility(person1, person2);
        double healthCompatibility = calculateHealthCompatibility(person1, person2);

        // Equal weighting for now, could be made configurable
        return (wealthCompatibility + healthCompatibility) / 2.0;
    }

    /**
     * Calculates wealth status compatibility.
     *
     * @param person1 the first person
     * @param person2 the second person
     * @return wealth compatibility score (0.0 to 1.0)
     */
    private double calculateWealthCompatibility(Person person1, Person person2) {
        // In reality, people often partner within similar socioeconomic levels
        // but some cross-class relationships do occur
        if (person1.getWealthStatus() == person2.getWealthStatus()) {
            return 1.0;
        }

        // Adjacent wealth levels are reasonably compatible
        int wealthDiff = Math.abs(person1.getWealthStatus().ordinal() - person2.getWealthStatus().ordinal());
        return Math.max(0.3, 1.0 - (wealthDiff * 0.2));
    }

    /**
     * Calculates health status compatibility.
     *
     * @param person1 the first person
     * @param person2 the second person
     * @return health compatibility score (0.0 to 1.0)
     */
    private double calculateHealthCompatibility(Person person1, Person person2) {
        // Health status affects partnership viability
        if (person1.getHealthStatus() == person2.getHealthStatus()) {
            return 1.0;
        }

        // Healthy individuals may be less likely to partner with unhealthy ones
        int healthDiff = Math.abs(person1.getHealthStatus().ordinal() - person2.getHealthStatus().ordinal());
        return Math.max(0.4, 1.0 - (healthDiff * 0.15));
    }

    /**
     * Checks if two people are related (family members).
     *
     * @param person1 the first person
     * @param person2 the second person
     * @return true if they are family members
     */
    private boolean areRelated(Person person1, Person person2) {
        // Check if person2 is person1's parent
        if (person1.getParents().contains(person2)) {
            return true;
        }

        // Check if person2 is person1's child
        if (person1.getChildren().contains(person2)) {
            return true;
        }

        // Check if they share any parents (siblings)
        for (Person parent : person1.getParents()) {
            if (person2.getParents().contains(parent)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Checks if two people are age-appropriate for a partnership.
     *
     * @param person1 the first person
     * @param person2 the second person
     * @return true if both are old enough and age-appropriate
     */
    private boolean areAgeAppropriate(Person person1, Person person2) {
        int age1 = person1.getAge();
        int age2 = person2.getAge();

        // Both must be at least minimum partnership age
        if (age1 < MIN_PARTNERSHIP_AGE || age2 < MIN_PARTNERSHIP_AGE) {
            return false;
        }

        // No child-adult partnerships
        LifeStage stage1 = person1.getLifeStage();
        LifeStage stage2 = person2.getLifeStage();

        if (stage1 == LifeStage.CHILD || stage2 == LifeStage.CHILD) {
            return false;
        }

        if (stage1 == LifeStage.ADOLESCENT || stage2 == LifeStage.ADOLESCENT) {
            // Adolescents can only partner with other adolescents or young adults
            return (stage1 == LifeStage.ADOLESCENT || stage1 == LifeStage.YOUNG_ADULT) &&
                   (stage2 == LifeStage.ADOLESCENT || stage2 == LifeStage.YOUNG_ADULT);
        }

        return true;
    }
}
