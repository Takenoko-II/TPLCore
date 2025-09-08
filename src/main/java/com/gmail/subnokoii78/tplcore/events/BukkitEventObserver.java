package com.gmail.subnokoii78.tplcore.events;

import com.gmail.subnokoii78.tplcore.TPLCore;
import com.gmail.subnokoii78.tplcore.execute.CommandSourceStack;
import com.gmail.subnokoii78.tplcore.execute.EntitySelector;
import com.gmail.subnokoii78.tplcore.execute.SelectorArgument;
import com.gmail.subnokoii78.tplcore.execute.SourceOrigin;
import com.gmail.subnokoii78.tplcore.json.JSONParser;
import com.gmail.subnokoii78.tplcore.json.JSONValueTypes;
import com.gmail.subnokoii78.tplcore.json.values.JSONObject;
import com.gmail.subnokoii78.tplcore.schedule.GameTickScheduler;
import com.gmail.subnokoii78.tplcore.schedule.RealTimeScheduler;
import com.gmail.subnokoii78.tplcore.scoreboard.ScoreObjective;
import io.papermc.paper.event.player.PrePlayerAttackEntityEvent;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityTeleportEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.*;

public class BukkitEventObserver implements Listener {
    private BukkitEventObserver() {}

    public static final BukkitEventObserver INSTANCE = new BukkitEventObserver();

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

    @EventHandler(priority = EventPriority.LOW)
    public void onPlayerInteractAtEntity(PlayerInteractAtEntityEvent event) {
        TimeStorage.getStorage(PlayerInteractAtEntityEvent.class).setTime(event.getPlayer());

        new GameTickScheduler(() -> {
            // priorityがLOWだと少し速く発火してしまうのでゲームティックに合わせる
            EventDispatcher.getDispatcher(EventTypes.PLAYER_CLICK)
                .dispatch(new PlayerClickEvent(
                    event.getPlayer(),
                    event,
                    PlayerClickEvent.Click.RIGHT,
                    event.getRightClicked()
                ));
        }).runTimeout();
    }

    @EventHandler
    public void onPrePlayerAttack(PrePlayerAttackEntityEvent event) {
        final Player player = event.getPlayer();
        TimeStorage.getStorage(PrePlayerAttackEntityEvent.class).setTime(player);
        EventDispatcher.getDispatcher(EventTypes.PLAYER_CLICK)
            .dispatch(new PlayerClickEvent(
                event.getPlayer(),
                event,
                PlayerClickEvent.Click.LEFT,
                event.getAttacked()
            ));
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        final Player player = event.getPlayer();
        final Block block = event.getClickedBlock();

        if (event.getAction().isRightClick()) {
            TimeStorage.getStorage(PlayerInteractEvent.class).setTime(player);

            final long interactAtEntityEventTime = TimeStorage.getStorage(PlayerInteractAtEntityEvent.class).getTime(player);

            // エンティティへの右クリックと同時のとき発火しない
            if (System.currentTimeMillis() - interactAtEntityEventTime < 50L) return;

            if (block != null) {
                EventDispatcher.getDispatcher(EventTypes.PLAYER_CLICK)
                    .dispatch(new PlayerClickEvent(
                        player,
                        event,
                        PlayerClickEvent.Click.RIGHT,
                        block
                    ));
            }

            return;
        }

        new RealTimeScheduler(() -> {
            final long dropEventTime = TimeStorage.getStorage(PlayerDropItemEvent.class).getTime(player);
            final long interactEventTime = TimeStorage.getStorage(PlayerInteractEvent.class).getTime(player);
            final long interactAtEntityEventTime = TimeStorage.getStorage(PlayerInteractAtEntityEvent.class).getTime(player);
            final long preAttackTime = TimeStorage.getStorage(PrePlayerAttackEntityEvent.class).getTime(player);

            // ドロップと同時のとき発火しない
            if (System.currentTimeMillis() - dropEventTime < 50L) return;
            // 右クリックと同時のとき発火しない
            else if (System.currentTimeMillis() - interactEventTime < 50L) return;
            // エンティティへの右クリックと同時のとき発火しない
            else if (System.currentTimeMillis() - interactAtEntityEventTime < 50L) return;
            // エンティティへの攻撃と同時のとき発火しない
            else if (System.currentTimeMillis() - preAttackTime < 50L) return;

            if (block != null) {
                new GameTickScheduler(() -> {
                    EventDispatcher.getDispatcher(EventTypes.PLAYER_CLICK)
                        .dispatch(new PlayerClickEvent(
                            player,
                            event,
                            PlayerClickEvent.Click.LEFT,
                            block
                        ));
                }).runTimeout();
            }
        }).runTimeout(8L);
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

                if (!jsonObject.hasKey("id")) {
                    throw new IllegalArgumentException();
                }

                final DatapackMessageReceiveEvent data = new DatapackMessageReceiveEvent(
                    location,
                    targets,
                    jsonObject
                );

                EventDispatcher.getDispatcher(EventTypes.DATAPACK_MESSAGE_RECEIVE).dispatch(data);

                if (!TPLCore.scoreboard.hasObjective("plugin_api.return")) return;
                final ScoreObjective objective = TPLCore.scoreboard.getObjective("plugin_api.return");
                objective.setScore("#", data.getReturnValue());
            }
            catch (RuntimeException e) {
                return;
            }
        }
    }
}
