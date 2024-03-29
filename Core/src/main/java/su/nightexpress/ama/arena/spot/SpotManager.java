package su.nightexpress.ama.arena.spot;

import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nexmedia.engine.api.config.JYML;
import su.nexmedia.engine.utils.StringUtil;
import su.nightexpress.ama.AMA;
import su.nightexpress.ama.api.arena.ArenaChild;
import su.nightexpress.ama.api.arena.Inspectable;
import su.nightexpress.ama.api.arena.Report;
import su.nightexpress.ama.arena.editor.spot.SpotListEditor;
import su.nightexpress.ama.arena.impl.ArenaConfig;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class SpotManager implements ArenaChild, Inspectable {

    public static final String DIR_SPOTS = "/spots/";

    private final AMA         plugin;
    private final ArenaConfig       arenaConfig;
    private final Map<String, Spot> spots;

    private SpotListEditor editor;

    public SpotManager(@NotNull ArenaConfig arenaConfig) {
        this.plugin = arenaConfig.plugin();
        this.arenaConfig = arenaConfig;
        this.spots = new HashMap<>();
    }

    public void setup() {
        for (JYML cfg : JYML.loadAll(this.getSpotsPath(), false)) {
            Spot spot = new Spot(this.arenaConfig, cfg);
            if (spot.load()) {
                this.spots.put(spot.getId(), spot);
            }
            else arenaConfig.plugin().error("Spot not loaded: '" + cfg.getFile().getName() + "' spot for '" + arenaConfig.getId() + "' arena!");
        }
    }
    public void shutdown() {
        if (this.editor != null) {
            this.editor.clear();
            this.editor = null;
        }
        this.getSpots().forEach(Spot::clear);
        this.getSpotsMap().clear();
    }

    @NotNull
    @Override
    public Report getReport() {
        Report report = new Report();

        this.getSpots().forEach(spot -> {
            if (spot.isActive() && spot.hasProblems()) {
                report.addProblem("Problems with '" + spot.getId() + "' spot!");
            }
        });

        if (!report.hasProblems() && !report.hasWarns()) {
            report.addGood("All " + this.getSpots().size() + " spots are fine!");
        }

        return report;
    }

    @NotNull
    public String getSpotsPath() {
        return this.getArenaConfig().getFile().getParentFile().getAbsolutePath() + DIR_SPOTS;
    }

    @NotNull
    public SpotListEditor getEditor() {
        if (this.editor == null) {
            this.editor = new SpotListEditor(this.plugin, this);
        }
        return editor;
    }

    @Override
    @NotNull
    public ArenaConfig getArenaConfig() {
        return this.arenaConfig;
    }

    @NotNull
    public Map<String, Spot> getSpotsMap() {
        return this.spots;
    }

    @NotNull
    public Collection<Spot> getSpots() {
        return this.getSpotsMap().values();
    }

    public boolean createSpot(@NotNull String id) {
        id = StringUtil.lowerCaseUnderscore(id);

        if (this.getSpot(id) != null) return false;

        String path = this.getSpotsPath() + id + ".yml";
        Spot spot = new Spot(this.getArenaConfig(), new JYML(this.getSpotsPath(), id + ".yml"));
        spot.setActive(false);
        spot.setName(StringUtil.capitalizeUnderscored(spot.getId()) + " Spot");
        spot.save();
        spot.load();
        this.addSpot(spot);
        return true;
    }

    public void addSpot(@NotNull Spot spot) {
        this.getSpotsMap().put(spot.getId(), spot);
    }

    public void removeSpot(@NotNull Spot spot) {
        if (spot.getFile().delete()) {
            spot.clear();
            this.getSpotsMap().remove(spot.getId());
        }
    }

    @Nullable
    public Spot getSpot(@NotNull String id) {
        return this.getSpotsMap().get(id.toLowerCase());
    }

    @Nullable
    public Spot getSpot(@NotNull Location location) {
        return this.getSpots().stream()
            .filter(spot -> spot.getCuboid().isPresent() && spot.getCuboid().get().contains(location))
            .findFirst().orElse(null);
    }
}
