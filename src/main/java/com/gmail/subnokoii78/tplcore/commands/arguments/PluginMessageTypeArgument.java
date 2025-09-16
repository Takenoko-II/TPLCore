package com.gmail.subnokoii78.tplcore.commands.arguments;

import com.gmail.subnokoii78.tplcore.files.LogMessageType;
import org.jetbrains.annotations.NotNull;

public class PluginMessageTypeArgument extends AbstractEnumerationArgument<LogMessageType> {
    @Override
    protected @NotNull Class<LogMessageType> getEnumClass() {
        return LogMessageType.class;
    }

    @Override
    protected @NotNull String getErrorMessage(String unknownString) {
        return unknownString + " is not valid plugin message type";
    }

    public static @NotNull PluginMessageTypeArgument pluginMessageType() {
        return new PluginMessageTypeArgument();
    }
}
