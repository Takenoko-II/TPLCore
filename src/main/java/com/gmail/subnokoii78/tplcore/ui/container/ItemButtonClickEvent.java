package com.gmail.subnokoii78.tplcore.ui.container;

import com.gmail.subnokoii78.tplcore.events.TPLEventType;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class ItemButtonClickEvent implements ContainerInteractionEvent {
    private final Player player;

    private final ContainerInteraction interaction;

    private final int slot;

    private final ItemButton button;

    private boolean played = false;

    protected ItemButtonClickEvent(@NotNull Player player, @NotNull ContainerInteraction interaction, int slot, @NotNull ItemButton button) {
        this.player = player;
        this.interaction = interaction;
        this.slot = slot;
        this.button = button;
    }

    @Override
    public @NotNull Player getPlayer() {
        return player;
    }

    @Override
    public @NotNull ContainerInteraction getInteraction() {
        return interaction;
    }

    public int getSlot() {
        return slot;
    }

    public @NotNull ItemButton getClickedButton() {
        return button;
    }

    public void playClickingSound() {
        if (played) return;
        played = true;
        player.playSound(player.getLocation(), Sound.BLOCK_STONE_BUTTON_CLICK_ON, 10.0f, 2.0f);
    }

    public void close() {
        player.closeInventory();
    }

    @Override
    public @NotNull TPLEventType<ItemButtonClickEvent> getType() {
        return ITEM_BUTTON_CLICK;
    }
}
