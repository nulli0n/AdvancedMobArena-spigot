package su.nightexpress.ama.api.event;

import org.jetbrains.annotations.NotNull;
import su.nightexpress.ama.api.arena.type.ArenaGameEventType;
import su.nightexpress.ama.arena.AbstractArena;

public class ArenaWaveStartEvent extends ArenaGameGenericEvent {

    public ArenaWaveStartEvent(@NotNull AbstractArena arena) {
        super(arena, ArenaGameEventType.WAVE_START);
    }
}
