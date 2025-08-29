package com.littlepeople.core.interfaces;

import java.time.Duration;
import java.time.LocalDateTime;

/**
 * Interface for managing simulation time and clock operations.
 * The simulation clock controls the flow of time within the simulation
 * and provides time-related utilities.
 */
public interface SimulationClock {

    /**
     * Gets the current simulation time.
     *
     * @return the current simulation time
     */
    LocalDateTime getCurrentTime();

    /**
     * Sets the current simulation time.
     *
     * @param time the new simulation time
     */
    void setCurrentTime(LocalDateTime time);

    /**
     * Advances the simulation time by the specified duration.
     *
     * @param duration the duration to advance
     * @return the new current time
     */
    LocalDateTime advance(Duration duration);

    /**
     * Advances the simulation time by the specified number of days.
     *
     * @param days the number of days to advance
     * @return the new current time
     */
    LocalDateTime advanceDays(long days);

    /**
     * Gets the time scale factor for the simulation.
     * A scale of 1.0 means real-time, 2.0 means twice as fast, etc.
     *
     * @return the time scale factor
     */
    double getTimeScale();

    /**
     * Sets the time scale factor for the simulation.
     *
     * @param scale the new time scale factor (must be positive)
     */
    void setTimeScale(double scale);

    /**
     * Gets the start time of the simulation.
     *
     * @return the simulation start time
     */
    LocalDateTime getStartTime();

    /**
     * Gets the duration since the simulation started.
     *
     * @return the elapsed simulation time
     */
    Duration getElapsedTime();

    /**
     * Checks if the simulation clock is running.
     *
     * @return true if the clock is running
     */
    boolean isRunning();

    /**
     * Starts the simulation clock.
     */
    void start();

    /**
     * Stops the simulation clock.
     */
    void stop();

    /**
     * Pauses the simulation clock.
     */
    void pause();

    /**
     * Resumes the simulation clock.
     */
    void resume();

    /**
     * Resets the simulation clock to the start time.
     */
    void reset();
}
