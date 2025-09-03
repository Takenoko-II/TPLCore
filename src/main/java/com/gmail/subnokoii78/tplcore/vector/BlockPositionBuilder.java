package com.gmail.subnokoii78.tplcore.vector;

import com.gmail.subnokoii78.tplcore.generic.TriFunction;
import org.jetbrains.annotations.NotNull;

import java.util.function.BiFunction;
import java.util.function.UnaryOperator;

public class BlockPositionBuilder implements VectorBuilder<BlockPositionBuilder, Integer> {
    private int x, y, z;

    public BlockPositionBuilder(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    @Override
    public boolean equals(@NotNull BlockPositionBuilder other) {
        return x == other.x && y == other.y && z == other.z;
    }

    @Override
    public @NotNull BlockPositionBuilder calculate(@NotNull UnaryOperator<Integer> operator) {
        x = operator.apply(x);
        y = operator.apply(y);
        z = operator.apply(z);
        return this;
    }

    @Override
    public @NotNull BlockPositionBuilder calculate(@NotNull BlockPositionBuilder other, @NotNull BiFunction<Integer, Integer, Integer> operator) {
        x = operator.apply(x, other.x);
        y = operator.apply(y, other.y);
        z = operator.apply(z, other.z);
        return this;
    }

    @Override
    public @NotNull BlockPositionBuilder calculate(@NotNull BlockPositionBuilder other1, @NotNull BlockPositionBuilder other2, @NotNull TriFunction<Integer, Integer, Integer, Integer> operator) {
        x = operator.apply(x, other1.x, other2.x);
        y = operator.apply(y, other1.y, other2.y);
        z = operator.apply(z, other1.z, other2.z);
        return this;
    }

    @Override
    public @NotNull BlockPositionBuilder add(@NotNull BlockPositionBuilder other) {
        return calculate(other, Integer::sum);
    }

    @Override
    public @NotNull BlockPositionBuilder subtract(@NotNull BlockPositionBuilder other) {
        return add(other.copy().invert());
    }

    @Override
    public @NotNull BlockPositionBuilder scale(@NotNull Integer scalar) {
        return calculate(component -> component * scalar);
    }

    @Override
    public @NotNull BlockPositionBuilder invert() {
        return scale(-1);
    }

    @Override
    public @NotNull BlockPositionBuilder clamp(@NotNull BlockPositionBuilder min, @NotNull BlockPositionBuilder max) {
        return calculate(min, max, (value, minValue, maxValue) -> Math.max(minValue, Math.min(value, maxValue)));
    }

    @Override
    public @NotNull String format(@NotNull String format, int digits) {
        return format
            .replaceAll("\\$x", String.valueOf(x))
            .replaceAll("\\$y", String.valueOf(y))
            .replaceAll("\\$z", String.valueOf(z))
            .replaceFirst("\\$c", String.valueOf(x))
            .replaceFirst("\\$c", String.valueOf(y))
            .replaceFirst("\\$c", String.valueOf(z))
            .replaceAll("\\$c", "");
    }

    @Override
    public @NotNull String toString() {
        return format("($x, $y, $z)", 0);
    }

    @Override
    public @NotNull BlockPositionBuilder copy() {
        return new BlockPositionBuilder(x, y, z);
    }

    @Override
    public boolean isZero() {
        return equals(new BlockPositionBuilder(0, 0, 0));
    }

    @Override
    public boolean similar(@NotNull BlockPositionBuilder other, int digits) {
        return false;
    }
}
