package com.gmail.subnokoii78.tplcore.events;

import org.jspecify.annotations.NullMarked;

@NullMarked
public class TickEvent implements TPLEvent {
    private final boolean isFrozen;

    protected TickEvent(boolean isFrozen) {
        this.isFrozen = isFrozen;
    }

    public boolean isFrozen() {
        return isFrozen;
    }

    @Override
    public TPLEventType<? extends TPLEvent> getType() {
        return TPLEventTypes.TICK;
    }
}
