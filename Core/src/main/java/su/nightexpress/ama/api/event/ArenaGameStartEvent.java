package su.nightexpress.ama.api.event;

import org.jetbrains.annotations.NotNull;
import su.nightexpress.ama.api.arena.type.ArenaGameEventType;
import su.nightexpress.ama.arena.AbstractArena;

public class ArenaGameStartEvent extends ArenaGameGenericEvent {

	public ArenaGameStartEvent(@NotNull AbstractArena arena) {
		super(arena, ArenaGameEventType.GAME_START);
	}
}
