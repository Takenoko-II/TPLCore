package com.gmail.subnokoii78.tplcore.files;

import org.jetbrains.annotations.NotNull;

import java.util.Set;

public enum LogHistoryType {
    CHAT_HISTORY,

    SERVER_MESSAGE,

    VELOCITY_ACTIVATE,

    COMMAND_EXECUTION("issued", "server", "command"),

    PLAYER_JOIN,

    PLAYER_LEAVE,

    PLUGIN_LOG;

    private final Set<String> triggers;

    LogHistoryType(@NotNull String... triggers) {
        this.triggers = Set.of(triggers);
    }

    boolean matches(@NotNull String log) {
        return triggers.stream().allMatch(log::contains);
    }
}
