package su.nightexpress.ama.arena.impl;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nexmedia.engine.api.config.JYML;
import su.nexmedia.engine.api.manager.AbstractConfigHolder;
import su.nexmedia.engine.api.placeholder.Placeholder;
import su.nexmedia.engine.api.placeholder.PlaceholderMap;
import su.nexmedia.engine.utils.Colorizer;
import su.nexmedia.engine.utils.LocationUtil;
import su.nexmedia.engine.utils.StringUtil;
import su.nightexpress.ama.AMA;
import su.nightexpress.ama.Perms;
import su.nightexpress.ama.Placeholders;
import su.nightexpress.ama.api.arena.Inspectable;
import su.nightexpress.ama.api.arena.Report;
import su.nightexpress.ama.api.arena.type.ArenaLocationType;
import su.nightexpress.ama.api.currency.Currency;
import su.nightexpress.ama.api.hologram.HologramHolder;
import su.nightexpress.ama.api.hologram.HologramType;
import su.nightexpress.ama.api.type.GameState;
import su.nightexpress.ama.arena.ArenaStatsHologram;
import su.nightexpress.ama.arena.editor.arena.ArenaMainEditor;
import su.nightexpress.ama.arena.game.GameplaySettings;
import su.nightexpress.ama.arena.region.RegionManager;
import su.nightexpress.ama.arena.reward.RewardManager;
import su.nightexpress.ama.arena.script.ScriptManager;
import su.nightexpress.ama.arena.shop.ShopManager;
import su.nightexpress.ama.arena.spot.SpotManager;
import su.nightexpress.ama.arena.supply.SupplyManager;
import su.nightexpress.ama.arena.util.ArenaUtils;
import su.nightexpress.ama.arena.wave.WaveManager;
import su.nightexpress.ama.hologram.HologramManager;
import su.nightexpress.ama.hook.level.PlayerLevelProvider;
import su.nightexpress.ama.hook.level.PluginLevelProvider;
import su.nightexpress.ama.stats.object.StatType;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

public class ArenaConfig extends AbstractConfigHolder<AMA> implements HologramHolder, Inspectable, Placeholder {

    private static final String GAMEPLAY_CONFIG_NAME = "gameplay.yml";
    private static final String REWARDS_CONFIG_NAME  = "rewards.yml";

    private final Map<DayOfWeek, Set<LocalTime>>    autoOpenTimes;
    private final Map<DayOfWeek, Set<LocalTime>>    autoCloseTimes;
    private final Map<ArenaLocationType, Location>  locations;
    private final Map<StatType, ArenaStatsHologram> statsHolograms;
    private final Set<UUID>                         hologramIds;
    private final Set<Location>                     hologramLocations;
    private final Map<Currency, Double>             paymentRequirements;
    private final Map<PlayerLevelProvider, Integer> levelRequirements;

    private final Arena         arena;
    private final WaveManager   waveManager;
    private final RegionManager regionManager;
    private final GameplaySettings gameplaySettings;
    private final SupplyManager    supplyManager;
    private final SpotManager      spotManager;
    private final RewardManager      rewardManager;
    private final ShopManager    shopManager;
    private final ScriptManager  scriptManager;
    private final PlaceholderMap placeholderMap;

    private boolean active;
    private String  name;
    private boolean   permissionRequired;
    private ItemStack icon;

    private ArenaMainEditor mainEditor;

    public ArenaConfig(@NotNull AMA plugin, @NotNull JYML cfg, @NotNull String id) {
        super(plugin, cfg, id);
        this.autoOpenTimes = new HashMap<>();
        this.autoCloseTimes = new HashMap<>();
        this.locations = new HashMap<>();
        this.statsHolograms = new HashMap<>();
        this.hologramIds = new HashSet<>();
        this.hologramLocations = new HashSet<>();
        this.paymentRequirements = new HashMap<>();
        this.levelRequirements = new HashMap<>();

        String path = this.getFile().getParentFile().getAbsolutePath();

        this.waveManager = new WaveManager(this, new JYML(path, WaveManager.CONFIG_NAME));
        this.regionManager = new RegionManager(this);
        this.gameplaySettings = new GameplaySettings(this, new JYML(path, GAMEPLAY_CONFIG_NAME));
        this.supplyManager = new SupplyManager(this, new JYML(path, SupplyManager.CONFIG_NAME));
        this.spotManager = new SpotManager(this);
        this.rewardManager = new RewardManager(this, new JYML(path, REWARDS_CONFIG_NAME));
        this.shopManager = new ShopManager(this, new JYML(path, ShopManager.CONFIG_NAME));
        this.scriptManager = new ScriptManager(this);

        this.placeholderMap = Placeholders.forArena(this);
        this.arena = new Arena(this);
    }

    @Override
    public boolean load() {
        this.setActive(cfg.getBoolean("Active"));
        this.setName(cfg.getString("Name", this.getId() + " Arena"));
        this.setIcon(cfg.getItem("Icon"));

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

            this.getPaymentRequirements().put(currency, amount);
        }

        for (String sId : cfg.getSection("Join_Requirements.Level")) {
            PlayerLevelProvider provider = PluginLevelProvider.getProvider(sId);
            if (provider == null) continue;

            int level = cfg.getInt("Join_Requirements.Level." + sId, 0);
            if (level <= 0) continue;

            this.getLevelRequirements().put(provider, level);
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

        this.waveManager.load();
        this.regionManager.load();
        this.gameplaySettings.load();
        this.supplyManager.load();
        this.spotManager.setup();
        this.rewardManager.load();
        this.shopManager.load();
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
        if (this.mainEditor != null) {
            this.mainEditor.clear();
            this.mainEditor = null;
        }
        this.spotManager.shutdown();
        this.regionManager.shutdown();
        this.gameplaySettings.clear();
        this.supplyManager.clear();
        this.waveManager.clear();
        this.rewardManager.clear();
        this.shopManager.clear();
        this.scriptManager.shutdown();
    }

    @Override
    public void onSave() {
        cfg.set("Active", this.isActive());
        cfg.set("Name", this.getName());
        cfg.setItem("Icon", this.getIcon());
        this.autoOpenTimes.forEach((day, times) -> cfg.set("Auto_State_Schedulers.Open." + day.name(), times.stream().map(time -> time.format(ArenaUtils.TIME_FORMATTER)).toList()));
        this.autoCloseTimes.forEach((day, times) -> cfg.set("Auto_State_Schedulers.Close." + day.name(), times.stream().map(time -> time.format(ArenaUtils.TIME_FORMATTER)).toList()));

        String path = "Join_Requirements.";
        cfg.set(path + "Permission", this.isPermissionRequired());
        cfg.set(path + "Payment", null);
        this.getPaymentRequirements().forEach((currency, amount) -> {
            cfg.set(path + "Payment." + currency.getId(), amount);
        });
        cfg.remove(path + "Level");
        this.getLevelRequirements().forEach((provider, level) -> {
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
        this.gameplaySettings.save();
        this.supplyManager.save();
        this.rewardManager.save();
        this.shopManager.save();
        this.scriptManager.save();
    }

    @Override
    @NotNull
    public Report getReport() {
        Report report = new Report();

        if (this.getLocation(ArenaLocationType.LOBBY) == null) {
            report.addProblem("No lobby location!");
        }
        if (this.getLocation(ArenaLocationType.SPECTATE) == null) {
            report.addProblem("No spectate location!");
        }

        if (this.getRegionManager().hasProblems()) {
            report.addProblem("Major issues in Region Manager.");
        }
        else if (this.getRegionManager().hasWarns()) {
            report.addWarn("Minor issues in Region Manager.");
        }

        if (this.getGameplaySettings().hasProblems()) {
            report.addProblem("Major issues in Gameplay Settings.");
        }
        else if (this.getGameplaySettings().hasWarns()) {
            report.addWarn("Minor issues in Gameplay Settings.");
        }

        if (this.getWaveManager().hasProblems()) {
            report.addProblem("Major issues in Wave Manager.");
        }
        else if (this.getWaveManager().hasWarns()) {
            report.addWarn("Minor issues in Wave Manager.");
        }

        if (this.getSpotManager().hasProblems()) {
            report.addProblem("Major issues in Spot Manager.");
        }
        else if (this.getSpotManager().hasWarns()) {
            report.addWarn("Minor issues in Spot Manager.");
        }

        if (this.getShopManager().hasProblems()) {
            report.addProblem("Major issues in Shop Manager.");
        }
        else if (this.getShopManager().hasWarns()) {
            report.addWarn("Minor issues in Shop Manager.");
        }

        if (this.getRewardManager().hasProblems()) {
            report.addProblem("Major issues in Reward Manager.");
        }
        else if (this.getRewardManager().hasWarns()) {
            report.addWarn("Minor issues in Reward Manager.");
        }

        if (this.getScriptManager().hasProblems()) {
            report.addProblem("Major issues in Script Manager.");
        }
        else if (this.getScriptManager().hasWarns()) {
            report.addWarn("Minor issues in Script Manager.");
        }

        return report;
    }

    @NotNull
    public ArenaMainEditor getEditor() {
        if (this.mainEditor == null) {
            this.mainEditor = new ArenaMainEditor(this.plugin, this);
        }
        return this.mainEditor;
    }

    @NotNull
    public Arena getArena() {
        return this.arena;
    }

    public boolean isActive() {
        return this.active;
    }

    public void setActive(boolean isActive) {
        this.active = isActive;
    }

    @NotNull
    public String getName() {
        return this.name;
    }

    public void setName(@NotNull String name) {
        this.name = Colorizer.apply(name);
    }

    public boolean isPermissionRequired() {
        return permissionRequired;
    }

    public void setPermissionRequired(boolean permissionRequired) {
        this.permissionRequired = permissionRequired;
    }

    @NotNull
    public ItemStack getIcon() {
        return new ItemStack(this.icon);
    }

    public void setIcon(@NotNull ItemStack icon) {
        if (icon.getType().isAir()) {
            icon = new ItemStack(Material.MAP);
        }
        this.icon = new ItemStack(icon);
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
    public Map<Currency, Double> getPaymentRequirements() {
        return paymentRequirements;
    }

    @NotNull
    public Map<PlayerLevelProvider, Integer> getLevelRequirements() {
        return levelRequirements;
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
    public WaveManager getWaveManager() {
        return this.waveManager;
    }

    @NotNull
    public RegionManager getRegionManager() {
        return this.regionManager;
    }

    @NotNull
    public GameplaySettings getGameplaySettings() {
        return this.gameplaySettings;
    }

    @NotNull
    public SupplyManager getSupplyManager() {
        return this.supplyManager;
    }

    @NotNull
    public SpotManager getSpotManager() {
        return this.spotManager;
    }

    @NotNull
    public RewardManager getRewardManager() {
        return rewardManager;
    }

    @NotNull
    public ShopManager getShopManager() {
        return shopManager;
    }

    @NotNull
    public ScriptManager getScriptManager() {
        return scriptManager;
    }
}
