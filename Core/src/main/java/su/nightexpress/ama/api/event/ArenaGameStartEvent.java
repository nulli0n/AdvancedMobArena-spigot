package su.nightexpress.ama.api.event;

import org.jetbrains.annotations.NotNull;
import su.nightexpress.ama.api.type.GameEventType;
import su.nightexpress.ama.arena.impl.Arena;

public class ArenaGameStartEvent extends ArenaGameGenericEvent {

    public ArenaGameStartEvent(@NotNull Arena arena) {
        super(arena, GameEventType.GAME_START);
    }
}
