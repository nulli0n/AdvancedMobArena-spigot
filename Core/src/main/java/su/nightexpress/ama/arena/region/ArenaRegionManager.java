package su.nightexpress.ama.arena.region;

import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nexmedia.engine.api.config.JYML;
import su.nexmedia.engine.api.manager.IEditable;
import su.nexmedia.engine.api.manager.ILoadable;
import su.nightexpress.ama.Placeholders;
import su.nightexpress.ama.api.arena.ArenaChild;
import su.nightexpress.ama.api.arena.IProblematic;
import su.nightexpress.ama.api.arena.type.ArenaLockState;
import su.nightexpress.ama.arena.impl.ArenaConfig;
import su.nightexpress.ama.arena.editor.region.EditorRegionList;

import java.util.*;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;

public class ArenaRegionManager implements ArenaChild, ILoadable, IEditable, IProblematic {

    public static final String DIR_REGIONS = "/regions/";

    private final ArenaConfig arenaConfig;
    private final Map<String, ArenaRegion> regions;

    private EditorRegionList editor;

    public ArenaRegionManager(@NotNull ArenaConfig arenaConfig) {
        this.arenaConfig = arenaConfig;
        this.regions = new HashMap<>();
    }

    @Override
    public void setup() {
        String path = this.arenaConfig.getFile().getParentFile().getAbsolutePath() + DIR_REGIONS;
        for (JYML rCfg : JYML.loadAll(path, false)) {
            try {
                ArenaRegion region = new ArenaRegion(this.arenaConfig, rCfg);
                this.addRegion(region);
            }
            catch (Exception e) {
                arenaConfig.plugin().error("Could not load '" + rCfg.getFile().getName() + "' region in '" + arenaConfig.getFile().getName() + "' arena!");
                e.printStackTrace();
            }
        }

        this.getProblems().forEach(problem -> {
            this.plugin().warn("Problem in '" + arenaConfig.getId() + "' arena Region Manager: " + problem);
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
        this.regions.values().forEach(ArenaRegion::save);
    }

    @Override
    @NotNull
    public UnaryOperator<String> replacePlaceholders() {
        return str -> str.replace(Placeholders.GENERIC_PROBLEMS, Placeholders.formatProblems(this.getProblems()));
    }

    @Override
    @NotNull
    public List<String> getProblems() {
        List<String> list = new ArrayList<>();
        if (this.regions.isEmpty()) {
            list.add("No Regions Defined!");
        }
        if (this.getRegionDefault() == null) {
            list.add("No Default Region!");
        }
        else if (!this.getRegionDefault().isActive()) {
            list.add("Default Region is Inactive!");
        }

        for (ArenaRegion region : this.getRegions()) {
            if (region.isActive() && region.hasProblems()) {
                list.add("Problems with " + region.getId() + " region!");
            }
        }
        return list;
    }

    @NotNull
    @Override
    public EditorRegionList getEditor() {
        if (editor == null) {
            editor = new EditorRegionList(this);
        }
        return editor;
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
    public ArenaRegion getRegionDefault() {
        return this.getRegions().stream().filter(ArenaRegion::isDefault).findFirst().orElse(null);
    }

    @Nullable
    public ArenaRegion getRegionAnyAvailable() {
        return this.getRegions().stream().filter(reg -> reg.getState() == ArenaLockState.UNLOCKED).findFirst().orElse(null);
    }

    @Nullable
    public ArenaRegion getRegion(@NotNull String id) {
        return this.getRegionsMap().get(id.toLowerCase());
    }

    @Nullable
    public ArenaRegion getRegion(@NotNull Location location) {
        return this.getRegions().stream().filter(reg -> reg.getCuboid().contains(location)).findFirst().orElse(null);
    }

    @NotNull
    public Set<ArenaRegion> getLinkedRegions(@NotNull ArenaRegion region) {
        return region.getLinkedRegions().stream().map(this::getRegion).filter(Objects::nonNull).collect(Collectors.toSet());
    }

    public void addRegion(@NotNull ArenaRegion region) {
        this.getRegionsMap().put(region.getId(), region);
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
