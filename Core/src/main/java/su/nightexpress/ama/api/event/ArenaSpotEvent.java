package su.nightexpress.ama.api.event;

import org.jetbrains.annotations.NotNull;
import su.nightexpress.ama.api.type.GameEventType;
import su.nightexpress.ama.arena.impl.Arena;
import su.nightexpress.ama.arena.spot.ArenaSpot;

public abstract class ArenaSpotEvent extends ArenaGameGenericEvent {

    private final ArenaSpot spot;

    public ArenaSpotEvent(
        @NotNull Arena arena,
        @NotNull GameEventType eventType,
        @NotNull ArenaSpot spot) {
        super(arena, eventType);
        this.spot = spot;
    }

    @NotNull
    public ArenaSpot getSpot() {
        return this.spot;
    }
}
