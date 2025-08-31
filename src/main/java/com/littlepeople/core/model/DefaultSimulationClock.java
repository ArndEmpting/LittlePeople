package com.littlepeople.core.model;

import com.littlepeople.core.interfaces.SimulationClock;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Default implementation of the SimulationClock interface.
 * This class manages simulation time and provides thread-safe operations.
 */
public class DefaultSimulationClock implements SimulationClock {

    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
    private final ReentrantReadWriteLock.ReadLock readLock = lock.readLock();
    private final ReentrantReadWriteLock.WriteLock writeLock = lock.writeLock();

    private LocalDateTime startTime;
    private LocalDateTime currentTime;
    private double timeScale;
    private boolean running;
    private boolean paused;

    /**
     * Creates a new simulation clock with the specified start time.
     *
     * @param startTime the initial simulation time
     */
    public DefaultSimulationClock(LocalDateTime startTime) {
        if (startTime == null) {
            throw new IllegalArgumentException("Start time cannot be null");
        }

        this.startTime = startTime;
        this.currentTime = startTime;
        this.timeScale = 1.0;
        this.running = false;
        this.paused = false;
    }

    /**
     * Creates a new simulation clock starting at the current real time.
     */
    public DefaultSimulationClock() {
        this(LocalDateTime.now());
    }

    @Override
    public LocalDateTime getCurrentTime() {
        readLock.lock();
        try {
            return currentTime;
        } finally {
            readLock.unlock();
        }
    }

    @Override
    public void setCurrentTime(LocalDateTime time) {
        if (time == null) {
            throw new IllegalArgumentException("Time cannot be null");
        }

        writeLock.lock();
        try {
            this.currentTime = time;
        } finally {
            writeLock.unlock();
        }
    }

    @Override
    public LocalDateTime advance(Duration duration) {
        if (duration == null) {
            throw new IllegalArgumentException("Duration cannot be null");
        }
        if (duration.isNegative()) {
            throw new IllegalArgumentException("Duration cannot be negative");
        }

        writeLock.lock();
        try {
            if (!paused && running) {
                Duration scaledDuration = duration.multipliedBy((long) timeScale);
                currentTime = currentTime.plus(scaledDuration);
            }
            return currentTime;
        } finally {
            writeLock.unlock();
        }
    }

    @Override
    public LocalDateTime advanceUnits(long years, ChronoUnit unit) {
        if (years < 0) {
            throw new IllegalArgumentException("Years cannot be negative");
        }

        writeLock.lock();
        try {
            if (!paused && running) {
                if(unit == ChronoUnit.YEARS) {

                    currentTime = currentTime.plusYears(years);
                } else if (unit == ChronoUnit.MONTHS) {
                    currentTime = currentTime.plusMonths(years);
                } else if (unit == ChronoUnit.DAYS) {
                    currentTime = currentTime.plusDays(years);
                } else {
                    throw new IllegalArgumentException("Unsupported ChronoUnit: " + unit);
                }
            }
            return currentTime;
        } finally {
            writeLock.unlock();
        }
    }

    @Override
    public double getTimeScale() {
        readLock.lock();
        try {
            return timeScale;
        } finally {
            readLock.unlock();
        }
    }

    @Override
    public void setTimeScale(double scale) {
        if (scale <= 0) {
            throw new IllegalArgumentException("Time scale must be positive");
        }

        writeLock.lock();
        try {
            this.timeScale = scale;
        } finally {
            writeLock.unlock();
        }
    }

    @Override
    public LocalDateTime getStartTime() {
        readLock.lock();
        try {
            return startTime;
        } finally {
            readLock.unlock();
        }
    }

    @Override
    public Duration getElapsedTime() {
        readLock.lock();
        try {
            return Duration.between(startTime, currentTime);
        } finally {
            readLock.unlock();
        }
    }

    @Override
    public boolean isRunning() {
        readLock.lock();
        try {
            return running && !paused;
        } finally {
            readLock.unlock();
        }
    }

    @Override
    public void start() {
        writeLock.lock();
        try {
            running = true;
            paused = false;
        } finally {
            writeLock.unlock();
        }
    }

    @Override
    public void stop() {
        writeLock.lock();
        try {
            running = false;
            paused = false;
        } finally {
            writeLock.unlock();
        }
    }

    @Override
    public void pause() {
        writeLock.lock();
        try {
            if (running) {
                paused = true;
            }
        } finally {
            writeLock.unlock();
        }
    }

    @Override
    public void resume() {
        writeLock.lock();
        try {
            if (running) {
                paused = false;
            }
        } finally {
            writeLock.unlock();
        }
    }

    @Override
    public void reset() {
        writeLock.lock();
        try {
            currentTime = startTime;
            running = false;
            paused = false;
        } finally {
            writeLock.unlock();
        }
    }

    /**
     * Gets the paused state of the clock.
     *
     * @return true if the clock is paused
     */
    public boolean isPaused() {
        readLock.lock();
        try {
            return paused;
        } finally {
            readLock.unlock();
        }
    }

    @Override
    public String toString() {
        readLock.lock();
        try {
            return String.format("DefaultSimulationClock{currentTime=%s, startTime=%s, timeScale=%.2f, running=%s, paused=%s}",
                    currentTime, startTime, timeScale, running, paused);
        } finally {
            readLock.unlock();
        }
    }
}
