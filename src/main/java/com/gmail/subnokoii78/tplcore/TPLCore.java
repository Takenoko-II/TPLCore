package com.gmail.subnokoii78.tplcore;

import com.gmail.subnokoii78.tplcore.events.*;
import com.gmail.subnokoii78.tplcore.execute.*;
import com.gmail.subnokoii78.tplcore.schedule.GameTickScheduler;
import com.gmail.subnokoii78.tplcore.scoreboard.Scoreboard;
import com.gmail.subnokoii78.tplcore.shape.ParticleSpawner;
import com.gmail.subnokoii78.tplcore.shape.Pentagram;
import com.gmail.subnokoii78.tplcore.shape.ShapeBase;
import com.gmail.subnokoii78.tplcore.shape.ShapeTemplate;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.EntityType;
import org.bukkit.plugin.Plugin;
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

        public <T extends Event> int register(@NotNull EventType<T> type, @NotNull Consumer<T> handler) {
            return EventDispatcher.getDispatcher(type).add(handler);
        }

        public <T extends Event> boolean unregister(@NotNull EventType<T> type, int id) {
            return EventDispatcher.getDispatcher(type).remove(id);
        }
    }

    public static final Events events = new Events();

    public static final Scoreboard scoreboard = Scoreboard.MAIN;

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
        Bukkit.getPluginManager().registerEvents(BukkitEventObserver.INSTANCE, plugin);
    }

    /* TODO
    * - JSONPath
    * - ItemStackBuilder
    * - ContainerInteractionBuilder
    */
}
