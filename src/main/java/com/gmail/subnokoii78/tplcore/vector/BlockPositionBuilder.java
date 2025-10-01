package com.gmail.subnokoii78.tplcore.vector;

import com.gmail.subnokoii78.tplcore.generic.TriFunction;
import io.papermc.paper.math.BlockPosition;
import net.minecraft.core.BlockPos;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.jetbrains.annotations.ApiStatus;
import org.jspecify.annotations.NullMarked;

import java.util.function.BiFunction;
import java.util.function.UnaryOperator;

@NullMarked
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
    public BlockPositionBuilder x(int value) {
        x = value;
        return this;
    }


    @Destructive
    public BlockPositionBuilder y(int value) {
        y = value;
        return this;
    }


    @Destructive
    public BlockPositionBuilder z(int value) {
        z = value;
        return this;
    }

    @Override
    public boolean equals(BlockPositionBuilder other) {
        return x == other.x && y == other.y && z == other.z;
    }


    @Destructive
    @Override
    public BlockPositionBuilder calculate(UnaryOperator<Integer> operator) {
        x = operator.apply(x);
        y = operator.apply(y);
        z = operator.apply(z);
        return this;
    }


    @Destructive
    @Override
    public BlockPositionBuilder calculate(BlockPositionBuilder other, BiFunction<Integer, Integer, Integer> operator) {
        x = operator.apply(x, other.x);
        y = operator.apply(y, other.y);
        z = operator.apply(z, other.z);
        return this;
    }

    @Destructive
    @Override
    public BlockPositionBuilder calculate(BlockPositionBuilder other1, BlockPositionBuilder other2, TriFunction<Integer, Integer, Integer, Integer> operator) {
        x = operator.apply(x, other1.x, other2.x);
        y = operator.apply(y, other1.y, other2.y);
        z = operator.apply(z, other1.z, other2.z);
        return this;
    }

    @Destructive
    @Override
    public BlockPositionBuilder add(BlockPositionBuilder other) {
        return calculate(other, Integer::sum);
    }

    @Destructive
    @Override
    public BlockPositionBuilder subtract(BlockPositionBuilder other) {
        return add(other.copy().invert());
    }

    @Destructive
    @Override
    public BlockPositionBuilder scale(Integer scalar) {
        return calculate(component -> component * scalar);
    }


    @Destructive
    @Override
    public BlockPositionBuilder invert() {
        return scale(-1);
    }

    @Destructive
    @Override
    public BlockPositionBuilder clamp(BlockPositionBuilder min, BlockPositionBuilder max) {
        return calculate(min, max, (value, minValue, maxValue) -> Math.max(minValue, Math.min(value, maxValue)));
    }

    public String format(String format) {
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
    public String toString() {
        return format("($x, $y, $z)");
    }

    @Override
    public BlockPositionBuilder copy() {
        return new BlockPositionBuilder(x, y, z);
    }

    @Override
    public boolean isZero() {
        return equals(new BlockPositionBuilder(0, 0, 0));
    }

    public Vector3Builder toDoubleVector() {
        return new Vector3Builder(x, y, z);
    }

    public Block toBlock(World world) {
        return world.getBlockAt(x, y, z);
    }

    @ApiStatus.Internal
    public BlockPos toNMSBlockPos() {
        return new BlockPos(x, y, z);
    }
}
