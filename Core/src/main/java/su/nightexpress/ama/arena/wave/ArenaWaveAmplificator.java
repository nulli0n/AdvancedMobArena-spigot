package su.nightexpress.ama.arena.wave;

import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.manager.ICleanable;
import su.nexmedia.engine.api.manager.IEditable;
import su.nightexpress.ama.Placeholders;
import su.nightexpress.ama.api.arena.IArenaObject;
import su.nightexpress.ama.api.arena.game.ArenaGameEventTrigger;
import su.nightexpress.ama.api.arena.game.IArenaGameEventListener;
import su.nightexpress.ama.api.event.ArenaGameGenericEvent;
import su.nightexpress.ama.arena.AbstractArena;
import su.nightexpress.ama.arena.config.ArenaConfig;
import su.nightexpress.ama.arena.editor.wave.EditorWaveAmplificatorMain;

import java.util.HashSet;
import java.util.Set;
import java.util.function.UnaryOperator;

public class ArenaWaveAmplificator implements IArenaGameEventListener, IArenaObject, IEditable, ICleanable {

	private final ArenaWave                     arenaWave;
	private final String                        id;
	private final Set<ArenaGameEventTrigger<?>> triggers;
	private       int                           valueAmount;
	private       int                           valueLevel;
	
	private EditorWaveAmplificatorMain editor;
	
	public ArenaWaveAmplificator(
			@NotNull ArenaWave arenaWave,
			@NotNull String id,
			@NotNull Set<ArenaGameEventTrigger<?>> triggers,
			int valueAmount, int valueLevel) {
		this.arenaWave = arenaWave;
		this.id = id.toLowerCase();
		this.triggers = new HashSet<>(triggers);
		this.setValueAmount(valueAmount);
		this.setValueLevel(valueLevel);
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
	public EditorWaveAmplificatorMain getEditor() {
		if (this.editor == null) {
			this.editor = new EditorWaveAmplificatorMain(this.getArenaWave().plugin(), this);
		}
		return this.editor;
	}

	@Override
	@NotNull
	public UnaryOperator<String> replacePlaceholders() {
		return str -> str
			.replace(Placeholders.WAVE_AMPLIFICATOR_TRIGGERS, Placeholders.format(this.getTriggers()))
			.replace(Placeholders.WAVE_AMPLIFICATOR_ID, this.getId())
			.replace(Placeholders.WAVE_AMPLIFICATOR_VALUE_AMOUNT, String.valueOf(this.getValueAmount()))
			.replace(Placeholders.WAVE_AMPLIFICATOR_VALUE_LEVEL, String.valueOf(this.getValueLevel()))
			;
	}

	@Override
	public boolean onGameEvent(@NotNull ArenaGameGenericEvent gameEvent) {
		if (!this.isReady(gameEvent)) return false;

		AbstractArena arena = gameEvent.getArena();
		arena.addWaveAmplificatorAmount(this.getArenaWave().getId(), this.getValueAmount());
		arena.addWaveAmplificatorLevel(this.getArenaWave().getId(), this.getValueLevel());
		return true;
	}

	@NotNull
	@Override
	public ArenaConfig getArenaConfig() {
		return this.getArenaWave().getArenaConfig();
	}

	@NotNull
	public String getId() {
		return this.id;
	}

	@NotNull
	public ArenaWave getArenaWave() {
		return arenaWave;
	}

	@NotNull
	@Override
	public Set<ArenaGameEventTrigger<?>> getTriggers() {
		return triggers;
	}

	public int getValueAmount() {
		return valueAmount;
	}

	public void setValueAmount(int valueAmount) {
		this.valueAmount = valueAmount;
	}

	public int getValueLevel() {
		return valueLevel;
	}

	public void setValueLevel(int valueLevel) {
		this.valueLevel = valueLevel;
	}
}
