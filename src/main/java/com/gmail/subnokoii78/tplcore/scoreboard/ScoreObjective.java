package com.gmail.subnokoii78.tplcore.scoreboard;

import io.papermc.paper.scoreboard.numbers.NumberFormat;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Entity;
import org.bukkit.scoreboard.Criteria;
import org.bukkit.scoreboard.Objective;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public final class ScoreObjective {
    private final Scoreboard scoreboard;

    private final Objective bukkit;

    ScoreObjective(@NotNull Scoreboard scoreboard, @NotNull Objective objective) {
        this.scoreboard = scoreboard;
        this.bukkit = objective;
    }

    @Override
    public int hashCode() {
        return Objects.hash(scoreboard, bukkit);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        else if (!(obj instanceof ScoreObjective)) return false;
        else return hashCode() == obj.hashCode();
    }

    public boolean hasScore(@NotNull Entity entity) {
        return bukkit.getScoreFor(entity).isScoreSet();
    }

    public boolean hasScore(@NotNull String name) {
        return bukkit.getScore(name).isScoreSet();
    }

    public int getScore(@NotNull Entity entity) {
        return bukkit.getScoreFor(entity).getScore();
    }

    public int getScore(@NotNull String name) {
        return bukkit.getScore(name).getScore();
    }

    public @NotNull ScoreObjective setScore(@NotNull Entity entity, int value) {
        bukkit.getScoreFor(entity).setScore(value);
        return this;
    }

    public @NotNull ScoreObjective setScore(@NotNull String name, int value) {
        bukkit.getScore(name).setScore(value);
        return this;
    }

    public @NotNull ScoreObjective addScore(@NotNull Entity entity, int value) {
        setScore(entity, getScore(entity) + value);
        return this;
    }

    public @NotNull ScoreObjective addScore(@NotNull String name, int value) {
        setScore(name, getScore(name) + value);
        return this;
    }

    public @NotNull ScoreObjective subtractScore(@NotNull Entity entity, int value) {
        setScore(entity, getScore(entity) - value);
        return this;
    }

    public @NotNull ScoreObjective subtractScore(@NotNull String name, int value) {
        setScore(name, getScore(name) - value);
        return this;
    }

    public @NotNull ScoreObjective multiplyScore(@NotNull Entity entity, int value) {
        setScore(entity, getScore(entity) * value);
        return this;
    }

    public @NotNull ScoreObjective multiplyScore(@NotNull String name, int value) {
        final int subtrahend = getScore(name) * value;
        setScore(name, subtrahend);
        return this;
    }

    public @NotNull ScoreObjective divideScore(@NotNull Entity entity, int value) {
        if (value == 0) return this;
        setScore(entity, getScore(entity) / value);
        return this;
    }

    public @NotNull ScoreObjective divideScore(@NotNull String name, int value) {
        if (value == 0) return this;
        setScore(name, getScore(name) / value);
        return this;
    }

    public @NotNull ScoreObjective resetScore(@NotNull Entity entity) {
        bukkit.getScoreFor(entity).resetScore();
        return this;
    }

    public @NotNull ScoreObjective resetScore(@NotNull String name) {
        bukkit.getScore(name).resetScore();
        return this;
    }

    public @NotNull String getName() {
        return bukkit.getName();
    }

    public @NotNull Criteria getCriteria() {
        return bukkit.getTrackedCriteria();
    }

    public @NotNull Component getDisplayName() {
        return bukkit.displayName();
    }

    public void setDisplayName(@NotNull Component displayName) {
        bukkit.displayName(displayName);
    }

    public boolean isDisplayed() {
        return bukkit.getDisplaySlot() != null;
    }

    public boolean isDisplayedOn(@NotNull ScoreDisplay display) {
        return display.getObjective().equals(this);
    }

    public boolean hasNumberFormat() {
        return bukkit.numberFormat() != null;
    }

    public @NotNull NumberFormat getNumberFormat() {
        return Objects.requireNonNull(bukkit.numberFormat(), "オブジェクティブ '" + bukkit.getName() + "' は数値の書式を持っていません");
    }

    public void setNumberFormat(@NotNull NumberFormat format) {
        bukkit.numberFormat(format);
    }

    public void resetNumberFormat() {
        bukkit.numberFormat(null);
    }

    @NotNull Objective toBukkit() {
        return bukkit;
    }
}
