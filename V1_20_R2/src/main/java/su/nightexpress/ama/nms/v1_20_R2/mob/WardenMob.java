package su.nightexpress.ama.nms.v1_20_R2.mob;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.mojang.serialization.Dynamic;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.monster.warden.Warden;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.schedule.Activity;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.ama.api.arena.IArena;
import su.nightexpress.ama.api.type.MobFaction;
import su.nightexpress.ama.nms.ArenaMob;
import su.nightexpress.ama.nms.v1_20_R2.brain.MobAI;
import su.nightexpress.ama.nms.v1_20_R2.brain.MobBrain;
import su.nightexpress.ama.nms.v1_20_R2.brain.behavior.MobCoreBehaviors;
import su.nightexpress.ama.nms.v1_20_R2.brain.behavior.MobFightBehaviors;

public class WardenMob extends Warden implements ArenaMob {

    private final IArena     arena;
    private final MobFaction faction;

    public WardenMob(@NotNull ServerLevel level, @NotNull IArena arena, @NotNull MobFaction faction) {
        super(EntityType.WARDEN, level);
        this.arena = arena;
        this.faction = faction;
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
    protected Brain.Provider<Warden> brainProvider() {
        return MobBrain.brainProvider(this);
    }

    protected Brain<?> makeBrain(Dynamic<?> dynamic) {
        return this.refreshBrain(this, this.brainProvider().makeBrain(dynamic));
    }

    @NotNull
    public Brain<Warden> refreshBrain(@NotNull Warden warden, @NotNull Brain<Warden> brain) {
        brain.addActivity(Activity.CORE, 0, ImmutableList.of(
            MobCoreBehaviors.lookAtTarget(),
            MobCoreBehaviors.moveToTarget(),
            MobCoreBehaviors.swim(),
            MobFightBehaviors.stopAngryIfTargetDead())
        );

        brain.addActivity(Activity.IDLE, 10, ImmutableList.of(
            MobFightBehaviors.autoTargetAndAttack())
        );

        brain.addActivity(Activity.FIGHT, 10, ImmutableList.of(
            MobFightBehaviors.stopAttackIfTargetInvalid(warden),
            MobFightBehaviors.reachTargetWhenOutOfRange(),
            MobFightBehaviors.meleeAttack(20))
        );

        brain.setCoreActivities(ImmutableSet.of(Activity.CORE));
        brain.setDefaultActivity(Activity.IDLE);
        brain.useDefaultActivity();

        this.brain = brain;
        return brain;
    }

    @Override
    protected void customServerAiStep() {
        ServerLevel worldserver = (ServerLevel)this.level();
        worldserver.getProfiler().push("wardenBrain");
        this.getBrain().tick(worldserver, this);
        this.level().getProfiler().pop();
        this.updateActivity();
        if ((this.tickCount + this.getId()) % 120 == 0) {
            applyDarknessAround(worldserver, this.position(), this, 20);
        }
    }

    protected void updateActivity() {
        Brain<Warden> brain = this.getBrain();
        if (MobAI.getAngerTarget(this).isPresent()) {
            brain.setActiveActivityIfPossible(Activity.FIGHT);
        }
        else {
            brain.setActiveActivityIfPossible(Activity.IDLE);
        }
        this.setAggressive(brain.hasMemoryValue(MemoryModuleType.ATTACK_TARGET));
    }

    @Override
    public boolean hurt(DamageSource damagesource, float damage) {
        return MobBrain.hurt(this, damagesource, damage);
    }

    @Override
    public InteractionResult mobInteract(Player entityhuman, InteractionHand enumhand) {
        return InteractionResult.sidedSuccess(this.level().isClientSide());
    }
}
