package com.gmail.subnokoii78.tplcore.events;

import com.gmail.subnokoii78.tplcore.TPLCore;
import com.gmail.subnokoii78.tplcore.schedule.GameTickScheduler;
import com.gmail.subnokoii78.tplcore.schedule.SystemTimeScheduler;
import io.papermc.paper.event.player.PrePlayerAttackEntityEvent;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityTeleportEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;

import java.util.*;

public class BukkitEventObserver implements Listener {
    private final GameTickScheduler scheduler = new GameTickScheduler(this::onTick);

    private BukkitEventObserver() {
        scheduler.runInterval();
    }

    public static final BukkitEventObserver INSTANCE = new BukkitEventObserver();

    private static final class TimeStorage {
        private static final Map<String, TimeStorage> storages = new HashMap<>();

        private final Map<Player, Long> timeStorage = new HashMap<>();

        /**
         * @param eventId recommended: .class.getName()
         */
        private TimeStorage(String eventId) {
            if (storages.containsKey(eventId)) {
                throw new IllegalArgumentException();
            }

            storages.put(eventId, this);
        }

        private long getTime(Player player) {
            return timeStorage.getOrDefault(player, 0L);
        }

        private void setTime(Player player) {
            timeStorage.put(player, System.currentTimeMillis());
        }

        private static TimeStorage getStorage(String eventId) {
            if (storages.containsKey(eventId)) {
                return storages.get(eventId);
            }
            else {
                return new TimeStorage(eventId);
            }
        }
    }

    public void onTick() {
        TPLCore.events.getDispatcher(TPLEventTypes.TICK).dispatch(new TickEvent(
            Bukkit.getServer().getServerTickManager().isFrozen(),
            Bukkit.getServer().isTickingWorlds()
        ));
    }

    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        TimeStorage.getStorage(PlayerDropItemEvent.class.getName()).setTime(event.getPlayer());
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
        final long lastFiredTime = TimeStorage.getStorage(PlayerInteractEntityEvent.class.getName()).getTime(event.getPlayer());

        // おそらくバグのための2回目の発火を抑制
        if (System.currentTimeMillis() - lastFiredTime < 50L) return;

        TimeStorage.getStorage(PlayerInteractEntityEvent.class.getName()).setTime(event.getPlayer());

        new GameTickScheduler(() -> {
            // priorityがLOWESTだと少し速く発火してしまうのでゲームティックに合わせる
            TPLCore.events.getDispatcher(TPLEventTypes.PLAYER_CLICK)
                .dispatch(new PlayerClickEvent(
                    event.getPlayer(),
                    event.getPlayer().getEquipment() == null ? null : event.getPlayer().getEquipment().getItem(event.getHand()),
                    event,
                    PlayerClickEvent.Click.RIGHT,
                    event.getRightClicked()
                ));
        }).runTimeout();
    }

    @EventHandler
    public void onPrePlayerAttack(PrePlayerAttackEntityEvent event) {
        final Player player = event.getPlayer();
        TimeStorage.getStorage(PrePlayerAttackEntityEvent.class.getName()).setTime(player);
        TPLCore.events.getDispatcher(TPLEventTypes.PLAYER_CLICK)
            .dispatch(new PlayerClickEvent(
                event.getPlayer(),
                event.getPlayer().getEquipment() == null ? null : event.getPlayer().getEquipment().getItem(EquipmentSlot.HAND),
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
            // Paper-APIの仕様上、右クリックのみメインハンドとオフハンドで2回発火するっぽい
            // メインハンド -> オフハンド の順に発火するので、メインハンド側の発火を記録し、時間を見ることでオフハンド側の発火を防ぐ
            if (event.getHand() == EquipmentSlot.HAND) {
                TimeStorage.getStorage("MainHandRightClick").setTime(player);
            }
            else if (event.getHand() == EquipmentSlot.OFF_HAND) {
                final long mainHandRightClickTime = TimeStorage.getStorage("MainHandRightClick").getTime(player);

                if (System.currentTimeMillis() - mainHandRightClickTime < 50L) {
                    return;
                }
            }

            TimeStorage.getStorage(PlayerInteractEvent.class.getName()).setTime(player);

            final long interactEntityEventTime = TimeStorage.getStorage(PlayerInteractEntityEvent.class.getName()).getTime(player);

            // エンティティへの右クリックと同時のとき発火しない
            if (System.currentTimeMillis() - interactEntityEventTime < 50L) return;

            if (block != null) {
                TPLCore.events.getDispatcher(TPLEventTypes.PLAYER_CLICK)
                    .dispatch(new PlayerClickEvent(
                        player,
                        event.getItem(),
                        event,
                        PlayerClickEvent.Click.RIGHT,
                        block
                    ));
            }
            else {
                TPLCore.events.getDispatcher(TPLEventTypes.PLAYER_CLICK)
                    .dispatch(new PlayerClickEvent(
                        player,
                        event.getItem(),
                        event,
                        PlayerClickEvent.Click.RIGHT
                    ));
            }

            return;
        }

        new SystemTimeScheduler(() -> {
            final long dropEventTime = TimeStorage.getStorage(PlayerDropItemEvent.class.getName()).getTime(player);
            final long interactEventTime = TimeStorage.getStorage(PlayerInteractEvent.class.getName()).getTime(player);
            final long interactEntityEventTime = TimeStorage.getStorage(PlayerInteractEntityEvent.class.getName()).getTime(player);
            final long preAttackTime = TimeStorage.getStorage(PrePlayerAttackEntityEvent.class.getName()).getTime(player);

            // ドロップと同時のとき発火しない
            if (System.currentTimeMillis() - dropEventTime < 50L) return;
            // 右クリックと同時のとき発火しない
            else if (System.currentTimeMillis() - interactEventTime < 50L) return;
            // エンティティへの右クリックと同時のとき発火しない
            else if (System.currentTimeMillis() - interactEntityEventTime < 50L) return;
            // エンティティへの攻撃と同時のとき発火しない
            else if (System.currentTimeMillis() - preAttackTime < 50L) return;

            if (block != null) {
                new GameTickScheduler(() -> {
                    TPLCore.events.getDispatcher(TPLEventTypes.PLAYER_CLICK)
                        .dispatch(new PlayerClickEvent(
                            player,
                            event.getItem(),
                            event,
                            PlayerClickEvent.Click.LEFT,
                            block
                        ));
                }).runTimeout();
            }
            else {
                new GameTickScheduler(() -> {
                    TPLCore.events.getDispatcher(TPLEventTypes.PLAYER_CLICK)
                        .dispatch(new PlayerClickEvent(
                            player,
                            event.getItem(),
                            event,
                            PlayerClickEvent.Click.LEFT
                        ));
                }).runTimeout();
            }
        }).runTimeout(8L);
    }

    @EventHandler
    public void onEntityTeleport(EntityTeleportEvent event) {
        final Entity entity = event.getEntity();

        if (entity.getScoreboardTags().contains(PluginApi.MESSENGER_ENTITY_TAG)) {
            TPLCore.pluginApi.broadcast(entity);
        }
    }
}
