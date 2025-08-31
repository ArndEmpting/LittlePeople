package com.littlepeople.core.model;

import com.littlepeople.core.interfaces.Event;
import com.littlepeople.core.interfaces.EventProcessor;
import com.littlepeople.core.interfaces.EventScheduler;
import com.littlepeople.core.interfaces.SimulationClock;
import com.littlepeople.core.exceptions.SimulationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;

/**
 * Default implementation of the EventScheduler interface.
 * This class manages event scheduling, queuing, and processing in a thread-safe manner.
 */
public class DefaultEventScheduler implements EventScheduler {

    private static final Logger logger = LoggerFactory.getLogger(DefaultEventScheduler.class);

    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
    private final ReentrantReadWriteLock.ReadLock readLock = lock.readLock();
    private final ReentrantReadWriteLock.WriteLock writeLock = lock.writeLock();

    private final PriorityBlockingQueue<Event> eventQueue;
    private final Map<UUID, Event> eventMap;
    private final Map<Class<? extends Event>, EventProcessor> processors ;

    private final SimulationClock simulationClock;

    /**
     * Creates a new event scheduler with the specified simulation clock.
     *
     * @param simulationClock the simulation clock to use for timing
     */
    public DefaultEventScheduler(SimulationClock simulationClock) {
        if (simulationClock == null) {
            throw new IllegalArgumentException("Simulation clock cannot be null");
        }

        this.simulationClock = simulationClock;
        this.eventQueue = new PriorityBlockingQueue<>(100, new EventComparator());
        this.eventMap = new ConcurrentHashMap<>();
        this.processors = new ConcurrentHashMap<>();
    }

    @Override
    public void scheduleEvent(Event event) throws SimulationException {
        if (event == null) {
            throw new IllegalArgumentException("Event cannot be null");
        }
        if (event.isCancelled()) {
            throw new SimulationException("Cannot schedule a cancelled event");
        }

        writeLock.lock();
        try {
            eventQueue.offer(event);
            eventMap.put(event.getId(), event);
            logger.debug("Scheduled event: {}", event);
        } finally {
            writeLock.unlock();
        }
    }

    @Override
    public void scheduleEventWithDelay(Event event, long delayInDays) throws SimulationException {
        if (event == null) {
            throw new IllegalArgumentException("Event cannot be null");
        }
        if (delayInDays < 0) {
            throw new IllegalArgumentException("Delay cannot be negative");
        }

        LocalDateTime scheduledTime = simulationClock.getCurrentTime().plusDays(delayInDays);
        Event delayedEvent = new SimulationEvent(
            event.getType(),
            scheduledTime,
            event.getTargetEntityId(),
            ((SimulationEvent) event).getPriority(),
                 event.getData(),
                event.getSourceId()
        );

        scheduleEvent(delayedEvent);
    }

    @Override
    public boolean cancelEvent(String eventId) {
        if (eventId == null || eventId.trim().isEmpty()) {
            return false;
        }

        writeLock.lock();
        try {
            Event event = eventMap.get(eventId);
            if (event != null && !event.isProcessed()) {
                event.cancel();
                eventQueue.remove(event);
                eventMap.remove(eventId);
                logger.debug("Cancelled event: {}", event);
                return true;
            }
            return false;
        } finally {
            writeLock.unlock();
        }
    }

    @Override
    public void processEvents(LocalDateTime currentTime) throws SimulationException {
        if (currentTime == null) {
            throw new IllegalArgumentException("Current time cannot be null");
        }

        List<Event> eventsToProcess = new ArrayList<>();

        writeLock.lock();
        try {
            // Collect all events that are due
            while (!eventQueue.isEmpty()) {
                Event nextEvent = eventQueue.peek();
                if (nextEvent.getScheduledTime().isAfter(currentTime)) {
                    break; // No more events due at this time
                }

                Event event = eventQueue.poll();
                if (event != null && !event.isCancelled() && !event.isProcessed()) {
                    eventsToProcess.add(event);
                }
            }
        } finally {
            writeLock.unlock();
        }

        // Process events outside of the lock to avoid blocking
        for (Event event : eventsToProcess) {
            try {
                processEvent(event);
            } catch (Exception e) {
                logger.error("Error processing event: {}", event, e);
                throw new SimulationException("Failed to process event: " + event, e);
            }
        }
    }

    // Verarbeitung
    private void processEvent(Event event) throws SimulationException {
        EventProcessor processor = processors.get(event.getClass());
        if (processor == null) {
            logger.warn("Kein Prozessor für Eventklasse: {}", event.getClass());
            return;
        }

        try {
            processor.processEvent(event);
            event.markProcessed();
            eventMap.remove(event.getId());
            logger.debug("Processed event: {}", event);
        } catch (Exception e) {
            logger.error("Error in event processor for event: {}", event, e);
            throw new SimulationException("Event processing failed", e);
        }
    }

    @Override
    public List<Event> getEventsAt(LocalDateTime time) {
        if (time == null) {
            return Collections.emptyList();
        }

        readLock.lock();
        try {
            return eventQueue.stream()
                .filter(event -> event.getScheduledTime().equals(time))
                .filter(event -> !event.isCancelled())
                .collect(Collectors.toList());
        } finally {
            readLock.unlock();
        }
    }

    @Override
    public List<Event> getEventsBetween(LocalDateTime startTime, LocalDateTime endTime) {
        if (startTime == null || endTime == null) {
            return Collections.emptyList();
        }
        if (startTime.isAfter(endTime)) {
            throw new IllegalArgumentException("Start time cannot be after end time");
        }

        readLock.lock();
        try {
            return eventQueue.stream()
                .filter(event -> !event.getScheduledTime().isBefore(startTime))
                .filter(event -> !event.getScheduledTime().isAfter(endTime))
                .filter(event -> !event.isCancelled())
                .collect(Collectors.toList());
        } finally {
            readLock.unlock();
        }
    }

    @Override
    public Event getNextEvent() {
        readLock.lock();
        try {
            return eventQueue.stream()
                .filter(event -> !event.isCancelled())
                .findFirst()
                .orElse(null);
        } finally {
            readLock.unlock();
        }
    }

    @Override
    public LocalDateTime getNextEventTime() {
        Event nextEvent = getNextEvent();
        return nextEvent != null ? nextEvent.getScheduledTime() : null;
    }

    @Override
    public int getEventCount() {
        readLock.lock();
        try {
            return (int) eventQueue.stream()
                .filter(event -> !event.isCancelled())
                .count();
        } finally {
            readLock.unlock();
        }
    }

    @Override
    public boolean hasEvents() {
        return getEventCount() > 0;
    }

    @Override
    public void clear() {
        writeLock.lock();
        try {
            eventQueue.clear();
            eventMap.clear();
            logger.debug("Cleared all events from scheduler");
        } finally {
            writeLock.unlock();
        }
    }


    // Registrierung
    public void registerProcessor(EventProcessor processor) {
        if (processor == null || processor.getEventType() == null) {
            throw new IllegalArgumentException("Processor oder EventClass darf nicht null sein");
        }
        processors.put(processor.getEventType(), processor);
    }

    @Override
    public void unregisterProcessor(Class<? extends Event> eventType) {
        if (eventType != null) {
            processors.remove(eventType);
            logger.debug("Unregistered processor for event type: {}", eventType);
        }
    }

    /**
     * Comparator for ordering events by scheduled time and priority.
     */
    private static class EventComparator implements Comparator<Event> {
        @Override
        public int compare(Event e1, Event e2) {
            // First compare by scheduled time
            int timeComparison = e1.getScheduledTime().compareTo(e2.getScheduledTime());
            if (timeComparison != 0) {
                return timeComparison;
            }

            // If times are equal, compare by priority (higher priority first)
            int priority1 = (e1 instanceof SimulationEvent) ? ((SimulationEvent) e1).getPriority() : 0;
            int priority2 = (e2 instanceof SimulationEvent) ? ((SimulationEvent) e2).getPriority() : 0;
            return Integer.compare(priority2, priority1);
        }
    }
}
