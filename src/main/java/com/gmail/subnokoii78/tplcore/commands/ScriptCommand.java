package com.gmail.subnokoii78.tplcore.commands;

import com.gmail.subnokoii78.tplcore.commands.arguments.LangTypeArgument;
import com.gmail.subnokoii78.tplcore.eval.LangType;
import com.gmail.subnokoii78.tplcore.vector.DualAxisRotationBuilder;
import com.gmail.subnokoii78.tplcore.vector.Vector3Builder;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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
                                final LangType type = ctx.getArgument("lang_type", LangType.class);
                                final String script = ctx.getArgument("script", String.class);
                                return type.interpret(ctx.getSource(), script);
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

    public static final ScriptCommand SCRIPT_COMMAND = new ScriptCommand();
}
