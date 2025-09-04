package com.gmail.subnokoii78.tplcore.events;

import org.bukkit.event.Cancellable;
import org.jetbrains.annotations.NotNull;

public abstract class CancellableEvent implements Event {
    private final Cancellable event;

    protected CancellableEvent(@NotNull Cancellable event) {
        this.event = event;
    }

    public void cancel() {
        event.setCancelled(true);
    }

    @Override
    public abstract @NotNull EventType<? extends CancellableEvent> getType();
}
