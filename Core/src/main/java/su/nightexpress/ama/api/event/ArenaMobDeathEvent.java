package su.nightexpress.ama.api.event;

import org.bukkit.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nightexpress.ama.api.type.GameEventType;
import su.nightexpress.ama.arena.impl.Arena;
import su.nightexpress.ama.arena.impl.ArenaPlayer;

public class ArenaMobDeathEvent extends ArenaGameGenericEvent {

    private       ArenaPlayer  killer;
    private final LivingEntity entity;
    private final String       mobId;

    public ArenaMobDeathEvent(@NotNull Arena arena, @NotNull LivingEntity entity, @NotNull String mobId) {
        super(arena, GameEventType.MOB_KILLED);
        this.entity = entity;
        this.mobId = mobId;
    }

    public void setKiller(@Nullable ArenaPlayer killer) {
        this.killer = killer;
    }

    @Nullable
    public ArenaPlayer getKiller() {
        return killer;
    }

    @NotNull
    public LivingEntity getEntity() {
        return entity;
    }

    @NotNull
    public String getMobId() {
        return mobId;
    }
}
