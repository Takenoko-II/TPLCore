package com.gmail.subnokoii78.tplcore.commands.arguments;

import com.gmail.subnokoii78.tplcore.files.LogHistoryType;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import io.papermc.paper.command.brigadier.MessageComponentSerializer;
import io.papermc.paper.command.brigadier.argument.CustomArgumentType;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;
import java.util.concurrent.CompletableFuture;

public class LogTypeHistoryArgument implements CustomArgumentType.Converted<LogHistoryType, String> {
    // TODO: ENUM Argument Abstract Class

    private static final DynamicCommandExceptionType ERROR_INVALID_LOG_TYPE = new DynamicCommandExceptionType(type -> {
        return MessageComponentSerializer.message().serialize(Component.text(type + " is not a valid log type"));
    });

    @Override
    public @NotNull LogHistoryType convert(String nativeType) throws CommandSyntaxException {
        try {
            return LogHistoryType.valueOf(nativeType.toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException ignored) {
            throw ERROR_INVALID_LOG_TYPE.create(nativeType);
        }
    }

    @Override
    public <S> @NotNull CompletableFuture<Suggestions> listSuggestions(@NotNull CommandContext<S> context, @NotNull SuggestionsBuilder builder) {
        for (final LogHistoryType logHistoryType : LogHistoryType.values()) {
            final String name = logHistoryType.toString();

            // Only suggest if the log_type name matches the user input
            if (name.startsWith(builder.getRemainingLowerCase())) {
                builder.suggest(name);
            }
        }

        return builder.buildFuture();
    }

    @Override
    public @NotNull ArgumentType<String> getNativeType() {
        return StringArgumentType.word();
    }

    public static @NotNull LogTypeHistoryArgument logType() {
        return new LogTypeHistoryArgument();
    }
}
