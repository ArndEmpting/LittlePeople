package com.littlepeople.simulation.population;

import com.littlepeople.core.model.Gender;
import com.littlepeople.simulation.population.DemographicDistribution.AgeGroup;

import java.util.Objects;
import java.util.Optional;

/**
 * Search criteria for querying the population based on various demographic attributes.
 *
 * <p>This class provides a flexible way to search for inhabitants based on age ranges,
 * gender, family status, and other demographic characteristics. It supports both
 * exact matches and range-based queries.
 *
 * @author LittlePeople Development Team
 * @version 1.0
 * @since 1.0
 */
public class PopulationSearchCriteria {

    private final Integer minAge;
    private final Integer maxAge;
    private final Gender gender;
    private final AgeGroup ageGroup;
    private final Boolean hasPartner;
    private final Boolean hasChildren;
    private final Integer minChildren;
    private final Integer maxChildren;
    private final Boolean isAlive;
    private final String namePattern;

    private PopulationSearchCriteria(Builder builder) {
        this.minAge = builder.minAge;
        this.maxAge = builder.maxAge;
        this.gender = builder.gender;
        this.ageGroup = builder.ageGroup;
        this.hasPartner = builder.hasPartner;
        this.hasChildren = builder.hasChildren;
        this.minChildren = builder.minChildren;
        this.maxChildren = builder.maxChildren;
        this.isAlive = builder.isAlive;
        this.namePattern = builder.namePattern;
    }

    /**
     * Gets the minimum age criteria.
     *
     * @return the minimum age, or empty if not specified
     */
    public Optional<Integer> getMinAge() {
        return Optional.ofNullable(minAge);
    }

    /**
     * Gets the maximum age criteria.
     *
     * @return the maximum age, or empty if not specified
     */
    public Optional<Integer> getMaxAge() {
        return Optional.ofNullable(maxAge);
    }

    /**
     * Gets the gender criteria.
     *
     * @return the gender, or empty if not specified
     */
    public Optional<Gender> getGender() {
        return Optional.ofNullable(gender);
    }

    /**
     * Gets the age group criteria.
     *
     * @return the age group, or empty if not specified
     */
    public Optional<AgeGroup> getAgeGroup() {
        return Optional.ofNullable(ageGroup);
    }

    /**
     * Gets the partner status criteria.
     *
     * @return the partner status, or empty if not specified
     */
    public Optional<Boolean> getHasPartner() {
        return Optional.ofNullable(hasPartner);
    }

    /**
     * Gets the children status criteria.
     *
     * @return the children status, or empty if not specified
     */
    public Optional<Boolean> getHasChildren() {
        return Optional.ofNullable(hasChildren);
    }

    /**
     * Gets the minimum children count criteria.
     *
     * @return the minimum children count, or empty if not specified
     */
    public Optional<Integer> getMinChildren() {
        return Optional.ofNullable(minChildren);
    }

    /**
     * Gets the maximum children count criteria.
     *
     * @return the maximum children count, or empty if not specified
     */
    public Optional<Integer> getMaxChildren() {
        return Optional.ofNullable(maxChildren);
    }

    /**
     * Gets the alive status criteria.
     *
     * @return the alive status, or empty if not specified
     */
    public Optional<Boolean> getIsAlive() {
        return Optional.ofNullable(isAlive);
    }

    /**
     * Gets the name pattern criteria.
     *
     * @return the name pattern, or empty if not specified
     */
    public Optional<String> getNamePattern() {
        return Optional.ofNullable(namePattern);
    }

    /**
     * Checks if this criteria has any search parameters defined.
     *
     * @return true if at least one criteria is specified, false otherwise
     */
    public boolean isEmpty() {
        return minAge == null && maxAge == null && gender == null &&
               ageGroup == null && hasPartner == null && hasChildren == null &&
               minChildren == null && maxChildren == null && isAlive == null &&
               namePattern == null;
    }

    /**
     * Creates an empty search criteria that matches all inhabitants.
     *
     * @return an empty search criteria
     */
    public static PopulationSearchCriteria empty() {
        return new Builder().build();
    }

    /**
     * Creates search criteria for living inhabitants only.
     *
     * @return criteria for living inhabitants
     */
    public static PopulationSearchCriteria livingOnly() {
        return new Builder().isAlive(true).build();
    }

    /**
     * Creates search criteria for a specific age group.
     *
     * @param ageGroup the age group to search for
     * @return criteria for the specified age group
     */
    public static PopulationSearchCriteria forAgeGroup(AgeGroup ageGroup) {
        return new Builder().ageGroup(ageGroup).build();
    }

    /**
     * Creates search criteria for a specific gender.
     *
     * @param gender the gender to search for
     * @return criteria for the specified gender
     */
    public static PopulationSearchCriteria forGender(Gender gender) {
        return new Builder().gender(gender).build();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;

        PopulationSearchCriteria that = (PopulationSearchCriteria) obj;
        return Objects.equals(minAge, that.minAge) &&
               Objects.equals(maxAge, that.maxAge) &&
               gender == that.gender &&
               ageGroup == that.ageGroup &&
               Objects.equals(hasPartner, that.hasPartner) &&
               Objects.equals(hasChildren, that.hasChildren) &&
               Objects.equals(minChildren, that.minChildren) &&
               Objects.equals(maxChildren, that.maxChildren) &&
               Objects.equals(isAlive, that.isAlive) &&
               Objects.equals(namePattern, that.namePattern);
    }

    @Override
    public int hashCode() {
        return Objects.hash(minAge, maxAge, gender, ageGroup, hasPartner,
                           hasChildren, minChildren, maxChildren, isAlive, namePattern);
    }

    @Override
    public String toString() {
        return "PopulationSearchCriteria{" +
               "minAge=" + minAge +
               ", maxAge=" + maxAge +
               ", gender=" + gender +
               ", ageGroup=" + ageGroup +
               ", hasPartner=" + hasPartner +
               ", hasChildren=" + hasChildren +
               ", minChildren=" + minChildren +
               ", maxChildren=" + maxChildren +
               ", isAlive=" + isAlive +
               ", namePattern='" + namePattern + '\'' +
               '}';
    }

    /**
     * Creates a new builder for PopulationSearchCriteria.
     *
     * @return a new builder instance
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * Builder class for creating PopulationSearchCriteria instances.
     */
    public static class Builder {
        private Integer minAge;
        private Integer maxAge;
        private Gender gender;
        private AgeGroup ageGroup;
        private Boolean hasPartner;
        private Boolean hasChildren;
        private Integer minChildren;
        private Integer maxChildren;
        private Boolean isAlive;
        private String namePattern;

        private Builder() {}

        /**
         * Sets the minimum age criteria.
         *
         * @param age the minimum age
         * @return this builder
         */
        public Builder minAge(int age) {
            this.minAge = age;
            return this;
        }

        /**
         * Sets the maximum age criteria.
         *
         * @param age the maximum age
         * @return this builder
         */
        public Builder maxAge(int age) {
            this.maxAge = age;
            return this;
        }

        /**
         * Sets the age range criteria.
         *
         * @param minAge the minimum age
         * @param maxAge the maximum age
         * @return this builder
         */
        public Builder ageRange(int minAge, int maxAge) {
            this.minAge = minAge;
            this.maxAge = maxAge;
            return this;
        }

        /**
         * Sets the gender criteria.
         *
         * @param gender the gender to search for
         * @return this builder
         */
        public Builder gender(Gender gender) {
            this.gender = gender;
            return this;
        }

        /**
         * Sets the age group criteria.
         *
         * @param ageGroup the age group to search for
         * @return this builder
         */
        public Builder ageGroup(AgeGroup ageGroup) {
            this.ageGroup = ageGroup;
            return this;
        }

        /**
         * Sets the partner status criteria.
         *
         * @param hasPartner true to find those with partners, false for those without
         * @return this builder
         */
        public Builder hasPartner(boolean hasPartner) {
            this.hasPartner = hasPartner;
            return this;
        }

        /**
         * Sets the children status criteria.
         *
         * @param hasChildren true to find those with children, false for those without
         * @return this builder
         */
        public Builder hasChildren(boolean hasChildren) {
            this.hasChildren = hasChildren;
            return this;
        }

        /**
         * Sets the minimum children count criteria.
         *
         * @param count the minimum number of children
         * @return this builder
         */
        public Builder minChildren(int count) {
            this.minChildren = count;
            return this;
        }

        /**
         * Sets the maximum children count criteria.
         *
         * @param count the maximum number of children
         * @return this builder
         */
        public Builder maxChildren(int count) {
            this.maxChildren = count;
            return this;
        }

        /**
         * Sets the children count range criteria.
         *
         * @param minCount the minimum number of children
         * @param maxCount the maximum number of children
         * @return this builder
         */
        public Builder childrenRange(int minCount, int maxCount) {
            this.minChildren = minCount;
            this.maxChildren = maxCount;
            return this;
        }

        /**
         * Sets the alive status criteria.
         *
         * @param isAlive true to find living inhabitants, false for deceased
         * @return this builder
         */
        public Builder isAlive(boolean isAlive) {
            this.isAlive = isAlive;
            return this;
        }

        /**
         * Sets the name pattern criteria.
         *
         * @param pattern the name pattern (supports wildcards)
         * @return this builder
         */
        public Builder namePattern(String pattern) {
            this.namePattern = pattern;
            return this;
        }

        /**
         * Builds the PopulationSearchCriteria instance.
         *
         * @return a new PopulationSearchCriteria
         */
        public PopulationSearchCriteria build() {
            return new PopulationSearchCriteria(this);
        }
    }
}
