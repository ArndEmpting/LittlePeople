package com.littlepeople.simulation.lifecycle;

import com.littlepeople.core.exceptions.SimulationException;
import com.littlepeople.core.interfaces.Event;
import com.littlepeople.core.interfaces.EventScheduler;
import com.littlepeople.core.model.DeathCause;
import com.littlepeople.core.model.EventType;
import com.littlepeople.core.model.HealthStatus;
import com.littlepeople.core.model.Person;
import com.littlepeople.core.model.events.TimeChangeEvent;
import com.littlepeople.core.model.events.PersonDeathEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Default implementation of the MortalityProcessor interface.
 * Handles mortality calculations and death event processing.
 *
 * @author LittlePeople Development Team
 * @version 1.0
 * @since 1.0
 */
public class DefaultMortalityProcessor implements MortalityProcessor {

    private static final Logger logger = LoggerFactory.getLogger(DefaultMortalityProcessor.class);

    private final Random random;
    private MortalityModel mortalityModel;
    private final EventScheduler eventScheduler;

    /**
     * Creates a new DefaultMortalityProcessor.
     */
    public DefaultMortalityProcessor(EventScheduler eventScheduler) {
        this.random = new Random();
        this.mortalityModel = new RealisticMortalityModel();
        this.eventScheduler = eventScheduler;

        logger.info("Initialized DefaultMortalityProcessor with {}",
                mortalityModel.getModelName());
    }

    /**
     * Creates a new DefaultMortalityProcessor with a custom random seed.
     *
     * @param randomSeed the seed for random number generation
     */
    public DefaultMortalityProcessor(long randomSeed, EventScheduler eventScheduler) {
        this.random = new Random(randomSeed);
        this.mortalityModel = new RealisticMortalityModel();
        this.eventScheduler = eventScheduler;

        logger.info("Initialized DefaultMortalityProcessor with seed {} and {}",
                randomSeed, mortalityModel.getModelName());
    }

    @Override
    public EventType getEventType() {
        return EventType.LIFECYCLE;
    }

    @Override
    public boolean canProcess(EventType eventType) {
        return eventType == EventType.LIFECYCLE;
    }

    @Override
    public void processEvent(Event event) throws SimulationException {
        if (event instanceof TimeChangeEvent) {
            TimeChangeEvent timeEvent = (TimeChangeEvent) event;
            processTimeChange(timeEvent);
        } else {
            logger.warn("Received unsupported event type: {}", event.getClass().getSimpleName());
        }
    }

    /**
     * Processes a time change event by checking mortality for all affected persons.
     */
    private void processTimeChange(TimeChangeEvent timeEvent) throws SimulationException {
        logger.debug("Processing time change from {} to {}",
                    timeEvent.getPreviousDate(), timeEvent.getNewDate());

        int deathCount = 0;
        int errorCount = 0;

        for (Person person : timeEvent.getAffectedPersonIds()) {

            if (person != null && person.isAlive() && shouldDie(person)) {
                try {
                    // Determine cause of death
                    DeathCause cause = determineDeathCause(person);

                    // Create and schedule death event instead of direct change
                    PersonDeathEvent deathEvent = new PersonDeathEvent(
                        person.getId(),
                        timeEvent.getNewDate(),
                        cause
                    );

                    eventScheduler.scheduleEvent(deathEvent);
                    deathCount++;

                    logger.debug("Scheduled death event for person {} at age {} from {}",
                            person.getId(), person.getAge(), cause);

                } catch (Exception e) {
                    logger.error("Error processing death for person: {}", person.getId(), e);
                    errorCount++;
                }
            }
        }

        logger.info("Scheduled {} death events out of {} persons on {} ({} errors)",
                deathCount, timeEvent.getAffectedPersonIds().size(), timeEvent.getNewDate(), errorCount);

        timeEvent.markProcessed();
    }

    @Override
    public int getPriority() {
        // Medium-high priority, should run after aging but before other processors
        return 800;
    }

    @Override
    public double calculateDeathProbability(Person person) {
        if (person == null) {
            throw new IllegalArgumentException("Person cannot be null");
        }

        if (!person.isAlive()) {
            return 0.0; // Already dead
        }

        // Get base probability from mortality model
        double baselineProbability = mortalityModel.calculateBaselineProbability(person.getAge());

        // Adjust for health status if available
        HealthStatus healthStatus = getPersonHealthStatus(person);
        double adjustedProbability = mortalityModel.adjustForHealth(
                baselineProbability, healthStatus);

        logger.trace("Death probability for {} (age {}): baseline={}, adjusted={}",
                person.getId(), person.getAge(), baselineProbability, adjustedProbability);

        return adjustedProbability;
    }

    @Override
    public boolean shouldDie(Person person) {
        if (person == null || !person.isAlive()) {
            return false;
        }

        double probability = calculateDeathProbability(person);
        double roll = random.nextDouble(); // Random value between 0 and 1

        boolean result = roll < probability;

        if (result) {
            logger.debug("Person {} (age {}) will die (probability: {:.6f}, roll: {:.6f})",
                    person.getId(), person.getAge(), probability, roll);
        }

        return result;
    }

    @Override
    public void processMortality(List<Person> population, LocalDate currentDate)
            throws SimulationException {

        if (population == null) {
            throw new IllegalArgumentException("Population cannot be null");
        }

        if (currentDate == null) {
            throw new IllegalArgumentException("Current date cannot be null");
        }

        logger.debug("Processing mortality for {} inhabitants on {}",
                population.size(), currentDate);

        int deathCount = 0;
        int errorCount = 0;

        for (Person person : population) {
            if (person != null && person.isAlive() && shouldDie(person)) {
                try {
                    // Determine cause of death
                    DeathCause cause = determineDeathCause(person);

                    // Mark the person as deceased
                    markPersonDeceased(person, currentDate);

                    deathCount++;

                    logger.debug("Person {} died at age {} from {}",
                            person.getId(), person.getAge(), cause);

                    // Note: In a full implementation, we would publish DeathEvent here

                } catch (Exception e) {
                    logger.error("Error processing death for person: {}",
                            person != null ? person.getId() : "null", e);
                    errorCount++;
                    // Continue with other people even if one fails
                }
            }
        }

        logger.info("Processed {} deaths out of {} inhabitants on {} ({} errors)",
                deathCount, population.size(), currentDate, errorCount);
    }

    @Override
    public DeathCause determineDeathCause(Person person) {
        if (person == null) {
            throw new IllegalArgumentException("Person cannot be null");
        }

        int age = person.getAge();
        HealthStatus health = getPersonHealthStatus(person);

        // Determine cause based on age and health
        if (age == 0) {
            return DeathCause.INFANT_MORTALITY;
        } else if (age >= 70 ) {
            return DeathCause.NATURAL_OLD_AGE;
        } else if (health == HealthStatus.CRITICALLY_ILL) {
            return DeathCause.DISEASE;
        } else if (health == HealthStatus.SICK) {
            // 70% disease, 30% accident for sick people
            return random.nextDouble() < 0.7 ? DeathCause.DISEASE : DeathCause.ACCIDENT;
        } else {
            // Healthy but died - most likely accident (90%) or undetected disease (10%)
            return random.nextDouble() < 0.9 ? DeathCause.ACCIDENT : DeathCause.DISEASE;
        }
    }

    @Override
    public void setMortalityModel(MortalityModel model) {
        this.mortalityModel = Objects.requireNonNull(model, "Mortality model cannot be null");
        logger.info("Set mortality model to: {}", model.getModelName());
    }

    @Override
    public MortalityModel getMortalityModel() {
        return mortalityModel;
    }

    /**
     * Gets the health status of a person.
     *
     * @param person the person
     * @return the health status from the person's current health status
     */
    private HealthStatus getPersonHealthStatus(Person person) {
        return person.getHealthStatus();
    }

    /**
     * This method is now deprecated - use event-driven approach instead.
     * @deprecated Use processEvent(TimeChangeEvent) instead
     */
    @Deprecated
    private void markPersonDeceased(Person person, LocalDate deathDate) {
        logger.warn("Direct markPersonDeceased called - this should use events instead");
        // This method should no longer be used in the event-driven architecture
    }
}
