package com.gmail.subnokoii78.tplcore.random;

import com.gmail.subnokoii78.tplcore.execute.NumberRange;
import org.jetbrains.annotations.NotNull;

public interface RangeRandomizer {
    int randInt(@NotNull NumberRange<Integer> range);

    long randLong(@NotNull NumberRange<Long> range);

    float randFloat(@NotNull NumberRange<Float> range);

    double randDouble(@NotNull NumberRange<Double> range);
}
