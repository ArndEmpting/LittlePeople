package com.littlepeople.simulation.partnerships;

import com.littlepeople.core.exceptions.SimulationException;
import com.littlepeople.core.interfaces.EventProcessor;
import com.littlepeople.core.interfaces.Event;
import com.littlepeople.core.interfaces.EventScheduler;
import com.littlepeople.core.model.*;
import com.littlepeople.core.model.events.ChildAddedEvent;
import com.littlepeople.core.model.events.FamilyCalculationEvent;
import com.littlepeople.core.model.events.PartnershipCalculationEvent;
import com.littlepeople.core.processors.AbstractEventProcessor;
import com.littlepeople.simulation.population.PopulationManagerImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.util.*;

/**
 * Handles family formation through childbirth and genetic inheritance.
 *
 * <p>This class manages the creation of children from partnerships, implementing
 * realistic fertility cycles, genetic trait inheritance, and family relationship
 * establishment. It integrates with the fertility calculator to determine
 * conception probability and manages the complete childbirth process.</p>
 *
 * <h2>Family Formation Process</h2>
 * <ol>
 *   <li>Evaluate fertility for all married couples</li>
 *   <li>Calculate monthly conception probabilities</li>
 *   <li>Generate pregnancy events for successful conceptions</li>
 *   <li>Track pregnancy progression over 9 months</li>
 *   <li>Create child with inherited traits</li>
 *   <li>Establish parent-child relationships</li>
 * </ol>
 *
 * <h2>Genetic Inheritance</h2>
 * <p>Children inherit traits from both parents using these algorithms:</p>
 * <ul>
 *   <li><strong>Personality Traits:</strong> Average of parents with random variation</li>
 *   <li><strong>Physical Traits:</strong> Random selection from parent traits</li>
 *   <li><strong>Gender:</strong> 50/50 probability for male/female</li>
 *   <li><strong>Health:</strong> Generally healthy with small chance of complications</li>
 * </ul>
 *
 * @version 1.0.0
 * @since 1.0.0
 */
public class FamilyProcessor extends AbstractEventProcessor implements EventProcessor {

    public static final double DOMINANT_PARENT_INFLUENCE = 0.8;
    public static final double MINOR_PARENT_INFLUENCE = 0.2;
    private static final Logger logger = LoggerFactory.getLogger(FamilyProcessor.class);

    private final FertilityCalculatorInterface fertilityCalculator;
    private final Random random;

    // Configuration parameters
    private static final int PREGNANCY_DURATION_MONTHS = 9;
    private static final double TRAIT_INHERITANCE_VARIATION = 0.2; // 20% random variation
    private static final double BIRTH_COMPLICATION_RATE = 0.05; // 5% chance of complications
    EventScheduler eventScheduler;

    /**
     * Creates a new family processor with default configuration.
     */
    public FamilyProcessor(EventScheduler eventScheduler) {
        super(FamilyCalculationEvent.class);
        this.fertilityCalculator = new MedievalFertilityCalculator();
        this.random = new Random();
        this.eventScheduler = eventScheduler;
    }

    /**
     * Creates a new family processor with custom configuration.
     *
     * @param fertilityCalculator custom fertility calculator
     * @param random              random number generator for reproducible testing
     */
    public FamilyProcessor(FertilityCalculatorInterface fertilityCalculator, Random random) {
        super(FamilyCalculationEvent.class);
        this.fertilityCalculator = fertilityCalculator;
        this.random = random;
    }

    /**
     * Processes family formation for all eligible partnerships in the population.
     *
     * <p>This method evaluates each married couple for potential childbirth,
     * calculates conception probabilities based on fertility factors, and
     * creates child events for successful conceptions.</p>
     *
     * @param population  the current population
     * @param currentDate the current simulation date
     * @return list of child birth events created during this cycle
     * @throws SimulationException if processing fails
     */
    public List<ChildAddedEvent> processFamilyFormation(List<Person> population, LocalDate currentDate)
            throws SimulationException {

        if (population == null || population.isEmpty()) {
            logger.debug("No population provided for family formation");
            return Collections.emptyList();
        }

        logger.debug("Processing family formation for {} people on {}", population.size(), currentDate);

        List<ChildAddedEvent> birthEvents = new ArrayList<>();

        try {
            // Find all eligible couples
            List<Partnership> eligibleCouples = findEligibleCouples(population);
            logger.info("Found {} eligible couples for family formation", eligibleCouples.size());

            for (Partnership couple : eligibleCouples) {
                // Calculate conception probability for this month
                double conceptionProbability = fertilityCalculator.calculateMonthlyConceptionProbability(
                        couple.malePartner, couple.femalePartner);

                if (conceptionProbability > 0.0 && random.nextDouble() < conceptionProbability) {
                    // Conception successful - create child
                    Person child = createChild(couple.malePartner, couple.femalePartner, currentDate);
                    PersonRegistry.add(child);
                    // Create birth event
                    ChildAddedEvent birthEvent = new ChildAddedEvent(
                            couple.malePartner.getId(),
                            child.getId());

                    birthEvents.add(birthEvent);

                    logger.debug("Child {} born to parents {} and {}",
                            child.getFullName(), couple.malePartner.getFullName(), couple.femalePartner.getFullName());
                }
            }

            logger.info("Family formation complete: {} children born", birthEvents.size());
            return birthEvents;

        } catch (Exception e) {
            logger.error("Error during family formation processing", e);
            throw new SimulationException("Failed to process family formation", e);
        }
    }

    /**
     * Creates a child with genetic traits inherited from both parents.
     *
     * @param father    the father
     * @param mother    the mother
     * @param birthDate the child's birth date
     * @return the newly created child
     * @throws SimulationException if child creation fails
     */
    public Person createChild(Person father, Person mother, LocalDate birthDate) throws SimulationException {
        if (father == null || mother == null) {
            throw new IllegalArgumentException("Both parents must be non-null");
        }

        if (birthDate == null) {
            throw new IllegalArgumentException("Birth date cannot be null");
        }

        try {
            // Generate child's basic information
            String firstName = generateChildName();
            String lastName = inheritLastName(father, mother);
            Gender gender = generateChildGender();

            // Create child using PersonBuilder
            PersonBuilder childBuilder = new PersonBuilder()
                    .firstName(firstName)
                    .lastName(lastName)
                    .birthDate(birthDate)
                    .gender(gender)
                    .personalityTraits(inheritPersonalityTraits(father, mother));
            // Inherit personality traits

            // Create the child
            Person child = childBuilder.build();

            // Establish family relationships
            establishFamilyRelationships(child, father, mother);

            logger.debug("Child created: {} {} ({}), born to {} and {} : family.size={}",
                    firstName, lastName, gender, father.getId(), mother.getId(), mother.getChildren().size());

            return child;

        } catch (Exception e) {
            logger.error("Failed to create child for parents {} and {}",
                    father.getId(), mother.getId(), e);
            throw new SimulationException("Failed to create child", e);
        }
    }

    /**
     * Finds all eligible couples for family formation.
     *
     * @param population the population to search
     * @return list of eligible partnerships
     */
    private List<Partnership> findEligibleCouples(List<Person> population) {
        List<Partnership> couples = new ArrayList<>();
        Set<UUID> processedIds = new HashSet<>();

        for (Person person : population) {
            if (processedIds.contains(person.getId())) {
                continue; // Already processed this person
            }

            Person partner = person.getPartner();
            if (partner == null || processedIds.contains(partner.getId())) {
                continue; // No partner or partner already processed
            }

            // Check if couple is eligible for children
            if (isEligibleForChildren(person, partner)) {
                // Determine male and female partners
                Person male = person.getGender() == Gender.MALE ? person : partner;
                Person female = person.getGender() == Gender.FEMALE ? person : partner;

                couples.add(new Partnership(male, female));

                // Mark both as processed
                processedIds.add(person.getId());
                processedIds.add(partner.getId());
            }
        }

        return couples;
    }

    /**
     * Checks if a couple is eligible to have children.
     *
     * @param person1 first partner
     * @param person2 second partner
     * @return true if eligible for children
     */
    private boolean isEligibleForChildren(Person person1, Person person2) {
        // Both must be alive
        if (person1.isDeceased() || person2.isDeceased()) {
            return false;
        }

        // Must be different genders
        if (person1.getGender() == person2.getGender()) {
            return false;
        }

        // At least one must be fertile
        return fertilityCalculator.isFertile(person1) || fertilityCalculator.isFertile(person2);
    }

    /**
     * Inherits personality traits from both parents.
     *
     * @param father the father
     * @param mother the mother
     * @return map of inherited personality traits
     */
    private Map<PersonalityTrait, Integer> inheritPersonalityTraits(Person father, Person mother) {
        Map<PersonalityTrait, Integer> inheritedTraits = new HashMap<>();
        Map<PersonalityTrait, Integer> fatherTraits = father.getPersonalityTraits();
        Map<PersonalityTrait, Integer> motherTraits = mother.getPersonalityTraits();

        for (PersonalityTrait trait : PersonalityTrait.values()) {
            Integer fatherValue = fatherTraits.get(trait);
            Integer motherValue = motherTraits.get(trait);

            if (fatherValue != null && motherValue != null) {
                // Randomly choose which parent is dominant
                boolean fatherDominant = random.nextBoolean();
                double dominantValue = fatherDominant ? fatherValue : motherValue;
                double minorValue = fatherDominant ? motherValue : fatherValue;

                // Calculate base inheritance: 90% dominant parent + 10% minor parent
                double baseInheritance = (dominantValue * DOMINANT_PARENT_INFLUENCE) +
                        (minorValue * MINOR_PARENT_INFLUENCE);
                // Add individual variation of ±20%
                int inheritedValue = trimValue(traitVariation(baseInheritance));
                inheritedTraits.put(trait, inheritedValue);
            } else if (fatherValue != null) {
                inheritedTraits.put(trait, trimValue(traitVariation(fatherValue)));
            } else if (motherValue != null) {
                inheritedTraits.put(trait, trimValue(traitVariation(motherValue)));

            }
        }
        return inheritedTraits;
    }


    private Integer trimValue(Integer value) {
        if (value < 0) {
            return 0;
        } else if (value > 100) {
            return 100;
        } else {
            return value;
        }
    }


    private int traitVariation(double baseInheritance) {
        double variationRange = baseInheritance * TRAIT_INHERITANCE_VARIATION;
        double variation = (random.nextDouble() * 2.0 - 1.0) * variationRange; // -20% to +20%

        int inheritedValue = (int) Math.round(baseInheritance + variation);
        return inheritedValue;
    }

    /**
     * Generates a random gender for the child.
     *
     * @return randomly selected gender
     */
    private Gender generateChildGender() {
        return random.nextBoolean() ? Gender.MALE : Gender.FEMALE;
    }

    /**
     * Generates a random first name for the child.
     *
     * @return randomly generated name
     */
    private String generateChildName() {
        // Simple name generation - in a full implementation, this would use
        // cultural name databases and naming conventions
        String[] maleNames = {"James", "John", "Robert", "Michael", "William", "David", "Richard", "Joseph", "Thomas", "Christopher"};
        String[] femaleNames = {"Mary", "Patricia", "Jennifer", "Linda", "Elizabeth", "Barbara", "Susan", "Jessica", "Sarah", "Karen"};

        if (random.nextBoolean()) {
            return maleNames[random.nextInt(maleNames.length)];
        } else {
            return femaleNames[random.nextInt(femaleNames.length)];
        }
    }

    /**
     * Determines the child's last name based on cultural conventions.
     *
     * @param father the father
     * @param mother the mother
     * @return inherited last name
     */
    private String inheritLastName(Person father, Person mother) {
        // Simple convention: child takes father's last name
        // In a more sophisticated implementation, this could consider
        // cultural practices, marriage status, etc.
        return father.getLastName();
    }

    /**
     * Establishes bidirectional family relationships between child and parents.
     *
     * @param child  the child
     * @param father the father
     * @param mother the mother
     */
    private void establishFamilyRelationships(Person child, Person father, Person mother) {


        // Add child to parents
        father.addChild(child);
        mother.addChild(child);

        logger.debug("Family relationships established: {} is child of {} and {}",
                child.getId(), father.getId(), mother.getId());
    }

    // EventProcessor interface implementation

    @Override
    public void processEvent(Event event) throws SimulationException {
        if (event == null) {
            throw new IllegalArgumentException("Event cannot be null");
        }


        if (!canProcess(event.getClass())) {
            throw new SimulationException("Cannot process event type: " + event.getClass());
        }

        logger.debug("Processing event: {} of type: {}", event.getId(), event.getClass());
        if (event instanceof FamilyCalculationEvent) {
            List<ChildAddedEvent> childAddedEvents = processFamilyFormation(PopulationManagerImpl.getInstance().getPopulation()
                    , ((FamilyCalculationEvent) event).getScheduledTime().toLocalDate());



        }
        // Handle family-related events
        else if (event instanceof ChildAddedEvent) {
            ChildAddedEvent childEvent = (ChildAddedEvent) event;
            logger.info("Processing child birth event: {}", childEvent.getId());
            // Event is already processed during creation, just mark as complete
        } else {
            logger.warn("Unknown event subtype for family processor: {}", event.getClass().getSimpleName());
        }

        // Mark event as processed
        event.markProcessed();
    }


    @Override
    public int getPriority() {
        return 200; // Higher priority than partnership processing since family formation depends on partnerships
    }

    /**
     * Helper class to represent a partnership for family formation.
     */
    private static class Partnership {
        final Person malePartner;
        final Person femalePartner;

        Partnership(Person malePartner, Person femalePartner) {
            this.malePartner = malePartner;
            this.femalePartner = femalePartner;
        }
    }
}
