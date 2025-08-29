package com.littlepeople.core.model.events;

import com.littlepeople.core.interfaces.Event;
import com.littlepeople.core.model.EventType;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Event representing the dissolution of a partnership between two people.
 */
public class PartnershipDissolvedEvent implements Event {

    private final UUID id;
    private final UUID person1Id;
    private final UUID person2Id;
    private final Instant timestamp;
    private final LocalDateTime scheduledTime;
    private final Map<String, Object> data;
    private boolean processed = false;
    private boolean cancelled = false;

    public PartnershipDissolvedEvent(UUID person1Id, UUID person2Id) {
        this.id = UUID.randomUUID();
        this.person1Id = person1Id;
        this.person2Id = person2Id;
        this.timestamp = Instant.now();
        this.scheduledTime = LocalDateTime.now();

        this.data = new HashMap<>();
        this.data.put("person1Id", person1Id.toString());
        this.data.put("person2Id", person2Id.toString());
        this.data.put("eventType", "PARTNERSHIP_DISSOLVED");
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
        return person1Id;
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
        return person1Id.toString();
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

    public UUID getPerson1Id() {
        return person1Id;
    }

    public UUID getPerson2Id() {
        return person2Id;
    }
}
