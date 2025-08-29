package com.littlepeople.core.model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;

/**
 * Validator class for Person entities and business rules.
 *
 * <p>This class provides comprehensive validation methods for Person entities
 * and related business rules. It validates entity state, relationship
 * constraints, and business logic requirements to ensure data integrity
 * throughout the simulation.</p>
 *
 * <p>Key validation areas include:</p>
 * <ul>
 *   <li>Person entity validation</li>
 *   <li>Relationship constraints</li>
 *   <li>Age and life stage consistency</li>
 *   <li>Partnership rules</li>
 *   <li>Family structure validation</li>
 * </ul>
 *
 * @since 1.0.0
 * @version 1.0.0
 */
public class PersonValidator {

    private static final Logger logger = LoggerFactory.getLogger(PersonValidator.class);
    private static final int MIN_PARENT_AGE = 12;
    private static final int MAX_AGE_YEARS = 150;
    private static final int ADULT_AGE = 18;

    /**
     * Validates a Person entity for basic integrity.
     *
     * @param person the person to validate
     * @return list of validation errors (empty if valid)
     */
    public static List<String> validatePerson(Person person) {
        List<String> errors = new ArrayList<>();

        if (person == null) {
            errors.add("Person cannot be null");
            return errors;
        }

        // Validate basic attributes
        validateBasicAttributes(person, errors);

        // Validate age and life stage consistency
        validateAgeConsistency(person, errors);

        // Validate relationships
        validateRelationships(person, errors);

        // Validate personality traits
        validatePersonalityTraits(person, errors);

        return errors;
    }

    /**
     * Validates basic person attributes.
     */
    private static void validateBasicAttributes(Person person, List<String> errors) {
        // Name validation
        if (person.getFirstName() == null || person.getFirstName().trim().isEmpty()) {
            errors.add("First name cannot be null or empty");
        }
        if (person.getLastName() == null || person.getLastName().trim().isEmpty()) {
            errors.add("Last name cannot be null or empty");
        }

        // Gender validation
        if (person.getGender() == null) {
            errors.add("Gender cannot be null");
        }

        // Birth date validation
        if (person.getBirthDate() == null) {
            errors.add("Birth date cannot be null");
        } else {
            if (person.getBirthDate().isAfter(LocalDate.now())) {
                errors.add("Birth date cannot be in the future");
            }
        }

        // Health status validation
        if (person.getHealthStatus() == null) {
            errors.add("Health status cannot be null");
        }

        // Wealth status validation
        if (person.getWealthStatus() == null) {
            errors.add("Wealth status cannot be null");
        }

        // Death date validation
        if (!person.isAlive() && person.getDeathDate() != null) {
            if (person.getDeathDate().isBefore(person.getBirthDate())) {
                errors.add("Death date cannot be before birth date");
            }
            if (person.getDeathDate().isAfter(LocalDate.now())) {
                errors.add("Death date cannot be in the future");
            }
        }
    }

    /**
     * Validates age and life stage consistency.
     */
    private static void validateAgeConsistency(Person person, List<String> errors) {
        int age = person.getAge();

        // Age limits
        if (age < 0) {
            errors.add("Age cannot be negative");
        }
        if (age > MAX_AGE_YEARS) {
            errors.add("Age exceeds maximum allowed: " + age);
        }

        // Life stage consistency
        LifeStage expectedStage = LifeStage.fromAge(age);
        if (person.getLifeStage() != expectedStage) {
            errors.add("Life stage inconsistent with age");
        }

        // Adult status consistency
        boolean shouldBeAdult = age >= ADULT_AGE;
        if (person.isAdult() != shouldBeAdult) {
            errors.add("Adult status inconsistent with age");
        }
    }

    /**
     * Validates relationship constraints.
     */
    private static void validateRelationships(Person person, List<String> errors) {
        // Partnership validation
        validatePartnership(person, errors);

        // Parent-child relationships
        validateParentChildRelationships(person, errors);

        // Relationship consistency
        validateRelationshipConsistency(person, errors);
    }

    /**
     * Validates partnership rules.
     */
    private static void validatePartnership(Person person, List<String> errors) {
        if (person.getPartner() != null) {
            Person partner = person.getPartner();

            // Both must be adults
            if (!person.isAdult()) {
                errors.add("Person in partnership must be adult");
            }
            if (!partner.isAdult()) {
                errors.add("Partner must be adult");
            }

            // Both must be alive
            if (!person.isAlive()) {
                errors.add("Dead person cannot have partner");
            }
            if (!partner.isAlive()) {
                errors.add("Partner must be alive");
            }

            // Bidirectional relationship
            if (partner.getPartner() != person) {
                errors.add("Partnership not bidirectional");
            }

            // Cannot be direct family
            if (isDirectFamily(person, partner)) {
                errors.add("Cannot partner with direct family member");
            }
        }
    }

    /**
     * Validates parent-child relationships.
     */
    private static void validateParentChildRelationships(Person person, List<String> errors) {
        // Validate children
        for (Person child : person.getChildren()) {
            // Age difference
            int ageDifference = Period.between(person.getBirthDate(), child.getBirthDate()).getYears();
            if (ageDifference < MIN_PARENT_AGE) {
                errors.add("Parent must be at least " + MIN_PARENT_AGE + " years older than child");
            }

            // Child must have this person as parent
            if (!child.getParents().contains(person)) {
                errors.add("Child relationship not bidirectional");
            }
        }

        // Validate parents
        for (Person parent : person.getParents()) {
            // Age difference
            int ageDifference = Period.between(parent.getBirthDate(), person.getBirthDate()).getYears();
            if (ageDifference < MIN_PARENT_AGE) {
                errors.add("Parent must be at least " + MIN_PARENT_AGE + " years older than person");
            }

            // Parent must have this person as child
            if (!parent.getChildren().contains(person)) {
                errors.add("Parent relationship not bidirectional");
            }
        }

        // Maximum number of parents
        if (person.getParents().size() > 2) {
            errors.add("Person cannot have more than 2 parents");
        }
    }

    /**
     * Validates relationship consistency.
     */
    private static void validateRelationshipConsistency(Person person, List<String> errors) {
        // Cannot be own parent or child
        if (person.getParents().contains(person)) {
            errors.add("Person cannot be their own parent");
        }
        if (person.getChildren().contains(person)) {
            errors.add("Person cannot be their own child");
        }

        // Cannot be own partner
        if (person.getPartner() == person) {
            errors.add("Person cannot be their own partner");
        }
    }

    /**
     * Validates personality traits.
     */
    private static void validatePersonalityTraits(Person person, List<String> errors) {
        for (PersonalityTrait trait : PersonalityTrait.values()) {
            int intensity = person.getPersonalityTrait(trait);
            if (intensity < 1 || intensity > 10) {
                errors.add("Personality trait " + trait.getDisplayName() + " intensity out of range: " + intensity);
            }
        }
    }

    /**
     * Checks if two persons are direct family members.
     */
    private static boolean isDirectFamily(Person person1, Person person2) {
        // Parent-child relationship
        if (person1.getParents().contains(person2) || person1.getChildren().contains(person2)) {
            return true;
        }

        // Sibling relationship (share at least one parent)
        for (Person parent : person1.getParents()) {
            if (person2.getParents().contains(parent)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Validates partnership compatibility between two persons.
     *
     * @param person1 first person
     * @param person2 second person
     * @return list of validation errors preventing partnership
     */
    public static List<String> validatePartnershipCompatibility(Person person1, Person person2) {
        List<String> errors = new ArrayList<>();

        if (person1 == null || person2 == null) {
            errors.add("Both persons must be non-null");
            return errors;
        }

        if (person1 == person2) {
            errors.add("Person cannot partner with themselves");
        }

        if (!person1.isAlive() || !person2.isAlive()) {
            errors.add("Both persons must be alive");
        }

        if (!person1.isAdult() || !person2.isAdult()) {
            errors.add("Both persons must be adults");
        }

        if (person1.getPartner() != null || person2.getPartner() != null) {
            errors.add("Both persons must be available (no existing partners)");
        }

        if (isDirectFamily(person1, person2)) {
            errors.add("Cannot partner with direct family member");
        }

        return errors;
    }

    /**
     * Validates whether a person can adopt a child.
     *
     * @param parent potential parent
     * @param child potential child
     * @return list of validation errors preventing adoption
     */
    public static List<String> validateChildAdoption(Person parent, Person child) {
        List<String> errors = new ArrayList<>();

        if (parent == null || child == null) {
            errors.add("Both parent and child must be non-null");
            return errors;
        }

        if (parent == child) {
            errors.add("Person cannot be their own child");
        }

        if (parent.getChildren().contains(child)) {
            errors.add("Child relationship already exists");
        }

        if (child.getBirthDate().isBefore(parent.getBirthDate())) {
            errors.add("Child cannot be older than parent");
        }

        int ageDifference = Period.between(parent.getBirthDate(), child.getBirthDate()).getYears();
        if (ageDifference < MIN_PARENT_AGE) {
            errors.add("Parent must be at least " + MIN_PARENT_AGE + " years older than child");
        }

        if (child.getParents().size() >= 2) {
            errors.add("Child already has maximum number of parents (2)");
        }

        return errors;
    }

    /**
     * Checks if a person is in a valid state for simulation.
     *
     * @param person the person to check
     * @return true if valid, false otherwise
     */
    public static boolean isValidForSimulation(Person person) {
        List<String> errors = validatePerson(person);
        if (!errors.isEmpty()) {
            logger.warn("Person validation failed for {}: {}", person.getFullName(), errors);
            return false;
        }
        return true;
    }
}
