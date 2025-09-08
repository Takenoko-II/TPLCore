package com.gmail.subnokoii78.tplcore.itemstack;

import com.gmail.subnokoii78.tplcore.generic.UnImplementedException;
import com.gmail.subnokoii78.tplcore.network.GameProfileServerConnector;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.papermc.paper.datacomponent.DataComponentType;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.*;
import io.papermc.paper.datacomponent.item.attribute.AttributeModifierDisplay;
import io.papermc.paper.registry.tag.TagKey;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.minecraft.commands.arguments.NbtPathArgument;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.component.CustomData;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.damage.DamageType;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlotGroup;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.*;
import org.bukkit.inventory.meta.Repairable;
import org.bukkit.inventory.meta.trim.ArmorTrim;
import org.bukkit.inventory.meta.trim.TrimMaterial;
import org.bukkit.inventory.meta.trim.TrimPattern;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

public class ItemStackBuilder {
    private final ItemStack itemStack;

    public ItemStackBuilder(@NotNull Material material) {
        itemStack = new ItemStack(material);
    }

    public ItemStackBuilder() {
        this(Material.AIR);
    }

    private <T extends ItemMeta> @NotNull ItemStackBuilder editMeta(@NotNull Class<T> clazz, @NotNull Consumer<T> consumer) {
        final ItemMeta meta = itemStack.getItemMeta();

        if (clazz.isInstance(meta)) {
            consumer.accept(clazz.cast(meta));
            itemStack.setItemMeta(meta);
        }

        return this;
    }

    private @NotNull ItemStackBuilder editMeta(@NotNull Consumer<ItemMeta> consumer) {
        final ItemMeta meta = itemStack.getItemMeta();

        consumer.accept(meta);
        itemStack.setItemMeta(meta);

        return this;
    }

    @ApiStatus.Experimental
    private <T> @NotNull ItemStackBuilder editComponent(@NotNull DataComponentType.Valued<T> type, @NotNull Consumer<T> consumer) {
        final T data = itemStack.getData(type);

        consumer.accept(data);

        itemStack.setData(type, data);

        return this;
    }

    public @NotNull ItemStackBuilder copyWithType(@NotNull Material material) {
        return ItemStackBuilder.from(itemStack.withType(material));
    }

    public @NotNull ItemStackBuilder count(int count) {
        itemStack.setAmount(count);
        return this;
    }

    public @NotNull ItemStackBuilder maxStackSize(int size) {
        return editMeta(meta -> {
            meta.setMaxStackSize(size);
        });
    }

    public @NotNull ItemStackBuilder itemName(@NotNull TextComponent name) {
        editMeta(meta -> {
            meta.itemName(name);
        });
        return this;
    }

    public @NotNull ItemStackBuilder customName(@NotNull TextComponent name) {
        editMeta(meta -> {
            meta.customName(name);
        });
        return this;
    }

    public ItemStackBuilder lore(@NotNull TextComponent line) {
        return editMeta(meta -> {
            final List<Component> lore = meta.lore();

            if (lore == null) {
                meta.lore(List.of(line));
            }
            else {
                lore.add(line);
                meta.lore(lore);
            }
        });
    }

    public @NotNull ItemStackBuilder enchantment(@NotNull Enchantment enchantment, int level) {
        return editMeta(meta -> {
            meta.addEnchant(enchantment, level, true);
        });
    }

    public @NotNull ItemStackBuilder hideFlag(@NotNull ItemFlag flag) {
        return editMeta(meta -> {
            meta.addItemFlags(flag);
        });
    }

    public @NotNull ItemStackBuilder hideTooltip(boolean flag) {
        return editMeta(meta -> {
            meta.setHideTooltip(flag);
        });
    }

    public @NotNull ItemStackBuilder maxDamage(int damage) {
        return editMeta(Damageable.class, meta -> {
            meta.setMaxDamage(damage);
        });
    }

    public @NotNull ItemStackBuilder damage(int damage) {
        return editMeta(Damageable.class, meta -> {
            meta.setDamage(damage);
        });
    }

    public @NotNull ItemStackBuilder unbreakable(boolean flag) {
        return editMeta(meta -> {
            meta.setUnbreakable(flag);
        });
    }

    public @NotNull ItemStackBuilder repairCost(int cost) {
        return editMeta(Repairable.class, meta -> {
            meta.setRepairCost(cost);
        });
    }

    @ApiStatus.Experimental
    public @NotNull ItemStackBuilder repairableItem(@NotNull Material material) throws UnImplementedException {
        throw new UnImplementedException("paperがわるい");
        /*return editComponent(DataComponentTypes.REPAIRABLE, component -> {
            component.types();
        });*/
    }

    public @NotNull ItemStackBuilder playerProfile(@NotNull Player player) {
        return editMeta(SkullMeta.class, meta -> {
            meta.setPlayerProfile(player.getPlayerProfile());
        });
    }

    public @NotNull ItemStackBuilder attributeModifier(@NotNull Attribute attribute, @NotNull NamespacedKey id, double amount, @NotNull AttributeModifier.Operation operation, @NotNull EquipmentSlotGroup slotGroup, @NotNull AttributeModifierDisplay display) {
        final AttributeModifier attributeModifier = new AttributeModifier(
            id,
            amount,
            operation,
            slotGroup
        );

        return editMeta(meta -> {
            meta.addAttributeModifier(attribute, attributeModifier);
        });
    }

    @Deprecated
    public ItemStackBuilder customModelData(int value) {
        return editMeta(meta -> {
            meta.setCustomModelData(value);
        });
    }

    public @NotNull ItemStackBuilder potionEffect(@NotNull PotionEffect effect) {
        return editMeta(DataComponentTypes.POTION_CONTENTS, component -> {

        });
    }

    public ItemStackBuilder potionEffect(PotionEffectType effectType) {
        edit(builder -> {
            if (!(builder instanceof PotionMeta)) {
                return builder;
            }

            ((PotionMeta) builder).addCustomEffect(new PotionEffect(effectType, 20 * 30, 0), false);
            return builder;
        });

        return this;
    }

    public ItemStackBuilder potionEffect(PotionEffectType effectType, int duration) {
        edit(builder -> {
            if (!(builder instanceof PotionMeta)) {
                return builder;
            }

            ((PotionMeta) builder).addCustomEffect(new PotionEffect(effectType, duration, 0), false);
            return builder;
        });

        return this;
    }

    public ItemStackBuilder potionEffect(PotionEffectType effectType, int duration, int amplifier) {
        edit(builder -> {
            if (!(builder instanceof PotionMeta)) {
                return builder;
            }

            ((PotionMeta) builder).addCustomEffect(new PotionEffect(effectType, duration, amplifier), false);
            return builder;
        });

        return this;
    }

    public ItemStackBuilder potionEffect(PotionEffectType effectType, int duration, int amplifier, boolean ambient) {
        edit(builder -> {
            if (!(builder instanceof PotionMeta)) {
                return builder;
            }

            ((PotionMeta) builder).addCustomEffect(new PotionEffect(effectType, duration, amplifier, ambient), false);
            return builder;
        });

        return this;
    }

    public ItemStackBuilder potionEffect(PotionEffectType effectType, int duration, int amplifier, boolean ambient, boolean showParticles) {
        edit(builder -> {
            if (!(builder instanceof PotionMeta)) {
                return builder;
            }

            ((PotionMeta) builder).addCustomEffect(new PotionEffect(effectType, duration, amplifier, ambient, showParticles), false);
            return builder;
        });

        return this;
    }

    public ItemStackBuilder potionColor(Color color) {
        edit(builder -> {
            if (!(builder instanceof PotionMeta)) {
                return builder;
            }

            ((PotionMeta) builder).setColor(color);
            return builder;
        });

        return this;
    }

    public ItemStackBuilder chargedProjectile(ItemStack itemStack) {
        edit(builder -> {
            if (!(builder instanceof CrossbowMeta)) {
                return builder;
            }

            ((CrossbowMeta) builder).addChargedProjectile(itemStack);
            return builder;
        });

        return this;
    }

    public ItemStackBuilder leatherArmorColor(Color color) {
        edit(builder -> {
            if (!(builder instanceof LeatherArmorMeta)) {
                return builder;
            }

            ((LeatherArmorMeta) builder).setColor(color);
            return builder;
        });

        return this;
    }

    public ItemStackBuilder lodeStoneTracked(boolean flag) {
        edit(builder -> {
            if (!(builder instanceof CompassMeta)) {
                return builder;
            }

            ((CompassMeta) builder).setLodestoneTracked(true);
            return builder;
        });

        return this;
    }

    public ItemStackBuilder lodeStoneLocation(Location location) {
        edit(builder -> {
            if (!(builder instanceof CompassMeta)) {
                return builder;
            }

            ((CompassMeta) builder).setLodestoneTracked(true);
            ((CompassMeta) builder).setLodestone(location);
            return builder;
        });

        return this;
    }

    public ItemStackBuilder armorTrim(TrimMaterial material, TrimPattern pattern) {
        edit(builder -> {
            if (!(builder instanceof ColorableArmorMeta)) {
                return builder;
            }

            ((ColorableArmorMeta) builder).setTrim(new ArmorTrim(material, pattern));
            return builder;
        });

        return this;
    }

    public ItemStackBuilder bookAuthor(String name) {
        edit(builder -> {
            if (!(builder instanceof BookMeta)) {
                return builder;
            }

            ((BookMeta) builder).setAuthor(name);
            return builder;
        });

        return this;
    }

    public ItemStackBuilder bookTitle(String name) {
        edit(builder -> {
            if (!(builder instanceof BookMeta)) {
                return builder;
            }

            ((BookMeta) builder).setTitle(name);
            return builder;
        });

        return this;
    }

    public ItemStackBuilder bookGeneration(BookMeta.Generation generation) {
        edit(builder -> {
            if (!(builder instanceof BookMeta)) {
                return builder;
            }

            ((BookMeta) builder).setGeneration(generation);
            return builder;
        });

        return this;
    }

    public ItemStackBuilder bookPage(Component component) {
        edit(builder -> {
            if (!(builder instanceof BookMeta)) {
                return builder;
            }

            ((BookMeta) builder).addPages(component);
            return builder;
        });

        return this;
    }

    public ItemStackBuilder fireworkPower(int power) {
        edit(builder -> {
            if (!(builder instanceof FireworkMeta)) {
                return builder;
            }

            ((FireworkMeta) builder).setPower(power);
            return builder;
        });

        return this;
    }

    public ItemStackBuilder fireworkEffect(FireworkEffect effect) {
        edit(builder -> {
            if (!(builder instanceof FireworkMeta)) {
                return builder;
            }

            ((FireworkMeta) builder).addEffect(effect);
            return builder;
        });

        return this;
    }

    public ItemStackBuilder storedEnchantment(Enchantment enchantment, int level) {
        edit(builder -> {
            if (!(builder instanceof EnchantmentStorageMeta)) {
                return builder;
            }

            ((EnchantmentStorageMeta) builder).addStoredEnchant(enchantment, level, false);
            return builder;
        });

        return this;
    }

    public ItemStackBuilder glint(boolean flag) {
        edit(meta -> {
            meta.setEnchantmentGlintOverride(flag);

            return meta;
        });

        return this;
    }

    public ItemStackBuilder damageResistant(@NotNull TagKey<DamageType> damageTypeTagKey) {
        return editMeta(DataComponentTypes.DAMAGE_RESISTANT, component -> {
            return DamageResistant.damageResistant(damageTypeTagKey);
        });
    }

    public @NotNull ItemStackBuilder dataContainer(@NotNull String path, @NotNull Tag nbtTag) {
        final net.minecraft.world.item.ItemStack itemStackNMS = ((CraftItemStack) itemStack).handle;
        final CustomData customData = itemStackNMS.get(DataComponents.CUSTOM_DATA);

        final CompoundTag compound;
        if (customData == null) compound = new CompoundTag();
        else compound = customData.copyTag();

        final NbtPathArgument.NbtPath nbtPath;
        try {
            nbtPath = NbtPathArgument.nbtPath().parse(new StringReader(path));
        }
        catch (CommandSyntaxException exception) {
            throw new IllegalArgumentException("NBTパスの解析に失敗しました", exception);
        }

        try {
            nbtPath.set(compound, nbtTag);
        }
        catch (CommandSyntaxException exception) {
            throw new IllegalArgumentException("NBTパスに値をセットできませんでした", exception);
        }

        itemStackNMS.set(DataComponents.CUSTOM_DATA, CustomData.of(compound));

        return this;
    }

    public ItemStack build() {
        return itemStack.clone();
    }

    public static ItemStackBuilder from(ItemStack itemStack) {
        final ItemStackBuilder itemStackBuilder = new ItemStackBuilder(itemStack.getType());

        itemStackBuilder.edit(meta -> itemStack.getItemMeta());

        return itemStackBuilder;
    }
}
