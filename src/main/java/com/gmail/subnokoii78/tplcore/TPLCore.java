package com.gmail.subnokoii78.tplcore;

import com.gmail.subnokoii78.tplcore.events.*;
import com.gmail.subnokoii78.tplcore.network.PaperVelocityManager;
import com.gmail.subnokoii78.tplcore.scoreboard.Scoreboard;
import com.gmail.subnokoii78.tplcore.ui.container.ContainerInteraction;
import com.gmail.takenokoii78.consolecolorizer.Output;
import io.papermc.paper.plugin.bootstrap.PluginBootstrap;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.messaging.Messenger;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public class TPLCore {
    private static Plugin plugin;

    private static PluginBootstrap bootstrap;

    public static final class TPLCoreException extends RuntimeException {
        private TPLCoreException(@NotNull String message) {
            super(message);
        }
    }

    public static final class Events {
        private Events() {}

        public <T extends TPLEvent> int register(@NotNull TPLEventType<T> type, @NotNull Consumer<T> handler) {
            return EventDispatcher.getDispatcher(type).add(handler);
        }

        public <T extends TPLEvent> boolean unregister(@NotNull TPLEventType<T> type, int id) {
            return EventDispatcher.getDispatcher(type).remove(id);
        }
    }

    public static final Events events = new Events();

    public static final Scoreboard scoreboard = new Scoreboard(Bukkit.getScoreboardManager().getMainScoreboard());

    public static final PaperVelocityManager paperVelocityManager = new PaperVelocityManager();

    private TPLCore() {}

    public static @NotNull Plugin getPlugin() throws TPLCoreException {
        if (plugin == null) {
            throw new TPLCoreException("プラグインのインスタンスが用意されていません");
        }
        else {
            return plugin;
        }
    }

    public static @NotNull PluginBootstrap getBootstrap() throws TPLCoreException {
        if (bootstrap == null) {
            throw new TPLCoreException("プラグインのインスタンスが用意されていません");
        }
        else {
            return bootstrap;
        }
    }

    public static void initialize(@NotNull Plugin plugin, @NotNull PluginBootstrap bootstrap) throws TPLCoreException {
        if (TPLCore.plugin == null) {
            TPLCore.plugin = plugin;
            TPLCore.bootstrap = bootstrap;

            final PluginManager manager = Bukkit.getPluginManager();
            manager.registerEvents(BukkitEventObserver.INSTANCE, plugin);
            manager.registerEvents(ContainerInteraction.ContainerEventObserver.INSTANCE, plugin);

            final Messenger messenger = Bukkit.getServer().getMessenger();
            messenger.registerOutgoingPluginChannel(plugin, "BungeeCord");
            messenger.registerIncomingPluginChannel(plugin, "BungeeCord", paperVelocityManager);
        }
        else {
            throw new TPLCoreException("プラグインのインスタンスが既に登録されています");
        }
    }
}

// TODO: ConsoleColorizerをクラスパスに追加