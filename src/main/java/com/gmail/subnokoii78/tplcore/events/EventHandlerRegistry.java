package com.gmail.subnokoii78.tplcore.events;

import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

public class EventHandlerRegistry<T extends Event> {
    private static final Map<EventType<?>, EventHandlerRegistry<?>> registries = new HashMap<>();

    private final EventType<T> type;

    private final Set<Consumer<T>> handlers = new HashSet<>();

    private EventHandlerRegistry(@NotNull EventType<T> eventType) {
        this.type = eventType;
        registries.put(eventType, this);
    }

    public @NotNull EventType<T> getType() {
        return type;
    }

    public void call(T event) {
        handlers.forEach(handler -> handler.accept(event));
    }

    public static <T extends Event> EventHandlerRegistry<T> getRegistry(@NotNull EventType<T> type) {
        if (registries.containsKey(type)) {
            return (EventHandlerRegistry<T>) registries.get(type);
        }
        else {
            return new EventHandlerRegistry<>(type);
        }
    }
}
