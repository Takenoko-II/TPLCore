package com.gmail.subnokoii78.tplcore.commands;

import com.gmail.subnokoii78.tplcore.commands.arguments.LangTypeArgument;
import com.gmail.subnokoii78.tplcore.eval.LangType;
import com.gmail.subnokoii78.tplcore.vector.DualAxisRotationBuilder;
import com.gmail.subnokoii78.tplcore.vector.Vector3Builder;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.util.Set;
import java.util.UUID;

public class ScriptCommand extends AbstractCommand {
    private static final Set<UUID> DEVELOPER_IDS = Set.of(
        UUID.fromString("90732c94-ff58-4b4f-884c-d255f0a482ae")
    );

    @Override
    public @NotNull LiteralCommandNode<CommandSourceStack> getCommandNode() {
        return Commands.literal("script")
            .requires(stack -> {
                final CommandSender sender = stack.getSender();

                if (sender instanceof Player player) {
                    return DEVELOPER_IDS.contains(player.getUniqueId());
                }
                else return sender instanceof ConsoleCommandSender;
            })
            .then(
                Commands.argument("lang_type", LangTypeArgument.langType())
                    .then(
                        Commands.argument("script", StringArgumentType.string())
                            .executes(ctx -> {
                                switch (ctx.getArgument("lang_type", LangType.class)) {
                                    case KOTLIN -> {
                                        return runKotlin(
                                            ctx.getSource(),
                                            ctx.getArgument("script", String.class)
                                        );
                                    }
                                    default -> {
                                        throw new IllegalArgumentException("Invalid lang argument");
                                    }
                                }
                            })
                    )
            )
            .build();
    }

    private static final class ScriptCommandContext {
        @Nullable
        public final Player player;

        public final World dimension;

        public final Vector3Builder position;

        public final DualAxisRotationBuilder rotation;

        public final Server server;

        public ScriptCommandContext(@Nullable Player player, @NotNull World dimension, @NotNull Vector3Builder position, @NotNull DualAxisRotationBuilder rotation) {
            this.player = player;
            this.dimension = dimension;
            this.position = position;
            this.rotation = rotation;
            this.server = Bukkit.getServer();
        }
    }

    private int runKotlin(@NotNull CommandSourceStack stack, @NotNull String script) {
        final ScriptEngine engine = new ScriptEngineManager().getEngineByName("kotlin");

        final Player player;
        if (stack.getExecutor() instanceof Player p) {
            player = p;
        }
        else {
            player = null;
        }

        engine.put("context", new ScriptCommandContext(
            player,
            stack.getLocation().getWorld(),
            Vector3Builder.from(stack.getLocation()),
            DualAxisRotationBuilder.from(stack.getLocation())
        ));
        engine.put("output", System.out);

        final Object result;
        try {
            result = engine.eval(script);
        }
        catch (ScriptException e) {
            return failure(stack, e);
        }

        stack.getSender().sendMessage(
            Component.text("kotlin を実行しました: ")
                .appendNewline()
                .append(Component.text("    " + result))
        );

        if (result instanceof Number number) {
            return (int) number;
        }
        else return 1;
    }

    public static final ScriptCommand SCRIPT_COMMAND = new ScriptCommand();
}
