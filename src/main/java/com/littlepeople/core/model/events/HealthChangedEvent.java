package com.littlepeople.core.model.events;

import com.littlepeople.core.interfaces.Event;
import com.littlepeople.core.model.EventType;
import com.littlepeople.core.model.HealthStatus;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Event representing a change in a person's health status.
 */
public class HealthChangedEvent implements Event {

    private final UUID id;
    private final UUID personId;
    private final HealthStatus newHealthStatus;
    private final HealthStatus previousHealthStatus;
    private final Instant timestamp;
    private final LocalDateTime scheduledTime;
    private final Map<String, Object> data;
    private boolean processed = false;
    private boolean cancelled = false;

    public HealthChangedEvent(UUID personId, HealthStatus newHealthStatus, HealthStatus previousHealthStatus) {
        this.id = UUID.randomUUID();
        this.personId = personId;
        this.newHealthStatus = newHealthStatus;
        this.previousHealthStatus = previousHealthStatus;
        this.timestamp = Instant.now();
        this.scheduledTime = LocalDateTime.now();

        this.data = new HashMap<>();
        this.data.put("personId", personId.toString());
        this.data.put("newHealthStatus", newHealthStatus.name());
        this.data.put("previousHealthStatus", previousHealthStatus != null ? previousHealthStatus.name() : null);
        this.data.put("eventType", "HEALTH_CHANGED");
    }

    @Override
    public UUID getId() {
        return id;
    }

    @Override
    public EventType getType() {
        return EventType.ENTITY;
    }

    @Override
    public Instant getTimestamp() {
        return timestamp;
    }

    @Override
    public UUID getSourceId() {
        return personId;
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
        return personId.toString();
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

    public UUID getPersonId() {
        return personId;
    }

    public HealthStatus getNewHealthStatus() {
        return newHealthStatus;
    }

    public HealthStatus getPreviousHealthStatus() {
        return previousHealthStatus;
    }
}
