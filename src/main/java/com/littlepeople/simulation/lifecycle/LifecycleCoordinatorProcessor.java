package com.littlepeople.simulation.lifecycle;

import com.littlepeople.core.exceptions.SimulationException;
import com.littlepeople.core.interfaces.Event;
import com.littlepeople.core.interfaces.EventProcessor;
import com.littlepeople.core.interfaces.EventScheduler;
import com.littlepeople.core.model.Person;
import com.littlepeople.core.model.events.*;
import com.littlepeople.core.processors.AbstractEventProcessor;
import com.littlepeople.simulation.population.PopulationManagerImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.util.List;

/**
 * Central coordinator processor that listens for TimeChangeEvent and creates all lifecycle calculation events.
 * This processor acts as a central hub that coordinates all lifecycle processing by dispatching
 * specific calculation events to their respective processors.
 */
public class LifecycleCoordinatorProcessor extends AbstractEventProcessor implements EventProcessor {

    private static final Logger logger = LoggerFactory.getLogger(LifecycleCoordinatorProcessor.class);

    private final EventScheduler eventScheduler;

    /**
     * Creates a new LifecycleCoordinatorProcessor.
     *
     * @param eventScheduler the event scheduler for dispatching calculation events
     */
    public LifecycleCoordinatorProcessor(EventScheduler eventScheduler) {
        super(TimeChangeEvent.class);
        this.eventScheduler = eventScheduler;
        logger.info("Initialized LifecycleCoordinatorProcessor");
    }

    @Override
    public void processEvent(Event event) throws SimulationException {
        if (event instanceof TimeChangeEvent) {
            TimeChangeEvent timeEvent = (TimeChangeEvent) event;
            coordinateLifecycleProcessing(timeEvent);
        }
    }

    private void coordinateLifecycleProcessing(TimeChangeEvent timeEvent) throws SimulationException {
        LocalDate currentDate = timeEvent.getNewDate();
        LocalDate previousDate = timeEvent.getPreviousDate();
        List<Person> population = timeEvent.getAffectedPersonIds();
        logger.info("TimeEvent: " + timeEvent.getData("newDate") + " " + PopulationManagerImpl.getInstance().getStatistics());
        if (population.isEmpty()) {
            logger.info("Population is empty");
            return;
        }
        logger.debug("Coordinating lifecycle processing for {} persons from {} to {}",
                population.size(), previousDate, currentDate);

        try {
            // Create and schedule Health Calculation Event
            HealthCalculationEvent healthEvent = new HealthCalculationEvent(population, currentDate);
            eventScheduler.scheduleEvent(healthEvent);
            logger.debug("Scheduled HealthCalculationEvent for {} persons", population.size());

            // Create and schedule Mortality Calculation Event
            MortalityCalculationEvent mortalityEvent = new MortalityCalculationEvent(
                    population, currentDate, previousDate);
            eventScheduler.scheduleEvent(mortalityEvent);
            logger.debug("Scheduled MortalityCalculationEvent for {} persons", population.size());

            // TODO: Add other lifecycle calculation events as needed (aging, partnerships, etc.)

                PartnershipCalculationEvent partnershipEvent = new PartnershipCalculationEvent(currentDate);
                eventScheduler.scheduleEvent(partnershipEvent);

                FamilyCalculationEvent familyEvent = new FamilyCalculationEvent(currentDate);
                eventScheduler.scheduleEvent(familyEvent);
            logger.info("Coordinated lifecycle processing: dispatched {} calculation events for {} persons",
                    4, population.size()); // Update count when adding more events

        } catch (Exception e) {
            logger.error("Error coordinating lifecycle processing for date {}: {}", currentDate, e.getMessage(), e);
            throw new SimulationException("Failed to coordinate lifecycle processing", e);
        }
    }

    @Override
    public int getPriority() {
        // High priority to ensure coordination happens before individual processors
        return 600;
    }
}
