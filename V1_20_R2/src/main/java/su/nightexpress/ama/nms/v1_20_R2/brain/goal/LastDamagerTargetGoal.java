package su.nightexpress.ama.nms.v1_20_R2.brain.goal;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.target.TargetGoal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import org.bukkit.event.entity.EntityTargetEvent;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.ama.api.arena.IArena;
import su.nightexpress.ama.api.type.MobFaction;

import java.util.EnumSet;

public class LastDamagerTargetGoal extends TargetGoal {

    private static final TargetingConditions CONDITIONS = TargetingConditions.forCombat().ignoreLineOfSight().ignoreInvisibilityTesting();

    private final IArena     arena;
    private final MobFaction faction;

    private int timestamp;

    public LastDamagerTargetGoal(@NotNull Mob mob, @NotNull IArena arena, @NotNull MobFaction faction) {
        super(mob, true);
        this.setFlags(EnumSet.of(Flag.TARGET));

        this.arena = arena;
        this.faction = faction;
    }

    public void start() {
        this.mob.setTarget(this.mob.getLastHurtByMob(), EntityTargetEvent.TargetReason.TARGET_ATTACKED_ENTITY, true);
        this.targetMob = this.mob.getTarget();
        this.timestamp = this.mob.getLastHurtByMobTimestamp();
        this.unseenMemoryTicks = 300;

        super.start();
    }

    public boolean canUse() {
        int lastHurtDate = this.mob.getLastHurtByMobTimestamp();
        LivingEntity lastDamager = this.mob.getLastHurtByMob();
        if (lastHurtDate == this.timestamp || lastDamager == null) return false;

        if (this.arena.getMobs().getFaction((org.bukkit.entity.LivingEntity) lastDamager.getBukkitEntity()) == this.faction) {
            return false;
        }

        LivingEntity currentTarget = this.mob.getTarget();
        if (currentTarget != null) {
            if (currentTarget == lastDamager) return false;
            if (this.mob.distanceTo(lastDamager) > this.mob.distanceTo(currentTarget)) return false;
        }

        return this.canAttack(lastDamager, CONDITIONS);
    }
}
