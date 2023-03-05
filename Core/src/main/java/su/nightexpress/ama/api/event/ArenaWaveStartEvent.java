package su.nightexpress.ama.api.event;

import org.jetbrains.annotations.NotNull;
import su.nightexpress.ama.api.arena.type.ArenaGameEventType;
import su.nightexpress.ama.arena.impl.Arena;

public class ArenaWaveStartEvent extends ArenaGameGenericEvent {

    public ArenaWaveStartEvent(@NotNull Arena arena) {
        super(arena, ArenaGameEventType.WAVE_START);
    }
}
