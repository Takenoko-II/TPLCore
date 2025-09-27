package com.gmail.subnokoii78.tplcore.eval;

import com.gmail.subnokoii78.tplcore.TPLCore;
import com.gmail.subnokoii78.tplcore.execute.*;
import com.gmail.subnokoii78.tplcore.random.RandomService;
import com.gmail.subnokoii78.tplcore.random.Xorshift32;
import com.gmail.subnokoii78.tplcore.vector.DualAxisRotationBuilder;
import com.gmail.subnokoii78.tplcore.vector.TripleAxisRotationBuilder;
import com.gmail.subnokoii78.tplcore.vector.Vector3Builder;
import groovy.lang.Binding;
import groovy.lang.Closure;
import groovy.lang.GroovyShell;
import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ItemType;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

@NullMarked
public class GroovyEvaluator {
    private final Map<String, Function<CommandSourceStack, @Nullable Object>> variableGenerators = new HashMap<>();

    public GroovyEvaluator(boolean putApi) {
        if (putApi) putApi();
    }

    private void putApi() {
        variableGenerators.put("server", ctx -> Bukkit.getServer());
        variableGenerators.put("executor", CommandSourceStack::getExecutorOrNull);
        variableGenerators.put("location", CommandSourceStack::getLocation);
        variableGenerators.put("position", CommandSourceStack::getPosition);
        variableGenerators.put("rotation", CommandSourceStack::getRotation);
        variableGenerators.put("dimension", CommandSourceStack::getDimension);
        variableGenerators.put("sender", CommandSourceStack::getSender);
        variableGenerators.put("scoreboard", ctx -> TPLCore.getScoreboard());
        variableGenerators.put("execute", ctx -> new Closure<Execute>(null) {
            public Execute doCall() {
                return new Execute();
            }
        });
        variableGenerators.put("itemStack", ctx -> new Closure<ItemStack>(null) {
            public ItemStack doCall(String id, Integer count) {
                final Registry<ItemType> registry = RegistryAccess.registryAccess().getRegistry(RegistryKey.ITEM);
                final NamespacedKey key = NamespacedKey.fromString(id);
                if (key == null) throw new IllegalArgumentException("invalid item id");
                final ItemType itemType = registry.get(key.key());
                if (itemType == null) throw new IllegalArgumentException("unknown item id");
                return itemType.createItemStack(count);
            }
        });
        variableGenerators.put("players", ctx -> new Closure<List<Player>>(null) {
            public List<Player> doCall() {
                return Bukkit.getOnlinePlayers().stream().map(p -> (Player) p).toList();
            }
        });
        variableGenerators.put("teleport", ctx -> new Closure<Boolean>(null) {
            public Boolean doCall(Player player, Location location) {
                return player.teleport(location);
            }
        });
        variableGenerators.put("vector3d", ctx -> new Closure<Vector3Builder>(null) {
            public Vector3Builder doCall(Double x, Double y, Double z) {
                return new Vector3Builder(x, y, z);
            }
        });
        variableGenerators.put("rotation2f", ctx -> new Closure<DualAxisRotationBuilder>(null) {
            public DualAxisRotationBuilder doCall(Float yaw, Float pitch) {
                return new DualAxisRotationBuilder(yaw, pitch);
            }
        });
        variableGenerators.put("rotation3f", ctx -> new Closure<TripleAxisRotationBuilder>(null) {
            public TripleAxisRotationBuilder doCall(Float yaw, Float pitch, Float roll) {
                return new TripleAxisRotationBuilder(yaw, pitch, roll);
            }
        });
        variableGenerators.put("logger", ctx -> TPLCore.getPlugin().getComponentLogger());
        variableGenerators.put("text", ctx -> new Closure<TextComponent>(null) {
            public TextComponent doCall(String text) {
                return Component.text(text);
            }
        });
        variableGenerators.put("EntitySelector", ctx -> EntitySelector.class);
        variableGenerators.put("SelectorArgument", ctx -> SelectorArgument.class);
        variableGenerators.put("Conditional", ctx -> Conditional.class);
        put("ItemSlots", ctx -> ItemSlots.class);
        put("NumberRange", ctx -> NumberRange.class);
        put("DimensionAccess", ctx -> DimensionAccess.class);
        put("EntityAnchor", ctx -> EntityAnchor.class);
        put("give", ctx -> new Closure<Boolean>(null) {
            public Boolean doCall(Player player, ItemStack itemStack) {
                player.getInventory().addItem(itemStack);
                return true;
            }
        });
        put("random", ctx -> new Closure<RandomService>(null) {
            public RandomService doCall(Integer seed) {
                return new RandomService(new Xorshift32(seed));
            }
        });
    }

    public void put(String name, Function<CommandSourceStack, Object> generator) {
        variableGenerators.put(name, generator);
    }

    public ScriptEvaluationResult evaluate(CommandSourceStack stack, String script) {
        final Binding binding = new Binding();

        variableGenerators.forEach((name, generator) -> {
            binding.setVariable(name, generator.apply(stack));
        });

        final GroovyShell shell = new GroovyShell(binding);

        final Object returnValue;
        try {
            returnValue = shell.evaluate(script);
        }
        catch (Exception e) {
            return new ScriptEvaluationResult(false, 0);
        }

        if (returnValue instanceof Integer integer) {
            return new ScriptEvaluationResult(true, integer);
        }
        else {
            return new ScriptEvaluationResult(true, 1);
        }
    }

    public static final class ScriptEvaluationResult {
        public final boolean successful;

        public final int returnValue;

        private ScriptEvaluationResult(boolean successful, int returnValue) {
            this.successful = successful;
            this.returnValue = returnValue;
        }
    }
}
