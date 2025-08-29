# LittlePeople Development Rules & Guidelines

**Version:** 1.0  
**Date:** August 26, 2025  
**Project:** LittlePeople Life Simulation Engine  
**Document Type:** Development Guidelines

---

## Table of Contents

1. [Technology Stack & Versions](#technology-stack--versions)
2. [Project Structure & Architecture](#project-structure--architecture)
3. [Code Standards & Conventions](#code-standards--conventions)
4. [Testing & Quality Assurance](#testing--quality-assurance)
5. [Performance & Security](#performance--security)
6. [Documentation Standards](#documentation-standards)
7. [Implementation Priorities](#implementation-priorities)
8. [Development Workflow](#development-workflow)
9. [AI Assistant Guidelines](#ai-assistant-guidelines)

---

## Technology Stack & Versions

### Core Technologies (MUST USE)
- **Java:** 21 LTS (minimum: Java 17 for compatibility)
- **Build System:** Maven 3.9.x
- **Database (MVP):** SQLite JDBC 3.42.0.0
- **Database (Future):** PostgreSQL 15.x
- **Testing:** JUnit 5.10.x
- **Logging:** SLF4J 2.0.9 with Logback
- **Configuration:** Jackson 2.15.x for JSON/YAML parsing

### Development Tools (RECOMMENDED)
- **IDE:** IntelliJ IDEA (any recent version)
- **Code Coverage:** JaCoCo Maven Plugin
- **Static Analysis:** SpotBugs, PMD, Checkstyle
- **Documentation:** JavaDoc for API, Markdown for user docs

### Dependencies Management Rules
- **MUST:** Use Maven dependency management with version properties
- **MUST:** Pin all dependency versions explicitly (no version ranges)
- **MUST:** Keep dependencies up-to-date with security patches
- **SHOULD:** Minimize external dependencies to reduce attack surface
- **MUST NOT:** Include dependencies with known security vulnerabilities

```xml
<!-- Example dependency declaration -->
<dependency>
    <groupId>org.xerial</groupId>
    <artifactId>sqlite-jdbc</artifactId>
    <version>${sqlite.version}</version>
</dependency>
```

---

## Project Structure & Architecture

### Module Structure (MUST FOLLOW)
```
src/main/java/
??? com.littlepeople.core/           # Core entities and interfaces
?   ??? model/                       # Person, Event, Configuration classes
?   ??? interfaces/                  # Core interfaces and contracts
?   ??? exceptions/                  # Custom exceptions
??? com.littlepeople.simulation/     # Life cycle simulation logic
?   ??? engine/                      # Main simulation engine
?   ??? processors/                  # Event processors
?   ??? demographics/                # Population management
?   ??? clock/                       # Time management
??? com.littlepeople.persistence/    # Data storage and retrieval
?   ??? dao/                         # Data access objects
?   ??? entities/                    # JPA/database entities
?   ??? migrations/                  # Database schema migrations
??? com.littlepeople.ui/            # User interface components
?   ??? console/                     # Console interface implementation
?   ??? commands/                    # CLI command handlers
??? com.littlepeople.extensions/     # Plugin framework
    ??? api/                         # Extension APIs
    ??? loader/                      # Plugin loading system
    ??? samples/                     # Example extensions
```

### Architectural Principles (MUST FOLLOW)
1. **Modular Design:** Each module must have clear responsibilities and minimal coupling
2. **Interface Segregation:** Define small, focused interfaces rather than large monolithic ones
3. **Dependency Injection:** Use constructor injection for dependencies
4. **Event-Driven Architecture:** Core simulation operates through event processing
5. **Plugin Architecture:** Core system must remain unchanged when adding extensions

### Package Naming Convention (MUST FOLLOW)
- **Pattern:** `com.littlepeople.<module>.<component>`
- **Examples:**
  - `com.littlepeople.core.model.Person`
  - `com.littlepeople.simulation.engine.SimulationEngine`
  - `com.littlepeople.persistence.dao.PersonDAO`

---

## Code Standards & Conventions

### Naming Conventions (MUST FOLLOW)

#### Classes and Interfaces
- **Classes:** PascalCase, descriptive nouns
  - `Person`, `SimulationEngine`, `PopulationManager`
- **Interfaces:** PascalCase, often ending with -able or starting with I
  - `LifeEventProcessor`, `Configurable`, `IPersistenceManager`
- **Abstract Classes:** PascalCase, often starting with Abstract
  - `AbstractEventProcessor`, `AbstractDAO`

#### Methods and Variables
- **Methods:** camelCase, verb phrases describing actions
  - `generatePerson()`, `processLifeEvent()`, `saveSimulationState()`
- **Variables:** camelCase, descriptive nouns
  - `currentPopulation`, `simulationClock`, `eventQueue`
- **Constants:** UPPER_SNAKE_CASE
  - `MAX_POPULATION_SIZE`, `DEFAULT_MORTALITY_RATE`

#### Files and Directories
- **Java Files:** Match class name exactly
- **Configuration Files:** lowercase with hyphens
  - `simulation-config.yaml`, `database-migration.sql`
- **Test Files:** Same as source with `Test` suffix
  - `PersonTest.java`, `SimulationEngineTest.java`

### Code Structure Rules (MUST FOLLOW)

#### Class Organization
```java
public class Person {
    // 1. Static constants
    private static final int MAX_AGE = 150;
    
    // 2. Instance variables (private)
    private final UUID id;
    private String firstName;
    private LocalDate birthDate;
    
    // 3. Constructors
    public Person(String firstName, LocalDate birthDate) {
        this.id = UUID.randomUUID();
        this.firstName = firstName;
        this.birthDate = birthDate;
    }
    
    // 4. Public methods
    public int getAge() {
        return Period.between(birthDate, LocalDate.now()).getYears();
    }
    
    // 5. Package-private methods
    void updateHealth(HealthStatus newStatus) {
        // Implementation
    }
    
    // 6. Private methods
    private void validateAge() {
        // Implementation
    }
    
    // 7. Getters and setters (if needed)
    public String getFirstName() {
        return firstName;
    }
}
```

#### Method Guidelines
- **MUST:** Keep methods under 50 lines
- **MUST:** Single responsibility per method
- **MUST:** Use descriptive parameter names
- **SHOULD:** Prefer immutable objects where possible
- **MUST NOT:** Use magic numbers or strings

```java
// GOOD
public void agePopulation(int years) {
    for (Person person : population) {
        person.increaseAge(years);
        checkMortalityRisk(person);
    }
}

// BAD
public void agePopulation(int y) {
    for (Person p : population) {
        p.age += y;
        if (Math.random() < 0.1) { // Magic number!
            p.die();
        }
    }
}
```

### Error Handling Rules (MUST FOLLOW)

#### Exception Hierarchy
```java
// Base exception for all simulation errors
public class SimulationException extends Exception {
    public SimulationException(String message, Throwable cause) {
        super(message, cause);
    }
}

// Specific exceptions for different error types
public class PopulationException extends SimulationException { }
public class ConfigurationException extends SimulationException { }
public class PersistenceException extends SimulationException { }
```

#### Exception Handling Guidelines
- **MUST:** Use specific exception types, not generic Exception
- **MUST:** Include meaningful error messages with context
- **MUST:** Log all exceptions with appropriate severity level
- **SHOULD:** Use try-with-resources for resource management
- **MUST NOT:** Catch and ignore exceptions silently

```java
// GOOD
public void saveSimulation(SimulationState state) throws PersistenceException {
    try (Connection conn = dataSource.getConnection()) {
        saveStateToDatabase(conn, state);
        logger.info("Simulation saved successfully: {}", state.getName());
    } catch (SQLException e) {
        logger.error("Failed to save simulation: {}", state.getName(), e);
        throw new PersistenceException("Could not save simulation state", e);
    }
}
```

---

## Testing & Quality Assurance

### Testing Requirements (MUST FOLLOW)

#### Coverage Requirements
- **Unit Tests:** Minimum 85% line coverage
- **Integration Tests:** All public APIs and critical workflows
- **Performance Tests:** All features handling large datasets (1000+ inhabitants)
- **Cross-Platform Tests:** Windows, macOS, Linux compatibility

#### Test Structure
```java
class PersonTest {
    
    @Test
    @DisplayName("Should calculate correct age from birth date")
    void shouldCalculateCorrectAge() {
        // Given
        LocalDate birthDate = LocalDate.of(1990, 5, 15);
        Person person = new Person("John", "Doe", birthDate);
        
        // When
        int age = person.getAge();
        
        // Then
        assertThat(age).isEqualTo(35); // Assuming current year is 2025
    }
    
    @Test
    @DisplayName("Should throw exception for future birth date")
    void shouldThrowExceptionForFutureBirthDate() {
        // Given
        LocalDate futureBirthDate = LocalDate.now().plusYears(1);
        
        // When & Then
        assertThatThrownBy(() -> new Person("John", "Doe", futureBirthDate))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Birth date cannot be in the future");
    }
}
```

#### Test Guidelines
- **MUST:** Use Given-When-Then structure for test clarity
- **MUST:** Use descriptive test method names with @DisplayName
- **MUST:** Test both happy path and error conditions
- **MUST:** Use AssertJ for fluent assertions
- **SHOULD:** Use @ParameterizedTest for multiple similar test cases
- **MUST NOT:** Have interdependent tests

### Code Quality Rules (MUST FOLLOW)

#### Static Analysis
- **MUST:** Pass all SpotBugs checks without warnings
- **MUST:** Follow PMD rules (custom ruleset defined in pom.xml)
- **MUST:** Maintain Checkstyle compliance
- **MUST:** Achieve minimum 8.0/10 SonarQube quality gate score

#### Code Review Requirements
- **MUST:** All code changes require peer review
- **MUST:** Review checklist includes: functionality, tests, documentation, performance
- **MUST:** Address all review comments before merging
- **SHOULD:** Keep pull requests small and focused (<500 lines)

---

## Performance & Security

### Performance Requirements (MUST MEET)

#### Simulation Performance
- **Target:** Process 500 inhabitants/year in <1 second
- **Memory:** Peak usage <2GB for 1000 inhabitants over 100 years
- **Startup:** Application launch <10 seconds
- **Persistence:** Save/load operations <5 seconds for typical datasets

#### Performance Guidelines
- **MUST:** Use efficient algorithms for population management (O(n) or better)
- **MUST:** Implement lazy loading for large datasets
- **SHOULD:** Use caching for frequently accessed data
- **MUST:** Profile performance for populations >500 inhabitants
- **MUST NOT:** Use inefficient algorithms or data structures without justification

```java
// GOOD - Efficient population aging
public void agePopulation() {
    population.parallelStream()
        .forEach(person -> person.incrementAge());
}

// BAD - Inefficient nested loops
public void findPartners() {
    for (Person person1 : population) {
        for (Person person2 : population) {
            if (isCompatible(person1, person2)) {
                // This is O(n²) - too slow for large populations
            }
        }
    }
}
```

### Security Requirements (MUST IMPLEMENT)

#### Data Security
- **MUST:** Validate all external input (configuration files, user commands)
- **MUST:** Use parameterized queries for database operations
- **MUST:** Implement file integrity checks for save/load operations
- **MUST:** Sanitize all user-provided data before processing
- **MUST NOT:** Store sensitive data in plain text

#### Plugin Security
- **MUST:** Sandbox plugin execution to prevent system access
- **MUST:** Validate plugin signatures before loading
- **SHOULD:** Implement permission system for plugin capabilities
- **MUST NOT:** Allow plugins to access system files outside designated directories

```java
// GOOD - Parameterized query
public Person findPersonById(UUID id) {
    String sql = "SELECT * FROM persons WHERE id = ?";
    return jdbcTemplate.queryForObject(sql, personRowMapper, id);
}

// BAD - SQL injection vulnerability
public Person findPersonByName(String name) {
    String sql = "SELECT * FROM persons WHERE name = '" + name + "'";
    return jdbcTemplate.queryForObject(sql, personRowMapper);
}
```

---

## Documentation Standards

### Code Documentation (MUST FOLLOW)

#### JavaDoc Requirements
- **MUST:** Document all public classes and interfaces
- **MUST:** Document all public and protected methods
- **MUST:** Include @param, @return, and @throws tags where applicable
- **SHOULD:** Include usage examples for complex APIs
- **MUST:** Keep documentation up-to-date with code changes

```java
/**
 * Manages the life cycle simulation for a population of inhabitants.
 * 
 * <p>This class coordinates aging, mortality, partnership formation, and
 * child generation across the entire population. It operates on a yearly
 * time step by default but can be configured for different intervals.
 * 
 * <p>Example usage:
 * <pre>{@code
 * SimulationEngine engine = new SimulationEngine(populationManager);
 * engine.configureTimeStep(TimeUnit.YEARS, 1);
 * engine.runSimulation(50); // Run for 50 years
 * }</pre>
 * 
 * @author LittlePeople Development Team
 * @since 1.0
 */
public class SimulationEngine {
    
    /**
     * Runs the simulation for the specified number of time units.
     * 
     * @param duration the number of time units to simulate
     * @throws SimulationException if the simulation encounters an error
     * @throws IllegalArgumentException if duration is negative
     */
    public void runSimulation(int duration) throws SimulationException {
        // Implementation
    }
}
```

#### README and Markdown Documentation
- **MUST:** Keep README.md up-to-date with setup instructions
- **MUST:** Document configuration options and examples
- **MUST:** Include troubleshooting guide for common issues
- **SHOULD:** Provide tutorial for first-time users
- **MUST:** Document extension development guide

### Configuration Documentation (MUST FOLLOW)

#### Configuration File Comments
```yaml
# LittlePeople Simulation Configuration
# This file controls the core parameters of the population simulation

simulation:
  # Unique name for this simulation scenario
  name: "Medieval Village"
  
  # Time progression settings
  time_step: "year"  # Options: day, month, year
  
population:
  # Initial population size (10-1000)
  initial_size: 250
  
  # Annual growth rates
  immigration_rate: 12      # New inhabitants per year
  emigration_rate: 0.03     # Percentage leaving annually (0.0-1.0)
```

---

## Implementation Priorities

### MVP Phase (Weeks 1-8) - MUST HAVE
**Critical Path Features:** F026, F013, F003, F014, F009-F012, F016-F018, F035

#### Phase 1 Priority Order
1. **F026:** Modular Architecture (Foundation)
2. **F013:** Simulation Clock (Time Management)
3. **F003:** Inhabitant Generation (Core Entities)
4. **F014:** Event System (Event Processing)
5. **F009-F012:** Life Cycle Features (Aging, Death, Partnerships, Children)

#### Phase 2 Priority Order
1. **F001-F002:** Population Configuration
2. **F016:** Simulation Control Interface
3. **F017:** Save/Load System
4. **F035:** Console Interface
5. **F027:** Database Abstraction Layer

### Quality Gates (MUST PASS)
- **Gate 1:** All unit tests pass with >85% coverage
- **Gate 2:** Performance benchmarks meet requirements
- **Gate 3:** Security scan passes without critical issues
- **Gate 4:** Documentation complete and up-to-date
- **Gate 5:** Cross-platform testing successful

### Feature Implementation Rules
- **MUST:** Implement features in dependency order
- **MUST:** Complete all acceptance criteria before marking feature done
- **MUST NOT:** Skip testing or documentation for "quick delivery"
- **SHOULD:** Implement features in small, testable increments
- **MUST:** Validate performance impact with each major feature

---

## Development Workflow

### Git Workflow (MUST FOLLOW)

#### Branch Naming
- **Feature branches:** `feature/F001-population-configuration`
- **Bug fixes:** `bugfix/issue-123-save-corruption`
- **Hotfixes:** `hotfix/critical-memory-leak`
- **Release branches:** `release/v1.0.0`

#### Commit Message Format
```
<type>(scope): <description>

[optional body]

[optional footer]
```

**Types:** feat, fix, docs, style, refactor, test, chore  
**Examples:**
- `feat(population): implement immigration/emigration controls (F002)`
- `fix(persistence): handle database connection timeout gracefully`
- `docs(api): add extension development examples`

#### Pull Request Requirements
- **MUST:** Include feature number in title (e.g., "F003: Implement inhabitant generation")
- **MUST:** Link to related issues or features
- **MUST:** Include test coverage information
- **MUST:** Update documentation if API changes
- **MUST:** Pass all CI checks before review

### CI/CD Pipeline (MUST IMPLEMENT)
1. **Build:** Maven compile and package
2. **Test:** Unit and integration test execution
3. **Quality:** Static analysis and coverage reports
4. **Security:** Dependency vulnerability scanning
5. **Performance:** Benchmark execution for large populations
6. **Documentation:** Generate and validate JavaDoc

---

## AI Assistant Guidelines

### Code Generation Rules (MUST FOLLOW)

#### When Writing Code
- **MUST:** Follow all naming conventions and code standards defined above
- **MUST:** Include comprehensive error handling and logging
- **MUST:** Write unit tests for all new functionality
- **MUST:** Add JavaDoc documentation for public APIs
- **MUST:** Consider performance implications for large populations
- **MUST NOT:** Leave TODO comments or placeholder implementations
- **MUST NOT:** Hardcode configuration values

#### Implementation Standards
- **ALWAYS:** Use the exact package structure defined in this document
- **ALWAYS:** Implement interfaces defined in the features document
- **ALWAYS:** Follow the acceptance criteria precisely
- **ALWAYS:** Consider thread safety for concurrent operations
- **ALWAYS:** Validate input parameters and throw appropriate exceptions

#### Code Quality Expectations
```java
// EXAMPLE: Proper implementation style
@Component
public class PopulationManager implements IPopulationManager {
    
    private static final Logger logger = LoggerFactory.getLogger(PopulationManager.class);
    private static final int MAX_POPULATION_SIZE = 10000;
    
    private final List<Person> population;
    private final Random random;
    
    public PopulationManager(RandomProvider randomProvider) {
        this.population = new CopyOnWriteArrayList<>();
        this.random = randomProvider.getRandom();
        logger.info("PopulationManager initialized");
    }
    
    @Override
    public void addImmigrants(int count) throws PopulationException {
        validateImmigrantCount(count);
        
        try {
            for (int i = 0; i < count; i++) {
                Person immigrant = generateRandomPerson();
                population.add(immigrant);
                logger.debug("Added immigrant: {}", immigrant.getId());
            }
            logger.info("Added {} immigrants to population", count);
        } catch (Exception e) {
            throw new PopulationException("Failed to add immigrants", e);
        }
    }
    
    private void validateImmigrantCount(int count) throws PopulationException {
        if (count < 0) {
            throw new PopulationException("Immigrant count cannot be negative: " + count);
        }
        if (population.size() + count > MAX_POPULATION_SIZE) {
            throw new PopulationException(
                String.format("Population would exceed maximum size: %d + %d > %d", 
                    population.size(), count, MAX_POPULATION_SIZE));
        }
    }
}
```

### Problem-Solving Approach (MUST FOLLOW)

#### When Implementing Features
1. **Read the feature specification completely** before starting implementation
2. **Identify all dependencies** and ensure they are implemented first
3. **Break down complex features** into smaller, testable components
4. **Implement interfaces first**, then concrete classes
5. **Write tests alongside implementation**, not after
6. **Validate against acceptance criteria** before considering complete

#### When Facing Uncertainty
- **ASK for clarification** rather than making assumptions
- **Reference the PRD and features document** for context
- **Consider the target user** (story writers) when making design decisions
- **Prioritize extensibility** for features marked as extension points
- **Default to simple, maintainable solutions** over complex optimizations

#### Performance Considerations
- **ALWAYS:** Consider scalability to 1000+ inhabitants
- **ALWAYS:** Profile memory usage for long-running simulations
- **ALWAYS:** Use efficient data structures and algorithms
- **ALWAYS:** Consider database query performance
- **NEVER:** Optimize prematurely without measuring performance

### Communication Guidelines (MUST FOLLOW)

#### When Providing Updates
- **State which feature** you are implementing (use feature ID)
- **Describe the approach** you are taking
- **Highlight any deviations** from the specification with justification
- **Mention testing strategy** and coverage achieved
- **Note any performance considerations** or optimizations made

#### When Asking Questions
- **Reference specific feature numbers** and acceptance criteria
- **Provide context** about what you've already implemented
- **Suggest potential solutions** rather than just identifying problems
- **Ask for specific guidance** rather than general direction

### File Management Rules (MUST FOLLOW)

#### When Creating Files
- **ALWAYS:** Use the correct package structure
- **ALWAYS:** Follow naming conventions exactly
- **ALWAYS:** Include appropriate file headers with license and author information
- **NEVER:** Create files outside the defined module structure
- **NEVER:** Leave empty or template files in the codebase

#### When Modifying Existing Files
- **ALWAYS:** Maintain backward compatibility for public APIs
- **ALWAYS:** Update related tests when changing implementation
- **ALWAYS:** Update documentation when changing public interfaces
- **ALWAYS:** Consider impact on other modules before making changes

---

## Compliance and Validation

### Pre-Commit Checklist (MUST COMPLETE)
- [ ] All unit tests pass locally
- [ ] Code coverage meets minimum 85% threshold
- [ ] Static analysis tools pass without warnings
- [ ] JavaDoc documentation updated for any API changes
- [ ] Performance impact assessed for population-scale features
- [ ] Security implications reviewed for new functionality
- [ ] Integration tests updated if public interfaces changed

### Definition of Done (MUST MEET)
A feature is considered complete only when:
- [ ] All acceptance criteria implemented and verified
- [ ] Unit tests written with >85% coverage
- [ ] Integration tests cover critical workflows
- [ ] JavaDoc documentation complete for public APIs
- [ ] Performance benchmarks meet requirements
- [ ] Security review completed (if applicable)
- [ ] Code review approved by peer
- [ ] Feature demonstrated working end-to-end

---

**Document Maintenance:**
This RULES.md document should be updated whenever:
- New technologies or libraries are adopted
- Architecture decisions change the module structure
- Performance requirements are modified
- Security policies are updated
- Development workflow processes change

**Last Updated:** August 26, 2025  
**Next Review:** September 26, 2025
