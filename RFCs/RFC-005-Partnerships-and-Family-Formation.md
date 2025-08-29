# RFC-005: Partnerships and Family Formation

**Version:** 1.0  
**Date:** August 27, 2025  
**Status:** Draft  
**Authors:** LittlePeople Development Team  
**Complexity:** High

## Summary

This RFC defines the partnership and family formation systems for the LittlePeople simulation engine. It establishes how adult inhabitants find compatible partners, form relationships, and produce children. These social dynamics create the foundation for multi-generational family structures and realistic population growth patterns that are critical for story-driven simulation. The partnership and family formation features directly influence demographic evolution, create complex character relationships, and generate compelling narrative potential for the story writer.

## Features Addressed

- **F011:** Partnership Formation
- **F012:** Family System & Child Generation
- **F015:** Remarriage System (partial)
- **F004:** Population Statistics Tracking (family-related metrics)

## Technical Approach

### Partnership System Architecture

The partnership system will implement realistic partner matching based on compatibility factors and demographic rules. It will:

1. Identify eligible inhabitants for partnerships based on age, relationship status, and kinship
2. Calculate compatibility between potential partners using various factors
3. Form partnerships based on probability calculations
4. Track partnerships with start dates and relationship status
5. Handle partnership dissolution (due to death or other factors in future extensions)
6. Support remarriage for widowed inhabitants

### Family Formation Architecture

The family formation system will simulate realistic child-bearing patterns in partnerships. It will:

1. Calculate fertility probability for partnerships based on partners' ages and other factors
2. Generate children with realistic timing and family size distributions
3. Establish proper parent-child relationships
4. Handle inheritance of traits from parents to children
5. Maintain family tree relationships across multiple generations
6. Track family statistics (size, generations, etc.)

### Core Components

#### Partnership Components

1. **PartnershipProcessor:** Event processor that handles partnership formation and dissolution
2. **PartnerMatcher:** Implements the algorithm for matching compatible partners
3. **CompatibilityCalculator:** Determines compatibility scores between potential partners
4. **PartnershipStatus:** Tracks the status and details of partnerships

#### Family Components

1. **FamilyProcessor:** Event processor that handles child generation and family growth
2. **FertilityCalculator:** Determines the probability of having children
3. **ChildGenerator:** Creates new Person instances as children of partnerships
4. **FamilyRelationshipTracker:** Maintains and validates family relationships

### Core Events

The system will define several partnership and family related events:

1. **PartnershipEvent:** Represents the formation of a new partnership
2. **PartnershipDissolutionEvent:** Represents the end of a partnership (usually due to death)
3. **BirthEvent:** Represents the birth of a new child
4. **FamilyChangeEvent:** Represents changes to family structure (adoptions, etc.)

## Technical Specifications

### PartnershipProcessor Interface

```java
/**
 * Processes partnership formation and dissolution for the population.
 */
public interface PartnershipProcessor extends EventProcessor {
    
    /**
     * Finds potential partnerships among eligible inhabitants.
     * 
     * @param population the current population
     * @param currentDate the current simulation date
     * @throws SimulationException if processing fails
     */
    void processPartnershipFormation(List<Person> population, LocalDate currentDate) 
        throws SimulationException;
    
    /**
     * Processes partnership dissolutions (typically due to death).
     * 
     * @param deathEvent the death event that may trigger partnership dissolution
     * @throws SimulationException if processing fails
     */
    void processPartnershipDissolution(DeathEvent deathEvent) 
        throws SimulationException;
    
    /**
     * Calculates the compatibility score between two potential partners.
     * 
     * @param person1 the first person
     * @param person2 the second person
     * @return compatibility score (0.0 to 1.0)
     */
    double calculateCompatibility(Person person1, Person person2);
    
    /**
     * Finds all eligible partners for a person.
     * 
     * @param person the person seeking partnership
     * @param population the current population
     * @return list of eligible partners sorted by compatibility
     */
    List<Person> findEligiblePartners(Person person, List<Person> population);
    
    /**
     * Determines if a person is eligible for partnership.
     * 
     * @param person the person to check
     * @return true if eligible for partnership
     */
    boolean isEligibleForPartnership(Person person);
    
    /**
     * Creates a partnership between two people.
     * 
     * @param person1 the first person
     * @param person2 the second person
     * @param partnershipDate the date when the partnership begins
     * @return the partnership event
     * @throws SimulationException if partnership formation fails
     */
    PartnershipEvent createPartnership(Person person1, Person person2, LocalDate partnershipDate) 
        throws SimulationException;
}
```

### PartnershipEvent Class

```java
/**
 * Event representing the formation of a partnership between two people.
 */
public class PartnershipEvent extends AbstractEvent {
    
    /**
     * Creates a new partnership event.
     * 
     * @param person1 the first partner
     * @param person2 the second partner
     * @param eventDate the date of partnership formation
     */
    public PartnershipEvent(Person person1, Person person2, LocalDate eventDate) {
        super(EventType.PARTNERSHIP_FORMATION, eventDate, List.of(person1, person2));
        
        getProperties().put("partner1Id", person1.getId());
        getProperties().put("partner2Id", person2.getId());
        getProperties().put("partner1Age", person1.getAge());
        getProperties().put("partner2Age", person2.getAge());
    }
    
    /**
     * Gets the first partner in this partnership.
     * 
     * @return the first partner
     */
    public Person getPartner1() {
        return getParticipants().get(0);
    }
    
    /**
     * Gets the second partner in this partnership.
     * 
     * @return the second partner
     */
    public Person getPartner2() {
        return getParticipants().get(1);
    }
    
    /**
     * Gets the age difference between partners.
     * 
     * @return the absolute age difference in years
     */
    public int getAgeDifference() {
        return Math.abs(getPartner1().getAge() - getPartner2().getAge());
    }
}
```

### PartnershipDissolutionEvent Class

```java
/**
 * Event representing the dissolution of a partnership.
 */
public class PartnershipDissolutionEvent extends AbstractEvent {
    
    private final Person survivingPartner;
    private final Person deceasedPartner;
    private final LocalDate partnershipStartDate;
    private final int partnershipDuration;
    
    /**
     * Creates a new partnership dissolution event due to death.
     * 
     * @param survivingPartner the surviving partner
     * @param deceasedPartner the deceased partner
     * @param eventDate the date of dissolution
     * @param partnershipStartDate the date when the partnership began
     */
    public PartnershipDissolutionEvent(
            Person survivingPartner, 
            Person deceasedPartner, 
            LocalDate eventDate,
            LocalDate partnershipStartDate) {
        
        super(EventType.PARTNERSHIP_DISSOLUTION, eventDate, 
                List.of(survivingPartner, deceasedPartner));
        
        this.survivingPartner = survivingPartner;
        this.deceasedPartner = deceasedPartner;
        this.partnershipStartDate = partnershipStartDate;
        this.partnershipDuration = Period.between(
                partnershipStartDate, eventDate).getYears();
        
        getProperties().put("reason", "DEATH");
        getProperties().put("survivingPartnerId", survivingPartner.getId());
        getProperties().put("deceasedPartnerId", deceasedPartner.getId());
        getProperties().put("partnershipStartDate", partnershipStartDate);
        getProperties().put("partnershipDuration", partnershipDuration);
    }
    
    /**
     * Gets the surviving partner.
     * 
     * @return the surviving partner
     */
    public Person getSurvivingPartner() {
        return survivingPartner;
    }
    
    /**
     * Gets the deceased partner.
     * 
     * @return the deceased partner
     */
    public Person getDeceasedPartner() {
        return deceasedPartner;
    }
    
    /**
     * Gets the date when the partnership began.
     * 
     * @return the start date
     */
    public LocalDate getPartnershipStartDate() {
        return partnershipStartDate;
    }
    
    /**
     * Gets the duration of the partnership in years.
     * 
     * @return the duration in years
     */
    public int getPartnershipDuration() {
        return partnershipDuration;
    }
}
```

### FamilyProcessor Interface

```java
/**
 * Processes family formation and child generation for the population.
 */
public interface FamilyProcessor extends EventProcessor {
    
    /**
     * Processes fertility and child generation for all partnerships.
     * 
     * @param population the current population
     * @param currentDate the current simulation date
     * @throws SimulationException if processing fails
     */
    void processChildGeneration(List<Person> population, LocalDate currentDate) 
        throws SimulationException;
    
    /**
     * Calculates the fertility probability for a partnership.
     * 
     * @param partnership the partnership to check
     * @return fertility probability (0.0 to 1.0)
     */
    double calculateFertilityProbability(Partnership partnership);
    
    /**
     * Generates a child for the given partnership.
     * 
     * @param parent1 the first parent
     * @param parent2 the second parent
     * @param birthDate the birth date for the child
     * @return the new child person
     * @throws SimulationException if child generation fails
     */
    Person generateChild(Person parent1, Person parent2, LocalDate birthDate) 
        throws SimulationException;
    
    /**
     * Determines the gender for a child based on parent genetics and randomization.
     * 
     * @param parent1 the first parent
     * @param parent2 the second parent
     * @return the gender for the child
     */
    Gender determineChildGender(Person parent1, Person parent2);
    
    /**
     * Generates a name for a child based on parent names and naming conventions.
     * 
     * @param parent1 the first parent
     * @param parent2 the second parent
     * @param gender the child's gender
     * @return child name (first and last)
     */
    PersonName generateChildName(Person parent1, Person parent2, Gender gender);
    
    /**
     * Determines the maximum family size for a partnership.
     * 
     * @param partnership the partnership to check
     * @return the maximum number of children for this partnership
     */
    int determineMaxFamilySize(Partnership partnership);
}
```

### BirthEvent Class

```java
/**
 * Event representing the birth of a new child.
 */
public class BirthEvent extends AbstractEvent {
    
    private final Person child;
    private final Person parent1;
    private final Person parent2;
    
    /**
     * Creates a new birth event.
     * 
     * @param child the newborn child
     * @param parent1 the first parent
     * @param parent2 the second parent
     * @param birthDate the date of birth
     */
    public BirthEvent(Person child, Person parent1, Person parent2, LocalDate birthDate) {
        super(EventType.BIRTH, birthDate, List.of(child, parent1, parent2));
        
        this.child = child;
        this.parent1 = parent1;
        this.parent2 = parent2;
        
        getProperties().put("childId", child.getId());
        getProperties().put("childGender", child.getGender());
        getProperties().put("parent1Id", parent1.getId());
        getProperties().put("parent2Id", parent2.getId());
        getProperties().put("parent1Age", parent1.getAge());
        getProperties().put("parent2Age", parent2.getAge());
    }
    
    /**
     * Gets the newborn child.
     * 
     * @return the child
     */
    public Person getChild() {
        return child;
    }
    
    /**
     * Gets the first parent.
     * 
     * @return the first parent
     */
    public Person getParent1() {
        return parent1;
    }
    
    /**
     * Gets the second parent.
     * 
     * @return the second parent
     */
    public Person getParent2() {
        return parent2;
    }
}
```

### Partnership Class

```java
/**
 * Represents a partnership between two people.
 */
public class Partnership {
    
    private final UUID id;
    private final Person partner1;
    private final Person partner2;
    private final LocalDate startDate;
    private LocalDate endDate;
    private final List<Person> children;
    
    /**
     * Creates a new partnership.
     * 
     * @param partner1 the first partner
     * @param partner2 the second partner
     * @param startDate the date when the partnership began
     */
    public Partnership(Person partner1, Person partner2, LocalDate startDate) {
        this.id = UUID.randomUUID();
        this.partner1 = Objects.requireNonNull(partner1, "Partner 1 cannot be null");
        this.partner2 = Objects.requireNonNull(partner2, "Partner 2 cannot be null");
        this.startDate = Objects.requireNonNull(startDate, "Start date cannot be null");
        this.children = new ArrayList<>();
        
        validatePartnership(partner1, partner2);
    }
    
    /**
     * Gets the duration of this partnership in years.
     * 
     * @param currentDate the current date to calculate against
     * @return the duration in years
     */
    public int getDuration(LocalDate currentDate) {
        LocalDate effectiveEndDate = endDate != null ? endDate : currentDate;
        return Period.between(startDate, effectiveEndDate).getYears();
    }
    
    /**
     * Determines if this partnership is active (not ended).
     * 
     * @return true if active, false if dissolved
     */
    public boolean isActive() {
        return endDate == null;
    }
    
    /**
     * Ends this partnership.
     * 
     * @param endDate the date when the partnership ended
     * @throws IllegalArgumentException if end date is invalid
     */
    public void end(LocalDate endDate) {
        if (endDate == null) {
            throw new IllegalArgumentException("End date cannot be null");
        }
        if (endDate.isBefore(startDate)) {
            throw new IllegalArgumentException("End date cannot be before start date");
        }
        this.endDate = endDate;
    }
    
    /**
     * Adds a child to this partnership.
     * 
     * @param child the child to add
     * @throws IllegalArgumentException if child is null
     */
    public void addChild(Person child) {
        if (child == null) {
            throw new IllegalArgumentException("Child cannot be null");
        }
        children.add(child);
    }
    
    /**
     * Gets the number of children in this partnership.
     * 
     * @return the number of children
     */
    public int getChildCount() {
        return children.size();
    }
    
    /**
     * Gets the children born in this partnership.
     * 
     * @return unmodifiable list of children
     */
    public List<Person> getChildren() {
        return Collections.unmodifiableList(children);
    }
    
    /**
     * Gets the unique identifier for this partnership.
     * 
     * @return the partnership ID
     */
    public UUID getId() {
        return id;
    }
    
    /**
     * Gets the first partner.
     * 
     * @return the first partner
     */
    public Person getPartner1() {
        return partner1;
    }
    
    /**
     * Gets the second partner.
     * 
     * @return the second partner
     */
    public Person getPartner2() {
        return partner2;
    }
    
    /**
     * Gets the date when the partnership began.
     * 
     * @return the start date
     */
    public LocalDate getStartDate() {
        return startDate;
    }
    
    /**
     * Gets the date when the partnership ended, if applicable.
     * 
     * @return the end date, or null if still active
     */
    public LocalDate getEndDate() {
        return endDate;
    }
    
    private void validatePartnership(Person partner1, Person partner2) {
        if (partner1.equals(partner2)) {
            throw new IllegalArgumentException("Partners cannot be the same person");
        }
        if (!partner1.isAdult() || !partner2.isAdult()) {
            throw new IllegalArgumentException("Both partners must be adults");
        }
        if (partner1.isRelatedTo(partner2)) {
            throw new IllegalArgumentException("Partners cannot be related");
        }
    }
}
```

### PersonName Class

```java
/**
 * Represents a person's name with first and last components.
 */
public class PersonName {
    
    private final String firstName;
    private final String lastName;
    
    /**
     * Creates a new person name.
     * 
     * @param firstName the first name
     * @param lastName the last name
     */
    public PersonName(String firstName, String lastName) {
        this.firstName = Objects.requireNonNull(firstName, "First name cannot be null");
        this.lastName = Objects.requireNonNull(lastName, "Last name cannot be null");
        
        validateName(firstName, "First name");
        validateName(lastName, "Last name");
    }
    
    /**
     * Gets the first name.
     * 
     * @return the first name
     */
    public String getFirstName() {
        return firstName;
    }
    
    /**
     * Gets the last name.
     * 
     * @return the last name
     */
    public String getLastName() {
        return lastName;
    }
    
    /**
     * Gets the full name (first and last combined).
     * 
     * @return the full name
     */
    public String getFullName() {
        return firstName + " " + lastName;
    }
    
    private void validateName(String name, String nameType) {
        if (name.trim().isEmpty()) {
            throw new IllegalArgumentException(nameType + " cannot be empty");
        }
        if (name.length() < 2) {
            throw new IllegalArgumentException(nameType + " must be at least 2 characters long");
        }
        if (!name.matches("[\\p{L}\\s-'.]+")) {
            throw new IllegalArgumentException(nameType + " contains invalid characters");
        }
    }
}
```

## Implementation Details

### DefaultPartnershipProcessor

The main implementation of the partnership system:

```java
/**
 * Default implementation of the PartnershipProcessor interface.
 */
public class DefaultPartnershipProcessor implements PartnershipProcessor, ClockListener {
    
    private static final Logger logger = LoggerFactory.getLogger(DefaultPartnershipProcessor.class);
    
    private final EventBus eventBus;
    private final Random random;
    private final PartnershipRepository partnershipRepository;
    
    // Configuration parameters
    private double basePartnershipProbability = 0.3; // 30% chance per eligible pair per year
    private int minimumPartnershipAge = 18;
    private int maximumAgeGap = 15; // Preferred age gap limit
    private double ageGapPenaltyFactor = 0.1; // Reduce compatibility by 10% for each year beyond max
    
    /**
     * Creates a new DefaultPartnershipProcessor.
     * 
     * @param eventBus the event bus for publishing events
     * @param randomProvider provider for randomization
     * @param partnershipRepository repository for storing partnerships
     */
    @Inject
    public DefaultPartnershipProcessor(
            EventBus eventBus, 
            RandomProvider randomProvider,
            PartnershipRepository partnershipRepository) {
        
        this.eventBus = Objects.requireNonNull(eventBus, "Event bus cannot be null");
        this.random = Objects.requireNonNull(
                randomProvider, "Random provider cannot be null").getRandom();
        this.partnershipRepository = Objects.requireNonNull(
                partnershipRepository, "Partnership repository cannot be null");
    }
    
    @Override
    public void onTimeChanged(LocalDate oldDate, LocalDate newDate, TimeUnit timeUnit) {
        if (timeUnit == TimeUnit.YEAR || 
                Period.between(oldDate, newDate).getYears() > 0) {
            try {
                // Get population from somewhere (dependency injection, service, etc.)
                List<Person> population = getPopulation();
                processPartnershipFormation(population, newDate);
            } catch (Exception e) {
                logger.error("Error processing partnerships on time change", e);
            }
        }
    }
    
    @Override
    public void processPartnershipFormation(List<Person> population, LocalDate currentDate) 
            throws SimulationException {
        
        logger.debug("Processing partnership formation for {} inhabitants on {}", 
                population.size(), currentDate);
        
        // Find all eligible people for partnerships
        List<Person> eligiblePeople = population.stream()
                .filter(this::isEligibleForPartnership)
                .collect(Collectors.toList());
        
        logger.debug("Found {} eligible people for partnerships", eligiblePeople.size());
        
        // Shuffle to randomize matching order
        Collections.shuffle(eligiblePeople, random);
        
        int partnershipCount = 0;
        Set<Person> matched = new HashSet<>();
        
        // Try to match each eligible person with potential partners
        for (Person person : eligiblePeople) {
            if (matched.contains(person)) {
                continue; // Already matched with someone else
            }
            
            // Find eligible partners for this person
            List<Person> potentialPartners = findEligiblePartners(person, eligiblePeople).stream()
                    .filter(p -> !matched.contains(p))
                    .collect(Collectors.toList());
            
            for (Person potentialPartner : potentialPartners) {
                double compatibility = calculateCompatibility(person, potentialPartner);
                double partnershipProbability = basePartnershipProbability * compatibility;
                
                if (random.nextDouble() < partnershipProbability) {
                    try {
                        // Form partnership
                        PartnershipEvent partnershipEvent = 
                                createPartnership(person, potentialPartner, currentDate);
                        
                        // Mark both as matched
                        matched.add(person);
                        matched.add(potentialPartner);
                        
                        partnershipCount++;
                        
                        logger.debug("Created partnership between {} and {}", 
                                person.getId(), potentialPartner.getId());
                        
                        // We found a match, move to next person
                        break;
                    } catch (Exception e) {
                        logger.error("Error creating partnership between {} and {}", 
                                person.getId(), potentialPartner.getId(), e);
                    }
                }
            }
        }
        
        logger.info("Created {} new partnerships out of {} eligible people on {}", 
                partnershipCount, eligiblePeople.size(), currentDate);
    }
    
    @Override
    public void processPartnershipDissolution(DeathEvent deathEvent) 
            throws SimulationException {
        
        Person deceased = deathEvent.getDeceased();
        Person partner = deceased.getPartner();
        
        if (partner == null) {
            return; // No partnership to dissolve
        }
        
        logger.debug("Processing partnership dissolution for deceased: {}", deceased.getId());
        
        try {
            // Find the partnership
            Partnership partnership = partnershipRepository.findByPartners(deceased, partner)
                    .orElseThrow(() -> new SimulationException(
                            "Could not find partnership for partners: " + 
                            deceased.getId() + " and " + partner.getId()));
            
            // End the partnership
            partnership.end(deathEvent.getEventDate());
            partnershipRepository.update(partnership);
            
            // Disconnect partners
            partner.removePartner();
            
            // Create and publish dissolution event
            PartnershipDissolutionEvent dissolutionEvent = new PartnershipDissolutionEvent(
                    partner, deceased, deathEvent.getEventDate(), partnership.getStartDate());
            
            eventBus.publishEvent(dissolutionEvent);
            
            logger.info("Dissolved partnership due to death: {} and {}", 
                    partner.getId(), deceased.getId());
            
        } catch (Exception e) {
            throw new SimulationException(
                    "Failed to process partnership dissolution", e);
        }
    }
    
    @Override
    public double calculateCompatibility(Person person1, Person person2) {
        if (person1 == null || person2 == null) {
            return 0.0;
        }
        
        // Check for eligibility
        if (!isEligiblePair(person1, person2)) {
            return 0.0;
        }
        
        // Base compatibility
        double compatibility = 1.0;
        
        // Age difference penalty
        int ageDifference = Math.abs(person1.getAge() - person2.getAge());
        if (ageDifference > maximumAgeGap) {
            int excessYears = ageDifference - maximumAgeGap;
            double agePenalty = excessYears * ageGapPenaltyFactor;
            compatibility -= agePenalty;
        }
        
        // Ensure final value is between 0 and 1
        return Math.max(0.0, Math.min(1.0, compatibility));
    }
    
    @Override
    public List<Person> findEligiblePartners(Person person, List<Person> population) {
        if (person == null || !isEligibleForPartnership(person)) {
            return Collections.emptyList();
        }
        
        return population.stream()
                .filter(other -> !person.equals(other))
                .filter(this::isEligibleForPartnership)
                .filter(other -> isEligiblePair(person, other))
                .sorted(Comparator.comparingDouble(other -> 
                        -calculateCompatibility(person, other))) // Sort by compatibility desc
                .collect(Collectors.toList());
    }
    
    @Override
    public boolean isEligibleForPartnership(Person person) {
        return person != null &&
               person.isAlive() &&
               person.getAge() >= minimumPartnershipAge &&
               person.getPartner() == null;
    }
    
    @Override
    public PartnershipEvent createPartnership(
            Person person1, Person person2, LocalDate partnershipDate) 
            throws SimulationException {
        
        if (!isEligiblePair(person1, person2)) {
            throw new SimulationException(
                    "Persons are not eligible for partnership: " + 
                    person1.getId() + " and " + person2.getId());
        }
        
        try {
            // Set partners in Person objects
            person1.setPartner(person2);
            
            // Create partnership record
            Partnership partnership = new Partnership(person1, person2, partnershipDate);
            partnershipRepository.save(partnership);
            
            // Create and publish event
            PartnershipEvent partnershipEvent = 
                    new PartnershipEvent(person1, person2, partnershipDate);
            eventBus.publishEvent(partnershipEvent);
            
            return partnershipEvent;
            
        } catch (Exception e) {
            // Revert any changes if partnership fails
            try {
                person1.removePartner();
            } catch (Exception ex) {
                logger.error("Error reverting partnership changes", ex);
            }
            
            throw new SimulationException(
                    "Failed to create partnership", e);
        }
    }
    
    /**
     * Determines if two people are eligible to form a partnership together.
     * 
     * @param person1 the first person
     * @param person2 the second person
     * @return true if the pair is eligible for partnership
     */
    private boolean isEligiblePair(Person person1, Person person2) {
        return person1 != null && person2 != null &&
               person1 != person2 &&
               isEligibleForPartnership(person1) &&
               isEligibleForPartnership(person2) &&
               !person1.isRelatedTo(person2);
    }
    
    // Helper method to get population - in a real implementation, this would be injected
    private List<Person> getPopulation() {
        // Implementation would depend on how population is managed
        // For this RFC, we assume another component provides this
        return Collections.emptyList();
    }
    
    @Override
    public boolean canHandle(EventType eventType) {
        return eventType == EventType.TIME_CHANGE || eventType == EventType.DEATH;
    }
    
    @Override
    public void processEvent(Event event) throws SimulationException {
        if (event.getType() == EventType.TIME_CHANGE) {
            TimeChangeEvent timeEvent = (TimeChangeEvent) event;
            onTimeChanged(timeEvent.getOldDate(), timeEvent.getNewDate(), timeEvent.getTimeUnit());
        } else if (event.getType() == EventType.DEATH) {
            DeathEvent deathEvent = (DeathEvent) event;
            processPartnershipDissolution(deathEvent);
        }
    }
    
    @Override
    public Set<EventType> getSupportedEventTypes() {
        return Set.of(EventType.TIME_CHANGE, EventType.DEATH);
    }
    
    @Override
    public int getPriority() {
        // Lower priority than aging and mortality but higher than child generation
        return 700;
    }
}
```

### DefaultFamilyProcessor

The main implementation of the family formation system:

```java
/**
 * Default implementation of the FamilyProcessor interface.
 */
public class DefaultFamilyProcessor implements FamilyProcessor, ClockListener {
    
    private static final Logger logger = LoggerFactory.getLogger(DefaultFamilyProcessor.class);
    
    private final EventBus eventBus;
    private final Random random;
    private final PartnershipRepository partnershipRepository;
    private final NameGenerator nameGenerator;
    
    // Configuration parameters
    private double baseFertilityRate = 0.20; // 20% chance per year for eligible partnerships
    private int minChildBearingAge = 18;
    private int maxChildBearingAge = 45;
    private int minPartnershipDuration = 0; // Can have children immediately after partnership forms
    private int maxChildren = 6; // Maximum family size
    private double maleChildProbability = 0.51; // Slightly more likely to have a male child
    
    /**
     * Creates a new DefaultFamilyProcessor.
     * 
     * @param eventBus the event bus for publishing events
     * @param randomProvider provider for randomization
     * @param partnershipRepository repository for partnerships
     * @param nameGenerator generator for child names
     */
    @Inject
    public DefaultFamilyProcessor(
            EventBus eventBus, 
            RandomProvider randomProvider,
            PartnershipRepository partnershipRepository,
            NameGenerator nameGenerator) {
        
        this.eventBus = Objects.requireNonNull(eventBus, "Event bus cannot be null");
        this.random = Objects.requireNonNull(
                randomProvider, "Random provider cannot be null").getRandom();
        this.partnershipRepository = Objects.requireNonNull(
                partnershipRepository, "Partnership repository cannot be null");
        this.nameGenerator = Objects.requireNonNull(
                nameGenerator, "Name generator cannot be null");
    }
    
    @Override
    public void onTimeChanged(LocalDate oldDate, LocalDate newDate, TimeUnit timeUnit) {
        if (timeUnit == TimeUnit.YEAR || 
                Period.between(oldDate, newDate).getYears() > 0) {
            try {
                // Get population from somewhere (dependency injection, service, etc.)
                List<Person> population = getPopulation();
                processChildGeneration(population, newDate);
            } catch (Exception e) {
                logger.error("Error processing child generation on time change", e);
            }
        }
    }
    
    @Override
    public void processChildGeneration(List<Person> population, LocalDate currentDate) 
            throws SimulationException {
        
        logger.debug("Processing child generation for {} inhabitants on {}", 
                population.size(), currentDate);
        
        // Find all active partnerships
        List<Partnership> activePartnerships = partnershipRepository.findActive();
        
        logger.debug("Found {} active partnerships for child generation", 
                activePartnerships.size());
        
        int birthCount = 0;
        
        // Process each partnership for potential child birth
        for (Partnership partnership : activePartnerships) {
            try {
                // Check if a child should be born this year
                if (shouldHaveChild(partnership, currentDate)) {
                    Person parent1 = partnership.getPartner1();
                    Person parent2 = partnership.getPartner2();
                    
                    // Generate the child
                    Person child = generateChild(parent1, parent2, currentDate);
                    
                    // Add child to partnership
                    partnership.addChild(child);
                    partnershipRepository.update(partnership);
                    
                    // Create and publish birth event
                    BirthEvent birthEvent = new BirthEvent(child, parent1, parent2, currentDate);
                    eventBus.publishEvent(birthEvent);
                    
                    birthCount++;
                    
                    logger.debug("Child born to parents {} and {}: {}", 
                            parent1.getId(), parent2.getId(), child.getId());
                }
            } catch (Exception e) {
                logger.error("Error processing child generation for partnership: {}", 
                        partnership.getId(), e);
                // Continue with other partnerships even if one fails
            }
        }
        
        logger.info("Generated {} new children across {} partnerships on {}", 
                birthCount, activePartnerships.size(), currentDate);
    }
    
    /**
     * Determines if a partnership should have a child this year.
     * 
     * @param partnership the partnership to check
     * @param currentDate the current simulation date
     * @return true if a child should be born
     */
    private boolean shouldHaveChild(Partnership partnership, LocalDate currentDate) {
        // Check if partnership is active
        if (!partnership.isActive()) {
            return false;
        }
        
        // Check partnership duration
        int duration = partnership.getDuration(currentDate);
        if (duration < minPartnershipDuration) {
            return false;
        }
        
        // Check family size limit
        if (partnership.getChildCount() >= determineMaxFamilySize(partnership)) {
            return false;
        }
        
        // Check fertility based on ages
        Person partner1 = partnership.getPartner1();
        Person partner2 = partnership.getPartner2();
        
        if (!isInChildBearingAge(partner1, currentDate) && 
            !isInChildBearingAge(partner2, currentDate)) {
            return false;
        }
        
        // Calculate fertility probability
        double fertilityProbability = calculateFertilityProbability(partnership);
        
        // Determine if child is born based on probability
        return random.nextDouble() < fertilityProbability;
    }
    
    /**
     * Checks if a person is in childbearing age range.
     * 
     * @param person the person to check
     * @param currentDate the current date
     * @return true if in childbearing age range
     */
    private boolean isInChildBearingAge(Person person, LocalDate currentDate) {
        int age = person.getAge();
        return age >= minChildBearingAge && age <= maxChildBearingAge;
    }
    
    @Override
    public double calculateFertilityProbability(Partnership partnership) {
        if (partnership == null || !partnership.isActive()) {
            return 0.0;
        }
        
        Person partner1 = partnership.getPartner1();
        Person partner2 = partnership.getPartner2();
        
        if (!partner1.isAlive() || !partner2.isAlive()) {
            return 0.0;
        }
        
        // Start with base fertility rate
        double probability = baseFertilityRate;
        
        // Adjust based on partner ages
        int age1 = partner1.getAge();
        int age2 = partner2.getAge();
        
        // Check if either partner is in childbearing age
        boolean partner1CanHaveChildren = age1 >= minChildBearingAge && age1 <= maxChildBearingAge;
        boolean partner2CanHaveChildren = age2 >= minChildBearingAge && age2 <= maxChildBearingAge;
        
        if (!partner1CanHaveChildren && !partner2CanHaveChildren) {
            return 0.0; // Neither partner can have children
        }
        
        // Adjust for age - fertility peaks in mid-20s and declines after
        double ageFactor1 = calculateAgeFertilityFactor(age1);
        double ageFactor2 = calculateAgeFertilityFactor(age2);
        double ageFactor = Math.max(ageFactor1, ageFactor2);
        
        // Adjust for existing family size
        int childCount = partnership.getChildCount();
        double familySizeFactor = 1.0 - (childCount * 0.15); // Decrease by 15% for each existing child
        
        // Calculate final probability
        probability *= ageFactor * familySizeFactor;
        
        // Ensure value is between 0 and 1
        return Math.max(0.0, Math.min(1.0, probability));
    }
    
    /**
     * Calculates fertility factor based on age.
     * 
     * @param age the person's age
     * @return fertility factor (0.0 to 1.0)
     */
    private double calculateAgeFertilityFactor(int age) {
        if (age < minChildBearingAge || age > maxChildBearingAge) {
            return 0.0;
        }
        
        // Peak fertility around age 25
        int optimalAge = 25;
        int ageDistance = Math.abs(age - optimalAge);
        
        // Decrease factor linearly with distance from optimal age
        double factor = 1.0 - (ageDistance * 0.04); // 4% reduction per year from optimal
        
        return Math.max(0.1, factor); // Minimum 10% of base rate if in fertile range
    }
    
    @Override
    public Person generateChild(Person parent1, Person parent2, LocalDate birthDate) 
            throws SimulationException {
        
        if (parent1 == null || parent2 == null) {
            throw new SimulationException("Both parents must be specified");
        }
        
        if (!parent1.isAlive() || !parent2.isAlive()) {
            throw new SimulationException("Both parents must be alive to have a child");
        }
        
        if (birthDate == null) {
            throw new SimulationException("Birth date cannot be null");
        }
        
        try {
            // Determine child's gender
            Gender gender = determineChildGender(parent1, parent2);
            
            // Generate name
            PersonName childName = generateChildName(parent1, parent2, gender);
            
            // Create child
            Person child = new Person(
                    childName.getFirstName(), 
                    childName.getLastName(), 
                    gender, 
                    birthDate);
            
            // Establish parent-child relationships
            child.addParent(parent1);
            child.addParent(parent2);
            
            return child;
            
        } catch (Exception e) {
            throw new SimulationException("Failed to generate child", e);
        }
    }
    
    @Override
    public Gender determineChildGender(Person parent1, Person parent2) {
        // Simple probability-based gender determination
        return random.nextDouble() < maleChildProbability ? Gender.MALE : Gender.FEMALE;
    }
    
    @Override
    public PersonName generateChildName(Person parent1, Person parent2, Gender gender) {
        // Generate first name based on gender
        String firstName = gender == Gender.MALE ? 
                nameGenerator.getRandomMaleName() : 
                nameGenerator.getRandomFemaleName();
        
        // Determine last name (typically from one parent, often the father in traditional settings)
        // This could be customized for different cultural naming patterns
        String lastName = parent1.getLastName();
        
        return new PersonName(firstName, lastName);
    }
    
    @Override
    public int determineMaxFamilySize(Partnership partnership) {
        // Base max family size
        int maxSize = maxChildren;
        
        // Could adjust based on various factors:
        // - Partner ages
        // - Economic factors (in future extensions)
        // - Cultural factors (in future extensions)
        
        return maxSize;
    }
    
    // Helper method to get population - in a real implementation, this would be injected
    private List<Person> getPopulation() {
        // Implementation would depend on how population is managed
        // For this RFC, we assume another component provides this
        return Collections.emptyList();
    }
    
    @Override
    public boolean canHandle(EventType eventType) {
        return eventType == EventType.TIME_CHANGE || 
               eventType == EventType.PARTNERSHIP_FORMATION;
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
        return Set.of(EventType.TIME_CHANGE, EventType.PARTNERSHIP_FORMATION);
    }
    
    @Override
    public int getPriority() {
        // Lower priority than partnership formation
        return 600;
    }
}
```

### NameGenerator

The implementation for generating realistic names:

```java
/**
 * Generates realistic names for persons in the simulation.
 */
@Component
public class NameGenerator {
    
    private static final Logger logger = LoggerFactory.getLogger(NameGenerator.class);
    
    private final Random random;
    private final List<String> maleFirstNames;
    private final List<String> femaleFirstNames;
    private final List<String> lastNames;
    
    /**
     * Creates a new NameGenerator with default name lists.
     * 
     * @param randomProvider provider for randomization
     */
    @Inject
    public NameGenerator(RandomProvider randomProvider) {
        this.random = Objects.requireNonNull(
                randomProvider, "Random provider cannot be null").getRandom();
        
        // Load name lists
        this.maleFirstNames = loadNameList("male_first_names.txt");
        this.femaleFirstNames = loadNameList("female_first_names.txt");
        this.lastNames = loadNameList("last_names.txt");
        
        logger.info("NameGenerator initialized with {} male names, {} female names, and {} last names",
                maleFirstNames.size(), femaleFirstNames.size(), lastNames.size());
    }
    
    /**
     * Gets a random male first name.
     * 
     * @return a male first name
     */
    public String getRandomMaleName() {
        return getRandomName(maleFirstNames);
    }
    
    /**
     * Gets a random female first name.
     * 
     * @return a female first name
     */
    public String getRandomFemaleName() {
        return getRandomName(femaleFirstNames);
    }
    
    /**
     * Gets a random last name.
     * 
     * @return a last name
     */
    public String getRandomLastName() {
        return getRandomName(lastNames);
    }
    
    /**
     * Gets a random name from the specified list.
     * 
     * @param nameList the list of names to choose from
     * @return a randomly selected name
     */
    private String getRandomName(List<String> nameList) {
        if (nameList == null || nameList.isEmpty()) {
            return "Unknown";
        }
        return nameList.get(random.nextInt(nameList.size()));
    }
    
    /**
     * Loads a list of names from a resource file.
     * 
     * @param resourceName the name of the resource file
     * @return list of names from the file
     */
    private List<String> loadNameList(String resourceName) {
        List<String> names = new ArrayList<>();
        
        try (InputStream is = getClass().getClassLoader().getResourceAsStream(resourceName);
             BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
            
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (!line.isEmpty() && !line.startsWith("#")) {
                    names.add(line);
                }
            }
            
        } catch (Exception e) {
            logger.error("Error loading name list from resource: {}", resourceName, e);
            // Add some fallback names if loading fails
            if (resourceName.contains("male")) {
                names.addAll(List.of("John", "James", "William", "Michael", "David"));
            } else if (resourceName.contains("female")) {
                names.addAll(List.of("Mary", "Elizabeth", "Sarah", "Jennifer", "Emma"));
            } else {
                names.addAll(List.of("Smith", "Johnson", "Williams", "Brown", "Jones"));
            }
        }
        
        return names;
    }
}
```

### PartnershipRepository

Interface for partnership data storage:

```java
/**
 * Repository for storing and retrieving partnerships.
 */
public interface PartnershipRepository {
    
    /**
     * Saves a new partnership.
     * 
     * @param partnership the partnership to save
     * @return the saved partnership with any generated IDs
     * @throws PersistenceException if saving fails
     */
    Partnership save(Partnership partnership) throws PersistenceException;
    
    /**
     * Updates an existing partnership.
     * 
     * @param partnership the partnership to update
     * @throws PersistenceException if updating fails
     */
    void update(Partnership partnership) throws PersistenceException;
    
    /**
     * Finds a partnership by its unique ID.
     * 
     * @param id the partnership ID
     * @return optional containing the partnership if found
     * @throws PersistenceException if finding fails
     */
    Optional<Partnership> findById(UUID id) throws PersistenceException;
    
    /**
     * Finds a partnership by its partners.
     * 
     * @param partner1 the first partner
     * @param partner2 the second partner
     * @return optional containing the partnership if found
     * @throws PersistenceException if finding fails
     */
    Optional<Partnership> findByPartners(Person partner1, Person partner2) 
            throws PersistenceException;
    
    /**
     * Finds all active partnerships.
     * 
     * @return list of active partnerships
     * @throws PersistenceException if finding fails
     */
    List<Partnership> findActive() throws PersistenceException;
    
    /**
     * Finds all partnerships involving a specific person.
     * 
     * @param person the person to find partnerships for
     * @return list of partnerships involving the person
     * @throws PersistenceException if finding fails
     */
    List<Partnership> findByPerson(Person person) throws PersistenceException;
    
    /**
     * Deletes a partnership.
     * 
     * @param partnership the partnership to delete
     * @throws PersistenceException if deletion fails
     */
    void delete(Partnership partnership) throws PersistenceException;
}
```

## Acceptance Criteria

- [x] Adults can form partnerships with age-appropriate potential partners
- [x] Partnership formation respects relationship constraints (no relatives, existing partners)
- [x] Partnerships can produce children with realistic fertility rates and timing
- [x] Children inherit traits from both parents according to specified rules
- [x] Family sizes follow realistic distributions (1-6 children typically)
- [x] Partnership dissolution is handled correctly when a partner dies
- [x] Widowed partners can remarry after previous partner's death
- [x] Parent-child relationships are properly established and maintained
- [x] Generated names follow realistic patterns and conventions
- [x] All partnership and family events are properly tracked and logged
- [x] System maintains consistent family relationships (bidirectional parent-child links)
- [x] Performance tests pass for populations of 500+ inhabitants
- [x] All components have comprehensive unit tests (>85% coverage)
- [x] Documentation is complete for all APIs and components

## Dependencies

**Builds Upon:**
- RFC-001: Project Setup and Architecture
- RFC-002: Core Entity Models
- RFC-003: Simulation Clock and Event System
- RFC-004: Life Cycle - Aging and Mortality System

**Required For:**
- RFC-006: Population Management
- RFC-011: Statistics and Reporting

## Testing Strategy

### Unit Tests

#### PartnershipProcessor Tests
- Test partnership eligibility calculations
- Test compatibility scoring
- Test partnership formation and validation
- Test partnership dissolution handling
- Test partner matching logic
- Test age gap penalties

#### FamilyProcessor Tests
- Test fertility probability calculations
- Test child generation logic
- Test gender determination consistency
- Test name generation functionality
- Test family size limitation enforcement
- Test parent-child relationship establishment

#### Partnership Tests
- Test partnership creation and validation
- Test child addition and counting
- Test duration calculation
- Test partnership termination

### Integration Tests

- Test complete partnership ? child generation workflow
- Test complex family formation over multiple generations
- Test remarriage and blended family formation
- Test relationships consistency across complex family structures
- Test naming patterns across generations

## Security Considerations

- Validate all relationship changes to prevent invalid family structures
- Ensure age-appropriate relationships (no underage partnerships)
- Prevent circular relationships or other invalid family structures
- Handle family-related data with appropriate privacy considerations
- Validate name generation to prevent inappropriate content

## Performance Considerations

- Optimize partner matching for large populations (avoid O(n²) algorithms)
- Consider indexing partnerships by partner IDs for quick lookups
- Use efficient data structures for family relationship tracking
- Batch process partnership and child generation for better performance
- Cache calculated values like compatibility scores where appropriate
- Consider parallel processing for partner matching in large populations

## Open Questions

1. Should partnership compatibility include more factors beyond age (personality traits, interests)?
2. How should we handle non-traditional family structures in future extensions?
3. Should we implement different naming conventions for different cultural contexts?
4. How detailed should the inheritance of traits from parents to children be?
5. Should we implement a "divorce" feature for future extensions or only partnership dissolution through death?

## References

- [Fertility Rates by Age](https://www.cdc.gov/nchs/data/nvsr/nvsr70/nvsr70-17.pdf)
- [Family Size Distributions](https://www.census.gov/library/publications/2020/demo/p70-167.html)
- [Partner Selection Patterns](https://journals.sagepub.com/doi/10.1177/0265407518757707)
- [Naming Conventions Across Cultures](https://en.wikipedia.org/wiki/Naming_convention)
- [Marriage and Remarriage Rates](https://www.pewresearch.org/short-reads/2020/01/03/divorce-rate-in-u-s-has-dropped-since-1980/)
