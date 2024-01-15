package su.nightexpress.ama.nms.v1_19_R3.brain.goal;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.target.TargetGoal;
import org.bukkit.event.entity.EntityTargetEvent;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.ama.api.arena.IArena;
import su.nightexpress.ama.api.type.MobFaction;
import su.nightexpress.ama.nms.v1_19_R3.brain.MobAI;

import javax.annotation.Nullable;
import java.util.EnumSet;

public class NearestFactionTargetGoal<T extends LivingEntity> extends TargetGoal {

    //private static final int DEFAULT_RANDOM_INTERVAL = 5;

    private final IArena     arena;
    private final MobFaction faction;

    private LivingEntity target;
    private int unseenTicks;

    public NearestFactionTargetGoal(@NotNull Mob mob, @NotNull IArena arena, @NotNull MobFaction faction) {
        super(mob, false, true);
        this.setFlags(EnumSet.of(Flag.TARGET));

        this.arena = arena;
        this.faction = faction;
    }

    public boolean canUse() {
        //if (this.mob.getRandom().nextInt(DEFAULT_RANDOM_INTERVAL) != 0) return false;

        this.findTarget();
        return this.target != null;
    }

    @Override
    public boolean canContinueToUse() {
        LivingEntity target = this.mob.getTarget();
        if (target == null) {
            target = this.target;
        }
        if (target == null) return false;
        if (this.arena.getMobs().getFaction((org.bukkit.entity.LivingEntity) target.getBukkitEntity()) == this.faction) return false;
        if (!this.mob.canAttack(target)) return false;

        if (this.mustSee) {
            if (this.mob.getSensing().hasLineOfSight(target)) {
                this.unseenTicks = 0;
            }
            else if (++this.unseenTicks > reducedTickDelay(this.unseenMemoryTicks)) {
                return false;
            }
        }

        LivingEntity nearest = MobAI.getNearestTarget(this.mob, this.faction, this.arena);
        if (target != nearest) return false;

        this.mob.setTarget(target, EntityTargetEvent.TargetReason.CLOSEST_ENTITY, true);
        return true;
    }

    protected void findTarget() {
        this.target = MobAI.getNearestTarget(this.mob, this.faction, this.arena);
    }

    public void start() {
        this.unseenTicks = 0;

        EntityTargetEvent.TargetReason reason = this.faction == MobFaction.ENEMY ? EntityTargetEvent.TargetReason.CLOSEST_PLAYER : EntityTargetEvent.TargetReason.CLOSEST_ENTITY;
        this.mob.setTarget(this.target, reason, true);
        super.start();
    }

    public void setTarget(@Nullable LivingEntity target) {
        this.target = target;
    }
}
