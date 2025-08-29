package com.littlepeople.core.model.events;

import com.littlepeople.core.interfaces.Event;
import com.littlepeople.core.model.EventType;
import com.littlepeople.core.model.DeathCause;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Event representing the death of a person in the simulation.
 * This event contains all necessary data to process a person's death
 * including the death date and cause.
 */
public class PersonDeathEvent implements Event {

    private final UUID id;
    private final UUID personId;
    private final LocalDate deathDate;
    private final DeathCause deathCause;
    private final Instant timestamp;
    private final LocalDateTime scheduledTime;
    private final Map<String, Object> data;
    private boolean processed = false;
    private boolean cancelled = false;

    public PersonDeathEvent(UUID personId, LocalDate deathDate, DeathCause deathCause) {
        this.id = UUID.randomUUID();
        this.personId = personId;
        this.deathDate = deathDate;
        this.deathCause = deathCause != null ? deathCause : DeathCause.UNEXPLAINED;
        this.timestamp = Instant.now();
        this.scheduledTime = LocalDateTime.now();

        this.data = new HashMap<>();
        this.data.put("personId", personId.toString());
        this.data.put("deathDate", deathDate.toString());
        this.data.put("deathCause", this.deathCause.name());
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

    // Getters for specific data
    public UUID getPersonId() {
        return personId;
    }

    public LocalDate getDeathDate() {
        return deathDate;
    }

    public DeathCause getDeathCause() {
        return deathCause;
    }
}
