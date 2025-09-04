package com.gmail.subnokoii78.tplcore.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.jetbrains.annotations.NotNull;

public class PlayerClickEvent extends CancellableEvent {
    private final Player player;

    private final Click click;

    protected PlayerClickEvent(@NotNull Player player, @NotNull Cancellable event, @NotNull Click click) {
        super(event);
        this.player = player;
        this.click = click;
    }

    @Override
    public @NotNull EventType<PlayerClickEvent> getType() {
        return TPLEvents.PLAYER_CLICK;
    }

    public enum Click {
        LEFT,
        RIGHT
    }
}
