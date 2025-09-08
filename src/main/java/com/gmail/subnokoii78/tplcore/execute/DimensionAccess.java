package com.gmail.subnokoii78.tplcore.execute;

import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public final class DimensionAccess {
    private static final Map<NamespacedKey, DimensionAccess> INSTANCES = new HashMap<>();

    /**
     * オーバーワールド
     */
    public static final DimensionAccess OVERWORLD = new DimensionAccess(NamespacedKey.minecraft("overworld"));

    /**
     * ネザー
     */
    public static final DimensionAccess THE_NETHER = new DimensionAccess(NamespacedKey.minecraft("nether"));

    /**
     * ジ・エンド
     */
    public static final DimensionAccess THE_END = new DimensionAccess(NamespacedKey.minecraft("the_end"));

    private final NamespacedKey key;

    private DimensionAccess(@NotNull NamespacedKey key) {
        this.key = key;
        INSTANCES.put(key, this);
    }

    /**
     * ディメンションを取得します。
     * @return ディメンション
     * @throws IllegalStateException ディメンションが未生成か、バニラのディメンションではない場合
     */
    public @NotNull World getWorld() throws IllegalStateException {
        final World world = Bukkit.getWorld(key);

        if (world == null) {
            throw new IllegalStateException("ディメンションが見つかりませんでした: 未生成か、存在しない可能性があります");
        }

        return world;
    }

    /**
     * IDを取得します。
     * @return ID
     */
    public @NotNull String getId() {
        return key.toString();
    }

    public static @NotNull Set<DimensionAccess> values() {
        return Set.copyOf(INSTANCES.values());
    }

    /**
     * ディメンションから{@link DimensionAccess}を取得します。
     * @param key ディメンションID
     * @return 対応する {@link DimensionAccess}
     * @throws IllegalArgumentException カスタムディメンションが渡されたとき
     */
    public static @NotNull DimensionAccess of(@NotNull NamespacedKey key) throws IllegalArgumentException {
        if (INSTANCES.containsKey(key)) {
            return INSTANCES.get(key);
        }
        else if (Bukkit.getWorld(key) != null) {
            final DimensionAccess provider = new DimensionAccess(key);
            INSTANCES.put(key, provider);
            return provider;
        }
        else {
            throw new IllegalStateException("ディメンションが見つかりませんでした: 未生成か、存在しない可能性があります");
        }
    }

    /**
     * ディメンションから{@link DimensionAccess}を取得します。
     * @param world ディメンション
     * @return 対応する {@link DimensionAccess}
     * @throws IllegalArgumentException カスタムディメンションが渡されたとき
     */
    public static @NotNull DimensionAccess of(@NotNull World world) throws IllegalArgumentException {
        return of(world.getKey());
    }

    /**
     * IDから{@link DimensionAccess}を取得します。
     * @param id ディメンションID
     * @return 対応する {@link DimensionAccess}
     * @throws IllegalArgumentException カスタムディメンションが渡されたとき
     */
    public static @NotNull DimensionAccess of(@NotNull String id) throws IllegalArgumentException {
        final NamespacedKey key = NamespacedKey.fromString(id);

        if (key == null) {
            throw new IllegalArgumentException("無効なキーです");
        }

        return of(key);
    }
}
