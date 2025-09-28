package com.gmail.subnokoii78.tplcore.commands;

import com.gmail.subnokoii78.tplcore.commands.arguments.PluginMessageTypeArgument;
import com.gmail.subnokoii78.tplcore.files.LogFile;
import com.gmail.subnokoii78.tplcore.files.LogPage;
import com.gmail.subnokoii78.tplcore.files.LogMessageType;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

@NullMarked
public final class ConsoleCommand extends AbstractCommand {
    private ConsoleCommand() {
        super();
    }

    @Override
    protected String getDescription() {
        return "コンソールの内容の取得・書き込みを行います";
    }

    @Override
    protected LiteralCommandNode<CommandSourceStack> getCommandNode() {
        return Commands.literal("console")
            .requires(stack -> {
                return stack.getSender().isOp() || isDeveloper(stack.getSender());
            })
            .then(
                Commands.literal("query")
                    .then(
                        Commands.argument("page_number", IntegerArgumentType.integer())
                            .then(
                                Commands.argument("words", StringArgumentType.string())
                                    .executes(ctx -> {
                                        return query(
                                            ctx.getSource().getSender(),
                                            ctx.getArgument("page_number", Integer.class),
                                            Arrays.stream(ctx.getArgument("words", String.class).split(",")).collect(Collectors.toSet())
                                        );
                                    })
                            )
                            .executes(ctx -> {
                                return query(
                                    ctx.getSource().getSender(),
                                    ctx.getArgument("page_number", Integer.class),
                                    Set.of()
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
                                    .then(
                                        Commands.argument("sends_chat", BoolArgumentType.bool())
                                            .executes(ctx -> {
                                                return put(
                                                    ctx.getSource().getSender(),
                                                    ctx.getArgument("plugin_message_type", LogMessageType.class),
                                                    ctx.getArgument("message", String.class),
                                                    ctx.getArgument("sends_chat", Boolean.class)
                                                );
                                            })
                                    )
                                    .executes(ctx -> {
                                        return put(
                                            ctx.getSource().getSender(),
                                            ctx.getArgument("plugin_message_type", LogMessageType.class),
                                            ctx.getArgument("message", String.class),
                                            true
                                        );
                                    })
                            )
                    )
            )
            .build();
    }

    private int query(CommandSender sender, int pageNumber, Set<String> words) {
        final LogFile logFile = new LogFile(LogFile.LATEST_LOG_FILE_PATH);

        if (logFile.exists()) {
            final LogPage logPage = logFile.readPage(pageNumber, words);
            final TextComponent.Builder builder = Component.text(
                    words.isEmpty()
                        ? "ログを取得しました (" + logPage.texts().size() + "): "
                        : "検索語句 '" + words + "' を満たすログを取得しました (" + logPage.texts().size() + "): "
                )
                .color(NamedTextColor.WHITE)
                .toBuilder();

            int i = 1;
            for (final String text : logPage.texts()) {
                builder.appendNewline()
                    .append(Component.text(text).color(
                    i == 1 ? NamedTextColor.DARK_GRAY : NamedTextColor.GRAY
                ));
                i *= -1;
            }

            sender.sendMessage(builder.build());
        }

        return Command.SINGLE_SUCCESS;
    }

    private int put(CommandSender sender, LogMessageType type, String message, boolean sendsChat) {
        sender.sendMessage(
            Component.text("ログに書き込みました:")
                .color(NamedTextColor.WHITE)
                .appendNewline()
                .append(Component.text("    " + message).color(NamedTextColor.GRAY))
        );

        if (sendsChat) {
            Bukkit.getServer().sendMessage(type.toDecoratedMessage(message));
        }
        else {
            new LogFile(LogFile.LATEST_LOG_FILE_PATH).write(message, type);
        }

        return Command.SINGLE_SUCCESS;
    }

    public static final ConsoleCommand CONSOLE_COMMAND = new ConsoleCommand();
}
