package com.gmail.subnokoii78.tplcore.ui.container;

import com.gmail.subnokoii78.tplcore.events.EventDispatcher;
import com.gmail.subnokoii78.tplcore.itemstack.ItemStackBuilder;
import net.kyori.adventure.text.TextComponent;
import net.minecraft.nbt.StringTag;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;
import java.util.function.Consumer;

public class ItemButton {
    protected final ItemStackBuilder itemStackBuilder;

    protected final UUID id = UUID.randomUUID();

    private final EventDispatcher<ItemButtonClickEvent> itemButtonClickEventDispatcher = new EventDispatcher<>(ItemButtonClickEvent.ITEM_BUTTON_CLICK);

    private int amount = 1;

    protected ItemButton(@NotNull Material material) {
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
        itemButtonClickEventDispatcher.add(listener);
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
        itemButtonClickEventDispatcher.dispatch(event);
    }

    public static @NotNull PotionButton potion() {
        return PotionButton.potion();
    }

    public static @NotNull PotionButton splashPotion() {
        return PotionButton.splashPotion();
    }

    public static @NotNull PotionButton lingeringPotion() {
        return PotionButton.lingeringPotion();
    }

    public static @NotNull PotionButton tippedArrow() {
        return PotionButton.tippedArrow();
    }

    public static @NotNull PlayerHeadButton playerHead() {
        return new PlayerHeadButton();
    }

    public static @NotNull LeatherArmorButton leatherArmor(@NotNull Material material) {
        return new LeatherArmorButton(material);
    }

    public static @NotNull ArmorButton armor(@NotNull Material material) {
        return new ArmorButton(material);
    }

    public static @NotNull ItemButton item(@NotNull Material material) {
        return new ItemButton(material);
    }
}
