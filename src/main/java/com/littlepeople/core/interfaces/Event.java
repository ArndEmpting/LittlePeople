package com.littlepeople.core.interfaces;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Base interface for all simulation events.
 *
 * <p>This interface defines the fundamental contract for events within
 * the simulation system. Events are immutable objects that represent
 * something that has happened or needs to happen within the simulation.</p>
 *
 * <p>Design Pattern: This interface supports the Event pattern,
 * enabling event-driven architecture throughout the system.</p>
 *
 * @since 1.0.0
 * @version 1.0.0
 */
public interface Event {

    /**
     * Returns the unique identifier for this event.
     *
     * @return the event ID, never null
     */
    String getId();

    /**
     * Returns the type of this event.
     *
     * @return the event type, never null
     */
    String getType();

    /**
     * Returns the timestamp when this event occurred.
     *
     * @return the event timestamp, never null
     */
    LocalDateTime getScheduledTime();

    /**
     * Returns the source entity that generated this event, if applicable.
     *
     * @return the source entity ID, or null if this is a system event
     */
    String getTargetEntityId();

    /**
     * Returns additional data associated with this event.
     *
     * @return a map of event data
     */
    Map<String, Object> getData();

    /**
     * Returns a specific data value from the event.
     *
     * @param key the data key
     * @return the data value, or null if not found
     */
    Object getData(String key);

    /**
     * Checks if this event has been processed.
     *
     * @return true if the event has been processed
     */
    boolean isProcessed();

    /**
     * Marks this event as processed.
     */
    void markProcessed();

    /**
     * Checks if this event has been cancelled.
     *
     * @return true if the event has been cancelled
     */
    boolean isCancelled();

    /**
     * Cancels this event.
     */
    void cancel();
}
