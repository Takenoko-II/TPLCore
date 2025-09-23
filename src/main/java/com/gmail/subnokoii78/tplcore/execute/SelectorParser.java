package com.gmail.subnokoii78.tplcore.execute;

import com.gmail.subnokoii78.tplcore.parse.AbstractParser;
import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NullMarked;

import java.util.*;

@ApiStatus.Experimental
@NullMarked
public final class SelectorParser extends AbstractParser<EntitySelector<?>> {
    private static final Map<String, EntitySelector.Builder<? extends Entity>> SELECTOR_TYPES = new HashMap<>(Map.of(
        "@p", EntitySelector.P,
        "@a", EntitySelector.A,
        "@r", EntitySelector.R,
        "@s", EntitySelector.S,
        "@e", EntitySelector.E,
        "@n", EntitySelector.N
    ));

    private static final char[] SELECTOR_ARGUMENT_BRACES = {'[', ']'};

    private static final Map<String, SelectorArgument.Builder<?>> ARGUMENT_TYPES = new HashMap<>();

    static {
        ARGUMENT_TYPES.put("x", SelectorArgument.X);
        ARGUMENT_TYPES.put("y", SelectorArgument.Y);
        ARGUMENT_TYPES.put("z", SelectorArgument.Z);
        ARGUMENT_TYPES.put("type", SelectorArgument.TYPE);
        ARGUMENT_TYPES.put("name", SelectorArgument.NAME);
        ARGUMENT_TYPES.put("tag", SelectorArgument.TAG);
        ARGUMENT_TYPES.put("distance", SelectorArgument.DISTANCE); // distanceRange()
        ARGUMENT_TYPES.put("sort", SelectorArgument.SORT);
        ARGUMENT_TYPES.put("dxyz", SelectorArgument.DXYZ); // vec3()?
        ARGUMENT_TYPES.put("gamemode", SelectorArgument.GAMEMODE);
        ARGUMENT_TYPES.put("level", SelectorArgument.LEVEL); // levelRange()
        ARGUMENT_TYPES.put("x_rotation", SelectorArgument.X_ROTATION); // rotationRange()
        ARGUMENT_TYPES.put("y_rotation", SelectorArgument.Y_ROTATION); // rotationRange()
        ARGUMENT_TYPES.put("team", SelectorArgument.TEAM);
        ARGUMENT_TYPES.put("advancements", SelectorArgument.ADVANCEMENTS); // advancements()
        ARGUMENT_TYPES.put("scores", SelectorArgument.SCORES); // scores()
        ARGUMENT_TYPES.put("limit", SelectorArgument.LIMIT);
        ARGUMENT_TYPES.put("predicate", SelectorArgument.PREDICATE); // entityPredicate()
    }

    private SelectorParser(@NotNull String text) {
        super(text);
    }

    @Override
    protected Set<Character> getWhitespace() {
        return Set.of(' ', '\n');
    }

    @Override
    protected Set<Character> getQuotes() {
        return Set.of('"');
    }

    @Override
    protected String getTrue() {
        return "true";
    }

    @Override
    protected String getFalse() {
        return "false";
    }

    private @NotNull EntitySelector<? extends Entity> type() {
        if (isOver()) {
            throw exception("type(): isOver");
        }

        final String typeId = expect(true, SELECTOR_TYPES.keySet().toArray(String[]::new));

        return SELECTOR_TYPES.get(typeId).build();
    }

    private @NotNull Set<SelectorArgument> arguments() {
        if (isOver()) {
            throw exception("arguments(): isOver");
        }

        final Set<SelectorArgument> arguments = new HashSet<>();

        expect(true, SELECTOR_ARGUMENT_BRACES[0]);

        do {
            final String argumentId = expect(true, ARGUMENT_TYPES.keySet().toArray(String[]::new));
            final SelectorArgument.Builder<Object> builder = (SelectorArgument.Builder<Object>) ARGUMENT_TYPES.get(argumentId);

            expect(true, '=');

            final boolean not = next(false, '!') != null;

            final SelectorArgument argument = builder.build(
                argumentValue(builder.getArgumentType())
            );

            if (not) {
                arguments.add(SelectorArgument.NOT.build(argument));
            }
            else {
                arguments.add(argument);
            }
        }
        while (next(true, ',') != null);

        expect(true, SELECTOR_ARGUMENT_BRACES[1]);

        return arguments;
    }

    private Object argumentValue(Class<?> clazz) {
        // switchに変えんな
        if (clazz.equals(String.class)) {
            return string(false, ',', ']');
        }
        else if (clazz.equals(Integer.class)) {
            return number(true);
        }
        else if (clazz.equals(Boolean.class)) {
            return bool();
        }
        else if (clazz.equals(Float.class) || clazz.equals(Double.class)) {
            return number(false);
        }
        else if (clazz.equals(EntityType.class)) {
            final NamespacedKey namespacedKey = NamespacedKey.fromString(string(false, ',', ']'));

            if (namespacedKey == null) {
                throw exception("無効なIDです");
            }

            final EntityType type = RegistryAccess.registryAccess().getRegistry(RegistryKey.ENTITY_TYPE).get(namespacedKey);
            if (type == null) {
                throw exception("不明なエンティティタイプです");
            }
            return type;
        }
        else {
            throw new SelectorParseException("パーサーが対応していない型が渡されました");
        }
    }

    @Override
    protected EntitySelector<? extends Entity> parse() {
        final EntitySelector<? extends Entity> selector = type();
        for (final SelectorArgument argument : arguments()) {
            selector.addArgument(argument);
        }

        return selector;
    }

    @ApiStatus.Experimental
    public static @NotNull EntitySelector<? extends Entity> parse(@NotNull String selector) {
        return new SelectorParser(selector).parse();
    }

    static {
        SelectorParser.parse("@e[type=player]");
    }
}
