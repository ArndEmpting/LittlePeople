package com.littlepeople.core.model;

import com.littlepeople.core.interfaces.Event;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

/**
 * Default implementation of the Event interface.
 * This class represents a simulation event with all necessary properties.
 */
public class SimulationEvent implements Event {

    private  UUID id;
    private  EventType type;
    private  LocalDateTime scheduledTime;
    private  int priority;
    private  String targetEntityId;
    private  Map<String, Object> data;
    private boolean processed;
    private boolean cancelled;
    private UUID sourceId;
    private Instant timestamp;
    /**
     * Constructor for creating a new simulation event.
     *
     * @param type the event type
     * @param scheduledTime when the event should be processed
     * @param targetEntityId the target entity ID (can be null)
     * @param priority the event priority
     * @param data additional event data
     */
    public SimulationEvent(EventType type, LocalDateTime scheduledTime, String targetEntityId,
                          int priority, Map<String, Object> data, UUID sourceId) {
        if (type == null) {
            throw new IllegalArgumentException("Event type cannot be null");
        }
        if (scheduledTime == null) {
            throw new IllegalArgumentException("Scheduled time cannot be null");
        }

        this.id = UUID.randomUUID();
        this.type = type;
        this.scheduledTime = scheduledTime;
        this.targetEntityId = targetEntityId;
        this.priority = priority;
        this.data = data != null ? new HashMap<>(data) : new HashMap<>();
        this.processed = false;
        this.cancelled = false;
        this.sourceId = sourceId;

    }

    /**
     * Convenience constructor with default priority (0) and no data.
     */
    public SimulationEvent(EventType type, LocalDateTime scheduledTime, String targetEntityId) {
        this(type, scheduledTime, targetEntityId, 0, null, null);
    }

    /**
     * Convenience constructor for system events (no target entity).
     */
    public SimulationEvent(EventType type, LocalDateTime scheduledTime) {
        this(type, scheduledTime,  null);
    }

    @Override
    public UUID getId() {
        return id;
    }

    @Override
    public EventType getType() {
        return type;
    }

    @Override
    public Instant getTimestamp() {
        return null;
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
        return targetEntityId;
    }

    @Override
    public Map<String, Object> getData() {
        return new HashMap<>(data);
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

    public int getPriority() {
        return priority;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        SimulationEvent that = (SimulationEvent) obj;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return String.format("SimulationEvent{id='%s', type='%s', scheduledTime=%s, targetEntityId='%s', priority=%d, processed=%s, cancelled=%s}",
                id, type, scheduledTime, targetEntityId, priority, processed, cancelled);
    }
}
