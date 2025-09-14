package com.gmail.subnokoii78.tplcore.itemstack;

import com.gmail.takenokoii78.mojangson.MojangsonParser;
import com.gmail.takenokoii78.mojangson.MojangsonSerializer;
import com.gmail.takenokoii78.mojangson.values.MojangsonCompound;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.TagParser;
import net.minecraft.world.item.component.CustomData;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public final class ItemStackCustomDataAccess {
    private final ItemStack itemStack;

    private ItemStackCustomDataAccess(@NotNull ItemStack itemStack) {
        this.itemStack = itemStack;
    }

    @Override
    public int hashCode() {
        return Objects.hash(itemStack);
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        ItemStackCustomDataAccess that = (ItemStackCustomDataAccess) object;
        return Objects.equals(itemStack, that.itemStack);
    }

    private @NotNull CompoundTag getCustomData() {
        final CustomData customData = ((CraftItemStack) itemStack).handle.get(DataComponents.CUSTOM_DATA);
        return (customData == null) ? new CompoundTag() : customData.copyTag();
    }

    private void setCustomData(@NotNull CompoundTag compound) {
        ((CraftItemStack) itemStack).handle.set(DataComponents.CUSTOM_DATA, CustomData.of(compound));
    }

    public @NotNull MojangsonCompound read() {
        return MojangsonParser.compound(getCustomData().toString());
    }

    public boolean write(@NotNull MojangsonCompound compound) {
        try {
            setCustomData(TagParser.parseCompoundFully(MojangsonSerializer.serialize(compound)));
            return true;
        }
        catch (CommandSyntaxException e) {
            return false;
        }
    }

    public static @NotNull ItemStackCustomDataAccess of(@NotNull ItemStack itemStack) {
        return new ItemStackCustomDataAccess(itemStack);
    }
}
