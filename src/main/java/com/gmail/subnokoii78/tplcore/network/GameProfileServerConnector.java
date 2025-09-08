package com.gmail.subnokoii78.tplcore.network;

import com.destroystokyo.paper.profile.CraftPlayerProfile;
import com.destroystokyo.paper.profile.PlayerProfile;
import com.gmail.subnokoii78.tplcore.json.JSONParser;
import com.gmail.subnokoii78.tplcore.json.JSONValueTypes;
import com.gmail.subnokoii78.tplcore.json.values.JSONObject;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.UUID;
import java.util.function.Function;

public final class GameProfileServerConnector {
    private static final String PLAYER_PROFILE_URL_PREFIX = "https://sessionserver.mojang.com/session/minecraft/profile/";

    private static final String PLAYER_PROFILE_URL_SUFFIX = "?unsigned=false";

    private final UUID id;

    public GameProfileServerConnector(@NotNull String gamerTag) {
        this.id = Bukkit.getOfflinePlayer(gamerTag).getUniqueId();
    }

    private @NotNull URL getURL() {
        try {
            return URI.create(PLAYER_PROFILE_URL_PREFIX + id + PLAYER_PROFILE_URL_SUFFIX).toURL();
        }
        catch (MalformedURLException e) {
            throw new RuntimeException("不正な形式のURLが作成されました");
        }
    }

    private <T> @NotNull T connect(@NotNull Function<InputStreamReader, T> callback) {
        try {
            final HttpsURLConnection connection = (HttpsURLConnection) getURL().openConnection();
            final InputStreamReader reader = new InputStreamReader(connection.getInputStream());
            final T value = callback.apply(reader);
            reader.close();
            return value;
        }
        catch (IOException e) {
            throw new RuntimeException("接続に失敗しました", e);
        }
    }

    private @NotNull String read() {
        final StringBuilder output = new StringBuilder();

        return connect(reader -> {
            final BufferedReader bufferedReader = new BufferedReader(reader);
            String input;

            while (true) {
                try {
                    if ((input = bufferedReader.readLine()) == null) break;
                }
                catch (IOException e) {
                    throw new RuntimeException("データを読み取れませんでした", e);
                }

                output.append(input);
            }

            if (output.isEmpty()) {
                throw new RuntimeException("データが存在しません");
            }

            return output.toString();
        });
    }

    private @NotNull Property getProperty() {
        try {
            final JSONObject propertyObject = JSONParser.object(read());

            final String value = propertyObject.get("properties[0].value", JSONValueTypes.STRING).getValue();
            final String signature = propertyObject.get("properties[0].signature", JSONValueTypes.STRING).getValue();

            return new Property("textures", value, signature);
        }
        catch (IllegalStateException e) {
            throw new RuntimeException("解析中に無効なデータであることを検知しました", e);
        }
    }

    public @NotNull GameProfile newGameProfile(@NotNull String name) {
        final GameProfile profile = new GameProfile(UUID.randomUUID(), name);
        profile.getProperties().put("textures", getProperty());
        return profile;
    }

    public @NotNull PlayerProfile newPlayerProfile(@NotNull String name) {
        return new CraftPlayerProfile(newGameProfile(name));
    }
}
