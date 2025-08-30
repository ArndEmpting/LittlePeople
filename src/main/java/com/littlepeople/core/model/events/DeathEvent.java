package com.littlepeople.core.model.events;

import com.littlepeople.core.interfaces.Event;
import com.littlepeople.core.model.DeathCause;
import com.littlepeople.core.model.EventType;
import com.littlepeople.core.model.Person;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Event generated when a person dies.
 *
 * @author LittlePeople Development Team
 * @version 1.0
 * @since 1.0
 */
public class DeathEvent implements Event {

    private final UUID id;
    private final EventType type;
    private final Instant timestamp;
    private final UUID sourceId;
    private final Person person;
    private final DeathCause cause;
    private final int ageAtDeath;
    private final Map<String, Object> data;
    private boolean processed = false;
    private boolean cancelled = false;

    /**
     * Creates a new death event.
     *
     * @param person the person who died
     * @param eventDate the date of death
     * @param cause the cause of death
     */
    public DeathEvent(Person person, LocalDate eventDate, DeathCause cause) {
        this.id = UUID.randomUUID();
        this.type = EventType.LIFECYCLE;
        this.timestamp = eventDate.atStartOfDay().toInstant(ZoneOffset.UTC);
        this.sourceId = person.getId();
        this.person = person;
        this.cause = cause;
        this.ageAtDeath = person.getAge();

        this.data = new HashMap<>();
        data.put("cause", cause);
        data.put("ageAtDeath", ageAtDeath);
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
     * Gets the person who died.
     *
     * @return the deceased person
     */
    public Person getDeceased() {
        return person;
    }

    /**
     * Gets the cause of death.
     *
     * @return the death cause
     */
    public DeathCause getCause() {
        return cause;
    }

    /**
     * Gets the person's age at death.
     *
     * @return the age in years
     */
    public int getAgeAtDeath() {
        return ageAtDeath;
    }
}
