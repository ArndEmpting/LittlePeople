package com.littlepeople.simulation.population;

import com.littlepeople.core.model.Person;
import com.littlepeople.core.exceptions.SimulationException;
import com.littlepeople.core.model.events.EmigrationEvent;
import com.littlepeople.core.model.events.ImmigrationEvent;
import com.littlepeople.core.model.events.PopulationInitializedEvent;

import java.time.LocalDate;
import java.util.List;
import java.util.Collections;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

/**
 * Default implementation of the PopulationManager interface.
 *
 * <p>This class provides the complete population management functionality including
 * initialization, growth control, validation, and statistics generation. It coordinates
 * all population-related operations and integrates with the simulation event system.
 *
 * @author LittlePeople Development Team
 * @version 1.0
 * @since 1.0
 */
public class PopulationManagerImpl implements PopulationManager {

    private final PopulationGenerator generator;
    private final PopulationGrowthController growthController;
    private final DemographicValidator validator;
    private final List<Person> population;
    private PopulationConfiguration configuration;
    private boolean initialized = false;
    static PopulationManager instance = new PopulationManagerImpl();
    public static PopulationManager getInstance() {
        return instance;
    }
    /**
     * Creates a new PopulationManagerImpl with default components.
     */
    public PopulationManagerImpl() {
        this.generator = new PopulationGenerator();
        this.growthController = new PopulationGrowthController(generator);
        this.validator = new DemographicValidator();
        this.population = new CopyOnWriteArrayList<>();
    }

    /**
     * Creates a new PopulationManagerImpl with specified components.
     *
     * @param generator the population generator to use
     * @param growthController the growth controller to use
     * @param validator the demographic validator to use
     */
    public PopulationManagerImpl(PopulationGenerator generator,
                                PopulationGrowthController growthController,
                                DemographicValidator validator) {
        this.generator = generator;
        this.growthController = growthController;
        this.validator = validator;
        this.population = new CopyOnWriteArrayList<>();
    }

    @Override
    public int initializePopulation(PopulationConfiguration config) throws SimulationException {
        if (config == null) {
            throw new IllegalArgumentException("Configuration cannot be null");
        }

        if (initialized) {
            throw new IllegalStateException("Population is already initialized");
        }

        config.validate();
        this.configuration = config;

        // Generate initial population
        LocalDate startDate = LocalDate.now(); // In real implementation, this would come from simulation clock
        List<Person> initialInhabitants = generator.generateInitialPopulation(config, startDate);

        // Add to population
        .addAll(initialInhabitants);
        initialized = true;

        // Fire population initialized event
        PopulationInitializedEvent event = new PopulationInitializedEvent(
            initialInhabitants,
            startDate,
            "Initial population of " + initialInhabitants.size() + " inhabitants"
        );

        // In a real implementation, this would be published through the event system
        // eventPublisher.publish(event);

        return initialInhabitants.size();
    }

    @Override
    public int processImmigration(LocalDate currentDate) throws SimulationException {
        if (!initialized) {
            throw new IllegalStateException("Population is not initialized");
        }

        if (currentDate == null) {
            throw new IllegalArgumentException("Current date cannot be null");
        }

        List<Person> currentPopulation = getLivingPopulation();
        ImmigrationEvent event = growthController.processImmigration(currentPopulation, configuration, currentDate);

        if (event != null) {
            // Add immigrants to population
            for (Person immigrant : event.getImmigrants()) {
                addInhabitant(immigrant);
            }

            // In a real implementation, this would be published through the event system
            // eventPublisher.publish(event);

            return event.getImmigrantCount();
        }

        return 0;
    }

    @Override
    public int processEmigration(LocalDate currentDate) throws SimulationException {
        if (!initialized) {
            throw new IllegalStateException("Population is not initialized");
        }

        if (currentDate == null) {
            throw new IllegalArgumentException("Current date cannot be null");
        }

        List<Person> currentPopulation = getLivingPopulation();
        EmigrationEvent event = growthController.processEmigration(currentPopulation, configuration, currentDate);

        if (event != null) {
            // Remove emigrants from population
            for (Person emigrant : event.getEmigrants()) {
                removeInhabitant(emigrant);
            }

            // In a real implementation, this would be published through the event system
            // eventPublisher.publish(event);

            return event.getEmigrantCount();
        }

        return 0;
    }

    @Override
    public int getPopulationSize() {
        if (!initialized) {
            throw new IllegalStateException("Population is not initialized");
        }

        return (int) population.stream().filter(Person::isAlive).count();
    }

    @Override
    public List<Person> getPopulation() {
        if (!initialized) {
            throw new IllegalStateException("Population is not initialized");
        }

        return Collections.unmodifiableList(getLivingPopulation());
    }

    @Override
    public List<Person> findInhabitants(PopulationSearchCriteria criteria) {
        if (!initialized) {
            throw new IllegalStateException("Population is not initialized");
        }

        if (criteria == null) {
            throw new IllegalArgumentException("Criteria cannot be null");
        }

        return population.stream()
                .filter(person -> matchesCriteria(person, criteria))
                .collect(Collectors.toList());
    }

    @Override
    public void addInhabitant(Person person) throws SimulationException {
        if (!initialized) {
            throw new IllegalStateException("Population is not initialized");
        }

        if (person == null) {
            throw new IllegalArgumentException("Person cannot be null");
        }

        if (population.contains(person)) {
            throw new IllegalArgumentException("Person is already in the population");
        }

        population.add(person);
    }

    @Override
    public void removeInhabitant(Person person) throws SimulationException {
        if (!initialized) {
            throw new IllegalStateException("Population is not initialized");
        }

        if (person == null) {
            throw new IllegalArgumentException("Person cannot be null");
        }

        if (!population.contains(person)) {
            throw new IllegalArgumentException("Person is not in the population");
        }

        population.remove(person);
    }

    @Override
    public PopulationValidationResult validatePopulation() {
        if (!initialized) {
            throw new IllegalStateException("Population is not initialized");
        }

        return validator.validatePopulation(population, configuration.getDemographicDistribution());
    }

    @Override
    public DemographicStatistics getStatistics() {
        if (!initialized) {
            throw new IllegalStateException("Population is not initialized");
        }

        return calculateStatistics();
    }

    @Override
    public boolean isInitialized() {
        return initialized;
    }

    /**
     * Gets the living population as a list.
     *
     * @return list of living inhabitants
     */
    private List<Person> getLivingPopulation() {
        return population.stream()
                .filter(Person::isAlive)
                .collect(Collectors.toList());
    }

    /**
     * Checks if a person matches the search criteria.
     *
     * @param person the person to check
     * @param criteria the search criteria
     * @return true if the person matches the criteria
     */
    private boolean matchesCriteria(Person person, PopulationSearchCriteria criteria) {
        // Check alive status
        if (criteria.getIsAlive().isPresent() && criteria.getIsAlive().get() != person.isAlive()) {
            return false;
        }

        // Check age range
        if (criteria.getMinAge().isPresent() && person.getAge() < criteria.getMinAge().get()) {
            return false;
        }
        if (criteria.getMaxAge().isPresent() && person.getAge() > criteria.getMaxAge().get()) {
            return false;
        }

        // Check gender
        if (criteria.getGender().isPresent() && person.getGender() != criteria.getGender().get()) {
            return false;
        }

        // Check age group
        if (criteria.getAgeGroup().isPresent()) {
            DemographicDistribution.AgeGroup personAgeGroup = configuration.getDemographicDistribution().getAgeGroup(person.getAge());
            if (personAgeGroup != criteria.getAgeGroup().get()) {
                return false;
            }
        }

        // Check partner status
        if (criteria.getHasPartner().isPresent()) {
            boolean hasPartner = person.getPartner() != null;
            if (hasPartner != criteria.getHasPartner().get()) {
                return false;
            }
        }

        // Check children status
        if (criteria.getHasChildren().isPresent()) {
            boolean hasChildren = !person.getChildren().isEmpty();
            if (hasChildren != criteria.getHasChildren().get()) {
                return false;
            }
        }

        // Check children count range
        int childrenCount = person.getChildren().size();
        if (criteria.getMinChildren().isPresent() && childrenCount < criteria.getMinChildren().get()) {
            return false;
        }
        if (criteria.getMaxChildren().isPresent() && childrenCount > criteria.getMaxChildren().get()) {
            return false;
        }

        // Check name pattern
        if (criteria.getNamePattern().isPresent()) {
            String pattern = criteria.getNamePattern().get();
            String personName = person.getFirstName() + " " + person.getLastName();
            if (!personName.toLowerCase().contains(pattern.toLowerCase())) {
                return false;
            }
        }

        return true;
    }

    /**
     * Calculates comprehensive demographic statistics for the current population.
     *
     * @return demographic statistics
     */
    private DemographicStatistics calculateStatistics() {
        List<Person> living = getLivingPopulation();

        DemographicStatistics.Builder builder = DemographicStatistics.builder()
                .totalPopulation(population.size())
                .livingPopulation(living.size())
                .deceasedPopulation(population.size() - living.size());

        if (living.isEmpty()) {
            return builder.build();
        }

        // Calculate gender statistics
        for (com.littlepeople.core.model.Gender gender : com.littlepeople.core.model.Gender.values()) {
            long count = living.stream().filter(p -> p.getGender() == gender).count();
            builder.genderCount(gender, (int) count);
        }

        // Calculate age group statistics
        for (DemographicDistribution.AgeGroup ageGroup : DemographicDistribution.AgeGroup.values()) {
            long count = living.stream()
                    .filter(p -> configuration.getDemographicDistribution().getAgeGroup(p.getAge()) == ageGroup)
                    .count();
            builder.ageGroupCount(ageGroup, (int) count);
        }

        // Calculate age statistics
        double averageAge = living.stream().mapToInt(Person::getAge).average().orElse(0.0);
        int minAge = living.stream().mapToInt(Person::getAge).min().orElse(0);
        int maxAge = living.stream().mapToInt(Person::getAge).max().orElse(0);
        int medianAge = calculateMedianAge(living);

        builder.averageAge(averageAge)
               .minAge(minAge)
               .maxAge(maxAge)
               .medianAge(medianAge);

        // Calculate family statistics
        long partnerships = living.stream().filter(p -> p.getPartner() != null).count() / 2;
        long families = living.stream().filter(p -> !p.getChildren().isEmpty()).count();
        double avgChildren = living.stream()
                .mapToInt(p -> p.getChildren().size())
                .average()
                .orElse(0.0);

        builder.totalPartnerships((int) partnerships)
               .totalFamilies((int) families)
               .averageChildrenPerFamily(avgChildren);

        return builder.build();
    }

    /**
     * Calculates the median age of the population.
     *
     * @param population the population to analyze
     * @return the median age
     */
    private int calculateMedianAge(List<Person> population) {
        if (population.isEmpty()) {
            return 0;
        }

        List<Integer> ages = population.stream()
                .map(Person::getAge)
                .sorted()
                .collect(Collectors.toList());

        int size = ages.size();
        if (size % 2 == 0) {
            return (ages.get(size / 2 - 1) + ages.get(size / 2)) / 2;
        } else {
            return ages.get(size / 2);
        }
    }
}
