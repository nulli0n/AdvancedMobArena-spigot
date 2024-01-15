package su.nightexpress.ama.nms.v1_20_R3.mob;

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
import net.minecraft.world.entity.monster.Zoglin;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.schedule.Activity;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.ama.api.arena.IArena;
import su.nightexpress.ama.api.type.MobFaction;
import su.nightexpress.ama.nms.ArenaMob;
import su.nightexpress.ama.nms.v1_20_R3.brain.MobAI;
import su.nightexpress.ama.nms.v1_20_R3.brain.MobBrain;
import su.nightexpress.ama.nms.v1_20_R3.brain.behavior.MobCoreBehaviors;
import su.nightexpress.ama.nms.v1_20_R3.brain.behavior.MobFightBehaviors;
import su.nightexpress.ama.nms.v1_20_R3.brain.behavior.MobIdleBehaviors;

public class ZoglinMob extends Zoglin implements ArenaMob {

    private final IArena arena;
    private final MobFaction faction;

    public ZoglinMob(@NotNull ServerLevel level, @NotNull IArena arena, @NotNull MobFaction faction) {
        super(EntityType.ZOGLIN, level);
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
    protected Brain.Provider<Zoglin> brainProvider() {
        return MobBrain.brainProvider(this);
    }

    protected Brain<?> makeBrain(Dynamic<?> dynamic) {
        return this.refreshBrain(this, this.brainProvider().makeBrain(dynamic));
    }

    @NotNull
    public Brain<Zoglin> refreshBrain(@NotNull Zoglin zoglin, @NotNull Brain<Zoglin> brain) {
        brain.addActivity(Activity.CORE, 0, ImmutableList.of(
            MobCoreBehaviors.lookAtTarget(),
            MobCoreBehaviors.moveToTarget(),
            MobCoreBehaviors.swim(),
            MobFightBehaviors.stopAngryIfTargetDead())
        );

        brain.addActivity(Activity.IDLE, 10, ImmutableList.of(
            MobIdleBehaviors.followOwner(),
            MobFightBehaviors.autoTargetAndAttack())
        );

        brain.addActivity(Activity.FIGHT, 10, ImmutableList.of(
            MobFightBehaviors.stopAttackIfTargetInvalid(zoglin),
            MobFightBehaviors.reachTargetWhenOutOfRange(),
            MobFightBehaviors.meleeAttack(zoglin.isAdult() ? 40 : 15))
        );

        brain.setCoreActivities(ImmutableSet.of(Activity.CORE));
        brain.setDefaultActivity(Activity.IDLE);
        brain.useDefaultActivity();

        this.brain = brain;
        return brain;
    }

    @Override
    protected void customServerAiStep() {
        super.customServerAiStep();
    }

    @Override
    protected void updateActivity() {
        Brain<Zoglin> brain = this.getBrain();
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
    protected void playAngrySound() {

    }

    @Override
    public InteractionResult mobInteract(Player entityhuman, InteractionHand enumhand) {
        return InteractionResult.sidedSuccess(this.level().isClientSide());
    }
}
