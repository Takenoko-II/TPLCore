package com.gmail.subnokoii78.tplcore.commands.arguments;

import com.gmail.subnokoii78.tplcore.files.LogHistoryType;
import org.jetbrains.annotations.NotNull;

public class LogTypeHistoryArgument extends AbstractEnumerationArgument<LogHistoryType> {

    public static @NotNull LogTypeHistoryArgument logType() {
        return new LogTypeHistoryArgument();
    }

    @Override
    protected @NotNull Class<LogHistoryType> getEnumClass() {
        return LogHistoryType.class;
    }

    @Override
    protected @NotNull String getErrorMessage(String unknownString) {
        return unknownString + " is not valid log type";
    }
}
