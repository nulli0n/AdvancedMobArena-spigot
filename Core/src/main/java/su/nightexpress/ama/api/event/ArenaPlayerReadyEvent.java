package su.nightexpress.ama.api.event;

import org.jetbrains.annotations.NotNull;
import su.nightexpress.ama.arena.AbstractArena;
import su.nightexpress.ama.arena.ArenaPlayer;

public class ArenaPlayerReadyEvent extends ArenaPlayerEvent {

    public ArenaPlayerReadyEvent(@NotNull AbstractArena arena, @NotNull ArenaPlayer arenaPlayer) {
        super(arena, arenaPlayer);
    }

    public boolean isReady() {
        return this.getArenaPlayer().isReady();
    }
}
