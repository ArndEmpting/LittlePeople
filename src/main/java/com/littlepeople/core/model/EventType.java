package com.littlepeople.core.model;

/**
 * Enumeration representing different types of simulation events.
 *
 * <p>This enum defines the various event types that can occur within
 * the simulation system, supporting the Observer pattern implementation
 * for event-driven architecture.</p>
 *
 * @since 1.0.0
 * @version 1.0.0
 */
public enum EventType {

    /**
     * System-level events related to simulation lifecycle.
     */
    SYSTEM("System Event", "Events related to simulation system operations"),

    /**
     * Entity-related events such as creation, modification, or deletion.
     */
    ENTITY("Entity Event", "Events related to entity operations"),

    /**
     * Life cycle events such as birth, aging, or death.
     */
    LIFECYCLE("Lifecycle Event", "Events related to entity lifecycle"),

    /**
     * Relationship events such as partnership formation or dissolution.
     */
    RELATIONSHIP("Relationship Event", "Events related to entity relationships"),

    /**
     * Population-level events affecting groups of entities.
     */
    POPULATION("Population Event", "Events affecting population dynamics"),

    /**
     * Configuration change events.
     */
    CONFIGURATION("Configuration Event", "Events related to configuration changes"),

    /**
     * Error events for exception handling.
     */
    ERROR("Error Event", "Events related to error conditions"),

    /**
     * Custom user-defined events for extensions.
     */
    CUSTOM("Custom Event", "User-defined custom events");

    private final String displayName;
    private final String description;

    /**
     * Constructs a new EventType.
     *
     * @param displayName the human-readable name of this event type
     * @param description a description of what this event type represents
     */
    EventType(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }

    /**
     * Returns the human-readable display name for this event type.
     *
     * @return the display name, never null or empty
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * Returns a description of what this event type represents.
     *
     * @return the description, never null or empty
     */
    public String getDescription() {
        return description;
    }
}
