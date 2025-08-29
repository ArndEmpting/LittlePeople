package com.littlepeople.core.model;

import com.littlepeople.core.util.NameGenerator;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * Builder class for creating Person instances with flexible configuration.
 *
 * <p>This builder provides a fluent API for constructing Person objects
 * with various optional attributes. It handles personality trait generation
 * for different scenarios:</p>
 * <ul>
 *   <li><strong>Seeding persons:</strong> Random traits 0-100% for initial population</li>
 *   <li><strong>Children (RFC-005):</strong> Inherited traits from parents with variation</li>
 *   <li><strong>Manual configuration:</strong> Explicitly set traits</li>
 * </ul>
 *
 * <p>Usage examples:</p>
 * <pre>
 * // Seeding person with random gender, age, and traits
 * Person seedingPerson = new PersonBuilder()
 *     .randomSeedingPerson(65)
 *     .build();
 *
 * // Child inheriting from parents (RFC-005)
 * Person child = new PersonBuilder()
 *     .childFromParents(parent1, parent2, birthDate)
 *     .build();
 *
 * // Manual configuration
 * Person person = new PersonBuilder()
 *     .firstName("John")
 *     .lastName("Doe")
 *     .gender(Gender.MALE)
 *     .birthDate(LocalDate.of(1990, 1, 1))
 *     .personalityTrait(PersonalityTrait.EXTRAVERSION, 85)
 *     .build();
 * </pre>
 *
 * @since 1.0.0
 * @version 1.0.0
 */
public class PersonBuilder {

    private String firstName;
    private String lastName;
    private Gender gender;
    private LocalDate birthDate;
    private HealthStatus healthStatus;
    private WealthStatus wealthStatus;
    private Map<PersonalityTrait, Integer> personalityTraits;
    private final Random random = new Random();
    private final NameGenerator nameGenerator = new NameGenerator();

    public PersonBuilder() {
        this.personalityTraits = new HashMap<>();
    }

    /**
     * Sets the first name for the person being built.
     *
     * @param firstName the first name (required)
     * @return this builder instance for method chaining
     */
    public PersonBuilder firstName(String firstName) {
        this.firstName = firstName;
        return this;
    }

    /**
     * Sets the last name for the person being built.
     *
     * @param lastName the last name (required)
     * @return this builder instance for method chaining
     */
    public PersonBuilder lastName(String lastName) {
        this.lastName = lastName;
        return this;
    }

    /**
     * Sets the gender for the person being built.
     *
     * @param gender the gender (required)
     * @return this builder instance for method chaining
     */
    public PersonBuilder gender(Gender gender) {
        this.gender = gender;
        return this;
    }

    /**
     * Sets the birth date for the person being built.
     *
     * @param birthDate the birth date (required)
     * @return this builder instance for method chaining
     */
    public PersonBuilder birthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
        return this;
    }

    /**
     * Sets the health status for the person being built.
     *
     * @param healthStatus the health status (optional, defaults to HEALTHY)
     * @return this builder instance for method chaining
     */
    public PersonBuilder healthStatus(HealthStatus healthStatus) {
        this.healthStatus = healthStatus;
        return this;
    }

    /**
     * Sets the wealth status for the person being built.
     *
     * @param wealthStatus the wealth status (optional, defaults to MIDDLE_CLASS)
     * @return this builder instance for method chaining
     */
    public PersonBuilder wealthStatus(WealthStatus wealthStatus) {
        this.wealthStatus = wealthStatus;
        return this;
    }

    /**
     * Sets a specific personality trait value (0-100).
     *
     * @param trait the personality trait to set
     * @param intensity the intensity level (0-100)
     * @return this builder instance for method chaining
     */
    public PersonBuilder personalityTrait(PersonalityTrait trait, int intensity) {
        if (trait == null) {
            throw new IllegalArgumentException("Personality trait cannot be null");
        }
        if (intensity < 0 || intensity > 100) {
            throw new IllegalArgumentException("Personality trait intensity must be between 0 and 100: " + intensity);
        }
        personalityTraits.put(trait, intensity);
        return this;
    }

    /**
     * Creates a random seeding person for initial population generation.
     * This generates completely random gender, age, and personality traits (0-100%)
     * for the founding population. This is the primary method for creating
     * the initial diverse population.
     *
     * @param maxAge maximum age for the random person
     * @return this builder instance configured with random seeding values
     */
    public PersonBuilder randomSeedingPerson(int maxAge) {


        // Random gender (50/50 distribution for natural population balance)
        this.gender = random.nextBoolean() ? Gender.MALE : Gender.FEMALE;

        // Generate random age between 18 and maxAge
        int age = 18 + random.nextInt(maxAge - 18 + 1);
        this.birthDate = LocalDate.now().minusYears(age);

        // Generate random names using NameGenerator
        this.firstName = nameGenerator.generateFirstName(gender);
        this.lastName = nameGenerator.generateLastName();

        // Random health status (weighted toward healthy)
        double healthRoll = random.nextDouble();
        if (healthRoll < 0.8) {
            this.healthStatus = HealthStatus.HEALTHY;
        } else if (healthRoll < 0.95) {
            this.healthStatus = HealthStatus.SICK;
        } else {
            this.healthStatus = HealthStatus.CRITICALLY_ILL;
        }

        // Random wealth status
        WealthStatus[] wealthOptions = WealthStatus.values();
        this.wealthStatus = wealthOptions[random.nextInt(wealthOptions.length)];

        // Generate completely random personality traits (0-100%) for seeding
        generateRandomSeedingTraits();

        return this;
    }

    /**
     * Creates a child person that inherits traits from two parents (for RFC-005).
     * This method handles trait inheritance with genetic variation.
     *
     * @param parent1 first parent
     * @param parent2 second parent
     * @param childBirthDate birth date for the child
     * @return this builder instance configured with inherited values
     */
    public PersonBuilder childFromParents(Person parent1, Person parent2, LocalDate childBirthDate) {
        if (parent1 == null || parent2 == null) {
            throw new IllegalArgumentException("Both parents must be non-null");
        }
        if (childBirthDate == null) {
            throw new IllegalArgumentException("Child birth date cannot be null");
        }

        this.birthDate = childBirthDate;

        // Random gender (50/50 chance)
        this.gender = random.nextBoolean() ? Gender.MALE : Gender.FEMALE;

        // Generate child names using NameGenerator (inherit last name from one parent)
        this.firstName = nameGenerator.generateFirstName(gender);
        this.lastName = parent1.getLastName(); // Simple inheritance - could be more sophisticated

        // Children are typically healthy at birth
        this.healthStatus = HealthStatus.HEALTHY;

        // Inherit wealth status from parents (take the better of the two)
        WealthStatus parent1Wealth = parent1.getWealthStatus();
        WealthStatus parent2Wealth = parent2.getWealthStatus();
        this.wealthStatus = parent1Wealth.ordinal() > parent2Wealth.ordinal() ? parent1Wealth : parent2Wealth;

        // Generate inherited personality traits with variation
        generateInheritedTraits(parent1, parent2);

        return this;
    }

    /**
     * Generates completely random personality traits (0-100%) for seeding persons.
     * This creates the founding population with maximum trait diversity.
     */
    private void generateRandomSeedingTraits() {
        personalityTraits.clear();
        for (PersonalityTrait trait : PersonalityTrait.values()) {
            // Generate random value from 0-100
            int intensity = random.nextInt(101);
            personalityTraits.put(trait, intensity);
        }
    }

    /**
     * Generates inherited personality traits from two parents with some variation.
     * This will be used for child generation in RFC-005.
     *
     * @param parent1 first parent
     * @param parent2 second parent
     */
    private void generateInheritedTraits(Person parent1, Person parent2) {
        personalityTraits.clear();

        for (PersonalityTrait trait : PersonalityTrait.values()) {
            // Get trait values from both parents
            int parent1Value = parent1.getPersonalityTrait(trait);
            int parent2Value = parent2.getPersonalityTrait(trait);

            // Calculate average of parents
            // instead of average take the value of one of the parents
            int inheritedValue = random.nextBoolean() ? parent1Value : parent2Value;

            // Add some random variation (±20 points)
            int variation = random.nextInt(41) - 20; // -20 to +20
            int childValue = Math.max(0, Math.min(100, inheritedValue + variation));

            personalityTraits.put(trait, childValue);
        }
    }

    /**
     * Builds and returns the Person instance with the configured attributes.
     *
     * <p>This method validates that all required fields are set and
     * creates a Person with the specified or default values. The personality
     * traits are set after Person creation using the new 0-100 scale.</p>
     *
     * @return a new Person instance
     * @throws IllegalStateException if required fields are not set
     */
    public Person build() {
        validateRequiredFields();

        // Ensure personality traits are set if not already configured
        if (personalityTraits.isEmpty()) {
            generateRandomSeedingTraits();
        }

        // Create the person with required fields
        Person person = new Person(firstName, lastName, gender, birthDate);

        // Set optional fields if specified
        if (healthStatus != null) {
            person.setHealthStatus(healthStatus);
        }
        if (wealthStatus != null) {
            person.setWealthStatus(wealthStatus);
        }

        // Set personality traits using the new 0-100 scale
        person.setPersonalityTraits(personalityTraits);

        return person;
    }

    /**
     * Validates that all required fields are set.
     */
    private void validateRequiredFields() {
        if (firstName == null) {
            throw new IllegalStateException("First name must be set");
        }
        if (lastName == null) {
            throw new IllegalStateException("Last name must be set");
        }
        if (gender == null) {
            throw new IllegalStateException("Gender must be set");
        }
        if (birthDate == null) {
            throw new IllegalStateException("Birth date must be set");
        }
    }
}
