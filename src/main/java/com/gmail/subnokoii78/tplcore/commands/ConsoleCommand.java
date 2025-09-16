package com.gmail.subnokoii78.tplcore.commands;

import com.gmail.subnokoii78.tplcore.commands.arguments.PluginMessageTypeArgument;
import com.gmail.subnokoii78.tplcore.files.LogFile;
import com.gmail.subnokoii78.tplcore.files.LogHistoryType;
import com.gmail.subnokoii78.tplcore.files.LogPage;
import com.gmail.subnokoii78.tplcore.files.PluginMessageType;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import com.gmail.subnokoii78.tplcore.commands.arguments.LogTypeHistoryArgument;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public final class ConsoleCommand extends AbstractCommand {
    private ConsoleCommand() {
        super();
    }

    @Override
    public @NotNull LiteralCommandNode<CommandSourceStack> getCommandNode() {
        return Commands.literal("console")
            .then(
                Commands.literal("query")
                    .then(
                        Commands.argument("log_history_type", LogTypeHistoryArgument.logType())
                            .then(
                                Commands.argument("page_number", IntegerArgumentType.integer())
                                    .executes(ctx -> {
                                        return query(
                                            ctx.getSource().getSender(),
                                            ctx.getArgument("log_history_type", LogHistoryType.class),
                                            ctx.getArgument("page_number", Integer.class)
                                        );
                                    })
                            )
                            .executes(ctx -> {
                                return query(
                                    ctx.getSource().getSender(),
                                    ctx.getArgument("log_history_type", LogHistoryType.class),
                                    1
                                );
                            })
                    )
            )
            .then(
                Commands.literal("put")
                    .then(
                        Commands.argument("plugin_message_type", PluginMessageTypeArgument.pluginMessageType())
                            .then(
                                Commands.argument("message", StringArgumentType.string())
                                    .executes(ctx -> {
                                        return put(
                                            ctx.getSource().getSender(),
                                            ctx.getArgument("plugin_message_type", PluginMessageType.class),
                                            ctx.getArgument("message", String.class)
                                        );
                                    })
                            )
                    )
            )
            .build();
    }

    private int query(@NotNull CommandSender sender, @NotNull LogHistoryType logHistoryType, @NotNull Integer pageNumber) {
        final LogFile logFile = new LogFile(LogFile.LATEST_LOG_FILE_PATH);

        if (logFile.exists()) {
            final LogPage logPage = logFile.readPage(pageNumber, logHistoryType);
            final TextComponent.Builder builder = Component.text("ログ '" + logHistoryType.toString().toLowerCase() + "' を取得しました: ")
                .color(NamedTextColor.WHITE)
                .toBuilder();

            int i = 1;
            for (final String text : logPage.texts()) {
                builder.append(Component.text(text).color(
                    i == 1 ? NamedTextColor.WHITE : NamedTextColor.GRAY
                ));
                i *= -1;
            }

            sender.sendMessage(builder.build());
        }

        return Command.SINGLE_SUCCESS;
    }

    private int put(@NotNull CommandSender sender, @NotNull PluginMessageType type, @NotNull String message) {
        sender.sendMessage(
            Component.text("ログに書き込みました:")
                .color(NamedTextColor.WHITE)
                .append(Component.text("    " + message).color(NamedTextColor.GRAY))
        );

        new LogFile(LogFile.LATEST_LOG_FILE_PATH).write(message, type);

        return Command.SINGLE_SUCCESS;
    }

    public static final ConsoleCommand CONSOLE_COMMAND = new ConsoleCommand();
}
