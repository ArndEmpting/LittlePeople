package com.littlepeople.core.interfaces;

import com.littlepeople.core.exceptions.SimulationException;
import com.littlepeople.core.model.EventType;

/**
 * Interface for processing events in the simulation system.
 * Event processors are responsible for handling specific types of events
 * and implementing the business logic associated with them.
 */
public interface EventProcessor {

    /**
     * Gets the event type that this processor can handle.
     *
     * @return the event type
     */
    EventType getEventType();

    /**
     * Processes the given event.
     *
     * @param event the event to process
     * @throws SimulationException if processing fails
     */
    void processEvent(Event event) throws SimulationException;

    /**
     * Checks if this processor can handle the given event type.
     *
     * @param eventType the event type to check
     * @return true if this processor can handle the event type
     */
    boolean canProcess(EventType eventType);

    /**
     * Gets the priority of this processor when multiple processors
     * can handle the same event type.
     *
     * @return the processor priority (higher values = higher priority)
     */
    int getPriority();
}
