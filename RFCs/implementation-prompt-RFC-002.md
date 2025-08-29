# Implementation Prompt for RFC-002: Core Entity Models

## Overview

This implementation prompt provides guidance for implementing RFC-002: Core Entity Models. The RFC describes the core entity models that form the foundation of the LittlePeople simulation engine, including the Person class and supporting enumerations.

## Implementation Requirements

Please implement the features described in RFC-002 according to the following guidelines:

1. **Follow the Design Specifications**: Adhere strictly to the design, architecture, and interface specifications provided in the RFC document.
   - Implement the Person class with all specified attributes and methods
   - Create supporting enumerations (Gender, LifeStage, PersonalityTraits, etc.)
   - Establish proper entity relationships and data structures

2. **Code Quality Standards**:
   - Follow all coding standards and conventions defined in RULES.md
   - Include comprehensive error handling and logging using SLF4J
   - Write clear, maintainable code with appropriate comments and JavaDoc
   - Avoid code duplication and excessive complexity
   - Implement required unit tests with minimum 85% coverage

3. **Performance Considerations**:
   - Optimize entity operations for large population datasets
   - Use efficient memory management for person attributes
   - Consider serialization performance for persistence operations
   - Implement proper equals() and hashCode() methods

4. **Documentation Requirements**:
   - Include JavaDoc for all public classes and methods
   - Document entity relationships and business rules
   - Create package-info.java files explaining model purposes
   - Document any design decisions for entity modeling

5. **Testing Requirements**:
   - Implement unit tests for all entity classes and enumerations
   - Test entity validation and business rule enforcement
   - Include boundary value testing for attributes
   - Test serialization and deserialization scenarios
   - Validate entity relationship management

6. **Integration Requirements**:
   - Build upon the architectural foundation from RFC-001
   - Implement the Entity interface established in RFC-001
   - Use the exception hierarchy and logging framework from RFC-001
   - Follow the package structure established in RFC-001

## Deliverables

Your implementation should include:

1. Complete Person entity implementation with all specified attributes:
   - Basic demographics (age, gender, name)
   - Family relationships (parents, children, spouse)
   - Personality and traits system
   - Life stage tracking and transitions

2. Supporting enumeration implementations:
   - Gender enumeration
   - LifeStage enumeration  
   - PersonalityTraits enumeration
   - Any additional enumerations specified in the RFC

3. Entity validation and business rules:
   - Age validation and life stage consistency
   - Relationship validation (marriage rules, family structures)
   - Attribute constraints and data integrity checks

4. Utility classes for entity management:
   - PersonBuilder for entity creation
   - PersonValidator for business rule validation
   - Relationship management utilities

5. Unit tests for all implemented components with 85%+ coverage:
   - Entity creation and modification tests
   - Validation rule testing
   - Relationship management testing
   - Serialization/deserialization testing

## Technical Focus Areas

### Entity Design
- Implement immutable or carefully managed mutable entities
- Design efficient attribute storage and access patterns
- Establish clear entity lifecycle management
- Create proper entity comparison and identification methods

### Relationship Management
- Implement bidirectional relationship management
- Handle relationship consistency and integrity
- Design efficient relationship queries and navigation
- Manage relationship lifecycle (creation, modification, removal)

### Validation Framework
- Create comprehensive validation rules for all entity attributes
- Implement business rule validation (age constraints, relationship rules)
- Design extensible validation framework for future enhancements
- Provide clear validation error messages and recovery guidance

### Performance Optimization
- Optimize entity creation and modification operations
- Implement efficient attribute access patterns
- Consider memory usage for large population datasets
- Design for efficient serialization and persistence

## Success Criteria

Your implementation will be considered successful when:

1. All Person entity attributes and methods are properly implemented
2. Supporting enumerations are complete and well-designed
3. Entity validation rules are comprehensive and working correctly
4. Relationship management functions properly with integrity checks
5. Unit tests achieve minimum 85% code coverage
6. All acceptance criteria from RFC-002 are met
7. Integration with RFC-001 foundation is seamless
8. Performance requirements for entity operations are met

## Quality Gates

Before considering the implementation complete, ensure:

- All RFC-002 acceptance criteria are validated
- Code quality standards from RULES.md are followed
- Entity design supports scalability requirements
- Validation framework is comprehensive and extensible
- Relationship management maintains data integrity
- Documentation is complete for all entity models
- Test coverage targets are achieved with meaningful tests

## Integration Notes

This RFC builds directly upon RFC-001 and establishes the core entities that all subsequent RFCs will use. Pay special attention to:

- Entity interface implementation - must conform to RFC-001 specifications
- Exception handling - use the hierarchy established in RFC-001
- Logging integration - follow the patterns established in RFC-001
- Package organization - entities go in the com.littlepeople.core.model package
- Testing framework - build upon the testing foundation from RFC-001

The Person entity and supporting models created here will be used extensively by RFC-003 (Event System), RFC-004 (Life Cycle), RFC-005 (Partnerships), and all subsequent RFCs.
