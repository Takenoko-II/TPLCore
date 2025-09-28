package com.gmail.subnokoii78.tplcore.eval;

import com.gmail.subnokoii78.tplcore.commands.arguments.CommandArgumentableEnumeration;
import com.gmail.subnokoii78.tplcore.eval.groovy.GroovyContext;
import com.gmail.subnokoii78.tplcore.eval.groovy.GroovyEvaluator;
import com.gmail.subnokoii78.tplcore.execute.CommandSourceStack;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

public enum ScriptLanguage implements CommandArgumentableEnumeration {
    GROOVY {
        public final GroovyEvaluator evaluator = new GroovyEvaluator(GroovyContext.getApiContext());

        @Override
        public int interpret(@NotNull io.papermc.paper.command.brigadier.CommandSourceStack stack, @NotNull String script) {
            return evaluator.evaluate(CommandSourceStack.fromPaper(stack), script).returnValue;
        }

        @Override
        public Component getDescription() {
            return Component.text("Groovy言語");
        }
    };

    ScriptLanguage() {

    }

    @ApiStatus.OverrideOnly
    public int interpret(@NotNull io.papermc.paper.command.brigadier.CommandSourceStack stack, @NotNull String script) {
        return 0;
    }
}
