package su.nightexpress.ama.api.event;

import org.jetbrains.annotations.NotNull;
import su.nightexpress.ama.api.type.GameEventType;
import su.nightexpress.ama.arena.impl.Arena;
import su.nightexpress.ama.arena.spot.ArenaSpot;
import su.nightexpress.ama.arena.spot.SpotState;

public class ArenaSpotStateChangeEvent extends ArenaSpotEvent {

    private final SpotState state;

    public ArenaSpotStateChangeEvent(
        @NotNull Arena arena,
        @NotNull ArenaSpot spot,
        @NotNull SpotState state) {
        super(arena, GameEventType.SPOT_CHANGED, spot);
        this.state = state;
    }

    @NotNull
    public SpotState getNewState() {
        return this.state;
    }
}
