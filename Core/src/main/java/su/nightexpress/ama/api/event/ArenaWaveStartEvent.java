package su.nightexpress.ama.api.event;

import org.jetbrains.annotations.NotNull;
import su.nightexpress.ama.api.type.GameEventType;
import su.nightexpress.ama.arena.impl.Arena;

public class ArenaWaveStartEvent extends ArenaGameGenericEvent {

    public ArenaWaveStartEvent(@NotNull Arena arena) {
        super(arena, GameEventType.WAVE_START);
    }
}
