package su.nightexpress.ama.arena.spot;

import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nexmedia.engine.api.config.JYML;
import su.nexmedia.engine.api.manager.ILoadable;
import su.nexmedia.engine.api.placeholder.Placeholder;
import su.nexmedia.engine.api.placeholder.PlaceholderMap;
import su.nexmedia.engine.utils.StringUtil;
import su.nightexpress.ama.Placeholders;
import su.nightexpress.ama.api.arena.ArenaChild;
import su.nightexpress.ama.api.arena.Problematic;
import su.nightexpress.ama.arena.editor.spot.SpotListEditor;
import su.nightexpress.ama.arena.impl.ArenaConfig;

import java.util.*;

public class ArenaSpotManager implements ArenaChild, ILoadable, Problematic, Placeholder {

    public static final String DIR_SPOTS = "/spots/";

    private final ArenaConfig arenaConfig;
    private final Map<String, ArenaSpot> spots;
    private final PlaceholderMap placeholderMap;

    private SpotListEditor editor;

    public ArenaSpotManager(@NotNull ArenaConfig arenaConfig) {
        this.arenaConfig = arenaConfig;
        this.spots = new HashMap<>();

        this.placeholderMap = new PlaceholderMap()
            .add(Placeholders.GENERIC_PROBLEMS, () -> String.join("\n", this.getProblems()));
    }

    @Override
    public void setup() {
        for (JYML cfg : JYML.loadAll(this.getSpotsPath(), false)) {
            ArenaSpot spot = new ArenaSpot(this.arenaConfig, cfg);
            if (spot.load()) {
                this.spots.put(spot.getId(), spot);
            }
            else arenaConfig.plugin().error("Spot not loaded: '" + cfg.getFile().getName() + "' spot for '" + arenaConfig.getId() + "' arena!");
        }
    }

    @Override
    public void shutdown() {
        if (this.editor != null) {
            this.editor.clear();
            this.editor = null;
        }
        this.getSpots().forEach(ArenaSpot::clear);
        this.getSpotsMap().clear();
    }

    @Override
    @NotNull
    public PlaceholderMap getPlaceholders() {
        return this.placeholderMap;
    }

    @NotNull
    public String getSpotsPath() {
        return this.getArenaConfig().getFile().getParentFile().getAbsolutePath() + DIR_SPOTS;
    }

    @Override
    @NotNull
    public List<String> getProblems() {
        List<String> list = new ArrayList<>();
        this.getSpots().forEach(spot -> {
            if (spot.isActive() && spot.hasProblems()) {
                list.add(problem("Problems with '" + spot.getId() + "' spot!"));
            }
        });

        return list;
    }

    @NotNull
    public SpotListEditor getEditor() {
        if (this.editor == null) {
            this.editor = new SpotListEditor(this);
        }
        return editor;
    }

    @Override
    @NotNull
    public ArenaConfig getArenaConfig() {
        return this.arenaConfig;
    }

    @NotNull
    public Map<String, ArenaSpot> getSpotsMap() {
        return this.spots;
    }

    @NotNull
    public Collection<ArenaSpot> getSpots() {
        return this.getSpotsMap().values();
    }

    public boolean createSpot(@NotNull String id) {
        if (this.getSpot(id) != null) return false;

        String path = this.getSpotsPath() + id + ".yml";
        ArenaSpot spot = new ArenaSpot(this.getArenaConfig(), new JYML(this.getSpotsPath(), id + ".yml"));
        spot.setActive(false);
        spot.setName(StringUtil.capitalizeUnderscored(spot.getId()) + " Spot");
        spot.save();
        spot.load();
        this.addSpot(spot);
        return true;
    }

    public void addSpot(@NotNull ArenaSpot spot) {
        this.getSpotsMap().put(spot.getId(), spot);
    }

    public void removeSpot(@NotNull ArenaSpot spot) {
        if (spot.getFile().delete()) {
            spot.clear();
            this.getSpotsMap().remove(spot.getId());
        }
    }

    @Nullable
    public ArenaSpot getSpot(@NotNull String id) {
        return this.getSpotsMap().get(id.toLowerCase());
    }

    @Nullable
    public ArenaSpot getSpot(@NotNull Location location) {
        return this.getSpots().stream()
            .filter(spot -> spot.getCuboid().isPresent() && spot.getCuboid().get().contains(location))
            .findFirst().orElse(null);
    }
}
