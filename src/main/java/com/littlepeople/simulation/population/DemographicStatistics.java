package com.littlepeople.simulation.population;

import com.littlepeople.core.model.Gender;
import com.littlepeople.simulation.population.DemographicDistribution.AgeGroup;

import java.util.Map;
import java.util.HashMap;
import java.util.Objects;

/**
 * Comprehensive demographic statistics for the current population.
 *
 * <p>This class provides detailed statistical analysis of the population including
 * age distributions, gender ratios, family structure statistics, and population
 * growth trends. It supports both raw counts and percentage-based analysis.
 *
 * @author LittlePeople Development Team
 * @version 1.0
 * @since 1.0
 */
public class DemographicStatistics {

    private final int totalPopulation;
    private final int livingPopulation;
    private final int deceasedPopulation;
    private final Map<Gender, Integer> genderCounts;
    private final Map<AgeGroup, Integer> ageGroupCounts;
    private final double averageAge;
    private final int medianAge;
    private final int minAge;
    private final int maxAge;
    private final int totalFamilies;
    private final int totalPartnerships;
    private final double averageChildrenPerFamily;
    private final int maxChildrenInFamily;
    private final long statisticsTimestamp;

    private DemographicStatistics(Builder builder) {
        this.totalPopulation = builder.totalPopulation;
        this.livingPopulation = builder.livingPopulation;
        this.deceasedPopulation = builder.deceasedPopulation;
        this.genderCounts = Map.copyOf(builder.genderCounts);
        this.ageGroupCounts = Map.copyOf(builder.ageGroupCounts);
        this.averageAge = builder.averageAge;
        this.medianAge = builder.medianAge;
        this.minAge = builder.minAge;
        this.maxAge = builder.maxAge;
        this.totalFamilies = builder.totalFamilies;
        this.totalPartnerships = builder.totalPartnerships;
        this.averageChildrenPerFamily = builder.averageChildrenPerFamily;
        this.statisticsTimestamp = System.currentTimeMillis();
        this.maxChildrenInFamily = builder.maxChildrenInFamily;
    }

    /**
     * Gets the total population count (living + deceased).
     *
     * @return the total population count
     */
    public int getTotalPopulation() {
        return totalPopulation;
    }

    /**
     * Gets the living population count.
     *
     * @return the living population count
     */
    public int getLivingPopulation() {
        return livingPopulation;
    }

    /**
     * Gets the deceased population count.
     *
     * @return the deceased population count
     */
    public int getDeceasedPopulation() {
        return deceasedPopulation;
    }

    /**
     * Gets the population count by gender.
     *
     * @return unmodifiable map of gender to population count
     */
    public Map<Gender, Integer> getGenderCounts() {
        return genderCounts;
    }

    /**
     * Gets the population count by age group.
     *
     * @return unmodifiable map of age group to population count
     */
    public Map<AgeGroup, Integer> getAgeGroupCounts() {
        return ageGroupCounts;
    }

    /**
     * Gets the average age of the living population.
     *
     * @return the average age
     */
    public double getAverageAge() {
        return averageAge;
    }

    /**
     * Gets the median age of the living population.
     *
     * @return the median age
     */
    public int getMedianAge() {
        return medianAge;
    }

    /**
     * Gets the minimum age in the living population.
     *
     * @return the minimum age
     */
    public int getMinAge() {
        return minAge;
    }

    /**
     * Gets the maximum age in the living population.
     *
     * @return the maximum age
     */
    public int getMaxAge() {
        return maxAge;
    }

    /**
     * Gets the total number of families.
     *
     * @return the total family count
     */
    public int getTotalFamilies() {
        return totalFamilies;
    }

    /**
     * Gets the total number of partnerships.
     *
     * @return the total partnership count
     */
    public int getTotalPartnerships() {
        return totalPartnerships;
    }

    /**
     * Gets the average number of children per family.
     *
     * @return the average children per family
     */
    public double getAverageChildrenPerFamily() {
        return averageChildrenPerFamily;
    }

    /**
     * Gets the timestamp when statistics were calculated.
     *
     * @return statistics timestamp in milliseconds
     */
    public long getStatisticsTimestamp() {
        return statisticsTimestamp;
    }

    /**
     * Gets the population count for a specific gender.
     *
     * @param gender the gender to query
     * @return the population count for the gender
     */
    public int getGenderCount(Gender gender) {
        return genderCounts.getOrDefault(gender, 0);
    }

    /**
     * Gets the population count for a specific age group.
     *
     * @param ageGroup the age group to query
     * @return the population count for the age group
     */
    public int getAgeGroupCount(AgeGroup ageGroup) {
        return ageGroupCounts.getOrDefault(ageGroup, 0);
    }

    /**
     * Calculates the gender ratio (male to female).
     *
     * @return the male to female ratio, or 0.0 if no females
     */
    public double getGenderRatio() {
        int maleCount = getGenderCount(Gender.MALE);
        int femaleCount = getGenderCount(Gender.FEMALE);
        return femaleCount > 0 ? (double) maleCount / femaleCount : 0.0;
    }

    /**
     * Calculates the percentage of population for a specific gender.
     *
     * @param gender the gender to calculate percentage for
     * @return the percentage (0.0 to 100.0)
     */
    public double getGenderPercentage(Gender gender) {
        if (livingPopulation == 0) return 0.0;
        return (double) getGenderCount(gender) / livingPopulation * 100.0;
    }

    /**
     * Calculates the percentage of population for a specific age group.
     *
     * @param ageGroup the age group to calculate percentage for
     * @return the percentage (0.0 to 100.0)
     */
    public double getAgeGroupPercentage(AgeGroup ageGroup) {
        if (livingPopulation == 0) return 0.0;
        return (double) getAgeGroupCount(ageGroup) / livingPopulation * 100.0;
    }

    /**
     * Calculates the mortality rate (deceased / total population).
     *
     * @return the mortality rate (0.0 to 1.0)
     */
    public double getMortalityRate() {
        if (totalPopulation == 0) return 0.0;
        return (double) deceasedPopulation / totalPopulation;
    }

    /**
     * Calculates the dependency ratio (children + elderly / working age).
     *
     * @return the dependency ratio
     */
    public double getDependencyRatio() {
        int workingAge = getAgeGroupCount(AgeGroup.YOUNG_ADULT) +
                        getAgeGroupCount(AgeGroup.ADULT) +
                        getAgeGroupCount(AgeGroup.SENIOR) ;
        if (workingAge == 0) return 0.0;

        int dependents = getAgeGroupCount(AgeGroup.CHILD) +
                        getAgeGroupCount(AgeGroup.ADOLESCENT) +
                        getAgeGroupCount(AgeGroup.ELDER);

        return (double) dependents / workingAge;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;

        DemographicStatistics that = (DemographicStatistics) obj;
        return totalPopulation == that.totalPopulation &&
               livingPopulation == that.livingPopulation &&
               deceasedPopulation == that.deceasedPopulation &&
               Double.compare(that.averageAge, averageAge) == 0 &&
               medianAge == that.medianAge &&
               minAge == that.minAge &&
               maxAge == that.maxAge &&
               totalFamilies == that.totalFamilies &&
               totalPartnerships == that.totalPartnerships &&
               Double.compare(that.averageChildrenPerFamily, averageChildrenPerFamily) == 0 &&
               Objects.equals(genderCounts, that.genderCounts) &&
               Objects.equals(ageGroupCounts, that.ageGroupCounts);
    }

    @Override
    public int hashCode() {
        return Objects.hash(totalPopulation, livingPopulation, deceasedPopulation,
                           genderCounts, ageGroupCounts, averageAge, medianAge,
                           minAge, maxAge, totalFamilies, totalPartnerships,
                           averageChildrenPerFamily);
    }

    @Override
    public String toString() {
        return "DemographicStatistics{" +
                "totalPopulation=" + totalPopulation +
                ", livingPopulation=" + livingPopulation +
                ", deceasedPopulation=" + deceasedPopulation +
                ", genderCounts=" + genderCounts +
                ", ageGroupCounts=" + ageGroupCounts +
                ", averageAge=" + averageAge +
                ", medianAge=" + medianAge +
                ", maxAge=" + maxAge +
                ", totalFamilies=" + totalFamilies +
                ", totalPartnerships=" + totalPartnerships +
                ", averageChildrenPerFamily=" + averageChildrenPerFamily +
                ", maxChildrenInFamily=" + maxChildrenInFamily +
                '}';
    }

    /**
     * Creates a new builder for DemographicStatistics.
     *
     * @return a new builder instance
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * Builder class for creating DemographicStatistics instances.
     */
    public static class Builder {
        private int totalPopulation = 0;
        private int livingPopulation = 0;
        private int deceasedPopulation = 0;
        private Map<Gender, Integer> genderCounts = new HashMap<>();
        private Map<AgeGroup, Integer> ageGroupCounts = new HashMap<>();
        private double averageAge = 0.0;
        private int medianAge = 0;
        private int minAge = 0;
        private int maxAge = 0;
        private int totalFamilies = 0;
        private int totalPartnerships = 0;
        private double averageChildrenPerFamily = 0.0;
        private int maxChildrenInFamily = 0;

        private Builder() {
            // Initialize gender counts
            for (Gender gender : Gender.values()) {
                genderCounts.put(gender, 0);
            }

            // Initialize age group counts
            for (AgeGroup ageGroup : AgeGroup.values()) {
                ageGroupCounts.put(ageGroup, 0);
            }
        }

        public Builder totalPopulation(int count) {
            this.totalPopulation = count;
            return this;
        }

        public Builder livingPopulation(int count) {
            this.livingPopulation = count;
            return this;
        }

        public Builder deceasedPopulation(int count) {
            this.deceasedPopulation = count;
            return this;
        }

        public Builder genderCount(Gender gender, int count) {
            this.genderCounts.put(gender, count);
            return this;
        }

        public Builder ageGroupCount(AgeGroup ageGroup, int count) {
            this.ageGroupCounts.put(ageGroup, count);
            return this;
        }

        public Builder averageAge(double age) {
            this.averageAge = age;
            return this;
        }

        public Builder medianAge(int age) {
            this.medianAge = age;
            return this;
        }

        public Builder minAge(int age) {
            this.minAge = age;
            return this;
        }

        public Builder maxAge(int age) {
            this.maxAge = age;
            return this;
        }

        public Builder totalFamilies(int count) {
            this.totalFamilies = count;
            return this;
        }

        public Builder totalPartnerships(int count) {
            this.totalPartnerships = count;
            return this;
        }

        public Builder averageChildrenPerFamily(double average) {
            this.averageChildrenPerFamily = average;
            return this;
        }

        public Builder maxChildrenInFamily(int max) {
            this.maxChildrenInFamily = max;
            return this;
        }
        public DemographicStatistics build() {
            return new DemographicStatistics(this);
        }
    }
}
