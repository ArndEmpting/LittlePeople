package com.littlepeople.core.interfaces;

import com.littlepeople.core.exceptions.SimulationException;
import java.time.LocalDateTime;

/**
 * Interface for managing the lifecycle of a simulation.
 * This interface defines the basic operations for starting, stopping,
 * pausing, and controlling the simulation execution.
 */
public interface SimulationLifecycle {

    /**
     * Starts the simulation.
     *
     * @throws SimulationException if the simulation cannot be started
     */
    void start() throws SimulationException;

    /**
     * Stops the simulation.
     *
     * @throws SimulationException if the simulation cannot be stopped
     */
    void stop() throws SimulationException;

    /**
     * Pauses the simulation.
     *
     * @throws SimulationException if the simulation cannot be paused
     */
    void pause() throws SimulationException;

    /**
     * Resumes a paused simulation.
     *
     * @throws SimulationException if the simulation cannot be resumed
     */
    void resume() throws SimulationException;

    /**
     * Resets the simulation to its initial state.
     *
     * @throws SimulationException if the simulation cannot be reset
     */
    void reset() throws SimulationException;

    /**
     * Checks if the simulation is currently running.
     *
     * @return true if the simulation is running
     */
    boolean isRunning();

    /**
     * Checks if the simulation is currently paused.
     *
     * @return true if the simulation is paused
     */
    boolean isPaused();

    /**
     * Checks if the simulation has been stopped.
     *
     * @return true if the simulation is stopped
     */
    boolean isStopped();

    /**
     * Gets the current simulation time.
     *
     * @return the current simulation time
     */
    LocalDateTime getCurrentTime();

    /**
     * Sets the simulation time.
     *
     * @param time the new simulation time
     * @throws SimulationException if the time cannot be set
     */
    void setCurrentTime(LocalDateTime time) throws SimulationException;
}
