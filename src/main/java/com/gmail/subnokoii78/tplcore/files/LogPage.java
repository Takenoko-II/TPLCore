package com.gmail.subnokoii78.tplcore.files;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public record LogPage(@NotNull List<String> texts, @Nullable LogHistoryType type) {
    public static final int ONE_PAGE_LINES_COUNT = 15;
}
