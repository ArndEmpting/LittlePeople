/**
 * Core entity models for the LittlePeople simulation engine.
 *
 * <p>This package contains the fundamental domain objects that represent
 * the core entities in the LittlePeople simulation. These models form
 * the foundation for all simulation mechanics and data persistence.</p>
 *
 * <h2>Core Entities</h2>
 * <p>The primary entity is {@link com.littlepeople.core.model.Person},
 * which represents an individual inhabitant in the simulation with
 * complete demographic data, personality traits, family relationships,
 * and life status tracking.</p>
 *
 * <h2>Supporting Enumerations</h2>
 * <p>Several enumerations define discrete states and characteristics:</p>
 * <ul>
 *   <li>{@link com.littlepeople.core.model.Gender} - Biological gender categories</li>
 *   <li>{@link com.littlepeople.core.model.HealthStatus} - Health conditions with mortality multipliers</li>
 *   <li>{@link com.littlepeople.core.model.WealthStatus} - Economic status levels with income ranges</li>
 *   <li>{@link com.littlepeople.core.model.LifeStage} - Age-based life phases</li>
 *   <li>{@link com.littlepeople.core.model.PersonalityTrait} - Personality characteristics</li>
 *   <li>{@link com.littlepeople.core.model.DeathCause} - Reasons for mortality</li>
 * </ul>
 *
 * <h2>Utility Classes</h2>
 * <p>Supporting classes provide entity management capabilities:</p>
 * <ul>
 *   <li>{@link com.littlepeople.core.model.PersonBuilder} - Fluent API for Person creation</li>
 *   <li>{@link com.littlepeople.core.model.PersonValidator} - Business rule validation</li>
 * </ul>
 *
 * <h2>Design Principles</h2>
 * <p>The entity models follow these key design principles:</p>
 * <ul>
 *   <li><strong>Immutable Core Identity:</strong> Core identity properties (ID, gender, birth date) are immutable</li>
 *   <li><strong>Bidirectional Relationships:</strong> Family relationships are maintained consistently on both sides</li>
 *   <li><strong>Comprehensive Validation:</strong> All entity operations include validation for data integrity</li>
 *   <li><strong>Thread Safety:</strong> Collections use synchronized implementations for concurrent access</li>
 *   <li><strong>Performance Optimization:</strong> Efficient data structures and defensive copying</li>
 *   <li><strong>Logging Integration:</strong> All significant operations are logged for debugging and monitoring</li>
 * </ul>
 *
 * <h2>Entity Relationships</h2>
 * <p>The Person entity supports complex relationship management:</p>
 * <ul>
 *   <li><strong>Partnerships:</strong> Bidirectional spouse/partner relationships with validation rules</li>
 *   <li><strong>Parent-Child:</strong> Family relationships with age constraints and integrity checks</li>
 *   <li><strong>Sibling Detection:</strong> Automatic detection of sibling relationships through shared parents</li>
 * </ul>
 *
 * <h2>Personality System</h2>
 * <p>The comprehensive personality trait system includes 17 traits based on
 * psychological research, each with intensity levels from 1-10. These traits
 * influence behavior patterns and simulation outcomes.</p>
 *
 * <h2>Life Cycle Management</h2>
 * <p>Entities support complete life cycle management including:</p>
 * <ul>
 *   <li>Birth and initialization with random or specified attributes</li>
 *   <li>Age calculation and life stage transitions</li>
 *   <li>Health and wealth status changes over time</li>
 *   <li>Death handling with cause tracking and relationship cleanup</li>
 * </ul>
 *
 * <h2>Integration</h2>
 * <p>This package implements RFC-002: Core Entity Models and builds upon
 * the architectural foundation established in RFC-001. The models integrate
 * with the exception hierarchy, logging framework, and Entity interface
 * defined in the core architecture.</p>
 *
 * <p>These entities are designed to be used extensively by subsequent RFCs
 * including the Event System (RFC-003), Life Cycle mechanics (RFC-004),
 * Partnership Formation (RFC-005), and all other simulation subsystems.</p>
 *
 * @since 1.0.0
 * @version 1.0.0
 * @see com.littlepeople.core.model.Person
 * @see com.littlepeople.core.interfaces.Entity
 */
package com.littlepeople.core.model;

