package su.nightexpress.ama.nms.v1_20_R3.brain;

import com.google.common.collect.ImmutableList;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.sensing.Sensor;
import net.minecraft.world.entity.ai.sensing.SensorType;
import net.minecraft.world.entity.animal.Wolf;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.utils.Reflex;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Optional;
import java.util.UUID;

public class MobBrain {

    protected static final ImmutableList<SensorType<? extends Sensor<? extends LivingEntity>>> SENSOR_TYPES;
    protected static final ImmutableList<MemoryModuleType<?>>                                  MEMORY_TYPES;

    static {
        SENSOR_TYPES = ImmutableList.of(
            SensorType.NEAREST_LIVING_ENTITIES,
            SensorType.NEAREST_PLAYERS,
            SensorType.NEAREST_ITEMS,
            SensorType.HURT_BY
            //SensorType.PIGLIN_SPECIFIC_SENSOR
        );
        MEMORY_TYPES = ImmutableList.of(
            MemoryModuleType.IS_IN_WATER,
            MemoryModuleType.LOOK_TARGET,
            MemoryModuleType.LIKED_PLAYER,
            MemoryModuleType.NEAREST_LIVING_ENTITIES,
            MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES,
            MemoryModuleType.NEAREST_VISIBLE_PLAYER,
            MemoryModuleType.NEAREST_VISIBLE_ATTACKABLE_PLAYER,
            MemoryModuleType.NEAREST_VISIBLE_WANTED_ITEM,
            MemoryModuleType.NEAREST_VISIBLE_ADULT_HOGLINS,
            MemoryModuleType.HURT_BY,
            MemoryModuleType.HURT_BY_ENTITY,
            MemoryModuleType.WALK_TARGET,
            MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE,
            MemoryModuleType.ATTACK_TARGET,
            MemoryModuleType.ATTACK_COOLING_DOWN,
            MemoryModuleType.INTERACTION_TARGET,
            MemoryModuleType.PATH,
            MemoryModuleType.ANGRY_AT,
            MemoryModuleType.UNIVERSAL_ANGER,
            MemoryModuleType.AVOID_TARGET,
            MemoryModuleType.RIDE_TARGET,
            MemoryModuleType.ATE_RECENTLY,
            MemoryModuleType.RAM_COOLDOWN_TICKS,
            MemoryModuleType.RAM_TARGET,
            MemoryModuleType.LONG_JUMP_COOLDOWN_TICKS,
            MemoryModuleType.NEAREST_REPELLENT);
    }

    public static <E extends Mob> ImmutableList<SensorType<? extends Sensor<? super E>>> getSensorTypes(@NotNull E entity) {
        return ImmutableList.of(
            SensorType.NEAREST_LIVING_ENTITIES,
            SensorType.NEAREST_PLAYERS,
            SensorType.NEAREST_ITEMS,
            SensorType.HURT_BY,
            SensorType.PIGLIN_SPECIFIC_SENSOR);
    }

    public static Player getOwner(@NotNull Mob pet) {
        Optional<UUID> optional = pet.getBrain().getMemory(MemoryModuleType.LIKED_PLAYER);
        return optional.map(id -> pet.level().getPlayerByUUID(id)).orElse(null);
    }

    public static void setOwnerMemory(@NotNull Mob pet, @NotNull Player player) {
        pet.getBrain().setMemory(MemoryModuleType.LIKED_PLAYER, player.getUUID());
    }

    public static <E extends Mob> Brain.Provider<E> brainProvider(@NotNull E entity) {
        return Brain.provider(MEMORY_TYPES, getSensorTypes(entity));
    }

    private static final Method ACTUALLY_HURT    = Reflex.getMethod(LivingEntity.class, "actuallyHurt", DamageSource.class, Float.TYPE);
    private static final Field  NO_ACTION_TIME   = Reflex.getField(LivingEntity.class, "bb");
    private static final Field  LAST_DAMAGE_TIME = Reflex.getField(LivingEntity.class, "ce");
    private static final Field  LAST_DAMAGE_SOURCE = Reflex.getField(LivingEntity.class, "cd");

    public static boolean hurt(LivingEntity mob, DamageSource damagesource, float damage) {
        if (ACTUALLY_HURT == null) return false;
        if (mob.isInvulnerableTo(damagesource)) return false;
        if (mob.level().isClientSide) return false;
        if (!mob.isAlive()) return false;
        if (damagesource.is(DamageTypeTags.IS_FIRE) && mob.hasEffect(MobEffects.FIRE_RESISTANCE)) {
            return false;
        }

        if (mob.isSleeping() && !mob.level().isClientSide) {
            mob.stopSleeping();
        }

        if (NO_ACTION_TIME != null) {
            try {
                NO_ACTION_TIME.trySetAccessible();
                NO_ACTION_TIME.set(mob, 0);
            }
            catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }

        float f1 = damage;
        boolean blocked = damage > 0.0F && mob.isDamageSourceBlocked(damagesource);
        if (damagesource.is(DamageTypeTags.IS_FREEZING) && mob.getType().is(EntityTypeTags.FREEZE_HURTS_EXTRA_TYPES)) {
            damage *= 5.0F;
        }

        mob.walkAnimation.setSpeed(1.5F);
        boolean punch = true;
        if ((float) mob.invulnerableTime > (float) mob.invulnerableDuration / 2.0F && !damagesource.is(DamageTypeTags.BYPASSES_COOLDOWN)) {
            if (damage <= mob.lastHurt) {
                return false;
            }

            Object val = Reflex.invokeMethod(ACTUALLY_HURT, mob, damagesource, damage - mob.lastHurt);
            if (val != null && !(boolean) val) {
                return false;
            }

            mob.lastHurt = damage;
            punch = false;
        }
        else {
            Object val = Reflex.invokeMethod(ACTUALLY_HURT, mob, damagesource, damage);
            if (val != null && !(boolean) val) {
                return false;
            }
            mob.lastHurt = damage;
            mob.invulnerableTime = mob.invulnerableDuration;
            mob.hurtDuration = 10;
            mob.hurtTime = mob.hurtDuration;
        }

        Entity damager = damagesource.getEntity();
        if (damager != null) {
            if (damager instanceof LivingEntity entityliving1) {
                if (!damagesource.is(DamageTypeTags.NO_ANGER)) {
                    mob.setLastHurtByMob(entityliving1);
                }
            }

            int ticksHas = mob.tickCount;
            if (damager instanceof Player player) {
                mob.tickCount = 100;
                mob.setLastHurtByPlayer(player);
                mob.tickCount = ticksHas;
            }
            else if (damager instanceof Wolf entitywolf) {
                if (entitywolf.isTame()) {
                    LivingEntity entityliving = entitywolf.getOwner();
                    if (entityliving instanceof Player player) {
                        mob.tickCount = 100;
                        mob.setLastHurtByPlayer(player);
                        mob.tickCount = ticksHas;
                    }
                    else mob.lastHurtByPlayer = null;
                }
            }
        }

        if (punch) {
            if (blocked) {
                mob.level().broadcastEntityEvent(mob, (byte) 29);
            }
            else {
                mob.level().broadcastDamageEvent(mob, damagesource);
            }

            if (!damagesource.is(DamageTypeTags.NO_IMPACT) && (!blocked || damage > 0.0F)) {
                mob.hurtMarked = true;
            }

            if (damager != null && !damagesource.is(DamageTypeTags.IS_EXPLOSION)) {
                double d0 = damager.getX() - mob.getX();

                double d1;
                for (d1 = damager.getZ() - mob.getZ(); d0 * d0 + d1 * d1 < 1.0E-4D; d1 = (Math.random() - Math.random()) * 0.01D) {
                    d0 = (Math.random() - Math.random()) * 0.01D;
                }

                mob.knockback(0.4000000059604645D, d0, d1);
                if (!blocked) {
                    mob.indicateDamage(d0, d1);
                }
            }
        }

        if (mob.isDeadOrDying()) {
                    /*if (!pet.checkTotemDeathProtection(damagesource)) {
                        SoundEvent soundeffect = pet.getDeathSound();
                        if (punch && soundeffect != null) {
                            pet.playSound(soundeffect, pet.getSoundVolume(), pet.getVoicePitch());
                        }
                    }*/
            mob.die(damagesource);
        }
        else if (punch) {
            //pet.playHurtSound(damagesource);
        }

        boolean damaged = !blocked || damage > 0.0F;
        if (damaged) {
            if (LAST_DAMAGE_TIME != null && LAST_DAMAGE_SOURCE != null) {
                try {
                    LAST_DAMAGE_SOURCE.trySetAccessible();
                    LAST_DAMAGE_SOURCE.set(mob, damagesource);

                    LAST_DAMAGE_TIME.trySetAccessible();
                    LAST_DAMAGE_TIME.set(mob, mob.level().getGameTime());
                }
                catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }

        if (damager instanceof ServerPlayer) {
            CriteriaTriggers.PLAYER_HURT_ENTITY.trigger((ServerPlayer) damager, mob, damagesource, f1, damage, blocked);
        }

        return damaged;
    }
}
