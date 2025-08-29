package com.littlepeople.core.model;

/**
 * Enumeration representing the possible states of a simulation component.
 *
 * <p>This enum defines the lifecycle states that simulation components
 * can be in, supporting the State pattern implementation throughout
 * the simulation system.</p>
 *
 * @since 1.0.0
 * @version 1.0.0
 */
public enum SimulationStatus {

    /**
     * The component has not been initialized yet.
     */
    NOT_INITIALIZED("Not Initialized", "Component requires initialization"),

    /**
     * The component has been initialized and is ready to start.
     */
    INITIALIZED("Initialized", "Component is ready to start"),

    /**
     * The component is currently running.
     */
    RUNNING("Running", "Component is actively executing"),

    /**
     * The component is temporarily paused but can be resumed.
     */
    PAUSED("Paused", "Component is paused and can be resumed"),

    /**
     * The component has been stopped and requires re-initialization.
     */
    STOPPED("Stopped", "Component has been stopped"),

    /**
     * The component is in an error state.
     */
    ERROR("Error", "Component encountered an error");

    private final String displayName;
    private final String description;

    /**
     * Constructs a new SimulationStatus.
     *
     * @param displayName the human-readable name of this status
     * @param description a description of what this status means
     */
    SimulationStatus(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }

    /**
     * Returns the human-readable display name for this status.
     *
     * @return the display name, never null or empty
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * Returns a description of what this status represents.
     *
     * @return the description, never null or empty
     */
    public String getDescription() {
        return description;
    }

    /**
     * Determines if the component can be started from this status.
     *
     * @return true if the component can be started, false otherwise
     */
    public boolean canStart() {
        return this == INITIALIZED || this == STOPPED;
    }

    /**
     * Determines if the component can be paused from this status.
     *
     * @return true if the component can be paused, false otherwise
     */
    public boolean canPause() {
        return this == RUNNING;
    }

    /**
     * Determines if the component can be resumed from this status.
     *
     * @return true if the component can be resumed, false otherwise
     */
    public boolean canResume() {
        return this == PAUSED;
    }

    /**
     * Determines if the component can be stopped from this status.
     *
     * @return true if the component can be stopped, false otherwise
     */
    public boolean canStop() {
        return this == RUNNING || this == PAUSED || this == ERROR;
    }
}
