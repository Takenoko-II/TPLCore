package com.gmail.subnokoii78.tplcore.random;

import org.jetbrains.annotations.NotNull;

public abstract class AbstractCountable<T> {
    protected int count;

    protected final T value;

    protected AbstractCountable(@NotNull T data, int count) {
        this.value = data;
        this.count = count;
    }

    public int getCount() {
        return count;
    }

    public abstract @NotNull AbstractCountable<T> copy();

    public final @NotNull AbstractCountable<T> split(int amount) {
        final AbstractCountable<T> copy = copy();
        copy.count = amount;
        count = count - amount;
        return copy;
    }
}
