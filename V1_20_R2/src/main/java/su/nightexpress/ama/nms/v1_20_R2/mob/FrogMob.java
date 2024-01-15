package su.nightexpress.ama.nms.v1_20_R2.mob;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Dynamic;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.behavior.*;
import net.minecraft.world.entity.ai.behavior.declarative.BehaviorBuilder;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.animal.frog.Frog;
import net.minecraft.world.entity.schedule.Activity;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.ama.api.arena.IArena;
import su.nightexpress.ama.api.type.MobFaction;
import su.nightexpress.ama.nms.ArenaMob;
import su.nightexpress.ama.nms.v1_20_R2.brain.MobAI;
import su.nightexpress.ama.nms.v1_20_R2.brain.MobBrain;
import su.nightexpress.ama.nms.v1_20_R2.brain.behavior.MobCoreBehaviors;
import su.nightexpress.ama.nms.v1_20_R2.brain.behavior.MobFightBehaviors;
import su.nightexpress.ama.nms.v1_20_R2.brain.behavior.MobIdleBehaviors;
import su.nightexpress.ama.nms.v1_20_R2.brain.behavior.impl.ShootTongue;

public class FrogMob extends Frog implements ArenaMob {

    private static final UniformInt TIME_BETWEEN_LONG_JUMPS = UniformInt.of(100, 140);

    private final IArena arena;
    private final MobFaction faction;

    public FrogMob(@NotNull ServerLevel world, @NotNull IArena arena, @NotNull MobFaction faction) {
        super(EntityType.FROG, world);
        this.arena = arena;
        this.faction = faction;
        this.getBrain().setMemory(MemoryModuleType.LONG_JUMP_COOLDOWN_TICKS, TIME_BETWEEN_LONG_JUMPS.sample(this.random));
    }

    @NotNull
    @Override
    public IArena getArena() {
        return arena;
    }

    @NotNull
    @Override
    public MobFaction getFaction() {
        return faction;
    }

    @Override
    protected Brain.Provider<Frog> brainProvider() {
        return MobBrain.brainProvider(this);
    }

    protected Brain<?> makeBrain(Dynamic<?> dynamic) {
        return this.refreshBrain(this, this.brainProvider().makeBrain(dynamic));
    }

    @NotNull
    public Brain<Frog> refreshBrain(@NotNull Frog pet, @NotNull Brain<Frog> brain) {
        BehaviorControl<LivingEntity> cooldownTicks = new CountDownCooldownTicks(MemoryModuleType.LONG_JUMP_COOLDOWN_TICKS);

        brain.addActivity(Activity.CORE, 0, ImmutableList.of(
            MobCoreBehaviors.lookAtTarget(),
            MobCoreBehaviors.moveToTarget(),
            //PetCoreBehaviors.swim(),
            cooldownTicks,
            MobFightBehaviors.stopAngryIfTargetDead())
        );

        brain.addActivity(Activity.IDLE, 10, ImmutableList.of(
            new RunOne<>(
                ImmutableMap.of(MemoryModuleType.WALK_TARGET, MemoryStatus.VALUE_ABSENT),
                ImmutableList.of(Pair.of(new Croak(), 3))
            ),
            MobIdleBehaviors.followOwner(),
            MobFightBehaviors.autoTargetAndAttack())
        );

        brain.addActivity(Activity.FIGHT, 10, ImmutableList.of(
            MobFightBehaviors.stopAttackIfTargetInvalid(pet),
            (BehaviorControl<? super Frog>) new ShootTongue())
        );

        brain.addActivityWithConditions(Activity.SWIM, ImmutableList.of(
            Pair.of(2, MobFightBehaviors.autoTargetAndAttack()),
            Pair.of(5, new GateBehavior<>(
                ImmutableMap.of(MemoryModuleType.WALK_TARGET, MemoryStatus.VALUE_ABSENT),
                ImmutableSet.of(),
                GateBehavior.OrderPolicy.ORDERED, GateBehavior.RunningPolicy.TRY_ALL,
                ImmutableList.of(
                    Pair.of(RandomStroll.swim(0.75F), 1),
                    Pair.of(SetWalkTargetFromLookTarget.create(1.0F, 3), 1),
                    Pair.of(BehaviorBuilder.triggerIf(Entity::isInWaterOrBubble), 5)))
            )),
            ImmutableSet.of()
        );

        brain.setCoreActivities(ImmutableSet.of(Activity.CORE));
        brain.setDefaultActivity(Activity.IDLE);
        brain.useDefaultActivity();

        this.brain = brain;
        return brain;
    }

    @Override
    protected void customServerAiStep() {
        this.level().getProfiler().push("frogBrain");
        this.getBrain().tick((ServerLevel)this.level(), this);
        this.level().getProfiler().pop();
        this.level().getProfiler().push("frogActivityUpdate");
        this.updateActivity();
        this.level().getProfiler().pop();
    }

    protected void updateActivity() {
        Brain<Frog> brain = this.getBrain();
        if (MobAI.getAngerTarget(this).isPresent()) {
            brain.setActiveActivityToFirstValid(ImmutableList.of(Activity.FIGHT));
        }
        else if (this.isInWaterOrBubble()) {
            brain.setActiveActivityToFirstValid(ImmutableList.of(Activity.SWIM));
        }
        else {
            brain.setActiveActivityToFirstValid(ImmutableList.of(Activity.IDLE));
        }
        this.setAggressive(brain.hasMemoryValue(MemoryModuleType.ATTACK_TARGET));
    }

    @Override
    public boolean hurt(DamageSource damagesource, float damage) {
        if (super.hurt(damagesource, damage)) {
            if (damagesource.getEntity() instanceof LivingEntity target) {
                MobAI.setAngerTarget(this, target, false);
                return true;
            }
        }
        return false;
    }
}
