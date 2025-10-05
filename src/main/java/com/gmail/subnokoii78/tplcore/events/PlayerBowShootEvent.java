package com.gmail.subnokoii78.tplcore.events;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class PlayerBowShootEvent extends CancellableEvent {
    private final Player player;

    private final Projectile projectile;

    private final ItemStack bow;

    private final ItemStack arrow;

    private final float force;

    private final int ticks;

    protected PlayerBowShootEvent(EntityShootBowEvent event, Player player, Projectile projectile, ItemStack bow, int ticks) {
        super(event);
        this.bow = bow;
        this.arrow = event.getConsumable() == null ? new ItemStack(Material.ARROW) : event.getConsumable();
        force = event.getForce();
        this.player = player;
        this.projectile = projectile;
        this.ticks = ticks;
    }

    public Player getPlayer() {
        return player;
    }

    public int getTicks() {
        return ticks;
    }

    public Projectile getProjectile() {
        return projectile;
    }

    public ItemStack getBow() {
        return bow;
    }

    public ItemStack getArrow() {
        return arrow;
    }

    public float getForce() {
        return force;
    }

    @Override
    public TPLEventType<PlayerBowShootEvent> getType() {
        return TPLEventTypes.PLAYER_BOW_SHOOT;
    }
}
