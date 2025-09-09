package com.gmail.subnokoii78.tplcore.ui.container;

import com.destroystokyo.paper.MaterialSetTag;
import io.papermc.paper.datacomponent.item.attribute.AttributeModifierDisplay;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.inventory.EquipmentSlotGroup;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.trim.ArmorTrim;
import org.bukkit.inventory.meta.trim.TrimMaterial;
import org.bukkit.inventory.meta.trim.TrimPattern;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.function.Consumer;

public class ArmorButton extends ItemButton {
    public ArmorButton(@NotNull Material material) {
        super(material);

        if (!MaterialSetTag.ITEMS_TRIMMABLE_ARMOR.isTagged(material)) {
            throw new IllegalArgumentException();
        }
    }

    @Override
    public @NotNull ArmorButton name(@NotNull TextComponent component) {
        return (ArmorButton) super.name(component);
    }

    @Override
    public @NotNull ArmorButton lore(@NotNull TextComponent component) {
        return (ArmorButton) super.lore(component);
    }

    @Override
    public @NotNull ArmorButton amount(int amount) {
        return (ArmorButton) super.amount(amount);
    }

    @Override
    public @NotNull ArmorButton glint(boolean flag) {
        return (ArmorButton) super.glint(flag);
    }

    @Override
    public @NotNull ArmorButton itemModel(@NotNull NamespacedKey id) {
        return (ArmorButton) super.itemModel(id);
    }

    @Override
    public @NotNull ArmorButton onClick(Consumer<ItemButtonClickEvent> listener) {
        return (ArmorButton) super.onClick(listener);
    }

    public @NotNull ArmorButton trim(@NotNull TrimMaterial material, @NotNull TrimPattern pattern) {
        itemStackBuilder.trim(material, pattern);
        return this;
    }

    @Override
    protected @NotNull ItemStack build() {
        itemStackBuilder.hideFlag(ItemFlag.HIDE_ARMOR_TRIM);
        itemStackBuilder.attributeModifier(Attribute.ARMOR, NamespacedKey.minecraft("test"), 0, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlotGroup.ANY, AttributeModifierDisplay.hidden());
        itemStackBuilder.hideFlag(ItemFlag.HIDE_ATTRIBUTES);
        return super.build();
    }
}
