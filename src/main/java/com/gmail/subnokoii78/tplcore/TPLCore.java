package com.gmail.subnokoii78.tplcore;

import com.gmail.subnokoii78.tplcore.events.*;
import com.gmail.subnokoii78.tplcore.scoreboard.Scoreboard;
import com.gmail.subnokoii78.tplcore.ui.container.ContainerInteraction;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public class TPLCore {
    private static Plugin plugin;

    public static final class PluginUninitializedException extends RuntimeException {
        private PluginUninitializedException(@NotNull String message) {
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

    private TPLCore() {}

    public static @NotNull Plugin getPlugin() throws PluginUninitializedException {
        if (plugin == null) {
            throw new PluginUninitializedException("プラグインのインスタンスが用意されていません");
        }
        else {
            return plugin;
        }
    }

    public static void initialize(@NotNull Plugin plugin) {
        TPLCore.plugin = plugin;
        final PluginManager manager = Bukkit.getPluginManager();
        manager.registerEvents(BukkitEventObserver.INSTANCE, plugin);
        manager.registerEvents(ContainerInteraction.ContainerEventObserver.INSTANCE, plugin);
    }

    /* TODO
    * - JSONPathNode (入れ子構造にしてルートノードのみJSONPathに保持する) だるい
    * - ItemStackBuilder めんどい
    * - ContainerInteractionBuilder やるー？
    */
}
