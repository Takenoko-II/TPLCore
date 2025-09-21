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

        return w;
    }

    @Override
    public int randInt(@NotNull NumberRange<Integer> range) {
        return (next() & Integer.MAX_VALUE /* -2^31対策 */) % (range.max() - range.min() + 1) + range.min();
    }

    @Override
    public long randLong(@NotNull NumberRange<Long> range) {
        return  (next() & Long.MAX_VALUE) % (range.max() - range.min() + 1) + range.min();
    }

    @Override
    public float randFloat(@NotNull NumberRange<Float> range) {
        return (((float) (next() & Integer.MAX_VALUE)) / Integer.MAX_VALUE) * (range.max() - range.min()) + range.min();
    }

    @Override
    public double randDouble(@NotNull NumberRange<Double> range) {
        return (((double) (next() & Integer.MAX_VALUE)) / Integer.MAX_VALUE) * (range.max() - range.min()) + range.min();
    }

    public static @NotNull Xorshift32 random() {
        return new Xorshift32((int) (System.nanoTime() & Integer.MAX_VALUE));
    }
}
