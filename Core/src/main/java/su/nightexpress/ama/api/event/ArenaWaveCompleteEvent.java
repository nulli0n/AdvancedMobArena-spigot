package su.nightexpress.ama.api.event;

import org.jetbrains.annotations.NotNull;
import su.nightexpress.ama.api.arena.type.ArenaGameEventType;
import su.nightexpress.ama.arena.impl.Arena;

public class ArenaWaveCompleteEvent extends ArenaGameGenericEvent {

    public ArenaWaveCompleteEvent(@NotNull Arena arena) {
        super(arena, ArenaGameEventType.WAVE_END);
    }
}
