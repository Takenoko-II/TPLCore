package com.gmail.subnokoii78.tplcore.commands.arguments;

import com.gmail.subnokoii78.tplcore.files.LogMessageType;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class PluginMessageTypeArgument extends AbstractEnumerationArgument<LogMessageType> {
    @Override
    protected Class<LogMessageType> getEnumClass() {
        return LogMessageType.class;
    }

    @Override
    protected String getErrorMessage(String unknownString) {
        return unknownString + " is not valid plugin message type";
    }

    public static PluginMessageTypeArgument pluginMessageType() {
        return new PluginMessageTypeArgument();
    }
}
