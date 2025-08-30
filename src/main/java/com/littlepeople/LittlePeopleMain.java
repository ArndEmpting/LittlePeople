package com.littlepeople;

import com.littlepeople.core.model.*;
import com.littlepeople.core.exceptions.SimulationException;
import com.littlepeople.simulation.population.PopulationConfiguration;
import com.littlepeople.simulation.population.PopulationManager;
import com.littlepeople.simulation.population.PopulationManagerFactory;
import com.littlepeople.simulation.population.PopulationManagerImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Random;

/**
 * Main entry point for the LittlePeople simulation.
 *
 * This class creates an initial population and starts a simulation cycle
 * without requiring user interaction. It demonstrates the basic usage
 * of the simulation engine and population management.
 */
public class LittlePeopleMain {

    private static final Logger logger = LoggerFactory.getLogger(LittlePeopleMain.class);
    private static final int INITIAL_POPULATION_SIZE = 100;
    private static final int SIMULATION_DURATION_DAYS = 365; // Run for 1 year
    private static final Random random = new Random();

    public static void main(String[] args) {
        logger.info("Starting LittlePeople simulation...");

        try {
            // Create initial population
            createInitialPopulation();
            // Create simulation engine starting from current time
            SimulationEngine engine = new SimulationEngine(LocalDateTime.now());



            // Start the simulation
            engine.start();

            // Let simulation run for specified duration
            logger.info("Simulation running for {} days...", SIMULATION_DURATION_DAYS);
            Thread.sleep(SIMULATION_DURATION_DAYS * 50); // Scaled time for demo

            // Stop the simulation
            engine.stop();

            // Print final statistics
            printSimulationStatistics(engine);

        } catch (SimulationException e) {
            logger.error("Simulation error occurred", e);
        } catch (InterruptedException e) {
            logger.warn("Simulation interrupted", e);
            Thread.currentThread().interrupt();
        } catch (Exception e) {
            logger.error("Unexpected error during simulation", e);
        }

        logger.info("LittlePeople simulation completed.");
    }

    /**
     * Creates an initial population of people with random characteristics.
     *

     */
    private static void createInitialPopulation() throws SimulationException {
        logger.info("Creating initial population of {} people...", INITIAL_POPULATION_SIZE);

        PopulationManager populationManager = PopulationManagerImpl.getInstance();

        populationManager.initializePopulation(PopulationManagerFactory.createDefaultConfiguration(100));

        logger.info("Initial population created successfully with {} people",
                PersonRegistry.getPersonRegistry().size());
    }

    /**
     * Creates a random person with varied characteristics.
     *
     * @return a new Person with random attributes
     */
    private static Person createRandomPerson() {
        PersonBuilder builder = new PersonBuilder();

        // Random gender
        Gender gender = random.nextBoolean() ? Gender.MALE : Gender.FEMALE;

        // Random age between 18 and 65 years
        int ageYears = 18 + random.nextInt(48);
        LocalDate birthDate = LocalDate.now().minusYears(ageYears).minusDays(random.nextInt(365));

        // Generate random names (simplified for this example)
        String firstName = generateRandomName(gender);
        String lastName = generateRandomLastName();

        // Random health and wealth status
        HealthStatus healthStatus = getRandomHealthStatus();
        WealthStatus wealthStatus = getRandomWealthStatus();

        return builder
                .firstName(firstName)
                .lastName(lastName)
                .gender(gender)
                .birthDate(birthDate)
                .healthStatus(healthStatus)
                .wealthStatus(wealthStatus)
                .build();
    }

    /**
     * Generates a random first name based on gender.
     */
    private static String generateRandomName(Gender gender) {
        String[] maleNames = {"John", "Michael", "David", "Robert", "James", "William", "Richard", "Thomas", "Christopher", "Daniel"};
        String[] femaleNames = {"Mary", "Patricia", "Jennifer", "Linda", "Elizabeth", "Barbara", "Susan", "Jessica", "Sarah", "Karen"};

        if (gender == Gender.MALE) {
            return maleNames[random.nextInt(maleNames.length)];
        } else {
            return femaleNames[random.nextInt(femaleNames.length)];
        }
    }

    /**
     * Generates a random last name.
     */
    private static String generateRandomLastName() {
        String[] lastNames = {"Smith", "Johnson", "Williams", "Brown", "Jones", "Garcia", "Miller", "Davis", "Rodriguez", "Martinez"};
        return lastNames[random.nextInt(lastNames.length)];
    }

    /**
     * Returns a random health status.
     */
    private static HealthStatus getRandomHealthStatus() {
        HealthStatus[] statuses = HealthStatus.values();
        return statuses[random.nextInt(statuses.length)];
    }

    /**
     * Returns a random wealth status.
     */
    private static WealthStatus getRandomWealthStatus() {
        WealthStatus[] statuses = WealthStatus.values();
        return statuses[random.nextInt(statuses.length)];
    }

    /**
     * Prints simulation statistics at the end of the run.
     *
     * @param engine the simulation engine
     */
    private static void printSimulationStatistics(SimulationEngine engine) {
        logger.info("=== SIMULATION STATISTICS ===");
        logger.info("Final simulation time: {}", engine.getCurrentTime());
        logger.info("Total population: {}", PersonRegistry.getPersonRegistry().size());
        logger.info("Simulation state: {}", engine.getState());

        // Count population by gender
        long maleCount = PersonRegistry.getPersonRegistry().values().stream()
                .filter(p -> p.getGender() == Gender.MALE)
                .count();
        long femaleCount = PersonRegistry.getPersonRegistry().values().stream()
                .filter(p -> p.getGender() == Gender.FEMALE)
                .count();

        logger.info("Male population: {}", maleCount);
        logger.info("Female population: {}", femaleCount);

        // Count living vs deceased
        long livingCount = PersonRegistry.getPersonRegistry().values().stream()
                .filter(p -> p.isAlive())
                .count();
        long deceasedCount = PersonRegistry.getPersonRegistry().values().stream()
                .filter(Person::isDeceased)
                .count();

        logger.info("Living population: {}", livingCount);
        logger.info("Deceased population: {}", deceasedCount);
        logger.info("==============================");
    }
}
