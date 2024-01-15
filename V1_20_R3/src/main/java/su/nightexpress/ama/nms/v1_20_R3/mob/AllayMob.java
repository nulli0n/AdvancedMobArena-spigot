package su.nightexpress.ama.nms.v1_20_R3.mob;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.mojang.serialization.Dynamic;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.animal.allay.Allay;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.schedule.Activity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.gameevent.DynamicGameEventListener;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.ama.api.arena.IArena;
import su.nightexpress.ama.api.type.MobFaction;
import su.nightexpress.ama.nms.ArenaMob;
import su.nightexpress.ama.nms.v1_20_R3.brain.MobAI;
import su.nightexpress.ama.nms.v1_20_R3.brain.MobBrain;
import su.nightexpress.ama.nms.v1_20_R3.brain.behavior.MobCoreBehaviors;
import su.nightexpress.ama.nms.v1_20_R3.brain.behavior.MobFightBehaviors;
import su.nightexpress.ama.nms.v1_20_R3.brain.behavior.MobIdleBehaviors;

import java.util.function.BiConsumer;

public class AllayMob extends Allay implements ArenaMob {

    private final IArena arena;
    private final MobFaction faction;

    public AllayMob(@NotNull ServerLevel world, @NotNull IArena arena, @NotNull MobFaction faction) {
        super(EntityType.ALLAY, world);
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
    protected Brain.Provider<Allay> brainProvider() {
        return MobBrain.brainProvider(this);
    }

    protected Brain<?> makeBrain(Dynamic<?> dynamic) {
        return this.refreshBrain(this, this.brainProvider().makeBrain(dynamic));
    }

    @NotNull
    public Brain<Allay> refreshBrain(@NotNull Allay pet, @NotNull Brain<Allay> brain) {
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
            MobFightBehaviors.stopAttackIfTargetInvalid(pet),
            MobFightBehaviors.reachTargetWhenOutOfRange(),
            MobFightBehaviors.meleeAttack(20)
        ));

        brain.setCoreActivities(ImmutableSet.of(Activity.CORE));
        brain.setDefaultActivity(Activity.IDLE);
        brain.useDefaultActivity();

        this.brain = brain;
        return brain;
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

    @Override
    protected void customServerAiStep() {
        this.level().getProfiler().push("allayBrain");
        this.getBrain().tick((ServerLevel)this.level(), this);
        this.level().getProfiler().pop();
        this.level().getProfiler().push("allayActivityUpdate");
        this.updateActivity();
        this.level().getProfiler().pop();
    }

    protected void updateActivity() {
        Brain<Allay> brain = this.getBrain();
        if (MobAI.getAngerTarget(this).isPresent()) {
            brain.setActiveActivityIfPossible(Activity.FIGHT);
        }
        else {
            brain.setActiveActivityIfPossible(Activity.IDLE);
        }
        this.setAggressive(brain.hasMemoryValue(MemoryModuleType.ATTACK_TARGET));
    }

    @Override
    public void updateDynamicGameEventListener(BiConsumer<DynamicGameEventListener<?>, ServerLevel> var0) {

    }

    @Override
    public InteractionResult mobInteract(Player entityhuman, InteractionHand enumhand) {
        return InteractionResult.sidedSuccess(this.level().isClientSide);
    }

    @Override
    protected void dropEquipment() {

    }

    @Override
    public boolean wantsToPickUp(ItemStack var0) {
        return false;
    }

    @Override
    public boolean isPanicking() {
        return false;
    }

    @Override
    public boolean canDuplicate() {
        return false;
    }
}
