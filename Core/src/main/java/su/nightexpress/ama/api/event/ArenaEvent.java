package su.nightexpress.ama.api.event;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.ama.arena.impl.Arena;

public abstract class ArenaEvent extends Event {

    private final Arena arena;

    private static final HandlerList handlerList = new HandlerList();

    public ArenaEvent(@NotNull Arena arena) {
        this.arena = arena;
    }

    @NotNull
    public Arena getArena() {
        return this.arena;
    }

    public static HandlerList getHandlerList() {
        return handlerList;
    }

    @NotNull
    @Override
    public HandlerList getHandlers() {
        return handlerList;
    }
}
