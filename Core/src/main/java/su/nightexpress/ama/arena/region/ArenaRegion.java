package su.nightexpress.ama.arena.region;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nexmedia.engine.api.config.JYML;
import su.nexmedia.engine.api.manager.AbstractConfigHolder;
import su.nexmedia.engine.api.manager.ICleanable;
import su.nexmedia.engine.api.placeholder.Placeholder;
import su.nexmedia.engine.api.placeholder.PlaceholderMap;
import su.nexmedia.engine.lang.LangManager;
import su.nexmedia.engine.utils.Colorizer;
import su.nexmedia.engine.utils.LocationUtil;
import su.nexmedia.engine.utils.StringUtil;
import su.nightexpress.ama.AMA;
import su.nightexpress.ama.Placeholders;
import su.nightexpress.ama.api.arena.ArenaChild;
import su.nightexpress.ama.api.arena.Problematic;
import su.nightexpress.ama.api.arena.type.ArenaGameEventType;
import su.nightexpress.ama.api.event.ArenaRegionEvent;
import su.nightexpress.ama.api.hologram.HologramHolder;
import su.nightexpress.ama.api.hologram.HologramType;
import su.nightexpress.ama.arena.editor.region.RegionMainEditor;
import su.nightexpress.ama.arena.impl.ArenaConfig;
import su.nightexpress.ama.arena.impl.ArenaPlayer;
import su.nightexpress.ama.arena.lock.LockState;
import su.nightexpress.ama.arena.lock.Lockable;
import su.nightexpress.ama.arena.script.action.ParameterResult;
import su.nightexpress.ama.arena.script.action.Parameters;
import su.nightexpress.ama.arena.script.action.ScriptActions;
import su.nightexpress.ama.arena.script.action.ScriptPreparedAction;
import su.nightexpress.ama.arena.script.condition.ScriptPreparedCondition;
import su.nightexpress.ama.arena.script.impl.ArenaScript;
import su.nightexpress.ama.arena.supply.ArenaSupplyChest;
import su.nightexpress.ama.arena.type.GameState;
import su.nightexpress.ama.arena.type.PlayerType;
import su.nightexpress.ama.arena.util.ArenaCuboid;
import su.nightexpress.ama.hologram.HologramManager;

import java.util.*;
import java.util.stream.Collectors;

public class ArenaRegion extends AbstractConfigHolder<AMA> implements ArenaChild, Lockable, HologramHolder, Problematic, Placeholder, ICleanable {

    private final ArenaConfig           arenaConfig;
    private final Map<String, Location> mobSpawners;
    private final Set<Location>         hologramLocations;
    private final Set<UUID>             hologramIds;
    private final PlaceholderMap placeholderMap;

    private boolean     isActive;
    private boolean     isDefault;
    private LockState   lockState;
    private String      name;
    private ArenaCuboid cuboid;
    private Location    spawnLocation;

    private RegionMainEditor editor;

    public ArenaRegion(@NotNull ArenaConfig arenaConfig, @NotNull JYML cfg) {
        super(arenaConfig.plugin(), cfg);
        this.arenaConfig = arenaConfig;
        this.mobSpawners = new HashMap<>();
        this.hologramLocations = new HashSet<>();
        this.hologramIds = new HashSet<>();
        this.lockState = LockState.UNLOCKED;

        this.placeholderMap = new PlaceholderMap()
            .add(Placeholders.GENERIC_PROBLEMS, () -> String.join("\n", this.getProblems()))
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

        for (String sId : cfg.getSection("Mob_Spawners")) {
            Location loc = cfg.getLocation("Mob_Spawners." + sId);
            if (loc == null) {
                plugin.error("Invalid location for '" + sId + "' mob spawner in '" + getFile().getName() + "' region!");
                continue;
            }
            this.mobSpawners.put(sId.toLowerCase(), loc);
        }

        String path = "Entrance.State.";
        for (LockState lockState : LockState.values()) {
            // ----------- CONVERT SCRIPTS START -----------
            for (String eventRaw : cfg.getSection(path + lockState.name() + ".Triggers")) {
                ArenaGameEventType eventType = StringUtil.getEnum(eventRaw, ArenaGameEventType.class).orElse(null);
                if (eventType == null) continue;

                String name = "region_" + this.getId() + "_" + lockState.name().toLowerCase();
                ArenaScript script = new ArenaScript(this.arenaConfig, name, eventType);

                String values = cfg.getString(path + lockState.name() + ".Triggers." + eventRaw, "");
                Map<String, List<ScriptPreparedCondition>> conditions = ArenaScript.ofGameTrigger(eventType, values);
                script.getConditions().putAll(conditions);

                ScriptPreparedAction action = new ScriptPreparedAction(lockState == LockState.LOCKED ? ScriptActions.LOCK_REGION : ScriptActions.UNLOCK_REGION, new ParameterResult());
                action.getParameters().add(Parameters.REGION, this.getId());
                script.getActions().add(action);

                this.getArenaConfig().getScriptManager().addConverted(script);
            }
            cfg.remove(path + lockState.name() + ".Triggers");
            // ----------- CONVERT SCRIPTS END -----------
        }

        path = "Hologram.";
        this.hologramLocations.addAll(cfg.getStringSet(path + "Locations").stream().map(LocationUtil::deserialize)
            .filter(Objects::nonNull).toList());

        for (String sId : cfg.getSection("Waves.List")) {
            String path2 = "Waves.List." + sId + ".";

            String waveIdOld = cfg.getString(path2 + "Arena_Wave_Id", "");
            Set<String> waveIds = cfg.getStringSet(path2 + "Arena_Wave_Ids");
            if (!waveIdOld.isEmpty() && waveIds.isEmpty()) waveIds.add(waveIdOld);

            Set<String> spawnerIds = cfg.getStringSet(path2 + "Spawners");

            // ----------- CONVERT SCRIPTS START -----------
            for (String eventRaw : cfg.getSection(path2 + "Triggers")) {
                ArenaGameEventType eventType = StringUtil.getEnum(eventRaw, ArenaGameEventType.class).orElse(null);
                if (eventType == null) continue;

                String name = "region_" + this.getId() + "_spawn_wave_" + sId;
                ArenaScript script = new ArenaScript(this.arenaConfig, name, eventType);

                String values = cfg.getString(path2 + "Triggers." + eventRaw, "");
                Map<String, List<ScriptPreparedCondition>> conditions = ArenaScript.ofGameTrigger(eventType, values);
                script.getConditions().putAll(conditions);

                for (String waveId : waveIds) {
                    ScriptPreparedAction action = new ScriptPreparedAction(ScriptActions.INJECT_WAVE, new ParameterResult());
                    action.getParameters().add(Parameters.WAVE, waveId);
                    action.getParameters().add(Parameters.REGION, this.getId());
                    if (!spawnerIds.isEmpty()) action.getParameters().add(Parameters.SPAWNERS, String.join(",", spawnerIds));
                    script.getActions().add(action);
                }

                this.getArenaConfig().getScriptManager().addConverted(script);
            }
            cfg.remove(path2 + "Triggers");
            // ----------- CONVERT SCRIPTS END -----------
        }

        // ---------- OLD DATA START ----------
        for (String sId : cfg.getSection("Containers")) {
            String path2 = "Containers." + sId + ".";

            Location cLoc = cfg.getLocation(path2 + "Location");
            if (cLoc == null || !(cLoc.getBlock().getState() instanceof Chest chest)) {
                //plugin.error("Invalid location of '" + sId + "' container in '" + this.getFile().getName() + "' region!");
                continue;
            }

            // ----------- CONVERT SCRIPTS START -----------
            for (String eventRaw : cfg.getSection(path2 + "Refill.Triggers")) {
                ArenaGameEventType eventType = StringUtil.getEnum(eventRaw, ArenaGameEventType.class).orElse(null);
                if (eventType == null) continue;

                String name = "refill_supply_chest_" + sId;
                ArenaScript script = new ArenaScript(this.arenaConfig, name, eventType);

                String values = cfg.getString(path2 + "Refill.Triggers." + eventRaw, "");
                Map<String, List<ScriptPreparedCondition>> conditions = ArenaScript.ofGameTrigger(eventType, values);
                script.getConditions().putAll(conditions);

                ScriptPreparedAction action = new ScriptPreparedAction(ScriptActions.REFILL_SUPPLY_CHEST, new ParameterResult());
                action.getParameters().add(Parameters.NAME, sId);
                script.getActions().add(action);

                this.getArenaConfig().getScriptManager().addConverted(script);
            }
            cfg.remove(path2 + "Refill.Triggers");
            // ----------- CONVERT SCRIPTS END -----------

            int cMinItems = cfg.getInt(path2 + "Refill.Items.Min");
            int cMaxItems = cfg.getInt(path2 + "Refill.Items.Max");
            List<ItemStack> cItems = Arrays.asList(cfg.getItemsEncoded(path2 + "Items"));

            String id = this.getId() + "_" + sId;
            ArenaSupplyChest supplyChest = new ArenaSupplyChest(this.arenaConfig, id, chest.getLocation(), cMinItems, cMaxItems, cItems);
            this.getArenaConfig().getSupplyManager().getChestsMap().put(supplyChest.getId(), supplyChest);
        }
        if (cfg.remove("Containers")) {
            this.getArenaConfig().getSupplyManager().save();
        }
        // ---------- OLD DATA END ----------

        this.updateHolograms();
        this.getProblems().forEach(problem -> {
            this.plugin().warn("Problem in '" + getFile().getName() + "' Region: " + problem);
        });

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
        cfg.set("Mob_Spawners", null);
        this.mobSpawners.forEach((id, loc) -> {
            cfg.set("Mob_Spawners." + id, loc);
        });
        cfg.set("Hologram.Locations", this.getHologramLocations().stream().map(LocationUtil::serialize).toList());
    }

    @Override
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

    @Override
    @NotNull
    public List<String> getProblems() {
        List<String> list = new ArrayList<>();
        if (this.getCuboid().isEmpty()) {
            list.add(problem("Invalid Cuboid Selection!"));
        }
        if (this.getSpawnLocation() == null || !this.getCuboid().get().contains(this.getSpawnLocation())) {
            list.add(problem("Invalid Spawn Location!"));
        }
        if (this.getMobSpawners().isEmpty()) {
            list.add(problem("No Mob Spawners!"));
        }
        return list;
    }

    @NotNull
    public Set<ArenaPlayer> getPlayers() {
        return this.getArena().getPlayers(GameState.INGAME, PlayerType.REAL).stream().filter(arenaPlayer -> {
            return this.equals(arenaPlayer.getRegion());
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

        ArenaGameEventType eventType = this.isLocked() ? ArenaGameEventType.REGION_LOCKED : ArenaGameEventType.REGION_UNLOCKED;
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
    public Map<String, Location> getMobSpawners() {
        return this.mobSpawners;
    }

    @Nullable
    public Location getMobSpawner(@NotNull String id) {
        Location location = this.getMobSpawners().get(id.toLowerCase());
        return location == null ? null : location.clone();
    }

    public boolean addMobSpawner(@NotNull Location location) {
        if (this.getMobSpawners().containsValue(location)) {
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
        return true;
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
