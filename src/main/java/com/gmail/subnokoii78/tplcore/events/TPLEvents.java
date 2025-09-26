package com.gmail.subnokoii78.tplcore.events;

import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NullMarked;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

/**
 * イベントひとつにつきクラスひとつが必要なイベントAPI
 */
@NullMarked
public class TPLEvents {
    private final Map<TPLEventType<?>, EventDispatcher<?>> dispatchers = new HashMap<>();

    public <T extends TPLEvent> EventDispatcher<T> getDispatcher(@NotNull TPLEventType<T> type) {
        if (dispatchers.containsKey(type)) {
            return (EventDispatcher<T>) dispatchers.get(type);
        }
        else {
            final EventDispatcher<T> dispatcher = new EventDispatcher<>(type);
            dispatchers.put(type, dispatcher);
            return dispatcher;
        }
    }

    public <T extends TPLEvent> int register(TPLEventType<T> type, Consumer<T> handler) {
        return getDispatcher(type).add(handler);
    }

    public <T extends TPLEvent> boolean unregister(TPLEventType<T> type, int id) {
        return getDispatcher(type).remove(id);
    }
}
