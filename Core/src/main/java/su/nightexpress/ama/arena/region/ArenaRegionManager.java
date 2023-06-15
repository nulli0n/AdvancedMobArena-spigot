package su.nightexpress.ama.arena.region;

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
import su.nightexpress.ama.arena.editor.region.RegionListEditor;
import su.nightexpress.ama.arena.impl.ArenaConfig;

import java.util.*;

public class ArenaRegionManager implements ArenaChild, ILoadable, Problematic, Placeholder {

    public static final String DIR_REGIONS = "/regions/";

    private final ArenaConfig arenaConfig;
    private final Map<String, ArenaRegion> regions;
    private final PlaceholderMap placeholderMap;

    private RegionListEditor editor;

    public ArenaRegionManager(@NotNull ArenaConfig arenaConfig) {
        this.arenaConfig = arenaConfig;
        this.regions = new HashMap<>();

        this.placeholderMap = new PlaceholderMap()
            .add(Placeholders.GENERIC_PROBLEMS, () -> String.join("\n", this.getProblems()));
    }

    @Override
    public void setup() {
        for (JYML cfg : JYML.loadAll(this.getRegionsPath(), false)) {
            ArenaRegion region = new ArenaRegion(this.getArenaConfig(), cfg);
            if (region.load()) {
                this.addRegion(region);
            }
            else plugin().error("Region not loaded '" + cfg.getFile().getName() + "' in '" + getArenaConfig().getFile().getName() + "' arena!");
        }

        this.getProblems().forEach(problem -> {
            this.plugin().warn("Problem in '" + getArenaConfig().getId() + "' arena Region Manager: " + problem);
        });
    }

    @Override
    public void shutdown() {
        if (this.editor != null) {
            this.editor.clear();
            this.editor = null;
        }
        this.getRegions().forEach(ArenaRegion::clear);
        this.getRegions().clear();
    }

    public void save() {
        this.getRegions().forEach(ArenaRegion::save);
    }

    @Override
    @NotNull
    public PlaceholderMap getPlaceholders() {
        return this.placeholderMap;
    }

    @Override
    @NotNull
    public List<String> getProblems() {
        List<String> list = new ArrayList<>();
        if (this.getRegionsMap().isEmpty()) {
            list.add(problem("No Regions Created!"));
        }
        if (this.getDefaultRegion() == null) {
            list.add(problem("No Default Region!"));
        }
        else if (!this.getDefaultRegion().isActive()) {
            list.add(problem("Default Region is Inactive!"));
        }

        for (ArenaRegion region : this.getRegions()) {
            if (region.isActive() && region.hasProblems()) {
                list.add(problem("Problems with " + region.getId() + " region!"));
            }
        }
        return list;
    }

    @NotNull
    public RegionListEditor getEditor() {
        if (this.editor == null) {
            this.editor = new RegionListEditor(this);
        }
        return editor;
    }

    @NotNull
    public String getRegionsPath() {
        return this.arenaConfig.getFile().getParentFile().getAbsolutePath() + DIR_REGIONS;
    }

    @Override
    @NotNull
    public ArenaConfig getArenaConfig() {
        return this.arenaConfig;
    }

    @NotNull
    public Map<String, ArenaRegion> getRegionsMap() {
        return this.regions;
    }

    @NotNull
    public Collection<ArenaRegion> getRegions() {
        return this.getRegionsMap().values();
    }

    @Nullable
    public ArenaRegion getDefaultRegion() {
        return this.getRegions().stream().filter(ArenaRegion::isDefault).findFirst().orElse(null);
    }

    @Nullable
    public ArenaRegion getFirstUnlocked() {
        return this.getRegions().stream().filter(ArenaRegion::isActive).filter(ArenaRegion::isUnlocked).findFirst().orElse(null);
    }

    @Nullable
    public ArenaRegion getRegion(@NotNull String id) {
        return this.getRegionsMap().get(id.toLowerCase());
    }

    @Nullable
    public ArenaRegion getRegion(@NotNull Location location) {
        return this.getRegions().stream()
            .filter(region -> region.getCuboid().isPresent() && region.getCuboid().get().contains(location))
            .findFirst().orElse(null);
    }

    public void addRegion(@NotNull ArenaRegion region) {
        this.getRegionsMap().put(region.getId(), region);
    }

    public boolean createRegion(@NotNull String id) {
        if (this.getRegion(id) != null) return false;

        JYML cfg = new JYML(this.getRegionsPath(), id + ".yml");
        ArenaRegion region = new ArenaRegion(this.getArenaConfig(), cfg);
        region.setActive(false);
        region.setDefault(this.getRegions().isEmpty());
        region.setName(StringUtil.capitalizeUnderscored(region.getId()) + " Region");
        region.setSpawnLocation(null);
        region.save();
        region.load();
        this.addRegion(region);

        return true;
    }

    public boolean removeRegion(@NotNull ArenaRegion region) {
        if (region.getFile().delete()) {
            region.clear();
            this.getRegionsMap().remove(region.getId());
            return true;
        }
        return false;
    }
}
