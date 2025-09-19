package com.gmail.subnokoii78.tplcore.ui.container;

import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public record ItemButtonClickSound(@NotNull Sound sound, float volume, float pitch) {
    public void play(@NotNull Player player) {
        player.playSound(player.getLocation(), sound, volume, pitch);
    }

    public static final @NotNull ItemButtonClickSound BASIC = new ItemButtonClickSound(Sound.BLOCK_STONE_BUTTON_CLICK_ON, 10.0f, 2.0f);
}
