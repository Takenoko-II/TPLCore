package com.gmail.subnokoii78.tplcore.commands.arguments;

import com.gmail.subnokoii78.tplcore.eval.LangType;
import org.jetbrains.annotations.NotNull;

public class LangTypeArgument extends AbstractEnumerationArgument<LangType> {
    @Override
    protected @NotNull Class<LangType> getEnumClass() {
        return LangType.class;
    }

    @Override
    protected @NotNull String getErrorMessage(String unknownString) {
        return unknownString + " is not valid lang type";
    }

    public static @NotNull LangTypeArgument langType() {
        return new LangTypeArgument();
    }
}
