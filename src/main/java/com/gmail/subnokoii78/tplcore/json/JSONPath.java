package com.gmail.subnokoii78.tplcore.json;

import com.gmail.subnokoii78.tplcore.json.values.JSONObject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public final class JSONPath<T extends JSONValue<?>> {
    private final List<JSONPathNode<?, ?, ?>> nodes;

    private JSONPath(@NotNull List<JSONPathNode<?, ?, ?>> nodes) {
        this.nodes = nodes;
    }

    public @Nullable T get(@NotNull JSONObject jsonObject) {
        return null;
    }
}
