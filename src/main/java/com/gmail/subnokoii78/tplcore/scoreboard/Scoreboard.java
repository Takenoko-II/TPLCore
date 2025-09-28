package com.gmail.subnokoii78.tplcore.scoreboard;

import net.kyori.adventure.text.Component;
import net.minecraft.world.scores.criteria.ObjectiveCriteria;
import org.bukkit.scoreboard.Criteria;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.RenderType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public final class Scoreboard {
    private static final Set<Scoreboard> scoreboards = new HashSet<>();

    private final org.bukkit.scoreboard.Scoreboard bukkit;

    public Scoreboard(@NotNull org.bukkit.scoreboard.Scoreboard scoreboard) {
        if (scoreboards.stream().anyMatch(s -> s.bukkit == scoreboard)) {
            throw new IllegalArgumentException("引数に渡されたスコアボードは既に作成済みです");
        }

        this.bukkit = scoreboard;
        Scoreboard.scoreboards.add(this);
    }

    @Override
    public int hashCode() {
        return Objects.hash(bukkit);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Scoreboard that = (Scoreboard) o;
        return Objects.equals(bukkit, that.bukkit);
    }

    public @NotNull ScoreObjective getObjective(@NotNull String name) {
        final Objective objective = bukkit.getObjective(name);

        if (objective == null) {
            throw new IllegalStateException("オブジェクティブ '" + name + "' は存在しません");
        }

        return new ScoreObjective(this, objective);
    }

    public boolean hasObjective(@NotNull String name) {
        return bukkit.getObjective(name) != null;
    }

    public @NotNull ScoreObjective addObjective(@NotNull String name, @Nullable Criteria criteria, @Nullable Component displayName, @Nullable RenderType renderType) {
        if (hasObjective(name)) {
            throw new IllegalStateException("オブジェクティブ '" + name + "' は既に存在します");
        }
        else {
            return new ScoreObjective(this, bukkit.registerNewObjective(
                name,
                criteria == null ? Criteria.DUMMY : criteria,
                displayName == null ? Component.text(name) : displayName,
                renderType == null ? RenderType.INTEGER : renderType
            ));
        }
    }

    public @NotNull ScoreObjective getOrAddObjective(@NotNull String name, @Nullable Criteria criteria, @Nullable Component displayName, @Nullable RenderType renderType) {
        return hasObjective(name) ? getObjective(name) : addObjective(name, criteria, displayName, renderType);
    }

    public void removeObjective(@NotNull String name) {
        final Objective objective = bukkit.getObjective(name);

        if (objective != null) {
            objective.unregister();
        }
    }

    public @NotNull Set<ScoreObjective> getObjectives() {
        return bukkit.getObjectives().stream().map(o -> new ScoreObjective(this, o)).collect(Collectors.toSet());
    }

    public static @NotNull Set<Scoreboard> getScoreboards() {
        return Set.copyOf(scoreboards);
    }

    public static @NotNull ScoreDisplay getDisplay(@NotNull DisplaySlot slot) {
        return new ScoreDisplay(slot);
    }
}
