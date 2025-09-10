package com.gmail.subnokoii78.tplcore.ui.container;

import io.papermc.paper.datacomponent.DataComponentTypes;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.function.Consumer;

public class PotionButton extends ItemButton {
    private PotionButton(@NotNull Material material) {
        super(material);
    }

    public PotionButton color(@NotNull Color color) {
        itemStackBuilder.potionColor(color);
        return this;
    }

    @Override
    public @NotNull PotionButton name(@NotNull TextComponent component) {
        return (PotionButton) super.name(component);
    }

    @Override
    public @NotNull PotionButton lore(@NotNull TextComponent component) {
        return (PotionButton) super.lore(component);
    }

    @Override
    public @NotNull PotionButton amount(int amount) {
        return (PotionButton) super.amount(amount);
    }

    @Override
    public @NotNull PotionButton glint(boolean flag) {
        return (PotionButton) super.glint(flag);
    }

    @Override
    public @NotNull PotionButton itemModel(@NotNull NamespacedKey id) {
        return (PotionButton) super.itemModel(id);
    }

    @Override
    public @NotNull PotionButton onClick(Consumer<ItemButtonClickEvent> listener) {
        return (PotionButton) super.onClick(listener);
    }

    @Override
    protected @NotNull ItemStack build() {
        itemStackBuilder.hideComponent(DataComponentTypes.POTION_CONTENTS);
        return super.build();
    }

    public static @NotNull PotionButton potion() {
        return new PotionButton(Material.POTION);
    }

    public static @NotNull PotionButton splashPotion() {
        return new PotionButton(Material.SPLASH_POTION);
    }

    public static @NotNull PotionButton lingeringPotion() {
        return new PotionButton(Material.LINGERING_POTION);
    }

    public static @NotNull PotionButton tippedArrow() {
        return new PotionButton(Material.TIPPED_ARROW);
    }
}
