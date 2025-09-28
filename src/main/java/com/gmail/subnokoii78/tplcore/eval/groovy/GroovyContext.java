package com.gmail.subnokoii78.tplcore.eval.groovy;

import com.gmail.subnokoii78.tplcore.TPLCore;
import com.gmail.subnokoii78.tplcore.execute.*;
import com.gmail.subnokoii78.tplcore.random.RandomService;
import com.gmail.subnokoii78.tplcore.random.Xorshift32;
import com.gmail.subnokoii78.tplcore.vector.DualAxisRotationBuilder;
import com.gmail.subnokoii78.tplcore.vector.TripleAxisRotationBuilder;
import com.gmail.subnokoii78.tplcore.vector.Vector3Builder;
import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ItemType;
import org.jspecify.annotations.NullMarked;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

@NullMarked
public class GroovyContext {
    private final Map<String, Function<CommandSourceStack, Object>> variables = new HashMap<>();

    private final Map<String, GroovyMethodOverloads> methods = new HashMap<>();

    public Map<String, Function<CommandSourceStack, Object>> getVariables() {
        return variables;
    }

    public Map<String, GroovyMethodOverloads> getMethods() {
        return methods;
    }

    public GroovyContext putVariable(String name, Object value) {
        variables.put(name, ctx -> value);
        return this;
    }

    public GroovyContext putVariable(String name, Function<CommandSourceStack, Object> valueProvider) {
        variables.put(name, valueProvider);
        return this;
    }

    public GroovyContext putMethod(String name, GroovyMethod<?> method) {
        if (methods.containsKey(name)) {
            methods.get(name).put(method);
        }
        else {
            final GroovyMethodOverloads overloads = new GroovyMethodOverloads();
            overloads.put(method);
            methods.put(name, overloads);
        }

        return this;
    }

    public GroovyContext putClasses(Class<?>... classes) {
        for (final Class<?> clazz : classes) {
            putVariable(clazz.getSimpleName(), clazz);
        }
        return this;
    }

    public static GroovyContext getApiContext() {
        return new GroovyContext()
            .putVariable("server", Bukkit.getServer())
            .putVariable("executor", CommandSourceStack::getExecutorOrNull)
            .putVariable("location", CommandSourceStack::getLocation)
            .putVariable("position", CommandSourceStack::getPosition)
            .putVariable("rotation", CommandSourceStack::getRotation)
            .putVariable("dimension", CommandSourceStack::getDimension)
            .putVariable("sender", CommandSourceStack::getSender)
            .putVariable("scoreboard", TPLCore.getScoreboard())
            .putMethod("execute", GroovyMethod.builder(Execute.class, ctx -> {
                return new Execute(ctx.getStack());
            }))
            .putClasses(
                ItemSlots.class,
                Conditional.class,
                EntityAnchor.class,
                EntitySelector.class,
                SelectorArgument.class,
                Scores.class,
                ScoreComparator.class,
                NumberRange.class,
                DimensionAccess.class,
                ScanMode.class,
                Scores.class,
                Advancements.class,
                LocationGetOption.class
            )
            .putClasses(
                Component.class,
                TextDecoration.class,
                NamedTextColor.class
            )
            .putClasses(
                ItemStack.class,
                Material.class
            )
            .putMethod("itemStack", GroovyMethod.builder(ItemStack.class, ctx -> {
                final Registry<ItemType> registry = RegistryAccess.registryAccess().getRegistry(RegistryKey.ITEM);
                final NamespacedKey key = NamespacedKey.fromString(ctx.getArgument("id", String.class));

                if (key == null) throw new IllegalArgumentException("invalid item id");

                final ItemType itemType = registry.get(key.key());

                if (itemType == null) throw new IllegalArgumentException("unknown item id");

                return itemType.createItemStack(ctx.getArgument("count", Integer.class));
            }).argument("id", String.class).argument("count", Integer.class))
            .putMethod("randomService", GroovyMethod.builder(RandomService.class, ctx -> {
                return new RandomService(new Xorshift32(ctx.getArgument("seed", Integer.class)));
            }).argument("seed", Integer.class))
            .putMethod("players", GroovyMethod.builder(List.class, ctx -> {
                return Bukkit.getOnlinePlayers().stream().toList();
            }))
            .putMethod("vec3", GroovyMethod.builder(Vector3Builder.class, ctx -> {
                return new Vector3Builder(
                    ctx.getArgument("x", Double.class),
                    ctx.getArgument("y", Double.class),
                    ctx.getArgument("z", Double.class)
                );
            }).argument("x", Double.class).argument("y", Double.class).argument("z", Double.class))
            .putMethod("rot2", GroovyMethod.builder(DualAxisRotationBuilder.class, ctx -> {
                return new DualAxisRotationBuilder(
                    ctx.getArgument("yaw", Float.class),
                    ctx.getArgument("pitch", Float.class)
                );
            }).argument("yaw", Float.class).argument("pitch", Float.class))
            .putMethod("rot3", GroovyMethod.builder(TripleAxisRotationBuilder.class, ctx -> {
                return new TripleAxisRotationBuilder(
                    ctx.getArgument("yaw", Float.class),
                    ctx.getArgument("pitch", Float.class),
                    ctx.getArgument("roll", Float.class)
                );
            }).argument("yaw", Float.class).argument("pitch", Float.class).argument("roll", Float.class))
            .putMethod("give", GroovyMethod.builder(Integer.class, ctx -> {
                ctx.getArgument("player", Player.class).getInventory().addItem(ctx.getArgument("itemStack", ItemStack.class));
                return 1;
            }).argument("player", Player.class).argument("itemStack", ItemStack.class))
            .putClasses(SelectorParser.class);
    }
}
