package com.gmail.subnokoii78.tplcore.execute;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import jdk.jfr.Experimental;
import net.minecraft.commands.arguments.SlotArgument;
import org.bukkit.Material;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.entity.*;
import org.bukkit.inventory.*;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;

// TODO: $() が返す値をMatcherを継承したもの(=MultiAccessor)にする
// TODO: 現 $() の名称を getAccessorList() にする
@Experimental
@NullMarked
public final class ItemSlots {
    public interface Matcher<T> {
        boolean matches(T target, Predicate<@Nullable ItemStack> predicate);

        Class<T> getClazz();
    }

    private static abstract class SlotCategory<T> implements Matcher<T> {
        protected SlotCategory() {}

        protected abstract List<SingleAccessor<T>> getAccessorList();

        @Override
        public final boolean matches(T target, Predicate<@Nullable ItemStack> predicate) {
            return getAccessorList().stream().anyMatch(accessor -> predicate.test(accessor.getOrNull(target)));
        }
    }

    public static abstract class SingleAccessor<T> implements Matcher<T> {
        public abstract @Nullable ItemStack getOrNull(T target);

        @Override
        public boolean matches(T target, Predicate<@Nullable ItemStack> predicate) {
            return predicate.test(getOrNull(target));
        }
    }

    public static abstract class NumberableSlotCategory<T> extends SlotCategory<T> {
        protected NumberableSlotCategory() {}

        protected abstract NumberRange<Integer> getAvailableSlotRange();

        public abstract SingleAccessor<T> $(int index) throws UnknownSlotNumberException;

        protected final List<SingleAccessor<T>> getAllNumberableSlots() {
            return Arrays.stream(getAvailableSlotRange().ints()).mapToObj(this::$).toList();
        }

        @Override
        public abstract List<SingleAccessor<T>> getAccessorList();

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
        public List<SingleAccessor<BlockInventoryHolder>> getAccessorList() {
            return getAllNumberableSlots();
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

                @Override
                public Class<BlockInventoryHolder> getClazz() {
                    return BlockInventoryHolder.class;
                }
            };
        }

        @Override
        public Class<BlockInventoryHolder> getClazz() {
            return BlockInventoryHolder.class;
        }
    };

    public static final NumberableSlotCategory<HumanEntity> enderchest = new NumberableSlotCategory<>() {
        @Override
        protected NumberRange<Integer> getAvailableSlotRange() {
            return NumberRange.of(0, 26);
        }

        @Override
        public List<SingleAccessor<HumanEntity>> getAccessorList() {
            return getAllNumberableSlots();
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

                @Override
                public Class<HumanEntity> getClazz() {
                    return HumanEntity.class;
                }
            };
        }

        @Override
        public Class<HumanEntity> getClazz() {
            return HumanEntity.class;
        }
    };

    public static final NumberableSlotCategory<Player> hotbar = new NumberableSlotCategory<>() {
        @Override
        protected NumberRange<Integer> getAvailableSlotRange() {
            return NumberRange.of(0, 8);
        }

        @Override
        public List<SingleAccessor<Player>> getAccessorList() {
            return getAllNumberableSlots();
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

                @Override
                public Class<Player> getClazz() {
                    return Player.class;
                }
            };
        }

        @Override
        public Class<Player> getClazz() {
            return Player.class;
        }
    };

    public static final NumberableSlotCategory<InventoryHolder> inventory = new NumberableSlotCategory<>() {
        @Override
        protected NumberRange<Integer> getAvailableSlotRange() {
            return NumberRange.of(0, 26);
        }

        @Override
        public List<SingleAccessor<InventoryHolder>> getAccessorList() {
            return getAllNumberableSlots();
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

                @Override
                public Class<InventoryHolder> getClazz() {
                    return InventoryHolder.class;
                }
            };
        }

        @Override
        public Class<InventoryHolder> getClazz() {
            return InventoryHolder.class;
        }
    };

    public static final class HorseSlotCategory extends NumberableSlotCategory<ChestedHorse> {
        private HorseSlotCategory() {}

        @Override
        protected NumberRange<Integer> getAvailableSlotRange() {
            return NumberRange.of(0, 14);
        }

        @Override
        public List<SingleAccessor<ChestedHorse>> getAccessorList() {
            final List<SingleAccessor<ChestedHorse>> list = new ArrayList<>(getAllNumberableSlots());
            list.add(chest);
            return list;
        }

        @Override
        public SingleAccessor<ChestedHorse> $(int index) throws UnknownSlotNumberException {
            return new SingleAccessor<>() {
                @Override
                public @Nullable ItemStack getOrNull(ChestedHorse target) {
                    if (getAvailableSlotRange().within(index)) {
                        return target.getInventory().getItem(index);
                    }
                    else {
                        throw new UnknownSlotNumberException(index);
                    }
                }

                @Override
                public Class<ChestedHorse> getClazz() {
                    return ChestedHorse.class;
                }
            };
        }

        public final SingleAccessor<ChestedHorse> chest = new SingleAccessor<>() {
            @Override
            public @Nullable ItemStack getOrNull(ChestedHorse target) {
                return target.isCarryingChest() ? new ItemStack(Material.CHEST) : null;
            }

            @Override
            public Class<ChestedHorse> getClazz() {
                return ChestedHorse.class;
            }
        };

        @Override
        public Class<ChestedHorse> getClazz() {
            return ChestedHorse.class;
        }
    }

    public static final HorseSlotCategory horse = new HorseSlotCategory();

    public static final NumberableSlotCategory<AbstractVillager> villager = new NumberableSlotCategory<>() {
        @Override
        protected NumberRange<Integer> getAvailableSlotRange() {
            return NumberRange.of(0, 7);
        }

        @Override
        public List<SingleAccessor<AbstractVillager>> getAccessorList() {
            return getAllNumberableSlots();
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

                @Override
                public Class<AbstractVillager> getClazz() {
                    return AbstractVillager.class;
                }
            };
        }

        @Override
        public Class<AbstractVillager> getClazz() {
            return AbstractVillager.class;
        }
    };

    public static final class PlayerSlotCategory extends SlotCategory<Player> {
        private PlayerSlotCategory() {}

        @Override
        protected List<SingleAccessor<Player>> getAccessorList() {
            final List<SingleAccessor<Player>> list = new ArrayList<>(crafting.getAccessorList());
            list.add(cursor);
            return list;
        }

        public final NumberableSlotCategory<Player> crafting = new NumberableSlotCategory<>() {
            @Override
            protected NumberRange<Integer> getAvailableSlotRange() {
                return NumberRange.of(0, 3);
            }

            @Override
            public List<SingleAccessor<Player>> getAccessorList() {
                return getAllNumberableSlots();
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

                    @Override
                    public Class<Player> getClazz() {
                        return Player.class;
                    }
                };
            }

            @Override
            public Class<Player> getClazz() {
                return Player.class;
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

            @Override
            public Class<Player> getClazz() {
                return Player.class;
            }
        };

        @Override
        public Class<Player> getClazz() {
            return Player.class;
        }
    }

    public static final PlayerSlotCategory player = new PlayerSlotCategory();

    public static final SingleAccessor<AbstractHorse> saddle = new SingleAccessor<>() {
        private EntityEquipment getEquipment(LivingEntity target) {
            final EntityEquipment equipment = target.getEquipment();

            if (equipment == null) {
                throw new IllegalArgumentException("EntityEquipment の取得に失敗しました: " + target.getClass().getName());
            }

            return equipment;
        }

        @Override
        public @Nullable ItemStack getOrNull(AbstractHorse target) {
            final ItemStack itemStack = getEquipment(target).getItem(EquipmentSlot.SADDLE);

            return itemStack.isEmpty() ? null : itemStack;
        }

        @Override
        public Class<AbstractHorse> getClazz() {
            return AbstractHorse.class;
        }
    };

    public static final class WeaponSlotCategory extends SlotCategory<LivingEntity> {
        private WeaponSlotCategory() {}

        @Override
        public List<SingleAccessor<LivingEntity>> getAccessorList() {
            return List.of(mainhand, offhand);
        }

        private EntityEquipment getEquipment(LivingEntity target) {
            final EntityEquipment equipment = target.getEquipment();

            if (equipment == null) {
                throw new IllegalArgumentException("EntityEquipment の取得に失敗しました: " + target.getClass().getName());
            }

            return equipment;
        }

        public final SingleAccessor<LivingEntity> mainhand = new SingleAccessor<>() {
            @Override
            public @Nullable ItemStack getOrNull(LivingEntity target) {
                final ItemStack itemStack = getEquipment(target).getItemInMainHand();

                return itemStack.isEmpty() ? null : itemStack;
            }

            @Override
            public Class<LivingEntity> getClazz() {
                return LivingEntity.class;
            }
        };

        public final SingleAccessor<LivingEntity> offhand = new SingleAccessor<>() {
            @Override
            public @Nullable ItemStack getOrNull(LivingEntity target) {
                final ItemStack itemStack = getEquipment(target).getItemInOffHand();

                return itemStack.isEmpty() ? null : itemStack;
            }

            @Override
            public Class<LivingEntity> getClazz() {
                return LivingEntity.class;
            }
        };

        @Override
        public Class<LivingEntity> getClazz() {
            return LivingEntity.class;
        }
    }

    public static final WeaponSlotCategory weapon = new WeaponSlotCategory();

    public static final class ArmorSlotCategory extends SlotCategory<LivingEntity> {
        private ArmorSlotCategory() {}

        private EntityEquipment getEquipment(LivingEntity target) {
            final EntityEquipment equipment = target.getEquipment();

            if (equipment == null) {
                throw new IllegalArgumentException("EntityEquipment の取得に失敗しました: " + target.getClass().getName());
            }

            return equipment;
        }

        @Override
        public List<SingleAccessor<LivingEntity>> getAccessorList() {
            return List.of(head, chest, legs, feet, body);
        }

        public final SingleAccessor<LivingEntity> head = new SingleAccessor<>() {
            @Override
            public @Nullable ItemStack getOrNull(LivingEntity target) {
                final ItemStack itemStack = getEquipment(target).getHelmet();

                return itemStack.isEmpty() ? null : itemStack;
            }

            @Override
            public Class<LivingEntity> getClazz() {
                return LivingEntity.class;
            }
        };

        public final SingleAccessor<LivingEntity> chest = new SingleAccessor<>() {
            @Override
            public @Nullable ItemStack getOrNull(LivingEntity target) {
                final ItemStack itemStack = getEquipment(target).getChestplate();

                return itemStack.isEmpty() ? null : itemStack;
            }

            @Override
            public Class<LivingEntity> getClazz() {
                return LivingEntity.class;
            }
        };

        public final SingleAccessor<LivingEntity> legs = new SingleAccessor<>() {
            @Override
            public @Nullable ItemStack getOrNull(LivingEntity target) {
                final ItemStack itemStack = getEquipment(target).getLeggings();

                return itemStack.isEmpty() ? null : itemStack;
            }

            @Override
            public Class<LivingEntity> getClazz() {
                return LivingEntity.class;
            }
        };

        public final SingleAccessor<LivingEntity> feet = new SingleAccessor<>() {
            @Override
            public @Nullable ItemStack getOrNull(LivingEntity target) {
                final ItemStack itemStack = getEquipment(target).getBoots();

                return itemStack.isEmpty() ? null : itemStack;
            }

            @Override
            public Class<LivingEntity> getClazz() {
                return LivingEntity.class;
            }
        };

        public final SingleAccessor<LivingEntity> body = new SingleAccessor<>() {
            @Override
            public @Nullable ItemStack getOrNull(LivingEntity target) {
                final ItemStack itemStack = getEquipment(target).getItem(EquipmentSlot.BODY);

                return itemStack.isEmpty() ? null : itemStack;
            }

            @Override
            public Class<LivingEntity> getClazz() {
                return LivingEntity.class;
            }
        };

        @Override
        public Class<LivingEntity> getClazz() {
            return LivingEntity.class;
        }
    }

    public static final ArmorSlotCategory armor = new ArmorSlotCategory();
}
