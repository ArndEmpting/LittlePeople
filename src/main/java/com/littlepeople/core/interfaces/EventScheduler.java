package com.littlepeople.core.interfaces;

import com.littlepeople.core.exceptions.SimulationException;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Interface for scheduling and managing events in the simulation system.
 * The event scheduler is responsible for queuing events, processing them
 * at the appropriate times, and managing event priorities.
 */
public interface EventScheduler {

    /**
     * Schedules an event to be processed at the specified time.
     *
     * @param event the event to schedule
     * @throws SimulationException if the event cannot be scheduled
     */
    void scheduleEvent(Event event) throws SimulationException;

    /**
     * Schedules an event to be processed after the specified delay from now.
     *
     * @param event the event to schedule
     * @param delayInDays the delay in days from the current time
     * @throws SimulationException if the event cannot be scheduled
     */
    void scheduleEventWithDelay(Event event, long delayInDays) throws SimulationException;

    /**
     * Cancels a scheduled event.
     *
     * @param eventId the ID of the event to cancel
     * @return true if the event was found and cancelled
     */
    boolean cancelEvent(String eventId);

    /**
     * Processes all events that are due at or before the current simulation time.
     *
     * @param currentTime the current simulation time
     * @throws SimulationException if event processing fails
     */
    void processEvents(LocalDateTime currentTime) throws SimulationException;

    /**
     * Gets all events scheduled for a specific time.
     *
     * @param time the time to check
     * @return list of events scheduled for that time
     */
    List<Event> getEventsAt(LocalDateTime time);

    /**
     * Gets all events scheduled between two times (inclusive).
     *
     * @param startTime the start time
     * @param endTime the end time
     * @return list of events in the time range
     */
    List<Event> getEventsBetween(LocalDateTime startTime, LocalDateTime endTime);

    /**
     * Gets the next scheduled event.
     *
     * @return the next event to be processed, or null if none scheduled
     */
    Event getNextEvent();

    /**
     * Gets the time of the next scheduled event.
     *
     * @return the time of the next event, or null if none scheduled
     */
    LocalDateTime getNextEventTime();

    /**
     * Gets the total number of scheduled events.
     *
     * @return the number of events in the queue
     */
    int getEventCount();

    /**
     * Checks if there are any events scheduled.
     *
     * @return true if there are events in the queue
     */
    boolean hasEvents();

    /**
     * Clears all scheduled events.
     */
    void clear();

    /**
     * Registers an event processor for a specific event type.
     *
     * @param processor the event processor to register
     */
    void registerProcessor(EventProcessor processor);

    /**
     * Unregisters an event processor.
     *
     * @param eventType the event type to unregister
     */
    void unregisterProcessor(String eventType);
}
