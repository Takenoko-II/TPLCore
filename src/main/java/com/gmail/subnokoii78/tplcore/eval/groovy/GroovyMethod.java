package com.gmail.subnokoii78.tplcore.eval.groovy;

import com.gmail.subnokoii78.tplcore.execute.CommandSourceStack;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

@NullMarked
public class GroovyMethod<R> {
    private final Class<R> returns;

    private final Function<MethodContext, R> callback;

    private final List<ArgumentDefinition> arguments = new ArrayList<>();

    private boolean includesVarArg = false;

    public int length() {
        return arguments.size();
    }

    public boolean matches(Object[] args) {
        return collectArguments(args) != null;
    }

    private GroovyMethod(Class<R> returns, Function<MethodContext, R> callback) {
        this.returns = returns;
        this.callback = callback;
    }

    @Override
    public int hashCode() {
        return Objects.hash(includesVarArg, Arrays.hashCode(arguments.stream().map(e -> e.clazz).toArray()));
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        else if (obj.getClass() != getClass()) return false;
        else return obj.hashCode() == hashCode();
    }

    public GroovyMethod<R> argument(String name, Class<?> clazz) {
        arguments.add(new ArgumentDefinition(name, clazz));
        return this;
    }

    public GroovyMethod<R> setIsLastVarArg(boolean f) {
        includesVarArg = f;
        return this;
    }

    @Nullable
    private List<PassedArgument> collectArguments(Object[] args) {
        final List<PassedArgument> passedArguments = new ArrayList<>();

        if (includesVarArg) {
            if (args.length < arguments.size() - 1) {
                return null;
            }

            for (int i = 0; i < arguments.size() - 1; i++) {
                final ArgumentDefinition argument = arguments.get(i);

                if (!argument.clazz.isInstance(args[i])) {
                    return null;
                }

                passedArguments.add(new PassedArgument(argument.name, argument.clazz, argument.clazz.cast(args[i]), false));
            }

            if (args.length == arguments.size() - 1) return passedArguments;

            final ArgumentDefinition last = arguments.getLast();
            final Object[] remainings = Arrays.copyOfRange(args, arguments.size() - 1, args.length);

            for (final Object r : remainings) {
                if (!last.clazz.isInstance(r)) {
                    return null;
                }
            }

            passedArguments.add(new PassedArgument(last.name, last.clazz, Arrays.stream(remainings).toList(), true));
        }
        else {
            for (int i = 0; i < arguments.size(); i++) {
                final ArgumentDefinition argument = arguments.get(i);

                if (!argument.clazz.isInstance(args[i])) {
                    return null;
                }

                passedArguments.add(new PassedArgument(argument.name, argument.clazz, argument.clazz.cast(args[i]), false));
            }
        }

        return  passedArguments;
    }

    public R run(CommandSourceStack stack, Object... args) {
        final List<GroovyMethod.PassedArgument> passedArguments = collectArguments(args);

        if (passedArguments == null) {
            throw new IllegalArgumentException();
        }

        final MethodContext context = new MethodContext(stack, passedArguments);

        return callback.apply(context);
    }

    public static <R> GroovyMethod<R> builder(Class<R> returns, Function<MethodContext, R> callback) {
        return new GroovyMethod<>(returns, callback);
    }

    private record ArgumentDefinition(String name, Class<?> clazz) {}

    private record PassedArgument(String name, Class<?> clazz, Object value, boolean isVarArg) {}

    public static final class MethodContext {
        private final CommandSourceStack stack;

        private final List<PassedArgument> arguments;

        private MethodContext(CommandSourceStack stack, List<PassedArgument> passedArguments) {
            this.stack = stack;
            this.arguments = passedArguments;
        }

        public CommandSourceStack getStack() {
            return stack;
        }

        public <T> T getArgument(String name, Class<T> clazz) {
            for (final PassedArgument argument : arguments) {
                if (!argument.name.equals(name)) {
                    continue;
                }

                if (argument.isVarArg) {
                    if (!clazz.isArray()) {
                        throw new IllegalArgumentException();
                    }

                    if (!clazz.getComponentType().isAssignableFrom(argument.clazz)) {
                        throw new IllegalArgumentException();
                    }

                    return (T) ((List<?>) argument.value).stream().map(e -> clazz.getComponentType().cast(e)).toArray();
                }
                else {
                    if (!clazz.isAssignableFrom(argument.clazz)) {
                        throw new IllegalArgumentException();
                    }

                    return clazz.cast(argument.value);
                }
            }

            throw new IllegalArgumentException();
        }
    }
}
