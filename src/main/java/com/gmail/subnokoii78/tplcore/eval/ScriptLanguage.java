package com.gmail.subnokoii78.tplcore.eval;

import com.gmail.subnokoii78.tplcore.commands.arguments.CommandArgumentableEnumeration;
import com.gmail.subnokoii78.tplcore.eval.groovy.GroovyContext;
import com.gmail.subnokoii78.tplcore.eval.groovy.GroovyEvaluator;
import com.gmail.subnokoii78.tplcore.execute.CommandSourceStack;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.ApiStatus;
import org.jspecify.annotations.NullMarked;

@NullMarked
public enum ScriptLanguage implements CommandArgumentableEnumeration {
    GROOVY {
        public final GroovyEvaluator evaluator = new GroovyEvaluator(GroovyContext.getApiContext());

        @Override
        public ScriptEvaluationResult<?> interpret(io.papermc.paper.command.brigadier.CommandSourceStack stack, String script) {
            return evaluator.evaluate(CommandSourceStack.fromPaper(stack), script);
        }

        @Override
        public GroovyEvaluator getEvaluator() {
            return evaluator;
        }

        @Override
        public Component getDescription() {
            return Component.text("Groovy言語");
        }
    };

    ScriptLanguage() {

    }

    @ApiStatus.OverrideOnly
    public ScriptEvaluationResult<?> interpret(io.papermc.paper.command.brigadier.CommandSourceStack stack, String script) {
        throw new IllegalStateException("OVERRIDE ONLY");
    }

    @ApiStatus.OverrideOnly
    public IScriptEvaluator getEvaluator() {
        throw new IllegalStateException("OVERRIDE ONLY");
    }

    public static abstract class ScriptEvaluationResult<T> {
        protected final boolean successful;

        protected final T resultValue;

        private ScriptEvaluationResult(boolean successful, T resultValue) {
            this.successful = successful;
            this.resultValue = resultValue;
        }

        public boolean isSuccess() {
            return successful;
        }

        public T getResultValue() {
            return resultValue;
        }

        public int getReturnInt() {
            return successful
                ? (resultValue instanceof Number number)
                    ? number.intValue()
                    : 1
                : 0;
        }

        public static final class ScriptEvaluationSuccess<T> extends ScriptEvaluationResult<T> {

            private ScriptEvaluationSuccess(T resultValue) {
                super(true, resultValue);
            }
        }

        public static final class ScriptEvaluationFailure extends ScriptEvaluationResult<Throwable> {

            private ScriptEvaluationFailure(Throwable cause) {
                super(false, cause);
            }
        }

        public static <T> ScriptEvaluationSuccess<T> success(T resultValue) {
            return new ScriptEvaluationSuccess<>(resultValue);
        }

        public static ScriptEvaluationFailure failure(Throwable cause) {
            return new ScriptEvaluationFailure(cause);
        }
    }
}
