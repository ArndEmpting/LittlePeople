package com.littlepeople.core.model.events;

import com.littlepeople.core.interfaces.Event;
import com.littlepeople.core.model.EventType;
import com.littlepeople.core.util.SimulationTimeProvider;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Event representing the addition of a child to a parent-child relationship.
 */
public class BirthEvent implements Event {

    private final UUID id;
    private final UUID motherId;
    private final UUID fatherId;

    private final Instant timestamp;
    private final LocalDateTime scheduledTime;
    private final Map<String, Object> data;
    private boolean processed = false;
    private boolean cancelled = false;

    public BirthEvent(UUID fatherId, UUID motherId, LocalDate birthDate) {
        this.id = UUID.randomUUID();
        this.fatherId = fatherId;
        this.motherId = motherId;
        this.timestamp = Instant.now();
        this.scheduledTime = birthDate.atStartOfDay();

        this.data = new HashMap<>();
        this.data.put("fatherId", fatherId);
        this.data.put("motherId", motherId);
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
        return motherId;
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
        return motherId.toString();
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

    public UUID getMotherId() {
        return motherId;
    }

    public UUID getFatherId() {
        return fatherId;
    }

}
