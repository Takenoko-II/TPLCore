package com.gmail.subnokoii78.tplcore.vector;

import com.gmail.subnokoii78.tplcore.generic.TriFunction;
import net.minecraft.core.BlockPos;
import org.jetbrains.annotations.ApiStatus;
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

    public int x() {
        return x;
    }

    public int y() {
        return y;
    }

    public int z() {
        return z;
    }

    @Destructive
    public @NotNull BlockPositionBuilder x(int value) {
        x = value;
        return this;
    }


    @Destructive
    public @NotNull BlockPositionBuilder y(int value) {
        y = value;
        return this;
    }


    @Destructive
    public @NotNull BlockPositionBuilder z(int value) {
        z = value;
        return this;
    }

    @Override
    public boolean equals(@NotNull BlockPositionBuilder other) {
        return x == other.x && y == other.y && z == other.z;
    }


    @Destructive
    @Override
    public @NotNull BlockPositionBuilder calculate(@NotNull UnaryOperator<Integer> operator) {
        x = operator.apply(x);
        y = operator.apply(y);
        z = operator.apply(z);
        return this;
    }


    @Destructive
    @Override
    public @NotNull BlockPositionBuilder calculate(@NotNull BlockPositionBuilder other, @NotNull BiFunction<Integer, Integer, Integer> operator) {
        x = operator.apply(x, other.x);
        y = operator.apply(y, other.y);
        z = operator.apply(z, other.z);
        return this;
    }

    @Destructive
    @Override
    public @NotNull BlockPositionBuilder calculate(@NotNull BlockPositionBuilder other1, @NotNull BlockPositionBuilder other2, @NotNull TriFunction<Integer, Integer, Integer, Integer> operator) {
        x = operator.apply(x, other1.x, other2.x);
        y = operator.apply(y, other1.y, other2.y);
        z = operator.apply(z, other1.z, other2.z);
        return this;
    }

    @Destructive
    @Override
    public @NotNull BlockPositionBuilder add(@NotNull BlockPositionBuilder other) {
        return calculate(other, Integer::sum);
    }

    @Destructive
    @Override
    public @NotNull BlockPositionBuilder subtract(@NotNull BlockPositionBuilder other) {
        return add(other.copy().invert());
    }

    @Destructive
    @Override
    public @NotNull BlockPositionBuilder scale(@NotNull Integer scalar) {
        return calculate(component -> component * scalar);
    }


    @Destructive
    @Override
    public @NotNull BlockPositionBuilder invert() {
        return scale(-1);
    }

    @Destructive
    @Override
    public @NotNull BlockPositionBuilder clamp(@NotNull BlockPositionBuilder min, @NotNull BlockPositionBuilder max) {
        return calculate(min, max, (value, minValue, maxValue) -> Math.max(minValue, Math.min(value, maxValue)));
    }

    public @NotNull String format(@NotNull String format) {
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
        return format("($x, $y, $z)");
    }

    @Override
    public @NotNull BlockPositionBuilder copy() {
        return new BlockPositionBuilder(x, y, z);
    }

    @Override
    public boolean isZero() {
        return equals(new BlockPositionBuilder(0, 0, 0));
    }

    public @NotNull Vector3Builder toDoubleVector() {
        return new Vector3Builder(x, y, z);
    }

    @ApiStatus.Internal
    public @NotNull BlockPos toNMSBlockPos() {
        return new BlockPos(x, y, z);
    }
}
