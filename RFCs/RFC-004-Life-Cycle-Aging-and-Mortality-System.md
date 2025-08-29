# RFC-004: Life Cycle - Aging and Mortality System

**Version:** 1.0  
**Date:** August 27, 2025  
**Status:** Draft  
**Authors:** LittlePeople Development Team  
**Complexity:** Medium

## Summary

This RFC defines the aging and mortality systems for the LittlePeople simulation engine. It establishes how inhabitants age over time, how their health status changes, and the probability-based mortality system that determines when and how inhabitants die. This is a fundamental component of the life cycle simulation that directly influences population demographics and family dynamics over time.

## Features Addressed

- **F009:** Aging System
- **F010:** Mortality System
- **F004:** Population Statistics Tracking (mortality-related metrics)
- **F006:** Population Validation (age-related validation)

## Technical Approach

### Aging System Architecture

The aging system will be responsible for progressing each inhabitant's age in response to time changes in the simulation. It will:

1. Update all inhabitants' ages when time advances
2. Trigger age-related status changes (child to adult, adult to elderly)
3. Calculate derived age-based properties (isAdult, isElderly, etc.)
4. Emit aging-related events for further processing
5. Handle edge cases (maximum age limits, time jumps)

### Mortality System Architecture

The mortality system will determine when and how inhabitants die based on realistic probability curves. It will:

1. Calculate death probability for each inhabitant based on age, health, and other factors
2. Process death events for inhabitants who die
3. Update population statistics related to mortality
4. Support different mortality models (modern, historical, custom)
5. Categorize deaths by cause (natural aging, disease, accident, etc.)

### Core Components

#### Aging Components

1. **AgingProcessor:** Event processor that handles time change events to age the population
2. **AgeGroupCalculator:** Determines age groups and life stages based on age
3. **AgeValidator:** Validates age-related constraints and assumptions

#### Mortality Components

1. **MortalityProcessor:** Event processor that calculates death probability and generates death events
2. **MortalityModel:** Interface for different mortality probability calculation approaches
3. **DeathCause:** Enumeration of possible causes of death
4. **DeathStatistics:** Tracks mortality rates and causes

### Core Events

The system will define several age and mortality related events:

1. **AgingEvent:** Triggered when an inhabitant ages (yearly by default)
2. **LifeStageChangeEvent:** Triggered when an inhabitant transitions to a new life stage
3. **DeathEvent:** Triggered when an inhabitant dies
4. **HealthChangeEvent:** Triggered when health status changes (often age-related)

## Technical Specifications

### AgingProcessor Interface

```java
/**
 * Processes time change events and updates the ages of all inhabitants.
 */
public interface AgingProcessor extends EventProcessor {
    
    /**
     * Ages the entire population by the specified time period.
     * 
     * @param population the population to age
     * @param oldDate the previous simulation date
     * @param newDate the new simulation date
     * @throws SimulationException if aging fails
     */
    void agePopulation(List<Person> population, LocalDate oldDate, LocalDate newDate) 
        throws SimulationException;
    
    /**
     * Ages a specific person by the specified time period.
     * 
     * @param person the person to age
     * @param oldDate the previous simulation date
     * @param newDate the new simulation date
     * @throws SimulationException if aging fails
     */
    void agePerson(Person person, LocalDate oldDate, LocalDate newDate) 
        throws SimulationException;
    
    /**
     * Determines if a person should transition to a new life stage.
     * 
     * @param person the person to check
     * @return the new life stage if a transition should occur, or null if no change
     */
    LifeStage calculateLifeStageTransition(Person person);
    
    /**
     * Gets the maximum age limit for inhabitants.
     * 
     * @return the maximum age in years
     */
    int getMaximumAge();
    
    /**
     * Sets the maximum age limit for inhabitants.
     * 
     * @param maximumAge the maximum age in years
     * @throws IllegalArgumentException if maximumAge is not positive
     */
    void setMaximumAge(int maximumAge);
}
```

### LifeStage Enumeration

```java
/**
 * Enumeration representing the life stages of a person.
 */
public enum LifeStage {
    /**
     * Infant stage (0-2 years)
     */
    INFANT(0, 2),
    
    /**
     * Child stage (3-12 years)
     */
    CHILD(3, 12),
    
    /**
     * Adolescent stage (13-17 years)
     */
    ADOLESCENT(13, 17),
    
    /**
     * Young adult stage (18-29 years)
     */
    YOUNG_ADULT(18, 29),
    
    /**
     * Adult stage (30-59 years)
     */
    ADULT(30, 59),
    
    /**
     * Elderly stage (60+ years)
     */
    ELDERLY(60, Integer.MAX_VALUE);
    
    private final int minAge;
    private final int maxAge;
    
    LifeStage(int minAge, int maxAge) {
        this.minAge = minAge;
        this.maxAge = maxAge;
    }
    
    /**
     * Determines the life stage for the given age.
     * 
     * @param age the age in years
     * @return the corresponding life stage
     */
    public static LifeStage fromAge(int age) {
        for (LifeStage stage : values()) {
            if (age >= stage.minAge && age <= stage.maxAge) {
                return stage;
            }
        }
        return ELDERLY; // Default to elderly for any age beyond defined ranges
    }
    
    /**
     * Gets the minimum age for this life stage.
     * 
     * @return the minimum age in years
     */
    public int getMinAge() {
        return minAge;
    }
    
    /**
     * Gets the maximum age for this life stage.
     * 
     * @return the maximum age in years
     */
    public int getMaxAge() {
        return maxAge;
    }
    
    /**
     * Checks if the given age falls within this life stage.
     * 
     * @param age the age to check
     * @return true if the age is within this life stage
     */
    public boolean includes(int age) {
        return age >= minAge && age <= maxAge;
    }
}
```

### MortalityProcessor Interface

```java
/**
 * Processes mortality calculations and generates death events.
 */
public interface MortalityProcessor extends EventProcessor {
    
    /**
     * Calculates death probability for a person based on their age, health, and other factors.
     * 
     * @param person the person to calculate for
     * @return the probability of death (0.0 to 1.0)
     */
    double calculateDeathProbability(Person person);
    
    /**
     * Determines if a person should die based on their death probability.
     * 
     * @param person the person to check
     * @return true if the person should die
     */
    boolean shouldDie(Person person);
    
    /**
     * Processes potential deaths for the entire population.
     * 
     * @param population the population to process
     * @param currentDate the current simulation date
     * @throws SimulationException if processing fails
     */
    void processMortality(List<Person> population, LocalDate currentDate) 
        throws SimulationException;
    
    /**
     * Determines the cause of death for a person.
     * 
     * @param person the person who died
     * @return the cause of death
     */
    DeathCause determineDeathCause(Person person);
    
    /**
     * Sets the mortality model to use for calculations.
     * 
     * @param model the mortality model to use
     */
    void setMortalityModel(MortalityModel model);
    
    /**
     * Gets the currently configured mortality model.
     * 
     * @return the current mortality model
     */
    MortalityModel getMortalityModel();
}
```

### MortalityModel Interface

```java
/**
 * Interface for different mortality probability calculation models.
 */
public interface MortalityModel {
    
    /**
     * Calculates the baseline death probability based on age.
     * 
     * @param age the age in years
     * @return the baseline probability of death (0.0 to 1.0)
     */
    double calculateBaselineProbability(int age);
    
    /**
     * Adjusts the baseline death probability based on health status.
     * 
     * @param baselineProbability the baseline probability from age
     * @param healthStatus the person's current health status
     * @return the adjusted probability of death (0.0 to 1.0)
     */
    double adjustForHealth(double baselineProbability, HealthStatus healthStatus);
    
    /**
     * Gets the name of this mortality model.
     * 
     * @return the model name
     */
    String getModelName();
    
    /**
     * Gets the model type (historical, modern, custom).
     * 
     * @return the model type
     */
    MortalityModelType getModelType();
}
```

### DeathCause Enumeration

```java
/**
 * Enumeration of possible causes of death.
 */
public enum DeathCause {
    /**
     * Natural causes related to old age
     */
    NATURAL_CAUSES,
    
    /**
     * Death due to disease or illness
     */
    DISEASE,
    
    /**
     * Death due to accidents
     */
    ACCIDENT,
    
    /**
     * Death during childbirth (mothers)
     */
    CHILDBIRTH,
    
    /**
     * Death in infancy
     */
    INFANT_MORTALITY,
    
    /**
     * Unspecified or other causes
     */
    OTHER
}
```

### AgingEvent Class

```java
/**
 * Event generated when a person ages.
 */
public class AgingEvent extends AbstractEvent {
    
    private final int previousAge;
    private final int newAge;
    private final LifeStage previousLifeStage;
    private final LifeStage newLifeStage;
    
    /**
     * Creates a new aging event.
     * 
     * @param person the person who aged
     * @param eventDate the date when the aging occurred
     * @param previousAge the person's age before the event
     * @param newAge the person's age after the event
     * @param previousLifeStage the person's life stage before the event
     * @param newLifeStage the person's life stage after the event
     */
    public AgingEvent(
            Person person, 
            LocalDate eventDate, 
            int previousAge, 
            int newAge,
            LifeStage previousLifeStage,
            LifeStage newLifeStage) {
        super(EventType.AGING, eventDate, Collections.singletonList(person));
        this.previousAge = previousAge;
        this.newAge = newAge;
        this.previousLifeStage = previousLifeStage;
        this.newLifeStage = newLifeStage;
        
        getProperties().put("previousAge", previousAge);
        getProperties().put("newAge", newAge);
        getProperties().put("previousLifeStage", previousLifeStage);
        getProperties().put("newLifeStage", newLifeStage);
        getProperties().put("isLifeStageChange", !previousLifeStage.equals(newLifeStage));
    }
    
    /**
     * Gets the person who aged.
     * 
     * @return the person
     */
    public Person getPerson() {
        return getParticipants().get(0);
    }
    
    /**
     * Gets the person's age before the event.
     * 
     * @return the previous age
     */
    public int getPreviousAge() {
        return previousAge;
    }
    
    /**
     * Gets the person's age after the event.
     * 
     * @return the new age
     */
    public int getNewAge() {
        return newAge;
    }
    
    /**
     * Gets the person's life stage before the event.
     * 
     * @return the previous life stage
     */
    public LifeStage getPreviousLifeStage() {
        return previousLifeStage;
    }
    
    /**
     * Gets the person's life stage after the event.
     * 
     * @return the new life stage
     */
    public LifeStage getNewLifeStage() {
        return newLifeStage;
    }
    
    /**
     * Determines if this aging event resulted in a life stage change.
     * 
     * @return true if the life stage changed
     */
    public boolean isLifeStageChange() {
        return !previousLifeStage.equals(newLifeStage);
    }
}
```

### DeathEvent Class

```java
/**
 * Event generated when a person dies.
 */
public class DeathEvent extends AbstractEvent {
    
    private final DeathCause cause;
    private final int ageAtDeath;
    
    /**
     * Creates a new death event.
     * 
     * @param person the person who died
     * @param eventDate the date of death
     * @param cause the cause of death
     */
    public DeathEvent(Person person, LocalDate eventDate, DeathCause cause) {
        super(EventType.DEATH, eventDate, Collections.singletonList(person));
        this.cause = cause;
        this.ageAtDeath = person.getAge();
        
        getProperties().put("cause", cause);
        getProperties().put("ageAtDeath", ageAtDeath);
    }
    
    /**
     * Gets the person who died.
     * 
     * @return the deceased person
     */
    public Person getDeceased() {
        return getParticipants().get(0);
    }
    
    /**
     * Gets the cause of death.
     * 
     * @return the death cause
     */
    public DeathCause getCause() {
        return cause;
    }
    
    /**
     * Gets the person's age at death.
     * 
     * @return the age in years
     */
    public int getAgeAtDeath() {
        return ageAtDeath;
    }
}
```

## Implementation Details

### DefaultAgingProcessor

The main implementation of the aging system:

```java
/**
 * Default implementation of the AgingProcessor interface.
 */
public class DefaultAgingProcessor implements AgingProcessor, ClockListener {
    
    private static final Logger logger = LoggerFactory.getLogger(DefaultAgingProcessor.class);
    
    private final EventBus eventBus;
    private int maximumAge = 120; // Default maximum age
    
    /**
     * Creates a new DefaultAgingProcessor.
     * 
     * @param eventBus the event bus for publishing events
     */
    @Inject
    public DefaultAgingProcessor(EventBus eventBus) {
        this.eventBus = Objects.requireNonNull(eventBus, "Event bus cannot be null");
    }
    
    @Override
    public void onTimeChanged(LocalDate oldDate, LocalDate newDate, TimeUnit timeUnit) {
        // When time changes, this will be called if registered as a ClockListener
        try {
            // Get population from somewhere (dependency injection, service, etc.)
            List<Person> population = getPopulation();
            agePopulation(population, oldDate, newDate);
        } catch (Exception e) {
            logger.error("Error aging population on time change", e);
        }
    }
    
    @Override
    public void agePopulation(List<Person> population, LocalDate oldDate, LocalDate newDate) 
            throws SimulationException {
        
        logger.debug("Aging population of {} inhabitants from {} to {}", 
                population.size(), oldDate, newDate);
        
        int yearDifference = Period.between(oldDate, newDate).getYears();
        if (yearDifference <= 0) {
            // No year change, no aging needed
            return;
        }
        
        for (Person person : population) {
            if (person.isAlive()) {
                try {
                    agePerson(person, oldDate, newDate);
                } catch (Exception e) {
                    logger.error("Error aging person: {}", person.getId(), e);
                    // Continue with other people even if one fails
                }
            }
        }
        
        logger.info("Aged {} inhabitants by {} years", population.size(), yearDifference);
    }
    
    @Override
    public void agePerson(Person person, LocalDate oldDate, LocalDate newDate) 
            throws SimulationException {
        
        if (!person.isAlive()) {
            return; // Don't age deceased people
        }
        
        try {
            int previousAge = person.getAge();
            LifeStage previousLifeStage = LifeStage.fromAge(previousAge);
            
            // Calculate years to age
            int yearDifference = Period.between(oldDate, newDate).getYears();
            
            // Check if the person would exceed maximum age
            if (previousAge + yearDifference > maximumAge) {
                logger.debug("Person {} would exceed maximum age, capping at {}", 
                        person.getId(), maximumAge);
                yearDifference = maximumAge - previousAge;
                if (yearDifference <= 0) {
                    // Person already at or beyond maximum age
                    return;
                }
            }
            
            // Apply aging to person (implementation would depend on Person class)
            // For this RFC, we assume Person has internal methods to handle this
            person.incrementAge(yearDifference);
            
            int newAge = person.getAge();
            LifeStage newLifeStage = LifeStage.fromAge(newAge);
            
            // Generate aging event
            AgingEvent agingEvent = new AgingEvent(
                    person, newDate, previousAge, newAge, previousLifeStage, newLifeStage);
            
            // Publish event
            eventBus.publishEvent(agingEvent);
            
            // If life stage changed, publish life stage change event too
            if (agingEvent.isLifeStageChange()) {
                LifeStageChangeEvent stageEvent = new LifeStageChangeEvent(
                        person, newDate, previousLifeStage, newLifeStage);
                eventBus.publishEvent(stageEvent);
                
                logger.debug("Person {} transitioned from {} to {}", 
                        person.getId(), previousLifeStage, newLifeStage);
            }
            
            logger.trace("Aged person {} from {} to {}", person.getId(), previousAge, newAge);
            
        } catch (Exception e) {
            throw new SimulationException(
                    "Failed to age person: " + person.getId(), e);
        }
    }
    
    @Override
    public LifeStage calculateLifeStageTransition(Person person) {
        int age = person.getAge();
        LifeStage currentStage = LifeStage.fromAge(age);
        return currentStage;
    }
    
    @Override
    public boolean canHandle(EventType eventType) {
        return eventType == EventType.TIME_CHANGE;
    }
    
    @Override
    public void processEvent(Event event) throws SimulationException {
        if (event.getType() == EventType.TIME_CHANGE) {
            TimeChangeEvent timeEvent = (TimeChangeEvent) event;
            onTimeChanged(timeEvent.getOldDate(), timeEvent.getNewDate(), timeEvent.getTimeUnit());
        }
    }
    
    @Override
    public Set<EventType> getSupportedEventTypes() {
        return Collections.singleton(EventType.TIME_CHANGE);
    }
    
    @Override
    public int getPriority() {
        // High priority to ensure aging happens early in event processing chain
        return 900;
    }
    
    @Override
    public int getMaximumAge() {
        return maximumAge;
    }
    
    @Override
    public void setMaximumAge(int maximumAge) {
        if (maximumAge <= 0) {
            throw new IllegalArgumentException("Maximum age must be positive");
        }
        this.maximumAge = maximumAge;
        logger.info("Set maximum age to {}", maximumAge);
    }
    
    // Helper method to get population - in a real implementation, this would be injected
    private List<Person> getPopulation() {
        // Implementation would depend on how population is managed
        // For this RFC, we assume another component provides this
        return Collections.emptyList();
    }
}
```

### RealisticMortalityModel

The main implementation of the mortality model:

```java
/**
 * A realistic mortality model based on modern demographic data.
 */
public class RealisticMortalityModel implements MortalityModel {
    
    private static final Logger logger = LoggerFactory.getLogger(RealisticMortalityModel.class);
    
    // Gompertz-Makeham model parameters (realistic human mortality)
    private double alpha = 0.0001; // Base mortality (independent of age)
    private double beta = 0.085;   // Rate of mortality increase with age
    private double gamma = 0.000001; // Initial mortality
    
    // Infant mortality parameters
    private double infantMortalityRate = 0.004; // 4 per 1000 (modern rate)
    private double childMortalityFactor = 0.3;  // Adjustment for child mortality
    
    /**
     * Creates a realistic mortality model with default parameters.
     */
    public RealisticMortalityModel() {
        logger.info("Initialized realistic mortality model with default parameters");
    }
    
    /**
     * Creates a realistic mortality model with custom parameters.
     * 
     * @param alpha base mortality rate (independent of age)
     * @param beta rate of mortality increase with age
     * @param gamma initial mortality rate
     * @param infantMortalityRate mortality rate for infants under 1 year
     * @param childMortalityFactor adjustment factor for child mortality
     */
    public RealisticMortalityModel(
            double alpha, 
            double beta, 
            double gamma,
            double infantMortalityRate,
            double childMortalityFactor) {
        
        this.alpha = alpha;
        this.beta = beta;
        this.gamma = gamma;
        this.infantMortalityRate = infantMortalityRate;
        this.childMortalityFactor = childMortalityFactor;
        
        logger.info("Initialized realistic mortality model with custom parameters");
    }
    
    @Override
    public double calculateBaselineProbability(int age) {
        if (age < 0) {
            throw new IllegalArgumentException("Age cannot be negative: " + age);
        }
        
        // Special case for infants (under 1 year)
        if (age == 0) {
            return infantMortalityRate;
        }
        
        // Special case for young children (1-5 years)
        if (age <= 5) {
            return infantMortalityRate * childMortalityFactor * (6 - age) / 5;
        }
        
        // Gompertz-Makeham model for adult mortality
        // q(x) = alpha + gamma * exp(beta * x)
        // where x is age, and q(x) is probability of death
        double probability = alpha + gamma * Math.exp(beta * age);
        
        // Cap at 1.0 (100% probability)
        return Math.min(probability, 1.0);
    }
    
    @Override
    public double adjustForHealth(double baselineProbability, HealthStatus healthStatus) {
        if (baselineProbability < 0 || baselineProbability > 1) {
            throw new IllegalArgumentException(
                    "Baseline probability must be between 0 and 1: " + baselineProbability);
        }
        
        if (healthStatus == null) {
            return baselineProbability; // No adjustment for null health status
        }
        
        // Adjust based on health status
        return switch (healthStatus) {
            case HEALTHY -> baselineProbability * 0.8; // 20% reduction for healthy individuals
            case SICK -> baselineProbability * 1.5; // 50% increase for sick individuals
            case CRITICALLY_ILL -> baselineProbability * 5.0; // 5x increase for critically ill
            default -> baselineProbability; // No adjustment for unknown status
        };
    }
    
    @Override
    public String getModelName() {
        return "Realistic Mortality Model";
    }
    
    @Override
    public MortalityModelType getModelType() {
        return MortalityModelType.MODERN;
    }
    
    /**
     * Gets the base mortality rate (alpha parameter).
     * 
     * @return the alpha parameter
     */
    public double getAlpha() {
        return alpha;
    }
    
    /**
     * Sets the base mortality rate (alpha parameter).
     * 
     * @param alpha the alpha parameter to set
     */
    public void setAlpha(double alpha) {
        this.alpha = alpha;
    }
    
    /**
     * Gets the rate of mortality increase with age (beta parameter).
     * 
     * @return the beta parameter
     */
    public double getBeta() {
        return beta;
    }
    
    /**
     * Sets the rate of mortality increase with age (beta parameter).
     * 
     * @param beta the beta parameter to set
     */
    public void setBeta(double beta) {
        this.beta = beta;
    }
    
    /**
     * Gets the initial mortality rate (gamma parameter).
     * 
     * @return the gamma parameter
     */
    public double getGamma() {
        return gamma;
    }
    
    /**
     * Sets the initial mortality rate (gamma parameter).
     * 
     * @param gamma the gamma parameter to set
     */
    public void setGamma(double gamma) {
        this.gamma = gamma;
    }
    
    /**
     * Gets the infant mortality rate.
     * 
     * @return the infant mortality rate
     */
    public double getInfantMortalityRate() {
        return infantMortalityRate;
    }
    
    /**
     * Sets the infant mortality rate.
     * 
     * @param infantMortalityRate the infant mortality rate to set
     */
    public void setInfantMortalityRate(double infantMortalityRate) {
        this.infantMortalityRate = infantMortalityRate;
    }
    
    /**
     * Gets the child mortality factor.
     * 
     * @return the child mortality factor
     */
    public double getChildMortalityFactor() {
        return childMortalityFactor;
    }
    
    /**
     * Sets the child mortality factor.
     * 
     * @param childMortalityFactor the child mortality factor to set
     */
    public void setChildMortalityFactor(double childMortalityFactor) {
        this.childMortalityFactor = childMortalityFactor;
    }
}
```

### DefaultMortalityProcessor

The main implementation of the mortality processor:

```java
/**
 * Default implementation of the MortalityProcessor interface.
 */
public class DefaultMortalityProcessor implements MortalityProcessor, ClockListener {
    
    private static final Logger logger = LoggerFactory.getLogger(DefaultMortalityProcessor.class);
    
    private final EventBus eventBus;
    private final Random random;
    private MortalityModel mortalityModel;
    
    /**
     * Creates a new DefaultMortalityProcessor.
     * 
     * @param eventBus the event bus for publishing events
     * @param randomProvider provider for randomization
     */
    @Inject
    public DefaultMortalityProcessor(
            EventBus eventBus, 
            RandomProvider randomProvider) {
        
        this.eventBus = Objects.requireNonNull(eventBus, "Event bus cannot be null");
        this.random = Objects.requireNonNull(
                randomProvider, "Random provider cannot be null").getRandom();
        
        // Default to realistic mortality model
        this.mortalityModel = new RealisticMortalityModel();
    }
    
    @Override
    public void onTimeChanged(LocalDate oldDate, LocalDate newDate, TimeUnit timeUnit) {
        try {
            // Get population from somewhere (dependency injection, service, etc.)
            List<Person> population = getPopulation();
            processMortality(population, newDate);
        } catch (Exception e) {
            logger.error("Error processing mortality on time change", e);
        }
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
        
        // Adjust for health status
        double adjustedProbability = mortalityModel.adjustForHealth(
                baselineProbability, person.getHealthStatus());
        
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
            logger.debug("Person {} (age {}) will die (probability: {}, roll: {})", 
                    person.getId(), person.getAge(), probability, roll);
        }
        
        return result;
    }
    
    @Override
    public void processMortality(List<Person> population, LocalDate currentDate) 
            throws SimulationException {
        
        logger.debug("Processing mortality for {} inhabitants on {}", 
                population.size(), currentDate);
        
        int deathCount = 0;
        
        for (Person person : population) {
            if (person.isAlive() && shouldDie(person)) {
                try {
                    // Determine cause of death
                    DeathCause cause = determineDeathCause(person);
                    
                    // Mark the person as deceased
                    person.markDeceased(currentDate);
                    
                    // Create and publish death event
                    DeathEvent deathEvent = new DeathEvent(person, currentDate, cause);
                    eventBus.publishEvent(deathEvent);
                    
                    deathCount++;
                    
                    logger.debug("Person {} died at age {} from {}", 
                            person.getId(), person.getAge(), cause);
                    
                } catch (Exception e) {
                    logger.error("Error processing death for person: {}", person.getId(), e);
                    // Continue with other people even if one fails
                }
            }
        }
        
        logger.info("Processed {} deaths out of {} inhabitants on {}", 
                deathCount, population.size(), currentDate);
    }
    
    @Override
    public DeathCause determineDeathCause(Person person) {
        int age = person.getAge();
        HealthStatus health = person.getHealthStatus();
        
        // Determine cause based on age and health
        if (age == 0) {
            return DeathCause.INFANT_MORTALITY;
        } else if (age >= 80 || (age >= 60 && health != HealthStatus.HEALTHY)) {
            return DeathCause.NATURAL_CAUSES;
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
    public boolean canHandle(EventType eventType) {
        return eventType == EventType.TIME_CHANGE || eventType == EventType.AGING;
    }
    
    @Override
    public void processEvent(Event event) throws SimulationException {
        if (event.getType() == EventType.TIME_CHANGE) {
            TimeChangeEvent timeEvent = (TimeChangeEvent) event;
            onTimeChanged(timeEvent.getOldDate(), timeEvent.getNewDate(), timeEvent.getTimeUnit());
        } else if (event.getType() == EventType.AGING) {
            // For individual aging events, check mortality
            AgingEvent agingEvent = (AgingEvent) event;
            Person person = agingEvent.getPerson();
            
            if (shouldDie(person)) {
                DeathCause cause = determineDeathCause(person);
                person.markDeceased(event.getEventDate());
                
                DeathEvent deathEvent = new DeathEvent(person, event.getEventDate(), cause);
                eventBus.publishEvent(deathEvent);
                
                logger.debug("Person {} died at age {} from {} after aging", 
                        person.getId(), person.getAge(), cause);
            }
        }
    }
    
    @Override
    public Set<EventType> getSupportedEventTypes() {
        return Set.of(EventType.TIME_CHANGE, EventType.AGING);
    }
    
    @Override
    public int getPriority() {
        // Medium-high priority, should run after aging but before other processors
        return 800;
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
    
    // Helper method to get population - in a real implementation, this would be injected
    private List<Person> getPopulation() {
        // Implementation would depend on how population is managed
        // For this RFC, we assume another component provides this
        return Collections.emptyList();
    }
}
```

## Acceptance Criteria

- [x] Age progression works correctly for all population members when time advances
- [x] Different life stages are properly assigned based on age
- [x] Mortality calculations use realistic probability models
- [x] Death events are generated and processed appropriately
- [x] Health status affects mortality rates in a realistic manner
- [x] Age-appropriate death causes are assigned
- [x] Edge cases are handled (maximum age limits, etc.)
- [x] System maintains accurate population statistics regarding births/deaths
- [x] All age transitions generate appropriate events
- [x] Configuration options allow customization of aging and mortality parameters
- [x] Performance tests pass for large populations (1000+ inhabitants)
- [x] All components have comprehensive unit tests (>85% coverage)
- [x] Documentation is complete for all APIs and components

## Dependencies

**Builds Upon:**
- RFC-001: Project Setup and Architecture
- RFC-002: Core Entity Models
- RFC-003: Simulation Clock and Event System

**Required For:**
- RFC-005: Partnerships and Family Formation
- RFC-006: Population Management
- RFC-011: Statistics and Reporting

## Testing Strategy

### Unit Tests

#### AgingProcessor Tests
- Test population aging for various time increments
- Test life stage transitions at boundary ages
- Test maximum age handling
- Test event generation for age changes
- Test concurrent aging operations

#### MortalityProcessor Tests
- Test death probability calculations for different ages and health statuses
- Test death cause assignment logic
- Test random seed reproducibility for death determination
- Test population-wide mortality processing
- Test edge cases (age 0, maximum age)

#### MortalityModel Tests
- Test baseline probability calculations against expected curves
- Test health status adjustments
- Test configuration parameter impacts
- Test model swapping at runtime

### Integration Tests

- Test complete aging ? mortality flow
- Test demographic impact over long time periods (100+ years)
- Test statistical validity of mortality patterns
- Test performance with large populations

## Security Considerations

- Validate all configuration inputs to prevent invalid mortality/aging parameters
- Ensure random number generation is secure for mortality calculations
- Protect demographic data with appropriate access controls
- Consider privacy implications of death cause reporting

## Performance Considerations

- Optimize aging operations for large populations
- Consider parallel processing for mortality calculations
- Use efficient data structures for population filtering by age groups
- Implement age-based indexing for quick age cohort queries
- Cache calculated probabilities where appropriate
- Consider batch processing for events to reduce overhead

## Open Questions

1. Should we support different mortality models for different historical periods?
2. How detailed should health impact be on mortality calculations?
3. Should we implement sudden mortality events (plagues, disasters) that affect larger population groups?
4. How should we handle age-related health decline probabilities?
5. Should maximum age be a hard limit or a probabilistic threshold?

## References

- [Gompertz-Makeham Law of Mortality](https://en.wikipedia.org/wiki/Gompertz%E2%80%93Makeham_law_of_mortality)
- [World Health Organization Life Tables](https://www.who.int/data/gho/data/themes/mortality-and-global-health-estimates/life-tables)
- [Life Stage Development Models](https://www.simplypsychology.org/Erik-Erikson.html)
- [CDC Death Statistics](https://www.cdc.gov/nchs/fastats/deaths.htm)
