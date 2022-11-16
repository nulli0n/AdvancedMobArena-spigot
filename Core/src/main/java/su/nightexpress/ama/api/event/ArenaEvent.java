package su.nightexpress.ama.api.event;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.ama.arena.AbstractArena;

public abstract class ArenaEvent extends Event {

	private final AbstractArena arena;

	private static final HandlerList handlerList = new HandlerList();

	public ArenaEvent(@NotNull AbstractArena arena) {
	    this.arena = arena;
	}
	
	@NotNull
	public AbstractArena getArena() {
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
