package su.nightexpress.ama.arena.wave;

import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.placeholder.Placeholder;
import su.nexmedia.engine.api.placeholder.PlaceholderMap;
import su.nexmedia.engine.utils.random.Rnd;
import su.nightexpress.ama.Placeholders;
import su.nightexpress.ama.api.arena.ArenaChild;
import su.nightexpress.ama.arena.editor.wave.WaveMobsEditor;
import su.nightexpress.ama.arena.impl.ArenaConfig;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ArenaWave implements ArenaChild, Placeholder {

    private final ArenaConfig arenaConfig;
    private final String      id;
    private final Set<ArenaWaveMob> mobs;
    private final PlaceholderMap placeholderMap;

    private WaveMobsEditor editor;

    public ArenaWave(
        @NotNull ArenaConfig arenaConfig,
        @NotNull String id,
        @NotNull Set<ArenaWaveMob> mobs
    ) {
        this.arenaConfig = arenaConfig;
        this.id = id.toLowerCase();
        this.mobs = new HashSet<>(mobs);

        this.placeholderMap = new PlaceholderMap()
            .add(Placeholders.ARENA_WAVE_ID, this::getId)
            .add(Placeholders.ARENA_WAVE_MOBS, () -> String.join("\n", this.getMobs().stream().map(ArenaWaveMob::getMobId).toList()))
        ;
    }

    public void clear() {
        if (this.editor != null) {
            this.editor.clear();
            this.editor = null;
        }
    }

    @NotNull
    public WaveMobsEditor getEditor() {
        if (this.editor == null) {
            this.editor = new WaveMobsEditor(this);
        }
        return this.editor;
    }

    @Override
    @NotNull
    public PlaceholderMap getPlaceholders() {
        return this.placeholderMap;
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
    public Set<ArenaWaveMob> getMobs() {
        return this.mobs;
    }

    @NotNull
    public List<ArenaWaveMob> getMobsByChance() {
        return this.getMobs().stream().filter(mob -> mob.getAmount() > 0 && Rnd.chance(mob.getChance())).toList();
    }
}
