package com.littlepeople.core.model;

import com.littlepeople.core.interfaces.EventProcessor;
import com.littlepeople.core.interfaces.EventScheduler;
import com.littlepeople.core.processors.PersonDeathEventProcessor;
import com.littlepeople.core.processors.RelationshipEventProcessor;
import com.littlepeople.core.processors.EntityEventProcessor;
import com.littlepeople.core.exceptions.SimulationException;
import com.littlepeople.simulation.lifecycle.DefaultMortalityProcessor;
import com.littlepeople.simulation.lifecycle.HealthCalculationProcessor;
import com.littlepeople.simulation.lifecycle.LifecycleCoordinatorProcessor;
import com.littlepeople.simulation.partnerships.FamilyProcessor;
import com.littlepeople.simulation.partnerships.PartnershipProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Registry class that manages the registration of all event processors
 * in the simulation system. This ensures that all processors are properly
 * registered with the event scheduler for the fully event-driven architecture.
 */
public class EventProcessorRegistry {

    private static final Logger logger = LoggerFactory.getLogger(EventProcessorRegistry.class);

    private final EventScheduler eventScheduler;
    private final List<EventProcessor> registeredProcessors;

    /**
     * Creates a new event processor registry.
     *
     * @param eventScheduler the event scheduler to register processors with
     */
    public EventProcessorRegistry(EventScheduler eventScheduler) {
        this.eventScheduler = eventScheduler;
        this.registeredProcessors = new ArrayList<>();
    }

    /**
     * Registers all event processors with the event scheduler.
     * This method should be called during simulation initialization.
     *
     * @throws SimulationException if registration fails
     */
    public void registerAllProcessors() throws SimulationException {
        logger.info("Registering all event processors...");

        try {
            // Register lifecycle event processors

            // Register relationship event processors
            registerProcessor(new RelationshipEventProcessor());

            // Register entity event processors
            registerProcessor(new EntityEventProcessor());
            registerProcessor(new DefaultMortalityProcessor(eventScheduler));
            registerProcessor(new PersonDeathEventProcessor());
            registerProcessor(new LifecycleCoordinatorProcessor(eventScheduler));
            registerProcessor(new PartnershipProcessor());
            registerProcessor(new FamilyProcessor(eventScheduler));
            registerProcessor(new HealthCalculationProcessor(eventScheduler));
            logger.info("Successfully registered {} event processors", registeredProcessors.size());

        } catch (Exception e) {
            logger.error("Failed to register event processors", e);
            throw new SimulationException("Event processor registration failed: " + e.getMessage(), e);
        }
    }

    /**
     * Registers a single event processor.
     *
     * @param processor the processor to register
     */
    private void registerProcessor(EventProcessor processor) {
        eventScheduler.registerProcessor(processor);
        registeredProcessors.add(processor);

        logger.debug("Registered event processor: {} for event type: {}",
                    processor.getClass().getSimpleName(), processor.getEventType());
    }

    /**
     * Unregisters all event processors.
     * This method can be called during simulation shutdown.
     */
    public void unregisterAllProcessors() {
        logger.info("Unregistering all event processors...");

        for (EventProcessor processor : registeredProcessors) {
            try {
                eventScheduler.unregisterProcessor(processor.getEventType());
                logger.debug("Unregistered event processor: {} for event type: {}",
                            processor.getClass().getSimpleName(), processor.getEventType());
            } catch (Exception e) {
                logger.warn("Failed to unregister processor {}: {}",
                           processor.getClass().getSimpleName(), e.getMessage());
            }
        }

        registeredProcessors.clear();
        logger.info("All event processors unregistered");
    }

    /**
     * Gets the number of registered processors.
     *
     * @return the number of registered processors
     */
    public int getRegisteredProcessorCount() {
        return registeredProcessors.size();
    }

    /**
     * Gets a list of all registered processors.
     *
     * @return a copy of the registered processors list
     */
    public List<EventProcessor> getRegisteredProcessors() {
        return new ArrayList<>(registeredProcessors);
    }

    /**
     * Checks if processors are registered.
     *
     * @return true if any processors are registered
     */
    public boolean hasRegisteredProcessors() {
        return !registeredProcessors.isEmpty();
    }
}
