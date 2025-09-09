package com.gmail.subnokoii78.tplcore.ui.container;

import net.kyori.adventure.text.TextComponent;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Consumer;

public class LeatherArmorButton extends ArmorButton {
    public LeatherArmorButton(@NotNull Material material) {
        super(material);
    }

    @Override
    public @NotNull LeatherArmorButton name(@NotNull TextComponent component) {
        return (LeatherArmorButton) super.name(component);
    }

    @Override
    public @NotNull LeatherArmorButton lore(@NotNull TextComponent component) {
        return (LeatherArmorButton) super.lore(component);
    }

    @Override
    public @NotNull LeatherArmorButton amount(int amount) {
        return (LeatherArmorButton) super.amount(amount);
    }

    @Override
    public @NotNull LeatherArmorButton glint(boolean flag) {
        return (LeatherArmorButton) super.glint(flag);
    }

    @Override
    public @NotNull LeatherArmorButton itemModel(@NotNull NamespacedKey id) {
        return (LeatherArmorButton) super.itemModel(id);
    }

    @Override
    public @NotNull LeatherArmorButton onClick(Consumer<ItemButtonClickEvent> listener) {
        return (LeatherArmorButton) super.onClick(listener);
    }

    public @NotNull LeatherArmorButton color(@Nullable Color color) {
        itemStackBuilder.leatherArmorColor(color);
        return this;
    }

    @Override
    protected @NotNull ItemStack build() {
        itemStackBuilder.hideFlag(ItemFlag.HIDE_DYE);
        return super.build();
    }
}
