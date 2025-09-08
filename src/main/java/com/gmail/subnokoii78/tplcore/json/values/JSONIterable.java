package com.gmail.subnokoii78.tplcore.json.values;

import com.gmail.subnokoii78.tplcore.json.JSONValue;
import org.jetbrains.annotations.NotNull;

public interface JSONIterable<T extends JSONValue<?>> extends Iterable<T> {
    boolean isEmpty();

    @NotNull JSONIterable<T> copy();
}
