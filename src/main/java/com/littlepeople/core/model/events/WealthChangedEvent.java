package com.littlepeople.core.model.events;

import com.littlepeople.core.interfaces.Event;
import com.littlepeople.core.model.EventType;
import com.littlepeople.core.model.WealthStatus;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Event representing a change in a person's wealth status.
 */
public class WealthChangedEvent implements Event {

    private final UUID id;
    private final UUID personId;
    private final WealthStatus newWealthStatus;
    private final WealthStatus previousWealthStatus;
    private final Instant timestamp;
    private final LocalDateTime scheduledTime;
    private final Map<String, Object> data;
    private boolean processed = false;
    private boolean cancelled = false;

    public WealthChangedEvent(UUID personId, WealthStatus newWealthStatus, WealthStatus previousWealthStatus) {
        this.id = UUID.randomUUID();
        this.personId = personId;
        this.newWealthStatus = newWealthStatus;
        this.previousWealthStatus = previousWealthStatus;
        this.timestamp = Instant.now();
        this.scheduledTime = LocalDateTime.now();

        this.data = new HashMap<>();
        this.data.put("personId", personId.toString());
        this.data.put("newWealthStatus", newWealthStatus.name());
        this.data.put("previousWealthStatus", previousWealthStatus != null ? previousWealthStatus.name() : null);
        this.data.put("eventType", "WEALTH_CHANGED");
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

    public WealthStatus getNewWealthStatus() {
        return newWealthStatus;
    }

    public WealthStatus getPreviousWealthStatus() {
        return previousWealthStatus;
    }
}
