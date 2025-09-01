package com.littlepeople.core.processors;

import com.littlepeople.core.exceptions.SimulationException;
import com.littlepeople.core.interfaces.Event;
import com.littlepeople.core.interfaces.EventProcessor;
import com.littlepeople.core.interfaces.EventScheduler;
import com.littlepeople.core.model.DeathCause;
import com.littlepeople.core.model.Gender;
import com.littlepeople.core.model.Person;
import com.littlepeople.core.model.PersonBuilder;
import com.littlepeople.core.model.PersonRegistry;
import com.littlepeople.core.model.PersonalityTrait;
import com.littlepeople.core.model.events.BirthEvent;
import com.littlepeople.core.model.events.PersonDeathEvent;
import com.littlepeople.core.util.NameGenerator;
import com.littlepeople.simulation.partnerships.FertilityCalculatorInterface;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

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
public class BirthProcessor extends AbstractEventProcessor implements EventProcessor {

    public static final double DOMINANT_PARENT_INFLUENCE = 0.8;
    public static final double MINOR_PARENT_INFLUENCE = 0.2;
    private static final Logger logger = LoggerFactory.getLogger(BirthProcessor.class);

    private final Random random;

    // Configuration parameters
    private static final int PREGNANCY_DURATION_MONTHS = 9;
    private static final double TRAIT_INHERITANCE_VARIATION = 0.2; // 20% random variation
    private static final double BIRTH_COMPLICATION_RATE = 0.05; // 5% chance of complications
    private static final double TWIN_PROBABILITY = 0.02;
    private static final double TRIPLET_PROBABILITY = 0.0002;
    EventScheduler eventScheduler;

    /**
     * Creates a new family processor with default configuration.
     */
    public BirthProcessor(EventScheduler eventScheduler) {
        super(BirthEvent.class);
        this.random = new Random();
        this.eventScheduler = eventScheduler;
    }


    /**
     * Processes family formation for all eligible partnerships in the population.
     *
     * <p>This method evaluates each married couple for potential childbirth,
     * calculates conception probabilities based on fertility factors, and
     * creates child events for successful conceptions.</p>
     *
     * @param currentDate the current simulation date
     * @return list of child birth events created during this cycle
     * @throws SimulationException if processing fails
     */
    public void processBirthEvent(BirthEvent event, LocalDate currentDate)
            throws SimulationException {

        // Find all eligible couples
        Person mother = PersonRegistry.get(event.getMotherId());
        Person father = PersonRegistry.get(event.getFatherId());
        if (!mother.isAlive()) {
            return;
        }
        int kids = numberOfBirths();

        List<Person> children = createChilds(father, mother, currentDate, kids);
        for (Person kid : children) {
            PersonRegistry.add(kid);
            logger.debug("Child {} born to parents {} and {}",
                    kid.getFullName(), father.getFullName(), mother.getFullName());
        }

        mother.setPregnant(false);

        List<Event> events = checkBirthComplications(children, mother,currentDate);
        eventScheduler.scheduleEvents(events);


    }

    private int numberOfBirths() {
        double roll = random.nextDouble();
        if (roll < TRIPLET_PROBABILITY) {
            return 3;
        } else if (roll < TWIN_PROBABILITY) {
            return 2;
        } else {
            return 1;
        }
    }

    private List<Event> checkBirthComplications(List<Person> children, Person mother, LocalDate birthDate) {
        List<Event> events = new java.util.ArrayList<>();
        boolean motherDeath = false;
        double deathRate = BIRTH_COMPLICATION_RATE * children.size();

        // mother
        if (random.nextDouble() < BIRTH_COMPLICATION_RATE) {
            PersonDeathEvent deathEvent = new PersonDeathEvent(
                    mother.getId(),
                    birthDate,
                    DeathCause.BIRTH_COMPLICATION
            );
            events.add(deathEvent);
            motherDeath = true;
        }
        // when mother dies during birth, 50% chance of losing the
        // baby
        if (motherDeath) {
            deathRate = BIRTH_COMPLICATION_RATE * 10;
        }
        for (Person child : children) {
            if (random.nextDouble() < BIRTH_COMPLICATION_RATE) {
                PersonDeathEvent deathEvent = new PersonDeathEvent(
                        child.getId(),
                        birthDate,
                        DeathCause.BIRTH_COMPLICATION
                );
                events.add(deathEvent);
            }
        }
        return events;
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
    public List<Person> createChilds(Person father, Person mother, LocalDate birthDate, int numberOfBirths) throws SimulationException {
        List<Person> children = new java.util.ArrayList<>();
        if (father == null || mother == null) {
            throw new IllegalArgumentException("Both parents must be non-null");
        }

        if (birthDate == null) {
            throw new IllegalArgumentException("Birth date cannot be null");
        }

        try {
            for (int i = 0; i < numberOfBirths(); i++) {
                Gender gender = generateChildGender();
                // Generate child's basic information
                String firstName = generateChildName(gender);

                for (Person sibling : children) {
                    while (sibling.getFirstName().equals(firstName)) {
                        firstName = generateChildName(gender);
                    }
                }


                String lastName = inheritLastName(father, mother);


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
                children.add(child);
                logger.debug("Child created: {} {} ({}), born to {} and {} : family.size={}",
                        firstName, lastName, gender, father.getId(), mother.getId(), mother.getChildren().size());
            }


            return children;

        } catch (Exception e) {
            logger.error("Failed to create child for parents {} and {}: birthDate {}",
                    father, mother, birthDate, e);
            throw new SimulationException("Failed to create child", e);
        }
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
    private String generateChildName(Gender gender) {

        NameGenerator nameGen = new NameGenerator();
        return nameGen.generateFirstName(gender);
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
        if (event instanceof BirthEvent) {
            BirthEvent childEvent = (BirthEvent) event;
            processBirthEvent(childEvent, event.getScheduledTime().toLocalDate());
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
        return 800; // Higher priority than partnership processing since family formation depends on partnerships
    }


}
