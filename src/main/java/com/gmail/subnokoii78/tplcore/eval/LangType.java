package com.gmail.subnokoii78.tplcore.eval;

import com.gmail.subnokoii78.tplcore.execute.CommandSourceStack;
import org.jetbrains.annotations.NotNull;

public enum LangType {
    GROOVY {
        @Override
        public int interpret(@NotNull io.papermc.paper.command.brigadier.CommandSourceStack stack, @NotNull String script) {
            final GroovyEvaluator evaluator = new GroovyEvaluator(true);
            return evaluator.evaluate(CommandSourceStack.fromPaper(stack), script).returnValue;
        }
    };

    LangType() {

    }

    public int interpret(@NotNull io.papermc.paper.command.brigadier.CommandSourceStack stack, @NotNull String script) {
        return 0;
    }
}
