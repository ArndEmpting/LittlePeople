package com.littlepeople.core.model;

import com.littlepeople.core.interfaces.EventDrivenPersonManager;
import com.littlepeople.core.interfaces.EventScheduler;
import com.littlepeople.core.model.events.*;
import com.littlepeople.core.exceptions.SimulationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementation of EventDrivenPersonManager that schedules all person changes through events.
 * This ensures complete consistency in how person state changes are handled.
 */
public class DefaultEventDrivenPersonManager implements EventDrivenPersonManager {

    private static final Logger logger = LoggerFactory.getLogger(DefaultEventDrivenPersonManager.class);
    private final EventScheduler eventScheduler;

    public DefaultEventDrivenPersonManager(EventScheduler eventScheduler) {
        this.eventScheduler = eventScheduler;
    }

    @Override
    public void scheduleDeathEvent(PersonDeathEvent event) throws SimulationException {
        logger.debug("Scheduling death event for person: {}", event.getPersonId());
        eventScheduler.scheduleEvent(event);
    }

    @Override
    public void schedulePartnershipEvent(PartnershipFormedEvent event) throws SimulationException {
        logger.debug("Scheduling partnership formation event between: {} and {}",
                    event.getPerson1Id(), event.getPerson2Id());
        eventScheduler.scheduleEvent(event);
    }

    @Override
    public void schedulePartnershipDissolutionEvent(PartnershipDissolvedEvent event) throws SimulationException {
        logger.debug("Scheduling partnership dissolution event between: {} and {}",
                    event.getPerson1Id(), event.getPerson2Id());
        eventScheduler.scheduleEvent(event);
    }

    @Override
    public void scheduleChildAddedEvent(ChildAddedEvent event) throws SimulationException {
        logger.debug("Scheduling child addition event: parent {} {} child {}",
                    event.getParentId(),  event.getChildId());
        eventScheduler.scheduleEvent(event);
    }

    @Override
    public void scheduleHealthChangeEvent(HealthChangedEvent event) throws SimulationException {
        logger.debug("Scheduling health change event for person: {} to {}",
                    event.getPersonId(), event.getNewHealthStatus());
        eventScheduler.scheduleEvent(event);
    }

    @Override
    public void scheduleWealthChangeEvent(WealthChangedEvent event) throws SimulationException {
        logger.debug("Scheduling wealth change event for person: {} to {}",
                    event.getPersonId(), event.getNewWealthStatus());
        eventScheduler.scheduleEvent(event);
    }
}
