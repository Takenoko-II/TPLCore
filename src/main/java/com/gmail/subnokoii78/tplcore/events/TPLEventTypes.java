package com.gmail.subnokoii78.tplcore.events;

public final class TPLEventTypes {
    private TPLEventTypes() {}

    public static final TPLEventType<PlayerClickEvent> PLAYER_CLICK = new TPLEventType<>(PlayerClickEvent.class);

    public static final TPLEventType<DatapackMessageReceiveEvent> DATAPACK_MESSAGE_RECEIVE = new TPLEventType<>(DatapackMessageReceiveEvent.class);

    public static final TPLEventType<TickEvent> TICK = new TPLEventType<>(TickEvent.class);

    public static final TPLEventType<PluginConfigUpdateEvent> PLUGIN_CONFIG_UPDATE = new TPLEventType<>(PluginConfigUpdateEvent.class);

    public static final TPLEventType<PlayerBowShootEvent> PLAYER_BOW_SHOOT = new TPLEventType<>(PlayerBowShootEvent.class);

    public static final TPLEventType<PlayerUsingItemEvent> PLAYER_USING_ITEM = new TPLEventType<>(PlayerUsingItemEvent.class);
}
