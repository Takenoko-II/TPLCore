package com.gmail.subnokoii78.tplcore.json;

import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Map;

public abstract class JSONValueType<T extends JSONValue<?>> {
    protected final Class<T> clazz;

    protected JSONValueType(@NotNull Class<T> clazz) {
        this.clazz = clazz;
    }

    public abstract T cast(Object value) throws IllegalArgumentException;

    @Override
    public String toString() {
        return clazz.getSimpleName();
    }

    public static @NotNull JSONValueType<?> of(Object value) {
        return switch (value) {
            case JSONValue<?> jsonValue -> of(jsonValue.value);
            case Boolean ignored -> JSONValueTypes.BOOLEAN;
            case Number ignored -> JSONValueTypes.NUMBER;
            case String ignored -> JSONValueTypes.STRING;
            case Map<?, ?> ignored -> JSONValueTypes.OBJECT;
            case Collection<?> ignored -> JSONValueTypes.ARRAY;
            case Character ignored -> JSONValueTypes.STRING;
            case null -> JSONValueTypes.NULL;
            default -> {
                if (value.getClass().isArray()) yield JSONValueTypes.ARRAY;
                else throw new IllegalArgumentException("渡された値はjsonで使用できない型です: " + value.getClass().getName());
            }
        };
    }
}
