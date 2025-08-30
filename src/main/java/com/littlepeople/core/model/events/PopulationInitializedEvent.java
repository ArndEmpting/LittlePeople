package com.littlepeople.core.model.events;

import com.littlepeople.core.model.Person;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

/**
 * Event triggered when the initial population is created.
 *
 * <p>This event is fired when the population management system completes
 * the initial population generation process. It contains information about
 * the generated inhabitants and the configuration used.
 *
 * @author LittlePeople Development Team
 * @version 1.0
 * @since 1.0
 */
public class PopulationInitializedEvent {

    private final List<Person> inhabitants;
    private final int populationSize;
    private final LocalDate initializationDate;
    private final String configurationSummary;
    private final long eventTimestamp;

    /**
     * Creates a new PopulationInitializedEvent.
     *
     * @param inhabitants the list of generated inhabitants
     * @param initializationDate the date when initialization occurred
     * @param configurationSummary summary of the configuration used
     */
    public PopulationInitializedEvent(List<Person> inhabitants,
                                    LocalDate initializationDate,
                                    String configurationSummary) {
        this.inhabitants = List.copyOf(Objects.requireNonNull(inhabitants, "Inhabitants cannot be null"));
        this.populationSize = inhabitants.size();
        this.initializationDate = Objects.requireNonNull(initializationDate, "Initialization date cannot be null");
        this.configurationSummary = Objects.requireNonNull(configurationSummary, "Configuration summary cannot be null");
        this.eventTimestamp = System.currentTimeMillis();
    }

    /**
     * Gets the list of generated inhabitants.
     *
     * @return unmodifiable list of inhabitants
     */
    public List<Person> getInhabitants() {
        return inhabitants;
    }

    /**
     * Gets the size of the generated population.
     *
     * @return the population size
     */
    public int getPopulationSize() {
        return populationSize;
    }

    /**
     * Gets the date when initialization occurred.
     *
     * @return the initialization date
     */
    public LocalDate getInitializationDate() {
        return initializationDate;
    }

    /**
     * Gets a summary of the configuration used for initialization.
     *
     * @return the configuration summary
     */
    public String getConfigurationSummary() {
        return configurationSummary;
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

        PopulationInitializedEvent that = (PopulationInitializedEvent) obj;
        return populationSize == that.populationSize &&
               Objects.equals(inhabitants, that.inhabitants) &&
               Objects.equals(initializationDate, that.initializationDate) &&
               Objects.equals(configurationSummary, that.configurationSummary);
    }

    @Override
    public int hashCode() {
        return Objects.hash(inhabitants, populationSize, initializationDate, configurationSummary);
    }

    @Override
    public String toString() {
        return "PopulationInitializedEvent{" +
               "populationSize=" + populationSize +
               ", initializationDate=" + initializationDate +
               ", configurationSummary='" + configurationSummary + '\'' +
               ", eventTimestamp=" + eventTimestamp +
               '}';
    }
}
