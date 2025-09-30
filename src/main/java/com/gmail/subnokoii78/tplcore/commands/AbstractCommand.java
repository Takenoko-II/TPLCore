package com.gmail.subnokoii78.tplcore.commands;

import com.gmail.subnokoii78.tplcore.TPLCore;
import com.gmail.takenokoii78.json.JSONPath;
import com.gmail.takenokoii78.json.JSONValueTypes;
import com.gmail.takenokoii78.json.values.JSONString;
import com.gmail.takenokoii78.json.values.TypedJSONArray;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NullMarked;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@NullMarked
public abstract class AbstractCommand {
    private static final Set<UUID> ALLOWED_ADMIN_IDS = new HashSet<>();

    protected static int allow(Player player) {
        if (isAllowed(player)) {
            return 0;
        }
        else {
            ALLOWED_ADMIN_IDS.add(player.getUniqueId());
            return 1;
        }
    }

    protected static int disallow(Player player) {
        if (isAllowed(player) && !isDeveloper(player) && !isServerOwner(player)) {
            ALLOWED_ADMIN_IDS.remove(player.getUniqueId());
            return 1;
        }
        else {
            return 0;
        }
    }

    protected static boolean isDeveloper(CommandSender sender) {
        if (sender instanceof Player player) {
            final TypedJSONArray<JSONString> uuids = TPLCore.getPluginConfigLoader().get()
                .get(JSONPath.of("privileges.plugin_developers"), JSONValueTypes.ARRAY)
                .typed(JSONValueTypes.STRING);

            for (JSONString uuid : uuids) {
                if (player.getUniqueId().toString().equals(uuid.getValue())) {
                    return true;
                }
            }
        }
        return false;
    }

    protected static boolean isServerOwner(CommandSender sender) {
        if (sender instanceof Player player) {
            final String uuid = TPLCore.getPluginConfigLoader().get()
                .get(JSONPath.of("privileges.server_owner"), JSONValueTypes.STRING)
                .getValue();

            return player.getUniqueId().toString().equals(uuid);
        }
        else return sender instanceof ConsoleCommandSender;
    }

    protected static boolean isAllowed(CommandSender sender) {
        if (isDeveloper(sender) || isServerOwner(sender)) return true;

        if (sender instanceof Player player) {
            return ALLOWED_ADMIN_IDS.contains(player.getUniqueId());
        }
        else return false;
    }

    protected AbstractCommand() {}

    protected abstract LiteralCommandNode<CommandSourceStack> getCommandNode();

    protected abstract String getDescription();

    protected Set<String> getAliases() {
        return Set.of();
    }

    public void register(Commands registrar) {
        registrar.register(getCommandNode(), getDescription(), getAliases());
    }

    protected int failure(CommandSourceStack stack, Throwable cause) {
        boolean[] clickable = {true};

        final TextComponent.Builder component = Component.text("コマンドの実行に失敗しました: ").color(NamedTextColor.RED)
            .toBuilder()
            .appendNewline()
            .append(Component.text("    "))
            .append(Component.text(
                cause.getMessage() == null
                    ? cause.getClass().getSimpleName()
                    : cause.getMessage()
            ).color(NamedTextColor.RED));

        if (isAllowed(stack.getSender())) {
            component.appendNewline()
                .append(Component.text("    "))
                .append(
                    Component.text("例外 " + cause.getClass().getName() + " をスローする")
                        .color(NamedTextColor.GRAY)
                        .decorate(TextDecoration.UNDERLINED)
                        .hoverEvent(HoverEvent.showText(Component.text("コンソールに赤文字のスタックトレースが送信されます")))
                        .clickEvent(ClickEvent.callback(audience -> {
                            if (clickable[0] && isAllowed(stack.getSender())) {
                                clickable[0] = false;
                                audience.sendMessage(
                                    Component.text("例外をスローしました")
                                );
                                throw new CommandExecutionException("コマンドの実行の失敗ログから例外がスローされました: ", cause);
                            }
                        }))
                );
        }

        stack.getSender().sendMessage(component);
        return 0;
    }

    public static final class CommandExecutionException extends RuntimeException {
        private CommandExecutionException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}
