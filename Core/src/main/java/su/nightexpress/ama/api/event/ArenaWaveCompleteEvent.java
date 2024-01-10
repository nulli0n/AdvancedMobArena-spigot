package su.nightexpress.ama.api.event;

import org.jetbrains.annotations.NotNull;
import su.nightexpress.ama.api.type.GameEventType;
import su.nightexpress.ama.arena.impl.Arena;

public class ArenaWaveCompleteEvent extends ArenaGameGenericEvent {

    public ArenaWaveCompleteEvent(@NotNull Arena arena) {
        super(arena, GameEventType.WAVE_END);
    }
}
