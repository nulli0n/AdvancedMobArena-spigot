package su.nightexpress.ama.nms.v1_20_R2.mob;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.mojang.serialization.Dynamic;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.behavior.CrossbowAttack;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.piglin.Piglin;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.schedule.Activity;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.ama.api.arena.IArena;
import su.nightexpress.ama.api.type.MobFaction;
import su.nightexpress.ama.nms.ArenaMob;
import su.nightexpress.ama.nms.v1_20_R2.brain.MobAI;
import su.nightexpress.ama.nms.v1_20_R2.brain.MobBrain;
import su.nightexpress.ama.nms.v1_20_R2.brain.behavior.MobCoreBehaviors;
import su.nightexpress.ama.nms.v1_20_R2.brain.behavior.MobFightBehaviors;
import su.nightexpress.ama.nms.v1_20_R2.brain.behavior.MobIdleBehaviors;

public class PiglinMob extends Piglin implements ArenaMob {

    private final IArena arena;
    private final MobFaction faction;

    public PiglinMob(@NotNull ServerLevel world, @NotNull IArena arena, @NotNull MobFaction faction) {
        super(EntityType.PIGLIN, world);
        this.arena = arena;
        this.faction = faction;
        this.setImmuneToZombification(true);
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
    protected Brain.Provider<Piglin> brainProvider() {
        return MobBrain.brainProvider(this);
    }

    protected Brain<?> makeBrain(Dynamic<?> dynamic) {
        return this.refreshBrain(this, this.brainProvider().makeBrain(dynamic));
    }

    @NotNull
    public Brain<Piglin> refreshBrain(@NotNull Piglin pet, @NotNull Brain<Piglin> brain) {
        brain.addActivity(Activity.CORE, 0, ImmutableList.of(
            MobCoreBehaviors.lookAtTarget(),
            MobCoreBehaviors.moveToTarget(),
            MobCoreBehaviors.swim(),
            MobFightBehaviors.stopAngryIfTargetDead())
        );

        brain.addActivity(Activity.IDLE, 10, ImmutableList.of(
            //new RunOne<>(ImmutableList.of(Pair.of(PetIdleBehaviors.lookAtOwner(), 1))),
            //PetIdleBehaviors.followOwner(),
            MobIdleBehaviors.followOwner(),
            MobFightBehaviors.autoTargetAndAttack())
        );


        brain.addActivity(Activity.FIGHT, 10, ImmutableList.of(
            MobFightBehaviors.stopAttackIfTargetInvalid(pet),
            MobFightBehaviors.reachTargetWhenOutOfRange(),
            MobFightBehaviors.meleeAttack(20),
            new CrossbowAttack<>()));

        brain.setCoreActivities(ImmutableSet.of(Activity.CORE));
        brain.setDefaultActivity(Activity.IDLE);
        brain.useDefaultActivity();
        this.brain = brain;
        return brain;
    }

    @Override
    protected void customServerAiStep() {
        //super.customServerAiStep();
        this.level().getProfiler().push("piglinBrain");
        this.getBrain().tick((ServerLevel)this.level(), this);
        this.level().getProfiler().pop();
        this.updateActivity();
    }

    protected void updateActivity() {
        Brain<Piglin> brain = this.getBrain();
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
        return InteractionResult.sidedSuccess(this.level().isClientSide);
    }

    @Override
    public boolean isConverting() {
        return false;
    }

    @Override
    protected boolean canHunt() {
        return false;
    }

    @Override
    public void setDancing(boolean flag) {

    }

    @Override
    public boolean wantsToPickUp(ItemStack itemstack) {
        return false;
    }

    @Override
    protected void pickUpItem(ItemEntity entityitem) {

    }

    public boolean isImmuneToZombification() {
        return true;
    }

    @Override
    public boolean startRiding(Entity entity, boolean flag) {
        return false;
    }
}
