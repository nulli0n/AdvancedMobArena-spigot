package su.nightexpress.ama.api.event;

import org.jetbrains.annotations.NotNull;
import su.nightexpress.ama.api.type.GameEventType;
import su.nightexpress.ama.arena.impl.Arena;
import su.nightexpress.ama.arena.spot.Spot;

public abstract class ArenaSpotEvent extends ArenaGameGenericEvent {

    private final Spot spot;

    public ArenaSpotEvent(
        @NotNull Arena arena,
        @NotNull GameEventType eventType,
        @NotNull Spot spot) {
        super(arena, eventType);
        this.spot = spot;
    }

    @NotNull
    public Spot getSpot() {
        return this.spot;
    }
}
