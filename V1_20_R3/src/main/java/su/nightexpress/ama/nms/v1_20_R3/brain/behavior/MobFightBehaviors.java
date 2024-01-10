package su.nightexpress.ama.nms.v1_20_R3.brain.behavior;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.behavior.*;
import net.minecraft.world.entity.ai.behavior.declarative.BehaviorBuilder;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.WalkTarget;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ProjectileWeaponItem;
import org.bukkit.craftbukkit.v1_20_R3.entity.CraftLivingEntity;
import org.bukkit.craftbukkit.v1_20_R3.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_20_R3.event.CraftEventFactory;
import org.bukkit.event.entity.EntityTargetEvent;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.utils.random.Rnd;
import su.nightexpress.ama.api.arena.IArena;
import su.nightexpress.ama.api.type.MobFaction;
import su.nightexpress.ama.nms.ArenaMob;
import su.nightexpress.ama.nms.v1_20_R3.brain.MobAI;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

public class MobFightBehaviors {

    private static final TargetingConditions HURT_BY_TARGETING = TargetingConditions.forCombat().ignoreLineOfSight().ignoreInvisibilityTesting();

    public static Set<LivingEntity> getTargetList(@NotNull ArenaMob arenaMob) {
        IArena arena = arenaMob.getArena();
        MobFaction faction = arenaMob.getFaction();

        Set<LivingEntity> targetList = new HashSet<>();
        if (faction == MobFaction.ENEMY) {
            targetList.addAll(arena.getPlayers().getAlive().stream()
                .map(player -> ((CraftPlayer)player.getPlayer()).getHandle()).collect(Collectors.toSet()));
            targetList.addAll(arena.getMobs().getAllies().stream()
                .map(entity -> ((CraftLivingEntity)entity).getHandle()).collect(Collectors.toSet()));
        }
        else if (faction == MobFaction.ALLY) {
            targetList.addAll(arena.getMobs().getEnemies().stream()
                .map(entity -> ((CraftLivingEntity)entity).getHandle()).collect(Collectors.toSet()));
        }

        return targetList;
    }

    @NotNull
    public static BehaviorControl<Mob> autoTargetAndAttack() {
        return BehaviorBuilder.create((builder) -> {
            return builder.group(
                builder.absent(MemoryModuleType.ATTACK_TARGET),
                builder.registered(MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE)
            ).apply(builder, (memAttackTarget, memCantReach) -> {
                return (world, mob, longValue) -> {
                    if (!(mob instanceof ArenaMob arenaMob)) return false;

                    LivingEntity target = MobAI.getAngerTarget(mob).orElse(null);
                    if (target == null) {
                        //IArena arena = arenaMob.getArena();
                        //MobFaction faction = arenaMob.getFaction();
                        LivingEntity lastDamager = mob.getLastAttacker();

                        Set<LivingEntity> targetList = getTargetList(arenaMob);
                        if (!targetList.isEmpty()) {
                            if (lastDamager != null && targetList.contains(lastDamager)) {
                                target = lastDamager;
                            }
                            else target = Rnd.get(targetList);
                        }
                    }

                    if (target == null) {
                        MobAI.eraseTarget(mob);
                        return false;
                    }
                    /*if (!mob.canAttack(target)) {
                        return false;
                    }*/

                    EntityTargetEvent event = CraftEventFactory.callEntityTargetLivingEvent(mob, target, EntityTargetEvent.TargetReason.CLOSEST_ENTITY);
                    if (event.isCancelled()) {
                        return false;
                    }

                    if (event.getTarget() == null) {
                        memAttackTarget.erase();
                        MobAI.eraseTarget(mob);
                        return true;
                    }

                    target = ((CraftLivingEntity) event.getTarget()).getHandle();
                    return MobAI.setAngerTarget(mob, target, true);
                };
            });
        });
    }

    @NotNull
    public static OneShot<Mob> meleeAttack(int cooldown) {
        return BehaviorBuilder.create((builer) -> {
            return builer.group(
                builer.registered(MemoryModuleType.LOOK_TARGET),
                builer.present(MemoryModuleType.ATTACK_TARGET),
                builer.absent(MemoryModuleType.ATTACK_COOLING_DOWN)
            ).apply(builer, (lookTarget, attackTarget, attackCooldown) -> {
                return (world, mob, i) -> {
                    LivingEntity target = builer.get(attackTarget);
                    if (isHoldingUsableProjectileWeapon(mob)) return false;
                    if (!mob.isWithinMeleeAttackRange(target)) return false;

                    lookTarget.set(new EntityTracker(target, true));
                    mob.swing(InteractionHand.MAIN_HAND);
                    mob.doHurtTarget(target);
                    attackCooldown.setWithExpiry(true, cooldown);
                    return true;
                };
            });
        });
    }

    private static boolean isHoldingUsableProjectileWeapon(Mob mob) {
        return mob.isHolding((itemStack) -> {
            Item item = itemStack.getItem();
            return item instanceof ProjectileWeaponItem pj && mob.canFireProjectileWeapon(pj);
        });
    }

    @NotNull
    public static BehaviorControl<Mob> stopAttackIfTargetInvalid(@NotNull Mob pet) {
        return BehaviorBuilder.create((instance) -> {
            return instance.group(
                instance.present(MemoryModuleType.ATTACK_TARGET),
                instance.registered(MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE)).apply(instance, (attackTarget, canReach) -> {
                return (level, mob, num) -> {
                    if (!(mob instanceof ArenaMob arenaMob)) return true;

                    LivingEntity target = instance.get(attackTarget);

                    LivingEntity lastDamager = mob.getLastAttacker();
                    if (lastDamager != null && lastDamager != target && mob.distanceTo(lastDamager) < mob.distanceTo(target)) {
                        MobAI.eraseTarget(mob);
                        return true;
                    }

                    Set<LivingEntity> targetList = getTargetList(arenaMob);
                    if (!targetList.contains(target)) {
                        MobAI.eraseTarget(mob);
                        return true;
                    }

                    return false;
                };
            });
        });
    }

    @NotNull
    public static BehaviorControl<LivingEntity> stopAngryIfTargetDead() {
        return StopBeingAngryIfTargetDead.create();
    }

    @NotNull
    public static BehaviorControl<Mob> reachTargetWhenOutOfRange() {
        Function<LivingEntity, Float> speedFunc = mob -> 1F;
        //SetWalkTargetFromAttackTargetIfTargetOutOfReach
        return BehaviorBuilder.create((instance) -> {
            return instance.group(
                instance.registered(MemoryModuleType.WALK_TARGET),
                instance.registered(MemoryModuleType.LOOK_TARGET),
                instance.present(MemoryModuleType.ATTACK_TARGET)).apply(instance, (walkTarget, lookTarget, attackTarget) -> {
                return (level, mob, num) -> {
                    LivingEntity target = instance.get(attackTarget);
                    if (BehaviorUtils.isWithinAttackRange(mob, target, 1)) {
                        walkTarget.erase();
                    }
                    else {
                        lookTarget.set(new EntityTracker(target, true));
                        walkTarget.set(new WalkTarget(new EntityTracker(target, false), speedFunc.apply(mob), 1));
                    }
                    return true;
                };
            });
        });
    }
}
