package com.littlepeople.core.model;

import com.littlepeople.core.interfaces.*;
import com.littlepeople.core.exceptions.SimulationException;
import com.littlepeople.core.model.events.TimeChangeEvent;
import com.littlepeople.core.util.SimulationTimeProvider;
import com.littlepeople.simulation.population.PopulationManager;
import com.littlepeople.simulation.population.PopulationManagerImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Main simulation engine that coordinates the simulation clock and event system.
 * This class implements SimulationLifecycle to provide overall simulation control.
 */
public class SimulationEngine implements SimulationLifecycle {

    private static final Logger logger = LoggerFactory.getLogger(SimulationEngine.class);

    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
    private final ReentrantReadWriteLock.ReadLock readLock = lock.readLock();
    private final ReentrantReadWriteLock.WriteLock writeLock = lock.writeLock();

    private final SimulationClock clock;
    private final EventScheduler eventScheduler;
    private final EventProcessorRegistry processorRegistry;
    private SimulationState state;
    private Thread simulationThread;
    private volatile boolean shouldStop;

    /**
     * Simulation states.
     */
    public enum SimulationState {
        STOPPED, RUNNING, PAUSED
    }

    /**
     * Creates a new simulation engine with the specified start time.
     *
     * @param startTime the initial simulation time
     */
    public SimulationEngine(SimulationClock clock) {
        this.clock = clock;
        this.eventScheduler = new DefaultEventScheduler(clock);

        this.processorRegistry = new EventProcessorRegistry(eventScheduler) ;
        this.state = SimulationState.STOPPED;
        this.shouldStop = false;

        // Initialize event processors
        initializeEventProcessors();
    }

    /**
     * Creates a new simulation engine starting at the current time.
     */
    public SimulationEngine() {
        this(new DefaultSimulationClock(LocalDateTime.now()));
    }

    /**
     * Initializes all event processors for the fully event-driven architecture.
     */
    private void initializeEventProcessors() {
        try {
            processorRegistry.registerAllProcessors();
            logger.info("Event processors initialized successfully");
        } catch (SimulationException e) {
            logger.error("Failed to initialize event processors", e);
            throw new RuntimeException("Simulation engine initialization failed", e);
        }
    }

    @Override
    public void start() throws SimulationException {
        writeLock.lock();
        try {
            if (state == SimulationState.RUNNING) {
                throw new SimulationException("Simulation is already running");
            }

            state = SimulationState.RUNNING;
            shouldStop = false;
            clock.start();

            simulationThread = new Thread(this::runSimulation, "SimulationEngine");
            simulationThread.start();

            logger.info("Simulation started at time: {}", clock.getCurrentTime());
        } finally {
            writeLock.unlock();
        }
    }

    @Override
    public void stop() throws SimulationException {
        writeLock.lock();
        try {
            if (state == SimulationState.STOPPED) {
                return; // Already stopped
            }

            shouldStop = true;
            state = SimulationState.STOPPED;
            clock.stop();

            if (simulationThread != null && simulationThread.isAlive()) {
                try {
                    simulationThread.interrupt();
                    simulationThread.join(5000); // Wait up to 5 seconds
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    logger.warn("Interrupted while stopping simulation thread");
                }
            }

            logger.info("Simulation stopped at time: {}", clock.getCurrentTime());
        } finally {
            writeLock.unlock();
        }
    }

    @Override
    public void pause() throws SimulationException {
        writeLock.lock();
        try {
            if (state != SimulationState.RUNNING) {
                throw new SimulationException("Cannot pause simulation that is not running");
            }

            state = SimulationState.PAUSED;
            clock.pause();

            logger.info("Simulation paused at time: {}", clock.getCurrentTime());
        } finally {
            writeLock.unlock();
        }
    }

    @Override
    public void resume() throws SimulationException {
        writeLock.lock();
        try {
            if (state != SimulationState.PAUSED) {
                throw new SimulationException("Cannot resume simulation that is not paused");
            }

            state = SimulationState.RUNNING;
            clock.resume();

            logger.info("Simulation resumed at time: {}", clock.getCurrentTime());
        } finally {
            writeLock.unlock();
        }
    }

    @Override
    public void reset() throws SimulationException {
        writeLock.lock();
        try {
            if (state == SimulationState.RUNNING) {
                stop();
            }

            clock.reset();
            eventScheduler.clear();

            // Clear person registry and reinitialize processors
            PersonRegistry.getPersonRegistry().clear();
            processorRegistry.unregisterAllProcessors();
            processorRegistry.registerAllProcessors();

            state = SimulationState.STOPPED;

            logger.info("Simulation reset to start time: {}", clock.getStartTime());
        } finally {
            writeLock.unlock();
        }
    }

    @Override
    public boolean isRunning() {
        readLock.lock();
        try {
            return state == SimulationState.RUNNING;
        } finally {
            readLock.unlock();
        }
    }

    @Override
    public boolean isPaused() {
        readLock.lock();
        try {
            return state == SimulationState.PAUSED;
        } finally {
            readLock.unlock();
        }
    }

    @Override
    public boolean isStopped() {
        readLock.lock();
        try {
            return state == SimulationState.STOPPED;
        } finally {
            readLock.unlock();
        }
    }

    @Override
    public LocalDateTime getCurrentTime() {
        return clock.getCurrentTime();
    }

    @Override
    public void setCurrentTime(LocalDateTime time) throws SimulationException {
        if (time == null) {
            throw new IllegalArgumentException("Time cannot be null");
        }

        writeLock.lock();
        try {
            if (state == SimulationState.RUNNING) {
                throw new SimulationException("Cannot set time while simulation is running");
            }

            clock.setCurrentTime(time);
            logger.debug("Simulation time set to: {}", time);
        } finally {
            writeLock.unlock();
        }
    }

    /**
     * Gets the simulation clock.
     *
     * @return the simulation clock
     */
    public SimulationClock getClock() {
        return clock;
    }

    /**
     * Gets the event scheduler.
     *
     * @return the event scheduler
     */
    public EventScheduler getEventScheduler() {
        return eventScheduler;
    }

    /**
     * Gets the current simulation state.
     *
     * @return the simulation state
     */
    public SimulationState getState() {
        readLock.lock();
        try {
            return state;
        } finally {
            readLock.unlock();
        }
    }




    /**
     * Schedules an event in the simulation.
     *
     * @param event the event to schedule
     * @throws SimulationException if the event cannot be scheduled
     */
    public void scheduleEvent(Event event) throws SimulationException {
        eventScheduler.scheduleEvent(event);
    }

    /**
     * Registers an event processor.
     *
     * @param processor the event processor to register
     */
    public void registerEventProcessor(EventProcessor processor) {
        eventScheduler.registerProcessor(processor);
    }

    /**
     * Main simulation loop that runs in a separate thread.
     */
    private void runSimulation() {
        logger.info("Simulation loop started");

        try {
            while (!shouldStop && !Thread.currentThread().isInterrupted()) {
                readLock.lock();
                SimulationState currentState;
                try {
                    currentState = state;
                } finally {
                    readLock.unlock();
                }

                if (currentState == SimulationState.RUNNING) {
                   // Advance time by a small increment (1 day)
                    LocalDate oldDate= clock.getCurrentTime().toLocalDate();

                    LocalDateTime currentTime = clock.advanceUnits(1, ChronoUnit.MONTHS);
                    LocalDate newDate= clock.getCurrentTime().toLocalDate();
                    // make and schedule a TimeChangeEvnt

                    Event timeChangeEvent = new TimeChangeEvent(newDate,oldDate, PopulationManagerImpl.getInstance().getPopulation());
                    scheduleEvent(timeChangeEvent);



                    // Process any events that are due
                    eventScheduler.processEvents(currentTime);

                    // Small delay to prevent excessive CPU usage
                    Thread.sleep(10);
                } else if (currentState == SimulationState.PAUSED) {
                    // Wait while paused
                    Thread.sleep(100);
                }
                if(PopulationManagerImpl.getInstance().getPopulation().isEmpty()){
                    logger.info("Population is empty, stopping simulation");
                    stop();
                }
            }
        } catch (InterruptedException e) {
            logger.debug("Simulation loop interrupted");
            Thread.currentThread().interrupt();
        } catch (Exception e) {
            logger.error("Error in simulation loop", e);
            writeLock.lock();
            try {
                state = SimulationState.STOPPED;
                clock.stop();
            } finally {
                writeLock.unlock();
            }
        }

        logger.debug("Simulation loop ended");
    }

    @Override
    public String toString() {
        readLock.lock();
        try {
            return String.format("SimulationEngine{state=%s, currentTime=%s, eventCount=%d}",
                    state, clock.getCurrentTime(), eventScheduler.getEventCount());
        } finally {
            readLock.unlock();
        }
    }



    /**
     * Gets the event processor registry.
     *
     * @return the event processor registry
     */
    public EventProcessorRegistry getProcessorRegistry() {
        return processorRegistry;
    }
}
