package com.gmail.subnokoii78.tplcore.commands;

import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NullMarked;

import java.util.Set;
import java.util.UUID;

@NullMarked
public abstract class AbstractCommand {
    private static final Set<UUID> DEVELOPER_IDS = Set.of(
        UUID.fromString("90732c94-ff58-4b4f-884c-d255f0a482ae")
    );

    protected static boolean isDeveloper(CommandSender sender) {
        if (sender instanceof Player player) {
            return DEVELOPER_IDS.contains(player.getUniqueId());
        }
        else return false;
    }

    protected AbstractCommand() {}

    public abstract LiteralCommandNode<CommandSourceStack> getCommandNode();

    protected int failure(CommandSourceStack stack, Throwable cause) {
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
        private CommandExecutionException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}
