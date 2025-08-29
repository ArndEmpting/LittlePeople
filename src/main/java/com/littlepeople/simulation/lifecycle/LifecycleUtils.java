package com.littlepeople.simulation.lifecycle;

import com.littlepeople.core.model.LifeStage;
import com.littlepeople.core.model.Person;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Utility class for lifecycle-related calculations and statistics.
 *
 * @author LittlePeople Development Team
 * @version 1.0
 * @since 1.0
 */
public class LifecycleUtils {

    private static final Logger logger = LoggerFactory.getLogger(LifecycleUtils.class);

    /**
     * Private constructor to prevent instantiation of utility class.
     */
    private LifecycleUtils() {
        throw new UnsupportedOperationException("Utility class");
    }

    /**
     * Calculates the distribution of people across life stages.
     *
     * @param population the population to analyze
     * @return a map of life stages to counts
     */
    public static Map<LifeStage, Long> calculateLifeStageDistribution(List<Person> population) {
        if (population == null) {
            throw new IllegalArgumentException("Population cannot be null");
        }

        return population.stream()
                .filter(Person::isAlive)
                .collect(Collectors.groupingBy(
                        person -> LifeStage.fromAge(person.getAge()),
                        Collectors.counting()));
    }

    /**
     * Calculates the average age of the living population.
     *
     * @param population the population to analyze
     * @return the average age, or 0.0 if population is empty
     */
    public static double calculateAverageAge(List<Person> population) {
        if (population == null) {
            throw new IllegalArgumentException("Population cannot be null");
        }

        return population.stream()
                .filter(Person::isAlive)
                .mapToInt(Person::getAge)
                .average()
                .orElse(0.0);
    }

    /**
     * Counts the number of living people in the population.
     *
     * @param population the population to count
     * @return the number of living people
     */
    public static long countLivingPopulation(List<Person> population) {
        if (population == null) {
            throw new IllegalArgumentException("Population cannot be null");
        }

        return population.stream()
                .filter(Person::isAlive)
                .count();
    }

    /**
     * Counts the number of deceased people in the population.
     *
     * @param population the population to count
     * @return the number of deceased people
     */
    public static long countDeceasedPopulation(List<Person> population) {
        if (population == null) {
            throw new IllegalArgumentException("Population cannot be null");
        }

        return population.stream()
                .filter(person -> !person.isAlive())
                .count();
    }

    /**
     * Finds the oldest living person in the population.
     *
     * @param population the population to search
     * @return the oldest living person, or null if no living people
     */
    public static Person findOldestLivingPerson(List<Person> population) {
        if (population == null) {
            throw new IllegalArgumentException("Population cannot be null");
        }

        return population.stream()
                .filter(Person::isAlive)
                .max((p1, p2) -> Integer.compare(p1.getAge(), p2.getAge()))
                .orElse(null);
    }

    /**
     * Finds the youngest living person in the population.
     *
     * @param population the population to search
     * @return the youngest living person, or null if no living people
     */
    public static Person findYoungestLivingPerson(List<Person> population) {
        if (population == null) {
            throw new IllegalArgumentException("Population cannot be null");
        }

        return population.stream()
                .filter(Person::isAlive)
                .min((p1, p2) -> Integer.compare(p1.getAge(), p2.getAge()))
                .orElse(null);
    }

    /**
     * Validates that all people in the population have valid ages.
     *
     * @param population the population to validate
     * @param maximumAge the maximum allowed age
     * @return true if all ages are valid
     */
    public static boolean validateAges(List<Person> population, int maximumAge) {
        if (population == null) {
            throw new IllegalArgumentException("Population cannot be null");
        }

        if (maximumAge <= 0) {
            throw new IllegalArgumentException("Maximum age must be positive");
        }

        boolean allValid = true;

        for (Person person : population) {
            if (person.getAge() < 0) {
                logger.warn("Person {} has negative age: {}", person.getId(), person.getAge());
                allValid = false;
            } else if (person.getAge() > maximumAge) {
                logger.warn("Person {} exceeds maximum age: {} > {}",
                        person.getId(), person.getAge(), maximumAge);
                allValid = false;
            }
        }

        return allValid;
    }

    /**
     * Gets all people in a specific life stage.
     *
     * @param population the population to filter
     * @param lifeStage the life stage to filter by
     * @return a list of people in the specified life stage
     */
    public static List<Person> getPeopleInLifeStage(List<Person> population, LifeStage lifeStage) {
        if (population == null) {
            throw new IllegalArgumentException("Population cannot be null");
        }

        if (lifeStage == null) {
            throw new IllegalArgumentException("Life stage cannot be null");
        }

        return population.stream()
                .filter(Person::isAlive)
                .filter(person -> LifeStage.fromAge(person.getAge()) == lifeStage)
                .collect(Collectors.toList());
    }
}
