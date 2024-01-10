package su.nightexpress.ama.nms.v1_20_R2.mob;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.IronGolem;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.ama.api.arena.IArena;
import su.nightexpress.ama.api.type.MobFaction;
import su.nightexpress.ama.nms.ArenaMob;

public class IronGolemMob extends IronGolem implements ArenaMob {

    private final IArena     arena;
    private final MobFaction faction;

    public IronGolemMob(@NotNull ServerLevel world, @NotNull IArena arena, @NotNull MobFaction faction) {
        super(EntityType.IRON_GOLEM, world);
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
    protected void doPush(Entity entity) {
        entity.push(this);
    }
}
