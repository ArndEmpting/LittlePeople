package com.littlepeople.core.interfaces;

import com.littlepeople.core.model.EventType;
import java.time.Instant;
import java.util.UUID;

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
    UUID getId();

    /**
     * Returns the type of this event.
     *
     * @return the event type, never null
     */
    EventType getType();

    /**
     * Returns the timestamp when this event occurred.
     *
     * @return the event timestamp, never null
     */
    Instant getTimestamp();

    /**
     * Returns the source entity that generated this event, if applicable.
     *
     * @return the source entity ID, or null if this is a system event
     */
    UUID getSourceId();

    /**
     * Returns additional data associated with this event.
     *
     * @return the event data, may be null
     */
    Object getData();
}
