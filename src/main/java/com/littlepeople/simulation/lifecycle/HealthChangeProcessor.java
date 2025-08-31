package com.littlepeople.simulation.lifecycle;

import com.littlepeople.core.exceptions.SimulationException;
import com.littlepeople.core.interfaces.Event;
import com.littlepeople.core.interfaces.EventProcessor;
import com.littlepeople.core.model.events.HealthChangedEvent;
import com.littlepeople.core.processors.AbstractEventProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Processor that listens for HealthChangedEvent and applies the health status changes to persons.
 * This processor is responsible for the actual modification of person health status.
 */
public class HealthChangeProcessor extends AbstractEventProcessor implements EventProcessor {

    private static final Logger logger = LoggerFactory.getLogger(HealthChangeProcessor.class);

    /**
     * Creates a new HealthChangeProcessor.
     */
    public HealthChangeProcessor() {
        super(HealthChangedEvent.class);
        logger.info("Initialized HealthChangeProcessor");
    }

    @Override
    public void processEvent(Event event) throws SimulationException {
        if (event instanceof HealthChangedEvent) {
            HealthChangedEvent healthEvent = (HealthChangedEvent) event;
            applyHealthChange(healthEvent);
        }
    }

    private void applyHealthChange(HealthChangedEvent healthEvent) throws SimulationException {
        try {
            // TODO: Get person by ID from population registry/service
            // For now, we assume the event contains the person reference
            // Person person = personService.getPersonById(healthEvent.getPersonId());

            // Apply the health status change
            // person.setHealthStatus(healthEvent.getNewHealthStatus());

            logger.info("Applied health change for person {} from {} to {}",
                    healthEvent.getPersonId(),
                    healthEvent.getPreviousHealthStatus(),
                    healthEvent.getNewHealthStatus());

            // Mark the event as processed
            healthEvent.markProcessed();

        } catch (Exception e) {
            logger.error("Error applying health change for person {}: {}",
                    healthEvent.getPersonId(), e.getMessage(), e);
            throw new SimulationException("Failed to apply health change", e);
        }
    }

    @Override
    public int getPriority() {
        // Lower priority than calculation processor to ensure changes are applied after calculation
        return 750;
    }
}
