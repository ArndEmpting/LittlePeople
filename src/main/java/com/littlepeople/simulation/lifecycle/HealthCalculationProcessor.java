package com.littlepeople.simulation.lifecycle;

import com.littlepeople.core.exceptions.SimulationException;
import com.littlepeople.core.interfaces.Event;
import com.littlepeople.core.interfaces.EventProcessor;
import com.littlepeople.core.interfaces.EventScheduler;
import com.littlepeople.core.model.EventProcessorRegistry;
import com.littlepeople.core.model.HealthStatus;
import com.littlepeople.core.model.Person;
import com.littlepeople.core.model.events.HealthCalculationEvent;
import com.littlepeople.core.model.events.HealthChangedEvent;
import com.littlepeople.core.processors.AbstractEventProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.util.Random;

/**
 * Processor that listens for HealthCalculationEvent and calculates new health status for each person,
 * generating HealthChangedEvent for each person whose health status should change.
 */
public class HealthCalculationProcessor extends AbstractEventProcessor implements EventProcessor {

    private static final Logger logger = LoggerFactory.getLogger(HealthCalculationProcessor.class);

    private final Random random;

    // Health transition probabilities (adjustable parameters)
    private double baseHealthDeclineRate = 0.02 / 12;  // 2% base chance per cycle
    private double baseHealthImprovementRate = 0.15/ 12; // 15% chance to improve when sick
    private double ageHealthDeclineMultiplier = 0.001/ 12; // Additional decline per year of age
    private double criticalIllnessRecoveryRate = 0.05/ 12; // 5% chance to recover from critical illness
private EventScheduler eventScheduler;
    /**
     * Creates a new HealthCalculationProcessor.
     */
    public HealthCalculationProcessor(EventScheduler eventScheduler) {
        super(HealthCalculationEvent.class);
        this.random = new Random();
         this.eventScheduler = eventScheduler;
    }

    /**
     * Creates a new HealthCalculationProcessor with a custom random seed.
     *
     * @param randomSeed the seed for random number generation
     */
    public HealthCalculationProcessor(long randomSeed) {
        super(HealthCalculationEvent.class);
        this.random = new Random(randomSeed);
        logger.info("Initialized HealthCalculationProcessor with seed {}", randomSeed);
    }

    @Override
    public void processEvent(Event event) throws SimulationException {
        if (event instanceof HealthCalculationEvent) {
            HealthCalculationEvent calcEvent = (HealthCalculationEvent) event;
            processHealthCalculations(calcEvent.getPopulation(), calcEvent.getCurrentDate());
        }
    }

    private void processHealthCalculations(java.util.List<Person> population, LocalDate currentDate)
            throws SimulationException {

        if (population == null) {
            throw new IllegalArgumentException("Population cannot be null");
        }

        if (currentDate == null) {
            throw new IllegalArgumentException("Current date cannot be null");
        }

        logger.debug("Processing health calculations for {} inhabitants on {}",
                population.size(), currentDate);

        int healthCalculations = 0;
        int errorCount = 0;

        for (Person person : population) {
            if (person != null && person.isAlive()) {
                try {
                    HealthStatus oldHealth = person.getHealthStatus();
                    HealthStatus newHealth = calculateNewHealthStatus(person, currentDate);

                    if (!oldHealth.equals(newHealth)) {
                        // Create HealthChangedEvent for this person
                        HealthChangedEvent healthEvent = new HealthChangedEvent(
                            person.getId(), newHealth, oldHealth);

                        // TODO: Send event to event dispatcher/queue
                         eventScheduler.scheduleEvent(healthEvent);

                        healthCalculations++;
                        logger.debug("Created health change event for person {} from {} to {}",
                                person.getId(), oldHealth, newHealth);
                    }

                } catch (Exception e) {
                    logger.error("Error calculating health for person: {}", person.getId(), e);
                    errorCount++;
                    // Continue with other people even if one fails
                }
            }
        }

        logger.info("Created {} health change events out of {} inhabitants ({} errors)",
                healthCalculations, population.size(), errorCount);
    }

    private HealthStatus calculateNewHealthStatus(Person person, LocalDate currentDate) {
        if (person == null) {
            throw new IllegalArgumentException("Person cannot be null");
        }

        if (!person.isAlive()) {
            return person.getHealthStatus(); // No health changes for deceased
        }

        HealthStatus currentHealth = person.getHealthStatus();
        int age = person.getAge();

        // Calculate probabilities based on current health and age
        double declineProbability = calculateHealthDeclineProbability(person);
        double improvementProbability = calculateHealthImprovementProbability(person);

        double roll = random.nextDouble();

        // Process health transitions
        switch (currentHealth) {
            case EXCELLENT:
                // Excellent health rarely declines
                if (roll < declineProbability * 0.5) {
                    logger.trace("Person {} (age {}) declined from excellent to healthy", person.getId(), age);
                    return HealthStatus.HEALTHY;
                }
                break;
            case HEALTHY:
                if (roll < improvementProbability * 0.1) {
                    logger.trace("Person {} (age {}) improved from healthy to excellent", person.getId(), age);
                    return HealthStatus.EXCELLENT;
                } else if (roll < declineProbability) {
                    // Decline from healthy - mostly to sick, rarely to critically ill
                    if (random.nextDouble() < 0.9) {
                        logger.trace("Person {} (age {}) became sick", person.getId(), age);
                        return HealthStatus.SICK;
                    } else {
                        logger.trace("Person {} (age {}) became critically ill", person.getId(), age);
                        return HealthStatus.CRITICALLY_ILL;
                    }
                }
                break;

            case SICK:
                if (roll < improvementProbability) {
                    logger.trace("Person {} (age {}) recovered to healthy", person.getId(), age);
                    return HealthStatus.HEALTHY;
                } else if (roll < declineProbability + 0.01) { // Small chance to worsen
                    logger.trace("Person {} (age {}) became critically ill", person.getId(), age);
                    return HealthStatus.CRITICALLY_ILL;
                }
                break;

            case CRITICALLY_ILL:
                if (roll < criticalIllnessRecoveryRate) {
                    // Recovery from critical illness - usually to sick, rarely to healthy
                    if (random.nextDouble() < 0.8) {
                        logger.trace("Person {} (age {}) improved to sick", person.getId(), age);
                        return HealthStatus.SICK;
                    } else {
                        logger.trace("Person {} (age {}) recovered to healthy", person.getId(), age);
                        return HealthStatus.HEALTHY;
                    }
                }
                break;

            default:
                // Unknown status should be resolved - default to healthy for young, sick for old
                if (age < 50) {
                    return HealthStatus.HEALTHY;
                } else {
                    return HealthStatus.SICK;
                }
        }

        return currentHealth; // No change
    }

    private double calculateHealthDeclineProbability(Person person) {
        if (person == null || !person.isAlive()) {
            return 0.0;
        }

        int age = person.getAge();
        HealthStatus currentHealth = person.getHealthStatus();

        // Base decline rate increases with age
        double probability = baseHealthDeclineRate + (age * ageHealthDeclineMultiplier);

        // Adjust based on current health status
        switch (currentHealth) {
            case HEALTHY:
                // Base probability as calculated
                break;
            case SICK:
                probability *= 1.5; // Sick people more likely to worsen
                break;
            case CRITICALLY_ILL:
                probability = 0.0; // Can't decline further
                break;
            default :
                probability *= 0.5; // Unknown status, moderate risk
                break;
        }

        // Age-based modifiers
        if (age >= 80) {
            probability *= 3.0; // Much higher decline rate for elderly
        } else if (age >= 60) {
            probability *= 2.0; // Higher decline rate for seniors
        } else if (age <= 5) {
            probability *= 1.5; // Young children more vulnerable
        }

        return Math.min(probability, 0.3); // Cap at 30% per cycle
    }

    private double calculateHealthImprovementProbability(Person person) {
        if (person == null || !person.isAlive()) {
            return 0.0;
        }

        int age = person.getAge();
        HealthStatus currentHealth = person.getHealthStatus();

        double probability;

        // Base improvement rate depends on current health
        switch (currentHealth) {
            case HEALTHY:
                probability = 0.0; // Can't improve from healthy
                break;
            case SICK:
                probability = baseHealthImprovementRate;
                break;
            case CRITICALLY_ILL:
                probability = criticalIllnessRecoveryRate;
                break;
            default:
                probability = 0.0;
        }

        // Age-based modifiers for recovery
        if (age <= 18) {
            probability *= 1.5; // Young people recover faster
        } else if (age >= 70) {
            probability *= 0.5; // Elderly recover slower
        } else if (age >= 50) {
            probability *= 0.8; // Middle-aged recover slightly slower
        }

        return Math.min(probability, 0.5); // Cap at 50% per cycle
    }

    @Override
    public int getPriority() {
        // Higher priority than the health change processor to ensure calculation happens first
        return 700;
    }

    // Configuration methods
    public double getBaseHealthDeclineRate() {
        return baseHealthDeclineRate;
    }

    public void setBaseHealthDeclineRate(double rate) {
        if (rate < 0.0 || rate > 1.0) {
            throw new IllegalArgumentException("Health decline rate must be between 0.0 and 1.0");
        }
        this.baseHealthDeclineRate = rate;
    }

    public double getBaseHealthImprovementRate() {
        return baseHealthImprovementRate;
    }

    public void setBaseHealthImprovementRate(double rate) {
        if (rate < 0.0 || rate > 1.0) {
            throw new IllegalArgumentException("Health improvement rate must be between 0.0 and 1.0");
        }
        this.baseHealthImprovementRate = rate;
    }

    public double getAgeHealthDeclineMultiplier() {
        return ageHealthDeclineMultiplier;
    }

    public void setAgeHealthDeclineMultiplier(double multiplier) {
        if (multiplier < 0.0) {
            throw new IllegalArgumentException("Age decline multiplier must be non-negative");
        }
        this.ageHealthDeclineMultiplier = multiplier;
    }

    public double getCriticalIllnessRecoveryRate() {
        return criticalIllnessRecoveryRate;
    }

    public void setCriticalIllnessRecoveryRate(double rate) {
        if (rate < 0.0 || rate > 1.0) {
            throw new IllegalArgumentException("Critical illness recovery rate must be between 0.0 and 1.0");
        }
        this.criticalIllnessRecoveryRate = rate;
    }
}
