package su.nightexpress.ama.api.event;

import org.jetbrains.annotations.NotNull;
import su.nightexpress.ama.api.arena.type.ArenaGameEventType;
import su.nightexpress.ama.arena.AbstractArena;

public abstract class ArenaGameGenericEvent extends ArenaEvent {

    private final ArenaGameEventType eventType;

    public ArenaGameGenericEvent(@NotNull AbstractArena arena, @NotNull ArenaGameEventType eventType) {
        super(arena);
        this.eventType = eventType;
    }

    @NotNull
    public ArenaGameEventType getEventType() {
        return eventType;
    }
}
