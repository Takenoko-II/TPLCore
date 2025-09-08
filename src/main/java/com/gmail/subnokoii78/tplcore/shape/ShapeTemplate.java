package com.gmail.subnokoii78.tplcore.shape;

import com.gmail.subnokoii78.tplcore.vector.DualAxisRotationBuilder;
import com.gmail.subnokoii78.tplcore.vector.TripleAxisRotationBuilder;
import com.gmail.subnokoii78.tplcore.vector.Vector3Builder;
import org.bukkit.Location;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.InvocationTargetException;

public final class ShapeTemplate {
    private final ShapeBase base = new ShapeBase() {
        @Override
        public void draw() {}
    };

    public ShapeTemplate() {}

    public ShapeTemplate put(@NotNull Location location) {
        world(location.getWorld());
        center(Vector3Builder.from(location));
        rotation(TripleAxisRotationBuilder.from(DualAxisRotationBuilder.from(location)));
        return this;
    }

    public ShapeTemplate world(@NotNull World world) {
        base.world = world;
        return this;
    }

    public ShapeTemplate center(@NotNull Vector3Builder center) {
        base.center.x(center.x());
        base.center.y(center.y());
        base.center.z(center.z());
        return this;
    }

    public ShapeTemplate rotation(@NotNull TripleAxisRotationBuilder rotation) {
        base.rotation.yaw(rotation.yaw());
        base.rotation.pitch(rotation.pitch());
        base.rotation.roll(rotation.roll());
        return this;
    }

    public ShapeTemplate particle(@NotNull ParticleSpawner<?> particle) {
        base.setParticle(particle);
        return this;
    }

    public ShapeTemplate scale(float scale) {
        base.setScale(scale);
        return this;
    }

    public ShapeTemplate density(float density) {
        base.setDensity(density);
        return this;
    }

    public <T extends ShapeBase> T newShape(Class<T> clazz) {
        final T instance;
        try {
            instance = clazz.getConstructor().newInstance();
        }
        catch (NoSuchMethodException | IllegalAccessException | InstantiationException | InvocationTargetException e) {
            throw new IllegalArgumentException("コンストラクターを呼び出せないクラスが渡されました");
        }

        instance.world = base.world;
        instance.center.x(base.center.x());
        instance.center.y(base.center.y());
        instance.center.z(base.center.z());
        instance.rotation.add(base.rotation);
        instance.setParticle(base.getParticle());
        instance.setScale(base.getScale());
        instance.setDensity(base.getDensity());

        return instance;
    }
}
