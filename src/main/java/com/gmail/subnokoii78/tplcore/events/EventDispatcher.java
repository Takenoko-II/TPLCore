package com.gmail.subnokoii78.tplcore.events;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class EventDispatcher<T extends TPLEvent> {
    private static final Map<TPLEventType<?>, EventDispatcher<?>> dispatchers = new HashMap<>();

    private final TPLEventType<T> type;

    private final Map<Integer, Consumer<T>> handlers = new HashMap<>();

    private int maxId = Integer.MIN_VALUE;

    private EventDispatcher(@NotNull TPLEventType<T> eventType) {
        this.type = eventType;
        dispatchers.put(eventType, this);
    }

    public @NotNull TPLEventType<T> getType() {
        return type;
    }

    public int add(@NotNull Consumer<T> handler) {
        final int id = maxId++;
        handlers.put(id, handler);
        return id;
    }

    public boolean remove(int id) {
        if (handlers.containsKey(id)) {
            handlers.remove(id);
            return true;
        }
        else return false;
    }

    public void dispatch(T event) {
        handlers.forEach((id, handler) -> handler.accept(event));
    }

    public static <T extends TPLEvent> EventDispatcher<T> getDispatcher(@NotNull TPLEventType<T> type) {
        if (dispatchers.containsKey(type)) {
            return (EventDispatcher<T>) dispatchers.get(type);
        }
        else {
            return new EventDispatcher<>(type);
        }
    }
}
