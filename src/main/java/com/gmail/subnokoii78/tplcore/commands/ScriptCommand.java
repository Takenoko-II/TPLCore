package com.gmail.subnokoii78.tplcore.commands;

import com.gmail.subnokoii78.tplcore.commands.arguments.LangTypeArgument;
import com.gmail.subnokoii78.tplcore.eval.LangType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import jdk.jshell.JShell;
import jdk.jshell.MethodSnippet;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Set;
import java.util.UUID;

public class ScriptCommand extends AbstractCommand {
    private static final Set<UUID> DEVELOPER_IDS = Set.of(
        UUID.fromString("90732c94-ff58-4b4f-884c-d255f0a482ae")
    );

    @Override
    protected @NotNull LiteralCommandNode<CommandSourceStack> getCommandNode() {
        return Commands.literal("script")
            .requires(stack -> {
                final CommandSender sender = stack.getSender();

                if (sender instanceof Player player) {
                    return DEVELOPER_IDS.contains(player.getUniqueId());
                }

                return false;
            })
            .then(
                Commands.argument("lang_type", LangTypeArgument.langType())
                    .then(
                        Commands.argument("code", StringArgumentType.string())
                            .executes(ctx -> {
                                switch (ctx.getArgument("lang_type", LangType.class)) {
                                    case JAVA -> {
                                        return executeJava(
                                            ctx.getArgument("code", String.class)
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

    private void parseSignatureString(@NotNull String signature) {

    }

    private int executeJava(@NotNull String code) {
        try (final JShell jShell = JShell.create()) {
            jShell.onSnippetEvent(event -> {
                if (event.snippet() instanceof MethodSnippet method) {

                }
            });
            jShell.eval(code);
        }
        catch (IllegalStateException e) {
            throw new ScriptCommandExecutionException(e);
        }
    }

    public static final class ScriptCommandExecutionException extends RuntimeException {
        public ScriptCommandExecutionException(@NotNull Throwable cause) {
            super(cause);
        }
    }
}
