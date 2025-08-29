# Implementation Prompt for RFC-004: Life Cycle - Aging and Mortality System

## Overview

This implementation prompt provides guidance for implementing RFC-004: Life Cycle - Aging and Mortality System. The RFC describes the aging and mortality systems that form the foundation of the life cycle simulation, including how inhabitants age over time and how their mortality is calculated using realistic probability models.

## Implementation Requirements

Please implement the features described in RFC-004 according to the following guidelines:

1. **Follow the Design Specifications**: Adhere strictly to the design, architecture, and interface specifications provided in the RFC document.
   - Implement the aging system with time-based progression and life stage transitions
   - Create the mortality system with probabilistic death calculations
   - Establish health and wellness tracking mechanisms

2. **Code Quality Standards**:
   - Follow all coding standards and conventions defined in RULES.md
   - Include comprehensive error handling and logging using SLF4J
   - Write clear, maintainable code with appropriate comments and JavaDoc
   - Avoid code duplication and excessive complexity
   - Implement required unit tests with minimum 85% coverage

3. **Performance Considerations**:
   - Optimize aging calculations for large populations
   - Use efficient probability calculations for mortality
   - Consider memory usage for health tracking over time
   - Implement efficient life stage transition processing

4. **Documentation Requirements**:
   - Include JavaDoc for all public classes and methods
   - Document aging algorithms and mortality probability models
   - Create package-info.java files explaining lifecycle purposes
   - Document health system mechanics and calculations

5. **Testing Requirements**:
   - Implement unit tests for aging and mortality calculations
   - Test life stage transitions and boundary conditions
   - Include statistical testing for mortality probability distributions
   - Test health system tracking and calculations
   - Validate integration with event system for time-based processing

6. **Integration Requirements**:
   - Build upon the event system from RFC-003 for time-based processing
   - Integrate with Person entities from RFC-002 for life cycle tracking
   - Use the simulation clock from RFC-003 for aging progression
   - Follow established architectural patterns from RFC-001

## Deliverables

Your implementation should include:

1. Aging system implementation with:
   - Automatic age progression based on simulation time
   - Life stage transitions (infant, child, teen, adult, elder)
   - Age-related attribute changes and development
   - Configurable aging rates and life expectancy

2. Mortality system implementation with:
   - Probabilistic death calculations based on age and health
   - Gender-specific mortality tables and life expectancy
   - Health condition impacts on mortality risk
   - Natural death event generation and processing

3. Health and wellness tracking:
   - Basic health status monitoring
   - Age-related health decline modeling
   - Health event generation (illness, recovery)
   - Health impact on other life aspects

4. Life cycle event processors:
   - AgingEventProcessor for time-based aging
   - MortalityEventProcessor for death calculations
   - HealthEventProcessor for health-related changes
   - LifeStageTransitionEventProcessor for stage changes

5. Unit tests for all implemented components with 85%+ coverage:
   - Aging calculation and progression testing
   - Mortality probability and death event testing
   - Health system tracking and calculation testing
   - Life stage transition testing
   - Event processor integration testing

## Technical Focus Areas

### Aging Algorithms
- Implement precise age tracking with configurable time units
- Design life stage transition logic with appropriate thresholds
- Create age-related attribute modification systems
- Handle edge cases in aging calculations and transitions

### Mortality Modeling
- Implement statistically accurate mortality probability calculations
- Design gender and age-specific mortality tables
- Create health condition impact modeling on death risk
- Handle rare events and statistical edge cases appropriately

### Health System Design
- Create comprehensive health status tracking mechanisms
- Design health decline models that reflect realistic aging
- Implement health event generation with appropriate frequencies
- Create health recovery and improvement mechanisms

### Event Integration
- Design efficient event scheduling for lifecycle events
- Implement proper event ordering for simultaneous lifecycle changes
- Create event cleanup and lifecycle management
- Handle event processing errors and recovery scenarios

## Success Criteria

Your implementation will be considered successful when:

1. Aging system accurately progresses character ages over simulation time
2. Life stage transitions occur correctly at appropriate ages
3. Mortality system generates realistic death events based on probability
4. Health system tracks and modifies health status appropriately
5. All lifecycle events integrate properly with the event system
6. Unit tests achieve minimum 85% code coverage
7. All acceptance criteria from RFC-004 are met
8. Performance requirements for population-scale processing are met

## Quality Gates

Before considering the implementation complete, ensure:

- All RFC-004 acceptance criteria are validated
- Code quality standards from RULES.md are followed
- Aging and mortality calculations are statistically sound
- Health system provides realistic health progression
- Event integration works correctly with RFC-003 event system
- Documentation is complete for all lifecycle algorithms
- Test coverage includes statistical validation of probability models

## Integration Notes

This RFC builds upon RFC-001 (foundation), RFC-002 (entities), and RFC-003 (events) to create the core life simulation mechanics. Pay special attention to:

- Event processing - use the event system from RFC-003 for all time-based lifecycle changes
- Person entity modification - aging and health changes must update RFC-002 Person entities
- Statistical accuracy - mortality and health models should reflect realistic demographic data
- Package organization - lifecycle code goes in com.littlepeople.simulation.lifecycle package
- Performance optimization - lifecycle processing will run for every person every time step

The aging, mortality, and health systems created here will be essential for RFC-005 (Partnerships) which depends on life stages, and all subsequent simulation features that involve character development over time.
