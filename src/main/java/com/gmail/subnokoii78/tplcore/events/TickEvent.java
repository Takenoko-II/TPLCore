package com.gmail.subnokoii78.tplcore.events;

import org.jspecify.annotations.NullMarked;

@NullMarked
public class TickEvent implements TPLEvent {
    private final boolean isFrozen;

    private final boolean isTicking;

    protected TickEvent(boolean isFrozen, boolean isTicking) {
        this.isFrozen = isFrozen;
        this.isTicking = isTicking;
    }

    public boolean isFrozen() {
        return isFrozen;
    }

    public boolean isTicking() {
        return isTicking;
    }

    @Override
    public TPLEventType<? extends TPLEvent> getType() {
        return TPLEventTypes.TICK;
    }
}
