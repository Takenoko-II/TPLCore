package com.gmail.subnokoii78.tplcore.random;

import com.gmail.subnokoii78.tplcore.execute.DimensionAccess;
import com.gmail.subnokoii78.tplcore.execute.NumberRange;
import org.bukkit.Material;
import org.bukkit.block.Container;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.stream.Stream;

public final class LootTable {
    private final Set<Pool> pools = new HashSet<>();

    private LootTable() {}

    public @NotNull LootTable pool(@NotNull Pool pool) {
        pools.add(pool);
        return this;
    }

    public @NotNull List<ItemStack> roll(@NotNull RandomService randomService) {
        return pools.stream()
            .flatMap(pool -> pool.roll(randomService).map(CountableItemStack::toItemStack))
            .toList();
    }

    private @NotNull List<ItemStack> rollWithSplitShuffle(@NotNull RandomService randomService, int slots) {
        final List<CountableItemStack> list = pools.stream()
            .flatMap(pool -> pool.roll(randomService))
            .toList();

        randomService.split(
            list,
            slots - list.size(),
            0.7f
        );

        return randomService.shuffledClone(list.stream().map(CountableItemStack::toItemStack).toList());
    }

    public @NotNull Map<Integer, ItemStack> rollWithSpread(@NotNull RandomService randomService, int slots) {
        final List<Integer> ints = new ArrayList<>(Arrays.stream(NumberRange.of(0, slots - 1).ints()).boxed().toList());
        final Map<Integer, ItemStack> map = new HashMap<>();

        for (final ItemStack itemStack : rollWithSplitShuffle(randomService, slots)) {
            final int slot = randomService.choice(ints);
            ints.remove(slot);
            map.put(slot, itemStack);
        }

        return map;
    }

    public void fill(@NotNull RandomService randomService, @NotNull Container container) {
        final Inventory inventory = container.getInventory();
        rollWithSpread(randomService, inventory.getSize()).forEach(inventory::setItem);
    }

    public static @NotNull LootTable of(@NotNull Pool... pools) {
        final LootTable lootTable = new LootTable();
        lootTable.pools.addAll(Arrays.stream(pools).toList());
        return lootTable;
    }

    public static final class Pool {
        private final int rolls;

        private Pool(int rolls) {
            this.rolls = rolls;
        }

        private final Set<Entry> entries = new HashSet<>();

        public @NotNull Pool entry(@NotNull Entry entry) {
            entries.add(entry);
            return this;
        }

        private @NotNull Stream<CountableItemStack> roll(@NotNull RandomService randomService) {
            final Map<Entry, Integer> map = new HashMap<>();

            for (final Entry entry : entries) {
                map.put(entry, entry.weight);
            }

            final Set<CountableItemStack> countables = new HashSet<>();
            for (int i = rolls; i >= 0; i--) {
                final Entry entry = randomService.weightedChoice(map);
                map.remove(entry);
                countables.add(entry.toCountable(randomService));
            }

            return countables.stream();
        }

        public static @NotNull Pool of(int rolls, @NotNull Entry... entries) {
            final Pool pool = new Pool(rolls);
            pool.entries.addAll(Arrays.stream(entries).toList());
            return pool;
        }
    }

    public static final class Entry {
        private final Material material;

        private NumberRange<Integer> count = NumberRange.of(1, 1);

        private int weight = 1;

        private BiConsumer<ItemMeta, RandomService> function = (itemMeta, randomizer) -> {};

        private Entry(@NotNull Material material) {
            if (!material.isItem()) {
                throw new IllegalArgumentException();
            }

            this.material = material;
        }

        @Override
        public boolean equals(Object object) {
            if (this == object) return true;
            if (object == null || getClass() != object.getClass()) return false;
            Entry entry = (Entry) object;
            return weight == entry.weight && material == entry.material && Objects.equals(count, entry.count) && Objects.equals(function, entry.function);
        }

        @Override
        public int hashCode() {
            return Objects.hash(material, count, weight, function);
        }

        public @NotNull Entry count(@NotNull NumberRange<Integer> range) {
            this.count = range;
            return this;
        }

        public @NotNull Entry weight(int weight) {
            this.weight = weight;
            return this;
        }

        public @NotNull Entry function(@NotNull BiConsumer<ItemMeta, RandomService> function) {
            this.function = (itemMeta, randomizer) -> {
                this.function.accept(itemMeta, randomizer);
                function.accept(itemMeta, randomizer);
            };
            return this;
        }

        private @NotNull CountableItemStack toCountable(@NotNull RandomService randomService) {
            final ItemMeta itemMeta = new ItemStack(material).getItemMeta();
            function.accept(itemMeta, randomService);
            return new CountableItemStack(material, randomService.getRandomizer().randInt(count), itemMeta);
        }

        public static @NotNull Entry of(@NotNull Material material) {
            return new Entry(material);
        }
    }

    private record Pair<A, B>(A a, B b) {}

    public static final class CountableItemStack extends AbstractCountable<Pair<Material, ItemMeta>> {
        private CountableItemStack(@NotNull Material data, int count,  @NotNull ItemMeta itemMeta) {
            super(new Pair<>(data, itemMeta), count);
        }

        @Override
        public @NotNull CountableItemStack copy() {
            return new CountableItemStack(value.a, count, value.b);
        }

        public @NotNull ItemStack toItemStack() {
            final ItemStack itemStack = new ItemStack(value.a, count);
            itemStack.setItemMeta(value.b.clone());
            return itemStack;
        }
    }
}
