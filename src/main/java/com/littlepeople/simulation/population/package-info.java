/**
 * Population management system for the LittlePeople simulation engine.
 *
 * <p>This package provides comprehensive population management capabilities including:
 * <ul>
 *   <li>Initial population generation with realistic demographic distributions</li>
 *   <li>Immigration and emigration processing with configurable rates</li>
 *   <li>Population validation and demographic consistency checking</li>
 *   <li>Population statistics and reporting</li>
 *   <li>Integration with family formation and lifecycle systems</li>
 * </ul>
 *
 * <h2>Core Components</h2>
 * <ul>
 *   <li>{@link com.littlepeople.simulation.population.PopulationManager} - Central coordination of all population operations</li>
 *   <li>{@link com.littlepeople.simulation.population.PopulationGenerator} - Creates initial and immigrant populations</li>
 *   <li>{@link com.littlepeople.simulation.population.PopulationGrowthController} - Manages immigration/emigration</li>
 *   <li>{@link com.littlepeople.simulation.population.DemographicValidator} - Validates population consistency</li>
 * </ul>
 *
 * <h2>Event Processing</h2>
 * The population system integrates with the simulation event system to process:
 * <ul>
 *   <li>Population initialization events</li>
 *   <li>Immigration and emigration events</li>
 *   <li>Population growth monitoring events</li>
 *   <li>Demographic validation events</li>
 * </ul>
 *
 * @version 1.0
 * @since 1.0
 */
package com.littlepeople.simulation.population;

