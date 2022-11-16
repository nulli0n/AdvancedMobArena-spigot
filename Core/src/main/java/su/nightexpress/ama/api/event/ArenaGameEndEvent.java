package su.nightexpress.ama.api.event;

import org.jetbrains.annotations.NotNull;
import su.nightexpress.ama.api.arena.type.ArenaEndType;
import su.nightexpress.ama.arena.AbstractArena;

public class ArenaGameEndEvent extends ArenaGameGenericEvent {

	private final ArenaEndType type;

	public ArenaGameEndEvent(@NotNull AbstractArena arena, @NotNull ArenaEndType type) {
		super(arena, type.getGameEventType());
		this.type = type;
	}

	@NotNull
	public ArenaEndType getType() {
		return this.type;
	}
}
