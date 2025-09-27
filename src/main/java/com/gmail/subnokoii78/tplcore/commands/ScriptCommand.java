package com.gmail.subnokoii78.tplcore.commands;

import com.gmail.subnokoii78.tplcore.commands.arguments.LangTypeArgument;
import com.gmail.subnokoii78.tplcore.eval.LangType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import net.kyori.adventure.text.Component;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;
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
                        Commands.argument("script", StringArgumentType.greedyString())
                            .executes(ctx -> {
                                final LangType type = ctx.getArgument("lang_type", LangType.class);
                                final String script = ctx.getArgument("script", String.class);
                                final int returnValue = type.interpret(ctx.getSource(), script);

                                if (returnValue > 0) {
                                    ctx.getSource().getSender().sendMessage(Component.text(
                                        String.format(
                                            "%s の実行に成功しました: %d",
                                            type.name().toLowerCase(Locale.ROOT),
                                            returnValue
                                        )
                                    ));
                                }
                                else {
                                    return failure(ctx.getSource(), new IllegalArgumentException(
                                        String.format("%s の実行に失敗しました", type.name().toLowerCase(Locale.ROOT))
                                    ));
                                }

                                return returnValue;
                            })
                    )
            )
            .build();
    }

    public static final ScriptCommand SCRIPT_COMMAND = new ScriptCommand();
}
