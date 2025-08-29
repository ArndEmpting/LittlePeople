# RFC-003: Simulation Clock and Event System

**Version:** 1.0  
**Date:** August 27, 2025  
**Status:** Draft  
**Authors:** LittlePeople Development Team  
**Complexity:** High

## Summary

This RFC defines the simulation clock and event system that forms the temporal and behavioral foundation of the LittlePeople simulation engine. It establishes the mechanisms for time progression, event scheduling, processing, and dispatching, enabling the life cycle features that drive the entire simulation. The components defined here provide the core infrastructure that allows inhabitants to age, form relationships, have children, and die according to realistic patterns.

## Features Addressed

- **F013:** Simulation Clock
- **F014:** Event System
- **F009:** Aging System (time management component)
- **F016:** Simulation Control Interface (backend components)

## Technical Approach

### Simulation Clock Architecture

The Simulation Clock will serve as the temporal heart of the system, managing the progression of time and triggering appropriate events. It will support different time scales (daily, monthly, yearly) with yearly being the default for the MVP. The clock will be designed to:

1. Track the current simulation date and time
2. Progress time by configurable increments
3. Dispatch time-change events to registered listeners
4. Support pausing, resuming, and stepping time progression
5. Allow seeking to specific dates (for bookmarks or save/load operations)

### Event System Architecture

The Event System will provide a flexible, extensible framework for defining, scheduling, processing, and handling life events within the simulation. It will follow an event-driven architecture with:

1. A central event bus for publishing and subscribing to events
2. Event processors that respond to specific event types
3. Event scheduling for future time points
4. Historical event tracking for reporting and analysis
5. Event prioritization to ensure correct processing order

### Core Components

#### Clock Components

1. **SimulationClock:** Manages current date and time progression
2. **TimeUnit:** Enumeration of supported time increments (DAY, MONTH, YEAR)
3. **ClockListener:** Interface for components that need time change notifications
4. **ClockController:** Controls clock operations (start, pause, resume, seek)

#### Event Components

1. **Event:** Base class for all simulation events
2. **EventType:** Enumeration of supported event types
3. **EventBus:** Central event publishing and dispatching mechanism
4. **EventProcessor:** Interface for components that handle specific event types
5. **EventScheduler:** Schedules events for future time points
6. **EventHistory:** Maintains record of past events for querying

### Core Events

The system will define several core event types:

1. **TimeChangeEvent:** Fired when simulation time advances
2. **BirthEvent:** Represents the birth of a new inhabitant
3. **DeathEvent:** Represents the death of an inhabitant
4. **PartnershipEvent:** Represents partnership formation or dissolution
5. **AgingEvent:** Triggered when inhabitants age
6. **ImmigrationEvent:** Represents new inhabitants arriving
7. **EmigrationEvent:** Represents inhabitants leaving

## Technical Specifications

### SimulationClock Interface

```java
/**
 * Manages time progression for the simulation.
 */
public interface SimulationClock {
    
    /**
     * Gets the current simulation date.
     * 
     * @return the current date in the simulation
     */
    LocalDate getCurrentDate();
    
    /**
     * Advances simulation time by the configured time step.
     * 
     * @return the new current date after advancement
     * @throws SimulationException if time advancement fails
     */
    LocalDate advanceTime() throws SimulationException;
    
    /**
     * Advances simulation time by a specific number of time units.
     * 
     * @param steps number of time steps to advance
     * @return the new current date after advancement
     * @throws SimulationException if time advancement fails
     * @throws IllegalArgumentException if steps is negative
     */
    LocalDate advanceTime(int steps) throws SimulationException;
    
    /**
     * Sets the simulation to the specified date.
     * 
     * @param targetDate the date to set
     * @throws SimulationException if date seeking fails
     * @throws IllegalArgumentException if target date is before the current date
     */
    void seekTo(LocalDate targetDate) throws SimulationException;
    
    /**
     * Registers a listener to receive time change notifications.
     * 
     * @param listener the listener to register
     */
    void registerListener(ClockListener listener);
    
    /**
     * Unregisters a previously registered listener.
     * 
     * @param listener the listener to unregister
     * @return true if the listener was found and removed, false otherwise
     */
    boolean unregisterListener(ClockListener listener);
    
    /**
     * Gets the configured time unit for this clock.
     * 
     * @return the time unit (DAY, MONTH, YEAR)
     */
    TimeUnit getTimeUnit();
    
    /**
     * Sets the time unit for clock advancement.
     * 
     * @param timeUnit the time unit to use
     */
    void setTimeUnit(TimeUnit timeUnit);
    
    /**
     * Gets the simulation start date.
     * 
     * @return the date when the simulation started
     */
    LocalDate getStartDate();
}
```

### ClockListener Interface

```java
/**
 * Interface for components that need to be notified of time changes.
 */
public interface ClockListener {
    
    /**
     * Called when the simulation time changes.
     * 
     * @param oldDate the previous simulation date
     * @param newDate the new simulation date
     * @param timeUnit the time unit of the change
     */
    void onTimeChanged(LocalDate oldDate, LocalDate newDate, TimeUnit timeUnit);
}
```

### TimeUnit Enumeration

```java
/**
 * Enumeration of supported time units for simulation.
 */
public enum TimeUnit {
    /**
     * Daily time increment
     */
    DAY(1, ChronoUnit.DAYS),
    
    /**
     * Monthly time increment
     */
    MONTH(1, ChronoUnit.MONTHS),
    
    /**
     * Yearly time increment
     */
    YEAR(1, ChronoUnit.YEARS);
    
    private final int amount;
    private final ChronoUnit chronoUnit;
    
    TimeUnit(int amount, ChronoUnit chronoUnit) {
        this.amount = amount;
        this.chronoUnit = chronoUnit;
    }
    
    public LocalDate increment(LocalDate date) {
        return date.plus(amount, chronoUnit);
    }
}
```

### Event Interface

```java
/**
 * Base interface for all simulation events.
 */
public interface Event {
    
    /**
     * Gets the unique identifier for this event.
     * 
     * @return the event ID
     */
    UUID getId();
    
    /**
     * Gets the type of this event.
     * 
     * @return the event type
     */
    EventType getType();
    
    /**
     * Gets the date when this event occurred.
     * 
     * @return the event date
     */
    LocalDate getEventDate();
    
    /**
     * Gets the persons involved in this event.
     * 
     * @return list of involved persons
     */
    List<Person> getParticipants();
    
    /**
     * Gets additional event properties.
     * 
     * @return map of property names to values
     */
    Map<String, Object> getProperties();
    
    /**
     * Gets a specific property value.
     * 
     * @param key the property name
     * @return the property value, or null if not found
     */
    Object getProperty(String key);
}
```

### EventType Enumeration

```java
/**
 * Enumeration of supported event types in the simulation.
 */
public enum EventType {
    /**
     * Time change event
     */
    TIME_CHANGE,
    
    /**
     * Birth of a new inhabitant
     */
    BIRTH,
    
    /**
     * Death of an inhabitant
     */
    DEATH,
    
    /**
     * Partnership formation event
     */
    PARTNERSHIP_FORMATION,
    
    /**
     * Partnership dissolution event
     */
    PARTNERSHIP_DISSOLUTION,
    
    /**
     * Aging milestone event
     */
    AGING,
    
    /**
     * Immigration event
     */
    IMMIGRATION,
    
    /**
     * Emigration event
     */
    EMIGRATION
}
```

### EventBus Interface

```java
/**
 * Central event dispatching mechanism.
 */
public interface EventBus {
    
    /**
     * Publishes an event to all registered processors.
     * 
     * @param event the event to publish
     * @throws SimulationException if event processing fails
     */
    void publishEvent(Event event) throws SimulationException;
    
    /**
     * Registers an event processor for specific event types.
     * 
     * @param processor the processor to register
     */
    void registerProcessor(EventProcessor processor);
    
    /**
     * Unregisters an event processor.
     * 
     * @param processor the processor to unregister
     * @return true if processor was found and removed, false otherwise
     */
    boolean unregisterProcessor(EventProcessor processor);
    
    /**
     * Checks if an event type has registered processors.
     * 
     * @param eventType the event type to check
     * @return true if processors exist for this event type
     */
    boolean hasProcessorsForType(EventType eventType);
}
```

### EventProcessor Interface

```java
/**
 * Interface for components that process specific event types.
 */
public interface EventProcessor {
    
    /**
     * Processes an event.
     * 
     * @param event the event to process
     * @throws SimulationException if event processing fails
     */
    void processEvent(Event event) throws SimulationException;
    
    /**
     * Determines if this processor can handle the specified event type.
     * 
     * @param eventType the event type to check
     * @return true if this processor handles the event type
     */
    boolean canHandle(EventType eventType);
    
    /**
     * Gets the set of event types this processor can handle.
     * 
     * @return set of supported event types
     */
    Set<EventType> getSupportedEventTypes();
    
    /**
     * Gets the priority of this processor.
     * Higher priority processors are executed first.
     * 
     * @return the processor priority
     */
    int getPriority();
}
```

### EventScheduler Interface

```java
/**
 * Schedules events for future processing.
 */
public interface EventScheduler {
    
    /**
     * Schedules an event for a future date.
     * 
     * @param event the event to schedule
     * @param scheduledDate the date when the event should occur
     * @throws SimulationException if scheduling fails
     * @throws IllegalArgumentException if scheduled date is before current date
     */
    void scheduleEvent(Event event, LocalDate scheduledDate) throws SimulationException;
    
    /**
     * Gets all events scheduled for a specific date.
     * 
     * @param date the date to check
     * @return list of events scheduled for the date
     */
    List<Event> getEventsForDate(LocalDate date);
    
    /**
     * Processes all events scheduled for the current date.
     * 
     * @param currentDate the current simulation date
     * @throws SimulationException if event processing fails
     */
    void processScheduledEvents(LocalDate currentDate) throws SimulationException;
    
    /**
     * Cancels a previously scheduled event.
     * 
     * @param eventId the ID of the event to cancel
     * @return true if the event was found and canceled
     */
    boolean cancelEvent(UUID eventId);
}
```

### SimulationController Interface

```java
/**
 * Controls simulation execution state.
 */
public interface SimulationController {
    
    /**
     * Starts the simulation.
     * 
     * @throws SimulationException if starting fails
     */
    void start() throws SimulationException;
    
    /**
     * Pauses the simulation.
     * 
     * @throws SimulationException if pausing fails
     */
    void pause() throws SimulationException;
    
    /**
     * Resumes a paused simulation.
     * 
     * @throws SimulationException if resuming fails
     */
    void resume() throws SimulationException;
    
    /**
     * Steps the simulation forward by one time unit.
     * 
     * @return the new current date after stepping
     * @throws SimulationException if stepping fails
     */
    LocalDate step() throws SimulationException;
    
    /**
     * Steps the simulation forward by a specific number of time units.
     * 
     * @param steps the number of time units to advance
     * @return the new current date after stepping
     * @throws SimulationException if stepping fails
     * @throws IllegalArgumentException if steps is negative
     */
    LocalDate step(int steps) throws SimulationException;
    
    /**
     * Gets the current simulation status.
     * 
     * @return the current status (RUNNING, PAUSED, STOPPED)
     */
    SimulationStatus getStatus();
    
    /**
     * Sets the simulation speed.
     * 
     * @param speedFactor multiplier for simulation speed (0.1 to 10.0)
     * @throws IllegalArgumentException if speed factor is invalid
     */
    void setSpeed(double speedFactor);
    
    /**
     * Gets the current simulation speed factor.
     * 
     * @return the speed factor
     */
    double getSpeed();
}
```

### SimulationStatus Enumeration

```java
/**
 * Enumeration of possible simulation execution states.
 */
public enum SimulationStatus {
    /**
     * Simulation is running
     */
    RUNNING,
    
    /**
     * Simulation is paused
     */
    PAUSED,
    
    /**
     * Simulation is stopped
     */
    STOPPED,
    
    /**
     * Simulation is in the process of stepping
     */
    STEPPING
}
```

## Implementation Details

### SimulationClock Implementation

1. **DefaultSimulationClock**
   - Core clock implementation with support for different time units
   - Tracks current date and notifies listeners on changes
   - Implements time advancement and seeking logic
   - Handles listener registration and notification
   - Thread-safe design for concurrent access

```java
public class DefaultSimulationClock implements SimulationClock {
    private final LocalDate startDate;
    private LocalDate currentDate;
    private TimeUnit timeUnit;
    private final Set<ClockListener> listeners;
    private final Object lock = new Object();
    
    public DefaultSimulationClock(LocalDate startDate, TimeUnit timeUnit) {
        this.startDate = Objects.requireNonNull(startDate, "Start date cannot be null");
        this.currentDate = startDate;
        this.timeUnit = Objects.requireNonNull(timeUnit, "Time unit cannot be null");
        this.listeners = new CopyOnWriteArraySet<>();
    }
    
    @Override
    public LocalDate advanceTime() throws SimulationException {
        synchronized (lock) {
            LocalDate oldDate = currentDate;
            currentDate = timeUnit.increment(currentDate);
            notifyTimeChanged(oldDate, currentDate);
            return currentDate;
        }
    }
    
    // Other method implementations...
    
    private void notifyTimeChanged(LocalDate oldDate, LocalDate newDate) {
        for (ClockListener listener : listeners) {
            try {
                listener.onTimeChanged(oldDate, newDate, timeUnit);
            } catch (Exception e) {
                // Log exception but continue notifying other listeners
                logger.error("Error notifying listener: {}", listener, e);
            }
        }
    }
}
```

### Event System Implementation

1. **DefaultEventBus**
   - Core event dispatching implementation
   - Maintains registry of event processors by event type
   - Prioritizes processors for correct execution order
   - Thread-safe design for concurrent event publishing

```java
public class DefaultEventBus implements EventBus {
    private final Map<EventType, List<EventProcessor>> processorRegistry;
    private final Object lock = new Object();
    
    public DefaultEventBus() {
        this.processorRegistry = new EnumMap<>(EventType.class);
    }
    
    @Override
    public void publishEvent(Event event) throws SimulationException {
        Objects.requireNonNull(event, "Event cannot be null");
        
        List<EventProcessor> processors = getProcessorsForType(event.getType());
        if (processors.isEmpty()) {
            logger.debug("No processors registered for event type: {}", event.getType());
            return;
        }
        
        // Sort by priority
        processors.sort(Comparator.comparingInt(EventProcessor::getPriority).reversed());
        
        for (EventProcessor processor : processors) {
            try {
                processor.processEvent(event);
            } catch (Exception e) {
                throw new SimulationException("Error processing event: " + event.getType(), e);
            }
        }
    }
    
    // Other method implementations...
    
    private List<EventProcessor> getProcessorsForType(EventType eventType) {
        synchronized (lock) {
            return processorRegistry.getOrDefault(eventType, Collections.emptyList());
        }
    }
}
```

2. **DefaultEventScheduler**
   - Schedules events for future dates
   - Maintains a sorted queue of pending events
   - Integrates with SimulationClock for time-based event processing
   - Thread-safe design for concurrent scheduling

```java
public class DefaultEventScheduler implements EventScheduler, ClockListener {
    private final Map<LocalDate, List<Event>> scheduledEvents;
    private final EventBus eventBus;
    private final Object lock = new Object();
    
    public DefaultEventScheduler(EventBus eventBus) {
        this.eventBus = Objects.requireNonNull(eventBus, "Event bus cannot be null");
        this.scheduledEvents = new TreeMap<>();
    }
    
    @Override
    public void onTimeChanged(LocalDate oldDate, LocalDate newDate, TimeUnit timeUnit) {
        try {
            processScheduledEvents(newDate);
        } catch (SimulationException e) {
            logger.error("Error processing scheduled events for date: {}", newDate, e);
        }
    }
    
    // Other method implementations...
}
```

3. **AbstractEvent**
   - Base implementation for all event types
   - Handles common event properties and behavior
   - Immutable design for thread safety

```java
public abstract class AbstractEvent implements Event {
    private final UUID id;
    private final EventType type;
    private final LocalDate eventDate;
    private final List<Person> participants;
    private final Map<String, Object> properties;
    
    protected AbstractEvent(EventType type, LocalDate eventDate, List<Person> participants) {
        this.id = UUID.randomUUID();
        this.type = Objects.requireNonNull(type, "Event type cannot be null");
        this.eventDate = Objects.requireNonNull(eventDate, "Event date cannot be null");
        this.participants = new ArrayList<>(Objects.requireNonNull(participants, "Participants cannot be null"));
        this.properties = new HashMap<>();
    }
    
    // Interface method implementations...
}
```

### Controller Implementation

1. **DefaultSimulationController**
   - Implements simulation execution control
   - Manages simulation state (running, paused, stopped)
   - Controls speed and stepping functionality
   - Integrates with SimulationClock for time advancement

```java
public class DefaultSimulationController implements SimulationController {
    private final SimulationClock clock;
    private final Object lock = new Object();
    private SimulationStatus status = SimulationStatus.STOPPED;
    private double speedFactor = 1.0;
    private ScheduledExecutorService executor;
    
    public DefaultSimulationController(SimulationClock clock) {
        this.clock = Objects.requireNonNull(clock, "Clock cannot be null");
    }
    
    @Override
    public void start() throws SimulationException {
        synchronized (lock) {
            if (status == SimulationStatus.RUNNING) {
                return; // Already running
            }
            
            status = SimulationStatus.RUNNING;
            startClockThread();
        }
    }
    
    // Other method implementations...
    
    private void startClockThread() {
        executor = Executors.newSingleThreadScheduledExecutor();
        long interval = calculateInterval();
        
        executor.scheduleAtFixedRate(() -> {
            try {
                if (status == SimulationStatus.RUNNING) {
                    clock.advanceTime();
                }
            } catch (Exception e) {
                logger.error("Error advancing simulation time", e);
                try {
                    pause(); // Auto-pause on error
                } catch (SimulationException se) {
                    logger.error("Error pausing simulation after time advancement failure", se);
                }
            }
        }, interval, interval, TimeUnit.MILLISECONDS);
    }
    
    private long calculateInterval() {
        // Base interval calculation based on time unit and speed factor
        long baseInterval = switch (clock.getTimeUnit()) {
            case DAY -> 100; // 100ms per day
            case MONTH -> 500; // 500ms per month
            case YEAR -> 1000; // 1000ms per year
        };
        
        return (long) (baseInterval / speedFactor);
    }
}
```

## Acceptance Criteria

- [x] SimulationClock advances time at configurable steps (day, month, year)
- [x] Clock listeners are notified when time changes
- [x] Time-seeking functionality works for jumping to specific dates
- [x] EventBus correctly dispatches events to registered processors
- [x] Event processors execute in priority order
- [x] Event scheduler correctly triggers events at specified future dates
- [x] Controller can start, pause, resume, and step the simulation
- [x] Speed adjustment changes the rate of time progression
- [x] System handles concurrent event publishing and processing
- [x] Core event types (Birth, Death, Partnership, etc.) are properly defined
- [x] All components have comprehensive unit tests (>85% coverage)
- [x] System maintains time consistency across operations
- [x] Documentation is complete for all APIs and components

## Dependencies

**Builds Upon:**
- RFC-001: Project Setup and Architecture
- RFC-002: Core Entity Models

**Required For:**
- RFC-004: Life Cycle - Aging and Mortality
- RFC-005: Partnerships and Family Formation
- RFC-006: Population Management
- RFC-008: Simulation Control Interface

## Testing Strategy

### Unit Tests

#### SimulationClock Tests
- Test time advancement with different time units
- Test listener notification on time changes
- Test seeking to future and past dates
- Test concurrent access to clock methods

#### EventBus Tests
- Test event publishing to processors
- Test processor registration and unregistration
- Test priority-based processor execution
- Test error handling during event processing

#### EventScheduler Tests
- Test event scheduling for future dates
- Test event retrieval by date
- Test scheduled event processing
- Test cancellation of scheduled events

#### SimulationController Tests
- Test start/pause/resume functionality
- Test stepping behavior
- Test speed adjustment
- Test status reporting
- Test error handling during operations

### Integration Tests

- Test complete time progression ? event generation ? event processing flow
- Test clock-scheduler-bus integration for time-based events
- Test controller-clock integration for simulation control
- Test concurrent operations under load

## Security Considerations

- Ensure thread safety for concurrent access to shared state
- Validate all date inputs to prevent invalid time states
- Implement proper exception handling for event processing failures
- Consider performance implications of listener and processor registrations
- Ensure no circular dependencies between event processors

## Performance Considerations

- Optimize event dispatching for large numbers of processors
- Use efficient data structures for event scheduling and retrieval
- Consider batching time change notifications for performance
- Implement listener limitations to prevent excessive registrations
- Optimize concurrent access to simulation components
- Consider memory usage for long-running simulations with many events

## Open Questions

1. Should we support custom event types defined by extensions?
2. How granular should time progression be (e.g., should we support hourly steps)?
3. Should event history have size limitations to prevent memory issues?
4. How should we handle conflicting or dependent events scheduled for the same time?
5. Should we implement priority-based time scheduling (e.g., some events must occur before others)?

## References

- [Java Concurrency in Practice](https://jcip.net/)
- [Event-Driven Architecture](https://martinfowler.com/articles/201701-event-driven.html)
- [Java Time API Documentation](https://docs.oracle.com/en/java/javase/17/docs/api/java.base/java/time/package-summary.html)
- [Observer Pattern](https://refactoring.guru/design-patterns/observer)
- [Command Pattern](https://refactoring.guru/design-patterns/command) (for simulation control)
