package su.nightexpress.ama.api.event;

import org.jetbrains.annotations.NotNull;
import su.nightexpress.ama.arena.AbstractArena;
import su.nightexpress.ama.arena.ArenaPlayer;

public abstract class ArenaPlayerEvent extends ArenaEvent {

    private final ArenaPlayer arenaPlayer;

    public ArenaPlayerEvent(@NotNull AbstractArena arena, @NotNull ArenaPlayer arenaPlayer) {
        super(arena);
        this.arenaPlayer = arenaPlayer;
    }

    @NotNull
    public final ArenaPlayer getArenaPlayer() {
        return this.arenaPlayer;
    }
}
