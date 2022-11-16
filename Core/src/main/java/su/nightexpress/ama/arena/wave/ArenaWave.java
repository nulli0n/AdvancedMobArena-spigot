package su.nightexpress.ama.arena.wave;

import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.manager.ICleanable;
import su.nexmedia.engine.api.manager.IEditable;
import su.nexmedia.engine.api.manager.IPlaceholder;
import su.nexmedia.engine.utils.random.Rnd;
import su.nightexpress.ama.Placeholders;
import su.nightexpress.ama.api.arena.IArenaObject;
import su.nightexpress.ama.arena.config.ArenaConfig;
import su.nightexpress.ama.arena.editor.wave.EditorWaveSettings;

import java.util.List;
import java.util.Map;
import java.util.function.UnaryOperator;

public class ArenaWave implements IArenaObject, IEditable, ICleanable, IPlaceholder {

    private final ArenaConfig                        arenaConfig;
    private final String                             id;
    private       Map<String, ArenaWaveMob>          mobs;
    private       Map<String, ArenaWaveAmplificator> amplificators;

    private EditorWaveSettings editor;

    public ArenaWave(
        @NotNull ArenaConfig arenaConfig,
        @NotNull String id,
        @NotNull Map<String, ArenaWaveMob> mobs,
        @NotNull Map<String, ArenaWaveAmplificator> amplificators
    ) {
        this.arenaConfig = arenaConfig;
        this.id = id.toLowerCase();
        this.setMobs(mobs);
        this.setAmplificators(amplificators);
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
    public EditorWaveSettings getEditor() {
        if (this.editor == null) {
            this.editor = new EditorWaveSettings(this);
        }
        return this.editor;
    }

    @Override
    @NotNull
    public UnaryOperator<String> replacePlaceholders() {
        return str -> str
            .replace(Placeholders.ARENA_WAVE_ID, this.getId())
            .replace(Placeholders.ARENA_WAVE_MOBS, String.join(DELIMITER_DEFAULT, this.getMobs().values().stream()
                .map(ArenaWaveMob::getMobId).toList()))
            ;
    }

    @NotNull
    @Override
    public ArenaConfig getArenaConfig() {
        return arenaConfig;
    }

    @NotNull
    public String getId() {
        return this.id;
    }

    @NotNull
    public Map<String, ArenaWaveMob> getMobs() {
        return this.mobs;
    }

    @NotNull
    public List<ArenaWaveMob> getMobsByChance() {
        return this.getMobs().values().stream()
            .filter(mob -> mob.getAmount() > 0 && Rnd.chance(mob.getChance())).toList();
    }

    public void setMobs(@NotNull Map<String, ArenaWaveMob> mobs) {
        this.mobs = mobs;
    }

    @NotNull
    public Map<String, ArenaWaveAmplificator> getAmplificators() {
        return amplificators;
    }

    public void setAmplificators(@NotNull Map<String, ArenaWaveAmplificator> amplificators) {
        this.amplificators = amplificators;
    }
}
