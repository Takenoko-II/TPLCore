package com.gmail.subnokoii78.tplcore.commands;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.jetbrains.annotations.NotNull;

public abstract class AbstractCommand {
    protected AbstractCommand() {}

    public abstract @NotNull LiteralCommandNode<CommandSourceStack> getCommandNode();

    protected int failure(@NotNull CommandSourceStack stack, @NotNull Throwable cause) {
        boolean[] clickable = {true};

        stack.getSender().sendMessage(
            Component.text("コマンドの実行に失敗しました: ").color(NamedTextColor.RED)
                .appendNewline()
                .append(Component.text("    " + cause).color(NamedTextColor.GRAY))
                .appendNewline()
                .append(Component.text("    "))
                .append(Component.text("例外をスローする").color(NamedTextColor.GOLD).clickEvent(ClickEvent.callback(audience -> {
                    if (clickable[0] && stack.getSender().isOp()) {
                        clickable[0] = false;
                        audience.sendMessage(
                            Component.text("例外をスローしました")
                        );
                        throw new CommandExecutionException("コマンドの実行の失敗ログから例外がスローされました: ", cause);
                    }
                })))

        );
        return 0;
    }

    public static final class CommandExecutionException extends RuntimeException {
        private CommandExecutionException(@NotNull String message, @NotNull Throwable cause) {
            super(message, cause);
        }
    }
}
