package com.gmail.subnokoii78.tplcore.ui.container;

import com.gmail.subnokoii78.tplcore.itemstack.ItemStackBuilder;
import io.papermc.paper.datacomponent.DataComponentTypes;
import net.kyori.adventure.text.TextComponent;
import net.minecraft.nbt.StringTag;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.function.Consumer;

public class ItemButton {
    protected final ItemStackBuilder itemStackBuilder;

    protected final UUID id = UUID.randomUUID();

    private final Set<Consumer<ItemButtonClickEvent>> listeners = new HashSet<>();

    private int amount = 1;

    public ItemButton(@NotNull Material material) {
        itemStackBuilder = new ItemStackBuilder(material);
    }

    public @NotNull ItemButton name(@NotNull TextComponent component) {
        itemStackBuilder.itemName(component);
        return this;
    }

    public @NotNull ItemButton lore(@NotNull TextComponent component) {
        itemStackBuilder.lore(component);
        return this;
    }

    public @NotNull ItemButton amount(int amount) {
        if (amount < 1 || amount > 99) {
            throw new IllegalArgumentException("個数としては範囲外の値です");
        }

        if (amount > 1) {
            itemStackBuilder.resetMaxDamage();
        }

        this.amount = amount;
        return this;
    }

    public @NotNull ItemButton glint(boolean flag) {
        itemStackBuilder.glint(flag);
        return this;
    }

    public @NotNull ItemButton itemModel(@NotNull NamespacedKey id) {
        itemStackBuilder.itemModel(id);
        return this;
    }

    public @NotNull ItemButton damage(float rate) throws IllegalStateException {
        if (amount > 1) {
            throw new IllegalStateException("耐久力の表示はアイテムの個数が1のときのみ利用できます");
        }
        itemStackBuilder.maxDamage(100);
        itemStackBuilder.damage((int) (rate * 100));
        return this;
    }

    public @NotNull ItemButton onClick(Consumer<ItemButtonClickEvent> listener) {
        listeners.add(listener);
        return this;
    }

    protected @NotNull ItemStack build() {
        itemStackBuilder.maxStackSize(amount);

        return itemStackBuilder
            .count(amount)
            .customData("tpl_core.container_button.id", StringTag.valueOf(id.toString()))
            .build();
    }

    protected void click(@NotNull ItemButtonClickEvent event) {
        listeners.forEach(listener -> listener.accept(event));
    }
}
