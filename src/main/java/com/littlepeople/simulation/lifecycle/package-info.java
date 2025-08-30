/**
 * Life Cycle - Aging and Mortality System package.
 *
 * <p>This package implements RFC-004: Life Cycle - Aging and Mortality System for the LittlePeople
 * simulation engine. It provides comprehensive aging and mortality systems that form the foundation
 * of the life cycle simulation.</p>
 *
 * <h2>Key Components</h2>
 *
 * <h3>Aging System</h3>
 * <ul>
 *   <li>{@link com.littlepeople.simulation.lifecycle.AgingProcessor} - Processes time change events to age population</li>
 *   <li>{@link com.littlepeople.simulation.lifecycle.LifeStage} - Enumeration of life stages (infant, child, adult, etc.)</li>
 *   <li>{@link com.littlepeople.simulation.lifecycle.AgingEvent} - Event generated when a person ages</li>
 * </ul>
 *
 * <h3>Mortality System</h3>
 * <ul>
 *   <li>{@link com.littlepeople.simulation.lifecycle.MortalityProcessor} - Calculates death probability and processes deaths</li>
 *   <li>{@link com.littlepeople.simulation.lifecycle.MortalityModel} - Interface for mortality probability calculation models</li>
 *   <li>{@link com.littlepeople.simulation.lifecycle.RealisticMortalityModel} - Gompertz-Makeham mortality model implementation</li>
 *   <li>{@link com.littlepeople.core.model.events.DeathEvent} - Event generated when a person dies</li>
 *   <li>{@link com.littlepeople.simulation.lifecycle.DeathCause} - Enumeration of possible causes of death</li>
 * </ul>
 *
 * <h3>Health System</h3>
 * <ul>
 *   <li>{@link com.littlepeople.simulation.lifecycle.HealthStatus} - Enumeration of health statuses</li>
 * </ul>
 *
 * <h2>Features Implemented</h2>
 *
 * <ul>
 *   <li><strong>F009:</strong> Aging System - Automatic age progression and life stage transitions</li>
 *   <li><strong>F010:</strong> Mortality System - Probabilistic death calculations with realistic models</li>
 *   <li><strong>F004:</strong> Population Statistics Tracking (mortality-related metrics)</li>
 *   <li><strong>F006:</strong> Population Validation (age-related validation)</li>
 * </ul>
 *
 * <h2>Architecture</h2>
 *
 * <p>The lifecycle system is built on the event-driven architecture established in RFC-003.
 * All aging and mortality changes are processed through events, ensuring proper integration
 * with the simulation's event system and enabling other components to react to lifecycle changes.</p>
 *
 * <h3>Aging Process</h3>
 * <ol>
 *   <li>Time change events trigger the AgingProcessor</li>
 *   <li>Population ages are updated based on time progression</li>
 *   <li>Life stage transitions are detected and processed</li>
 *   <li>AgingEvent and LifeStageChangeEvent are published</li>
 * </ol>
 *
 * <h3>Mortality Process</h3>
 * <ol>
 *   <li>MortalityProcessor calculates death probability for each person</li>
 *   <li>Probability is adjusted based on age, health, and other factors</li>
 *   <li>Random determination decides if death occurs</li>
 *   <li>Death cause is determined and DeathEvent is published</li>
 * </ol>
 *
 * <h2>Statistical Accuracy</h2>
 *
 * <p>The mortality models are based on real demographic data and use established
 * mathematical models like the Gompertz-Makeham law to ensure realistic population
 * dynamics over time.</p>
 *
 * @author LittlePeople Development Team
 * @version 1.0
 * @since 1.0
 */
package com.littlepeople.simulation.lifecycle;

