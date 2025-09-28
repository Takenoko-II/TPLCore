package com.gmail.subnokoii78.tplcore.commands.arguments;

import com.gmail.subnokoii78.tplcore.eval.ScriptLanguage;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class ScriptLanguageArgument extends AbstractEnumerationArgument<ScriptLanguage> {
    @Override
    protected Class<ScriptLanguage> getEnumClass() {
        return ScriptLanguage.class;
    }

    @Override
    protected String getErrorMessage(String unknownString) {
        return unknownString + " is not a valid script language";
    }

    public static ScriptLanguageArgument scriptLanguage() {
        return new ScriptLanguageArgument();
    }
}
