package com.gmail.subnokoii78.tplcore.commands.arguments;

import com.gmail.subnokoii78.tplcore.files.PluginMessageType;
import org.jetbrains.annotations.NotNull;

public class PluginMessageTypeArgument extends AbstractEnumerationArgument<PluginMessageType> {
    @Override
    protected @NotNull Class<PluginMessageType> getEnumClass() {
        return PluginMessageType.class;
    }

    @Override
    protected @NotNull String getErrorMessage(String unknownString) {
        return unknownString + " is not valid plugin message type";
    }

    public static @NotNull PluginMessageTypeArgument pluginMessageType() {
        return new PluginMessageTypeArgument();
    }
}
