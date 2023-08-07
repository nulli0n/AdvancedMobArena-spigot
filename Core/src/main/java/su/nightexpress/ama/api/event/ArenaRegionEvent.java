package su.nightexpress.ama.api.event;

import org.jetbrains.annotations.NotNull;
import su.nightexpress.ama.api.type.GameEventType;
import su.nightexpress.ama.arena.impl.Arena;
import su.nightexpress.ama.arena.region.ArenaRegion;

public class ArenaRegionEvent extends ArenaGameGenericEvent {

    private final ArenaRegion arenaRegion;

    public ArenaRegionEvent(
        @NotNull Arena arena,
        @NotNull GameEventType eventType,
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
