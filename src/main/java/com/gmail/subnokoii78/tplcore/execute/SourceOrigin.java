package com.gmail.subnokoii78.tplcore.execute;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.block.Block;
import org.bukkit.block.CommandBlock;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Entity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

/**
 * executeコマンドの送信者(実行者ではない)を表現するクラス
 * @param <T> 制限なし
 */
@Deprecated
public abstract class SourceOrigin<T> {
    final T sender;

    private SourceOrigin(@NotNull T sender) {
        this.sender = sender;
    }

    /**
     * 名前を取得します。
     * <br>コマンドブロック以外のブロックに限りIDを返します。
     * @return 送信者名
     */
    public abstract @NotNull Component getDisplayName();

    public abstract @NotNull Location getLocation();

    /**
     * メッセージを送信します。
     * <br>ブロック・サーバーが送信者の場合メッセージは送信できません。
     * @param message メッセージ
     */
    public abstract void sendMessage(@NotNull Component message);

    public <U, V> @Nullable V useOrigin(@NotNull Class<U> clazz, @NotNull Function<U, V> callback) {
        if (clazz.isInstance(sender)) {
            return callback.apply(clazz.cast(sender));
        }
        else return null;
    }

    private static final class EntitySourceOrigin extends SourceOrigin<Entity> {
        private EntitySourceOrigin(@NotNull Entity sender) {
            super(sender);
        }

        @Override
        public @NotNull Component getDisplayName() {
            return sender.name();
        }

        @Override
        public @NotNull Location getLocation() {
            return sender.getLocation();
        }

        @Override
        public void sendMessage(@NotNull Component message) {
            sender.sendMessage(message);
        }
    }

    private static final class BlockSourceOrigin extends SourceOrigin<Block> {
        private BlockSourceOrigin(@NotNull Block sender) {
            super(sender);
        }

        @Override
        public @NotNull Component getDisplayName() {
            if (sender.getState() instanceof CommandBlock commandBlock) {
                return commandBlock.name();
            }

            return Component.translatable(sender.getType().translationKey());
        }

        @Override
        public @NotNull Location getLocation() {
            return sender.getLocation().toCenterLocation();
        }

        @Override
        public void sendMessage(@NotNull Component message) {
            if (sender instanceof CommandBlock commandBlock) {
                commandBlock.lastOutput(message);
            }
        }
    }

    private static final class ConsoleSourceOrigin extends SourceOrigin<ConsoleCommandSender> {
        private ConsoleSourceOrigin(@NotNull ConsoleCommandSender sender) {
            super(sender);
        }

        @Override
        public @NotNull Component getDisplayName() {
            return sender.name();
        }

        @Override
        public @NotNull Location getLocation() {
            return new Location(DimensionAccess.OVERWORLD.getWorld(), 0d, 0d, 0d, 0f, 0f);
        }

        @Override
        public void sendMessage(@NotNull Component message) {
            sender.sendMessage(message);
        }
    }

    private static final class ServerSourceOrigin extends SourceOrigin<Server> {
        private ServerSourceOrigin(@NotNull Server server) {
            super(server);
        }

        @Override
        public @NotNull Component getDisplayName() {
            return Component.text(sender.getName());
        }

        @Override
        public @NotNull Location getLocation() {
            return new Location(DimensionAccess.OVERWORLD.getWorld(), 0d, 0d, 0d, 0f, 0f);
        }

        @Override
        public void sendMessage(@NotNull Component message) {
            Bukkit.getServer().sendMessage(message);
        }
    }

    /**
     * エンティティからコマンド送信者を作成します。
     * @param entity 送信者
     * @return {@link SourceOrigin}
     */
    public static @NotNull SourceOrigin<Entity> of(@NotNull Entity entity) {
        return new EntitySourceOrigin(entity);
    }

    /**
     * ブロックからコマンド送信者を作成します。
     * @param block 送信者
     * @return {@link SourceOrigin}
     */
    public static @NotNull SourceOrigin<Block> of(@NotNull Block block) {
        return new BlockSourceOrigin(block);
    }

    /**
     * コンソールからコマンド送信者を作成します。
     * @param consoleCommandSender 送信者
     * @return {@link SourceOrigin}
     */
    public static @NotNull SourceOrigin<ConsoleCommandSender> of(@NotNull ConsoleCommandSender consoleCommandSender) {
        return new ConsoleSourceOrigin(consoleCommandSender);
    }

    /**
     * サーバーからコマンド送信者を作成します。
     * @param server 送信者
     * @return {@link SourceOrigin}
     */
    public static @NotNull SourceOrigin<Server> of(@NotNull Server server) {
        return new ServerSourceOrigin(server);
    }
}
