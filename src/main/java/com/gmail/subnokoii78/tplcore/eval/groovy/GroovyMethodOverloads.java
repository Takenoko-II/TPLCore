package com.gmail.subnokoii78.tplcore.eval.groovy;

import com.gmail.subnokoii78.tplcore.execute.CommandSourceStack;
import groovy.lang.Closure;
import org.jspecify.annotations.NullMarked;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@NullMarked
public class GroovyMethodOverloads {
    private final Set<GroovyMethod<?>> methods = new HashSet<>();

    public GroovyMethodOverloads put(GroovyMethod<?> method) {
        methods.add(method);
        return this;
    }

    public Closure<Object> asClosure(CommandSourceStack stack) {
        return new Closure<Object>(null) {
            public Object doCall(Object... args) {
                GroovyMethod<?> method = null;
                for (GroovyMethod<?> m : methods.stream().sorted((a, b) -> b.length() - a.length()).toList()) {
                    if (m.matches(args)) {
                        method = m;
                        break;
                    }
                }

                if (method == null) {
                    throw new IllegalArgumentException();
                }

                return method.run(stack, args);
            }
        };
    }
}
