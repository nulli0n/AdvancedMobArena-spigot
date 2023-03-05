package su.nightexpress.ama.api.event;

import org.jetbrains.annotations.NotNull;
import su.nightexpress.ama.arena.impl.Arena;
import su.nightexpress.ama.arena.impl.ArenaPlayer;

public abstract class ArenaPlayerEvent extends ArenaEvent {

    private final ArenaPlayer arenaPlayer;

    public ArenaPlayerEvent(@NotNull Arena arena, @NotNull ArenaPlayer arenaPlayer) {
        super(arena);
        this.arenaPlayer = arenaPlayer;
    }

    @NotNull
    public final ArenaPlayer getArenaPlayer() {
        return this.arenaPlayer;
    }
}
