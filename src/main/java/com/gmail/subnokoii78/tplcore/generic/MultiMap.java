package com.gmail.subnokoii78.tplcore.generic;

import org.jspecify.annotations.NullMarked;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

@NullMarked
public class MultiMap<K, V> {
    private final Map<K, Set<V>> map = new HashMap<>();

    public MultiMap() {}

    public void forEach(BiConsumer<? super K, ? super V> action) {
        for (final Map.Entry<K, Set<V>> entry : map.entrySet()) {
            for (final V value : entry.getValue()) {
                action.accept(entry.getKey(), value);
            }
        }
    }

    public int size() {
        final AtomicInteger size = new AtomicInteger();
        forEach((k, v) -> size.getAndIncrement());
        return size.get();
    }

    public boolean isEmpty() {
        return map.isEmpty();
    }

    public boolean containsKey(K key) {
        return map.containsKey(key);
    }

    public boolean containsValue(V value) {
        final AtomicBoolean contains = new AtomicBoolean();
        forEach((k, v) -> {
            if (v.equals(value)) contains.set(true);
        });
        return contains.get();
    }

    public Set<V> get(K key) {
        final Set<V> set = map.get(key);
        if (set == null) return new HashSet<>();
        else return set;
    }

    public V put(K key, V value) {
        final Set<V> set = get(key);
        set.add(value);
        map.put(key, set);
        return value;
    }

    public Set<V> remove(K key) {
        return map.remove(key);
    }

    public void putAll(Map<? extends K, ? extends V> m) {
        m.forEach(this::put);
    }

    public void putAll(MultiMap<? extends K, ? extends V> m) {
        m.forEach(this::put);
    }

    public void clear() {
        map.clear();
    }

    public Set<K> keySet() {
        return map.keySet();
    }

    public Collection<V> values() {
        return map.values().stream().flatMap(Collection::stream).collect(Collectors.toSet());
    }

    public Set<Map.Entry<K, V>> entrySet() {
        return keySet().stream().flatMap(key -> {
            return get(key).stream().map(value -> {
                return new Map.Entry<K, V>() {
                    @Override
                    public K getKey() {
                        return key;
                    }

                    @Override
                    public V getValue() {
                        return value;
                    }

                    @Override
                    public V setValue(V value) {
                        get(key).remove(value);
                        return value;
                    }
                };
            });
        }).collect(Collectors.toSet());
    }
}
