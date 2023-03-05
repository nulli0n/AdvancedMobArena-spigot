package su.nightexpress.ama.api.event;

import org.jetbrains.annotations.NotNull;
import su.nightexpress.ama.arena.impl.Arena;
import su.nightexpress.ama.arena.impl.ArenaPlayer;

public class ArenaPlayerReadyEvent extends ArenaPlayerEvent {

    public ArenaPlayerReadyEvent(@NotNull Arena arena, @NotNull ArenaPlayer arenaPlayer) {
        super(arena, arenaPlayer);
    }

    public boolean isReady() {
        return this.getArenaPlayer().isReady();
    }
}
