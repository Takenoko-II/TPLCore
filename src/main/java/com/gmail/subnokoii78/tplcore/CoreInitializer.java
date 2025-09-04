package com.gmail.subnokoii78.tplcore;

import com.gmail.subnokoii78.tplcore.events.BukkitEventObserver;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

public class CoreInitializer {
    private static Plugin plugin;

    public static final class PluginUninitializedException extends RuntimeException {
        private PluginUninitializedException(@NotNull String message) {
            super(message);
        }
    }

    private CoreInitializer() {}

    public static @NotNull Plugin getPlugin() throws PluginUninitializedException {
        if (plugin == null) {
            throw new PluginUninitializedException("プラグインのインスタンスが用意されていません");
        }
        else {
            return plugin;
        }
    }

    public static void initialize(@NotNull Plugin plugin) {
        CoreInitializer.plugin = plugin;

        Bukkit.getPluginManager().registerEvents(BukkitEventObserver.OBSERVER, CoreInitializer.getPlugin());
    }
}
