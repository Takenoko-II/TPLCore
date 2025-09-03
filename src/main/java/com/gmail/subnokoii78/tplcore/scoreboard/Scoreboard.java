package com.gmail.subnokoii78.tplcore.scoreboard;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.scoreboard.Criteria;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.RenderType;
import org.jetbrains.annotations.NotNull;

public final class Scoreboard {
    private final org.bukkit.scoreboard.Scoreboard bukkitScoreboard;

    private Scoreboard(@NotNull org.bukkit.scoreboard.Scoreboard scoreboard) {
        this.bukkitScoreboard = scoreboard;
    }

    public @NotNull ScoreObjective getObjective(@NotNull String name) {
        final Objective objective = bukkitScoreboard.getObjective(name);

        if (objective == null) {
            throw new IllegalStateException("オブジェクティブ '" + name + "' は存在しません");
        }

        return new ScoreObjective(objective);
    }

    public boolean hasObjective(@NotNull String name) {
        return bukkitScoreboard.getObjective(name)  == null;
    }

    public @NotNull ScoreObjective addObjective(@NotNull String name, @NotNull Criteria criteria, @NotNull Component displayName, @NotNull RenderType renderType) {
        return hasObjective(name) ? getObjective(name) : new ScoreObjective(bukkitScoreboard.registerNewObjective(name, criteria, displayName, renderType));
    }

    public void removeObjective(@NotNull String name) {
        final Objective objective = bukkitScoreboard.getObjective(name);

        if (objective != null) {
            objective.unregister();
        }
    }

    public static final Scoreboard MAIN = new Scoreboard(Bukkit.getScoreboardManager().getMainScoreboard());
}
