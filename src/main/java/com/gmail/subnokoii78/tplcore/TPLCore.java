package com.gmail.subnokoii78.tplcore;

import com.gmail.subnokoii78.tplcore.commands.PrivilegeCommand;
import com.gmail.subnokoii78.tplcore.commands.ScriptCommand;
import com.gmail.subnokoii78.tplcore.eval.ScriptLanguage;
import com.gmail.subnokoii78.tplcore.events.PluginApi;
import com.gmail.subnokoii78.tplcore.commands.ConsoleCommand;
import com.gmail.subnokoii78.tplcore.events.*;
import com.gmail.subnokoii78.tplcore.execute.EntitySelector;
import com.gmail.subnokoii78.tplcore.execute.Execute;
import com.gmail.subnokoii78.tplcore.network.PaperVelocityManager;
import com.gmail.subnokoii78.tplcore.scoreboard.Scoreboard;
import com.gmail.subnokoii78.tplcore.ui.container.ContainerInteraction;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.plugin.bootstrap.PluginBootstrap;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.messaging.Messenger;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

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

    public static final TPLEvents events = new TPLEvents();

    public static final PluginApi pluginApi = new PluginApi();

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
                ConsoleCommand.CONSOLE_COMMAND.register(registrar);
                ScriptCommand.SCRIPT_COMMAND.register(registrar);
                PrivilegeCommand.PRIVILEGE_COMMAND.register(registrar);
            });

            plugin.getComponentLogger().info(Component.text("TPLCore が起動しました"));
        }
        else {
            throw new TPLCoreException("プラグインのインスタンスが既に登録されています");
        }
    }
}
