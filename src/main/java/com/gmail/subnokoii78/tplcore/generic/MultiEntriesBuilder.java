package com.gmail.subnokoii78.tplcore.generic;

import org.jspecify.annotations.NullMarked;

import java.util.Iterator;

@NullMarked
public abstract class MultiEntriesBuilder<K, V, T extends MultiEntriesBuilder<K, V, T>> implements Iterable<MultiMap.Entry<K, V>> {
    private final MultiMap<K, V> map = new MultiMap<>();

    private final boolean duplicatable;

    protected MultiEntriesBuilder(boolean duplicatable) {
        this.duplicatable = duplicatable;
    }

    public final T $(K key, V value) {
        if (!duplicatable && map.containsKey(key)) {
            throw new IllegalArgumentException("キーの重複は不可能です");
        }

        map.put(key, value);
        return (T) this;
    }

    @Override
    public final Iterator<MultiMap.Entry<K,V>> iterator() {
        return map.entries().iterator();
    }
}
