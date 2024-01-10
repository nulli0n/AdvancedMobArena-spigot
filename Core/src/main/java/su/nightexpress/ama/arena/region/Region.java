package su.nightexpress.ama.arena.region;

import org.bukkit.Location;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nexmedia.engine.api.config.JYML;
import su.nexmedia.engine.api.manager.AbstractConfigHolder;
import su.nexmedia.engine.api.placeholder.Placeholder;
import su.nexmedia.engine.api.placeholder.PlaceholderMap;
import su.nexmedia.engine.lang.LangManager;
import su.nexmedia.engine.utils.Colorizer;
import su.nexmedia.engine.utils.LocationUtil;
import su.nightexpress.ama.AMA;
import su.nightexpress.ama.Placeholders;
import su.nightexpress.ama.api.arena.ArenaChild;
import su.nightexpress.ama.api.arena.Inspectable;
import su.nightexpress.ama.api.arena.Report;
import su.nightexpress.ama.api.event.ArenaRegionEvent;
import su.nightexpress.ama.api.hologram.HologramHolder;
import su.nightexpress.ama.api.hologram.HologramType;
import su.nightexpress.ama.api.type.GameEventType;
import su.nightexpress.ama.api.type.GameState;
import su.nightexpress.ama.api.type.PlayerType;
import su.nightexpress.ama.arena.editor.region.RegionMainEditor;
import su.nightexpress.ama.arena.impl.ArenaConfig;
import su.nightexpress.ama.arena.impl.ArenaPlayer;
import su.nightexpress.ama.arena.lock.LockState;
import su.nightexpress.ama.arena.lock.Lockable;
import su.nightexpress.ama.arena.util.ArenaCuboid;
import su.nightexpress.ama.arena.util.BlockPos;
import su.nightexpress.ama.hologram.HologramManager;

import java.util.*;
import java.util.stream.Collectors;

public class Region extends AbstractConfigHolder<AMA> implements ArenaChild, Lockable, HologramHolder, Inspectable, Placeholder {

    private final ArenaConfig                arenaConfig;
    private final Map<String, Set<BlockPos>> mobSpawners;
    private final Set<Location>              hologramLocations;
    private final Set<UUID>             hologramIds;
    private final PlaceholderMap        placeholderMap;

    private boolean     isActive;
    private boolean     isDefault;
    private LockState   lockState;
    private String      name;
    private ArenaCuboid cuboid;
    private Location    spawnLocation;

    private RegionMainEditor editor;

    public Region(@NotNull ArenaConfig arenaConfig, @NotNull JYML cfg) {
        super(arenaConfig.plugin(), cfg);
        this.arenaConfig = arenaConfig;
        this.mobSpawners = new HashMap<>();
        this.hologramLocations = new HashSet<>();
        this.hologramIds = new HashSet<>();
        this.lockState = LockState.UNLOCKED;

        this.placeholderMap = new PlaceholderMap()
            .add(Placeholders.REGION_REPORT, () -> String.join("\n", this.getReport().getFullReport()))
            .add(Placeholders.REGION_FILE, () -> this.getFile().getName())
            .add(Placeholders.REGION_ID, this::getId)
            .add(Placeholders.REGION_NAME, this::getName)
            .add(Placeholders.REGION_ACTIVE, () -> LangManager.getBoolean(this.isActive()))
            .add(Placeholders.REGION_DEFAULT, () -> LangManager.getBoolean(this.isDefault()))
            .add(Placeholders.REGION_STATE, () -> this.plugin().getLangManager().getEnum(this.getLockState()))
        ;
    }

    @Override
    public boolean load() {
        this.setActive(cfg.getBoolean("Enabled"));
        this.setDefault(cfg.getBoolean("Is_Default"));
        this.lockState = LockState.UNLOCKED;
        this.setName(cfg.getString("Name", this.getId()));

        Location from = cfg.getLocation("Bounds.From");
        Location to = cfg.getLocation("Bounds.To");
        if (from != null && to != null) {
            this.setCuboid(new ArenaCuboid(from, to));
        }
        this.spawnLocation = cfg.getLocation("Spawn_Location");

        if (cfg.contains("Mob_Spawners")) {
            Set<String> oldSpawners = new HashSet<>();

            for (String sId : cfg.getSection("Mob_Spawners")) {
                Location loc = cfg.getLocation("Mob_Spawners." + sId);
                if (loc == null) continue;

                oldSpawners.add(BlockPos.from(loc).serialize());
            }

            cfg.set("Spawners." + Placeholders.DEFAULT, oldSpawners);
            cfg.remove("Mob_Spawners");
        }

        for (String sId : cfg.getSection("Spawners")) {
            Set<BlockPos> spawners = cfg.getStringSet("Spawners." + sId).stream()
                .map(BlockPos::deserialize)
                .filter(pos -> !pos.isEmpty()).collect(Collectors.toSet());
            this.mobSpawners.put(sId.toLowerCase(), spawners);
        }

        String path = "Hologram.";
        this.hologramLocations.addAll(cfg.getStringSet(path + "Locations").stream().map(LocationUtil::deserialize)
            .filter(Objects::nonNull).toList());

        this.updateHolograms();

        cfg.saveChanges();
        return true;
    }

    @Override
    public void onSave() {
        cfg.set("Enabled", this.isActive());
        cfg.set("Is_Default", this.isDefault());
        cfg.set("Name", this.getName());
        cfg.remove("Bounds");
        this.getCuboid().ifPresent(cuboid -> {
            cfg.set("Bounds.From", cuboid.getMin());
            cfg.set("Bounds.To", cuboid.getMax());
        });
        cfg.set("Spawn_Location", this.getSpawnLocation());
        /*cfg.set("Mob_Spawners", null);
        this.mobSpawners.forEach((id, loc) -> {
            cfg.set("Mob_Spawners." + id, loc);
        });*/
        cfg.remove("Spawners");
        this.mobSpawners.forEach((id, spawners) -> {
            cfg.set("Spawners." + id, spawners.stream().map(BlockPos::serialize).toList());
        });
        cfg.set("Hologram.Locations", this.getHologramLocations().stream().map(LocationUtil::serialize).toList());
    }

    public void clear() {
        if (this.editor != null) {
            this.editor.clear();
            this.editor = null;
        }
        this.removeHolograms();
    }

    @Override
    @NotNull
    public PlaceholderMap getPlaceholders() {
        return this.placeholderMap;
    }

    @NotNull
    @Override
    public Report getReport() {
        Report report = new Report();

        if (this.getCuboid().isEmpty()) {
            report.addProblem("Invalid region selection!");
        }
        if (this.getSpawnLocation() == null || !this.getCuboid().get().contains(this.getSpawnLocation())) {
            report.addProblem("Invalid spawn location!");
        }
        if (this.getMobSpawners().isEmpty()) {
            report.addWarn("No mob spawners!");
        }

        return report;
    }

    @NotNull
    public Set<ArenaPlayer> getPlayers() {
        return this.getArena().getPlayers().select(GameState.INGAME, PlayerType.REAL).stream().filter(arenaPlayer -> {
            return arenaPlayer.getRegion() == this;
        }).collect(Collectors.toSet());
    }

    @NotNull
    public RegionMainEditor getEditor() {
        if (this.editor == null) {
            this.editor = new RegionMainEditor(this);
        }
        return this.editor;
    }

    @Override
    @NotNull
    public ArenaConfig getArenaConfig() {
        return this.arenaConfig;
    }

    @NotNull
    @Override
    public LockState getLockState() {
        return lockState;
    }

    @Override
    public void setLockState(@NotNull LockState lockState) {
        this.removeHolograms();

        this.lockState = lockState;

        GameEventType eventType = this.isLocked() ? GameEventType.REGION_LOCKED : GameEventType.REGION_UNLOCKED;
        ArenaRegionEvent regionEvent = new ArenaRegionEvent(this.getArena(), eventType, this);
        this.plugin.getPluginManager().callEvent(regionEvent);

        this.updateHolograms();
    }

    @NotNull
    public String getName() {
        return this.name;
    }

    public void setName(@NotNull String name) {
        this.name = Colorizer.apply(name);
    }

    public boolean isActive() {
        return this.isActive;
    }

    public void setActive(boolean isActive) {
        this.isActive = isActive;
    }

    public boolean isDefault() {
        return this.isDefault;
    }

    public void setDefault(boolean isDefault) {
        this.isDefault = isDefault;
    }

    @NotNull
    public Optional<ArenaCuboid> getCuboid() {
        return Optional.ofNullable(this.cuboid);
    }

    public void setCuboid(@Nullable ArenaCuboid cuboid) {
        this.cuboid = cuboid;
    }

    public Location getSpawnLocation() {
        return this.spawnLocation == null ? null : this.spawnLocation.clone();
    }

    public void setSpawnLocation(Location spawnLocation) {
        this.spawnLocation = spawnLocation == null ? null : spawnLocation.clone();
    }

    @NotNull
    public Map<String, Set<BlockPos>> getMobSpawners() {
        return this.mobSpawners;
    }

    @NotNull
    public Set<BlockPos> getMobSpawners(@NotNull String group) {
        return this.getMobSpawners().computeIfAbsent(group.toLowerCase(), k -> new HashSet<>());
    }

    @NotNull
    public Set<Location> getMobSpawnersLocations(@NotNull String group) {
        World world = this.getArenaConfig().getWorld();

        return this.getMobSpawners(group).stream().map(pos -> pos.toLocation(world)).collect(Collectors.toSet());
    }

    /*@Nullable
    public Location getMobSpawner(@NotNull String id) {
        Location location = this.getMobSpawners().get(id.toLowerCase());
        return location == null ? null : location.clone();
    }*/

    public boolean removeMobSpawner(@NotNull String group, @NotNull Location location) {
        BlockPos pos = BlockPos.from(location);

        return this.getMobSpawners(group).remove(pos);
    }

    public boolean addMobSpawner(@NotNull String group, @NotNull Location location) {
        BlockPos pos = BlockPos.from(location);

        return getMobSpawners(group).add(pos);

        /*if (this.getMobSpawners().containsValue(location)) {
            return false;
        }

        Block block = location.getBlock();
        String id = "s";
        int count = 0;

        String idFinal = id + count;
        while (this.getMobSpawners().containsKey(idFinal)) {
            idFinal = id + (++count);
        }

        this.getMobSpawners().put(idFinal, location);
        return true;*/
    }

    @NotNull
    @Override
    public HologramType getHologramType() {
        return this.isLocked() ? HologramType.REGION_LOCKED : HologramType.REGION_UNLOCKED;
    }

    @NotNull
    @Override
    public Set<Location> getHologramLocations() {
        return hologramLocations;
    }

    @NotNull
    @Override
    public Set<UUID> getHologramIds() {
        return hologramIds;
    }

    @NotNull
    @Override
    public List<String> getHologramFormat() {
        List<String> text = HologramManager.getFormat(this.getHologramType());
        text.replaceAll(this.replacePlaceholders());
        return text;
    }
}
