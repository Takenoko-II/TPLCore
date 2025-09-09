package com.gmail.subnokoii78.tplcore.events;

import org.jetbrains.annotations.NotNull;

public interface TPLEvent {
    @NotNull TPLEventType<? extends TPLEvent> getType();
}
