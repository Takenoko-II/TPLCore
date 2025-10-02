package com.gmail.subnokoii78.tplcore.eval;

import com.gmail.subnokoii78.tplcore.execute.CommandSourceStack;
import org.jspecify.annotations.NullMarked;

@NullMarked
public interface IScriptEvaluator {
    IScriptContext getContext();

    ScriptLanguage.ScriptEvaluationResult<?> evaluate(CommandSourceStack stack, String script);
}
