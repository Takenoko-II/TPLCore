package com.gmail.subnokoii78.tplcore.random;

import com.gmail.subnokoii78.tplcore.execute.NumberRange;
import org.jetbrains.annotations.NotNull;

public class Xorshift32 implements RangeRandomizer {
    private int x = 123456789;
    private int y = 362436069;
    private int z = 521288629;
    private int w;

    public Xorshift32(int seed) {
        w = seed;
    }

    public int next() {
        final int t = x ^ (x << 11);

        x = y;
        y = z;
        z = w;
        w = (w ^ (w >>> 19)) ^ (t ^ (t >>> 8));

        return w - NumberRange.INT32.min();
    }

    @Override
    public int randInt(@NotNull NumberRange<Integer> range) {
        return next() % (range.max() - range.min() + 1) + range.min();
    }

    @Override
    public long randLong(@NotNull NumberRange<Long> range) {
        return  (long) next() % (range.max() - range.min() + 1) + range.min();
    }

    @Override
    public float randFloat(@NotNull NumberRange<Float> range) {
        return ((float) next() / (float) NumberRange.UINT32.max()) * (range.max() - range.min()) + range.min();
    }

    @Override
    public double randDouble(@NotNull NumberRange<Double> range) {
        return (next() / (double) NumberRange.UINT32.max()) * (range.max() - range.min()) + range.min();
    }

    public static @NotNull Xorshift32 random() {
        return new Xorshift32(RandomService.int32());
    }
}
