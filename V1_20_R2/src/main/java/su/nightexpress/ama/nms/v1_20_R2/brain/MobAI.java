package su.nightexpress.ama.nms.v1_20_R2.brain;

import net.minecraft.util.TimeUtil;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.behavior.BehaviorUtils;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import org.bukkit.craftbukkit.v1_20_R2.entity.CraftLivingEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nightexpress.ama.api.arena.IArena;
import su.nightexpress.ama.api.type.MobFaction;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class MobAI {

    private static final TargetingConditions IGNORE_LOS = TargetingConditions.forCombat()
        .ignoreLineOfSight();

    private static final TargetingConditions IGNORE_INVISIBILITY_AND_LOS = TargetingConditions.forCombat()
        .ignoreLineOfSight().ignoreInvisibilityTesting();

    private static final double SEE_DISTANCE = 32D;

    private static final UniformInt RETREAT_DURATION = TimeUtil.rangeOfSeconds(5, 20);

    @NotNull
    public static Set<LivingEntity> getTargetList(@NotNull LivingEntity mob, @NotNull MobFaction faction, @NotNull IArena arena) {
        Set<org.bukkit.entity.LivingEntity> bukkitEntities = new HashSet<>();
        if (faction == MobFaction.ENEMY) {
            arena.getPlayers().getAlive().forEach(arenaPlayer -> bukkitEntities.add(arenaPlayer.getPlayer()));
            bukkitEntities.addAll(arena.getMobs().getAllies());
        }
        else if (faction == MobFaction.ALLY) {
            bukkitEntities.addAll(arena.getMobs().getEnemies());
        }

        Set<LivingEntity> targetList = new HashSet<>();
        for (org.bukkit.entity.LivingEntity bukkitEntity : bukkitEntities) {
            LivingEntity entity = ((CraftLivingEntity) bukkitEntity).getHandle();
            targetList.add(entity);
        }

        return targetList;
    }

    @Nullable
    public static LivingEntity getNearestTarget(@NotNull LivingEntity mob, @NotNull MobFaction faction, @NotNull IArena arena) {
        return getNearestTarget(mob, getTargetList(mob, faction, arena));
    }

    @Nullable
    public static LivingEntity getNearestTarget(@NotNull LivingEntity mob,
                                                @NotNull Set<LivingEntity> entities) {
        //double followRange = mob.getAttributeValue(Attributes.FOLLOW_RANGE);
        //AABB searchArea = mob.getBoundingBox().inflate(followRange, 4D, followRange);

        double bestDistance = -1D;
        LivingEntity target = null;

        for (LivingEntity entity : entities) {
            if (!IGNORE_LOS.test(mob, entity)) continue;
            //if (!searchArea.contains(entity.getX(), entity.getY(), entity.getZ())) continue;

            double distance = entity.distanceToSqr(mob);

            // Custom invisibility test, bc otherwise TargetConditions will fail for low range value.
            double visibilityPercent = entity.getVisibilityPercent(mob);
            if (visibilityPercent < 1D && entity.isInvisible()) {
                double seeDistance = SEE_DISTANCE * SEE_DISTANCE;
                double threshold = distance / seeDistance;
                double visibility = visibilityPercent - threshold;
                if (visibility <= 0D) continue;
            }

            if (bestDistance == -1D || distance < bestDistance) {
                bestDistance = distance;
                target = entity;
            }
        }

        return target;
    }

    public static boolean isEntityAttackableIgnoringLineOfSight(LivingEntity mob, LivingEntity target) {
        return mob.getBrain().isMemoryValue(MemoryModuleType.ATTACK_TARGET, target) ? IGNORE_INVISIBILITY_AND_LOS.test(mob, target) : IGNORE_LOS.test(mob, target);
    }

    public static boolean setAngerTarget(@NotNull Mob mob, @NotNull LivingEntity target, boolean force) {
        if (!force) {
            if (!isEntityAttackableIgnoringLineOfSight(mob, target)) return false;
        }

        mob.getBrain().eraseMemory(MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE);
        mob.getBrain().setMemoryWithExpiry(MemoryModuleType.ANGRY_AT, target.getUUID(), Integer.MAX_VALUE);
        //pet.getBrain().setMemoryWithExpiry(MemoryModuleType.UNIVERSAL_ANGER, true, Integer.MAX_VALUE);
        mob.getBrain().setMemory(MemoryModuleType.ATTACK_TARGET, target);
        return true;
    }

    public static void eraseTarget(@NotNull Mob pet) {
        pet.getBrain().eraseMemory(MemoryModuleType.ANGRY_AT);
        pet.getBrain().eraseMemory(MemoryModuleType.ATTACK_TARGET);
        pet.getBrain().eraseMemory(MemoryModuleType.WALK_TARGET);
    }

    public static void setAvoidTargetAndDontHuntForAWhile(@NotNull Mob pet, @NotNull LivingEntity target) {
        pet.getBrain().eraseMemory(MemoryModuleType.ANGRY_AT);
        pet.getBrain().eraseMemory(MemoryModuleType.ATTACK_TARGET);
        pet.getBrain().eraseMemory(MemoryModuleType.WALK_TARGET);
        pet.getBrain().setMemoryWithExpiry(MemoryModuleType.AVOID_TARGET, target, RETREAT_DURATION.sample(pet.level().random));
    }

    @NotNull
    public static Optional<LivingEntity> getAngerTarget(@NotNull Mob pet) {
        return BehaviorUtils.getLivingEntityFromUUIDMemory(pet, MemoryModuleType.ANGRY_AT);
    }
}
