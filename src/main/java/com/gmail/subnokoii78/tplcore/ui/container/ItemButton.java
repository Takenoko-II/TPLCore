package com.gmail.subnokoii78.tplcore.ui.container;

import com.gmail.subnokoii78.tplcore.events.EventDispatcher;
import com.gmail.subnokoii78.tplcore.itemstack.ItemStackBuilder;
import com.gmail.subnokoii78.tplcore.itemstack.ItemStackCustomDataAccess;
import com.gmail.takenokoii78.mojangson.MojangsonPath;
import com.gmail.takenokoii78.mojangson.MojangsonValueTypes;
import com.gmail.takenokoii78.mojangson.values.MojangsonCompound;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;
import java.util.function.Function;

public class ItemButton {
    protected final ItemStackBuilder itemStackBuilder;

    @Nullable
    protected ItemButtonClickSound sound = null;

    private final EventDispatcher<ItemButtonClickEvent> itemButtonClickEventDispatcher = new EventDispatcher<>(ItemButtonClickEvent.ITEM_BUTTON_CLICK);

    protected ItemButton(@NotNull ItemStackBuilder itemStackBuilder) {
        this.itemStackBuilder = itemStackBuilder;
    }

    protected ItemButton(@NotNull Material material) {
        this(new ItemStackBuilder(material));
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
        else if (itemStackBuilder.build() instanceof Damageable damageable && damageable.hasMaxDamage()) {
            throw new IllegalArgumentException("最大ダメージが設定されていないときのみ個数を変更できます");
        }

        itemStackBuilder.maxStackSize(amount);
        itemStackBuilder.count(amount);

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
        if (itemStackBuilder.build().getMaxStackSize() != 1) {
            throw new IllegalStateException("耐久力の表示はアイテムの個数が1のときのみ利用できます");
        }

        itemStackBuilder.maxDamage(100);
        itemStackBuilder.damage((int) (rate * 100));
        return this;
    }

    public @NotNull ItemButton clickSound(@NotNull Sound sound, float volume, float pitch) {
        this.sound = new ItemButtonClickSound(sound, volume, pitch);
        return this;
    }

    public @NotNull ItemButton clickSound(@NotNull ItemButtonClickSound sound) {
        this.sound = sound;
        return this;
    }

    public @NotNull ItemButton onClick(Consumer<ItemButtonClickEvent> listener) {
        itemButtonClickEventDispatcher.add(listener);
        return this;
    }

    public @NotNull ItemButton copy() {
        return copy(this, ItemButton::new);
    }

    protected @NotNull ItemStack build() {
        return itemStackBuilder
            .customData(PATH, (byte) 1)
            .build();
    }

    public @NotNull ItemStack visualItemStack() {
        return itemStackBuilder.build();
    }

    protected void click(@NotNull ItemButtonClickEvent event) {
        if (sound != null) {
            sound.play(event.getPlayer());
        }
        itemButtonClickEventDispatcher.dispatch(event);
    }

    private static final MojangsonPath PATH = MojangsonPath.of("tpl_core.container_interaction_button");

    protected static <T extends ItemButton> @NotNull T copy(@NotNull T itemButton, @NotNull Function<ItemStackBuilder, T> constructor) {
        final T copy = constructor.apply(itemButton.itemStackBuilder);
        copy.sound = itemButton.sound;
        copy.onClick(((ItemButton) itemButton).itemButtonClickEventDispatcher::dispatch);
        return copy;
    }

    protected static boolean isButton(@NotNull ItemStack itemStack) {
        final MojangsonCompound compound = ItemStackCustomDataAccess.of(itemStack).read();
        if (!compound.has(PATH)) return false;
        else if (!compound.getTypeOf(PATH).equals(MojangsonValueTypes.BYTE)) return false;
        return compound.get(PATH, MojangsonValueTypes.BYTE).getAsBooleanValueOrNull() == Boolean.TRUE;
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
