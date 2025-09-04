package com.gmail.subnokoii78.tplcore.events;

import org.jetbrains.annotations.NotNull;

public interface Event {
    @NotNull EventType<? extends Event> getType();
}
