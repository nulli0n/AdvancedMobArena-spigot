package su.nightexpress.ama.api.event;

import org.jetbrains.annotations.NotNull;
import su.nightexpress.ama.api.arena.type.ArenaGameEventType;
import su.nightexpress.ama.arena.impl.Arena;

public class ArenaGameStartEvent extends ArenaGameGenericEvent {

    public ArenaGameStartEvent(@NotNull Arena arena) {
        super(arena, ArenaGameEventType.GAME_START);
    }
}
