package com.littlepeople.core.model.events;

import com.littlepeople.core.interfaces.Event;
import com.littlepeople.core.model.EventType;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Event representing a marriage ceremony between two people.
 *
 * <p>This event is triggered when a partnership progresses to marriage status,
 * representing the formal ceremony and legal recognition of the relationship.
 * It contains information about both partners and the ceremony details.</p>
 *
 * @since 1.0.0
 * @version 1.0.0
 */
public class MarriageEvent implements Event {

    private final UUID id;
    private final UUID person1Id;
    private final UUID person2Id;
    private final LocalDateTime ceremonyDate;
    private final Instant timestamp;
    private final Map<String, Object> data;
    private boolean processed = false;
    private boolean cancelled = false;

    /**
     * Creates a new marriage event.
     *
     * @param person1Id the ID of the first partner
     * @param person2Id the ID of the second partner
     * @param ceremonyDate the date and time of the marriage ceremony
     */
    public MarriageEvent(UUID person1Id, UUID person2Id, LocalDateTime ceremonyDate) {
        this.id = UUID.randomUUID();
        this.person1Id = person1Id;
        this.person2Id = person2Id;
        this.ceremonyDate = ceremonyDate;
        this.timestamp = Instant.now();

        this.data = new HashMap<>();
        this.data.put("person1Id", person1Id.toString());
        this.data.put("person2Id", person2Id.toString());
        this.data.put("ceremonyDate", ceremonyDate.toString());
        this.data.put("eventType", "MARRIAGE");
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
    public LocalDateTime getScheduledTime() {
        return ceremonyDate;
    }

    @Override
    public Map<String, Object> getData() {
        return new HashMap<>(data);
    }

    @Override
    public boolean isProcessed() {
        return processed;
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
     * Gets the ID of the first partner.
     *
     * @return the first partner's ID
     */
    public UUID getPerson1Id() {
        return person1Id;
    }

    /**
     * Gets the ID of the second partner.
     *
     * @return the second partner's ID
     */
    public UUID getPerson2Id() {
        return person2Id;
    }

    /**
     * Gets the ceremony date and time.
     *
     * @return the ceremony date and time
     */
    public LocalDateTime getCeremonyDate() {
        return ceremonyDate;
    }

    @Override
    public UUID getSourceId() {
        // Return the first person as the source of the marriage proposal
        return person1Id;
    }

    @Override
    public String getTargetEntityId() {
        // Return the second person as the target of the marriage proposal
        return person2Id.toString();
    }

    @Override
    public Object getData(String key) {
        return data.get(key);
    }

    @Override
    public void markProcessed() {
        this.processed = true;
    }

    @Override
    public String toString() {
        return String.format("MarriageEvent{id=%s, person1=%s, person2=%s, ceremonyDate=%s}",
                           id, person1Id, person2Id, ceremonyDate);
    }
}
