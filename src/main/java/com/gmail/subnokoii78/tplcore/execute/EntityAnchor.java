package com.gmail.subnokoii78.tplcore.execute;

import com.gmail.subnokoii78.tplcore.vector.Vector3Builder;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * ソーススタックと結びついたエンティティアンカーを表現するクラス
 */
@NullMarked
public final class EntityAnchor {
    private final CommandSourceStack stack;

    private Type type = EntityAnchor.FEET;

    EntityAnchor(CommandSourceStack stack) {
        this.stack = stack;
    }

    /**
     * エンティティアンカーの種類を取得します。
     * @return eyesまたはfeet
     */
    public Type getType() {
        return type;
    }

    void setType(Type provider) {
        this.type = provider;
    }

    /**
     * エンティティアンカーによる実行座標のオフセットを取得します。
     * @return アンカーオフセット
     */
    public Vector3Builder getOffset() {
        return type.getOffset(stack.hasExecutor() ? stack.getExecutor() : null);
    }

    public static abstract class Type {
        private static final Map<String, Type> types = new HashMap<>();

        private final String id;

        protected Type(String id) {
            this.id = id;
            types.put(id, this);
        }

        @Override
        public int hashCode() {
            return Objects.hash(id);
        }

        @Override
        public boolean equals(Object obj) {
            if (obj instanceof Type type) {
                return id.equals(type.id);
            }
            else return false;
        }

        public String getId() {
            return id;
        }

        protected abstract Vector3Builder getOffset(@Nullable Entity entity);

        public static Type get(String id) throws IllegalArgumentException {
            return Objects.requireNonNull(types.get(id), "無効なIDです");
        }
    }

    public static final Type EYES = new Type("eyes") {
        @Override
        protected Vector3Builder getOffset(@Nullable Entity entity) {
            return switch (entity) {
                case LivingEntity livingEntity -> new Vector3Builder(0, livingEntity.getEyeHeight(), 0);
                case null, default -> new Vector3Builder();
            };
        }
    };

    public static final Type FEET = new Type("feet") {
        @Override
        protected Vector3Builder getOffset(@Nullable Entity entity) {
            return new Vector3Builder();
        }
    };
}
