package com.littlepeople.core.model.events;

import java.time.LocalDate;
import java.util.Objects;

/**
 * Event triggered when population crosses significant thresholds.
 *
 * <p>This event is fired when the population size crosses predefined thresholds
 * such as minimum/maximum population limits, growth rate milestones, or
 * demographic balance points. It helps monitor population health and triggers
 * appropriate responses.
 *
 * @author LittlePeople Development Team
 * @version 1.0
 * @since 1.0
 */
public class PopulationThresholdEvent {

    private final ThresholdType thresholdType;
    private final int currentPopulation;
    private final int thresholdValue;
    private final LocalDate eventDate;
    private final String description;
    private final ThresholdSeverity severity;
    private final long eventTimestamp;

    /**
     * Creates a new PopulationThresholdEvent.
     *
     * @param thresholdType the type of threshold crossed
     * @param currentPopulation the current population size
     * @param thresholdValue the threshold value that was crossed
     * @param eventDate the date when the threshold was crossed
     * @param description description of the threshold event
     * @param severity the severity of the threshold crossing
     */
    public PopulationThresholdEvent(ThresholdType thresholdType,
                                   int currentPopulation,
                                   int thresholdValue,
                                   LocalDate eventDate,
                                   String description,
                                   ThresholdSeverity severity) {
        this.thresholdType = Objects.requireNonNull(thresholdType, "Threshold type cannot be null");
        this.currentPopulation = currentPopulation;
        this.thresholdValue = thresholdValue;
        this.eventDate = Objects.requireNonNull(eventDate, "Event date cannot be null");
        this.description = Objects.requireNonNull(description, "Description cannot be null");
        this.severity = Objects.requireNonNull(severity, "Severity cannot be null");
        this.eventTimestamp = System.currentTimeMillis();
    }

    /**
     * Gets the type of threshold that was crossed.
     *
     * @return the threshold type
     */
    public ThresholdType getThresholdType() {
        return thresholdType;
    }

    /**
     * Gets the current population size.
     *
     * @return the current population
     */
    public int getCurrentPopulation() {
        return currentPopulation;
    }

    /**
     * Gets the threshold value that was crossed.
     *
     * @return the threshold value
     */
    public int getThresholdValue() {
        return thresholdValue;
    }

    /**
     * Gets the date when the threshold was crossed.
     *
     * @return the event date
     */
    public LocalDate getEventDate() {
        return eventDate;
    }

    /**
     * Gets the description of the threshold event.
     *
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Gets the severity of the threshold crossing.
     *
     * @return the severity
     */
    public ThresholdSeverity getSeverity() {
        return severity;
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

        PopulationThresholdEvent that = (PopulationThresholdEvent) obj;
        return currentPopulation == that.currentPopulation &&
               thresholdValue == that.thresholdValue &&
               thresholdType == that.thresholdType &&
               Objects.equals(eventDate, that.eventDate) &&
               Objects.equals(description, that.description) &&
               severity == that.severity;
    }

    @Override
    public int hashCode() {
        return Objects.hash(thresholdType, currentPopulation, thresholdValue,
                           eventDate, description, severity);
    }

    @Override
    public String toString() {
        return "PopulationThresholdEvent{" +
               "type=" + thresholdType +
               ", currentPopulation=" + currentPopulation +
               ", thresholdValue=" + thresholdValue +
               ", eventDate=" + eventDate +
               ", severity=" + severity +
               ", description='" + description + '\'' +
               '}';
    }

    /**
     * Enumeration of threshold types.
     */
    public enum ThresholdType {
        MINIMUM_POPULATION("Minimum Population"),
        MAXIMUM_POPULATION("Maximum Population"),
        GROWTH_RATE_HIGH("High Growth Rate"),
        GROWTH_RATE_LOW("Low Growth Rate"),
        DEMOGRAPHIC_IMBALANCE("Demographic Imbalance"),
        IMMIGRATION_SPIKE("Immigration Spike"),
        EMIGRATION_SPIKE("Emigration Spike");

        private final String displayName;

        ThresholdType(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    /**
     * Enumeration of threshold severity levels.
     */
    public enum ThresholdSeverity {
        INFO("Informational"),
        WARNING("Warning"),
        CRITICAL("Critical"),
        EMERGENCY("Emergency");

        private final String displayName;

        ThresholdSeverity(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }
}
