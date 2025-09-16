package com.gmail.subnokoii78.tplcore.files;

import com.gmail.subnokoii78.tplcore.TPLCore;
import org.intellij.lang.annotations.RegExp;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Set;
import java.util.regex.Pattern;

public enum LogHistoryType {
    CHAT_HISTORY(Pattern.compile("^$")),

    SERVER_MESSAGE(Pattern.compile("^$")),

    VELOCITY_ACTIVATE(Pattern.compile("^$")),

    COMMAND_EXECUTION(Pattern.compile("^[a-zA-Z0-9_ ]+ issued server command: /.+$")),

    PLAYER_JOIN(Pattern.compile("^[a-zA-Z0-9_ ]+ joined the game$")),

    PLAYER_LEAVE(Pattern.compile("^[a-zA-Z0-9_ ]+ left the game$")),

    PLUGIN_LOG(Pattern.compile("^\\[" + TPLCore.getPlugin().getName() + "] .+$"));

    private final Pattern pattern;

    LogHistoryType(@NotNull Pattern pattern) {
        this.pattern = pattern;
    }

    boolean matches(@NotNull String log) {
        return pattern.matcher(log).matches();
    }
}
