package com.gmail.subnokoii78.tplcore.commands.arguments;

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
import org.jspecify.annotations.NullMarked;

import java.lang.reflect.InvocationTargetException;
import java.util.Locale;
import java.util.concurrent.CompletableFuture;

@NullMarked
public abstract class AbstractEnumerationArgument<T extends Enum<T> & CommandArgumentableEnumeration> implements CustomArgumentType.Converted<T, String> {
    private static final DynamicCommandExceptionType ERROR = new DynamicCommandExceptionType(message -> {
        return MessageComponentSerializer.message().serialize(Component.text((String) message));
    });

    protected abstract Class<T> getEnumClass();

    protected abstract String getErrorMessage(String unknownString);

    @Override
    public final T convert(String nativeType) throws CommandSyntaxException {
        try {
            return (T) getEnumClass()
                .getMethod("valueOf", String.class)
                .invoke(null, nativeType.toUpperCase(Locale.ROOT));
        }
        catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException ignored) {
            throw ERROR.create(getErrorMessage(nativeType));
        }
    }

    @Override
    public final <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        final T[] values;
        try {
            values = (T[]) getEnumClass()
                .getMethod("values")
                .invoke(null);
        }
        catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }

        for (final T value : values) {
            final String name = value.toString().toLowerCase(Locale.ROOT);

            // Only suggest if argument value matches the user input
            if (name.startsWith(builder.getRemainingLowerCase())) {
                final Component description = value.getDescription();

                if (description == null) {
                    builder.suggest(name);
                }
                else {
                    builder.suggest(name, MessageComponentSerializer.message().serialize(description));
                }
            }
        }

        return builder.buildFuture();
    }

    @Override
    public final ArgumentType<String> getNativeType() {
        return StringArgumentType.word();
    }
}
