package com.gmail.subnokoii78.tplcore.ui.container;

import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class ItemButtonClickEvent {
    private final Player player;

    private final ContainerInteraction interaction;

    private final int slot;

    private final ItemButton button;

    public ItemButtonClickEvent(@NotNull Player player, @NotNull ContainerInteraction interaction, int slot, @NotNull ItemButton button) {
        this.player = player;
        this.interaction = interaction;
        this.slot = slot;
        this.button = button;
    }

    public @NotNull Player getPlayer() {
        return player;
    }

    public @NotNull ContainerInteraction getInteraction() {
        if (!interaction.isValid()) {
            throw new IllegalStateException();
        }
        return interaction;
    }

    public int getSlot() {
        return slot;
    }

    public @NotNull ItemButton getClickedButton() {
        return button;
    }

    public void playClickingSound() {
        player.playSound(player.getLocation(), Sound.BLOCK_STONE_BUTTON_CLICK_ON, 10.0f, 2.0f);
    }

    public void close() {
        player.closeInventory();
    }
}
