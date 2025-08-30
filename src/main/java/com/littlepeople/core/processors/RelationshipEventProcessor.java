package com.littlepeople.core.processors;

import com.littlepeople.core.interfaces.Event;
import com.littlepeople.core.interfaces.EventProcessor;
import com.littlepeople.core.model.EventType;
import com.littlepeople.core.model.Person;
import com.littlepeople.core.model.PersonRegistry;
import com.littlepeople.core.model.events.PartnershipFormedEvent;
import com.littlepeople.core.model.events.PartnershipDissolvedEvent;
import com.littlepeople.core.model.events.ChildAddedEvent;
import com.littlepeople.core.exceptions.SimulationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Event processor for handling relationship events.
 * This processor manages partnerships and family relationships.
 */
public class RelationshipEventProcessor implements EventProcessor {

    private static final Logger logger = LoggerFactory.getLogger(RelationshipEventProcessor.class);


    public RelationshipEventProcessor() {

    }

    @Override
    public EventType getEventType() {
        return EventType.RELATIONSHIP;
    }

    @Override
    public void processEvent(Event event) throws SimulationException {
        if (event instanceof PartnershipFormedEvent) {
            processPartnershipFormed((PartnershipFormedEvent) event);
        } else if (event instanceof PartnershipDissolvedEvent) {
            processPartnershipDissolved((PartnershipDissolvedEvent) event);
        } else if (event instanceof ChildAddedEvent) {
            processChildAdded((ChildAddedEvent) event);
        } else {
            throw new SimulationException("Unsupported relationship event type: " + event.getClass().getSimpleName());
        }

        event.markProcessed();
    }

    private void processPartnershipFormed(PartnershipFormedEvent event) throws SimulationException {
        Person person1 = PersonRegistry.get(event.getPerson1Id().toString());
        Person person2 = PersonRegistry.get(event.getPerson2Id().toString());

        if (person1 == null || person2 == null) {
            throw new SimulationException("One or both persons not found for partnership");
        }

        // Validate partnership rules
        validatePartnership(person1, person2);

        // Set bidirectional partnership - direct state change
        person1.setPartner(person2);
        person2.setPartner(person1);

        logger.info("Processed partnership formation between {} {} and {} {}",
                   person1.getFirstName(), person1.getLastName(),
                   person2.getFirstName(), person2.getLastName());
    }

    private void processPartnershipDissolved(PartnershipDissolvedEvent event) throws SimulationException {
        Person person1 = PersonRegistry.get(event.getPerson1Id().toString());
        Person person2 = PersonRegistry.get(event.getPerson2Id().toString());

        if (person1 == null || person2 == null) {
            throw new SimulationException("One or both persons not found for partnership dissolution");
        }

        // Remove bidirectional partnership - direct state change
        person1.setPartner(null);
        person2.setPartner(null);

        logger.info("Processed partnership dissolution between {} {} and {} {}",
                   person1.getFirstName(), person1.getLastName(),
                   person2.getFirstName(), person2.getLastName());
    }

    private void processChildAdded(ChildAddedEvent event) throws SimulationException {
        Person parent = PersonRegistry.get(event.getParentId().toString());
        Person child = PersonRegistry.get(event.getChildId().toString());

        if (parent == null || child == null) {
            throw new SimulationException("Parent or child not found for relationship");
        }

        // Validate child relationship
        validateChildRelationship(parent, child);

        // Add bidirectional relationship - direct state change
        parent.addChild(child);


        // If parent has a partner, add child to partner as well
        if (parent.getPartner() != null && !parent.getPartner().getChildren().contains(child)) {
            parent.getPartner().addChild(child);
        }

        logger.info("Processed child addition: {} {} added to parent {} {}",
                   child.getFirstName(), child.getLastName(),
                   parent.getFirstName(), parent.getLastName());
    }

    private void validatePartnership(Person person1, Person person2) throws SimulationException {
        if (person1 == person2) {
            throw new SimulationException("Cannot partner with self");
        }
        if (!person1.isAlive() || !person2.isAlive()) {
            throw new SimulationException("Cannot form partnership with deceased person");
        }
        if (!person1.isAdult() || !person2.isAdult()) {
            throw new SimulationException("Minors cannot form partnerships");
        }
        if (person1.getPartner() != null || person2.getPartner() != null) {
            throw new SimulationException("One or both persons already have partners");
        }
        if (isDirectFamily(person1, person2)) {
            throw new SimulationException("Cannot partner with direct family member");
        }
    }

    private void validateChildRelationship(Person parent, Person child) throws SimulationException {
        if (parent == child) {
            throw new SimulationException("Person cannot be their own child");
        }
        if (parent.getChildren().contains(child)) {
            throw new SimulationException("Child relationship already exists");
        }
        if (child.getBirthDate().isBefore(parent.getBirthDate())) {
            throw new SimulationException("Child cannot be older than parent");
        }
    }

    private boolean isDirectFamily(Person person1, Person person2) {
        // Check if parent/child relationship
        if (person1.getParents().contains(person2) || person1.getChildren().contains(person2)) {
            return true;
        }

        // Check if siblings (share at least one parent)
        for (Person parent : person1.getParents()) {
            if (person2.getParents().contains(parent)) {
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean canProcess(EventType eventType) {
        return EventType.RELATIONSHIP.equals(eventType);
    }

    @Override
    public int getPriority() {
        return 90; // High priority for relationship events
    }
}
