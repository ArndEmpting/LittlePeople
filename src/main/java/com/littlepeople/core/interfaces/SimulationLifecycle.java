package com.littlepeople.core.interfaces;

import com.littlepeople.core.exceptions.SimulationException;
import com.littlepeople.core.exceptions.SimulationControlException;
import com.littlepeople.core.model.SimulationStatus;

/**
 * Interface for simulation lifecycle management.
 *
 * <p>This interface defines the contract for managing the lifecycle
 * of simulation components. It provides standard methods for
 * initialization, execution control, and status monitoring.</p>
 *
 * <p>Design Pattern: This interface implements the State pattern,
 * allowing simulation components to manage their operational state
 * in a consistent manner.</p>
 *
 * @since 1.0.0
 * @version 1.0.0
 */
public interface SimulationLifecycle {

    /**
     * Initializes the simulation component.
     *
     * <p>This method prepares the component for execution by setting up
     * necessary resources, configurations, and initial state. It should
     * be called before any other lifecycle methods.</p>
     *
     * @throws SimulationException if initialization fails
     */
    void initialize() throws SimulationException;

    /**
     * Starts the simulation component.
     *
     * <p>This method begins execution of the component. The component
     * must be in an initialized state before calling this method.</p>
     *
     * @throws SimulationControlException if the component cannot be started
     * @throws IllegalStateException if the component is not initialized
     */
    void start() throws SimulationControlException;

    /**
     * Pauses the simulation component.
     *
     * <p>This method temporarily suspends execution while preserving
     * the current state. The component can be resumed later.</p>
     *
     * @throws SimulationControlException if the component cannot be paused
     * @throws IllegalStateException if the component is not running
     */
    void pause() throws SimulationControlException;

    /**
     * Resumes the simulation component from a paused state.
     *
     * <p>This method continues execution from where it was paused,
     * restoring the component to its running state.</p>
     *
     * @throws SimulationControlException if the component cannot be resumed
     * @throws IllegalStateException if the component is not paused
     */
    void resume() throws SimulationControlException;

    /**
     * Stops the simulation component.
     *
     * <p>This method terminates execution and releases any resources.
     * After stopping, the component must be re-initialized before
     * it can be started again.</p>
     *
     * @throws SimulationControlException if the component cannot be stopped
     */
    void stop() throws SimulationControlException;

    /**
     * Returns the current status of the simulation component.
     *
     * <p>This method provides real-time information about the
     * component's operational state.</p>
     *
     * @return the current simulation status, never null
     */
    SimulationStatus getStatus();
}
