package su.nightexpress.ama.nms.v1_20_R2.brain;

import net.minecraft.util.TimeUtil;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.behavior.BehaviorUtils;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class MobAI {

    private static final TargetingConditions IGNORE_LOS                  = TargetingConditions.forCombat().ignoreLineOfSight();
    private static final TargetingConditions IGNORE_INVISIBILITY_AND_LOS = TargetingConditions.forCombat().ignoreLineOfSight().ignoreInvisibilityTesting();

    private static final UniformInt RETREAT_DURATION = TimeUtil.rangeOfSeconds(5, 20);

    public static boolean isEntityAttackableIgnoringLineOfSight(LivingEntity mob, LivingEntity target) {
        return mob.getBrain().isMemoryValue(MemoryModuleType.ATTACK_TARGET, target) ? IGNORE_INVISIBILITY_AND_LOS.test(mob, target) : IGNORE_LOS.test(mob, target);
    }

    public static boolean setAngerTarget(@NotNull Mob pet, @NotNull LivingEntity target, boolean force) {
        Optional<LivingEntity> angerTarget = getAngerTarget(pet);
        if (!force) {
            if (!isEntityAttackableIgnoringLineOfSight(pet, target)) return false;
        }

        pet.getBrain().eraseMemory(MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE);
        pet.getBrain().setMemoryWithExpiry(MemoryModuleType.ANGRY_AT, target.getUUID(), Integer.MAX_VALUE);
        pet.getBrain().setMemoryWithExpiry(MemoryModuleType.UNIVERSAL_ANGER, true, Integer.MAX_VALUE);
        pet.getBrain().setMemory(MemoryModuleType.ATTACK_TARGET, target);
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
