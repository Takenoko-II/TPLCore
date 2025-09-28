package com.gmail.subnokoii78.tplcore.events;

import org.jspecify.annotations.NullMarked;

@NullMarked
public interface TPLEvent {
    TPLEventType<? extends TPLEvent> getType();
}
