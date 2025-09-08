package com.gmail.subnokoii78.tplcore.events;

public final class EventTypes {
    private EventTypes() {}

    public static final EventType<PlayerClickEvent> PLAYER_CLICK = new EventType<>(PlayerClickEvent.class);

    public static final EventType<DatapackMessageReceiveEvent> DATAPACK_MESSAGE_RECEIVE = new EventType<>(DatapackMessageReceiveEvent.class);
}
