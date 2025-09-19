package com.gmail.subnokoii78.tplcore.ui.container;

import com.gmail.subnokoii78.tplcore.itemstack.ItemStackBuilder;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public class PlayerHeadButton extends ItemButton {
    protected PlayerHeadButton(@NotNull ItemStackBuilder itemStackBuilder) {
        super(itemStackBuilder);
    }

    protected PlayerHeadButton() {
        super(Material.PLAYER_HEAD);
    }

    @Override
    public @NotNull PlayerHeadButton name(@NotNull TextComponent component) {
        return (PlayerHeadButton) super.name(component);
    }

    @Override
    public @NotNull PlayerHeadButton lore(@NotNull TextComponent component) {
        return (PlayerHeadButton) super.lore(component);
    }

    @Override
    public @NotNull PlayerHeadButton amount(int amount) {
        return (PlayerHeadButton) super.amount(amount);
    }

    @Override
    public @NotNull PlayerHeadButton glint(boolean flag) {
        return (PlayerHeadButton) super.glint(flag);
    }

    @Override
    public @NotNull PlayerHeadButton itemModel(@NotNull NamespacedKey id) {
        return (PlayerHeadButton) super.itemModel(id);
    }

    @Override
    public @NotNull PlayerHeadButton clickSound(@NotNull ItemButtonClickSound sound) {
        return (PlayerHeadButton) super.clickSound(sound);
    }

    @Override
    public @NotNull PlayerHeadButton clickSound(@NotNull Sound sound, float volume, float pitch) {
        return (PlayerHeadButton) super.clickSound(sound, volume, pitch);
    }

    @Override
    public @NotNull PlayerHeadButton onClick(Consumer<ItemButtonClickEvent> listener) {
        return (PlayerHeadButton) super.onClick(listener);
    }

    @Override
    public @NotNull PlayerHeadButton copy() {
        return copy(this, PlayerHeadButton::new);
    }

    public @NotNull PlayerHeadButton player(@NotNull String gamerTag) {
        itemStackBuilder.playerProfile(Bukkit.getOfflinePlayer(gamerTag).getPlayerProfile());
        return this;
    }
}
