package su.nightexpress.ama.api.event;

import org.jetbrains.annotations.NotNull;
import su.nightexpress.ama.api.arena.type.ArenaGameEventType;
import su.nightexpress.ama.arena.AbstractArena;
import su.nightexpress.ama.arena.ArenaPlayer;

public class ArenaPlayerDeathEvent extends ArenaPlayerGameEvent {

    public ArenaPlayerDeathEvent(@NotNull AbstractArena arena, @NotNull ArenaPlayer arenaPlayer) {
        super(arena, arenaPlayer, ArenaGameEventType.PLAYER_DEATH);
    }
}
