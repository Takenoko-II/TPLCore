package com.gmail.subnokoii78.tplcore.ui.container;

import com.gmail.subnokoii78.tplcore.itemstack.ItemStackBuilder;
import io.papermc.paper.datacomponent.DataComponentTypes;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.function.Consumer;

public class PotionButton extends ItemButton {
    protected PotionButton(@NotNull ItemStackBuilder itemStackBuilder) {
        super(itemStackBuilder);
    }

    protected PotionButton(@NotNull Material material) {
        super(material);
    }

    public @NotNull PotionButton color(@NotNull Color color) {
        itemStackBuilder.potionColor(color);
        return this;
    }

    @Override
    public @NotNull PotionButton clickSound(@NotNull Sound sound, float volume, float pitch) {
        return (PotionButton) super.clickSound(sound, volume, pitch);
    }

    @Override
    public @NotNull PotionButton clickSound(@NotNull ItemButtonClickSound sound) {
        return (PotionButton) super.clickSound(sound);
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
    public @NotNull PotionButton copy() {
        return copy(this, PotionButton::new);
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
}
