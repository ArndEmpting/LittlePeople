package com.littlepeople.core.util;

import com.littlepeople.core.interfaces.SimulationClock;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Provides access to the current simulation clock throughout the application.
 * This utility class ensures that all time-dependent operations use simulation time
 * instead of real system time.
 */
public class SimulationTimeProvider {

    private static volatile SimulationClock currentClock;

    /**
     * Sets the simulation clock to be used throughout the application.
     *
     * @param clock the simulation clock instance
     * @throws IllegalArgumentException if clock is null
     */
    public static void setSimulationClock(SimulationClock clock) {
        if (clock == null) {
            throw new IllegalArgumentException("Simulation clock cannot be null");
        }
        currentClock = clock;
    }

    /**
     * Gets the current simulation time as LocalDateTime.
     *
     * @return the current simulation time
     * @throws IllegalStateException if no simulation clock has been set
     */
    public static LocalDateTime getCurrentDateTime() {
        SimulationClock clock = currentClock;
        if (clock == null) {
            throw new IllegalStateException("No simulation clock has been set. Call setSimulationClock() first.");
        }
        return clock.getCurrentTime();
    }

    /**
     * Gets the current simulation date as LocalDate.
     *
     * @return the current simulation date
     * @throws IllegalStateException if no simulation clock has been set
     */
    public static LocalDate getCurrentDate() {
        return getCurrentDateTime().toLocalDate();
    }

    /**
     * Checks if a simulation clock has been set.
     *
     * @return true if a simulation clock is available
     */
    public static boolean isClockSet() {
        return currentClock != null;
    }

    /**
     * Gets the current simulation clock instance.
     *
     * @return the current simulation clock
     * @throws IllegalStateException if no simulation clock has been set
     */
    public static SimulationClock getSimulationClock() {
        SimulationClock clock = currentClock;
        if (clock == null) {
            throw new IllegalStateException("No simulation clock has been set. Call setSimulationClock() first.");
        }
        return clock;
    }

    /**
     * Clears the current simulation clock (mainly for testing).
     */
    public static void clearSimulationClock() {
        currentClock = null;
    }
}
