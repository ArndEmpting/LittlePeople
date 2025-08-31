package com.littlepeople.core.model;

import com.littlepeople.core.interfaces.Entity;
import com.littlepeople.core.util.SimulationTimeProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.time.Period;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Represents an inhabitant in the simulation with demographic and relationship information.
 *
 * <p>The Person class is the central entity in the LittlePeople simulation,
 * representing an individual with complete demographic data, personality traits,
 * family relationships, and life status tracking. This class implements the
 * Entity interface from RFC-001 and serves as the foundation for all
 * population-based simulation mechanics.</p>
 *
 * <p>Key features include:</p>
 * <ul>
 *   <li>Immutable core identity (ID, gender, birth date)</li>
 *   <li>Mutable life status (health, wealth, relationships)</li>
 *   <li>Comprehensive personality trait system</li>
 *   <li>Bidirectional family relationship management</li>
 *   <li>Age calculation and life stage determination</li>
 *   <li>Death handling with cause tracking</li>
 * </ul>
 *
 * @since 1.0.0
 * @version 1.0.0
 */
public class Person implements Entity {

    private static final Logger logger = LoggerFactory.getLogger(Person.class);
    private static final int MAX_AGE_YEARS = 150;

    // Core immutable identity
    private final UUID id;
    private final Gender gender;
    private final LocalDate birthDate;

    // Mutable basic information
    private String firstName;
    private String lastName;

    // Life status
    private HealthStatus healthStatus;
    private WealthStatus wealthStatus;
    private LocalDate deathDate;
    private DeathCause deathCause;

    // Personality traits (trait -> intensity level 1-10)
    private final Map<PersonalityTrait, Integer> personalityTraits;

    // Relationships (using synchronized collections for thread safety)
    private Person partner;
    private final Set<Person> children;
    private final Set<Person> parents;
    private final Set<Person> formerPartner;

    /**
     * Creates a new person with the specified basic information.
     *
     * <p>This constructor performs comprehensive validation of all parameters
     * and initializes the person with default values for optional attributes.
     * The person is created in a healthy state with randomized personality traits.</p>
     *
     * @param firstName the person's first name (required, non-empty)
     * @param lastName the person's last name (required, non-empty)
     * @param gender the person's gender (required)
     * @param birthDate the person's birth date (required, not in future)
     * @throws IllegalArgumentException if any parameter is invalid
     */
    public Person(String firstName, String lastName, Gender gender, LocalDate birthDate) {
        // Validate required parameters
        if (firstName == null || firstName.trim().isEmpty()) {
            throw new IllegalArgumentException("First name cannot be null or empty");
        }
        if (lastName == null || lastName.trim().isEmpty()) {
            throw new IllegalArgumentException("Last name cannot be null or empty");
        }
        if (gender == null) {
            throw new IllegalArgumentException("Gender cannot be null");
        }
        if (birthDate == null) {
            throw new IllegalArgumentException("Birth date cannot be null");
        }
        if (birthDate.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("Birth date cannot be in the future: " + birthDate);
        }

        // Initialize immutable fields
        this.id = UUID.randomUUID();
        this.gender = gender;
        this.birthDate = birthDate;

        // Initialize mutable fields
        this.firstName = firstName.trim();
        this.lastName = lastName.trim();
        this.healthStatus = HealthStatus.HEALTHY;
        this.wealthStatus = WealthStatus.MIDDLE_CLASS; // Default to middle class

        // Initialize collections (thread-safe)
        this.personalityTraits = new ConcurrentHashMap<>();
        this.children = Collections.synchronizedSet(new HashSet<>());
        this.parents = Collections.synchronizedSet(new HashSet<>());
        this.formerPartner = Collections.synchronizedSet(new HashSet<>());

        // Do NOT initialize personality traits here - they will be set by PersonBuilder

        logger.debug("Created new person: {} {} (ID: {}, Gender: {}, Birth: {})",
                    firstName, lastName, id, gender, birthDate);
    }

    /**
     * Sets all personality traits at once.
     * This method is intended to be called by PersonBuilder during construction.
     *
     * @param traits map of personality traits and their intensities (0-100)
     * @throws IllegalArgumentException if any trait intensity is out of range
     */
    public void setPersonalityTraits(Map<PersonalityTrait, Integer> traits) {
        if (traits == null) {
            throw new IllegalArgumentException("Personality traits map cannot be null");
        }

        // Validate all trait intensities
        for (Map.Entry<PersonalityTrait, Integer> entry : traits.entrySet()) {
            int intensity = entry.getValue();
            if (intensity < 0 || intensity > 100) {
                throw new IllegalArgumentException("Personality trait " + entry.getKey().getDisplayName() +
                    " intensity must be between 0 and 100: " + intensity);
            }
        }

        // Clear existing traits and set new ones
        this.personalityTraits.clear();
        this.personalityTraits.putAll(traits);
    }

    @Override
    public UUID getId() {
        return id;
    }

    /**
     * Calculates the person's current age in years.
     *
     * <p>Age is calculated from birth date to current date for living persons,
     * <p>Age is calculated from birth date to current simulation date for living persons,
     * uses precise date arithmetic to handle leap years correctly.</p>
     *
     * @return the age in complete years
     * <p>For living persons, this method uses the current simulation time instead of
     * real system time to ensure consistency within the simulation.</p>
     *
     *
     * @throws IllegalStateException if no simulation clock has been set for living persons
    */
    public int getAge() {
        LocalDate endDate;
        if (isAlive()) {
            // For living persons, use simulation time
            if (SimulationTimeProvider.isClockSet()) {
                endDate = SimulationTimeProvider.getCurrentDate();
            } else {
                // Fallback to system time if no simulation clock is set (e.g., during testing)
                endDate = LocalDate.now();
                logger.warn("No simulation clock set for person {}, using system time for age calculation", id);
            }
        } else {
            // For deceased persons, use death date
            endDate = deathDate;
        }
        return Period.between(birthDate, endDate).getYears();
    }

    /**
     * Determines the person's current life stage based on age.
     *
     * @return the current life stage
     */
    public LifeStage getLifeStage() {
        return LifeStage.fromAge(getAge());
    }

    /**
     * Determines if this person is currently alive.
     *
     * @return true if alive (death date is null), false if deceased
     */
    public boolean isAlive() {
        return deathDate == null;
    }

    /**
     * Determines if this person is an adult (18+ years old).
     *
     * @return true if adult, false if child
     */
    public boolean isAdult() {
        return getLifeStage().isAdult();
    }

    /**
     * Forms a partnership with another person.
     *
     * <p>This method establishes a bidirectional partnership relationship
     * with comprehensive validation of partnership rules including:</p>
     * <ul>
     *   <li>Both persons must be alive</li>
     *   <li>Both persons must be adults</li>
     *   <li>Neither person can already have a partner</li>
     *   <li>Cannot partner with self</li>
     *   <li>Cannot partner with direct family members</li>
     * </ul>
     *
     * @param otherPerson the person to partner with (required)
     * @throws IllegalArgumentException if partnership is not valid
     */
    public void setPartner(Person otherPerson) {
        // Validate partnership
        validatePartnership(otherPerson);

        // Set bidirectional partnership
        this.partner = otherPerson;
        otherPerson.partner = this;

        logger.info("Partnership formed between {} {} and {} {}",
                   this.firstName, this.lastName,
                   otherPerson.firstName, otherPerson.lastName);
    }

    public boolean isDeceased() {
        return !isAlive();
    }

    /**
     * Validates whether a partnership can be formed with another person.
     */
    private void validatePartnership(Person otherPerson) {
        if (otherPerson == null) {
            throw new IllegalArgumentException("Partner cannot be null");
        }
        if (otherPerson == this) {
            throw new IllegalArgumentException("Cannot partner with self");
        }
        if (!this.isAlive()) {
            throw new IllegalArgumentException("Dead person cannot form partnerships");
        }
        if (!otherPerson.isAlive()) {
            throw new IllegalArgumentException("Cannot partner with dead person");
        }
        if (!this.isAdult()) {
            throw new IllegalArgumentException("Minors cannot form partnerships");
        }
        if (!otherPerson.isAdult()) {
            throw new IllegalArgumentException("Cannot partner with minor");
        }
        if (this.partner != null) {
            throw new IllegalArgumentException("Person already has a partner");
        }
        if (otherPerson.partner != null) {
            throw new IllegalArgumentException("Other person already has a partner");
        }
        if (isDirectFamily(otherPerson)) {
            throw new IllegalArgumentException("Cannot partner with direct family member");
        }
    }

    /**
     * Checks if another person is direct family (parent, child, or sibling).
     */
    private boolean isDirectFamily(Person otherPerson) {
        // Check if parent/child relationship
        if (parents.contains(otherPerson) || children.contains(otherPerson)) {
            return true;
        }

        // Check if siblings (share at least one parent)
        for (Person parent : parents) {
            if (otherPerson.parents.contains(parent)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Removes the current partnership.
     *
     * <p>This method safely dissolves a partnership by removing the
     * bidirectional relationship between both partners.</p>
     */
    public void removePartnership() {
        if (partner != null) {
            Person formerPartner = partner;
            this.partner = null;
            formerPartner.partner = null;

            logger.debug("Partnership dissolved between {} {} and {} {}",
                       this.firstName, this.lastName,
                       formerPartner.firstName, formerPartner.lastName);
        }
    }

    /**
     * Adds a child to this person's family.
     *
     * <p>This method establishes a bidirectional parent-child relationship
     * with validation to ensure data integrity. The child is also added
     * to the other parent if this person has a partner.</p>
     *
     * @param child the child to add (required, must be younger)
     * @throws IllegalArgumentException if child is invalid or relationship already exists
     */
    public void addChild(Person child) {
        validateChildRelationship(child);

        // Add bidirectional relationship
        if(!this.children.contains(child)) {
            this.children.add(child);
        }
        if(!child.parents.contains(this)) {
            child.parents.add(this);
        }

        // If this person has a partner, add child to partner as well
        if (partner != null && !partner.children.contains(child)) {
            partner.children.add(child);
            if(!child.parents.contains(partner)) {
                child.parents.add(partner);
            }
        }

        logger.debug("Added child {} {} to parent {} {}",
                    child.firstName, child.lastName,
                    this.firstName, this.lastName);
    }

    /**
     * Validates whether a child relationship can be established.
     */
    private void validateChildRelationship(Person child) {
        if (child == null) {
            throw new IllegalArgumentException("Child cannot be null");
        }
        if (child == this) {
            throw new IllegalArgumentException("Person cannot be their own child");
        }

        if (child.birthDate.isBefore(this.birthDate)) {
            throw new IllegalArgumentException("Child cannot be older than parent");
        }

        // Check minimum age difference (e.g., 12 years)
        int ageDifference = Period.between(this.birthDate, child.birthDate).getYears();
        if (ageDifference < 12) {
            throw new IllegalArgumentException("Parent must be at least 12 years older than child");
        }
    }

    /**
     * Marks this person as deceased with the specified date and cause.
     *
     * <p>Death handling includes:</p>
     * <ul>
     *   <li>Setting death date and cause</li>
     *   <li>Dissolving partnerships</li>
     *   <li>Updating family relationships</li>
     *   <li>Logging the death event</li>
     * </ul>
     *
     * @param deathDate the date of death (required, not before birth, not in future)
     * @param cause the cause of death (optional, defaults to UNEXPLAINED)
     * @throws IllegalArgumentException if death date is invalid
     */
    public void markDeceased(LocalDate deathDate, DeathCause cause) {
        validateDeathDate(deathDate);

        this.deathDate = deathDate;
        this.deathCause = (cause != null) ? cause : DeathCause.UNEXPLAINED;

        // Dissolve partnership
        removePartnership();

        logger.debug("Person deceased: {} {} (Age: {}, Cause: {})",
                   firstName, lastName, getAge(), this.deathCause.getDescription());
    }

    /**
     * Convenience method to mark deceased with unknown cause.
     */
    public void markDeceased(LocalDate deathDate) {
        markDeceased(deathDate, DeathCause.UNEXPLAINED);
    }

    /**
     * Validates the death date.
     */
    private void validateDeathDate(LocalDate deathDate) {
        if (deathDate == null) {
            throw new IllegalArgumentException("Death date cannot be null");
        }
        if (deathDate.isBefore(birthDate)) {
            throw new IllegalArgumentException("Death date cannot be before birth date");
        }

        if (!isAlive()) {
            throw new IllegalArgumentException("Person is already deceased");
        }

        int ageAtDeath = Period.between(birthDate, deathDate).getYears();
        if (ageAtDeath > MAX_AGE_YEARS) {
            throw new IllegalArgumentException("Age at death exceeds maximum: " + ageAtDeath);
        }
    }

    /**
     * Sets a personality trait to a specific intensity level.
     *
     * @param trait the personality trait to set
     * @param intensity the intensity level (1-10)
     * @throws IllegalArgumentException if intensity is out of range
     */
    public void setPersonalityTrait(PersonalityTrait trait, int intensity) {
        if (trait == null) {
            throw new IllegalArgumentException("Personality trait cannot be null");
        }
        if (intensity < 1 || intensity > 10) {
            throw new IllegalArgumentException("Personality trait intensity must be between 1 and 10: " + intensity);
        }
        personalityTraits.put(trait, intensity);
    }

    /**
     * Gets the intensity level of a personality trait.
     *
     * @param trait the personality trait to query
     * @return the intensity level (1-10), or 5 (default) if not set
     */
    public int getPersonalityTrait(PersonalityTrait trait) {
        return personalityTraits.getOrDefault(trait, 5); // Default to middle value
    }

    // Getters and setters

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        if (firstName == null || firstName.trim().isEmpty()) {
            throw new IllegalArgumentException("First name cannot be null or empty");
        }
        this.firstName = firstName.trim();
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        if (lastName == null || lastName.trim().isEmpty()) {
            throw new IllegalArgumentException("Last name cannot be null or empty");
        }
        this.lastName = lastName.trim();
    }

    public String getFullName() {
        return firstName + " " + lastName;
    }

    public Gender getGender() {
        return gender;
    }

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public HealthStatus getHealthStatus() {
        return healthStatus;
    }

    public void setHealthStatus(HealthStatus healthStatus) {
        if (healthStatus == null) {
            throw new IllegalArgumentException("Health status cannot be null");
        }
        this.healthStatus = healthStatus;
    }

    public WealthStatus getWealthStatus() {
        return wealthStatus;
    }

    public void setWealthStatus(WealthStatus wealthStatus) {
        if (wealthStatus == null) {
            throw new IllegalArgumentException("Wealth status cannot be null");
        }
        this.wealthStatus = wealthStatus;
    }

    public LocalDate getDeathDate() {
        return deathDate;
    }

    public DeathCause getDeathCause() {
        return deathCause;
    }

    public Person getPartner() {
        return partner;
    }

    /**
     * Returns a defensive copy of the children set.
     *
     * @return unmodifiable set of children
     */
    public Set<Person> getChildren() {
        return Collections.unmodifiableSet(new HashSet<>(children));
    }

    /**
     * Returns a defensive copy of the parents set.
     *
     * @return unmodifiable set of parents
     */
    public Set<Person> getParents() {
        return Collections.unmodifiableSet(new HashSet<>(parents));
    }

    /**
     * Returns a defensive copy of the personality traits map.
     *
     * @return unmodifiable map of personality traits
     */
    public Map<PersonalityTrait, Integer> getPersonalityTraits() {
        return Collections.unmodifiableMap(new HashMap<>(personalityTraits));
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Person person = (Person) obj;
        return Objects.equals(id, person.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return String.format("Person{id=%s, name='%s %s', gender=%s, age=%d, alive=%s}",
                           id, firstName, lastName, gender, getAge(), isAlive());
    }

    public  int getMaxAgeYears() {
        return MAX_AGE_YEARS;
    }

    public Set<Person> getFormerPartner() {
        return this.formerPartner;
    }
    public void addFormerPartner(Person person) {
        this.formerPartner.add(person);
    }
}
