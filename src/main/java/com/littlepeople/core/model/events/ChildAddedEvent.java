package com.littlepeople.core.model.events;

import com.littlepeople.core.interfaces.Event;
import com.littlepeople.core.model.EventType;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Event representing the addition of a child to a parent-child relationship.
 */
public class ChildAddedEvent implements Event {

    private final UUID id;
    private final UUID parentId;
    private final UUID childId;
    private final Instant timestamp;
    private final LocalDateTime scheduledTime;
    private final Map<String, Object> data;
    private boolean processed = false;
    private boolean cancelled = false;

    public ChildAddedEvent(UUID parentId, UUID childId) {
        this.id = UUID.randomUUID();
        this.parentId = parentId;
        this.childId = childId;
        this.timestamp = Instant.now();
        this.scheduledTime = LocalDateTime.now();

        this.data = new HashMap<>();
        this.data.put("parentId", parentId.toString());
        this.data.put("childId", childId.toString());
        this.data.put("eventType", "CHILD_ADDED");
    }

    @Override
    public UUID getId() {
        return id;
    }

    @Override
    public EventType getType() {
        return EventType.RELATIONSHIP;
    }

    @Override
    public Instant getTimestamp() {
        return timestamp;
    }

    @Override
    public UUID getSourceId() {
        return parentId;
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
        return childId.toString();
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

    public UUID getParentId() {
        return parentId;
    }

    public UUID getChildId() {
        return childId;
    }
}
