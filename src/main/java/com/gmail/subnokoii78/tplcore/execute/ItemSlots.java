package com.gmail.subnokoii78.tplcore.execute;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import jdk.jfr.Experimental;
import net.minecraft.commands.arguments.SlotArgument;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.entity.*;
import org.bukkit.inventory.BlockInventoryHolder;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.Arrays;
import java.util.List;

// TODO: MultiAccessorの中身つくる <- なに作る予定だったの過去の私！！！！！！！
// TODO: ほかにもいっぱいフィールドつくる
// TODO: matches(Predicate<ItemStack>)とかもつくる
@Experimental
@NullMarked
public final class ItemSlots {
    public static abstract class SlotCategory<T> {
        protected abstract List<SingleAccessor<T>> $();
    }

    public static abstract class AbstractAccessor<T> {}

    public static abstract class SingleAccessor<T> extends AbstractAccessor<T> {
        public abstract @Nullable ItemStack getOrNull(T target);
    }

    public static abstract class MultiAccessor<T> extends AbstractAccessor<T> {

    }

    public static abstract class NumberableSlotCategory<T> extends SlotCategory<T> {
        protected abstract NumberRange<Integer> getAvailableSlotRange();

        public abstract SingleAccessor<T> $(int index) throws UnknownSlotNumberException;

        @Override
        protected final List<SingleAccessor<T>> $() {
            return Arrays.stream(getAvailableSlotRange().ints()).mapToObj(this::$).toList();
        }

        public static final class UnknownSlotNumberException extends RuntimeException {
            private UnknownSlotNumberException(int i) {
                super("無効なスロット番号です: " + i);
            }
        }
    }

    public static final NumberableSlotCategory<BlockInventoryHolder> container = new NumberableSlotCategory<>() {
        @Override
        protected NumberRange<Integer> getAvailableSlotRange() {
            return NumberRange.of(0, 26);
        }

        @Override
        public SingleAccessor<BlockInventoryHolder> $(int index) throws UnknownSlotNumberException {
            return new SingleAccessor<>() {
                @Override
                public @Nullable ItemStack getOrNull(BlockInventoryHolder target) {
                    if (getAvailableSlotRange().within(index)) {
                        return target.getInventory().getItem(index);
                    }
                    else {
                        throw new UnknownSlotNumberException(index);
                    }
                }
            };
        }
    };

    public static final NumberableSlotCategory<HumanEntity> enderchest = new NumberableSlotCategory<>() {
        @Override
        protected NumberRange<Integer> getAvailableSlotRange() {
            return NumberRange.of(0, 26);
        }

        @Override
        public SingleAccessor<HumanEntity> $(int index) throws UnknownSlotNumberException {
            return new SingleAccessor<>() {
                @Override
                public @Nullable ItemStack getOrNull(HumanEntity target) {
                    if (getAvailableSlotRange().within(index)) {
                        return target.getEnderChest().getItem(index);
                    }
                    else {
                        throw new UnknownSlotNumberException(index);
                    }
                }
            };
        }
    };

    public static final NumberableSlotCategory<Player> hotbar = new NumberableSlotCategory<>() {
        @Override
        protected NumberRange<Integer> getAvailableSlotRange() {
            return NumberRange.of(0, 8);
        }

        @Override
        public SingleAccessor<Player> $(int index) throws UnknownSlotNumberException {
            return new SingleAccessor<>() {
                @Override
                public @Nullable ItemStack getOrNull(Player target) {
                    if (getAvailableSlotRange().within(index)) {
                        return target.getInventory().getItem(index);
                    }
                    else {
                        throw new UnknownSlotNumberException(index);
                    }
                }
            };
        }
    };

    public static final NumberableSlotCategory<InventoryHolder> inventory = new NumberableSlotCategory<>() {
        @Override
        protected NumberRange<Integer> getAvailableSlotRange() {
            return NumberRange.of(0, 26);
        }

        @Override
        public SingleAccessor<InventoryHolder> $(int index) throws UnknownSlotNumberException {
            return new SingleAccessor<>() {
                @Override
                public @Nullable ItemStack getOrNull(InventoryHolder target) {
                    if (!(target instanceof Entity)) {
                        throw new IllegalArgumentException("スロット inventory はエンティティにのみ有効です");
                    }

                    if (getAvailableSlotRange().within(index)) {
                        return target.getInventory().getItem(index);
                    }
                    else {
                        throw new UnknownSlotNumberException(index);
                    }
                }
            };
        }
    };

    public static final NumberableSlotCategory<AbstractHorse> horse = new NumberableSlotCategory<>() {
        @Override
        protected NumberRange<Integer> getAvailableSlotRange() {
            return NumberRange.of(0, 14);
        }

        @Override
        public SingleAccessor<AbstractHorse> $(int index) throws UnknownSlotNumberException {
            return new SingleAccessor<>() {
                @Override
                public @Nullable ItemStack getOrNull(AbstractHorse target) {
                    if (getAvailableSlotRange().within(index)) {
                        return target.getInventory().getItem(index);
                    }
                    else {
                        throw new UnknownSlotNumberException(index);
                    }
                }
            };
        }

        public final SingleAccessor<AbstractHorse> chest = new SingleAccessor<>() {
            @Override
            public @Nullable ItemStack getOrNull(AbstractHorse target) {
                // TODO: chest
            }
        };
    };

    public static final NumberableSlotCategory<AbstractVillager> villager = new NumberableSlotCategory<>() {
        @Override
        protected NumberRange<Integer> getAvailableSlotRange() {
            return NumberRange.of(0, 7);
        }

        @Override
        public SingleAccessor<AbstractVillager> $(int index) throws UnknownSlotNumberException {
            return new SingleAccessor<>() {
                @Override
                public @Nullable ItemStack getOrNull(AbstractVillager target) {
                    if (getAvailableSlotRange().within(index)) {
                        return target.getInventory().getItem(index);
                    }
                    else {
                        throw new UnknownSlotNumberException(index);
                    }
                }
            };
        }
    };

    public static final SlotCategory<Player> player = new SlotCategory<>() {
        @Override
        protected List<SingleAccessor<Player>> $() {
            return List.of();
        }

        public final NumberableSlotCategory<Player> crafting = new NumberableSlotCategory<>() {
            @Override
            protected NumberRange<Integer> getAvailableSlotRange() {
                return NumberRange.of(0, 3);
            }

            private int getCraftingSlotId(int index) {
                try {
                    return SlotArgument.slot().parse(new StringReader("player.crafting." + index));
                }
                catch (CommandSyntaxException e) {
                    throw new IllegalArgumentException("不明な crafting へのアクセスです: " + index);
                }
            }

            @Override
            public SingleAccessor<Player> $(int index) throws UnknownSlotNumberException {
                return new SingleAccessor<>() {
                    @Override
                    public @Nullable ItemStack getOrNull(Player target) {
                        if (getAvailableSlotRange().within(index)) {
                            final ItemStack itemStack = CraftItemStack.asBukkitCopy(
                                ((CraftPlayer) target)
                                    .getHandle()
                                    .getSlot(getCraftingSlotId(index))
                                    .get()
                            );

                            if (itemStack.isEmpty()) return null;
                            else return itemStack;
                        }
                        else {
                            throw new UnknownSlotNumberException(index);
                        }
                    }
                };
            }
        };

        public final SingleAccessor<Player> cursor = new SingleAccessor<>() {
            @Override
            public @Nullable ItemStack getOrNull(Player target) {
                final ItemStack itemStack = target.getItemOnCursor();

                if (itemStack.isEmpty()) {
                    return null;
                }
                else return itemStack;
            }
        };
    };

    public static final SingleAccessor<AbstractHorse> saddle = new SingleAccessor<>() {
        @Override
        public @Nullable ItemStack getOrNull(AbstractHorse target) {
            return target.getInventory().getSaddle();
        }
    };

    // TODO: weapon, weapon.mainhand, weapon.offhand, armor.*, armor.head, armor.chest, armor.legs, armor.feet, armor.body
}
