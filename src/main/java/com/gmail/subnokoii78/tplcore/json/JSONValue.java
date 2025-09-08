package com.gmail.subnokoii78.tplcore.json;

import java.util.Objects;

public class JSONValue<T> {
    protected final T value;

    protected JSONValue(T value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        JSONValue<?> jsonValue = (JSONValue<?>) o;
        return Objects.equals(value, jsonValue.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
}
