package com.gmail.subnokoii78.tplcore.events;

import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

@NullMarked
public class PlayerClickEvent extends CancellableEvent {
    private final Player player;

    @Nullable
    private final ItemStack itemStack;

    private final Click click;

    private final Target target;

    @Nullable
    private final Block block;

    @Nullable
    private final Entity entity;

    protected PlayerClickEvent(Player player, @Nullable ItemStack itemStack, Cancellable event, Click click) {
        super(event);
        this.player = player;
        this.itemStack = itemStack;
        this.click = click;
        this.target = Target.NONE;
        this.block = null;
        this.entity = null;
    }

    protected PlayerClickEvent(Player player, @Nullable ItemStack itemStack, Cancellable event, Click click, Block block) {
        super(event);
        this.player = player;
        this.itemStack = itemStack;
        this.click = click;
        this.target = Target.BLOCK;
        this.block = block;
        this.entity = null;
    }

    protected PlayerClickEvent(Player player, @Nullable ItemStack itemStack, Cancellable event, Click click, Entity entity) {
        super(event);
        this.player = player;
        this.itemStack = itemStack;
        this.click = click;
        this.target = Target.ENTITY;
        this.block = null;
        this.entity = entity;
    }

    @Override
    public TPLEventType<PlayerClickEvent> getType() {
        return TPLEventTypes.PLAYER_CLICK;
    }

    public Player getPlayer() {
        return player;
    }

    public Click getClick() {
        return click;
    }

    public Target getTarget() {
        return target;
    }

    public Block getBlock() {
        if (block == null) {
            throw new IllegalStateException("ブロックをクリックしていないため、ブロックを取得できませんでした");
        }
        else return block;
    }

    public Entity getEntity() {
        if (entity == null) {
            throw new IllegalStateException("エンティティをクリックしていないため、エンティティを取得できませんでした");
        }
        else return entity;
    }

    public boolean hasItem() {
        return itemStack != null;
    }

    public ItemStack getItem() {
        if (itemStack == null) {
            throw new IllegalStateException("アイテムを手に持っていないため、アイテムを取得できませんでした");
        }
        return itemStack;
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
