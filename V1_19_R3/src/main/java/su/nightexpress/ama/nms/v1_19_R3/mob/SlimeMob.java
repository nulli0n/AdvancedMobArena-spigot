package su.nightexpress.ama.nms.v1_19_R3.mob;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Slime;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.ama.api.arena.IArena;
import su.nightexpress.ama.api.type.MobFaction;
import su.nightexpress.ama.nms.ArenaMob;

public class SlimeMob extends Slime implements ArenaMob {

    private final IArena arena;
    private final MobFaction faction;

    public SlimeMob(@NotNull ServerLevel world, @NotNull IArena arena, @NotNull MobFaction faction) {
        super(EntityType.SLIME, world);
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
    protected int getJumpDelay() {
        return 8;
    }

    @Override
    protected boolean isDealsDamage() {
        return true;
    }

    @Override
    protected void dealDamage(LivingEntity target) {
        if (this.isAlive()) {
            if (this.getArena().getMobs().getFaction((org.bukkit.entity.LivingEntity) target.getBukkitEntity()) == this.getFaction()) return;

            int size = Math.max(2, this.getSize());
            double dist = this.distanceToSqr(target);
            double offset = 0.6D;

            if (dist < offset * size * offset * size && this.hasLineOfSight(target) && target.hurt(this.damageSources().mobAttack(this), this.getAttackDamage())) {
                this.playSound(SoundEvents.SLIME_ATTACK, 1.0F, (this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F);
                this.doEnchantDamageEffects(this, target);
            }
        }
    }
}
