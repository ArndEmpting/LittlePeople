# RFC-008: Simulation Control Interface

**Version:** 1.0  
**Date:** August 27, 2025  
**Status:** Draft  
**Authors:** LittlePeople Development Team  
**Complexity:** Low

## Summary

This RFC defines the Simulation Control Interface for the LittlePeople simulation engine. It establishes how users interact with and control the simulation, providing mechanisms to start, pause, resume, and step through the simulation at various speeds. This interface acts as the primary control layer between the user and the simulation engine, allowing writers to explore their simulated towns and observe interesting events at their own pace.

## Features Addressed

- **F016:** Simulation Control Interface
- **F013:** Simulation Clock (control aspects)
- **F019:** Simulation Bookmarking (partial foundation)
- **F037:** User Feedback System (control-related feedback)

## Technical Approach

### Simulation Control Architecture

The Simulation Control Interface will provide a clean abstraction layer between the user interface and the simulation engine core, enabling:

1. **Simulation State Management:** Control simulation execution states (running, paused, stopped)
2. **Time Progression Control:** Adjust simulation speed and time step size
3. **Simulation Interaction:** Provide mechanisms for stepping and seeking through time
4. **Feedback and Monitoring:** Report simulation status and progress
5. **Bookmarking:** Create and jump to saved time points in the simulation

### Core Components

#### Control Components

1. **SimulationController:** Central component orchestrating all simulation control operations
2. **UserFeedbackProvider:** Delivers status updates and progress information to user interfaces
3. **BookmarkManager:** Handles creation and navigation of simulation bookmarks
4. **SpeedController:** Manages simulation execution speed adjustment
5. **SimulationStatusTracker:** Tracks and reports current simulation state

### Core Events

The system will define several control-related events:

1. **SimulationStartEvent:** Triggered when simulation starts
2. **SimulationPauseEvent:** Triggered when simulation pauses
3. **SimulationResumeEvent:** Triggered when simulation resumes after pausing
4. **SimulationStepEvent:** Triggered when simulation steps forward
5. **SimulationSeekEvent:** Triggered when simulation seeks to a specific date
6. **SimulationSpeedChangeEvent:** Triggered when simulation speed changes

## Technical Specifications

### SimulationController Interface

```java
/**
 * Controls simulation execution, providing the primary interface for
 * starting, pausing, resuming, and stepping through the simulation.
 */
public interface SimulationController {
    
    /**
     * Starts the simulation from its current state.
     * 
     * @return true if the simulation started successfully
     * @throws SimulationControlException if start operation fails
     */
    boolean start() throws SimulationControlException;
    
    /**
     * Pauses a running simulation, maintaining current state.
     * 
     * @return true if the simulation paused successfully
     * @throws SimulationControlException if pause operation fails
     */
    boolean pause() throws SimulationControlException;
    
    /**
     * Resumes a paused simulation.
     * 
     * @return true if the simulation resumed successfully
     * @throws SimulationControlException if resume operation fails
     */
    boolean resume() throws SimulationControlException;
    
    /**
     * Stops the simulation completely.
     * 
     * @return true if the simulation stopped successfully
     * @throws SimulationControlException if stop operation fails
     */
    boolean stop() throws SimulationControlException;
    
    /**
     * Steps the simulation forward by one time unit.
     * 
     * @return the new simulation date after stepping
     * @throws SimulationControlException if step operation fails
     */
    LocalDate step() throws SimulationControlException;
    
    /**
     * Steps the simulation forward by a specified number of time units.
     * 
     * @param steps the number of time units to advance
     * @return the new simulation date after stepping
     * @throws SimulationControlException if step operation fails
     * @throws IllegalArgumentException if steps is negative
     */
    LocalDate step(int steps) throws SimulationControlException;
    
    /**
     * Seeks to a specific date in the simulation timeline.
     * 
     * @param targetDate the date to seek to
     * @return true if the seek operation succeeded
     * @throws SimulationControlException if seek operation fails
     * @throws IllegalArgumentException if target date is invalid
     */
    boolean seekTo(LocalDate targetDate) throws SimulationControlException;
    
    /**
     * Sets the simulation speed factor.
     * 
     * @param speedFactor the speed multiplier (0.1 to 10.0)
     * @throws IllegalArgumentException if speed factor is out of range
     * @throws SimulationControlException if speed change fails
     */
    void setSpeed(double speedFactor) throws SimulationControlException;
    
    /**
     * Gets the current simulation speed factor.
     * 
     * @return the current speed factor
     */
    double getSpeed();
    
    /**
     * Gets the current simulation status.
     * 
     * @return the current status
     */
    SimulationStatus getStatus();
    
    /**
     * Gets the current simulation date.
     * 
     * @return the current date in the simulation
     */
    LocalDate getCurrentDate();
    
    /**
     * Registers a listener for simulation control events.
     * 
     * @param listener the listener to register
     */
    void registerListener(SimulationControlListener listener);
    
    /**
     * Unregisters a simulation control listener.
     * 
     * @param listener the listener to unregister
     * @return true if the listener was found and removed
     */
    boolean unregisterListener(SimulationControlListener listener);
}
```

### SimulationStatus Enumeration

```java
/**
 * Enumeration of possible simulation execution states.
 */
public enum SimulationStatus {
    /**
     * Simulation is running and automatically advancing time
     */
    RUNNING,
    
    /**
     * Simulation is paused and maintaining state
     */
    PAUSED,
    
    /**
     * Simulation is stopped
     */
    STOPPED,
    
    /**
     * Simulation is in the process of stepping
     */
    STEPPING,
    
    /**
     * Simulation is in the process of seeking to a specific date
     */
    SEEKING,
    
    /**
     * Simulation is initializing
     */
    INITIALIZING,
    
    /**
     * Simulation has encountered an error
     */
    ERROR
}
```

### SimulationControlListener Interface

```java
/**
 * Interface for components that need to be notified of simulation control events.
 */
public interface SimulationControlListener {
    
    /**
     * Called when the simulation starts.
     * 
     * @param startEvent the start event
     */
    void onSimulationStart(SimulationStartEvent startEvent);
    
    /**
     * Called when the simulation pauses.
     * 
     * @param pauseEvent the pause event
     */
    void onSimulationPause(SimulationPauseEvent pauseEvent);
    
    /**
     * Called when the simulation resumes.
     * 
     * @param resumeEvent the resume event
     */
    void onSimulationResume(SimulationResumeEvent resumeEvent);
    
    /**
     * Called when the simulation stops.
     * 
     * @param stopEvent the stop event
     */
    void onSimulationStop(SimulationStopEvent stopEvent);
    
    /**
     * Called when the simulation steps forward.
     * 
     * @param stepEvent the step event
     */
    void onSimulationStep(SimulationStepEvent stepEvent);
    
    /**
     * Called when the simulation seeks to a specific date.
     * 
     * @param seekEvent the seek event
     */
    void onSimulationSeek(SimulationSeekEvent seekEvent);
    
    /**
     * Called when the simulation speed changes.
     * 
     * @param speedChangeEvent the speed change event
     */
    void onSimulationSpeedChange(SimulationSpeedChangeEvent speedChangeEvent);
    
    /**
     * Called when the simulation encounters an error.
     * 
     * @param errorEvent the error event
     */
    void onSimulationError(SimulationErrorEvent errorEvent);
}
```

### SimulationStartEvent Class

```java
/**
 * Event triggered when the simulation starts.
 */
public class SimulationStartEvent {
    
    private final LocalDate startDate;
    private final LocalDateTime timestamp;
    private final SimulationConfiguration config;
    
    /**
     * Creates a new simulation start event.
     * 
     * @param startDate the simulation start date
     * @param config the simulation configuration
     */
    public SimulationStartEvent(LocalDate startDate, SimulationConfiguration config) {
        this.startDate = startDate;
        this.timestamp = LocalDateTime.now();
        this.config = config;
    }
    
    /**
     * Gets the simulation start date.
     * 
     * @return the start date
     */
    public LocalDate getStartDate() {
        return startDate;
    }
    
    /**
     * Gets the real-time timestamp when the event occurred.
     * 
     * @return the timestamp
     */
    public LocalDateTime getTimestamp() {
        return timestamp;
    }
    
    /**
     * Gets the simulation configuration.
     * 
     * @return the configuration
     */
    public SimulationConfiguration getConfig() {
        return config;
    }
}
```

### SimulationStepEvent Class

```java
/**
 * Event triggered when the simulation steps forward.
 */
public class SimulationStepEvent {
    
    private final LocalDate previousDate;
    private final LocalDate newDate;
    private final TimeUnit timeUnit;
    private final int steps;
    private final LocalDateTime timestamp;
    
    /**
     * Creates a new simulation step event.
     * 
     * @param previousDate the previous simulation date
     * @param newDate the new simulation date after stepping
     * @param timeUnit the time unit that was stepped
     * @param steps the number of steps taken
     */
    public SimulationStepEvent(
            LocalDate previousDate, 
            LocalDate newDate, 
            TimeUnit timeUnit,
            int steps) {
        
        this.previousDate = previousDate;
        this.newDate = newDate;
        this.timeUnit = timeUnit;
        this.steps = steps;
        this.timestamp = LocalDateTime.now();
    }
    
    /**
     * Gets the previous simulation date before stepping.
     * 
     * @return the previous date
     */
    public LocalDate getPreviousDate() {
        return previousDate;
    }
    
    /**
     * Gets the new simulation date after stepping.
     * 
     * @return the new date
     */
    public LocalDate getNewDate() {
        return newDate;
    }
    
    /**
     * Gets the time unit that was stepped.
     * 
     * @return the time unit
     */
    public TimeUnit getTimeUnit() {
        return timeUnit;
    }
    
    /**
     * Gets the number of steps taken.
     * 
     * @return the step count
     */
    public int getSteps() {
        return steps;
    }
    
    /**
     * Gets the real-time timestamp when the event occurred.
     * 
     * @return the timestamp
     */
    public LocalDateTime getTimestamp() {
        return timestamp;
    }
}
```

### BookmarkManager Interface

```java
/**
 * Manages simulation bookmarks for easy navigation to specific points in time.
 */
public interface BookmarkManager {
    
    /**
     * Creates a bookmark at the current simulation date.
     * 
     * @param name the bookmark name
     * @param description the bookmark description (optional)
     * @return the created bookmark
     * @throws SimulationControlException if bookmark creation fails
     */
    Bookmark createBookmark(String name, String description) throws SimulationControlException;
    
    /**
     * Creates a bookmark at a specific date.
     * 
     * @param date the date to bookmark
     * @param name the bookmark name
     * @param description the bookmark description (optional)
     * @return the created bookmark
     * @throws SimulationControlException if bookmark creation fails
     */
    Bookmark createBookmark(LocalDate date, String name, String description) 
            throws SimulationControlException;
    
    /**
     * Jumps to a bookmarked date in the simulation.
     * 
     * @param bookmarkId the bookmark identifier
     * @return true if the jump succeeded
     * @throws SimulationControlException if jump operation fails
     */
    boolean jumpToBookmark(UUID bookmarkId) throws SimulationControlException;
    
    /**
     * Gets all available bookmarks.
     * 
     * @return list of bookmarks
     */
    List<Bookmark> getBookmarks();
    
    /**
     * Deletes a bookmark.
     * 
     * @param bookmarkId the bookmark identifier
     * @return true if the bookmark was found and deleted
     */
    boolean deleteBookmark(UUID bookmarkId);
    
    /**
     * Updates a bookmark's metadata.
     * 
     * @param bookmarkId the bookmark identifier
     * @param name the new name (or null to keep unchanged)
     * @param description the new description (or null to keep unchanged)
     * @return the updated bookmark
     * @throws SimulationControlException if bookmark update fails
     */
    Bookmark updateBookmark(UUID bookmarkId, String name, String description) 
            throws SimulationControlException;
}
```

### Bookmark Class

```java
/**
 * Represents a bookmark in the simulation timeline.
 */
public class Bookmark {
    
    private final UUID id;
    private String name;
    private String description;
    private final LocalDate simulationDate;
    private final LocalDateTime creationTime;
    
    /**
     * Creates a new bookmark.
     * 
     * @param name the bookmark name
     * @param description the bookmark description
     * @param simulationDate the simulation date
     */
    public Bookmark(String name, String description, LocalDate simulationDate) {
        this.id = UUID.randomUUID();
        this.name = Objects.requireNonNull(name, "Bookmark name cannot be null");
        this.description = description != null ? description : "";
        this.simulationDate = Objects.requireNonNull(simulationDate, "Simulation date cannot be null");
        this.creationTime = LocalDateTime.now();
    }
    
    /**
     * Gets the bookmark's unique identifier.
     * 
     * @return the identifier
     */
    public UUID getId() {
        return id;
    }
    
    /**
     * Gets the bookmark name.
     * 
     * @return the name
     */
    public String getName() {
        return name;
    }
    
    /**
     * Sets the bookmark name.
     * 
     * @param name the new name
     * @throws IllegalArgumentException if name is null or empty
     */
    public void setName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Bookmark name cannot be null or empty");
        }
        this.name = name;
    }
    
    /**
     * Gets the bookmark description.
     * 
     * @return the description
     */
    public String getDescription() {
        return description;
    }
    
    /**
     * Sets the bookmark description.
     * 
     * @param description the new description
     */
    public void setDescription(String description) {
        this.description = description != null ? description : "";
    }
    
    /**
     * Gets the simulation date this bookmark points to.
     * 
     * @return the simulation date
     */
    public LocalDate getSimulationDate() {
        return simulationDate;
    }
    
    /**
     * Gets the real-time when this bookmark was created.
     * 
     * @return the creation time
     */
    public LocalDateTime getCreationTime() {
        return creationTime;
    }
}
```

### UserFeedbackProvider Interface

```java
/**
 * Provides user feedback about simulation control operations.
 */
public interface UserFeedbackProvider {
    
    /**
     * Reports an operation started.
     * 
     * @param operationType the type of operation
     * @param details additional details about the operation
     */
    void reportOperationStarted(OperationType operationType, String details);
    
    /**
     * Reports an operation completed successfully.
     * 
     * @param operationType the type of operation
     * @param details additional details about the completion
     */
    void reportOperationCompleted(OperationType operationType, String details);
    
    /**
     * Reports an operation failed.
     * 
     * @param operationType the type of operation
     * @param error the error that occurred
     * @param details additional details about the failure
     */
    void reportOperationFailed(OperationType operationType, Throwable error, String details);
    
    /**
     * Reports progress of a long-running operation.
     * 
     * @param operationType the type of operation
     * @param progress the progress percentage (0-100)
     * @param details additional details about the progress
     */
    void reportProgress(OperationType operationType, int progress, String details);
    
    /**
     * Reports the current simulation status.
     * 
     * @param status the current status
     * @param simulationDate the current simulation date
     * @param details additional status details
     */
    void reportStatus(SimulationStatus status, LocalDate simulationDate, String details);
    
    /**
     * Gets the last reported status.
     * 
     * @return the last status report
     */
    StatusReport getLastStatus();
    
    /**
     * Clears all pending feedback messages.
     */
    void clearFeedback();
}
```

### OperationType Enumeration

```java
/**
 * Types of operations that can be performed on the simulation.
 */
public enum OperationType {
    /**
     * Starting the simulation
     */
    START,
    
    /**
     * Pausing the simulation
     */
    PAUSE,
    
    /**
     * Resuming the simulation
     */
    RESUME,
    
    /**
     * Stopping the simulation
     */
    STOP,
    
    /**
     * Stepping the simulation
     */
    STEP,
    
    /**
     * Seeking to a specific date
     */
    SEEK,
    
    /**
     * Changing simulation speed
     */
    SPEED_CHANGE,
    
    /**
     * Creating a bookmark
     */
    BOOKMARK_CREATE,
    
    /**
     * Jumping to a bookmark
     */
    BOOKMARK_JUMP,
    
    /**
     * Loading a simulation
     */
    LOAD,
    
    /**
     * Saving a simulation
     */
    SAVE
}
```

### StatusReport Class

```java
/**
 * Represents a simulation status report.
 */
public class StatusReport {
    
    private final SimulationStatus status;
    private final LocalDate simulationDate;
    private final LocalDateTime reportTime;
    private final String details;
    private final double speedFactor;
    private final Map<String, Object> additionalInfo;
    
    /**
     * Creates a new status report.
     * 
     * @param status the simulation status
     * @param simulationDate the current simulation date
     * @param speedFactor the current speed factor
     * @param details the status details
     * @param additionalInfo additional information to include
     */
    public StatusReport(
            SimulationStatus status,
            LocalDate simulationDate,
            double speedFactor,
            String details,
            Map<String, Object> additionalInfo) {
        
        this.status = status;
        this.simulationDate = simulationDate;
        this.reportTime = LocalDateTime.now();
        this.details = details != null ? details : "";
        this.speedFactor = speedFactor;
        this.additionalInfo = additionalInfo != null ? 
                new HashMap<>(additionalInfo) : new HashMap<>();
    }
    
    // Getters for all properties
    
    /**
     * Gets additional information with the specified key.
     * 
     * @param key the information key
     * @return the information value, or null if not found
     */
    public Object getAdditionalInfo(String key) {
        return additionalInfo.get(key);
    }
    
    /**
     * Formats the status report as a string for display.
     * 
     * @return formatted status report
     */
    public String format() {
        return String.format(
                "[%s] %s - Date: %s, Speed: %.1fx %s",
                status,
                reportTime.format(DateTimeFormatter.ofPattern("HH:mm:ss")),
                simulationDate,
                speedFactor,
                details.isEmpty() ? "" : "- " + details);
    }
}
```

## Implementation Details

### DefaultSimulationController

The main implementation of the simulation control system:

```java
/**
 * Default implementation of the SimulationController interface.
 */
@Component
public class DefaultSimulationController implements SimulationController, ClockListener {
    
    private static final Logger logger = LoggerFactory.getLogger(DefaultSimulationController.class);
    
    private static final double MIN_SPEED_FACTOR = 0.1;
    private static final double MAX_SPEED_FACTOR = 10.0;
    private static final double DEFAULT_SPEED_FACTOR = 1.0;
    
    private final SimulationClock clock;
    private final EventBus eventBus;
    private final UserFeedbackProvider feedbackProvider;
    private final List<SimulationControlListener> listeners;
    
    private final Object lock = new Object();
    private SimulationStatus status;
    private double speedFactor;
    private ScheduledExecutorService executorService;
    
    /**
     * Creates a new DefaultSimulationController.
     * 
     * @param clock the simulation clock to control
     * @param eventBus the event bus for publishing events
     * @param feedbackProvider the feedback provider for user updates
     */
    @Inject
    public DefaultSimulationController(
            SimulationClock clock,
            EventBus eventBus,
            UserFeedbackProvider feedbackProvider) {
        
        this.clock = Objects.requireNonNull(clock, "Clock cannot be null");
        this.eventBus = Objects.requireNonNull(eventBus, "Event bus cannot be null");
        this.feedbackProvider = Objects.requireNonNull(feedbackProvider, "Feedback provider cannot be null");
        this.listeners = new CopyOnWriteArrayList<>();
        
        this.status = SimulationStatus.STOPPED;
        this.speedFactor = DEFAULT_SPEED_FACTOR;
        
        logger.info("Simulation controller initialized");
    }
    
    @Override
    public boolean start() throws SimulationControlException {
        synchronized (lock) {
            if (status == SimulationStatus.RUNNING) {
                logger.debug("Simulation already running");
                return true; // Already running
            }
            
            if (status == SimulationStatus.ERROR) {
                logger.warn("Cannot start simulation in ERROR state");
                throw new SimulationControlException("Cannot start simulation in ERROR state");
            }
            
            try {
                feedbackProvider.reportOperationStarted(OperationType.START, "Starting simulation");
                
                // Create and start the time advancement thread
                if (executorService == null || executorService.isShutdown()) {
                    executorService = Executors.newSingleThreadScheduledExecutor(
                            r -> {
                                Thread t = new Thread(r, "SimulationTimeAdvancement");
                                t.setDaemon(true);
                                return t;
                            });
                }
                
                long intervalMs = calculateIntervalMillis();
                
                // Schedule time advancement at regular intervals
                executorService.scheduleAtFixedRate(
                        this::advanceTimeIfRunning,
                        100, // Initial delay
                        intervalMs, // Period
                        TimeUnit.MILLISECONDS);
                
                // Update status
                SimulationStatus oldStatus = status;
                status = SimulationStatus.RUNNING;
                
                // Notify listeners
                SimulationStartEvent startEvent = new SimulationStartEvent(
                        clock.getCurrentDate(),
                        null); // TODO: Get configuration
                
                notifyListeners(listener -> listener.onSimulationStart(startEvent));
                
                // Report status
                feedbackProvider.reportOperationCompleted(
                        OperationType.START,
                        "Simulation started at date " + clock.getCurrentDate());
                
                feedbackProvider.reportStatus(
                        status,
                        clock.getCurrentDate(),
                        "Simulation running at " + speedFactor + "x speed");
                
                logger.info("Simulation started from {} state", oldStatus);
                return true;
                
            } catch (Exception e) {
                status = SimulationStatus.ERROR;
                
                feedbackProvider.reportOperationFailed(
                        OperationType.START,
                        e,
                        "Failed to start simulation");
                
                logger.error("Failed to start simulation", e);
                throw new SimulationControlException("Failed to start simulation", e);
            }
        }
    }
    
    @Override
    public boolean pause() throws SimulationControlException {
        synchronized (lock) {
            if (status != SimulationStatus.RUNNING) {
                logger.debug("Cannot pause: simulation is not running (current state: {})", status);
                return false;
            }
            
            try {
                feedbackProvider.reportOperationStarted(OperationType.PAUSE, "Pausing simulation");
                
                // Update status
                status = SimulationStatus.PAUSED;
                
                // Notify listeners
                SimulationPauseEvent pauseEvent = new SimulationPauseEvent(
                        clock.getCurrentDate(),
                        LocalDateTime.now());
                
                notifyListeners(listener -> listener.onSimulationPause(pauseEvent));
                
                // Report status
                feedbackProvider.reportOperationCompleted(
                        OperationType.PAUSE,
                        "Simulation paused at date " + clock.getCurrentDate());
                
                feedbackProvider.reportStatus(
                        status,
                        clock.getCurrentDate(),
                        "Simulation paused");
                
                logger.info("Simulation paused at date {}", clock.getCurrentDate());
                return true;
                
            } catch (Exception e) {
                logger.error("Failed to pause simulation", e);
                throw new SimulationControlException("Failed to pause simulation", e);
            }
        }
    }
    
    @Override
    public boolean resume() throws SimulationControlException {
        synchronized (lock) {
            if (status != SimulationStatus.PAUSED) {
                logger.debug("Cannot resume: simulation is not paused (current state: {})", status);
                return false;
            }
            
            try {
                feedbackProvider.reportOperationStarted(OperationType.RESUME, "Resuming simulation");
                
                // Update status
                status = SimulationStatus.RUNNING;
                
                // Notify listeners
                SimulationResumeEvent resumeEvent = new SimulationResumeEvent(
                        clock.getCurrentDate(),
                        LocalDateTime.now());
                
                notifyListeners(listener -> listener.onSimulationResume(resumeEvent));
                
                // Report status
                feedbackProvider.reportOperationCompleted(
                        OperationType.RESUME,
                        "Simulation resumed at date " + clock.getCurrentDate());
                
                feedbackProvider.reportStatus(
                        status,
                        clock.getCurrentDate(),
                        "Simulation running at " + speedFactor + "x speed");
                
                logger.info("Simulation resumed at date {}", clock.getCurrentDate());
                return true;
                
            } catch (Exception e) {
                logger.error("Failed to resume simulation", e);
                throw new SimulationControlException("Failed to resume simulation", e);
            }
        }
    }
    
    @Override
    public boolean stop() throws SimulationControlException {
        synchronized (lock) {
            if (status == SimulationStatus.STOPPED) {
                logger.debug("Simulation already stopped");
                return true; // Already stopped
            }
            
            try {
                feedbackProvider.reportOperationStarted(OperationType.STOP, "Stopping simulation");
                
                // Shutdown executor if it exists
                if (executorService != null && !executorService.isShutdown()) {
                    executorService.shutdown();
                    try {
                        executorService.awaitTermination(5, TimeUnit.SECONDS);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        logger.warn("Interrupted while waiting for executor shutdown", e);
                    }
                    
                    if (!executorService.isTerminated()) {
                        executorService.shutdownNow();
                    }
                }
                
                // Update status
                SimulationStatus oldStatus = status;
                status = SimulationStatus.STOPPED;
                
                // Notify listeners
                SimulationStopEvent stopEvent = new SimulationStopEvent(
                        clock.getCurrentDate(),
                        LocalDateTime.now(),
                        oldStatus);
                
                notifyListeners(listener -> listener.onSimulationStop(stopEvent));
                
                // Report status
                feedbackProvider.reportOperationCompleted(
                        OperationType.STOP,
                        "Simulation stopped at date " + clock.getCurrentDate());
                
                feedbackProvider.reportStatus(
                        status,
                        clock.getCurrentDate(),
                        "Simulation stopped");
                
                logger.info("Simulation stopped from {} state at date {}", 
                        oldStatus, clock.getCurrentDate());
                return true;
                
            } catch (Exception e) {
                logger.error("Failed to stop simulation", e);
                throw new SimulationControlException("Failed to stop simulation", e);
            }
        }
    }
    
    @Override
    public LocalDate step() throws SimulationControlException {
        return step(1);
    }
    
    @Override
    public LocalDate step(int steps) throws SimulationControlException {
        if (steps <= 0) {
            throw new IllegalArgumentException("Steps must be positive");
        }
        
        synchronized (lock) {
            if (status == SimulationStatus.RUNNING) {
                logger.debug("Cannot step: simulation is running - pause first");
                throw new SimulationControlException("Cannot step while simulation is running");
            }
            
            try {
                // Remember old status to restore it after stepping
                SimulationStatus oldStatus = status;
                
                // Update status to STEPPING
                status = SimulationStatus.STEPPING;
                
                feedbackProvider.reportOperationStarted(
                        OperationType.STEP, 
                        "Stepping simulation " + steps + " time units");
                
                // Remember old date for event
                LocalDate oldDate = clock.getCurrentDate();
                
                // Step the clock forward
                LocalDate newDate = null;
                for (int i = 0; i < steps; i++) {
                    try {
                        // Report progress for multi-step operations
                        if (steps > 1) {
                            int progress = (i * 100) / steps;
                            feedbackProvider.reportProgress(
                                    OperationType.STEP,
                                    progress,
                                    String.format("Step %d of %d", i + 1, steps));
                        }
                        
                        newDate = clock.advanceTime();
                        
                    } catch (Exception e) {
                        logger.error("Error during time advancement on step {}/{}", i + 1, steps, e);
                        throw new SimulationControlException("Failed to advance time during step", e);
                    }
                }
                
                // Notify listeners
                SimulationStepEvent stepEvent = new SimulationStepEvent(
                        oldDate,
                        newDate,
                        clock.getTimeUnit(),
                        steps);
                
                notifyListeners(listener -> listener.onSimulationStep(stepEvent));
                
                // Restore previous status
                status = oldStatus == SimulationStatus.STEPPING ? SimulationStatus.PAUSED : oldStatus;
                
                // Report status
                feedbackProvider.reportOperationCompleted(
                        OperationType.STEP,
                        "Stepped simulation " + steps + " time units to " + newDate);
                
                feedbackProvider.reportStatus(
                        status,
                        newDate,
                        "Simulation stepped forward " + steps + " time units");
                
                logger.info("Simulation stepped {} time units from {} to {}", 
                        steps, oldDate, newDate);
                
                return newDate;
                
            } catch (Exception e) {
                // Restore status to PAUSED in case of error
                status = SimulationStatus.PAUSED;
                
                feedbackProvider.reportOperationFailed(
                        OperationType.STEP,
                        e,
                        "Failed to step simulation");
                
                logger.error("Failed to step simulation", e);
                throw new SimulationControlException("Failed to step simulation", e);
            }
        }
    }
    
    @Override
    public boolean seekTo(LocalDate targetDate) throws SimulationControlException {
        if (targetDate == null) {
            throw new IllegalArgumentException("Target date cannot be null");
        }
        
        synchronized (lock) {
            if (status == SimulationStatus.RUNNING) {
                logger.debug("Cannot seek: simulation is running - pause first");
                throw new SimulationControlException("Cannot seek while simulation is running");
            }
            
            LocalDate currentDate = clock.getCurrentDate();
            
            if (targetDate.equals(currentDate)) {
                logger.debug("Already at target date: {}", targetDate);
                return true; // Already at target date
            }
            
            if (targetDate.isBefore(currentDate)) {
                logger.warn("Cannot seek backward in time to {}", targetDate);
                throw new SimulationControlException("Cannot seek backward in time");
            }
            
            try {
                // Remember old status to restore it after seeking
                SimulationStatus oldStatus = status;
                
                // Update status to SEEKING
                status = SimulationStatus.SEEKING;
                
                feedbackProvider.reportOperationStarted(
                        OperationType.SEEK, 
                        "Seeking to date " + targetDate);
                
                // Calculate steps needed
                long days = ChronoUnit.DAYS.between(currentDate, targetDate);
                long steps;
                
                switch (clock.getTimeUnit()) {
                    case DAY:
                        steps = days;
                        break;
                    case MONTH:
                        steps = ChronoUnit.MONTHS.between(currentDate, targetDate);
                        break;
                    case YEAR:
                    default:
                        steps = ChronoUnit.YEARS.between(currentDate, targetDate);
                        break;
                }
                
                if (steps <= 0) {
                    logger.debug("Target date {} is not a full time unit ahead of current date {}", 
                            targetDate, currentDate);
                    throw new SimulationControlException("Target date is not a full time unit ahead");
                }
                
                // Seek is a potentially long operation, so we'll do it in chunks and report progress
                long totalSteps = steps;
                long remainingSteps = totalSteps;
                long chunkSize = Math.min(100, totalSteps); // Process in chunks of 100 steps
                
                while (remainingSteps > 0) {
                    long currentChunk = Math.min(chunkSize, remainingSteps);
                    
                    // Report progress
                    int progress = (int)(((totalSteps - remainingSteps) * 100) / totalSteps);
                    feedbackProvider.reportProgress(
                            OperationType.SEEK,
                            progress,
                            String.format("Processed %d of %d steps", 
                                    totalSteps - remainingSteps, totalSteps));
                    
                    // Process current chunk
                    for (int i = 0; i < currentChunk; i++) {
                        clock.advanceTime();
                    }
                    
                    remainingSteps -= currentChunk;
                }
                
                // Notify listeners
                SimulationSeekEvent seekEvent = new SimulationSeekEvent(
                        currentDate,
                        targetDate,
                        clock.getTimeUnit(),
                        (int)steps);
                
                notifyListeners(listener -> listener.onSimulationSeek(seekEvent));
                
                // Restore previous status
                status = oldStatus == SimulationStatus.SEEKING ? SimulationStatus.PAUSED : oldStatus;
                
                // Report status
                feedbackProvider.reportOperationCompleted(
                        OperationType.SEEK,
                        "Sought to date " + targetDate);
                
                feedbackProvider.reportStatus(
                        status,
                        targetDate,
                        "Simulation date is now " + targetDate);
                
                logger.info("Simulation sought from {} to {}", currentDate, targetDate);
                
                return true;
                
            } catch (Exception e) {
                // Restore status to PAUSED in case of error
                status = SimulationStatus.PAUSED;
                
                feedbackProvider.reportOperationFailed(
                        OperationType.SEEK,
                        e,
                        "Failed to seek to date " + targetDate);
                
                logger.error("Failed to seek to date {}", targetDate, e);
                throw new SimulationControlException("Failed to seek to date " + targetDate, e);
            }
        }
    }
    
    @Override
    public void setSpeed(double speedFactor) throws SimulationControlException {
        if (speedFactor < MIN_SPEED_FACTOR || speedFactor > MAX_SPEED_FACTOR) {
            throw new IllegalArgumentException(String.format(
                    "Speed factor must be between %.1f and %.1f", 
                    MIN_SPEED_FACTOR, MAX_SPEED_FACTOR));
        }
        
        synchronized (lock) {
            if (this.speedFactor == speedFactor) {
                return; // No change
            }
            
            try {
                double oldSpeed = this.speedFactor;
                this.speedFactor = speedFactor;
                
                // Only restart the executor if simulation is running
                if (status == SimulationStatus.RUNNING && executorService != null) {
                    // Shutdown existing executor
                    executorService.shutdown();
                    try {
                        executorService.awaitTermination(1, TimeUnit.SECONDS);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        logger.warn("Interrupted while waiting for executor shutdown", e);
                    }
                    
                    // Create new executor with updated interval
                    executorService = Executors.newSingleThreadScheduledExecutor(
                            r -> {
                                Thread t = new Thread(r, "SimulationTimeAdvancement");
                                t.setDaemon(true);
                                return t;
                            });
                    
                    long intervalMs = calculateIntervalMillis();
                    
                    // Schedule time advancement at new interval
                    executorService.scheduleAtFixedRate(
                            this::advanceTimeIfRunning,
                            100, // Initial delay
                            intervalMs, // Period
                            TimeUnit.MILLISECONDS);
                }
                
                // Notify listeners
                SimulationSpeedChangeEvent speedChangeEvent = new SimulationSpeedChangeEvent(
                        oldSpeed, 
                        speedFactor,
                        clock.getCurrentDate());
                
                notifyListeners(listener -> listener.onSimulationSpeedChange(speedChangeEvent));
                
                // Report status
                feedbackProvider.reportOperationCompleted(
                        OperationType.SPEED_CHANGE,
                        String.format("Speed changed from %.1fx to %.1fx", oldSpeed, speedFactor));
                
                feedbackProvider.reportStatus(
                        status,
                        clock.getCurrentDate(),
                        String.format("Simulation running at %.1fx speed", speedFactor));
                
                logger.info("Simulation speed changed from {}x to {}x", oldSpeed, speedFactor);
                
            } catch (Exception e) {
                logger.error("Failed to change simulation speed", e);
                throw new SimulationControlException("Failed to change simulation speed", e);
            }
        }
    }
    
    @Override
    public double getSpeed() {
        return speedFactor;
    }
    
    @Override
    public SimulationStatus getStatus() {
        return status;
    }
    
    @Override
    public LocalDate getCurrentDate() {
        return clock.getCurrentDate();
    }
    
    @Override
    public void registerListener(SimulationControlListener listener) {
        if (listener != null && !listeners.contains(listener)) {
            listeners.add(listener);
            logger.debug("Registered simulation control listener: {}", listener.getClass().getName());
        }
    }
    
    @Override
    public boolean unregisterListener(SimulationControlListener listener) {
        boolean removed = listeners.remove(listener);
        if (removed) {
            logger.debug("Unregistered simulation control listener: {}", listener.getClass().getName());
        }
        return removed;
    }
    
    @Override
    public void onTimeChanged(LocalDate oldDate, LocalDate newDate, com.littlepeople.core.model.TimeUnit timeUnit) {
        // This method is called by the SimulationClock when time changes
        // We use it to update feedback but don't need to take any control actions
        feedbackProvider.reportStatus(
                status,
                newDate,
                String.format("Simulation date advanced to %s", newDate));
    }
    
    /**
     * Advances simulation time if the simulation is running.
     * This method is called by the scheduled executor service.
     */
    private void advanceTimeIfRunning() {
        synchronized (lock) {
            if (status == SimulationStatus.RUNNING) {
                try {
                    clock.advanceTime();
                } catch (Exception e) {
                    logger.error("Error advancing time in background thread", e);
                    
                    // Report error but don't change status as that requires proper exception handling
                    // The next call that acquires the lock will see the error
                    SimulationErrorEvent errorEvent = new SimulationErrorEvent(
                            "Error advancing time in background thread",
                            e,
                            clock.getCurrentDate());
                    
                    notifyListeners(listener -> listener.onSimulationError(errorEvent));
                    
                    feedbackProvider.reportOperationFailed(
                            OperationType.START,
                            e,
                            "Error advancing time in background thread");
                }
            }
        }
    }
    
    /**
     * Calculates the time advancement interval in milliseconds based on the current speed factor.
     * 
     * @return the interval in milliseconds
     */
    private long calculateIntervalMillis() {
        // Base interval depends on time unit
        long baseIntervalMs;
        
        switch (clock.getTimeUnit()) {
            case DAY:
                baseIntervalMs = 100; // 100ms per day at 1x speed
                break;
            case MONTH:
                baseIntervalMs = 500; // 500ms per month at 1x speed
                break;
            case YEAR:
            default:
                baseIntervalMs = 1000; // 1000ms (1 second) per year at 1x speed
                break;
        }
        
        // Adjust based on speed factor
        // Higher speed = lower interval (faster updates)
        return (long)(baseIntervalMs / speedFactor);
    }
    
    /**
     * Notifies all listeners using the provided notification function.
     * 
     * @param notificationFunction the function to apply to each listener
     */
    private void notifyListeners(Consumer<SimulationControlListener> notificationFunction) {
        for (SimulationControlListener listener : listeners) {
            try {
                notificationFunction.accept(listener);
            } catch (Exception e) {
                logger.error("Error notifying listener: {}", listener.getClass().getName(), e);
            }
        }
    }
}
```

### DefaultBookmarkManager

The implementation for the bookmark management system:

```java
/**
 * Default implementation of the BookmarkManager interface.
 */
@Component
public class DefaultBookmarkManager implements BookmarkManager {
    
    private static final Logger logger = LoggerFactory.getLogger(DefaultBookmarkManager.class);
    
    private final SimulationController controller;
    private final Map<UUID, Bookmark> bookmarks;
    
    /**
     * Creates a new DefaultBookmarkManager.
     * 
     * @param controller the simulation controller
     */
    @Inject
    public DefaultBookmarkManager(SimulationController controller) {
        this.controller = Objects.requireNonNull(controller, "Controller cannot be null");
        this.bookmarks = new ConcurrentHashMap<>();
        logger.info("Bookmark manager initialized");
    }
    
    @Override
    public Bookmark createBookmark(String name, String description) throws SimulationControlException {
        return createBookmark(controller.getCurrentDate(), name, description);
    }
    
    @Override
    public Bookmark createBookmark(LocalDate date, String name, String description) 
            throws SimulationControlException {
        
        if (date == null) {
            throw new IllegalArgumentException("Date cannot be null");
        }
        
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Bookmark name cannot be null or empty");
        }
        
        try {
            Bookmark bookmark = new Bookmark(name, description, date);
            bookmarks.put(bookmark.getId(), bookmark);
            
            logger.info("Created bookmark '{}' at date {}", name, date);
            return bookmark;
            
        } catch (Exception e) {
            logger.error("Failed to create bookmark '{}' at date {}", name, date, e);
            throw new SimulationControlException("Failed to create bookmark", e);
        }
    }
    
    @Override
    public boolean jumpToBookmark(UUID bookmarkId) throws SimulationControlException {
        if (bookmarkId == null) {
            throw new IllegalArgumentException("Bookmark ID cannot be null");
        }
        
        Bookmark bookmark = bookmarks.get(bookmarkId);
        if (bookmark == null) {
            logger.warn("Bookmark not found: {}", bookmarkId);
            return false;
        }
        
        try {
            boolean result = controller.seekTo(bookmark.getSimulationDate());
            
            if (result) {
                logger.info("Jumped to bookmark '{}' at date {}", 
                        bookmark.getName(), bookmark.getSimulationDate());
            } else {
                logger.warn("Failed to jump to bookmark '{}' at date {}", 
                        bookmark.getName(), bookmark.getSimulationDate());
            }
            
            return result;
            
        } catch (Exception e) {
            logger.error("Failed to jump to bookmark '{}' at date {}", 
                    bookmark.getName(), bookmark.getSimulationDate(), e);
            throw new SimulationControlException("Failed to jump to bookmark", e);
        }
    }
    
    @Override
    public List<Bookmark> getBookmarks() {
        // Sort bookmarks by date
        return bookmarks.values().stream()
                .sorted(Comparator.comparing(Bookmark::getSimulationDate))
                .collect(Collectors.toList());
    }
    
    @Override
    public boolean deleteBookmark(UUID bookmarkId) {
        if (bookmarkId == null) {
            return false;
        }
        
        Bookmark removed = bookmarks.remove(bookmarkId);
        if (removed != null) {
            logger.info("Deleted bookmark '{}' at date {}", 
                    removed.getName(), removed.getSimulationDate());
            return true;
        }
        
        return false;
    }
    
    @Override
    public Bookmark updateBookmark(UUID bookmarkId, String name, String description) 
            throws SimulationControlException {
        
        if (bookmarkId == null) {
            throw new IllegalArgumentException("Bookmark ID cannot be null");
        }
        
        Bookmark bookmark = bookmarks.get(bookmarkId);
        if (bookmark == null) {
            logger.warn("Bookmark not found: {}", bookmarkId);
            throw new SimulationControlException("Bookmark not found: " + bookmarkId);
        }
        
        try {
            if (name != null && !name.trim().isEmpty()) {
                bookmark.setName(name);
            }
            
            if (description != null) {
                bookmark.setDescription(description);
            }
            
            logger.info("Updated bookmark '{}' at date {}", 
                    bookmark.getName(), bookmark.getSimulationDate());
            
            return bookmark;
            
        } catch (Exception e) {
            logger.error("Failed to update bookmark '{}'", bookmark.getName(), e);
            throw new SimulationControlException("Failed to update bookmark", e);
        }
    }
}
```

### ConsoleUserFeedbackProvider

Example implementation for console user interface feedback:

```java
/**
 * Provides user feedback through the console interface.
 */
@Component
public class ConsoleUserFeedbackProvider implements UserFeedbackProvider {
    
    private static final Logger logger = LoggerFactory.getLogger(ConsoleUserFeedbackProvider.class);
    
    private final PrintStream console;
    private final Queue<String> messageQueue;
    private StatusReport lastStatus;
    
    /**
     * Creates a new ConsoleUserFeedbackProvider.
     */
    public ConsoleUserFeedbackProvider() {
        this.console = System.out;
        this.messageQueue = new ConcurrentLinkedQueue<>();
        logger.info("Console feedback provider initialized");
    }
    
    @Override
    public void reportOperationStarted(OperationType operationType, String details) {
        String message = String.format("[%s] Started: %s", operationType, details);
        enqueueMessage(message);
        printMessage(message);
    }
    
    @Override
    public void reportOperationCompleted(OperationType operationType, String details) {
        String message = String.format("[%s] Completed: %s", operationType, details);
        enqueueMessage(message);
        printMessage(message);
    }
    
    @Override
    public void reportOperationFailed(OperationType operationType, Throwable error, String details) {
        String message = String.format("[%s] Failed: %s - %s", 
                operationType, details, error.getMessage());
        enqueueMessage(message);
        printMessage(message);
    }
    
    @Override
    public void reportProgress(OperationType operationType, int progress, String details) {
        String message = String.format("[%s] Progress %d%%: %s", 
                operationType, progress, details);
        enqueueMessage(message);
        printMessage(message);
    }
    
    @Override
    public void reportStatus(SimulationStatus status, LocalDate simulationDate, String details) {
        Map<String, Object> additionalInfo = new HashMap<>();
        
        lastStatus = new StatusReport(
                status,
                simulationDate,
                1.0, // Speed factor would be obtained from controller in real implementation
                details,
                additionalInfo);
        
        String message = String.format("[STATUS] %s - %s - %s", 
                status, simulationDate, details);
        enqueueMessage(message);
        printMessage(message);
    }
    
    @Override
    public StatusReport getLastStatus() {
        return lastStatus;
    }
    
    @Override
    public void clearFeedback() {
        messageQueue.clear();
    }
    
    /**
     * Gets all pending messages.
     * 
     * @return list of pending messages
     */
    public List<String> getPendingMessages() {
        return new ArrayList<>(messageQueue);
    }
    
    /**
     * Adds a message to the queue.
     * 
     * @param message the message to add
     */
    private void enqueueMessage(String message) {
        messageQueue.add(message);
        
        // Trim queue if it gets too large
        while (messageQueue.size() > 100) {
            messageQueue.poll();
        }
    }
    
    /**
     * Prints a message to the console.
     * 
     * @param message the message to print
     */
    private void printMessage(String message) {
        console.println(message);
    }
}
```

## Acceptance Criteria

- [x] Simulation can be started, paused, resumed, and stopped
- [x] Users can step through simulation year by year or in multi-year steps
- [x] Speed adjustment allows from 0.1x to 10.0x normal speed
- [x] Users can create bookmarks at interesting points in the simulation
- [x] Users can jump to bookmarked dates
- [x] User feedback is provided for all operations
- [x] Simulation state is correctly maintained during control operations
- [x] All control operations handle errors gracefully
- [x] Control events are dispatched to registered listeners
- [x] Performance remains stable during long-running simulations
- [x] System handles invalid operations appropriately (e.g., seeking backwards)
- [x] All components are well-documented with complete JavaDoc
- [x] All components have comprehensive unit tests

## Dependencies

**Builds Upon:**
- RFC-001: Project Setup and Architecture
- RFC-003: Simulation Clock and Event System
- RFC-007: Configuration System

**Required For:**
- RFC-009: Persistence Layer (for save/load integration)
- RFC-010: Console UI
- RFC-011: Statistics and Reporting

## Testing Strategy

### Unit Tests

#### SimulationController Tests
- Test starting, pausing, resuming, and stopping
- Test stepping with various step counts
- Test seeking to valid and invalid dates
- Test speed adjustment within valid ranges
- Test listener notification for all events
- Test error handling for invalid operations
- Test concurrent access safety

#### BookmarkManager Tests
- Test bookmark creation with valid and invalid parameters
- Test jumping to bookmarks
- Test bookmark retrieval and sorting
- Test bookmark deletion
- Test bookmark updating

#### UserFeedbackProvider Tests
- Test all feedback message types
- Test status reporting
- Test progress updates for long operations
- Test message queue management

### Integration Tests

- Test complete simulation control workflow
- Test feedback during various operations
- Test bookmark creation and navigation
- Test performance under various speed settings
- Test proper event propagation across components

## Security Considerations

- Validate all user inputs for control operations
- Ensure thread safety for concurrent operations
- Implement proper locking to prevent race conditions
- Handle exceptions securely without exposing internal details
- Protect bookmark metadata from injection or corruption

## Performance Considerations

- Optimize time advancement algorithm for smooth operation
- Implement efficient event dispatching to listeners
- Handle large numbers of bookmarks efficiently
- Ensure seeking operations scale well for large time jumps
- Consider background thread management for time advancement
- Implement proper cancellation of scheduled tasks

## Open Questions

1. Should we support seeking backwards in time (would require state snapshots)?
2. How should bookmark persistence be implemented (file format, storage location)?
3. What additional metadata should be captured with bookmarks?
4. Should simulation state be fully restorable from bookmarks?
5. How should we handle very large time jumps (e.g., seeking 1000+ years ahead)?

## References

- [Java Concurrency in Practice](https://jcip.net/)
- [Observer Pattern](https://refactoring.guru/design-patterns/observer)
- [Command Pattern](https://refactoring.guru/design-patterns/command)
- [Java ScheduledExecutorService](https://docs.oracle.com/en/java/javase/17/docs/api/java.base/java/util/concurrent/ScheduledExecutorService.html)
