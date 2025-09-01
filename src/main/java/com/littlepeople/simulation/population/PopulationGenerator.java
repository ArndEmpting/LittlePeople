package com.littlepeople.simulation.population;

import com.littlepeople.core.model.HealthStatus;
import com.littlepeople.core.model.Person;
import com.littlepeople.core.model.Gender;
import com.littlepeople.core.model.PersonBuilder;
import com.littlepeople.core.exceptions.SimulationException;
import com.littlepeople.core.model.WealthStatus;
import com.littlepeople.core.util.NameGenerator;
import com.littlepeople.simulation.population.DemographicDistribution.AgeGroup;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Generates initial population and immigrants with realistic demographic distributions.
 *
 * <p>This class creates inhabitants based on demographic distributions and
 * configuration parameters. It generates realistic age, gender, and other
 * demographic characteristics while maintaining statistical consistency.
 *
 * @author LittlePeople Development Team
 * @version 1.0
 * @since 1.0
 */
public class PopulationGenerator {

    private final Random random;

    /**
     * Creates a new PopulationGenerator with default random seed.
     */
    public PopulationGenerator() {
        this.random = ThreadLocalRandom.current();
    }

    /**
     * Creates a new PopulationGenerator with specified random seed for reproducibility.
     *
     * @param seed the random seed to use
     */
    public PopulationGenerator(long seed) {
        this.random = new Random(seed);
    }

    /**
     * Generates the initial population based on configuration.
     *
     * @param config the population configuration
     * @param startDate the simulation start date
     * @return list of generated inhabitants
     * @throws SimulationException if generation fails
     */
    public List<Person> generateInitialPopulation(PopulationConfiguration config, LocalDate startDate)
            throws SimulationException {

        if (config == null) {
            throw new IllegalArgumentException("Configuration cannot be null");
        }
        if (startDate == null) {
            throw new IllegalArgumentException("Start date cannot be null");
        }

        config.validate();

        List<Person> population = new ArrayList<>();
        int targetSize = config.getInitialPopulationSize();
        DemographicDistribution demographics = config.getDemographicDistribution();

        // Generate individuals based on demographic distribution
        for (int i = 0; i < targetSize; i++) {
            Person person = generatePerson(demographics, startDate);
            population.add(person);
        }

        // Generate families if enabled
        if (config.isFamilyGenerationEnabled()) {
            generateFamilyRelationships(population, demographics);
        }

        return population;
    }

    /**
     * Generates immigrants based on configuration and current date.
     *
     * @param count the number of immigrants to generate
     * @param demographics the demographic distribution to use
     * @param currentDate the current simulation date
     * @return list of generated immigrants
     * @throws SimulationException if generation fails
     */
    public List<Person> generateImmigrants(int count, DemographicDistribution demographics, LocalDate currentDate)
            throws SimulationException {

        if (count < 0) {
            throw new IllegalArgumentException("Count cannot be negative");
        }
        if (demographics == null) {
            throw new IllegalArgumentException("Demographics cannot be null");
        }
        if (currentDate == null) {
            throw new IllegalArgumentException("Current date cannot be null");
        }

        List<Person> immigrants = new ArrayList<>();

        for (int i = 0; i < count; i++) {
            Person immigrant = generatePerson(demographics, currentDate);
            immigrants.add(immigrant);
        }

        return immigrants;
    }

    /**
     * Generates a single person based on demographic distribution.
     *
     * @param demographics the demographic distribution
     * @param currentDate the current date for age calculation
     * @return the generated person
     * @throws SimulationException if generation fails
     */
    private Person generatePerson(DemographicDistribution demographics, LocalDate currentDate)
            throws SimulationException {

        try {
            // Generate gender based on distribution
            Gender gender = generateGender(demographics);

            // Generate age based on distribution
            int age = generateAge(demographics);

            // Calculate birth date
            LocalDate birthDate = currentDate.minusYears(age);
            NameGenerator nameGen = new NameGenerator();
            // Create person using PersonBuilder
            return new PersonBuilder()
                    .gender(gender)
                    .birthDate(birthDate)
                    .firstName(nameGen.generateFirstName(gender))
                    .lastName(nameGen.generateLastName())
                    .healthStatus(generateHealthStatus())
                    .wealthStatus(generateWealthStatus())
                    .build();

        } catch (Exception e) {
            throw new SimulationException("Failed to generate person: " + e.getMessage(), e);
        }
    }

    WealthStatus generateWealthStatus() {
        double roll = random.nextDouble();
        if(roll< 0.1){
            return WealthStatus.RICH;
        }else if (roll < 0.2) {
            return WealthStatus.UPPER_MIDDLE_CLASS;
        } else if (roll < 0.4) {
            return WealthStatus.MIDDLE_CLASS;
        } else if (roll < 0.6) {
            return WealthStatus.LOWER_MIDDLE_CLASS;
        } else {
            return WealthStatus.POOR;
        }
    }

    HealthStatus generateHealthStatus() {
        double roll = random.nextDouble();
        if(roll< 0.2){
            return HealthStatus.EXCELLENT;
        }else if (roll < 0.7) {
            return HealthStatus.HEALTHY;
        } else if (roll < 0.9) {
            return HealthStatus.SICK;
        } else {
            return HealthStatus.CRITICALLY_ILL;
        }
    }

    /**
     * Generates gender based on demographic distribution.
     *
     * @param demographics the demographic distribution
     * @return the generated gender
     */
    private Gender generateGender(DemographicDistribution demographics) {
        double genderRoll = random.nextDouble();
        return genderRoll < demographics.getMaleRatio() ? Gender.MALE : Gender.FEMALE;
    }

    /**
     * Generates age based on demographic distribution.
     *
     * @param demographics the demographic distribution
     * @return the generated age
     */
    private int generateAge(DemographicDistribution demographics) {
        // First, select age group based on distribution weights
        AgeGroup selectedGroup = selectAgeGroup(demographics);

        // Then generate specific age within that group
        int minAge = Math.max(selectedGroup.getMinAge(), demographics.getMinAge());
        int maxAge = Math.min(selectedGroup.getMaxAge(), demographics.getMaxAge());

        // Use normal distribution around group center for more realistic distribution
        int groupCenter = (minAge + maxAge) / 2;
        double standardDeviation = (maxAge - minAge) / 4.0; // 95% within group range

        int age;
        do {
            double ageDouble = random.nextGaussian() * standardDeviation + groupCenter;
            age = (int) Math.round(ageDouble);
        } while (age < minAge || age > maxAge);

        return Math.max(demographics.getMinAge(), Math.min(age, demographics.getMaxAge()));
    }

    /**
     * Selects an age group based on distribution weights.
     *
     * @param demographics the demographic distribution
     * @return the selected age group
     */
    private AgeGroup selectAgeGroup(DemographicDistribution demographics) {
        double roll = random.nextDouble();
        double cumulative = 0.0;

        for (AgeGroup group : AgeGroup.values()) {
            cumulative += demographics.getAgeGroupWeight(group);
            if (roll <= cumulative) {
                return group;
            }
        }

        // Fallback to young adult if no group selected (shouldn't happen with proper weights)
        return AgeGroup.YOUNG_ADULT;
    }

    /**
     * Generates family relationships within the population.
     *
     * @param population the population to generate families for
     * @param demographics the demographic distribution
     */
    private void generateFamilyRelationships(List<Person> population, DemographicDistribution demographics) {
        // Simple family generation - pair adults and assign children
        List<Person> availableAdults = population.stream()
                .filter(p -> p.getAge() >= 18 && p.getAge() <= 65)
                .filter(p -> p.getPartner() == null)
                .toList();

        List<Person> children = population.stream()
                .filter(p -> p.getAge() < 18)
                .filter(p -> p.getParents().isEmpty())
                .toList();

        // Create partnerships
        for (int i = 0; i < availableAdults.size() - 1; i += 2) {
            Person person1 = availableAdults.get(i);
            Person person2 = availableAdults.get(i + 1);

            // Only pair different genders for this implementation
            if (person1.getGender() != person2.getGender() && !person1.isDirectFamily(person2)
            ) {
                person1.setPartner(person2);


                // Assign some children to this couple
                int childrenCount = generateChildrenCount();
                int assigned = 0;

                for (Person child : children) {
                    if (assigned >= childrenCount) break;
                    int minParentAge = Math.min(person1.getAge(), person2.getAge());
                    if (child.getAge() + 12 > minParentAge) continue; // Ensure
                    if (child.getParents().isEmpty()) {
                        person1.addChild(child);
                        person2.addChild(child);
                        assigned++;
                    }
                }
            }
        }
    }

    /**
     * Generates a realistic number of children for a family.
     *
     * @return the number of children
     */
    private int generateChildrenCount() {
        double roll = random.nextDouble();

        // Realistic distribution: 0-30%, 1-25%, 2-25%, 3-15%, 4+-5%
        if (roll < 0.30) return 0;
        if (roll < 0.55) return 1;
        if (roll < 0.80) return 2;
        if (roll < 0.95) return 3;
        return 4;
    }
}
