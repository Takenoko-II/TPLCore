package com.gmail.subnokoii78.tplcore.files;

import com.gmail.subnokoii78.tplcore.commands.arguments.CommandArgumentableEnumeration;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;

public enum LogMessageType implements CommandArgumentableEnumeration {
    ANNOUNCEMENT(NamedTextColor.LIGHT_PURPLE, true),

    INFORMATION(NamedTextColor.AQUA, false),

    TIP(NamedTextColor.GREEN, false),

    WARNING(NamedTextColor.GOLD, false),

    CAUTION(NamedTextColor.RED, true);

    private final TextColor color;

    private final boolean isContentColored;

    LogMessageType(@NotNull TextColor color, boolean isContentColored) {
        this.color = color;
        this.isContentColored = isContentColored;
    }

    public @NotNull TextComponent toDecoratedMessage(@NotNull String message) {
        return Component.text('[').color(NamedTextColor.WHITE)
            .append(
                Component.text(toString().toLowerCase(Locale.ROOT))
                    .color(color)
            )
            .append(
                Component.text(']').color(NamedTextColor.WHITE)
            )
            .appendSpace()
            .append(
                Component.text(message).color(isContentColored ? color : NamedTextColor.WHITE)
            );
    }
}
