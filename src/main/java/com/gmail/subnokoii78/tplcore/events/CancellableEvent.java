package com.gmail.subnokoii78.tplcore.events;

import org.bukkit.event.Cancellable;
import org.jspecify.annotations.NullMarked;

@NullMarked
public abstract class CancellableEvent implements TPLEvent {
    protected final Cancellable event;

    protected CancellableEvent(Cancellable event) {
        this.event = event;
    }

    public void cancel() {
        event.setCancelled(true);
    }

    @Override
    public abstract TPLEventType<? extends CancellableEvent> getType();
}
