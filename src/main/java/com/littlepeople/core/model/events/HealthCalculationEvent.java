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
 * Event that triggers health calculation for a population.
 * This event contains the population and current date for health processing.
 */
public class HealthCalculationEvent implements Event {

    private final UUID id;
    private final List<Person> population;
    private final LocalDate currentDate;
    private final Instant timestamp;
    private final LocalDateTime scheduledTime;
    private final Map<String, Object> data;
    private boolean processed = false;
    private boolean cancelled = false;

    public HealthCalculationEvent(List<Person> population, LocalDate currentDate) {
        this.id = UUID.randomUUID();
        this.population = population;
        this.currentDate = currentDate;
        this.timestamp = Instant.now();
        this.scheduledTime = SimulationTimeProvider.getCurrentSimulationTime();

        this.data = new HashMap<>();
        this.data.put("populationSize", population.size());
        this.data.put("currentDate", currentDate.toString());
        this.data.put("eventType", "HEALTH_CALCULATION");
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
        return null;
    }

    @Override
    public LocalDateTime getScheduledTime() {
        return scheduledTime;
    }

    @Override
    public String getTargetEntityId() {
        return "";
    }

    @Override
    public Object getData(String key) {
        return null;
    }

    @Override
    public Map<String, Object> getData() {
        return new HashMap<>(data);
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

    public List<Person> getPopulation() {
        return population;
    }

    public LocalDate getCurrentDate() {
        return currentDate;
    }

    @Override
    public String toString() {
        return String.format("HealthCalculationEvent{id=%s, populationSize=%d, currentDate=%s}",
                id, population.size(), currentDate);
    }
}
