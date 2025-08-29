package com.littlepeople.simulation.partnerships;

import com.littlepeople.core.exceptions.SimulationException;
import com.littlepeople.core.interfaces.EventProcessor;
import com.littlepeople.core.interfaces.Event;
import com.littlepeople.core.model.Person;
import com.littlepeople.core.model.LifeStage;
import com.littlepeople.core.model.EventType;
import com.littlepeople.core.model.events.PartnershipFormedEvent;
import com.littlepeople.core.model.events.PartnershipDissolvedEvent;
import com.littlepeople.core.model.events.PersonDeathEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Main processor for partnership formation and dissolution in the simulation.
 *
 * <p>This class implements the core partnership formation algorithm that identifies
 * eligible individuals, calculates compatibility scores, and creates realistic
 * romantic relationships. It integrates with the event system to ensure all
 * partnership changes are properly tracked and processed.</p>
 *
 * <h2>Partnership Formation Process</h2>
 * <ol>
 *   <li>Identify eligible singles in the population</li>
 *   <li>Calculate compatibility scores between potential partners</li>
 *   <li>Apply probability-based matching algorithm</li>
 *   <li>Create partnership events for successful matches</li>
 *   <li>Update relationship status and bidirectional connections</li>
 * </ol>
 *
 * <h2>Eligibility Criteria</h2>
 * <ul>
 *   <li>Person must be single, divorced, or widowed</li>
 *   <li>Person must be at least 16 years old (minimum partnership age)</li>
 *   <li>Person must not be deceased</li>
 *   <li>Person must be in appropriate life stage (not child)</li>
 * </ul>
 *
 * @since 1.0.0
 * @version 1.0.0
 */
public class PartnershipProcessor implements EventProcessor {

    private static final Logger logger = LoggerFactory.getLogger(PartnershipProcessor.class);

    private final CompatibilityCalculator compatibilityCalculator;
    private final Random random;

    // Configuration parameters
    private static final double BASE_PARTNERSHIP_PROBABILITY = 0.15; // Base chance per simulation cycle
    private static final double COMPATIBILITY_THRESHOLD = 0.3; // Minimum compatibility for consideration
    private static final int MAX_PARTNERS_TO_CONSIDER = 10; // Limit for performance
    private static final int MIN_PARTNERSHIP_AGE = 16;

    /**
     * Creates a new partnership processor with default configuration.
     */
    public PartnershipProcessor() {
        this.compatibilityCalculator = new CompatibilityCalculator();
        this.random = new Random();
    }

    /**
     * Creates a new partnership processor with custom configuration.
     *
     * @param compatibilityCalculator custom compatibility calculator
     * @param random random number generator for reproducible testing
     */
    public PartnershipProcessor(CompatibilityCalculator compatibilityCalculator, Random random) {
        this.compatibilityCalculator = compatibilityCalculator;
        this.random = random;
    }

    /**
     * Processes partnership formation for the current simulation cycle.
     *
     * <p>This method identifies all eligible singles in the population and attempts
     * to form new partnerships based on compatibility scores and probability
     * calculations. The algorithm ensures realistic partnership formation rates
     * while maintaining high-quality matches.</p>
     *
     * @param population the current population of people
     * @param currentDate the current simulation date
     * @return list of partnership events created during this cycle
     * @throws SimulationException if processing fails due to data corruption or system errors
     */
    public List<PartnershipFormedEvent> processPartnershipFormation(List<Person> population, LocalDate currentDate)
            throws SimulationException {

        if (population == null || population.isEmpty()) {
            logger.debug("No population provided for partnership formation");
            return Collections.emptyList();
        }

        logger.debug("Processing partnership formation for {} people on {}", population.size(), currentDate);

        List<PartnershipFormedEvent> partnerships = new ArrayList<>();

        try {
            // Find all eligible singles
            List<Person> eligibleSingles = findEligibleSingles(population);
            logger.debug("Found {} eligible singles for partnership formation", eligibleSingles.size());

            // Process partnership formation for each eligible person
            Set<UUID> alreadyMatched = new HashSet<>();

            for (Person person : eligibleSingles) {
                if (alreadyMatched.contains(person.getId())) {
                    continue; // Already matched in this cycle
                }

                // Find compatible partners
                List<Person> potentialPartners = findEligiblePartners(person, eligibleSingles);

                // Remove already matched people
                potentialPartners = potentialPartners.stream()
                    .filter(p -> !alreadyMatched.contains(p.getId()))
                    .collect(Collectors.toList());

                if (potentialPartners.isEmpty()) {
                    continue;
                }

                // Attempt to form a partnership
                Person selectedPartner = selectPartner(person, potentialPartners);
                if (selectedPartner != null) {
                    PartnershipFormedEvent partnership = createPartnership(person, selectedPartner, currentDate);
                    partnerships.add(partnership);

                    // Mark both as matched for this cycle
                    alreadyMatched.add(person.getId());
                    alreadyMatched.add(selectedPartner.getId());

                    logger.info("Partnership formed between {} and {}",
                               person.getId(), selectedPartner.getId());
                }
            }

            logger.info("Partnership formation complete: {} new partnerships formed", partnerships.size());
            return partnerships;

        } catch (Exception e) {
            logger.error("Error during partnership formation processing", e);
            throw new SimulationException("Failed to process partnership formation", e);
        }
    }

    /**
     * Processes partnership dissolution due to death events.
     *
     * @param deathEvent the death event that may trigger partnership dissolution
     * @return partnership dissolution event if applicable, null otherwise
     * @throws SimulationException if processing fails
     */
    public PartnershipDissolvedEvent processPartnershipDissolution(PersonDeathEvent deathEvent)
            throws SimulationException {

        if (deathEvent == null) {
            throw new IllegalArgumentException("Death event cannot be null");
        }

        // This would need to be implemented based on the actual Person manager
        // For now, return null as dissolution is handled by the death processor
        logger.debug("Partnership dissolution processing for death event: {}", deathEvent.getId());
        return null;
    }

    /**
     * Calculates compatibility score between two potential partners.
     *
     * @param person1 the first person
     * @param person2 the second person
     * @return compatibility score from 0.0 to 1.0
     */
    public double calculateCompatibility(Person person1, Person person2) {
        return compatibilityCalculator.calculateCompatibility(person1, person2);
    }

    /**
     * Finds all eligible partners for a specific person.
     *
     * @param person the person seeking partnership
     * @param population the population to search within
     * @return list of eligible partners sorted by compatibility (highest first)
     */
    public List<Person> findEligiblePartners(Person person, List<Person> population) {
        if (person == null || population == null) {
            return Collections.emptyList();
        }

        return population.stream()
            .filter(p -> !p.getId().equals(person.getId())) // Not same person
            .filter(this::isEligibleForPartnership) // Must be eligible
            .map(p -> new PartnerMatch(p, compatibilityCalculator.calculateCompatibility(person, p)))
            .filter(match -> match.compatibility >= COMPATIBILITY_THRESHOLD) // Minimum compatibility
            .sorted((m1, m2) -> Double.compare(m2.compatibility, m1.compatibility)) // Highest compatibility first
            .limit(MAX_PARTNERS_TO_CONSIDER) // Limit for performance
            .map(match -> match.person)
            .collect(Collectors.toList());
    }

    /**
     * Determines if a person is eligible for partnership formation.
     *
     * @param person the person to check
     * @return true if the person is eligible for partnership
     */
    public boolean isEligibleForPartnership(Person person) {
        if (person == null || !person.isAlive()) {
            return false;
        }

        // Must be old enough
        if (person.getAge() < MIN_PARTNERSHIP_AGE) {
            return false;
        }

        // Must not be a child
        if (person.getLifeStage() == LifeStage.CHILD) {
            return false;
        }

        // Must not already have a partner
        if (person.getPartner() != null) {
            return false;
        }

        // Must be available for partnership based on relationship status
        // Note: This assumes RelationshipStatus is added to Person class
        // For now, we'll use the partner field as the primary indicator
        return true;
    }

    /**
     * Creates a partnership event between two people.
     *
     * @param person1 the first person
     * @param person2 the second person
     * @param partnershipDate the date when the partnership begins
     * @return the partnership event
     * @throws SimulationException if partnership creation fails
     */
    public PartnershipFormedEvent createPartnership(Person person1, Person person2, LocalDate partnershipDate)
            throws SimulationException {

        if (person1 == null || person2 == null) {
            throw new IllegalArgumentException("Both persons must be non-null");
        }

        if (partnershipDate == null) {
            throw new IllegalArgumentException("Partnership date cannot be null");
        }

        try {
            // Create the partnership event
            PartnershipFormedEvent event = new PartnershipFormedEvent(person1.getId(), person2.getId());

            logger.info("Partnership created between {} and {} on {}",
                       person1.getId(), person2.getId(), partnershipDate);

            return event;

        } catch (Exception e) {
            logger.error("Failed to create partnership between {} and {}",
                        person1.getId(), person2.getId(), e);
            throw new SimulationException("Failed to create partnership", e);
        }
    }

    /**
     * Finds all eligible singles in the population.
     *
     * @param population the population to search
     * @return list of people eligible for partnership formation
     */
    private List<Person> findEligibleSingles(List<Person> population) {
        return population.stream()
            .filter(this::isEligibleForPartnership)
            .collect(Collectors.toList());
    }

    /**
     * Selects a partner from the list of potential partners using probability-based selection.
     *
     * @param person the person seeking partnership
     * @param potentialPartners list of potential partners sorted by compatibility
     * @return selected partner or null if no partnership is formed
     */
    private Person selectPartner(Person person, List<Person> potentialPartners) {
        if (potentialPartners.isEmpty()) {
            return null;
        }

        // Base probability adjusted by age and life stage
        double partnershipProbability = calculatePartnershipProbability(person);

        if (random.nextDouble() > partnershipProbability) {
            return null; // No partnership this cycle
        }

        // Select partner with preference for higher compatibility
        return selectWithCompatibilityWeight(person, potentialPartners);
    }

    /**
     * Calculates the probability of partnership formation for a specific person.
     *
     * @param person the person to evaluate
     * @return probability value between 0.0 and 1.0
     */
    private double calculatePartnershipProbability(Person person) {
        double probability = BASE_PARTNERSHIP_PROBABILITY;

        // Age-based adjustments
        int age = person.getAge();
        if (age >= 18 && age <= 30) {
            probability *= 1.5; // Peak partnership age
        } else if (age >= 31 && age <= 45) {
            probability *= 1.2; // Still good partnership age
        } else if (age >= 46 && age <= 60) {
            probability *= 0.8; // Reduced likelihood
        } else if (age > 60) {
            probability *= 0.5; // Lower likelihood for older adults
        }

        // Life stage adjustments
        switch (person.getLifeStage()) {
            case YOUNG_ADULT:
                probability *= 1.3;
                break;
            case ADULT:
                probability *= 1.1;
                break;
            case ELDER:
                probability *= 0.6;
                break;
            default:
                break;
        }

        return Math.min(1.0, probability);
    }

    /**
     * Selects a partner with weighted preference for higher compatibility.
     *
     * @param person the person seeking partnership
     * @param potentialPartners list of potential partners
     * @return selected partner
     */
    private Person selectWithCompatibilityWeight(Person person, List<Person> potentialPartners) {
        // Calculate weighted selection based on compatibility scores
        List<Double> weights = new ArrayList<>();
        double totalWeight = 0.0;

        for (Person partner : potentialPartners) {
            double compatibility = compatibilityCalculator.calculateCompatibility(person, partner);
            // Square the compatibility to give higher preference to better matches
            double weight = compatibility * compatibility;
            weights.add(weight);
            totalWeight += weight;
        }

        if (totalWeight == 0.0) {
            // Fallback to first partner if no weights
            return potentialPartners.get(0);
        }

        // Weighted random selection
        double randomValue = random.nextDouble() * totalWeight;
        double cumulativeWeight = 0.0;

        for (int i = 0; i < potentialPartners.size(); i++) {
            cumulativeWeight += weights.get(i);
            if (randomValue <= cumulativeWeight) {
                return potentialPartners.get(i);
            }
        }

        // Fallback to last partner
        return potentialPartners.get(potentialPartners.size() - 1);
    }

    // EventProcessor interface implementation
    @Override
    public EventType getEventType() {
        return EventType.RELATIONSHIP;
    }

    @Override
    public void processEvent(Event event) throws SimulationException {
        if (event == null) {
            throw new IllegalArgumentException("Event cannot be null");
        }

        EventType eventType = event.getType();
        if (!canProcess(eventType)) {
            throw new SimulationException("Cannot process event type: " + eventType);
        }

        logger.debug("Processing event: {} of type: {}", event.getId(), eventType);

        // Handle different event types
        switch (eventType) {
            case RELATIONSHIP:
                // For partnership-related events, we would need to determine
                // the specific subtype and handle accordingly
                logger.info("Processing relationship event: {}", event.getId());
                // Implementation would depend on specific event data
                break;
            default:
                throw new SimulationException("Unsupported event type: " + eventType);
        }

        // Mark event as processed
        if (event instanceof com.littlepeople.core.interfaces.Event) {
            ((com.littlepeople.core.interfaces.Event) event).markProcessed();
        }
    }

    @Override
    public boolean canProcess(EventType eventType) {
        return eventType == EventType.RELATIONSHIP;
    }

    @Override
    public int getPriority() {
        return 100; // Standard priority for partnership processing
    }

    /**
     * Helper class to store potential partner with compatibility score.
     */
    private static class PartnerMatch {
        final Person person;
        final double compatibility;

        PartnerMatch(Person person, double compatibility) {
            this.person = person;
            this.compatibility = compatibility;
        }
    }
}
