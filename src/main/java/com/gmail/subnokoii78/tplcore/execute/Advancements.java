package com.gmail.subnokoii78.tplcore.execute;

import com.gmail.subnokoii78.tplcore.generic.MultiEntriesBuilder;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.advancement.Advancement;
import org.jspecify.annotations.NullMarked;

@NullMarked
public final class Advancements extends MultiEntriesBuilder<Advancement, Boolean, Advancements> {
    private Advancements() {
        super(false);
    }

    public Advancements $(NamespacedKey key, boolean value) {
        final Advancement advancement = Bukkit.getAdvancement(key);
        if (advancement == null) {
            throw new IllegalArgumentException();
        }
        return $(advancement, value);
    }

    public Advancements $(String id, boolean value) {
        final NamespacedKey key = NamespacedKey.fromString(id);
        if (key == null) {
            throw new IllegalArgumentException();
        }
        return $(key, value);
    }

    public static Advancements advancements() {
        return new Advancements();
    }
}
