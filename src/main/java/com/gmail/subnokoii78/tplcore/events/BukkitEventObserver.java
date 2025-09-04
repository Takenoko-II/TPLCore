package com.gmail.subnokoii78.tplcore.events;

import com.gmail.subnokoii78.tplcore.CoreInitializer;
import com.gmail.subnokoii78.tplcore.execute.CommandSourceStack;
import com.gmail.subnokoii78.tplcore.execute.EntitySelector;
import com.gmail.subnokoii78.tplcore.execute.SelectorArgument;
import com.gmail.subnokoii78.tplcore.execute.SourceOrigin;
import com.gmail.subnokoii78.tplcore.schedule.GameTickScheduler;
import com.gmail.subnokoii78.tplcore.schedule.RealTimeScheduler;
import io.papermc.paper.event.player.PrePlayerAttackEntityEvent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityTeleportEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.plugin.Plugin;

import java.util.*;

public class BukkitEventObserver implements Listener {
    public static final BukkitEventObserver OBSERVER = new BukkitEventObserver();

    private static final class TimeStorage {
        private static final Map<Class<? extends Event>, TimeStorage> storages = new HashMap<>();

        private final Map<Player, Long> timeStorage = new HashMap<>();

        private TimeStorage(Class<? extends Event> clazz) {
            if (storages.containsKey(clazz)) {
                throw new IllegalArgumentException();
            }

            storages.put(clazz, this);
        }

        private long getTime(Player player) {
            return timeStorage.getOrDefault(player, 0L);
        }

        private void setTime(Player player) {
            timeStorage.put(player, System.currentTimeMillis());
        }

        private static <T extends Event> TimeStorage getStorage(Class<T> clazz) {
            if (storages.containsKey(clazz)) {
                return storages.get(clazz);
            }
            else {
                return new TimeStorage(clazz);
            }
        }
    }

    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        TimeStorage.getStorage(PlayerDropItemEvent.class).setTime(event.getPlayer());
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        final Player player = event.getPlayer();

        if (event.getAction().isRightClick()) {
            TimeStorage.getStorage(PlayerInteractEvent.class).setTime(player);
            EventHandlerRegistry.getRegistry(TPLEvents.PLAYER_CLICK)
                .call(new PlayerClickEvent(player, event, PlayerClickEvent.Click.RIGHT));

            EventHandlerRegistry.getRegistry(CustomEventType.PLAYER_RIGHT_CLICK).call(new PlayerRightClickEvent(player, event));
            return;
        }

        new RealTimeScheduler(() -> {
            final long dropEventTime = TimeStorage.getStorage(PlayerDropItemEvent.class).getTime(player);
            final long interactEventTime = TimeStorage.getStorage(PlayerInteractEvent.class).getTime(player);

            // ドロップと同時のとき発火しない
            if (System.currentTimeMillis() - dropEventTime < 50L) return;
                // 右クリックと同時のとき発火しない
            else if (System.currentTimeMillis() - interactEventTime < 50L) return;

            new GameTickScheduler(() -> {
                EventHandlerRegistry.getRegistry(TPLEvents.PLAYER_CLICK)
                    .call(new PlayerClickEvent(player, event, PlayerClickEvent.Click.LEFT));
                if (event.getClickedBlock() == null) {
                    getRegistry(CustomEventType.PLAYER_LEFT_CLICK).call(new PlayerLeftClickEvent(event.getPlayer(), event));
                }
                else {
                    getRegistry(CustomEventType.PLAYER_LEFT_CLICK).call(new PlayerLeftClickEvent(event.getPlayer(), event.getClickedBlock(), event));
                }
            }).runTimeout();
        }).runTimeout(8L);
    }

    @EventHandler
    public void onPrePlayerAttack(PrePlayerAttackEntityEvent event) {
        EventHandlerRegistry.getRegistry(CustomEventType.PLAYER_LEFT_CLICK).call(new PlayerLeftClickEvent(event.getPlayer(), event.getAttacked(), event));
    }

    @EventHandler
    public void onEntityTeleport(EntityTeleportEvent event) {
        final Entity entity = event.getEntity();
        final Set<String> tags = entity.getScoreboardTags();

        if (!tags.contains("plugin_api.messenger")) return;

        final EntitySelector<Entity> selector = EntitySelector.E.arg(SelectorArgument.TAG, "plugin_api.target");
        final Set<Entity> targets = new HashSet<>(new CommandSourceStack(SourceOrigin.of(entity)).getEntities(selector));

        final Location location = Objects.requireNonNullElse(event.getTo(), event.getFrom());

        entity.remove();

        for (final String tag : tags) {
            if (!tag.startsWith("plugin_api.json_message")) continue;

            final String message = tag.replaceFirst("^plugin_api\\.json_message\\s+", "");

            try {
                final JSONObject jsonObject = JSONParser.object(message);
                getRegistry(CustomEventType.DATA_PACK_MESSAGE_RECEIVE).call(new DataPackMessageReceiveEvent(location, targets, jsonObject));
            }
            catch (RuntimeException e) {
                return;
            }
        }
    }
}
