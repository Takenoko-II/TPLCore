package com.gmail.subnokoii78.tplcore.events;

import org.jetbrains.annotations.NotNull;

public class TPLEventType<T extends TPLEvent> {
    private final Class<T> clazz;

    public TPLEventType(@NotNull Class<T> clazz) {
        this.clazz = clazz;
    }

    public @NotNull Class<T> getEventClass() {
        return clazz;
    }
}
