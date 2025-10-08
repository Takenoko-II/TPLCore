package com.gmail.subnokoii78.tplcore.execute;

import com.gmail.subnokoii78.tplcore.generic.Pair;
import com.gmail.subnokoii78.tplcore.parse.AbstractParser;
import com.gmail.subnokoii78.tplcore.vector.DualAxisRotationBuilder;
import com.gmail.subnokoii78.tplcore.vector.Vector3Builder;
import org.jspecify.annotations.NullMarked;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Supplier;

@NullMarked
public class VectorParser extends AbstractParser<VectorParser.VectorComponents<?>> {
    protected VectorParser(String vector) {
        super(vector);
    }

    @Override
    protected Set<Character> getWhitespace() {
        return Set.of(' ');
    }

    @Override
    protected Set<Character> getQuotes() {
        return Set.of('\'', '"');
    }

    @Override
    protected String getTrue() {
        return "true";
    }

    @Override
    protected String getFalse() {
        return "false";
    }

    private VectorComponent component() {
        final char[] ints = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9'};

        if (test(true, ints) != null) {
            final double value = number(false);
            return new VectorComponent.AbsoluteVectorComponent(value);
        }
        else if (next(true, '~') != null) {
            final double value;
            if (test(true, ints) != null) {
                value = number(false);
            }
            else {
                value = 0;
            }
            return new VectorComponent.RelativeVectorComponent(value);
        }
        else if (next(true, '^') != null) {
            final double value;
            if (test(true, ints) != null) {
                value = number(false);
            }
            else {
                value = 0;
            }
            return new VectorComponent.LocalVectorComponent(value);
        }
        else {
            throw exception("ベクトル成分の解析に失敗しました");
        }
    }

    @Override
    protected VectorComponents<?> parse() {
        final List<VectorComponent> components = new ArrayList<>();

        boolean isLocal = false;
        while (!isOver()) {
            final VectorComponent component = component();

            if (isLocal && !(component instanceof VectorComponent.LocalVectorComponent)) {
                throw exception("キャレット記法とその他の記法を混在させることはできません");
            }

            if (component instanceof VectorComponent.LocalVectorComponent) {
                isLocal = true;
            }

            components.add(component);
        }

        finish();

        if (isLocal) {
            return new VectorComponents.LocalVectorComponents(
                components.stream().map(component -> (VectorComponent.LocalVectorComponent) component).toList()
            );
        }
        else {
            return new VectorComponents.AxisAlignedVectorComponents(
                components.stream().map(component -> (VectorComponent.AxisAlignedVectorComponent) component).toList()
            );
        }
    }

    protected static <C extends VectorComponents<?>> C parse(String text, Class<C> clazz) {
        final VectorComponents<?> components = new VectorParser(text).parse();
        if (clazz.isInstance(components)) {
            return clazz.cast(components);
        }
        else {
            throw new IllegalArgumentException();
        }
    }

    public static VectorComponents.AxisAlignedVectorComponents axisAligned(String text) {
        return parse(text, VectorComponents.AxisAlignedVectorComponents.class);
    }

    public static VectorComponents.LocalVectorComponents local(String text) {
        return parse(text, VectorComponents.LocalVectorComponents.class);
    }

    public static abstract class VectorComponent {
        protected final double value;

        private VectorComponent(double value) {
            this.value = value;
        }

        public static abstract class AxisAlignedVectorComponent extends VectorComponent {
            private AxisAlignedVectorComponent(double value) {
                super(value);
            }

            public abstract void assign(Supplier<Double> getter, Consumer<Double> setter);
        }

        public static final class AbsoluteVectorComponent extends AxisAlignedVectorComponent {
            private AbsoluteVectorComponent(double value) {
                super(value);
            }

            @Override
            public void assign(Supplier<Double> getter, Consumer<Double> setter) {
                setter.accept(value);
            }
        }

        public static final class RelativeVectorComponent extends AxisAlignedVectorComponent {
            private RelativeVectorComponent(double value) {
                super(value);
            }

            @Override
            public void assign(Supplier<Double> getter, Consumer<Double> setter) {
                setter.accept(getter.get() + value);
            }
        }

        public static final class LocalVectorComponent extends VectorComponent {
            private LocalVectorComponent(double value) {
                super(value);
            }
        }
    }

    public static abstract class VectorComponents<V extends VectorComponent> {
        protected final List<V> components;

        private VectorComponents(List<V> components) {
            this.components = components;
        }

        public static final class AxisAlignedVectorComponents extends VectorComponents<VectorComponent.AxisAlignedVectorComponent> {
            private AxisAlignedVectorComponents(List<VectorComponent.AxisAlignedVectorComponent> components) {
                super(components);
            }

            private void write(List<Pair<Supplier<Double>, Consumer<Double>>> accesses) {
                if (accesses.size() != components.size()) {
                    throw new IllegalArgumentException();
                }

                for (int i = 0; i < accesses.size(); i++) {
                    final var access = accesses.get(i);
                    final var component = components.get(i);

                    component.assign(access.a(), access.b());
                }
            }

            public void write(Vector3Builder vector3) {
                write(List.of(
                    new Pair<>(vector3::x, vector3::x),
                    new Pair<>(vector3::y, vector3::y),
                    new Pair<>(vector3::z, vector3::z)
                ));
            }

            public void write(DualAxisRotationBuilder rotation) {
                write(List.of(
                    new Pair<>(() -> (double) rotation.yaw(), (d) -> rotation.yaw(d.floatValue())),
                    new Pair<>(() -> (double) rotation.pitch(), (d) -> rotation.pitch(d.floatValue()))
                ));
            }
        }

        public static final class LocalVectorComponents extends VectorComponents<VectorComponent.LocalVectorComponent> {
            private LocalVectorComponents(List<VectorComponent.LocalVectorComponent> components) {
                super(components);
            }

            public void write(Vector3Builder vector3, DualAxisRotationBuilder rotation) {
                if (components.size() != 3) {
                    throw new IllegalArgumentException();
                }

                final var lap = rotation.getDirection3d().getLocalAxisProvider();

                vector3
                    .add(lap.getX().length(
                        components.get(0).value
                    ))
                    .add(lap.getY().length(
                        components.get(1).value
                    ))
                    .add(lap.getZ().length(
                        components.get(2).value
                    ));
            }
        }
    }
}
