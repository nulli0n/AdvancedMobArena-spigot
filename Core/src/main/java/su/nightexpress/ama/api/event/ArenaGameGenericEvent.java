package su.nightexpress.ama.api.event;

import org.jetbrains.annotations.NotNull;
import su.nightexpress.ama.api.type.GameEventType;
import su.nightexpress.ama.arena.impl.Arena;

public abstract class ArenaGameGenericEvent extends ArenaEvent {

    private final GameEventType eventType;

    public ArenaGameGenericEvent(@NotNull Arena arena, @NotNull GameEventType eventType) {
        super(arena);
        this.eventType = eventType;
    }

    @NotNull
    public GameEventType getEventType() {
        return eventType;
    }
}
