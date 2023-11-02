package su.nightexpress.ama.api.event;

import org.jetbrains.annotations.NotNull;
import su.nightexpress.ama.api.type.GameEventType;
import su.nightexpress.ama.arena.impl.Arena;
import su.nightexpress.ama.arena.region.Region;

public class ArenaRegionEvent extends ArenaGameGenericEvent {

    private final Region region;

    public ArenaRegionEvent(
        @NotNull Arena arena,
        @NotNull GameEventType eventType,
        @NotNull Region region
    ) {
        super(arena, eventType);
        this.region = region;
    }

    @NotNull
    public Region getArenaRegion() {
        return this.region;
    }
}
