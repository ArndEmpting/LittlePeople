package com.littlepeople.core.processors;

import com.littlepeople.core.interfaces.Event;
import com.littlepeople.core.interfaces.EventProcessor;
import com.littlepeople.core.model.EventType;
import com.littlepeople.core.model.Person;
import com.littlepeople.core.model.PersonRegistry;
import com.littlepeople.core.model.events.HealthChangedEvent;
import com.littlepeople.core.model.events.WealthChangedEvent;
import com.littlepeople.core.exceptions.SimulationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Event processor for handling entity property changes.
 * This processor manages health, wealth, and other personal attribute changes.
 */
public class EntityEventProcessor extends AbstractEventProcessor {

    private static final Logger logger = LoggerFactory.getLogger(EntityEventProcessor.class);
    public EntityEventProcessor() {
      super(HealthChangedEvent.class);
    }


    @Override
    public void processEvent(Event event) throws SimulationException {
        if(canProcess(event.getClass())) {
            processHealthChanged((HealthChangedEvent) event);
        } else if (event instanceof WealthChangedEvent) {
            processWealthChanged((WealthChangedEvent) event);
        } else {
            throw new SimulationException("Unsupported entity event type: " + event.getClass().getSimpleName());
        }

        event.markProcessed();
    }

    private void processHealthChanged(HealthChangedEvent event) throws SimulationException {
        Person person = PersonRegistry.get(event.getPersonId().toString());

        if (person == null) {
            throw new SimulationException("Person not found: " + event.getPersonId());
        }

        if (!person.isAlive()) {
            logger.warn("Attempted to change health status of deceased person: {}", person.getId());
            return;
        }

        // Perform the actual state change - direct field access
        person.setHealthStatus(event.getNewHealthStatus());

        logger.trace("Processed health change for person: {} {} from {} to {}",
                   person.getFirstName(), person.getLastName(),
                   event.getPreviousHealthStatus() != null ? event.getPreviousHealthStatus().name() : "unknown",
                   event.getNewHealthStatus().name());
    }

    private void processWealthChanged(WealthChangedEvent event) throws SimulationException {
        Person person = PersonRegistry.get(event.getPersonId().toString());

        if (person == null) {
            throw new SimulationException("Person not found: " + event.getPersonId());
        }

        if (!person.isAlive()) {
            logger.warn("Attempted to change wealth status of deceased person: {}", person.getId());
            return;
        }

        // Perform the actual state change - direct field access
        person.setWealthStatus(event.getNewWealthStatus());

        logger.info("Processed wealth change for person: {} {} from {} to {}",
                   person.getFirstName(), person.getLastName(),
                   event.getPreviousWealthStatus() != null ? event.getPreviousWealthStatus().name() : "unknown",
                   event.getNewWealthStatus().name());
    }

    @Override
    public int getPriority() {
        return 80; // Medium priority for entity changes
    }
}
