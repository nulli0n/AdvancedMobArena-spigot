package su.nightexpress.ama.arena.config;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nexmedia.engine.api.config.JYML;
import su.nexmedia.engine.api.manager.AbstractLoadableItem;
import su.nexmedia.engine.api.manager.IEditable;
import su.nexmedia.engine.api.manager.ILoadable;
import su.nexmedia.engine.api.server.JPermission;
import su.nexmedia.engine.lang.LangManager;
import su.nexmedia.engine.utils.LocationUtil;
import su.nexmedia.engine.utils.StringUtil;
import su.nightexpress.ama.AMA;
import su.nightexpress.ama.Perms;
import su.nightexpress.ama.Placeholders;
import su.nightexpress.ama.api.arena.IProblematic;
import su.nightexpress.ama.api.arena.type.ArenaLocationType;
import su.nightexpress.ama.api.arena.type.ArenaState;
import su.nightexpress.ama.api.currency.ICurrency;
import su.nightexpress.ama.api.hologram.HologramHolder;
import su.nightexpress.ama.api.hologram.HologramType;
import su.nightexpress.ama.arena.AbstractArena;
import su.nightexpress.ama.arena.Arena;
import su.nightexpress.ama.arena.ArenaStatsHologram;
import su.nightexpress.ama.arena.editor.arena.EditorArenaMain;
import su.nightexpress.ama.arena.game.ArenaGameplayManager;
import su.nightexpress.ama.arena.region.ArenaRegionManager;
import su.nightexpress.ama.arena.reward.ArenaRewardManager;
import su.nightexpress.ama.arena.shop.ArenaShopManager;
import su.nightexpress.ama.arena.spot.ArenaSpotManager;
import su.nightexpress.ama.arena.wave.ArenaWaveManager;
import su.nightexpress.ama.hologram.HologramManager;
import su.nightexpress.ama.stats.object.StatType;

import java.io.File;
import java.util.*;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;

public class ArenaConfig extends AbstractLoadableItem<AMA> implements HologramHolder, ILoadable, IEditable, IProblematic {

    private Arena   arena;
    private boolean isActive;
    private String  name;

    private boolean                isPermissionRequired;
    private JPermission            permission;
    private Map<ICurrency, Double> joinPaymentRequirements;

    private final Map<ArenaLocationType, Location>  locations;
    private final Map<StatType, ArenaStatsHologram> statsHolograms;
    private final Set<UUID>                         hologramIds;
    private final Set<Location>                     hologramLocations;

    private ArenaWaveManager     waveManager;
    private ArenaRegionManager   regionManager;
    private ArenaGameplayManager gameplayManager;
    private ArenaSpotManager     spotManager;
    private ArenaRewardManager   rewardManager;
    private ArenaShopManager     shopManager;

    private EditorArenaMain editorMain;

    public ArenaConfig(@NotNull AMA plugin, @NotNull String path) {
        this(plugin, new JYML(new File(path)));
        this.setActive(false);
    }

    public ArenaConfig(@NotNull AMA plugin, @NotNull JYML cfg) {
        super(plugin, cfg);

        this.setActive(cfg.getBoolean("Active"));
        this.setName(cfg.getString("Name", this.getId() + " Arena"));

        String path = "Join_Requirements.";
        this.setPermissionRequired(cfg.getBoolean(path + "Permission"));
        this.setPermission();

        this.setJoinPaymentRequirements(new HashMap<>());
        for (String sId : cfg.getSection(path + "Payment")) {
            ICurrency currency = this.plugin().getCurrencyManager().getCurrency(sId);
            if (currency == null) continue;

            double amount = cfg.getDouble(path + "Payment." + sId, 0D);
            if (amount <= 0D) continue;

            this.getJoinPaymentRequirements().put(currency, amount);
        }

        this.locations = new HashMap<>();
        for (ArenaLocationType locationType : ArenaLocationType.values()) {
            this.locations.put(locationType, cfg.getLocation("Locations." + locationType.name()));
        }

        this.statsHolograms = new HashMap<>();
        for (StatType statType : StatType.values()) {
            Set<Location> locations = cfg.getStringList("Hologram." + statType.name() + ".Locations")
                .stream().map(LocationUtil::deserialize).filter(Objects::nonNull).collect(Collectors.toSet());
            ArenaStatsHologram statsHologram = new ArenaStatsHologram(this, statType, locations);
            this.statsHolograms.put(statType, statsHologram);
        }

        this.hologramIds = new HashSet<>();
        this.hologramLocations = cfg.getStringList("Hologram.Default.Locations")
            .stream().map(LocationUtil::deserialize).filter(Objects::nonNull).collect(Collectors.toSet());

        this.setup();
    }

    @Override
    @NotNull
    public UnaryOperator<String> replacePlaceholders() {
        String payment = this.getJoinPaymentRequirements().keySet().stream().map(c -> {
            return c.format(this.getJoinPaymentRequirements().getOrDefault(c, 0D));
        }).collect(Collectors.joining(", "));

        return str -> str
            .replace(Placeholders.GENERIC_PROBLEMS, Placeholders.formatProblems(this.getProblems()))
            .replace(Placeholders.ARENA_ID, this.getId())
            .replace(Placeholders.ARENA_ACTIVE, LangManager.getBoolean(this.isActive()))
            .replace(Placeholders.ARENA_NAME, this.getName())
            .replace(Placeholders.ARENA_PERMISSION, this.getPermission().getName())
            .replace(Placeholders.ARENA_REQUIREMENT_PERMISSION, LangManager.getBoolean(this.isPermissionRequired()))
            .replace(Placeholders.ARENA_REQUIREMENT_PAYMENT, payment)
            ;
    }

    @Override
    public void setup() {
        this.waveManager = new ArenaWaveManager(this);
        this.waveManager.setup();

        this.regionManager = new ArenaRegionManager(this);
        this.regionManager.setup();

        this.gameplayManager = new ArenaGameplayManager(this);
        this.gameplayManager.setup();

        this.spotManager = new ArenaSpotManager(this);
        this.spotManager.setup();

        this.rewardManager = new ArenaRewardManager(this);
        this.rewardManager.setup();

        this.shopManager = new ArenaShopManager(this);
        this.shopManager.setup();

        this.getConfig().saveChanges();
        this.createHolograms();
    }

    @Override
    public void shutdown() {
        this.removeHolograms();
        if (this.statsHolograms != null) {
            this.statsHolograms.values().forEach(HologramHolder::removeHolograms);
            this.statsHolograms.clear();
        }
        if (this.editorMain != null) {
            this.editorMain.clear();
            this.editorMain = null;
        }
        if (this.spotManager != null) {
            this.spotManager.shutdown();
            this.spotManager = null;
        }
        if (this.regionManager != null) {
            this.regionManager.shutdown();
            this.regionManager = null;
        }
        if (this.gameplayManager != null) {
            this.gameplayManager.shutdown();
            this.gameplayManager = null;
        }
        if (this.waveManager != null) {
            this.waveManager.shutdown();
            this.waveManager = null;
        }
        if (this.rewardManager != null) {
            this.rewardManager.shutdown();
            this.rewardManager = null;
        }
        if (this.shopManager != null) {
            this.shopManager.shutdown();
            this.shopManager = null;
        }
        this.removePermission();
    }

    @Override
    public void onSave() {
        cfg.set("Active", this.isActive());
        cfg.set("Name", this.getName());

        String path = "Join_Requirements.";
        cfg.set(path + "Permission", this.isPermissionRequired());
        cfg.set(path + "Payment", null);
        this.getJoinPaymentRequirements().forEach((currency, amount) -> {
            cfg.set(path + "Payment." + currency.getId(), amount);
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
        this.rewardManager.save();
        this.shopManager.save();
    }

    @Override
    @NotNull
    public List<String> getProblems() {
        List<String> list = new ArrayList<>();
        if (this.getLocation(ArenaLocationType.LOBBY) == null) {
            list.add("No Lobby Location");
        }
        if (this.getLocation(ArenaLocationType.SPECTATE) == null) {
            list.add("No Spectate Location");
        }

        if (this.getRegionManager().hasProblems()) {
            list.add("Problems in Region Manager");
        }
        if (this.getGameplayManager().hasProblems()) {
            list.add("Problems in Gameplay Manager");
        }
        if (this.getWaveManager().hasProblems()) {
            list.add("Problems in Wave Manager");
        }
        if (this.getSpotManager().hasProblems()) {
            list.add("Problems in Spot Manager");
        }
        if (this.getRewardManager().hasProblems()) {
            list.add("Problems in Reward Manager");
        }

        return list;
    }

    @Override
    @NotNull
    public EditorArenaMain getEditor() {
        if (this.editorMain == null) {
            this.editorMain = new EditorArenaMain(this.plugin, this);
        }
        return this.editorMain;
    }

    @NotNull
    public Arena getArena() {
        if (this.arena == null) {
            this.arena = new Arena(this);
        }
        return arena;
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
        this.name = StringUtil.color(name);
    }

    public boolean isPermissionRequired() {
        return isPermissionRequired;
    }

    public void setPermissionRequired(boolean permissionRequired) {
        isPermissionRequired = permissionRequired;
    }

    private void setPermission() {
        this.permission = new JPermission(Perms.PREFIX_ARENA + this.getId(), "Access to the " + getId() + " arena.");
        Perms.ARENA_ALL.addChildren(this.getPermission());
        if (this.plugin.getPluginManager().getPermission(this.getPermission().getName()) == null) {
            this.plugin.getPluginManager().addPermission(this.getPermission());
        }
    }

    private void removePermission() {
        this.plugin.getPluginManager().removePermission(this.getPermission());
    }

    @NotNull
    public JPermission getPermission() {
        return permission;
    }

    @NotNull
    public Map<ICurrency, Double> getJoinPaymentRequirements() {
        return joinPaymentRequirements;
    }

    public void setJoinPaymentRequirements(@NotNull Map<ICurrency, Double> joinPaymentRequirements) {
        this.joinPaymentRequirements = joinPaymentRequirements;
    }

    public boolean checkJoinRequirements(@NotNull AbstractArena arena, @NotNull Player player) {
        if (this.isPermissionRequired() && !this.getArena().hasPermission(player)) return false;
        if (!this.getJoinPaymentRequirements().entrySet().stream().allMatch(entry -> {
            return entry.getKey().getBalance(player) >= entry.getValue();
        })) return false;

        return true;
    }

    public void payJoinRequirements(@NotNull AbstractArena arena, @NotNull Player player) {
        this.getJoinPaymentRequirements().forEach((currency, amount) -> currency.take(player, amount));
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
            if (arena.getState() == ArenaState.INGAME && !player.hasPermission(Perms.BYPASS_ARENA_JOIN_INGAME)) {
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
    public ArenaSpotManager getSpotManager() {
        return this.spotManager;
    }

    @NotNull
    public ArenaRewardManager getRewardManager() {
        return rewardManager;
    }

    @NotNull
    public ArenaShopManager getShopManager() {
        return shopManager;
    }
}
