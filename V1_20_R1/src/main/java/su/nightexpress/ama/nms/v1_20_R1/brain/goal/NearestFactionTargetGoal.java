package su.nightexpress.ama.nms.v1_20_R1.brain.goal;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.target.TargetGoal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import org.bukkit.craftbukkit.v1_20_R1.entity.CraftLivingEntity;
import org.bukkit.craftbukkit.v1_20_R1.entity.CraftPlayer;
import org.bukkit.event.entity.EntityTargetEvent;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.utils.random.Rnd;
import su.nightexpress.ama.api.arena.IArena;
import su.nightexpress.ama.api.type.MobFaction;

import javax.annotation.Nullable;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class NearestFactionTargetGoal<T extends LivingEntity> extends TargetGoal {

    private static final int DEFAULT_RANDOM_INTERVAL = 10;

    private final IArena              arena;
    private final MobFaction          faction;
    private final TargetingConditions targetConditions;

    private LivingEntity target;

    public NearestFactionTargetGoal(@NotNull Mob mob, @NotNull IArena arena, @NotNull MobFaction faction) {
        super(mob, false, true);
        this.setFlags(EnumSet.of(Flag.TARGET));

        this.arena = arena;
        this.faction = faction;
        this.targetConditions = TargetingConditions.forCombat().ignoreLineOfSight().range(this.getFollowDistance());
    }

    public boolean canUse() {
        this.findTarget();
        return this.target != null;
    }

    protected void findTarget() {
        Set<LivingEntity> targetList = new HashSet<>();
        if (this.faction == MobFaction.ENEMY) {
            targetList.addAll(this.arena.getPlayers().getAlive().stream()
                .map(player -> ((CraftPlayer)player.getPlayer()).getHandle()).collect(Collectors.toSet()));
            targetList.addAll(this.arena.getMobs().getAllies().stream()
                .map(entity -> ((CraftLivingEntity)entity).getHandle()).collect(Collectors.toSet()));
        }
        else if (this.faction == MobFaction.ALLY) {
            targetList.addAll(this.arena.getMobs().getEnemies().stream()
                .map(entity -> ((CraftLivingEntity)entity).getHandle()).collect(Collectors.toSet()));
        }

        targetList.removeIf(entity -> !this.targetConditions.test(this.mob, entity));

        if (!targetList.isEmpty()) {
            this.target = Rnd.get(targetList);
        }
        else this.target = null;
    }

    public void start() {
        EntityTargetEvent.TargetReason reason = this.faction == MobFaction.ENEMY ? EntityTargetEvent.TargetReason.CLOSEST_PLAYER : EntityTargetEvent.TargetReason.CLOSEST_ENTITY;
        this.mob.setTarget(this.target, reason, true);
        super.start();
    }

    public void setTarget(@Nullable LivingEntity target) {
        this.target = target;
    }
}
