package com.gmail.subnokoii78.tplcore.events;

import org.jetbrains.annotations.NotNull;

public class EventType<T extends Event> {
    private final Class<T> clazz;

    protected EventType(@NotNull Class<T> clazz) {
        this.clazz = clazz;
    }

    public @NotNull Class<T> getEventClass() {
        return clazz;
    }
}
