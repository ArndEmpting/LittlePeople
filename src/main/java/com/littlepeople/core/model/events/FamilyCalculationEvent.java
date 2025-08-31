package com.littlepeople.core.model.events;

import com.littlepeople.core.interfaces.Event;
import com.littlepeople.core.model.EventType;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Event zur Auslösung von Familienberechnungen im Simulationszyklus.
 */
public class FamilyCalculationEvent implements Event {

    private final UUID id;
    private final LocalDateTime scheduledTime;
    private final Map<String, Object> data;
    private boolean processed = false;
    private boolean cancelled = false;
   private final Instant timestamp;

    public FamilyCalculationEvent(LocalDate calculationDate) {
        this.id = UUID.randomUUID();
        this.timestamp = Instant.now();
        this.scheduledTime = LocalDateTime.now();
        this.data = new HashMap<>();
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


}