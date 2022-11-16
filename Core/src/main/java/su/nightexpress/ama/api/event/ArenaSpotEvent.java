package su.nightexpress.ama.api.event;

import org.jetbrains.annotations.NotNull;
import su.nightexpress.ama.api.arena.type.ArenaGameEventType;
import su.nightexpress.ama.arena.AbstractArena;
import su.nightexpress.ama.arena.spot.ArenaSpot;

public abstract class ArenaSpotEvent extends ArenaGameGenericEvent {

	private final ArenaSpot spot;
	
	public ArenaSpotEvent(
			@NotNull AbstractArena arena,
			@NotNull ArenaGameEventType eventType,
			@NotNull ArenaSpot spot) {
		super(arena, eventType);
		this.spot = spot;
	}

	@NotNull
	public ArenaSpot getSpot() {
		return this.spot;
	}
}
