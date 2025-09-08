package com.gmail.subnokoii78.tplcore.events;

import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PlayerClickEvent extends CancellableEvent {
    private final Player player;

    private final Click click;

    private final Target target;

    @Nullable
    private final Block block;

    @Nullable
    private final Entity entity;

    protected PlayerClickEvent(@NotNull Player player, @NotNull Cancellable event, @NotNull Click click) {
        super(event);
        this.player = player;
        this.click = click;
        this.target = Target.NONE;
        this.block = null;
        this.entity = null;
    }

    protected PlayerClickEvent(@NotNull Player player, @NotNull Cancellable event, @NotNull Click click, @NotNull Block block) {
        super(event);
        this.player = player;
        this.click = click;
        this.target = Target.BLOCK;
        this.block = block;
        this.entity = null;
    }

    protected PlayerClickEvent(@NotNull Player player, @NotNull Cancellable event, @NotNull Click click, @NotNull Entity entity) {
        super(event);
        this.player = player;
        this.click = click;
        this.target = Target.ENTITY;
        this.block = null;
        this.entity = entity;
    }

    @Override
    public @NotNull EventType<PlayerClickEvent> getType() {
        return EventTypes.PLAYER_CLICK;
    }

    public @NotNull Player getPlayer() {
        return player;
    }

    public @NotNull Click getClick() {
        return click;
    }

    public @NotNull Target getTarget() {
        return target;
    }

    public @NotNull Block getBlock() {
        if (block == null) {
            throw new IllegalStateException("ブロックをクリックしていないため、ブロックを取得できませんでした");
        }
        else return block;
    }

    public @NotNull Entity getEntity() {
        if (entity == null) {
            throw new IllegalStateException("エンティティをクリックしていないため、エンティティを取得できませんでした");
        }
        else return entity;
    }

    public enum Click {
        LEFT,
        RIGHT
    }

    public enum Target {
        NONE,
        BLOCK,
        ENTITY
    }
}
