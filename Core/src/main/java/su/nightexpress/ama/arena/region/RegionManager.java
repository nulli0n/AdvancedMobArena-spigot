package su.nightexpress.ama.arena.region;

import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nexmedia.engine.api.config.JYML;
import su.nexmedia.engine.utils.StringUtil;
import su.nightexpress.ama.AMA;
import su.nightexpress.ama.api.arena.ArenaChild;
import su.nightexpress.ama.api.arena.Inspectable;
import su.nightexpress.ama.api.arena.Report;
import su.nightexpress.ama.arena.editor.region.RegionListEditor;
import su.nightexpress.ama.arena.impl.ArenaConfig;

import java.util.*;

public class RegionManager implements ArenaChild, Inspectable {

    public static final String DIR_REGIONS = "/regions/";

    private final AMA plugin;
    private final ArenaConfig         arenaConfig;
    private final Map<String, Region> regionMap;

    private RegionListEditor editor;

    public RegionManager(@NotNull ArenaConfig arenaConfig) {
        this.plugin = arenaConfig.plugin();
        this.arenaConfig = arenaConfig;
        this.regionMap = new HashMap<>();
    }

    public void load() {
        ArenaConfig arenaConfig = this.getArenaConfig();

        for (JYML cfg : JYML.loadAll(this.getRegionsPath(), false)) {
            Region region = new Region(arenaConfig, cfg);
            if (region.load()) {
                this.addRegion(region);
            }
            else plugin.error("Region not loaded '" + cfg.getFile().getName() + "' in '" + arenaConfig.getId() + "' arena!");
        }
    }

    public void shutdown() {
        if (this.editor != null) {
            this.editor.clear();
            this.editor = null;
        }
        this.getRegions().forEach(Region::clear);
        this.getRegions().clear();
    }

    public void save() {
        this.getRegions().forEach(Region::save);
    }

    @NotNull
    @Override
    public Report getReport() {
        Report report = new Report();

        if (this.getRegionMap().isEmpty()) {
            report.addProblem("No regions created!");
        }
        if (this.getDefaultRegion() == null) {
            report.addProblem("No default region!");
        }
        else if (!this.getDefaultRegion().isActive()) {
            report.addProblem("Default region is inactive!");
        }

        for (Region region : this.getRegions()) {
            if (region.isActive() && region.hasProblems()) {
                report.addProblem("Problems with '" + region.getId() + "' region!");
            }
        }

        if (!report.hasProblems() && !report.hasWarns()) {
            report.addGood("All " + this.getRegions().size() + " regions are fine!");
        }

        return report;
    }

    @NotNull
    public RegionListEditor getEditor() {
        if (this.editor == null) {
            this.editor = new RegionListEditor(this.plugin, this);
        }
        return editor;
    }

    @NotNull
    public String getRegionsPath() {
        return this.getArenaConfig().getFile().getParentFile().getAbsolutePath() + DIR_REGIONS;
    }

    @Override
    @NotNull
    public ArenaConfig getArenaConfig() {
        return this.arenaConfig;
    }

    @NotNull
    public Map<String, Region> getRegionMap() {
        return this.regionMap;
    }

    @NotNull
    public Collection<Region> getRegions() {
        return this.getRegionMap().values();
    }

    @Nullable
    public Region getDefaultRegion() {
        return this.getRegions().stream().filter(Region::isDefault).findFirst().orElse(null);
    }

    @Nullable
    public Region getFirstUnlocked() {
        return this.getRegions().stream().filter(Region::isActive).filter(Region::isUnlocked).findFirst().orElse(null);
    }

    @Nullable
    public Region getRegion(@NotNull String id) {
        return this.getRegionMap().get(id.toLowerCase());
    }

    @Nullable
    public Region getRegion(@NotNull Location location) {
        return this.getRegions().stream()
            .filter(region -> region.getCuboid().isPresent() && region.getCuboid().get().contains(location))
            .findFirst().orElse(null);
    }

    public void addRegion(@NotNull Region region) {
        this.getRegionMap().put(region.getId(), region);
    }

    public boolean createRegion(@NotNull String id) {
        id = StringUtil.lowerCaseUnderscore(id);
        if (this.getRegion(id) != null) return false;

        JYML cfg = new JYML(this.getRegionsPath(), id + ".yml");
        Region region = new Region(this.getArenaConfig(), cfg);
        region.setActive(false);
        region.setDefault(this.getRegions().isEmpty());
        region.setName(StringUtil.capitalizeUnderscored(region.getId()) + " Region");
        region.setSpawnLocation(null);
        region.save();
        region.load();
        this.addRegion(region);

        return true;
    }

    public boolean removeRegion(@NotNull Region region) {
        if (region.getFile().delete()) {
            region.clear();
            this.getRegionMap().remove(region.getId());
            return true;
        }
        return false;
    }
}
