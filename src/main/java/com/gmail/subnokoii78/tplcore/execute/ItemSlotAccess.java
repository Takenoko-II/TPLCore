package com.gmail.subnokoii78.tplcore.execute;

import jdk.jfr.Experimental;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;

// TODO: MultiAccessorの中身つくる
// TODO: ほかにもいっぱいフィールドつくる
// TODO: matches(Predicate<ItemStack>)とかもつくる
@Experimental
public final class ItemSlotAccess {
    public static abstract class ItemSlots<T> {
        protected abstract @NotNull List<SingleAccessor<T>> getAllAccessors();
    }

    public static abstract class AbstractAccessor<T> {}

    public static abstract class SingleAccessor<T> extends AbstractAccessor<T> {
        public abstract @Nullable ItemStack getOrNull(@NotNull T target);
    }

    public static abstract class MultiAccessor<T> extends AbstractAccessor<T> {

    }

    public static abstract class NumberableItemSlots<T> extends ItemSlots<T> {
        public abstract @NotNull NumberRange<Integer> getAvailableSlotRange();

        public abstract @NotNull SingleAccessor<T> $(int index) throws UnknownSlotNumberException;

        @Override
        protected final @NotNull List<SingleAccessor<T>> getAllAccessors() {
            return Arrays.stream(getAvailableSlotRange().ints()).mapToObj(this::$).toList();
        }

        public static final class UnknownSlotNumberException extends RuntimeException {
            private UnknownSlotNumberException(int i) {
                super("無効なスロット番号です: " + i);
            }
        }
    }

    public static final NumberableItemSlots<InventoryHolder> container = new NumberableItemSlots<>() {
        @Override
        public @NotNull NumberRange<Integer> getAvailableSlotRange() {
            return NumberRange.of(0, 26);
        }

        @Override
        public @NotNull SingleAccessor<InventoryHolder> $(int index) throws UnknownSlotNumberException {
            return new SingleAccessor<>() {
                @Override
                public @Nullable ItemStack getOrNull(@NotNull InventoryHolder target) {
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

    public static final NumberableItemSlots<HumanEntity> enderchest = new NumberableItemSlots<>() {
        @Override
        public @NotNull NumberRange<Integer> getAvailableSlotRange() {
            return NumberRange.of(0, 26);
        }

        @Override
        public @NotNull SingleAccessor<HumanEntity> $(int index) throws UnknownSlotNumberException {
            return new SingleAccessor<>() {
                @Override
                public @Nullable ItemStack getOrNull(@NotNull HumanEntity target) {
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

    public static final NumberableItemSlots<Player> hotbar = new NumberableItemSlots<>() {
        @Override
        public @NotNull NumberRange<Integer> getAvailableSlotRange() {
            return NumberRange.of(0, 8);
        }

        @Override
        public @NotNull SingleAccessor<Player> $(int index) throws UnknownSlotNumberException {
            return new SingleAccessor<>() {
                @Override
                public @Nullable ItemStack getOrNull(@NotNull Player target) {
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
}
