package com.gmail.subnokoii78.tplcore.events;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class TPLEventType<T extends TPLEvent> {
    private final Class<T> clazz;

    public TPLEventType(@NotNull Class<T> clazz) {
        this.clazz = clazz;
    }

    public @NotNull Class<T> getEventClass() {
        return clazz;
    }

    @Override
    public int hashCode() {
        return Objects.hash(clazz);
    }

    @Override
    public boolean equals(Object object) {
        if (object == null) return false;
        else if (object == this) return true;
        else if (object.getClass() != getClass()) return false;
        else return ((TPLEventType<?>) object).clazz.equals(this.clazz);
    }
}
