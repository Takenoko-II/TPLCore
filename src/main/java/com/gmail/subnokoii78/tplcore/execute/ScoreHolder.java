package com.gmail.subnokoii78.tplcore.execute;

import com.gmail.subnokoii78.tplcore.TPLCore;
import com.gmail.subnokoii78.tplcore.scoreboard.ScoreObjective;
import org.bukkit.entity.Entity;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

/**
 * スコアホルダーを表現するクラス
 */
@NullMarked
public abstract class ScoreHolder {
    protected abstract @Nullable Integer getScore(String objectiveId, CommandSourceStack stack);

    protected abstract void setScore(String objectiveId, CommandSourceStack stack, int value);

    /**
     * 単一のエンティティを示すセレクターからエンティティのスコアホルダーを取得します。
     * @param selector セレクター
     * @return スコアホルダー
     */
    public static ScoreHolder of(EntitySelector<? extends Entity> selector) {
        return new EntityScoreHolder(selector);
    }

    /**
     * 単一のエンティティを示すセレクターからエンティティのスコアホルダーを取得します。
     * @param selector セレクター
     * @return スコアホルダー
     */
    public static ScoreHolder of(EntitySelector.Builder<? extends Entity> selector) {
        return new EntityScoreHolder(selector.build());
    }

    /**
     * 文字列をスコアホルダーとして取得します。
     * @param name フェイクプレイヤー名
     * @return スコアホルダー
     */
    public static ScoreHolder of(String name) {
        return new StringScoreHolder(name);
    }

    /**
     * エンティティをスコアホルダーとして取得します。
     * @param entity エンティティ
     * @return スコアホルダー
     */
    public static ScoreHolder of(Entity entity) {
        return new StringScoreHolder(entity.getUniqueId().toString());
    }

    private static final class EntityScoreHolder extends ScoreHolder {
        private final EntitySelector<? extends Entity> selector;

        private EntityScoreHolder(EntitySelector<? extends Entity> selector) {
            if (selector.isSingle()) {
                this.selector = selector;
            }
            else {
                throw new IllegalArgumentException("セレクターは単一のエンティティを指定する必要があります");
            }
        }

        @Override
        protected @Nullable Integer getScore(String objective, CommandSourceStack stack) {
            if (!TPLCore.getScoreboard().hasObjective(objective)) return null;
            return TPLCore.getScoreboard().getObjective(objective).getScore(stack.getEntities(selector).getFirst());
        }

        @Override
        protected void setScore(String objective, CommandSourceStack stack, int value) {
            if (!TPLCore.getScoreboard().hasObjective(objective)) return;
            final ScoreObjective o = TPLCore.getScoreboard().getObjective(objective);
            stack.getEntities(selector).forEach(entity -> o.setScore(entity, value));
        }
    }

    private static final class StringScoreHolder extends ScoreHolder {
        private final String name;

        private StringScoreHolder(String name) {
            this.name = name;
        }

        @Override
        protected @Nullable Integer getScore(String objective, CommandSourceStack stack) {
            if (!TPLCore.getScoreboard().hasObjective(objective)) return null;
            return TPLCore.getScoreboard().getObjective(objective).getScore(name);
        }

        @Override
        protected void setScore(String objective, CommandSourceStack stack, int value) {
            if (!TPLCore.getScoreboard().hasObjective(objective)) return;
            TPLCore.getScoreboard().getObjective(objective).setScore(name, value);
        }
    }
}
