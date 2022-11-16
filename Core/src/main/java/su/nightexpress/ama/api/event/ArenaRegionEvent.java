package su.nightexpress.ama.api.event;

import org.jetbrains.annotations.NotNull;
import su.nightexpress.ama.api.arena.type.ArenaGameEventType;
import su.nightexpress.ama.arena.AbstractArena;
import su.nightexpress.ama.arena.region.ArenaRegion;

public class ArenaRegionEvent extends ArenaGameGenericEvent {

    private final ArenaRegion arenaRegion;

    public ArenaRegionEvent(
        @NotNull AbstractArena arena,
        @NotNull ArenaGameEventType eventType,
        @NotNull ArenaRegion arenaRegion
    ) {
        super(arena, eventType);
        this.arenaRegion = arenaRegion;
    }

    @NotNull
    public ArenaRegion getArenaRegion() {
        return this.arenaRegion;
    }
}
