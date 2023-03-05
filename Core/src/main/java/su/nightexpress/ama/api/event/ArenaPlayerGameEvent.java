package su.nightexpress.ama.api.event;

import org.jetbrains.annotations.NotNull;
import su.nightexpress.ama.api.arena.type.ArenaGameEventType;
import su.nightexpress.ama.arena.impl.Arena;
import su.nightexpress.ama.arena.impl.ArenaPlayer;

public class ArenaPlayerGameEvent extends ArenaGameGenericEvent {

    protected final ArenaPlayer arenaPlayer;

    public ArenaPlayerGameEvent(@NotNull Arena arena, @NotNull ArenaPlayer arenaPlayer, @NotNull ArenaGameEventType eventType) {
        super(arena, eventType);
        this.arenaPlayer = arenaPlayer;
    }

    @NotNull
    public final ArenaPlayer getArenaPlayer() {
        return this.arenaPlayer;
    }
}
