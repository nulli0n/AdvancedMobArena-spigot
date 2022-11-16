package su.nightexpress.ama.api.event;

import org.jetbrains.annotations.NotNull;
import su.nightexpress.ama.api.arena.type.ArenaGameEventType;
import su.nightexpress.ama.arena.AbstractArena;
import su.nightexpress.ama.arena.spot.ArenaSpot;
import su.nightexpress.ama.arena.spot.ArenaSpotState;

public class ArenaSpotStateChangeEvent extends ArenaSpotEvent {

    private final ArenaSpotState state;

    public ArenaSpotStateChangeEvent(
        @NotNull AbstractArena arena,
        @NotNull ArenaSpot spot,
        @NotNull ArenaSpotState state) {
        super(arena, ArenaGameEventType.SPOT_CHANGED, spot);
        this.state = state;
    }

    @NotNull
    public ArenaSpotState getNewState() {
        return this.state;
    }
}
