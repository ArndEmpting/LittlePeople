package com.littlepeople.core.processors;

import com.littlepeople.core.interfaces.Event;
import com.littlepeople.core.interfaces.EventProcessor;

public abstract class AbstractEventProcessor implements EventProcessor {
    private final Class<? extends Event> eventType;

    protected AbstractEventProcessor(Class<? extends Event> eventType) {
        this.eventType = eventType;
    }

    @Override
    public Class<? extends Event> getEventType() {
        return eventType;
    }

    @Override
    public boolean canProcess(Class<? extends Event> eventType) {
        return this.eventType.equals(eventType);
    }
}