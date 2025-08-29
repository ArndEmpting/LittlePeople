package com.littlepeople.core.interfaces;

import com.littlepeople.core.model.events.*;
import com.littlepeople.core.exceptions.SimulationException;

/**
 * Interface for managing person state changes through events only.
 * This interface ensures all changes go through the event system for consistency,
 * auditability, and future extensibility.
 */
public interface EventDrivenPersonManager {

    /**
     * Schedules a death event for a person.
     *
     * @param event the death event to schedule
     * @throws SimulationException if the event cannot be scheduled
     */
    void scheduleDeathEvent(PersonDeathEvent event) throws SimulationException;

    /**
     * Schedules a partnership formation event.
     *
     * @param event the partnership formation event to schedule
     * @throws SimulationException if the event cannot be scheduled
     */
    void schedulePartnershipEvent(PartnershipFormedEvent event) throws SimulationException;

    /**
     * Schedules a partnership dissolution event.
     *
     * @param event the partnership dissolution event to schedule
     * @throws SimulationException if the event cannot be scheduled
     */
    void schedulePartnershipDissolutionEvent(PartnershipDissolvedEvent event) throws SimulationException;

    /**
     * Schedules a child addition event.
     *
     * @param event the child addition event to schedule
     * @throws SimulationException if the event cannot be scheduled
     */
    void scheduleChildAddedEvent(ChildAddedEvent event) throws SimulationException;

    /**
     * Schedules a health change event.
     *
     * @param event the health change event to schedule
     * @throws SimulationException if the event cannot be scheduled
     */
    void scheduleHealthChangeEvent(HealthChangedEvent event) throws SimulationException;

    /**
     * Schedules a wealth change event.
     *
     * @param event the wealth change event to schedule
     * @throws SimulationException if the event cannot be scheduled
     */
    void scheduleWealthChangeEvent(WealthChangedEvent event) throws SimulationException;
}
