package com.gmail.subnokoii78.tplcore.random;

import com.gmail.subnokoii78.tplcore.execute.NumberRange;
import com.gmail.subnokoii78.tplcore.vector.DualAxisRotationBuilder;
import com.gmail.subnokoii78.tplcore.vector.TripleAxisRotationBuilder;
import net.minecraft.Util;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class RandomService {
    private final RangeRandomizer randomizer;

    public RandomService(@NotNull RangeRandomizer randomizer) {
        this.randomizer = randomizer;
    }

    @ApiStatus.Obsolete
    public @NotNull String uuid() {
        final char[] chars = "xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx".toCharArray();

        for (int i = 0; i < chars.length; i++) {
            switch (chars[i]) {
                case 'x':
                    chars[i] = Integer.toHexString(this.randomizer.randInt(NumberRange.of(0, 15))).charAt(0);
                    break;
                case 'y':
                    chars[i] = Integer.toHexString(this.randomizer.randInt(NumberRange.of(8, 11))).charAt(0);
                    break;
            }
        }

        return String.valueOf(chars);
    }

    public boolean chance(double chance) {
        return this.randomizer.randDouble(NumberRange.of(0d, 1d)) < chance;
    }

    public int sign() {
        return this.chance(0.5f) ? 1 : -1;
    }

    public int choiceIndex(@NotNull Collection<?> collection) {
        return randomizer.randInt(NumberRange.of(0, collection.size() - 1));
    }

    public <T> @NotNull T choice(@NotNull Collection<T> collection) {
        return collection.stream().toList().get(choiceIndex(collection));
    }

    public <T> @NotNull Set<T> sample(@NotNull Set<T> set, int count) {
        if (count < 0 || count > set.size()) {
            throw new IllegalArgumentException("countの値は0以上要素数以下である必要があります");
        }

        return new HashSet<>(
            shuffledClone(set.stream().toList()).subList(0, count)
        );
    }

    public double boxMuller() {
        double a, b;

        do {
            a = this.randomizer.randDouble(NumberRange.of(0d, 1d));
        }
        while (a == 0);

        do {
            b = this.randomizer.randDouble(NumberRange.of(0d, 1d));
        }
        while (b == 1);

        return Math.sqrt(-2 * Math.log(a)) * Math.sin(2 * Math.PI * b);
    }

    public <T> T weightedChoice(@NotNull Map<T, Integer> weights) {
        int sum = 0;
        for (final int weight : weights.values()) {
            sum += weight;
        }

        final int random = this.randomizer.randInt(NumberRange.of(1, sum));

        int totalWeight = 0;

        for (final Map.Entry<T, Integer> entry : weights.entrySet()) {
            totalWeight += entry.getValue();
            if (totalWeight >= random) return entry.getKey();
        }

        throw new IllegalStateException("NEVER HAPPENS");
    }

    public <T> @NotNull List<T> shuffledClone(@NotNull List<T> list) {
        final ArrayList<T> clone = new ArrayList<>(list);

        if (list.size() <= 1) return clone;

        for (int i = clone.size() - 1; i >= 0; i--) {
            final T current = clone.get(i);
            final int random = this.randomizer.randInt(NumberRange.of(0, i));

            System.out.println("randInt(0, " + i + "): "+ random);

            clone.set(i, clone.get(random));
            clone.set(random, current);
        }

        return clone;
    }

    public @NotNull DualAxisRotationBuilder rotation2() {
        return new DualAxisRotationBuilder(
            this.randomizer.randFloat(NumberRange.of(-180f, 180f)),
            this.randomizer.randFloat(NumberRange.of(-90f, 90f))
        );
    }

    public @NotNull TripleAxisRotationBuilder rotation3() {
        return new TripleAxisRotationBuilder(
            this.randomizer.randFloat(NumberRange.of(-180f, 180f)),
            this.randomizer.randFloat(NumberRange.of(-90f, 90f)),
            this.randomizer.randFloat(NumberRange.of(-180f, 180f))
        );
    }

    public <T extends AbstractCountable<?>> void split(@NotNull List<T> list, int slots, float splittability) {
        final List<T> splittables = new ArrayList<>();

        final Iterator<T> iterator = list.iterator();
        T countable;

        // ここでは, splittables には個数2以上のオブジェクトのみが入る
        // list は破壊され, 個数1のオブジェクトのみになる
        // 個数0のオブジェクトはすべて消去・無視される
        while (iterator.hasNext()) {
            countable = iterator.next();

            if (countable.getCount() == 0) {
                iterator.remove();
            }
            else if (countable.getCount() >= 2) {
                splittables.add(countable);
                iterator.remove();
            }
        }

        // まだ分割できていない残り物がある限りループね
        while (!splittables.isEmpty()) {
            // countable変数は再利用(元々入ってたものは無視)
            // ランダムに選んだ要素一つを1～半分のランダムな個数に分割する
            countable = splittables.remove(choiceIndex(splittables));
            final T part = (T) countable.split(
                randomizer.randInt(NumberRange.of(1, countable.getCount() / 2))
            );

            if (countable.getCount() >= 2 && chance(splittability)) {
                // まだ分割可能だった場合、splittability の確率で、分割されて残った方を再度リストに入れる
                splittables.add(countable);
            }
            else {
                // 分割不可能又は二分の一を外した場合、分割完了として元のリストに入れる
                list.add(countable);
            }

            // 分割されて新たに生成された方についても同様に
            if (part.getCount() >= 2 && chance(splittability)) {
                splittables.add(part);
            }
            else {
                list.add(part);
            }

            // 空きスロットがもうない！
            if (slots - list.size() - splittables.size() <= 0) {
                // 分割できなかったやつ入れて終了
                list.addAll(splittables);
                break;
            }
        }
    }
}
