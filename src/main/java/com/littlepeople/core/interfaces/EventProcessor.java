package com.littlepeople.core.interfaces;

import com.littlepeople.core.exceptions.SimulationException;
import com.littlepeople.core.model.EventType;

/**
 * Interface for simulation event processing.
 *
 * <p>This interface defines the contract for components that can process
 * simulation events. It supports the Observer pattern by allowing
 * different processors to handle specific types of events within
 * the simulation system.</p>
 *
 * <p>Design Pattern: This interface implements the Observer/Handler pattern,
 * enabling a decoupled event-driven architecture where multiple processors
 * can respond to different event types.</p>
 *
 * @since 1.0.0
 * @version 1.0.0
 */
public interface EventProcessor {

    /**
     * Processes the given simulation event.
     *
     * <p>This method is called when an event occurs that this processor
     * can handle. Implementations should check if they can handle the
     * event type before processing.</p>
     *
     * @param event the event to process, must not be null
     * @throws IllegalArgumentException if the event is null
     * @throws SimulationException if an error occurs during event processing
     */
    void processEvent(Event event) throws SimulationException;

    /**
     * Determines if this processor can handle the specified event type.
     *
     * <p>This method allows the event system to efficiently route events
     * to appropriate processors without attempting to process incompatible
     * event types.</p>
     *
     * @param eventType the type of event to check, must not be null
     * @return true if this processor can handle the event type, false otherwise
     * @throws IllegalArgumentException if eventType is null
     */
    boolean canHandle(EventType eventType);
}
