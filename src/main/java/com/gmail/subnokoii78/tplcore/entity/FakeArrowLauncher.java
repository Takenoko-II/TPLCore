package com.gmail.subnokoii78.tplcore.entity;

import com.destroystokyo.paper.event.entity.EntityRemoveFromWorldEvent;
import com.gmail.subnokoii78.tplcore.events.CancellableEvent;
import com.gmail.subnokoii78.tplcore.events.TPLEvent;
import com.gmail.subnokoii78.tplcore.events.TPLEventType;
import com.gmail.subnokoii78.tplcore.events.TPLEvents;
import com.gmail.subnokoii78.tplcore.shape.ParticleSpawner;
import com.gmail.subnokoii78.tplcore.vector.DualAxisRotationBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scheduler.BukkitRunnable;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;

@NullMarked
public class FakeArrowLauncher {
    private final Player shooter;

    private double power = 1.0;

    private double damage = 1.0;

    private boolean isCritical = false;

    private boolean isFlamed = false;

    private boolean isGravitated = true;

    @Nullable
    private ParticleSpawner<?> trailParticle = null;

    public FakeArrowLauncher(Player shooter) {
        this.shooter = shooter;
    }

    private Class<? extends AbstractArrow> getEntityClassOf(ItemStack itemStack) {
        return switch (itemStack.getType()) {
            case Material.ARROW, Material.TIPPED_ARROW -> Arrow.class;
            case Material.SPECTRAL_ARROW -> SpectralArrow.class;
            case Material.TRIDENT -> Trident.class;
            default -> throw new IllegalArgumentException();
        };
    }

    public FakeArrow launch(ItemStack weapon, ItemStack arrow) {
        final AbstractArrow projectile = shooter.launchProjectile(
            getEntityClassOf(arrow),
            DualAxisRotationBuilder.from(shooter)
                .getDirection3d()
                .scale(power)
                .toBukkitVector(),
            entity -> {
                entity.setShooter(shooter);
                entity.setHasLeftShooter(false);
                entity.setWeapon(weapon);

                entity.setLifetimeTicks(1200);
                entity.setItemStack(arrow);

                entity.setDamage(damage);
                entity.setCritical(isCritical);
                entity.setFireTicks(isFlamed ? 5000 : -1);
                entity.setGravity(isGravitated);

                if (arrow.getType().equals(Material.TIPPED_ARROW)) {
                    final Arrow arrowEntity = (Arrow) entity;
                    final PotionMeta potionMeta = (PotionMeta) arrow.getItemMeta();
                    for (PotionEffect effect : potionMeta.getAllEffects()) {
                        arrowEntity.addCustomEffect(effect, true);
                    }
                }
            }
        );

        return new FakeArrow(projectile, trailParticle);
    }

    public double getPower() {
        return power;
    }

    public FakeArrowLauncher setPower(double value) {
        power = value;
        return this;
    }

    public double getDamage() {
        return damage;
    }

    public FakeArrowLauncher setDamage(double value) {
        damage = value;
        return this;
    }

    public boolean isCritical() {
        return isCritical;
    }

    public FakeArrowLauncher setCritical(boolean value) {
        isCritical = value;
        return this;
    }

    public boolean isFlamed() {
        return isFlamed;
    }

    public FakeArrowLauncher setFlamed(boolean value) {
        isFlamed = value;
        return this;
    }

    public boolean isGravitated() {
        return isGravitated;
    }

    public FakeArrowLauncher setGravitated(boolean value) {
        isGravitated = value;
        return this;
    }

    @Nullable
    public ParticleSpawner<?> getTrailParticle() {
        return trailParticle;
    }

    public FakeArrowLauncher setTrailParticle(@Nullable ParticleSpawner<?> value) {
        trailParticle = value;
        return this;
    }

    public static final class FakeArrowEventListener extends BukkitRunnable implements Listener {
        private FakeArrowEventListener() {}

        @Override
        public void run() {
            for (final FakeArrow fakeArrow : FakeArrow.arrows) {
                final ParticleSpawner<?> spawner = fakeArrow.trailParticle;
                if (spawner == null) continue;
                spawner.place(fakeArrow.getEntity().getLocation());
                spawner.spawn();
                fakeArrow.movementTicks++;
            }
        }

        @EventHandler
        public void onProjectileHit(ProjectileHitEvent event) {
            final FakeArrow fakeArrow = FakeArrow.getFakeArrow(event.getEntity());
            if (fakeArrow == null) return;

            if (!(fakeArrow.arrow.getShooter() instanceof Player shooter)) {
                throw new IllegalStateException("shooter must be nonnull player");
            }

            final Block hitBlock = event.getHitBlock();
            if (hitBlock != null) {
                fakeArrow.events.getDispatcher(FakeArrow.BLOCK_HIT).dispatch(new FakeArrow.BlockHitEvent(
                    fakeArrow,
                    shooter,
                    hitBlock
                ));
            }

            final Entity hitEntity = event.getHitEntity();
            if (hitEntity != null) {
                fakeArrow.events.getDispatcher(FakeArrow.ENTITY_HIT).dispatch(new FakeArrow.EntityHitEvent(
                    event,
                    fakeArrow,
                    shooter,
                    hitEntity
                ));
            }
        }

        @EventHandler
        public void onEntityRemoveFromWorld(EntityRemoveFromWorldEvent event) {
            final Iterator<FakeArrow> iterator = FakeArrow.arrows.iterator();

            FakeArrowLauncher.FakeArrow arrow;
            while (iterator.hasNext()) {
                arrow = iterator.next();

                if (arrow.getEntity().equals(event.getEntity())) {
                    FakeArrow.arrows.remove(arrow);
                    break;
                }
            }
        }

        public static final FakeArrowEventListener INSTANCE = new FakeArrowEventListener();
    }

    public static final class FakeArrow {
        private static final Set<FakeArrowLauncher.FakeArrow> arrows = new HashSet<>();

        private static @Nullable FakeArrow getFakeArrow(Entity entity) {
            final Iterator<FakeArrow> iterator = FakeArrow.arrows.iterator();

            FakeArrowLauncher.FakeArrow arrow;
            while (iterator.hasNext()) {
                arrow = iterator.next();

                if (arrow.getEntity().equals(entity)) {
                    return arrow;
                }
            }

            return null;
        }

        private final AbstractArrow arrow;

        private final TPLEvents events = new TPLEvents();

        @Nullable
        private ParticleSpawner<?> trailParticle;

        private int movementTicks = 0;

        private final Location shootLocation;

        private FakeArrow(AbstractArrow arrow, @Nullable ParticleSpawner<?> trailParticle) {
            this.arrow = arrow;
            this.trailParticle = trailParticle;
            this.shootLocation = arrow.getLocation();
            arrows.add(this);
        }

        public ItemStack getWeapon() {
            return Objects.requireNonNull(arrow.getWeapon(), "FakeArrow Weapon must be nonnull");
        }

        public ItemStack getArrow() {
            return arrow.getItemStack();
        }

        public AbstractArrow getEntity() {
            return arrow;
        }

        public int getMovementTicks() {
            return movementTicks;
        }

        public Location getShootLocation() {
            return shootLocation;
        }

        public void onEntityHit(Consumer<EntityHitEvent> listener) {
            events.register(ENTITY_HIT, listener);
        }

        public void onBlockHit(Consumer<BlockHitEvent> listener) {
            events.register(BLOCK_HIT, listener);
        }

        public static final class BlockHitEvent implements TPLEvent {
            private final FakeArrow fakeArrow;

            private final Player shooter;

            private final Block block;

            private BlockHitEvent(FakeArrow fakeArrow, Player shooter, Block block) {
                this.fakeArrow = fakeArrow;
                this.shooter = shooter;
                this.block = block;
            }

            public FakeArrow getFakeArrow() {
                return fakeArrow;
            }

            public Player getShooter() {
                return shooter;
            }

            public Block getBlock() {
                return block;
            }

            @Override
            public TPLEventType<BlockHitEvent> getType() {
                return BLOCK_HIT;
            }
        }

        public static final class EntityHitEvent extends CancellableEvent {
            private final FakeArrow fakeArrow;

            private final Player shooter;

            private final Entity entity;

            private EntityHitEvent(ProjectileHitEvent event, FakeArrow fakeArrow, Player shooter, Entity entity) {
                super(event);
                this.fakeArrow = fakeArrow;
                this.shooter = shooter;
                this.entity = entity;
            }

            public FakeArrow getFakeArrow() {
                return fakeArrow;
            }

            public Player getShooter() {
                return shooter;
            }

            public Entity getEntity() {
                return entity;
            }

            @Override
            public TPLEventType<EntityHitEvent> getType() {
                return ENTITY_HIT;
            }
        }

        private static final TPLEventType<BlockHitEvent> BLOCK_HIT = new TPLEventType<>(BlockHitEvent.class);

        private static final TPLEventType<EntityHitEvent> ENTITY_HIT = new TPLEventType<>(EntityHitEvent.class);
    }
}
