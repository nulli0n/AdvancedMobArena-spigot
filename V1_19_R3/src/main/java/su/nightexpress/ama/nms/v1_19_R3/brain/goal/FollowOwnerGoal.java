package su.nightexpress.ama.nms.v1_19_R3.brain.goal;

import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import org.jetbrains.annotations.NotNull;

import java.util.EnumSet;

public class FollowOwnerGoal extends Goal {

    private final Mob            mob;
    private final Player         owner;
    private final double         speedModifier;
    private final PathNavigation navigation;
    private final float          stopDistance;

    private Player target;
    private int    timeToRecalcPath;
    private float oldWaterCost;

    public FollowOwnerGoal(@NotNull Mob mob, @NotNull Player owner) {
        this.mob = mob;
        this.owner = owner;
        this.speedModifier = 1F;
        this.navigation = mob.getNavigation();
        this.stopDistance = 2F;
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
    }

    public boolean canUse() {
        if (this.owner == null || this.owner.isSpectator() || this.owner.isDeadOrDying()) return false;
        if (this.unableToMove()) return false;

        this.target = this.owner;
        return true;
    }

    @Override
    public boolean canContinueToUse() {
        if (this.navigation.isDone() || !this.unableToMove()) return false;

        return this.mob.distanceToSqr(this.target) > (double) (this.stopDistance * this.stopDistance);
    }

    private boolean unableToMove() {
        return this.mob.isPassenger() || this.mob.isLeashed();
    }

    @Override
    public void start() {
        this.timeToRecalcPath = 0;
        this.oldWaterCost = this.mob.getPathfindingMalus(BlockPathTypes.WATER);
        this.mob.setPathfindingMalus(BlockPathTypes.WATER, 0F);
    }

    @Override
    public void stop() {
        this.target = null;
        this.navigation.stop();
        this.mob.setPathfindingMalus(BlockPathTypes.WATER, this.oldWaterCost);
    }

    @Override
    public void tick() {
        this.mob.getLookControl().setLookAt(this.target, 10.0F, (float) this.mob.getMaxHeadXRot());

        if (--this.timeToRecalcPath <= 0) {
            double distance = this.mob.distanceToSqr(this.target);
            this.timeToRecalcPath = this.adjustedTickDelay(10);

            if (distance > 1024.0) {
                this.timeToRecalcPath += 10;
            }
            else if (distance > 256.0) {
                this.timeToRecalcPath += 5;
            }
            if (!this.mob.getNavigation().moveTo(target, this.speedModifier)) {
                this.timeToRecalcPath += 15;
            }
        }
    }
}
