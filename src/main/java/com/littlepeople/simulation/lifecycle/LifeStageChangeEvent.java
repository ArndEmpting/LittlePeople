package com.littlepeople.simulation.lifecycle;

import com.littlepeople.core.interfaces.Event;
import com.littlepeople.core.model.EventType;
import com.littlepeople.core.model.LifeStage;
import com.littlepeople.core.model.Person;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Event generated when a person transitions from one life stage to another.
 *
 * @author LittlePeople Development Team
 * @version 1.0
 * @since 1.0
 */
public class LifeStageChangeEvent implements Event {

    private final UUID id;
    private final EventType type;
    private final Instant timestamp;
    private final UUID sourceId;
    private final Person person;
    private final LifeStage previousLifeStage;
    private final LifeStage newLifeStage;
    private final Map<String, Object> data;
    private boolean processed = false;
    private boolean cancelled = false;

    /**
     * Creates a new life stage change event.
     *
     * @param person the person who changed life stages
     * @param eventDate the date when the transition occurred
     * @param previousLifeStage the person's previous life stage
     * @param newLifeStage the person's new life stage
     */
    public LifeStageChangeEvent(
            Person person,
            LocalDate eventDate,
            LifeStage previousLifeStage,
            LifeStage newLifeStage) {
        this.id = UUID.randomUUID();
        this.type = EventType.LIFECYCLE;
        this.timestamp = eventDate.atStartOfDay().toInstant(ZoneOffset.UTC);
        this.sourceId = person.getId();
        this.person = person;
        this.previousLifeStage = previousLifeStage;
        this.newLifeStage = newLifeStage;

        this.data = new HashMap<>();
        data.put("previousLifeStage", previousLifeStage);
        data.put("newLifeStage", newLifeStage);
        data.put("personAge", person.getAge());
        data.put("person", person);
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
        return timestamp;
    }

    @Override
    public UUID getSourceId() {
        return sourceId;
    }

    @Override
    public Map<String, Object> getData() {
        return new HashMap<>(data);
    }

    @Override
    public LocalDateTime getScheduledTime() {
        return LocalDateTime.ofInstant(timestamp, ZoneOffset.UTC);
    }

    @Override
    public String getTargetEntityId() {
        return person.getId().toString();
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

    /**
     * Gets the person who changed life stages.
     *
     * @return the person
     */
    public Person getPerson() {
        return person;
    }

    /**
     * Gets the person's previous life stage.
     *
     * @return the previous life stage
     */
    public LifeStage getPreviousLifeStage() {
        return previousLifeStage;
    }

    /**
     * Gets the person's new life stage.
     *
     * @return the new life stage
     */
    public LifeStage getNewLifeStage() {
        return newLifeStage;
    }
}
