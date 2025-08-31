package com.littlepeople.simulation.population;

import com.littlepeople.core.model.Gender;

import java.util.Map;
import java.util.HashMap;
import java.util.Objects;

/**
 * Defines demographic distribution patterns for population generation.
 *
 * <p>This class encapsulates age and gender distribution settings used to
 * create realistic population demographics. It provides configurable parameters
 * for age ranges, gender ratios, and statistical distributions that mirror
 * real-world demographic patterns.
 *
 * @author LittlePeople Development Team
 * @version 1.0
 * @since 1.0
 */
public class DemographicDistribution {

    private final int minAge;
    private final int maxAge;
    private final double maleRatio;
    private final double femaleRatio;
    private final Map<AgeGroup, Double> ageDistribution;
    private final int averageAge;
    private final double ageStandardDeviation;

    private DemographicDistribution(Builder builder) {
        this.minAge = builder.minAge;
        this.maxAge = builder.maxAge;
        this.maleRatio = builder.maleRatio;
        this.femaleRatio = builder.femaleRatio;
        this.ageDistribution = new HashMap<>(builder.ageDistribution);
        this.averageAge = builder.averageAge;
        this.ageStandardDeviation = builder.ageStandardDeviation;
    }

    /**
     * Gets the minimum age for generated inhabitants.
     *
     * @return the minimum age
     */
    public int getMinAge() {
        return minAge;
    }

    /**
     * Gets the maximum age for generated inhabitants.
     *
     * @return the maximum age
     */
    public int getMaxAge() {
        return maxAge;
    }

    /**
     * Gets the ratio of male inhabitants (0.0 to 1.0).
     *
     * @return the male ratio
     */
    public double getMaleRatio() {
        return maleRatio;
    }

    /**
     * Gets the ratio of female inhabitants (0.0 to 1.0).
     *
     * @return the female ratio
     */
    public double getFemaleRatio() {
        return femaleRatio;
    }

    /**
     * Gets the age distribution by age groups.
     *
     * @return unmodifiable map of age group distributions
     */
    public Map<AgeGroup, Double> getAgeDistribution() {
        return Map.copyOf(ageDistribution);
    }

    /**
     * Gets the average age for the population.
     *
     * @return the average age
     */
    public int getAverageAge() {
        return averageAge;
    }

    /**
     * Gets the standard deviation for age distribution.
     *
     * @return the age standard deviation
     */
    public double getAgeStandardDeviation() {
        return ageStandardDeviation;
    }

    /**
     * Gets the distribution weight for a specific age group.
     *
     * @param ageGroup the age group to check
     * @return the distribution weight (0.0 to 1.0)
     */
    public double getAgeGroupWeight(AgeGroup ageGroup) {
        return ageDistribution.getOrDefault(ageGroup, 0.0);
    }

    /**
     * Determines the appropriate age group for a given age.
     *
     * @param age the age to categorize
     * @return the corresponding age group
     */
    public AgeGroup getAgeGroup(int age) {
        if (age < 0) throw new IllegalArgumentException("Age cannot be negative");
        if (age <= 2) return AgeGroup.INFANT;
        if (age <= 12) return AgeGroup.CHILD;
        if (age <= 17) return AgeGroup.ADOLESCENT;
        if (age <= 25) return AgeGroup.YOUNG_ADULT;
        if (age <= 54) return AgeGroup.ADULT;
        if (age <= 74) return AgeGroup.SENIOR;
        return AgeGroup.ELDER;
    }

    /**
     * Validates the demographic distribution for consistency.
     *
     * @throws IllegalArgumentException if the distribution is invalid
     */
    public void validate() {
        if (minAge < 0) {
            throw new IllegalArgumentException("Minimum age cannot be negative");
        }

        if (maxAge <= minAge) {
            throw new IllegalArgumentException("Maximum age must be greater than minimum age");
        }

        if (maleRatio < 0.0 || maleRatio > 1.0) {
            throw new IllegalArgumentException("Male ratio must be between 0.0 and 1.0");
        }

        if (femaleRatio < 0.0 || femaleRatio > 1.0) {
            throw new IllegalArgumentException("Female ratio must be between 0.0 and 1.0");
        }

        if (Math.abs(maleRatio + femaleRatio - 1.0) > 0.001) {
            throw new IllegalArgumentException("Male and female ratios must sum to 1.0");
        }

        if (averageAge < minAge || averageAge > maxAge) {
            throw new IllegalArgumentException("Average age must be within min/max age range");
        }

        if (ageStandardDeviation <= 0) {
            throw new IllegalArgumentException("Age standard deviation must be positive");
        }

        // Validate age distribution weights sum to 1.0
        double totalWeight = ageDistribution.values().stream()
                .mapToDouble(Double::doubleValue)
                .sum();

        if (Math.abs(totalWeight - 1.0) > 0.001) {
            throw new IllegalArgumentException("Age distribution weights must sum to 1.0");
        }
    }

    /**
     * Creates a default demographic distribution with realistic values.
     *
     * @return a default demographic distribution
     */
    public static DemographicDistribution createDefault() {
        return builder()
                .minAge(0)
                .maxAge(100)
                .maleRatio(0.49)
                .femaleRatio(0.51)
                .averageAge(35)
                .ageStandardDeviation(20.0)
                .ageGroupWeight(AgeGroup.INFANT, 0.05)
                .ageGroupWeight(AgeGroup.CHILD, 0.20)
                .ageGroupWeight(AgeGroup.ADOLESCENT, 0.30)
                .ageGroupWeight(AgeGroup.YOUNG_ADULT, 0.20)
                .ageGroupWeight(AgeGroup.ADULT, 0.15)
                .ageGroupWeight(AgeGroup.SENIOR, 0.10)
                .ageGroupWeight(AgeGroup.ELDER, 0.00)
                .build();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;

        DemographicDistribution that = (DemographicDistribution) obj;
        return minAge == that.minAge &&
               maxAge == that.maxAge &&
               Double.compare(that.maleRatio, maleRatio) == 0 &&
               Double.compare(that.femaleRatio, femaleRatio) == 0 &&
               averageAge == that.averageAge &&
               Double.compare(that.ageStandardDeviation, ageStandardDeviation) == 0 &&
               Objects.equals(ageDistribution, that.ageDistribution);
    }

    @Override
    public int hashCode() {
        return Objects.hash(minAge, maxAge, maleRatio, femaleRatio, ageDistribution,
                           averageAge, ageStandardDeviation);
    }

    @Override
    public String toString() {
        return "DemographicDistribution{" +
               "minAge=" + minAge +
               ", maxAge=" + maxAge +
               ", maleRatio=" + maleRatio +
               ", femaleRatio=" + femaleRatio +
               ", averageAge=" + averageAge +
               ", ageStdDev=" + ageStandardDeviation +
               ", ageDistribution=" + ageDistribution +
               '}';
    }

    /**
     * Creates a new builder for DemographicDistribution.
     *
     * @return a new builder instance
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * Builder class for creating DemographicDistribution instances.
     */
    public static class Builder {
        private int minAge = 0;
        private int maxAge = 100;
        private double maleRatio = 0.49;
        private double femaleRatio = 0.51;
        private Map<AgeGroup, Double> ageDistribution = new HashMap<>();
        private int averageAge = 35;
        private double ageStandardDeviation = 20.0;

        private Builder() {
            // Initialize with default age distribution
            ageDistribution.put(AgeGroup.INFANT, 0.05);
            ageDistribution.put(AgeGroup.CHILD, 0.15);
            ageDistribution.put(AgeGroup.ADOLESCENT, 0.12);
            ageDistribution.put(AgeGroup.YOUNG_ADULT, 0.25);
            ageDistribution.put(AgeGroup.ADULT, 0.20);
            ageDistribution.put(AgeGroup.SENIOR, 0.20);
        }
        public Builder minAge(int age) {
            this.minAge = age;
            return this;
        }

        public Builder maxAge(int age) {
            this.maxAge = age;
            return this;
        }

        public Builder maleRatio(double ratio) {
            this.maleRatio = ratio;
            this.femaleRatio = 1.0 - ratio;
            return this;
        }

        public Builder femaleRatio(double ratio) {
            this.femaleRatio = ratio;
            this.maleRatio = 1.0 - ratio;
            return this;
        }

        public Builder genderRatios(double maleRatio, double femaleRatio) {
            this.maleRatio = maleRatio;
            this.femaleRatio = femaleRatio;
            return this;
        }

        public Builder averageAge(int age) {
            this.averageAge = age;
            return this;
        }

        public Builder ageStandardDeviation(double deviation) {
            this.ageStandardDeviation = deviation;
            return this;
        }

        public Builder ageGroupWeight(AgeGroup ageGroup, double weight) {
            this.ageDistribution.put(ageGroup, weight);
            return this;
        }

        public Builder ageDistribution(Map<AgeGroup, Double> distribution) {
            this.ageDistribution = new HashMap<>(distribution);
            return this;
        }

        public DemographicDistribution build() {
            DemographicDistribution distribution = new DemographicDistribution(this);
            distribution.validate();
            return distribution;
        }
    }

    /**
     * Enumeration of age groups used for demographic distribution.
     */
    public enum AgeGroup {
        INFANT(0, 2, "Infant"),
        CHILD(3, 12, "Child"),
        ADOLESCENT(13, 17, "Teenager"),
        YOUNG_ADULT(18, 25, "Young Adult"),
        ADULT(26, 54, "Adult"),
        SENIOR(55, 74, "Senior"),
        ELDER(75, 120, "Elder");

        private final int minAge;
        private final int maxAge;
        private final String displayName;

        AgeGroup(int minAge, int maxAge, String displayName) {
            this.minAge = minAge;
            this.maxAge = maxAge;
            this.displayName = displayName;
        }

        public int getMinAge() { return minAge; }
        public int getMaxAge() { return maxAge; }
        public String getDisplayName() { return displayName; }

        public boolean contains(int age) {
            return age >= minAge && age <= maxAge;
        }
    }
}
