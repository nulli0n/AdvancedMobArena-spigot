package su.nightexpress.ama.arena.impl;

import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nexmedia.engine.api.config.JYML;
import su.nexmedia.engine.api.manager.AbstractConfigHolder;
import su.nexmedia.engine.api.placeholder.Placeholder;
import su.nexmedia.engine.api.placeholder.PlaceholderMap;
import su.nexmedia.engine.lang.LangManager;
import su.nexmedia.engine.utils.Colorizer;
import su.nexmedia.engine.utils.LocationUtil;
import su.nexmedia.engine.utils.StringUtil;
import su.nightexpress.ama.AMA;
import su.nightexpress.ama.Perms;
import su.nightexpress.ama.Placeholders;
import su.nightexpress.ama.api.arena.Problematic;
import su.nightexpress.ama.api.arena.type.ArenaLocationType;
import su.nightexpress.ama.api.currency.Currency;
import su.nightexpress.ama.api.hologram.HologramHolder;
import su.nightexpress.ama.api.hologram.HologramType;
import su.nightexpress.ama.arena.ArenaStatsHologram;
import su.nightexpress.ama.arena.editor.arena.ArenaMainEditor;
import su.nightexpress.ama.arena.game.ArenaGameplayManager;
import su.nightexpress.ama.arena.region.ArenaRegionManager;
import su.nightexpress.ama.arena.reward.ArenaRewardManager;
import su.nightexpress.ama.arena.script.ArenaScriptManager;
import su.nightexpress.ama.arena.shop.ShopManager;
import su.nightexpress.ama.arena.spot.ArenaSpotManager;
import su.nightexpress.ama.arena.supply.ArenaSupplyManager;
import su.nightexpress.ama.api.type.GameState;
import su.nightexpress.ama.arena.util.ArenaUtils;
import su.nightexpress.ama.arena.wave.ArenaWaveManager;
import su.nightexpress.ama.hologram.HologramManager;
import su.nightexpress.ama.hook.level.PlayerLevelProvider;
import su.nightexpress.ama.hook.level.PluginLevelProvider;
import su.nightexpress.ama.stats.object.StatType;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

public class ArenaConfig extends AbstractConfigHolder<AMA> implements HologramHolder, Problematic, Placeholder {

    private final Map<DayOfWeek, Set<LocalTime>>    autoOpenTimes;
    private final Map<DayOfWeek, Set<LocalTime>>    autoCloseTimes;
    private final Map<ArenaLocationType, Location>  locations;
    private final Map<StatType, ArenaStatsHologram> statsHolograms;
    private final Set<UUID>                         hologramIds;
    private final Set<Location>                     hologramLocations;
    private final Map<Currency, Double>            joinPaymentRequirements;
    private final Map<PlayerLevelProvider, Integer> joinLevelRequirements;

    private final Arena                arena;
    private final ArenaWaveManager     waveManager;
    private final ArenaRegionManager   regionManager;
    private final ArenaGameplayManager gameplayManager;
    private final ArenaSupplyManager   supplyManager;
    private final ArenaSpotManager     spotManager;
    private final ArenaRewardManager   rewardManager;
    private final ShopManager          shopManager;
    private final ArenaScriptManager   scriptManager;
    private final PlaceholderMap       placeholderMap;

    private boolean isActive;
    private String  name;
    private boolean isPermissionRequired;

    private ArenaMainEditor editorMain;

    public ArenaConfig(@NotNull AMA plugin, @NotNull JYML cfg, @NotNull String id) {
        super(plugin, cfg, id);
        this.autoOpenTimes = new HashMap<>();
        this.autoCloseTimes = new HashMap<>();
        this.locations = new HashMap<>();
        this.statsHolograms = new HashMap<>();
        this.hologramIds = new HashSet<>();
        this.hologramLocations = new HashSet<>();
        this.joinPaymentRequirements = new HashMap<>();
        this.joinLevelRequirements = new HashMap<>();

        String path = this.getFile().getParentFile().getAbsolutePath();

        this.waveManager = new ArenaWaveManager(this);
        this.regionManager = new ArenaRegionManager(this);
        this.gameplayManager = new ArenaGameplayManager(this);
        this.supplyManager = new ArenaSupplyManager(this, new JYML(path, ArenaSupplyManager.CONFIG_NAME));
        this.spotManager = new ArenaSpotManager(this);
        this.rewardManager = new ArenaRewardManager(this);
        this.shopManager = new ShopManager(this);
        this.scriptManager = new ArenaScriptManager(this);

        this.placeholderMap = new PlaceholderMap()
            .add(Placeholders.GENERIC_PROBLEMS, () -> String.join("\n", this.getProblems()))
            .add(Placeholders.ARENA_ID, this::getId)
            .add(Placeholders.ARENA_ACTIVE, () -> LangManager.getBoolean(this.isActive()))
            .add(Placeholders.ARENA_NAME, this::getName)
            .add(Placeholders.ARENA_PERMISSION, this::getPermission)
            .add(Placeholders.ARENA_AUTO_STATE_OPEN_TIMES, () -> {
                return this.getAutoOpenTimes().entrySet().stream().map(entry -> {
                    return entry.getKey().name() + ": " + entry.getValue().stream().map(time -> time.format(ArenaUtils.TIME_FORMATTER)).collect(Collectors.joining(", "));
                }).collect(Collectors.joining("\n"));
            })
            .add(Placeholders.ARENA_AUTO_STATE_CLOSE_TIMES, () -> {
                return this.getAutoCloseTimes().entrySet().stream().map(entry -> {
                    return entry.getKey().name() + ": " + entry.getValue().stream().map(time -> time.format(ArenaUtils.TIME_FORMATTER)).collect(Collectors.joining(", "));
                }).collect(Collectors.joining("\n"));
            })
            .add(Placeholders.ARENA_REQUIREMENT_PERMISSION, () -> LangManager.getBoolean(this.isPermissionRequired()))
            .add(Placeholders.ARENA_REQUIREMENT_PAYMENT, () -> {
                return this.getJoinPaymentRequirements().keySet().stream().map(c -> {
                    return c.format(this.getJoinPaymentRequirements().getOrDefault(c, 0D));
                }).collect(Collectors.joining(", "));
            })
            .add(Placeholders.ARENA_REQUIREMENT_LEVEL, () -> {
                return this.getJoinLevelRequirements().entrySet().stream().map(c -> {
                    return c.getKey().getName() + ": " + c.getValue();
                }).collect(Collectors.joining(", "));
            });

        this.arena = new Arena(this);
    }

    @Override
    public boolean load() {
        this.setActive(cfg.getBoolean("Active"));
        this.setName(cfg.getString("Name", this.getId() + " Arena"));

        for (String type : cfg.getSection("Auto_State_Schedulers")) {
            for (String dayName : cfg.getSection("Auto_State_Schedulers." + type)) {
                DayOfWeek day = StringUtil.getEnum(dayName, DayOfWeek.class).orElse(null);
                if (day == null) continue;

                (type.equalsIgnoreCase("open") ? this.autoOpenTimes : this.autoCloseTimes)
                    .put(day, cfg.getStringList("Auto_State_Schedulers." + type + "." + dayName)
                        .stream().map(timeRaw -> LocalTime.parse(timeRaw, ArenaUtils.TIME_FORMATTER)).collect(Collectors.toSet()));
            }
        }

        this.setPermissionRequired(cfg.getBoolean("Join_Requirements.Permission"));

        for (String sId : cfg.getSection("Join_Requirements.Payment")) {
            Currency currency = this.plugin().getCurrencyManager().getCurrency(sId);
            if (currency == null) continue;

            double amount = cfg.getDouble("Join_Requirements.Payment." + sId, 0D);
            if (amount <= 0D) continue;

            this.getJoinPaymentRequirements().put(currency, amount);
        }

        for (String sId : cfg.getSection("Join_Requirements.Level")) {
            PlayerLevelProvider provider = PluginLevelProvider.getProvider(sId);
            if (provider == null) continue;

            int level = cfg.getInt("Join_Requirements.Level." + sId, 0);
            if (level <= 0) continue;

            this.getJoinLevelRequirements().put(provider, level);
        }


        for (ArenaLocationType locationType : ArenaLocationType.values()) {
            this.locations.put(locationType, cfg.getLocation("Locations." + locationType.name()));
        }

        for (StatType statType : StatType.values()) {
            Set<Location> locations = cfg.getStringList("Hologram." + statType.name() + ".Locations")
                .stream().map(LocationUtil::deserialize).filter(Objects::nonNull).collect(Collectors.toSet());
            ArenaStatsHologram statsHologram = new ArenaStatsHologram(this, statType, locations);
            this.statsHolograms.put(statType, statsHologram);
        }

        this.hologramLocations.addAll(cfg.getStringList("Hologram.Default.Locations")
            .stream().map(LocationUtil::deserialize).filter(Objects::nonNull).collect(Collectors.toSet()));

        this.waveManager.setup();
        this.regionManager.setup();
        this.gameplayManager.setup();
        this.supplyManager.load();
        this.spotManager.setup();
        this.rewardManager.setup();
        this.shopManager.setup();
        this.scriptManager.setup();
        this.getConfig().saveChanges();

        this.getArena().reset();
        this.createHolograms();
        return true;
    }

    @Override
    @NotNull
    public PlaceholderMap getPlaceholders() {
        return this.placeholderMap;
    }

    public void clear() {
        this.removeHolograms();
        this.statsHolograms.values().forEach(HologramHolder::removeHolograms);
        this.statsHolograms.clear();
        if (this.editorMain != null) {
            this.editorMain.clear();
            this.editorMain = null;
        }
        this.spotManager.shutdown();
        this.regionManager.shutdown();
        this.gameplayManager.shutdown();
        this.supplyManager.clear();
        this.waveManager.shutdown();
        this.rewardManager.shutdown();
        this.shopManager.shutdown();
        this.scriptManager.shutdown();
    }

    @Override
    public void onSave() {
        cfg.set("Active", this.isActive());
        cfg.set("Name", this.getName());
        this.autoOpenTimes.forEach((day, times) -> cfg.set("Auto_State_Schedulers.Open." + day.name(), times.stream().map(time -> time.format(ArenaUtils.TIME_FORMATTER)).toList()));
        this.autoCloseTimes.forEach((day, times) -> cfg.set("Auto_State_Schedulers.Close." + day.name(), times.stream().map(time -> time.format(ArenaUtils.TIME_FORMATTER)).toList()));

        String path = "Join_Requirements.";
        cfg.set(path + "Permission", this.isPermissionRequired());
        cfg.set(path + "Payment", null);
        this.getJoinPaymentRequirements().forEach((currency, amount) -> {
            cfg.set(path + "Payment." + currency.getId(), amount);
        });
        cfg.remove(path + "Level");
        this.getJoinLevelRequirements().forEach((provider, level) -> {
            cfg.set(path + "Level." + provider.getName(), level);
        });

        this.locations.forEach((locType, loc) -> {
            cfg.set("Locations." + locType.name(), loc);
        });

        this.statsHolograms.forEach((statType, statsHologram) -> {
            cfg.set("Hologram." + statType.name() + ".Locations", statsHologram.getHologramLocations()
                .stream().map(LocationUtil::serialize).toList());
        });
        cfg.set("Hologram.Default.Locations", this.getHologramLocations().stream().map(LocationUtil::serialize).toList());

        this.waveManager.save();
        this.regionManager.save();
        this.gameplayManager.save();
        this.supplyManager.save();
        this.rewardManager.save();
        this.shopManager.save();
        this.scriptManager.save();
    }

    @Override
    @NotNull
    public List<String> getProblems() {
        List<String> list = new ArrayList<>();
        if (this.getLocation(ArenaLocationType.LOBBY) == null) {
            list.add(problem("No Lobby Location"));
        }
        if (this.getLocation(ArenaLocationType.SPECTATE) == null) {
            list.add(problem("No Spectate Location"));
        }

        if (this.getRegionManager().hasProblems()) {
            list.add(problem("Problems in Region Manager"));
        }
        if (this.getGameplayManager().hasProblems()) {
            list.add(problem("Problems in Gameplay Manager"));
        }
        if (this.getWaveManager().hasProblems()) {
            list.add(problem("Problems in Wave Manager"));
        }
        if (this.getSpotManager().hasProblems()) {
            list.add(problem("Problems in Spot Manager"));
        }
        if (this.getRewardManager().hasProblems()) {
            list.add(problem("Problems in Reward Manager"));
        }

        return list;
    }

    @NotNull
    public ArenaMainEditor getEditor() {
        if (this.editorMain == null) {
            this.editorMain = new ArenaMainEditor(this.plugin, this);
        }
        return this.editorMain;
    }

    @NotNull
    public Arena getArena() {
        return this.arena;
    }

    public boolean isActive() {
        return this.isActive;
    }

    public void setActive(boolean isActive) {
        this.isActive = isActive;
    }

    @NotNull
    public String getName() {
        return this.name;
    }

    public void setName(@NotNull String name) {
        this.name = Colorizer.apply(name);
    }

    public boolean isPermissionRequired() {
        return isPermissionRequired;
    }

    public void setPermissionRequired(boolean permissionRequired) {
        isPermissionRequired = permissionRequired;
    }

    @NotNull
    public String getPermission() {
        return Perms.PREFIX_ARENA + this.getId();
    }

    @NotNull
    public Map<DayOfWeek, Set<LocalTime>> getAutoOpenTimes() {
        return autoOpenTimes;
    }

    @NotNull
    public Map<DayOfWeek, Set<LocalTime>> getAutoCloseTimes() {
        return autoCloseTimes;
    }

    @NotNull
    public Map<Currency, Double> getJoinPaymentRequirements() {
        return joinPaymentRequirements;
    }

    @NotNull
    public Map<PlayerLevelProvider, Integer> getJoinLevelRequirements() {
        return joinLevelRequirements;
    }

    public Location getLocation(@NotNull ArenaLocationType locationType) {
        return this.locations.get(locationType);
    }

    public void setLocation(@NotNull ArenaLocationType locationType, @Nullable Location location) {
        this.locations.put(locationType, location);
    }

    @NotNull
    public Map<StatType, ArenaStatsHologram> getStatsHolograms() {
        return statsHolograms;
    }

    @NotNull
    public ArenaStatsHologram getStatsHologram(@NotNull StatType statType) {
        return this.getStatsHolograms().computeIfAbsent(statType, k -> new ArenaStatsHologram(this, statType, new HashSet<>()));
    }

    @Override
    public void updateHolograms() {
        if (this.arena == null) return;

        HologramHolder.super.updateHolograms();
        this.setHologramClick((player -> {
            Arena arena = this.getArena();
            if (arena.getState() == GameState.INGAME && !player.hasPermission(Perms.BYPASS_ARENA_JOIN_INGAME)) {
                arena.joinSpectate(player);
            }
            else {
                arena.joinLobby(player);
            }
        }));
    }

    @NotNull
    @Override
    public HologramType getHologramType() {
        return HologramType.ARENA;
    }

    @NotNull
    @Override
    public List<String> getHologramFormat() {
        List<String> text = HologramManager.getFormat(this.getHologramType());
        text.replaceAll(this.getArena().replacePlaceholders());
        return text;
    }

    @NotNull
    @Override
    public Set<UUID> getHologramIds() {
        return hologramIds;
    }

    @NotNull
    @Override
    public Set<Location> getHologramLocations() {
        return hologramLocations;
    }

    @NotNull
    public ArenaWaveManager getWaveManager() {
        return this.waveManager;
    }

    @NotNull
    public ArenaRegionManager getRegionManager() {
        return this.regionManager;
    }

    @NotNull
    public ArenaGameplayManager getGameplayManager() {
        return this.gameplayManager;
    }

    @NotNull
    public ArenaSupplyManager getSupplyManager() {
        return this.supplyManager;
    }

    @NotNull
    public ArenaSpotManager getSpotManager() {
        return this.spotManager;
    }

    @NotNull
    public ArenaRewardManager getRewardManager() {
        return rewardManager;
    }

    @NotNull
    public ShopManager getShopManager() {
        return shopManager;
    }

    @NotNull
    public ArenaScriptManager getScriptManager() {
        return scriptManager;
    }
}
