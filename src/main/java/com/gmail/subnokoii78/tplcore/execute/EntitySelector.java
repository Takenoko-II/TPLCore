package com.gmail.subnokoii78.tplcore.execute;

import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NullMarked;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * エンティティセレクターを表現するクラス
 * @param <T> @pや@aは{@link Player}、@eや@nは{@link Entity}、@sは暫定で{@link Entity}
 */
@NullMarked
public final class EntitySelector<T extends Entity> {
    private final List<SelectorArgument> arguments = new ArrayList<>();

    private final Builder<T> builder;

    private EntitySelector(Builder<T> builder) {
        this.builder = builder;
    }

    /**
     * セレクターが単一のエンティティを示すことが保障されていることをテストします。
     * @return @p, @n, @e[limit=1]などはtrue
     */
    public boolean isSingle() {
        for (final SelectorArgument argument : arguments) {
            if (argument.getId().equals(SelectorArgument.LIMIT.getId())) {
                return argument.value.equals(1);
            }
        }

        // limitを持っていないとき↓

        return builder.equals(S) || builder.equals(P) || builder.equals(N);
    }

    void addArgument(SelectorArgument argument) {
        arguments.add(argument);
        arguments.sort((a, b) -> b.getPriority() - a.getPriority());
    }

    /**
     * セレクターに引数を追加します。
     * @param modifier セレクター引数の種類
     * @param value セレクター引数に渡す値
     * @return thisをそのまま返す
     */
    public <U> EntitySelector<T> arg(SelectorArgument.Builder<U> modifier, U value) {
        if (arguments.stream().anyMatch(argument -> {
            if (argument.getId().equals(SelectorArgument.NOT.getId())) return false;
            else return argument.getId().equals(modifier.getId());
        })) {
            throw new IllegalArgumentException("既に指定された引数を使用することはできません");
        }

        addArgument(modifier.build(value));
        return this;
    }

    /**
     * セレクターに負の引数を追加します。
     * @param modifier セレクター引数の種類
     * @param value セレクター引数に渡す値
     * @return thisをそのまま返す
     */
    public <U> EntitySelector<T> notArg(SelectorArgument.Builder<U> modifier, U value) {
        addArgument(SelectorArgument.NOT.build(modifier.build(value)));
        return this;
    }

    public ScoreHolder toScoreHolder() {
        return ScoreHolder.of(this);
    }

    private List<T> modifier(List<T> entities, CommandSourceStack stack) {
        final CommandSourceStack copy = stack.copy();
        List<Entity> out = entities.stream().map(entity -> (Entity) entity).toList();

        // xyz -> sort -> limit の順番
        for (final SelectorArgument modifier : arguments) {
            out = modifier.modify(out, copy);
        }

        // out(List<Entity>)はentities(List<T extends Entity>)をフィルターしたりソートしたものなのでここのキャストでエラーが起こることはない！！！！
        return (List<T>) out;
    }

    List<T> getEntities(CommandSourceStack stack) {
        return builder.selectorSpecificModifier(modifier(builder.getTargetCandidates(stack), stack), stack, arguments);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EntitySelector<?> that = (EntitySelector<?>) o;
        return Objects.equals(arguments, that.arguments);
    }

    @Override
    public int hashCode() {
        return Objects.hash(arguments);
    }

    @Override
    public String toString() {
        return builder.toString() + arguments;
    }

    private static List<Entity> getAllEntities() {
        return Bukkit.getServer().getWorlds().stream()
            .flatMap(world -> world.getEntities().stream())
            .toList();
    }

    private static List<Player> getAllPlayers() {
        return Bukkit.getOnlinePlayers().stream().map(player -> (Player) player).toList();
    }

    /**
     * セレクター「@e」に相当するセレクターのテンプレート
     */
    public static final Builder<Entity> E = new Builder<>() {
        @Override
        List<Entity> getTargetCandidates(CommandSourceStack stack) {
            final List<Entity> entities = EntitySelector.getAllEntities();
            return SelectorSortOrder.ARBITRARY.sort(entities, stack);
        }

        @Override
        List<Entity> selectorSpecificModifier(List<Entity> entities, CommandSourceStack stack, List<SelectorArgument> arguments) {
            return entities;
        }

        @Override
        public String toString() {
            return "@e";
        }
    };

    /**
     * セレクター「@s」に相当するセレクターのテンプレート
     */
    public static final Builder<Entity> S = new Builder<>() {
        @Override
        List<Entity> getTargetCandidates(CommandSourceStack stack) {
            return stack.hasExecutor() ? List.of(stack.getExecutor()) : List.of();
        }

        @Override
        List<Entity> selectorSpecificModifier(List<Entity> entities, CommandSourceStack stack, List<SelectorArgument> arguments) {
            return entities;
        }

        @Override
        public String toString() {
            return "@s";
        }
    };

    /**
     * セレクター「@a」に相当するセレクターのテンプレート
     */
    public static final Builder<Player> A = new Builder<>() {
        @Override
        List<Player> getTargetCandidates(CommandSourceStack stack) {
            final List<Player> players = EntitySelector.getAllPlayers();
            return SelectorSortOrder.ARBITRARY.sort(players, stack);
        }

        @Override
        List<Player> selectorSpecificModifier(List<Player> entities, CommandSourceStack stack, List<SelectorArgument> arguments) {
            return entities;
        }

        @Override
        public String toString() {
            return "@a";
        }
    };

    /**
     * セレクター「@p」に相当するセレクターのテンプレート
     */
    public static final Builder<Player> P = new Builder<>() {
        @Override
        List<Player> getTargetCandidates(CommandSourceStack stack) {
            final List<Player> players = EntitySelector.getAllPlayers()
                .stream()
                .filter(player -> !player.isDead())
                .toList();

            return SelectorSortOrder.NEAREST.sort(players, stack);
        }

        @Override
        List<Player> selectorSpecificModifier(List<Player> entities, CommandSourceStack stack, List<SelectorArgument> arguments) {
            if (arguments.stream().anyMatch(argument -> argument.getId().equals(SelectorArgument.LIMIT.getId()))) {
                return entities;
            }
            else return List.of(entities.getFirst());
        }

        @Override
        public String toString() {
            return "@p";
        }
    };

    /**
     * セレクター「@r」に相当するセレクターのテンプレート
     */
    public static final Builder<Player> R = new Builder<>() {
        @Override
        List<Player> getTargetCandidates(CommandSourceStack stack) {
            final List<Player> players = new ArrayList<>(
                EntitySelector.getAllPlayers()
                    .stream()
                    .filter(player -> !player.isDead())
                    .toList()
            );

            return SelectorSortOrder.RANDOM.sort(players, stack);
        }

        @Override
        List<Player> selectorSpecificModifier(List<Player> entities, CommandSourceStack stack, List<SelectorArgument> arguments) {
            if (arguments.stream().anyMatch(argument -> argument.getId().equals(SelectorArgument.LIMIT.getId()))) {
                return entities;
            }
            else return List.of(entities.getFirst());
        }

        @Override
        public String toString() {
            return "@r";
        }
    };

    /**
     * セレクター「@n」に相当するセレクターのテンプレート
     */
    public static final Builder<Entity> N = new Builder<>() {
        @Override
        List<Entity> getTargetCandidates(CommandSourceStack stack) {
            return SelectorSortOrder.NEAREST.sort(EntitySelector.getAllEntities(), stack);
        }

        @Override
        List<Entity> selectorSpecificModifier(List<Entity> entities, CommandSourceStack stack, List<SelectorArgument> arguments) {
            if (arguments.stream().anyMatch(argument -> argument.getId().equals(SelectorArgument.LIMIT.getId()))) {
                return entities;
            }
            else return List.of(entities.getFirst());
        }

        @Override
        public String toString() {
            return "@n";
        }
    };

    /**
     * 引数未設定の状態のエンティティセレクターを作成するためのクラス
     */
    public static abstract class Builder<T extends Entity> {
        private Builder() {}

        /**
         * セレクタに選択されうるエンティティを取得する
         * @param stack コマンドソーススタック
         * @return たとえば '@a' での実装なら全プレイヤーのリストを返す
         */
        abstract List<T> getTargetCandidates(CommandSourceStack stack);

        /**
         * 受け取ったセレクタ引数の条件のもとセレクタの効果を再編集する
         * @param entities 選択されたエンティティ
         * @param stack コマンドソーススタック
         * @param arguments セレクタ引数のリスト
         * @return 最終結果
         */
        abstract List<T> selectorSpecificModifier(List<T> entities, CommandSourceStack stack, List<SelectorArgument> arguments);

        @Override
        public abstract String toString();

        /**
         * 新しくセレクターを作成します。
         */
        protected EntitySelector<T> build() {
            return new EntitySelector<>(this);
        }

        /**
         * セレクターに引数を追加してセレクターを作成します。
         * @param modifier セレクター引数の種類
         * @param value セレクター引数に渡す値
         * @return 作成されたセレクター
         */
        public <U> EntitySelector<T> arg(SelectorArgument.Builder<U> modifier, U value) {
            return build().arg(modifier, value);
        }

        /**
         * セレクターに負の引数を追加してセレクターを作成します。
         * @param modifier セレクター引数の種類
         * @param value セレクター引数に渡す値
         * @return 作成されたセレクター
         */
        public <U> EntitySelector<T> notArg(SelectorArgument.Builder<U> modifier, U value) {
            return build().notArg(modifier, value);
        }

        public ScoreHolder toScoreHolder() {
            return ScoreHolder.of(this);
        }
    }
}
