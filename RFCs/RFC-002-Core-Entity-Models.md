# RFC-002: Core Entity Models

**Version:** 1.0  
**Date:** August 27, 2025  
**Status:** Draft  
**Authors:** LittlePeople Development Team  
**Complexity:** Medium

## Summary

This RFC defines the core entity models that form the foundation of the LittlePeople simulation engine. It establishes the primary domain objects representing inhabitants (Person), their attributes, relationships, and supporting enumerations. These models will be used throughout the application and must be designed for extensibility, performance, and accurate representation of demographic characteristics.

## Features Addressed

- **F003:** Inhabitant Generation (partial - core model)
- **F012:** Family System & Child Generation (data model aspects)
- **F011:** Partnership Formation (data model aspects)
- **F008:** Population Persistence (data model aspects)

## Technical Approach

### Core Domain Models

The LittlePeople simulation engine will be built around a set of core domain models that represent the fundamental entities in the system. The most important entity is the `Person` class, which represents an inhabitant in the simulation.

#### Person Entity

The `Person` class will be the central entity in the system, representing an individual inhabitant with:
- Unique identifier
- Demographic information (name, gender, birth/death dates)
- Health status
- Personality traits (
      Openness, Conscientiousness, Extraversion, Agreeableness, Neuroticism ,Intelligence ,Ambition, Empathy, Humor, Patience, Creativity, Resilience, Curiosity, Altruism, Confidence, Optimism, Cautiousness) with a scoring system
- Wealth status
- Family relationships (partner, parents, children)
- Life status tracking (alive/deceased)

where suitable (like personality traits, Wealth status and Health status ) those attributes will be represented using enumerations with a numeric value representation. 
That way we know how strong a certain trait is represented in a person. or how wealthy a person is. or health can detieriorate over time. and health and wealth can change from one state to another.

#### Supporting Enumerations

The system requires several enumerations to model discrete states and characteristics:
- `Gender`: Biological gender (MALE, FEMALE)
- `HealthStatus`: Health condition (HEALTHY, SICK, CRITICALLY_ILL)
- `WealthStatus`: Economic status (POOR, LOWER_MIDDLE_CLASS, MIDDLE_CLASS, UPPER_MIDDLE_CLASS, RICH)
- `RelationshipType`: Types of relationships between people (PARTNER, PARENT, CHILD, SIBLING)
- `DeathCause`: Reasons for death ( DISEASE, NATURAL_OLD_AGE,  ACCIDENT, VIOLENT, UNEXPLAINED, BIRTH_COMPLICATION etc.)

### Data Validation and Integrity

Each core model will include validation logic to ensure data integrity:
- Age limits and constraints (no negative ages, maximum age limit)
- Birth date validation (no future dates)
- Relationship integrity (parent-child relationship consistency)
- Partner relationship rules (single partner at a time)

### Factory Classes

Factory classes will be implemented to create and initialize instances of the core entities:
- `PersonFactory`: Creates new Person instances with appropriate random or specified attributes
- `RelationshipFactory`: Establishes relationships between Person instances

## Technical Specifications

### Person Class

```java
/**
 * Represents an inhabitant in the simulation with basic demographic and relationship information.
 */
public class Person {
    
    // Core identity
    private final UUID id;
    private String firstName;
    private String lastName;
    private final Gender gender;
    
    // Life events
    private final LocalDate birthDate;
    private LocalDate deathDate;
    private HealthStatus healthStatus;
    
    // Relationships
    private Person partner;
    private final List<Person> children;
    private final List<Person> parents;
    
    /**
     * Creates a new person with the specified basic information.
     * 
     * @param firstName the person's first name
     * @param lastName the person's last name
     * @param gender the person's gender
     * @param birthDate the person's birth date
     * @throws IllegalArgumentException if any parameter is invalid
     */
    public Person(String firstName, String lastName, Gender gender, LocalDate birthDate) {
        // Validation and initialization logic
    }
    
    /**
     * Calculates the person's current age in years.
     * 
     * @return the age in years, or age at death if deceased
     */
    public int getAge() {
        // Age calculation logic
    }
    
    /**
     * Determines if this person is currently alive.
     * 
     * @return true if alive, false if deceased
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
        // Adult status logic
    }
    
    /**
     * Forms a partnership with another person.
     * Both persons must be available for partnership.
     * 
     * @param otherPerson the person to partner with
     * @throws IllegalArgumentException if partnership is not valid
     */
    public void setPartner(Person otherPerson) {
        // Partnership formation logic with validation
    }
    
    /**
     * Adds a child to this person's family.
     * Also sets this person as the child's parent.
     * 
     * @param child the child to add
     * @throws IllegalArgumentException if child is null or already has this parent
     */
    public void addChild(Person child) {
        // Child relationship logic
    }
    
    /**
     * Marks this person as deceased.
     * 
     * @param deathDate the date of death
     * @throws IllegalArgumentException if death date is invalid
     */
    public void markDeceased(LocalDate deathDate) {
        // Death handling logic
    }
    
    // Getters, equals, hashCode, toString, etc.
}
```

### Gender Enumeration

```java
/**
 * Enumeration representing the gender of a person in the simulation.
 */
public enum Gender {
    /**
     * Male gender
     */
    MALE,
    
    /**
     * Female gender
     */
    FEMALE
}
```

### HealthStatus Enumeration

```java
/**
 * Enumeration representing the health status of a person.
 */
public enum HealthStatus {
    /**
     * Person is in good health with no significant issues
     */
    HEALTHY,
    
    /**
     * Person has minor health issues
     */
    SICK,
    
    /**
     * Person has serious health issues with increased mortality risk
     */
    CRITICALLY_ILL
}
```

### PersonFactory Class

```java
/**
 * Factory for creating Person instances with various attributes.
 */
public class PersonFactory {
    
    private final Random random;
    private final NameGenerator nameGenerator;
    
    /**
     * Creates a random person based on configuration parameters.
     * 
     * @param gender specific gender or null for random
     * @param maxAge maximum age of the generated person
     * @return a new Person instance with random attributes
     */
    public Person createRandomPerson(Gender gender, int maxAge) {
        // Random person generation logic
    }
    
    /**
     * Creates a child from two parents.
     * 
     * @param parent1 first parent
     * @param parent2 second parent
     * @param birthDate birth date of the child
     * @return a new Person instance representing the child
     */
    public Person createChild(Person parent1, Person parent2, LocalDate birthDate) {
        // Child generation logic with inheritance from parents
    }
}
```

## Implementation Details

### Person Class Implementation

1. **Constructor and Validation**
   - Validate all input parameters (non-null, appropriate ranges)
   - Initialize collections for children and parents
   - Set default health status

2. **Age and Life Stage Calculation**
   - Implement age calculation based on birth date and current/death date
   - Define life stage determination (child, adult, elderly)
   - Add utility methods for common age-related queries

3. **Relationship Management**
   - Implement bidirectional relationship handling (partner, parent-child)
   - Ensure relationship integrity when adding/removing relationships
   - Add relationship validation logic

4. **Death Handling**
   - Implement death date validation and setting
   - Add appropriate state changes when a person dies (partner relationship handling)

### Data Model Considerations

1. **Immutability**
   - Make core identity properties immutable (id, gender, birth date)
   - Ensure mutable properties have appropriate validation in setters

2. **Collection Handling**
   - Return defensive copies of collections to prevent external modification
   - Consider thread safety for concurrent access

3. **Serialization**
   - Implement proper serialization handling for persistence
   - Consider Jackson annotations for JSON serialization
   - Handle bidirectional relationships in serialization to avoid circular references

4. **Performance Optimization**
   - Use appropriate data structures for relationship lookups
   - Consider caching calculated values like age
   - Minimize object creation during simulation

## Acceptance Criteria

- [x] Person class with complete demographic attributes implemented
- [x] Gender and HealthStatus enumerations defined
- [x] Relationship management methods function correctly (partner, children, parents)
- [x] Age calculation works correctly for living and deceased persons
- [x] Validation logic prevents invalid data (future birth dates, etc.)
- [x] PersonFactory creates valid Person instances with appropriate attributes
- [x] Unit tests cover all public methods with >85% coverage
- [x] JavaDoc documentation complete for all public classes and methods
- [x] Serialization/deserialization works correctly for all entity classes

## Dependencies

**Builds Upon:** RFC-001: Project Setup and Architecture

**Required For:** 
- RFC-003: Simulation Clock and Event System
- RFC-004: Life Cycle - Aging and Mortality
- RFC-005: Partnerships and Family Formation

## Testing Strategy

### Unit Tests

#### Person Class Tests
- Test person creation with valid/invalid parameters
- Test age calculation for various scenarios (current age, age at death)
- Test relationship methods (addChild, setPartner)
- Test validation logic
- Test life status methods (isAlive, isAdult)

#### Factory Tests
- Test random person generation with various parameters
- Test child generation from parents
- Test name generation functionality

### Integration Tests
- Test serialization/deserialization of Person objects
- Test relationship integrity across operations

## Security Considerations

- Ensure proper validation of all input data
- Consider privacy implications when generating and storing demographic data
- Implement validation to prevent impossible or illogical demographic data

## Performance Considerations

- Optimize relationship lookups for large family structures
- Consider memory usage for large populations (10000+ inhabitants)
- Optimize age calculation for frequent access
- Consider caching computed values that don't change often

## Open Questions

1. Should we support non-binary gender options in the future?
2. How detailed should health status be for the MVP?
3. Should we implement different naming conventions for different cultural contexts?
4. How should we handle serialization of bidirectional relationships?

## References

- [Java Time API Documentation](https://docs.oracle.com/en/java/javase/17/docs/api/java.base/java/time/package-summary.html)
- PRD.md - Requirements for core entities
- features.md - Feature specifications
- [Design Patterns: Factory Pattern](https://refactoring.guru/design-patterns/factory-method)
