package com.gmail.subnokoii78.tplcore.eval.groovy;

import com.gmail.subnokoii78.tplcore.execute.*;
import groovy.lang.Binding;
import groovy.lang.GroovyShell;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class GroovyEvaluator {
    private final GroovyContext context;

    public GroovyEvaluator(GroovyContext context) {
        this.context = context;
    }

    private Binding getBinding(CommandSourceStack stack) {
        final Binding binding = new Binding();
        context.getVariables().forEach((name, provider) -> {
            binding.setVariable(name, provider.apply(stack));
        });
        context.getMethods().forEach((name, overloads) -> {
            binding.setVariable(name, overloads.asClosure(stack));
        });
        return binding;
    }

    public ScriptEvaluationResult evaluate(CommandSourceStack stack, String script) {
        final GroovyShell shell = new GroovyShell(getBinding(stack));

        final Object returnValue;
        try {
            returnValue = shell.evaluate(script);
        }
        catch (Exception e) {
            return new ScriptEvaluationResult(false, 0);
        }

        if (returnValue instanceof Integer integer) {
            return new ScriptEvaluationResult(true, integer);
        }
        else {
            return new ScriptEvaluationResult(true, 1);
        }
    }

    public static final class ScriptEvaluationResult {
        public final boolean successful;

        public final int returnValue;

        private ScriptEvaluationResult(boolean successful, int returnValue) {
            this.successful = successful;
            this.returnValue = returnValue;
        }
    }
}
