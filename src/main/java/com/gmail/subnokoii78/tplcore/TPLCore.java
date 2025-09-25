package com.gmail.subnokoii78.tplcore;

import com.gmail.subnokoii78.tplcore.commands.ConsoleCommand;
import com.gmail.subnokoii78.tplcore.commands.ScriptCommand;
import com.gmail.subnokoii78.tplcore.events.*;
import com.gmail.subnokoii78.tplcore.network.PaperVelocityManager;
import com.gmail.subnokoii78.tplcore.scoreboard.Scoreboard;
import com.gmail.subnokoii78.tplcore.ui.container.ContainerInteraction;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.datapack.Datapack;
import io.papermc.paper.datapack.PaperDatapack;
import io.papermc.paper.plugin.bootstrap.PluginBootstrap;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minecraft.server.packs.repository.Pack;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.messaging.Messenger;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.lang.reflect.Field;
import java.util.function.Consumer;

@NullMarked
public class TPLCore {
    @Nullable
    private static Plugin plugin;

    @Nullable
    private static PluginBootstrap bootstrap;

    public static final class TPLCoreException extends RuntimeException {
        private TPLCoreException(String message) {
            super(message);
        }
    }

    public static final class Events {
        private Events() {}

        public <T extends TPLEvent> int register(TPLEventType<T> type, Consumer<T> handler) {
            return EventDispatcher.getDispatcher(type).add(handler);
        }

        public <T extends TPLEvent> boolean unregister(TPLEventType<T> type, int id) {
            return EventDispatcher.getDispatcher(type).remove(id);
        }
    }

    public static final Events events = new Events();

    @Nullable
    private static Scoreboard scoreboard;

    public static final PaperVelocityManager paperVelocityManager = new PaperVelocityManager();

    private TPLCore() {}

    public static Plugin getPlugin() throws TPLCoreException {
        if (plugin == null) {
            throw new TPLCoreException("プラグインのインスタンスが用意されていません");
        }
        else {
            return plugin;
        }
    }

    public static PluginBootstrap getBootstrap() throws TPLCoreException {
        if (bootstrap == null) {
            throw new TPLCoreException("ブートストラップのインスタンスが用意されていません");
        }
        else {
            return bootstrap;
        }
    }

    public static String getDatapackId(Datapack datapack) {
        if (!(datapack instanceof PaperDatapack paperDatapack)) {
            throw new IllegalArgumentException(PaperDatapack.class.getName() + " へのキャストに失敗しました");
        }

        final Pack pack;
        try {
            final Field field = PaperDatapack.class.getDeclaredField("pack");
            field.setAccessible(true);
            pack = (Pack) field.get(paperDatapack);
        }
        catch (NoSuchFieldException | IllegalAccessException e) {
            throw new IllegalStateException("リフレクションの実行に失敗しました");
        }

        return pack.getId();
    }

    public static Scoreboard getScoreboard() throws TPLCoreException {
        if (scoreboard == null) {
            throw new TPLCoreException("スコアボードのインスタンスが用意されていません");
        }
        else {
            return scoreboard;
        }
    }

    public static void initialize(Plugin plugin, PluginBootstrap bootstrap) throws TPLCoreException {
        if (TPLCore.plugin == null) {
            TPLCore.plugin = plugin;
            TPLCore.bootstrap = bootstrap;
            TPLCore.scoreboard = new Scoreboard(Bukkit.getScoreboardManager().getMainScoreboard());

            final PluginManager manager = Bukkit.getPluginManager();
            manager.registerEvents(BukkitEventObserver.INSTANCE, plugin);
            manager.registerEvents(ContainerInteraction.ContainerEventObserver.INSTANCE, plugin);

            final Messenger messenger = Bukkit.getServer().getMessenger();
            messenger.registerOutgoingPluginChannel(plugin, "BungeeCord");
            messenger.registerIncomingPluginChannel(plugin, "BungeeCord", paperVelocityManager);

            plugin.getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, event -> {
                final Commands registrar = event.registrar();
                registrar.register(ConsoleCommand.CONSOLE_COMMAND.getCommandNode());
                // registrar.register(ScriptCommand.SCRIPT_COMMAND.getCommandNode());
            });

            plugin.getComponentLogger().info(Component.text("TPLCore が起動しました").color(NamedTextColor.BLUE));
        }
        else {
            throw new TPLCoreException("プラグインのインスタンスが既に登録されています");
        }
    }
}
