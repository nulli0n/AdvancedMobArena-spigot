package su.nightexpress.ama.api.event;

import org.jetbrains.annotations.NotNull;
import su.nightexpress.ama.api.type.GameEventType;
import su.nightexpress.ama.arena.impl.Arena;
import su.nightexpress.ama.arena.impl.ArenaPlayer;

public class ArenaPlayerLeaveEvent extends ArenaPlayerGameEvent {

    public ArenaPlayerLeaveEvent(@NotNull Arena arena, @NotNull ArenaPlayer arenaPlayer) {
        super(arena, arenaPlayer, GameEventType.PLAYER_LEAVE);
    }
}
