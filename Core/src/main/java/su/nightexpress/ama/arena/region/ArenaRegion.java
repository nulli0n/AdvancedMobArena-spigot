package su.nightexpress.ama.arena.region;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nexmedia.engine.api.config.JYML;
import su.nexmedia.engine.api.manager.AbstractLoadableItem;
import su.nexmedia.engine.api.manager.ICleanable;
import su.nexmedia.engine.api.manager.IEditable;
import su.nexmedia.engine.lang.LangManager;
import su.nexmedia.engine.utils.LocationUtil;
import su.nexmedia.engine.utils.StringUtil;
import su.nightexpress.ama.AMA;
import su.nightexpress.ama.Placeholders;
import su.nightexpress.ama.api.arena.ArenaChild;
import su.nightexpress.ama.api.arena.IProblematic;
import su.nightexpress.ama.api.arena.game.ArenaGameEventTrigger;
import su.nightexpress.ama.api.arena.game.IArenaGameEventListenerState;
import su.nightexpress.ama.api.arena.type.ArenaGameEventType;
import su.nightexpress.ama.api.event.ArenaGameGenericEvent;
import su.nightexpress.ama.api.event.ArenaRegionEvent;
import su.nightexpress.ama.api.hologram.HologramHolder;
import su.nightexpress.ama.api.hologram.HologramType;
import su.nightexpress.ama.arena.editor.region.EditorRegionMain;
import su.nightexpress.ama.arena.impl.ArenaConfig;
import su.nightexpress.ama.arena.impl.ArenaPlayer;
import su.nightexpress.ama.arena.lock.LockState;
import su.nightexpress.ama.arena.script.action.ParameterResult;
import su.nightexpress.ama.arena.script.action.Parameters;
import su.nightexpress.ama.arena.script.action.ScriptActions;
import su.nightexpress.ama.arena.script.action.ScriptPreparedAction;
import su.nightexpress.ama.arena.script.condition.ScriptPreparedCondition;
import su.nightexpress.ama.arena.script.impl.ArenaScript;
import su.nightexpress.ama.arena.type.GameState;
import su.nightexpress.ama.arena.util.ArenaCuboid;
import su.nightexpress.ama.arena.wave.ArenaWave;
import su.nightexpress.ama.hologram.HologramManager;

import java.util.*;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;

public class ArenaRegion extends AbstractLoadableItem<AMA> implements IArenaGameEventListenerState, ArenaChild, HologramHolder, IProblematic, IEditable, ICleanable {

    private final ArenaConfig arenaConfig;
    private       boolean     isActive;
    private       boolean     isDefault;

    private       LockState                                     state;
    private final Map<LockState, Set<ArenaGameEventTrigger<?>>> stateTriggers;

    private       String                    name;
    private       ArenaCuboid               cuboid;
    private       Location                  spawnLocation;
    private final Set<String>               linkedRegions;
    private final Map<String, Location>     mobSpawners;
    private final Set<ArenaRegionWave>      waves;
    private final Set<ArenaRegionContainer> containers;
    private final Set<Location>             hologramLocations;
    private final Set<UUID>                 hologramIds;

    private EditorRegionMain editor;

    public ArenaRegion(@NotNull ArenaConfig arenaConfig, @NotNull String path) {
        super(arenaConfig.plugin(), path);
        this.arenaConfig = arenaConfig;

        this.setActive(false);
        this.setDefault(false);
        this.setState(LockState.UNLOCKED);
        this.setName(StringUtil.capitalizeFully(this.getId()) + " Region");
        this.setCuboid(ArenaCuboid.empty());
        this.setSpawnLocation(this.getCuboid().isEmpty() ? null : this.getCuboid().getCenter());
        this.linkedRegions = new HashSet<>();
        this.mobSpawners = new HashMap<>();
        this.stateTriggers = new HashMap<>();
        this.waves = new HashSet<>();
        this.containers = new HashSet<>();
        this.hologramLocations = new HashSet<>();
        this.hologramIds = new HashSet<>();
    }

    public ArenaRegion(@NotNull ArenaConfig arenaConfig, @NotNull JYML cfg) {
        super(arenaConfig.plugin(), cfg);
        this.arenaConfig = arenaConfig;

        this.setActive(cfg.getBoolean("Enabled"));
        this.setDefault(cfg.getBoolean("Is_Default"));
        this.setState(this.isDefault() ? LockState.UNLOCKED : LockState.LOCKED);
        this.setName(cfg.getString("Name", this.getId()));

        Location from = cfg.getLocation("Bounds.From");
        Location to = cfg.getLocation("Bounds.To");
        this.setCuboid((from == null || to == null) ? ArenaCuboid.empty() : new ArenaCuboid(from, to));

        this.linkedRegions = cfg.getStringSet("Linked_Regions");
        this.spawnLocation = cfg.getLocation("Spawn_Location");

        this.mobSpawners = new HashMap<>();
        for (String sId : cfg.getSection("Mob_Spawners")) {
            Location loc = cfg.getLocation("Mob_Spawners." + sId);
            if (loc == null) {
                plugin.error("Invalid location for '" + sId + "' mob spawner in '" + getFile().getName() + "' region!");
                continue;
            }
            this.mobSpawners.put(sId.toLowerCase(), loc);
        }

        String path = "Entrance.State.";
        this.stateTriggers = new HashMap<>();
        for (LockState lockState : LockState.values()) {
            this.stateTriggers.put(lockState, ArenaGameEventTrigger.parse(cfg, path + lockState.name() + ".Triggers"));

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
            // ----------- CONVERT SCRIPTS END -----------
        }

        path = "Hologram.";
        this.hologramLocations = new HashSet<>();
        this.hologramIds = new HashSet<>();
        this.hologramLocations.addAll(cfg.getStringSet(path + "Locations").stream().map(LocationUtil::deserialize)
            .filter(Objects::nonNull).toList());

        this.waves = new HashSet<>();
        for (String sId : cfg.getSection("Waves.List")) {
            String path2 = "Waves.List." + sId + ".";

            Set<ArenaGameEventTrigger<?>> triggers = ArenaGameEventTrigger.parse(cfg, path2 + "Triggers");

            String waveIdOld = cfg.getString(path2 + "Arena_Wave_Id", "");
            Set<String> waveIds = cfg.getStringSet(path2 + "Arena_Wave_Ids");
            if (!waveIdOld.isEmpty() && waveIds.isEmpty()) waveIds.add(waveIdOld);

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
                    script.getActions().add(action);
                }

                this.getArenaConfig().getScriptManager().addConverted(script);
            }
            // ----------- CONVERT SCRIPTS END -----------

            waveIds.removeIf(waveId -> {
                ArenaWave arenaWave = arenaConfig.getWaveManager().getWave(waveId);
                if (arenaWave == null) {
                    plugin.error("Invalid arena wave id '" + waveId + "' in '" + this.getFile().getName() + "' region!");
                    return true;
                }
                return false;
            });

            Set<String> waveSpawners = new HashSet<>();
            for (String spawnerName : cfg.getStringList(path2 + "Spawners")) {
                // Support for wildcard '*'
                if (spawnerName.equals(Placeholders.WILDCARD)) {
                    waveSpawners.add(Placeholders.WILDCARD);
                    //waveSpawners.addAll(this.mobSpawners.keySet());
                    continue;
                }

                spawnerName = spawnerName.toLowerCase();
                if (!this.mobSpawners.containsKey(spawnerName)) {
                    plugin.error("Invalid mob spawner id '" + spawnerName + "' for '" + sId + "' wave in '" + this.getFile().getName() + "' region!");
                    continue;
                }
                waveSpawners.add(spawnerName);
            }

            ArenaRegionWave waveRegion = new ArenaRegionWave(this, sId, waveIds, waveSpawners, triggers);
            this.waves.add(waveRegion);
        }

        this.containers = new HashSet<>();
        for (String sId : cfg.getSection("Containers")) {
            String path2 = "Containers." + sId + ".";

            Location cLoc = cfg.getLocation(path2 + "Location");
            if (cLoc == null || !(cLoc.getBlock().getState() instanceof Chest chest)) {
                plugin.error("Invalid location of '" + sId + "' container in '" + this.getFile().getName() + "' region!");
                continue;
            }

            Set<ArenaGameEventTrigger<?>> triggers = ArenaGameEventTrigger.parse(cfg, path2 + "Refill.Triggers");
            int cMinItems = cfg.getInt(path2 + "Refill.Items.Min");
            int cMaxItems = cfg.getInt(path2 + "Refill.Items.Max");
            List<ItemStack> cItems = Arrays.asList(cfg.getItemsEncoded(path2 + "Items"));

            ArenaRegionContainer container = new ArenaRegionContainer(this, chest.getLocation(), triggers, cMinItems, cMaxItems, cItems);
            this.containers.add(container);
        }

        this.updateHolograms();
        this.getProblems().forEach(problem -> {
            this.plugin().warn("Problem in '" + getFile().getName() + "' Region: " + problem);
        });
    }

    @Override
    public void onSave() {
        cfg.set("Enabled", this.isActive());
        cfg.set("Is_Default", this.isDefault());
        cfg.set("Name", this.getName());
        cfg.set("Bounds.From", this.getCuboid().getLocationMin());
        cfg.set("Bounds.To", this.getCuboid().getLocationMax());
        cfg.set("Spawn_Location", this.getSpawnLocation());
        cfg.set("Linked_Regions", this.getLinkedRegions());
        cfg.set("Mob_Spawners", null);
        this.mobSpawners.forEach((id, loc) -> {
            cfg.set("Mob_Spawners." + id, loc);
        });

        cfg.set("Entrance.State", null);
        this.getStateTriggers().forEach((lockState, triggers) -> {
            String path = "Entrance.State." + lockState.name() + ".Triggers.";
            triggers.forEach(trigger -> {
                cfg.set(path + trigger.getType().name(), trigger.getValuesRaw());
            });
        });
        cfg.set("Hologram.Locations", this.getHologramLocations().stream().map(LocationUtil::serialize).toList());

        cfg.set("Waves.List", null);
        for (ArenaRegionWave regionWave : this.getWaves()) {
            String path2 = "Waves.List." + regionWave.getId() + ".";

            regionWave.getTriggers().forEach(trigger -> {
                cfg.set(path2 + "Triggers." + trigger.getType().name(), trigger.getValuesRaw());
            });
            cfg.set(path2 + "Arena_Wave_Id", null);
            cfg.set(path2 + "Arena_Wave_Ids", regionWave.getArenaWaveIds());
            cfg.set(path2 + "Spawners", regionWave.getSpawnerIds());
        }

        cfg.set("Containers", null);
        int counter = 0;
        for (ArenaRegionContainer container : this.containers) {
            String path2 = "Containers." + (counter++) + ".";

            cfg.set(path2 + "Location", container.getLocation());
            container.getTriggers().forEach(trigger -> {
                cfg.set(path2 + "Refill.Triggers." + trigger.getType().name(), trigger.getValuesRaw());
            });
            cfg.set(path2 + "Refill.Items.Min", container.getMinItems());
            cfg.set(path2 + "Refill.Items.Max", container.getMaxItems());
            cfg.setItemsEncoded(path2 + "Items", container.getItems());
        }
    }

    @Override
    public void clear() {
        if (this.editor != null) {
            this.editor.clear();
            this.editor = null;
        }
        if (this.waves != null) {
            this.waves.forEach(ArenaRegionWave::clear);
            this.waves.clear();
        }
        this.removeHolograms();
    }

    @Override
    @NotNull
    public UnaryOperator<String> replacePlaceholders() {
        return str -> str
            .replace(Placeholders.GENERIC_PROBLEMS, Placeholders.formatProblems(this.getProblems()))
            .replace(Placeholders.REGION_TRIGGERS_LOCKED, Placeholders.format(this.getStateTriggers(LockState.LOCKED)))
            .replace(Placeholders.REGION_TRIGGERS_UNLOCKED, Placeholders.format(this.getStateTriggers(LockState.UNLOCKED)))
            .replace(Placeholders.REGION_LINKED_REGIONS, String.join(DELIMITER_DEFAULT, this.getLinkedRegions()))
            .replace(Placeholders.REGION_FILE, this.getFile().getName())
            .replace(Placeholders.REGION_ID, this.getId())
            .replace(Placeholders.REGION_NAME, this.getName())
            .replace(Placeholders.REGION_ACTIVE, LangManager.getBoolean(this.isActive()))
            .replace(Placeholders.REGION_DEFAULT, LangManager.getBoolean(this.isDefault()))
            .replace(Placeholders.REGION_STATE, this.plugin().getLangManager().getEnum(this.getState()))
            ;
    }

    @Override
    @NotNull
    public List<String> getProblems() {
        List<String> list = new ArrayList<>();
        if (this.getCuboid().isEmpty()) {
            list.add(Placeholders.PROBLEM_REGION_CUBOID_INVALID);
        }
        if (this.getSpawnLocation() == null || !this.getCuboid().contains(this.getSpawnLocation())) {
            list.add(Placeholders.PROBLEM_REGION_SPAWN_LOCATION);
        }
        if (this.getMobSpawners().isEmpty()) {
            list.add(Placeholders.PROBLEM_REGION_SPAWNERS_EMPTY);
        }
        this.getWaves().stream().filter(wave -> wave.getSpawnerIds().isEmpty()).forEach(wave -> {
            list.add("No Mob Spawners defined for the '" + wave.getId() + "' Region Wave!");
        });
        this.getWaves().stream().filter(wave -> wave.getTriggers().isEmpty()).forEach(wave -> {
            list.add("No Arena Game Triggers defined for the '" + wave.getId() + "' Region Wave!");
        });
        this.getWaves().stream().filter(wave -> wave.getArenaWaveIds().isEmpty()).forEach(wave -> {
            list.add("No Arena Wave Ids defined for the '" + wave.getId() + "' Region Wave!");
        });
        return list;
    }

    @Override
    public boolean onGameEvent(@NotNull ArenaGameGenericEvent gameEvent) {
        if (!this.isReady(gameEvent)) return false;

        this.removeHolograms();

        LockState state = this.getState().getOpposite();
        this.setState(state);

        ArenaGameEventType eventType = this.isLocked() ? ArenaGameEventType.REGION_LOCKED : ArenaGameEventType.REGION_UNLOCKED;
        ArenaRegionEvent regionEvent = new ArenaRegionEvent(gameEvent.getArena(), eventType, this);
        plugin().getPluginManager().callEvent(regionEvent);

        this.updateHolograms();
        return true;
    }

    @NotNull
    public Set<ArenaPlayer> getPlayers() {
        return this.getArena().getPlayers(GameState.INGAME).stream().filter(arenaPlayer -> {
            return this.equals(arenaPlayer.getRegion(false));
        }).collect(Collectors.toSet());
    }

    @Override
    @NotNull
    public EditorRegionMain getEditor() {
        if (this.editor == null) {
            this.editor = new EditorRegionMain(this);
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
    public LockState getState() {
        return state;
    }

    @Override
    public void setState(@NotNull LockState state) {
        this.state = state;
    }

    @NotNull
    public String getName() {
        return this.name;
    }

    public void setName(@NotNull String name) {
        this.name = StringUtil.color(name);
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
    @Override
    public Map<LockState, Set<ArenaGameEventTrigger<?>>> getStateTriggers() {
        return stateTriggers;
    }

    @NotNull
    public Set<String> getLinkedRegions() {
        return linkedRegions;
    }

    public void addLinkedRegion(@NotNull ArenaRegion region) {
        this.getLinkedRegions().add(region.getId());
        this.getArenaConfig().getRegionManager().getLinkedRegions(this).add(region);
    }

    public void removeLinkedRegion(@NotNull ArenaRegion region) {
        this.removeLinkedRegion(region.getId());
    }

    public void removeLinkedRegion(@NotNull String id) {
        this.getLinkedRegions().remove(id);
        this.getArenaConfig().getRegionManager().getLinkedRegions(this).removeIf(reg -> reg.getId().equalsIgnoreCase(id));
    }

    public void removeLinkedRegions() {
        this.getLinkedRegions().clear();
        this.getArenaConfig().getRegionManager().getLinkedRegions(this).clear();
    }

    @NotNull
    public ArenaCuboid getCuboid() {
        return this.cuboid;
    }

    public void setCuboid(@NotNull ArenaCuboid cuboid) {
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
        return this.getMobSpawners().get(id.toLowerCase()).clone();
    }

    public boolean addMobSpawner(@NotNull Location location) {
        if (this.getMobSpawners().containsValue(location)) {
            return false;
        }

        Block block = location.getBlock();
        String id = "spawner_on_" + block.getType().name().toLowerCase() + "_";
        int count = 0;

        String idFinal = id + count;
        while (this.getMobSpawners().containsKey(idFinal)) {
            idFinal = id + (++count);
        }

        this.getMobSpawners().put(idFinal, location);
        return true;
    }

    @NotNull
    public Set<ArenaRegionWave> getWaves() {
        return this.waves;
    }

    @NotNull
    public Set<ArenaRegionContainer> getContainers() {
        return this.containers;
    }

    public void emptyContainers() {
        this.getContainers().forEach(container -> {
            container.getChest().getInventory().clear();
        });
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

	/*@Override
	public void createHolograms() {
		HologramManager holograms = this.plugin.getHologramManager();
		if (holograms == null) return;

		HologramType hologramType = this.isLocked() ? HologramType.REGION_LOCKED : HologramType.REGION_UNLOCKED;
		List<String> text = new ArrayList<>(Config.HOLOGRAMS_FORMAT.get().getOrDefault(hologramType, Collections.emptyList()));
		if (text.isEmpty()) return;

		text.replaceAll(this.replacePlaceholders());

		this.getHologramLocations().forEach(location -> {
			this.getHologramIds().add(holograms.create(location, text));
		});
	}*/

	/*@Override
	public void removeHolograms() {
		HologramManager holograms = this.plugin.getHologramManager();
		if (holograms == null) return;

		this.getHologramIds().forEach(holograms::delete);
		this.getHologramIds().clear();
	}*/
}
