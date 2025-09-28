package com.gmail.subnokoii78.tplcore.commands.arguments;

import net.kyori.adventure.text.Component;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

@NullMarked
public interface CommandArgumentableEnumeration {
    @Nullable
    default Component getDescription() {
        return null;
    }
}
