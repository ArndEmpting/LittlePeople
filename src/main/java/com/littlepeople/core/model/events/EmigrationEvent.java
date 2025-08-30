package com.littlepeople.core.model.events;

import com.littlepeople.core.model.Person;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

/**
 * Event triggered when inhabitants leave the population through emigration.
 *
 * <p>This event is fired when the population management system processes
 * emigration and removes inhabitants from the population. It contains
 * information about the emigrants and the circumstances of their departure.
 *
 * @author LittlePeople Development Team
 * @version 1.0
 * @since 1.0
 */
public class EmigrationEvent {

    private final List<Person> emigrants;
    private final int emigrantCount;
    private final LocalDate departureDate;
    private final String emigrationReason;
    private final double emigrationRate;
    private final long eventTimestamp;

    /**
     * Creates a new EmigrationEvent.
     *
     * @param emigrants the list of emigrants leaving
     * @param departureDate the date when emigration occurred
     * @param emigrationReason the reason for emigration
     * @param emigrationRate the current emigration rate
     */
    public EmigrationEvent(List<Person> emigrants,
                          LocalDate departureDate,
                          String emigrationReason,
                          double emigrationRate) {
        this.emigrants = List.copyOf(Objects.requireNonNull(emigrants, "Emigrants cannot be null"));
        this.emigrantCount = emigrants.size();
        this.departureDate = Objects.requireNonNull(departureDate, "Departure date cannot be null");
        this.emigrationReason = Objects.requireNonNull(emigrationReason, "Emigration reason cannot be null");
        this.emigrationRate = emigrationRate;
        this.eventTimestamp = System.currentTimeMillis();
    }

    /**
     * Gets the list of emigrants leaving.
     *
     * @return unmodifiable list of emigrants
     */
    public List<Person> getEmigrants() {
        return emigrants;
    }

    /**
     * Gets the number of emigrants in this event.
     *
     * @return the emigrant count
     */
    public int getEmigrantCount() {
        return emigrantCount;
    }

    /**
     * Gets the date when emigration occurred.
     *
     * @return the departure date
     */
    public LocalDate getDepartureDate() {
        return departureDate;
    }

    /**
     * Gets the reason for emigration.
     *
     * @return the emigration reason
     */
    public String getEmigrationReason() {
        return emigrationReason;
    }

    /**
     * Gets the current emigration rate.
     *
     * @return the emigration rate
     */
    public double getEmigrationRate() {
        return emigrationRate;
    }

    /**
     * Gets the timestamp when the event was created.
     *
     * @return the event timestamp in milliseconds
     */
    public long getEventTimestamp() {
        return eventTimestamp;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;

        EmigrationEvent that = (EmigrationEvent) obj;
        return emigrantCount == that.emigrantCount &&
               Double.compare(that.emigrationRate, emigrationRate) == 0 &&
               Objects.equals(emigrants, that.emigrants) &&
               Objects.equals(departureDate, that.departureDate) &&
               Objects.equals(emigrationReason, that.emigrationReason);
    }

    @Override
    public int hashCode() {
        return Objects.hash(emigrants, emigrantCount, departureDate,
                           emigrationReason, emigrationRate);
    }

    @Override
    public String toString() {
        return "EmigrationEvent{" +
               "emigrantCount=" + emigrantCount +
               ", departureDate=" + departureDate +
               ", emigrationReason='" + emigrationReason + '\'' +
               ", emigrationRate=" + emigrationRate +
               ", eventTimestamp=" + eventTimestamp +
               '}';
    }
}
