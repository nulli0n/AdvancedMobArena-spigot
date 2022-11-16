package su.nightexpress.ama.arena.region;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nexmedia.engine.api.manager.ICleanable;
import su.nexmedia.engine.api.manager.IEditable;
import su.nexmedia.engine.utils.random.Rnd;
import su.nightexpress.ama.Placeholders;
import su.nightexpress.ama.api.arena.IArenaObject;
import su.nightexpress.ama.api.arena.game.ArenaGameEventTrigger;
import su.nightexpress.ama.api.arena.game.IArenaGameEventListener;
import su.nightexpress.ama.api.arena.type.ArenaGameEventType;
import su.nightexpress.ama.api.arena.type.ArenaLockState;
import su.nightexpress.ama.api.event.ArenaGameGenericEvent;
import su.nightexpress.ama.arena.AbstractArena;
import su.nightexpress.ama.arena.config.ArenaConfig;
import su.nightexpress.ama.arena.editor.region.EditorRegionWaveSettings;
import su.nightexpress.ama.arena.wave.ArenaWave;
import su.nightexpress.ama.arena.wave.ArenaWaveMob;
import su.nightexpress.ama.arena.wave.ArenaWaveUpcoming;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.UnaryOperator;

public class ArenaRegionWave implements IArenaGameEventListener, IArenaObject, IEditable, ICleanable {

	private final ArenaRegion                   region;
	private final Set<ArenaGameEventTrigger<?>> triggers;
	private final String                        id;

	private Set<String> arenaWaveIds;
	private Set<String> spawnerIds;
	
	private EditorRegionWaveSettings editor;
	
	public ArenaRegionWave(
			@NotNull ArenaRegion region,
			@NotNull String id,
			@NotNull Set<String> arenaWaveIds,
			@NotNull Set<String> spawnerIds,
			@NotNull Set<ArenaGameEventTrigger<?>> triggers) {
		this.region = region;
		this.id = id.toLowerCase();
		this.setArenaWaveIds(arenaWaveIds);
		this.setSpawnerIds(spawnerIds);
		this.triggers = triggers;
	}
	
	@Override
	public void clear() {
		if (this.editor != null) {
			this.editor.clear();
			this.editor = null;
		}
	}

	@Override
	@NotNull
	public UnaryOperator<String> replacePlaceholders() {
		return str -> str
			.replace(Placeholders.REGION_WAVE_TRIGGERS, Placeholders.format(this.getTriggers()))
			.replace(Placeholders.REGION_WAVE_ID, this.getId())
			.replace(Placeholders.REGION_WAVE_WAVE_IDS, String.join(DELIMITER_DEFAULT, this.getArenaWaveIds()))
			.replace(Placeholders.REGION_WAVE_SPAWNERS, String.join(DELIMITER_DEFAULT, this.getSpawnerIds()))
			;
	}

	@Override
	public boolean onGameEvent(@NotNull ArenaGameGenericEvent gameEvent) {
		if (gameEvent.getEventType() != ArenaGameEventType.WAVE_START) return false; // TODO Make support all events
		if (!this.isReady(gameEvent)) return false;

		ArenaRegion region = this.getRegion();
		if (this.getRegion().getState() == ArenaLockState.LOCKED) return false;

		ArenaWave wave = this.getArenaWave();
		if (wave == null) return false;

		// TODO If gradual spawn disabled, instant spawn this wave on arena? in case when using more than wave_start triggers

		// Check if this or linked regions contains players to spawn waves there.
		Set<ArenaRegion> linked = new HashSet<>(this.getArenaConfig().getRegionManager().getLinkedRegions(region));
		boolean hasNear = !this.getRegion().getPlayers().isEmpty() || linked.stream().anyMatch(reg -> !reg.getPlayers().isEmpty());
		if (!hasNear) return false;

		AbstractArena arena = gameEvent.getArena();

		// Generate upcoming arena waves depends on region waves and arena wave mob chances.
		// Also set mob amount and levels depends on the Amplificators.
		// Creates new instances for IArenaWaveMob to not affect default ones.
		List<ArenaWaveMob> mobs = new ArrayList<>(wave.getMobsByChance().stream().map(ArenaWaveMob::new).toList());
		mobs.forEach(mob -> mob.setAmount((int) (mob.getAmount() + arena.getWaveAmplificatorAmount(wave.getId()))));
		mobs.forEach(mob -> mob.setLevel((int) (mob.getLevel() + arena.getWaveAmplificatorLevel(wave.getId()))));
		mobs.removeIf(mob -> mob.getAmount() <= 0);
		if (mobs.isEmpty()) return false;

		arena.getUpcomingWaves().add(new ArenaWaveUpcoming(this, mobs));
		return true;
	}

	@Override
	@NotNull
	public EditorRegionWaveSettings getEditor() {
		if (this.editor == null) {
			this.editor = new EditorRegionWaveSettings(this);
		}
		return this.editor;
	}

	@Override
	@NotNull
	public ArenaConfig getArenaConfig() {
		return this.getRegion().getArenaConfig();
	}

	@NotNull
	@Override
	public Set<ArenaGameEventTrigger<?>> getTriggers() {
		return triggers;
	}

	@NotNull
	public ArenaRegion getRegion() {
		return region;
	}

	@NotNull
	public String getId() {
		return this.id;
	}

	@NotNull
	public Set<String> getArenaWaveIds() {
		return arenaWaveIds;
	}

	public void setArenaWaveIds(@NotNull Set<String> arenaWaveIds) {
		this.arenaWaveIds = arenaWaveIds;
	}

	@Nullable
	public ArenaWave getArenaWave() {
		String waveId = Rnd.get(this.getArenaWaveIds());
		if (waveId == null) return null;

		return this.getArenaConfig().getWaveManager().getWave(waveId);
	}

	@NotNull
	public Set<String> getSpawnerIds() {
		return this.spawnerIds;
	}

	public void setSpawnerIds(@NotNull Set<String> spawnerIds) {
		this.spawnerIds = spawnerIds;
	}
}
