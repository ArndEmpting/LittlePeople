package com.littlepeople.core.model.events;

import com.littlepeople.core.model.Person;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

/**
 * Event triggered when new inhabitants arrive through immigration.
 *
 * <p>This event is fired when the population management system processes
 * immigration and adds new inhabitants to the population. It contains
 * information about the immigrants and the circumstances of their arrival.
 *
 * @author LittlePeople Development Team
 * @version 1.0
 * @since 1.0
 */
public class ImmigrationEvent {

    private final List<Person> immigrants;
    private final int immigrantCount;
    private final LocalDate arrivalDate;
    private final String immigrationReason;
    private final double immigrationRate;
    private final long eventTimestamp;

    /**
     * Creates a new ImmigrationEvent.
     *
     * @param immigrants the list of new immigrants
     * @param arrivalDate the date when immigration occurred
     * @param immigrationReason the reason for immigration
     * @param immigrationRate the current immigration rate
     */
    public ImmigrationEvent(List<Person> immigrants,
                           LocalDate arrivalDate,
                           String immigrationReason,
                           double immigrationRate) {
        this.immigrants = List.copyOf(Objects.requireNonNull(immigrants, "Immigrants cannot be null"));
        this.immigrantCount = immigrants.size();
        this.arrivalDate = Objects.requireNonNull(arrivalDate, "Arrival date cannot be null");
        this.immigrationReason = Objects.requireNonNull(immigrationReason, "Immigration reason cannot be null");
        this.immigrationRate = immigrationRate;
        this.eventTimestamp = System.currentTimeMillis();
    }

    /**
     * Gets the list of new immigrants.
     *
     * @return unmodifiable list of immigrants
     */
    public List<Person> getImmigrants() {
        return immigrants;
    }

    /**
     * Gets the number of immigrants in this event.
     *
     * @return the immigrant count
     */
    public int getImmigrantCount() {
        return immigrantCount;
    }

    /**
     * Gets the date when immigration occurred.
     *
     * @return the arrival date
     */
    public LocalDate getArrivalDate() {
        return arrivalDate;
    }

    /**
     * Gets the reason for immigration.
     *
     * @return the immigration reason
     */
    public String getImmigrationReason() {
        return immigrationReason;
    }

    /**
     * Gets the current immigration rate.
     *
     * @return the immigration rate
     */
    public double getImmigrationRate() {
        return immigrationRate;
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

        ImmigrationEvent that = (ImmigrationEvent) obj;
        return immigrantCount == that.immigrantCount &&
               Double.compare(that.immigrationRate, immigrationRate) == 0 &&
               Objects.equals(immigrants, that.immigrants) &&
               Objects.equals(arrivalDate, that.arrivalDate) &&
               Objects.equals(immigrationReason, that.immigrationReason);
    }

    @Override
    public int hashCode() {
        return Objects.hash(immigrants, immigrantCount, arrivalDate,
                           immigrationReason, immigrationRate);
    }

    @Override
    public String toString() {
        return "ImmigrationEvent{" +
               "immigrantCount=" + immigrantCount +
               ", arrivalDate=" + arrivalDate +
               ", immigrationReason='" + immigrationReason + '\'' +
               ", immigrationRate=" + immigrationRate +
               ", eventTimestamp=" + eventTimestamp +
               '}';
    }
}
