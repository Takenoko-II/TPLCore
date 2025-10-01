package com.gmail.subnokoii78.tplcore.network;

import com.gmail.subnokoii78.tplcore.TPLCore;
import com.gmail.subnokoii78.tplcore.itemstack.ItemStackBuilder;
import com.gmail.subnokoii78.tplcore.ui.container.ContainerInteraction;
import com.gmail.subnokoii78.tplcore.ui.container.ItemButton;
import com.gmail.subnokoii78.tplcore.ui.container.ItemButtonClickSound;
import com.gmail.subnokoii78.tplcore.vector.Vector3Builder;
import com.gmail.takenokoii78.json.JSONParser;
import com.gmail.takenokoii78.json.JSONSerializer;
import com.gmail.takenokoii78.json.values.JSONObject;
import com.gmail.takenokoii78.mojangson.MojangsonPath;
import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.serializer.json.JSONComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public final class PaperVelocityManager implements PluginMessageListener {
    private final Set<Consumer<JSONObject>> customPluginMessageReceivers = new HashSet<>();

    public PaperVelocityManager() {}

    @Override
    public void onPluginMessageReceived(@NotNull String subChannel, @NotNull Player player, byte@NotNull[] message) {
        if (subChannel.equals("Forward")) {
            final ByteArrayDataInput input = ByteStreams.newDataInput(message);
            final String channel = input.readUTF();
            if (channel.equals("CustomPluginMessageByPaperVelocityManager")) {
                final byte[] data = new byte[input.readShort()];
                input.readFully(data);
                final DataInputStream dataInputStream = new DataInputStream(new ByteArrayInputStream(data));

                final String jsonString;
                try {
                    jsonString = dataInputStream.readUTF();
                }
                catch (IOException e) {
                    throw new RuntimeException("データの読み取りに失敗しました");
                }

                final JSONObject jsonObject = JSONParser.object(jsonString);

                customPluginMessageReceivers.forEach(receiver -> {
                    receiver.accept(jsonObject);
                });
                return;
            }
        }

        InteractivePluginMessageBuilder.receiveMessage(subChannel, player, message);
    }

    public void getServerOf(@NotNull Player player, @NotNull Consumer<BoAServer> callback) {
        newInteractiveMessage("GetPlayerServer", (input, time) -> {
            input.readUTF();
            final String id = input.readUTF();
            callback.accept(BoAServer.getById(id));
        })
            .argument(player.getName())
            .sendMessage();
    }

    public void getIPAndPort(@NotNull Player player, @NotNull BiConsumer<String, Integer> callback) {
        newInteractiveMessage("IP", (input, time) -> {
            final String ip = input.readUTF();
            final int port = input.readInt();
            callback.accept(ip, port);
        })
            .sendMessage(player);
    }

    public void getServer(@NotNull Consumer<BoAServer> callback) {
        newInteractiveMessage("GetServer", (input, time) -> {
            input.readUTF();
            final String id = input.readUTF();
            callback.accept(BoAServer.getById(id));
        })
            .sendMessage();
    }

    public void transfer(@NotNull Player player, @NotNull PaperVelocityManager.BoAServer serverType) {
        final ByteArrayDataOutput output = ByteStreams.newDataOutput();
        output.writeUTF("Connect");
        output.writeUTF(serverType.id);
        player.sendPluginMessage(TPLCore.getPlugin(), "BungeeCord", output.toByteArray());
        player.sendMessage(Component.text(String.format("%s サーバーへの接続を試行中...", serverType.id)));
        TPLCore.getPlugin().getComponentLogger().info(Component.text(
            String.format("%s (%s) の '%s' サーバーへの転送リクエストが送信されました", player.getName(), player.getUniqueId(), serverType.id)
        ));
    }

    public void kick(@NotNull Player player, @NotNull TextComponent reason) {
        final ByteArrayDataOutput output = ByteStreams.newDataOutput();
        output.writeUTF("KickPlayerRaw");
        output.writeUTF(player.getName());
        output.writeUTF(JSONComponentSerializer.json().serialize(reason));
        player.sendPluginMessage(TPLCore.getPlugin(), "BungeeCord", output.toByteArray());
    }

    public void sendCustomPluginMessage(@NotNull PaperVelocityManager.BoAServer target, @NotNull JSONObject data) {
        final JSONObject message = new JSONObject();

        getServer(server -> {
            message.set("id", UUID.randomUUID().toString());
            message.set("server", server);
            message.set("plugin", TPLCore.getPlugin().getName());
            message.set("timestamp", System.currentTimeMillis());
            message.set("data", data);

            newInteractiveMessage("Forward", (input, time) -> {})
                .argument(target.id)
                .argument("CustomPluginMessageByPaperVelocityManager")
                .argument(out -> {
                    try {
                        out.writeUTF(JSONSerializer.serialize(message));
                    }
                    catch (IOException e) {
                        throw new RuntimeException("データの書き込みに失敗しました");
                    }
                })
                .sendMessageOneWay();
        });
    }

    public void onCustomPluginMessageReceive(@NotNull Consumer<JSONObject> callback) {
        customPluginMessageReceivers.add(callback);
    }

    private final ContainerInteraction SERVER_SELECTOR = new ContainerInteraction(Component.text("Battle of Apostolos"), 1)
        .set(1, ItemButton.item(Material.NETHER_STAR)
            .clickSound(ItemButtonClickSound.BASIC)
            .name(Component.text("Game").color(NamedTextColor.AQUA))
            .lore(Component.text("ゲームサーバーに接続する").color(NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false))
            .onClick(event -> {
                event.close();
                transfer(event.getPlayer(), BoAServer.GAME);
            })
        )
        .set(3, ItemButton.item(Material.PAPER)
            .clickSound(ItemButtonClickSound.BASIC)
            .name(Component.text("Lobby").color(NamedTextColor.GOLD))
            .lore(Component.text("ロビーサーバーに接続する").color(NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false))
            .glint(true)
            .onClick(event -> {
                event.close();
                transfer(event.getPlayer(), BoAServer.LOBBY);
            })
        )
        .set(5, ItemButton.item(Material.COMMAND_BLOCK)
            .clickSound(ItemButtonClickSound.BASIC)
            .name(Component.text("Development").color(NamedTextColor.GOLD))
            .lore(Component.text("開発サーバーに接続する").color(NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false))
            .glint(true)
            .onClick(event -> {
                event.close();
                if (event.getPlayer().isOp()) {
                    transfer(event.getPlayer(), BoAServer.DEVELOPMENT);
                }
                else {
                    event.getPlayer().sendMessage(Component.text("このサーバーへの接続はオペレーター権限が必要です").color(NamedTextColor.RED));
                }
            })
        )
        .set(7, ItemButton.item(Material.RED_BED)
            .clickSound(ItemButtonClickSound.BASIC)
            .name(Component.text("Spawn").color(NamedTextColor.RED))
            .lore(Component.text("スポーン地点に戻る").color(NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false))
            .onClick(event -> {
                final Location spawnPoint = event.getPlayer().getRespawnLocation();
                event.close();
                event.getPlayer().teleport(
                    spawnPoint == null
                        ? event.getPlayer().getWorld().getSpawnLocation().add(new Vector3Builder(0.5, 0.5, 0.5).toBukkitVector())
                        : spawnPoint.add(new Vector3Builder(0.5, 0.5, 0.5).toBukkitVector())
                );
            })
        );

    public @NotNull ItemStack getServerSelectorItemStack() {
        return new ItemStackBuilder(Material.COMPASS)
            .itemName(Component.text("Server Selector").color(NamedTextColor.GREEN))
            .lore(Component.text("Right Click to Open").color(NamedTextColor.GRAY))
            .glint(true)
            .maxStackSize(1)
            .customData(MojangsonPath.of("locked"), (byte) 1)
            .customData(MojangsonPath.of("custom_item_tag"), "server_selector")
            .build();
    }

    public @NotNull ContainerInteraction getServerSelectorInteraction() {
        return SERVER_SELECTOR.copy();
    }

    public void sendDataPackMessage(@NotNull Location location, @NotNull Set<Entity> targets, @NotNull JSONObject message) {
        final Entity messenger = location.getWorld().spawnEntity(location, EntityType.MARKER);
        messenger.addScoreboardTag("plugin_api.messenger");
        messenger.addScoreboardTag("plugin_api.json_message" + ' ' + JSONSerializer.serialize(message));
        targets.forEach(target -> target.addScoreboardTag("plugin_api.target"));
        messenger.teleport(location);
        targets.forEach(target -> target.removeScoreboardTag("plugin_api.target"));
        messenger.remove();
    }

    public static final class InteractivePluginMessageBuilder {
        private static final List<UnreceivedPluginMessage> messageQueue = new ArrayList<>();

        private final Plugin plugin;

        private final ByteArrayDataOutput output = ByteStreams.newDataOutput();

        private final String subChannel;

        private final BiConsumer<ByteArrayDataInput, Long> callback;

        private InteractivePluginMessageBuilder(@NotNull Plugin plugin, @NotNull String subChannel, @NotNull BiConsumer<ByteArrayDataInput, Long> callback) {
            this.plugin = plugin;
            this.output.writeUTF(subChannel);
            this.subChannel = subChannel;
            this.callback = callback;
        }

        public InteractivePluginMessageBuilder argument(@NotNull String value) {
            output.writeUTF(value);
            return this;
        }

        public InteractivePluginMessageBuilder argument(short value) {
            output.writeInt(value);
            return this;
        }

        public InteractivePluginMessageBuilder argument(@NotNull TextComponent textComponent) {
            output.writeUTF(JSONComponentSerializer.json().serialize(textComponent));
            return this;
        }

        public InteractivePluginMessageBuilder argument(@NotNull Consumer<DataOutputStream> writer) {
            final ByteArrayOutputStream data = new ByteArrayOutputStream();
            final DataOutputStream out = new DataOutputStream(data);
            writer.accept(out);
            final byte[] bytes = data.toByteArray();
            output.writeShort(bytes.length);
            output.write(bytes);
            return this;
        }

        public void sendMessage(@NotNull Player player) {
            player.sendPluginMessage(plugin, subChannel, output.toByteArray());
            messageQueue.add(new UnreceivedPluginMessage(subChannel, System.currentTimeMillis(), callback));
        }

        public void sendMessage() {
            Bukkit.getServer().sendPluginMessage(plugin, subChannel, output.toByteArray());
            messageQueue.add(new UnreceivedPluginMessage(subChannel, System.currentTimeMillis(), callback));
        }

        public void sendMessageOneWay() {
            Bukkit.getServer().sendPluginMessage(plugin, subChannel, output.toByteArray());
        }

        private static void receiveMessage(@NotNull String subChannel, @NotNull Player player, byte[] message) {
            for (final UnreceivedPluginMessage pluginMessage : messageQueue) {
                if (pluginMessage.subChannel.equals(subChannel)) {
                    pluginMessage.callback.accept(ByteStreams.newDataInput(message), System.currentTimeMillis() - pluginMessage.time);
                    messageQueue.remove(pluginMessage);
                    return;
                }
            }

            throw new IllegalArgumentException("送信したプラグインメッセージに対応しないレスポンスです");
        }

        public record UnreceivedPluginMessage(@NotNull String subChannel, long time, @NotNull BiConsumer<ByteArrayDataInput, Long> callback) {}
    }

    private InteractivePluginMessageBuilder newInteractiveMessage(@NotNull String subChannel, @NotNull BiConsumer<ByteArrayDataInput, Long> callback) {
        return new InteractivePluginMessageBuilder(TPLCore.getPlugin(), subChannel, callback);
    }

    public enum BoAServer {
        GAME("game"),

        LOBBY("lobby"),

        DEVELOPMENT("develop");

        private final String id;

        BoAServer(@NotNull String id) {
            this.id = id;
        }

        static @NotNull BoAServer getById(@NotNull String id) {
            for (BoAServer value : values()) {
                if (value.id.equals(id)) return value;
            }

            throw new IllegalArgumentException("無効なIDです");
        }
    }
}
