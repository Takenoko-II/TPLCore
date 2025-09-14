package com.gmail.subnokoii78.tplcore.itemstack;

import com.destroystokyo.paper.profile.PlayerProfile;
import com.gmail.subnokoii78.tplcore.generic.UnImplementedException;
import com.gmail.takenokoii78.mojangson.MojangsonPath;
import com.gmail.takenokoii78.mojangson.values.MojangsonCompound;
import io.papermc.paper.datacomponent.DataComponentType;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.attribute.AttributeModifierDisplay;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.damage.DamageType;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.EquipmentSlotGroup;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.*;
import org.bukkit.inventory.meta.Repairable;
import org.bukkit.inventory.meta.components.CustomModelDataComponent;
import org.bukkit.inventory.meta.trim.ArmorTrim;
import org.bukkit.inventory.meta.trim.TrimMaterial;
import org.bukkit.inventory.meta.trim.TrimPattern;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionType;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

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

    public @NotNull ItemStackBuilder resetMaxDamage() {
        return editMeta(Damageable.class, meta -> {
            meta.setMaxDamage(null);
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

    public @NotNull ItemStackBuilder playerProfile(@NotNull PlayerProfile profile) {
        return editMeta(SkullMeta.class, meta -> {
            meta.setPlayerProfile(profile);
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

    public @NotNull ItemStackBuilder removeAttributeModifier(@NotNull Attribute attribute) {
        return editMeta(meta -> {
            meta.removeAttributeModifier(attribute);
        });
    }

    public @NotNull ItemStackBuilder potionEffect(@NotNull PotionEffect effect) {
        return editMeta(PotionMeta.class, meta -> {
            meta.addCustomEffect(effect, true);
        });
    }

    public @NotNull ItemStackBuilder potionEffectById(@NotNull PotionType potionType) {
        return editMeta(PotionMeta.class, meta -> {
            meta.setBasePotionType(potionType);
        });
    }

    public @NotNull ItemStackBuilder potionColor(@NotNull Color color) {
        return editMeta(PotionMeta.class, meta -> {
            meta.setColor(color);
        });
    }

    public @NotNull ItemStackBuilder chargedProjectile(@NotNull ItemStack itemStack) {
        return editMeta(CrossbowMeta.class, meta -> {
            meta.addChargedProjectile(itemStack);
        });
    }

    public @NotNull ItemStackBuilder leatherArmorColor(Color color) {
        return editMeta(LeatherArmorMeta.class, meta -> {
            meta.setColor(color);
        });
    }

    public @NotNull ItemStackBuilder removeLeatherArmorColor() {
        return editMeta(LeatherArmorMeta.class, meta -> {
            meta.setColor(null);
        });
    }

    public @NotNull ItemStackBuilder trim(@NotNull TrimMaterial material, @NotNull TrimPattern pattern) {
        return editMeta(ColorableArmorMeta.class, meta -> {
            meta.setTrim(new ArmorTrim(material, pattern));
        });
    }

    public @NotNull ItemStackBuilder bookHeader(@NotNull String author, @NotNull String title, @NotNull BookMeta.Generation generation) {
       return editMeta(BookMeta.class, meta -> {
           meta.setAuthor(author);
           meta.setTitle(title);
           meta.setGeneration(generation);
       });
    }

    public @NotNull ItemStackBuilder bookPage(@NotNull Component component) {
        return editMeta(BookMeta.class, meta -> {
            meta.addPages(component);
        });
    }

    public @NotNull ItemStackBuilder fireworkPower(int power) {
        return editMeta(FireworkMeta.class, meta -> {
            meta.setPower(power);
        });
    }

    public @NotNull ItemStackBuilder fireworkEffect(@NotNull FireworkEffect effect) {
        return editMeta(FireworkEffectMeta.class, meta -> {
            meta.setEffect(effect);
        });
    }

    public @NotNull ItemStackBuilder storedEnchantment(@NotNull Enchantment enchantment, int level) {
        return editMeta(EnchantmentStorageMeta.class, meta -> {
            meta.addStoredEnchant(enchantment, level, true);
        });
    }

    public @NotNull ItemStackBuilder glint(boolean flag) {
        editMeta(meta -> {
            meta.setEnchantmentGlintOverride(flag);
        });

        return this;
    }

    public @NotNull ItemStackBuilder damageResistant(@NotNull org.bukkit.Tag<DamageType> damageTypeTag) {
        return editMeta(meta -> {
            meta.setDamageResistant(damageTypeTag);
        });
    }

    public @NotNull ItemStackBuilder itemModel(@NotNull NamespacedKey id) {
        return editMeta(meta -> {
            meta.setItemModel(id);
        });
    }

    public @NotNull ItemStackBuilder hideComponent(@NotNull DataComponentType.Valued<?> type) {
        return editComponent(DataComponentTypes.TOOLTIP_DISPLAY, d -> {
            d.hiddenComponents().add(type);
        });
    }

    @ApiStatus.Experimental
    public @NotNull ItemStackBuilder customModelDataFlags(@NotNull Boolean... flags) {
        return editMeta(meta -> {
            final CustomModelDataComponent component = meta.getCustomModelDataComponent();
            component.setFlags(Arrays.stream(flags).toList());
            meta.setCustomModelDataComponent(component);
        });
    }

    public @NotNull ItemStackBuilder customData(@NotNull MojangsonPath path, @NotNull Object value) {
        final ItemStackCustomDataAccess access = ItemStackCustomDataAccess.of(itemStack);
        final MojangsonCompound compound = access.read();
        compound.set(path, value);
        access.write(compound);
        return this;
    }

    public ItemStack build() {
        return itemStack.clone();
    }

    public static ItemStackBuilder from(ItemStack itemStack) {
        final ItemStackBuilder itemStackBuilder = new ItemStackBuilder(itemStack.getType());

        itemStackBuilder.editMeta(meta -> itemStack.getItemMeta());

        return itemStackBuilder;
    }
}
