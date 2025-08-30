package com.littlepeople.simulation.population;

import com.littlepeople.core.model.Person;
import com.littlepeople.core.model.Gender;
import com.littlepeople.simulation.population.DemographicDistribution.AgeGroup;
import com.littlepeople.simulation.population.PopulationValidationResult.ValidationSeverity;
import com.littlepeople.simulation.population.PopulationValidationResult.ValidationIssue;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Validates population demographics for consistency and realistic patterns.
 *
 * <p>This class performs comprehensive validation of the population to ensure
 * demographic patterns remain realistic and consistent. It checks age distributions,
 * gender ratios, family structures, and other demographic constraints.
 *
 * @author LittlePeople Development Team
 * @version 1.0
 * @since 1.0
 */
public class DemographicValidator {

    private static final double GENDER_RATIO_WARNING_THRESHOLD = 0.1; // 10% deviation
    private static final double GENDER_RATIO_ERROR_THRESHOLD = 0.2; // 20% deviation
    private static final double AGE_GROUP_WARNING_THRESHOLD = 0.15; // 15% deviation
    private static final double AGE_GROUP_ERROR_THRESHOLD = 0.3; // 30% deviation
    private static final int MIN_VIABLE_POPULATION = 50;

    /**
     * Validates the entire population for demographic consistency.
     *
     * @param population the population to validate
     * @param expectedDistribution the expected demographic distribution
     * @return validation result with any issues found
     */
    public PopulationValidationResult validatePopulation(List<Person> population,
                                                        DemographicDistribution expectedDistribution) {

        if (population == null) {
            return PopulationValidationResult.withError(
                new ValidationIssue("Population cannot be null", ValidationSeverity.CRITICAL, ValidationIssue.IssueType.ERROR)
            );
        }

        if (expectedDistribution == null) {
            return PopulationValidationResult.withError(
                new ValidationIssue("Expected distribution cannot be null", ValidationSeverity.CRITICAL, ValidationIssue.IssueType.ERROR)
            );
        }

        PopulationValidationResult.Builder resultBuilder = PopulationValidationResult.builder();

        // Filter to living population only
        List<Person> livingPopulation = population.stream()
                .filter(Person::isAlive)
                .collect(Collectors.toList());

        // Validate population size
        validatePopulationSize(livingPopulation, resultBuilder);

        // Validate gender distribution
        validateGenderDistribution(livingPopulation, expectedDistribution, resultBuilder);

        // Validate age distribution
        validateAgeDistribution(livingPopulation, expectedDistribution, resultBuilder);

        // Validate family structures
        validateFamilyStructures(livingPopulation, resultBuilder);

        // Add recommendations based on findings
        addRecommendations(livingPopulation, expectedDistribution, resultBuilder);

        return resultBuilder.build();
    }

    /**
     * Validates population size for viability.
     *
     * @param population the living population
     * @param resultBuilder the result builder to add issues to
     */
    private void validatePopulationSize(List<Person> population, PopulationValidationResult.Builder resultBuilder) {
        int size = population.size();

        if (size == 0) {
            resultBuilder.addError("Population is empty", ValidationSeverity.CRITICAL);
        } else if (size < MIN_VIABLE_POPULATION) {
            resultBuilder.addWarning("Population size is below minimum viable threshold (" + MIN_VIABLE_POPULATION + ")",
                                   ValidationSeverity.HIGH);
            resultBuilder.addRecommendation("Consider increasing immigration or reducing emigration to maintain population viability");
        }
    }

    /**
     * Validates gender distribution against expected ratios.
     *
     * @param population the living population
     * @param expectedDistribution the expected distribution
     * @param resultBuilder the result builder to add issues to
     */
    private void validateGenderDistribution(List<Person> population,
                                          DemographicDistribution expectedDistribution,
                                          PopulationValidationResult.Builder resultBuilder) {

        if (population.isEmpty()) {
            return;
        }

        Map<Gender, Long> genderCounts = population.stream()
                .collect(Collectors.groupingBy(Person::getGender, Collectors.counting()));

        int totalPopulation = population.size();
        double actualMaleRatio = genderCounts.getOrDefault(Gender.MALE, 0L).doubleValue() / totalPopulation;
        double expectedMaleRatio = expectedDistribution.getMaleRatio();
        double deviation = Math.abs(actualMaleRatio - expectedMaleRatio);

        if (deviation > GENDER_RATIO_ERROR_THRESHOLD) {
            resultBuilder.addError(
                String.format("Gender ratio severely imbalanced. Expected male ratio: %.2f, Actual: %.2f",
                             expectedMaleRatio, actualMaleRatio),
                ValidationSeverity.HIGH
            );
        } else if (deviation > GENDER_RATIO_WARNING_THRESHOLD) {
            resultBuilder.addWarning(
                String.format("Gender ratio imbalanced. Expected male ratio: %.2f, Actual: %.2f",
                             expectedMaleRatio, actualMaleRatio),
                ValidationSeverity.MEDIUM
            );
        }
    }

    /**
     * Validates age distribution against expected patterns.
     *
     * @param population the living population
     * @param expectedDistribution the expected distribution
     * @param resultBuilder the result builder to add issues to
     */
    private void validateAgeDistribution(List<Person> population,
                                       DemographicDistribution expectedDistribution,
                                       PopulationValidationResult.Builder resultBuilder) {

        if (population.isEmpty()) {
            return;
        }

        Map<AgeGroup, Long> ageGroupCounts = population.stream()
                .collect(Collectors.groupingBy(
                    person -> expectedDistribution.getAgeGroup(person.getAge()),
                    Collectors.counting()
                ));

        int totalPopulation = population.size();

        for (AgeGroup ageGroup : AgeGroup.values()) {
            double actualRatio = ageGroupCounts.getOrDefault(ageGroup, 0L).doubleValue() / totalPopulation;
            double expectedRatio = expectedDistribution.getAgeGroupWeight(ageGroup);
            double deviation = Math.abs(actualRatio - expectedRatio);

            if (deviation > AGE_GROUP_ERROR_THRESHOLD) {
                resultBuilder.addError(
                    String.format("Age group %s severely imbalanced. Expected: %.2f, Actual: %.2f",
                                 ageGroup.getDisplayName(), expectedRatio, actualRatio),
                    ValidationSeverity.HIGH
                );
            } else if (deviation > AGE_GROUP_WARNING_THRESHOLD) {
                resultBuilder.addWarning(
                    String.format("Age group %s imbalanced. Expected: %.2f, Actual: %.2f",
                                 ageGroup.getDisplayName(), expectedRatio, actualRatio),
                    ValidationSeverity.MEDIUM
                );
            }
        }
    }

    /**
     * Validates family structures for consistency.
     *
     * @param population the living population
     * @param resultBuilder the result builder to add issues to
     */
    private void validateFamilyStructures(List<Person> population, PopulationValidationResult.Builder resultBuilder) {
        int orphanedChildren = 0;
        int inconsistentRelationships = 0;
        int impossibleAgeGaps = 0;

        for (Person person : population) {
            // Check for orphaned children
            if (person.getAge() < 18 && person.getParents().isEmpty()) {
                orphanedChildren++;
            }

            // Check parent-child age gaps
            for (Person parent : person.getParents()) {
                int ageGap = parent.getAge() - person.getAge();
                if (ageGap < 15 || ageGap > 60) {
                    impossibleAgeGaps++;
                }
            }

            // Check partner relationship consistency
            Person partner = person.getPartner();
            if (partner != null && (partner.getPartner() == null || !partner.getPartner().equals(person))) {
                inconsistentRelationships++;
            }
        }

        if (orphanedChildren > 0) {
            resultBuilder.addWarning(
                String.format("Found %d orphaned children without parents", orphanedChildren),
                ValidationSeverity.MEDIUM
            );
        }

        if (inconsistentRelationships > 0) {
            resultBuilder.addError(
                String.format("Found %d inconsistent partnership relationships", inconsistentRelationships),
                ValidationSeverity.HIGH
            );
        }

        if (impossibleAgeGaps > 0) {
            resultBuilder.addError(
                String.format("Found %d impossible parent-child age gaps", impossibleAgeGaps),
                ValidationSeverity.HIGH
            );
        }
    }

    /**
     * Adds recommendations based on validation findings.
     *
     * @param population the living population
     * @param expectedDistribution the expected distribution
     * @param resultBuilder the result builder to add recommendations to
     */
    private void addRecommendations(List<Person> population,
                                  DemographicDistribution expectedDistribution,
                                  PopulationValidationResult.Builder resultBuilder) {

        if (population.isEmpty()) {
            resultBuilder.addRecommendation("Initialize population to begin simulation");
            return;
        }

        // Calculate current demographics
        Map<Gender, Long> genderCounts = population.stream()
                .collect(Collectors.groupingBy(Person::getGender, Collectors.counting()));

        int totalPopulation = population.size();
        double actualMaleRatio = genderCounts.getOrDefault(Gender.MALE, 0L).doubleValue() / totalPopulation;
        double expectedMaleRatio = expectedDistribution.getMaleRatio();

        // Gender balance recommendations
        if (actualMaleRatio < expectedMaleRatio - GENDER_RATIO_WARNING_THRESHOLD) {
            resultBuilder.addRecommendation("Consider increasing male immigration to balance gender ratio");
        } else if (actualMaleRatio > expectedMaleRatio + GENDER_RATIO_WARNING_THRESHOLD) {
            resultBuilder.addRecommendation("Consider increasing female immigration to balance gender ratio");
        }

        // Age distribution recommendations
        Map<AgeGroup, Long> ageGroupCounts = population.stream()
                .collect(Collectors.groupingBy(
                    person -> expectedDistribution.getAgeGroup(person.getAge()),
                    Collectors.counting()
                ));

        long youngAdults = ageGroupCounts.getOrDefault(AgeGroup.YOUNG_ADULT, 0L);
        long children = ageGroupCounts.getOrDefault(AgeGroup.CHILD, 0L);

        if (children == 0 && youngAdults > 0) {
            resultBuilder.addRecommendation("Population may need children for long-term sustainability");
        }

        if (youngAdults < totalPopulation * 0.1) {
            resultBuilder.addRecommendation("Low young adult population may affect future growth and family formation");
        }

        // Population size recommendations
        if (totalPopulation < MIN_VIABLE_POPULATION) {
            resultBuilder.addRecommendation("Population is below viable threshold - consider increasing immigration");
        }
    }
}
