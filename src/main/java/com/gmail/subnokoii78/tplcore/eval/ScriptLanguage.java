package com.gmail.subnokoii78.tplcore.eval;

import com.gmail.subnokoii78.tplcore.commands.arguments.CommandArgumentableEnumeration;
import com.gmail.subnokoii78.tplcore.eval.groovy.GroovyContext;
import com.gmail.subnokoii78.tplcore.eval.groovy.GroovyEvaluator;
import com.gmail.subnokoii78.tplcore.execute.CommandSourceStack;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.ApiStatus;
import org.jspecify.annotations.NullMarked;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@NullMarked
public enum ScriptLanguage implements CommandArgumentableEnumeration {
    GROOVY {
        @Override
        public ScriptEvaluationResult<?> interpret(io.papermc.paper.command.brigadier.CommandSourceStack stack, String contextId, String script) {
            final GroovyEvaluator evaluator = new GroovyEvaluator(getContext(contextId));
            return evaluator.evaluate(CommandSourceStack.fromPaper(stack), script);
        }

        @Override
        public GroovyContext getContext(String id) {
            return (GroovyContext) super.getContext(id);
        }

        @Override
        public void registerContext(String id, IScriptContext context) {
            if (!(context instanceof GroovyContext)) {
                throw new IllegalArgumentException("Registration failure; Illegal context class: " + context.getClass().getName() + " (expected: " + GroovyContext.class.getName() + ")");
            }
            super.registerContext(id, context);
        }

        @Override
        public Component getDescription() {
            return Component.text("Groovy言語");
        }
    };

    private final Map<String, IScriptContext> contexts = new HashMap<>();

    ScriptLanguage() {

    }

    public IScriptContext getContext(String id) {
        if (contexts.containsKey(id)) {
            return contexts.get(id);
        }
        else {
            throw new IllegalArgumentException("Unknown context id");
        }
    }

    public Set<String> getContextIdList() {
        return Set.copyOf(contexts.keySet());
    }

    public void registerContext(String id, IScriptContext context) {
        if (contexts.containsKey(id)) {
            throw new IllegalArgumentException("Already used context id");
        }
        else {
            contexts.put(id, context);
        }
    }

    @ApiStatus.OverrideOnly
    public ScriptEvaluationResult<?> interpret(io.papermc.paper.command.brigadier.CommandSourceStack stack, String contextId, String script) {
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
