package com.littlepeople.core.model.events;

import com.littlepeople.core.interfaces.Event;
import com.littlepeople.core.model.EventType;
import com.littlepeople.core.model.Person;
import com.littlepeople.core.util.SimulationTimeProvider;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Event representing a time progression in the simulation.
 * This event triggers various processors to handle aging, mortality, and other time-based changes.
 */
public class TimeChangeEvent implements Event {

    private final UUID id;
    private final LocalDate newDate;
    private final LocalDate previousDate;
    private final List<Person> affectedPersonIds;
    private final Instant timestamp;
    private final LocalDateTime scheduledTime;
    private final Map<String, Object> data;
    private boolean processed = false;
    private boolean cancelled = false;

    public TimeChangeEvent(LocalDate newDate, LocalDate previousDate, List<Person> affectedPersonIds) {
        this.id = UUID.randomUUID();
        this.newDate = newDate;
        this.previousDate = previousDate;
        this.affectedPersonIds = affectedPersonIds;
        this.timestamp = Instant.now();
        this.scheduledTime = SimulationTimeProvider.getCurrentSimulationTime();

        this.data = new HashMap<>();
        this.data.put("newDate", newDate.toString());
        this.data.put("previousDate", previousDate != null ? previousDate.toString() : null);
        this.data.put("affectedPersonCount", affectedPersonIds.size());
        this.data.put("eventType", "TIME_CHANGE");
    }

    @Override
    public UUID getId() {
        return id;
    }

    @Override
    public EventType getType() {
        return EventType.LIFECYCLE;
    }

    @Override
    public Instant getTimestamp() {
        return timestamp;
    }

    @Override
    public UUID getSourceId() {
        return null; // System event
    }

    @Override
    public Map<String, Object> getData() {
        return new HashMap<>(data);
    }

    @Override
    public LocalDateTime getScheduledTime() {
        return scheduledTime;
    }

    @Override
    public String getTargetEntityId() {
        return "POPULATION"; // Affects entire population
    }

    @Override
    public Object getData(String key) {
        return data.get(key);
    }

    @Override
    public boolean isProcessed() {
        return processed;
    }

    @Override
    public void markProcessed() {
        this.processed = true;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void cancel() {
        this.cancelled = true;
    }

    public LocalDate getNewDate() {
        return newDate;
    }

    public LocalDate getPreviousDate() {
        return previousDate;
    }

    public List<Person> getAffectedPersonIds() {
        return affectedPersonIds;
    }
}
