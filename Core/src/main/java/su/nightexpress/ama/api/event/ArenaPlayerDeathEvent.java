package su.nightexpress.ama.api.event;

import org.jetbrains.annotations.NotNull;
import su.nightexpress.ama.api.arena.type.ArenaGameEventType;
import su.nightexpress.ama.arena.impl.Arena;
import su.nightexpress.ama.arena.impl.ArenaPlayer;

public class ArenaPlayerDeathEvent extends ArenaPlayerGameEvent {

    public ArenaPlayerDeathEvent(@NotNull Arena arena, @NotNull ArenaPlayer arenaPlayer) {
        super(arena, arenaPlayer, ArenaGameEventType.PLAYER_DEATH);
    }
}
