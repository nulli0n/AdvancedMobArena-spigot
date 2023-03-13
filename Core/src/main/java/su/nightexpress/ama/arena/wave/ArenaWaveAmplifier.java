package su.nightexpress.ama.arena.wave;

import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.manager.ICleanable;
import su.nexmedia.engine.api.manager.IEditable;
import su.nightexpress.ama.Placeholders;
import su.nightexpress.ama.api.arena.ArenaChild;
import su.nightexpress.ama.api.arena.game.ArenaGameEventTrigger;
import su.nightexpress.ama.api.arena.game.IArenaGameEventListener;
import su.nightexpress.ama.api.event.ArenaGameGenericEvent;
import su.nightexpress.ama.arena.impl.ArenaConfig;
import su.nightexpress.ama.arena.editor.wave.WaveAmplifierSettingsEditor;
import su.nightexpress.ama.arena.impl.Arena;

import java.util.HashSet;
import java.util.Set;
import java.util.function.UnaryOperator;

@Deprecated
public class ArenaWaveAmplifier implements IArenaGameEventListener, ArenaChild, IEditable, ICleanable {

    private final ArenaConfig arenaConfig;
    private final String                        id;
    private final Set<ArenaGameEventTrigger<?>> triggers;

    private int valueAmount;
    private int valueLevel;

    private WaveAmplifierSettingsEditor editor;

    public ArenaWaveAmplifier(
        @NotNull ArenaConfig arenaConfig,
        @NotNull String id,
        @NotNull Set<ArenaGameEventTrigger<?>> triggers,
        int valueAmount, int valueLevel) {
        this.arenaConfig = arenaConfig;
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
    public WaveAmplifierSettingsEditor getEditor() {
        if (this.editor == null) {
            this.editor = new WaveAmplifierSettingsEditor(this.plugin(), this);
        }
        return this.editor;
    }

    @Override
    @NotNull
    public UnaryOperator<String> replacePlaceholders() {
        return str -> str
            .replace(Placeholders.WAVE_AMPLIFIER_TRIGGERS, Placeholders.format(this.getTriggers()))
            .replace(Placeholders.WAVE_AMPLIFIER_ID, this.getId())
            .replace(Placeholders.WAVE_AMPLIFIER_VALUE_AMOUNT, String.valueOf(this.getValueAmount()))
            .replace(Placeholders.WAVE_AMPLIFIER_VALUE_LEVEL, String.valueOf(this.getValueLevel()))
            ;
    }

    @Override
    public boolean onGameEvent(@NotNull ArenaGameGenericEvent gameEvent) {
        if (!this.isReady(gameEvent)) return false;

        Arena arena = gameEvent.getArena();
        arena.addWaveAmplificatorAmount(this.getId(), this.getValueAmount());
        arena.addWaveAmplificatorLevel(this.getId(), this.getValueLevel());
        return true;
    }

    @NotNull
    @Override
    public ArenaConfig getArenaConfig() {
        return this.arenaConfig;
    }

    @NotNull
    public String getId() {
        return this.id;
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
