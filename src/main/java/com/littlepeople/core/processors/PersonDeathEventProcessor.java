package com.littlepeople.core.processors;

import com.littlepeople.core.interfaces.Event;
import com.littlepeople.core.interfaces.EventProcessor;
import com.littlepeople.core.model.EventType;
import com.littlepeople.core.model.Person;
import com.littlepeople.core.model.PersonRegistry;
import com.littlepeople.core.model.events.PersonDeathEvent;
import com.littlepeople.core.exceptions.SimulationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Event processor for handling person death events.
 * This processor manages the actual state changes when a person dies.
 */
public class PersonDeathEventProcessor implements EventProcessor {

    private static final Logger logger = LoggerFactory.getLogger(PersonDeathEventProcessor.class);

    public PersonDeathEventProcessor() {
    }

    @Override
    public EventType getEventType() {
        return EventType.LIFECYCLE;
    }

    @Override
    public void processEvent(Event event) throws SimulationException {
        if (!(event instanceof PersonDeathEvent)) {
            return;
//            throw new SimulationException("Expected PersonDeathEvent but got " + event.getClass().getSimpleName());
        }

        PersonDeathEvent deathEvent = (PersonDeathEvent) event;
        Person person = PersonRegistry.get(deathEvent.getPersonId().toString());

        if (person == null) {
            throw new SimulationException("Person not found: " + deathEvent.getPersonId());
        }

        if (!person.isAlive()) {
            logger.warn("Attempted to process death event for already deceased person: {}", person.getId());
            return;
        }

        // Perform the actual state change - this is the only place direct changes happen
        person.markDeceased(deathEvent.getDeathDate(),deathEvent.getDeathCause());

        // Dissolve partnership if exists
        if (person.getPartner() != null) {
            Person partner = person.getPartner();
            person.setPartner(null);
            partner.setPartner(null);
        }

        logger.info("Processed death event for person: {} {} (Age: {}, Cause: {})",
                   person.getFirstName(), person.getLastName(), person.getAge(),
                   deathEvent.getDeathCause().getDescription());

        event.markProcessed();
    }

    @Override
    public boolean canProcess(EventType eventType) {
        return EventType.LIFECYCLE.equals(eventType);
    }

    @Override
    public int getPriority() {
        return 100; // High priority for lifecycle events
    }
}
