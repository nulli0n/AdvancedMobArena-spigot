package su.nightexpress.ama.arena.game;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.config.JYML;
import su.nexmedia.engine.api.manager.AbstractConfigHolder;
import su.nexmedia.engine.api.placeholder.Placeholder;
import su.nexmedia.engine.api.placeholder.PlaceholderMap;
import su.nexmedia.engine.command.CommandRegister;
import su.nexmedia.engine.lang.LangManager;
import su.nexmedia.engine.utils.EngineUtils;
import su.nexmedia.engine.utils.NumberUtil;
import su.nexmedia.engine.utils.StringUtil;
import su.nightexpress.ama.AMA;
import su.nightexpress.ama.Placeholders;
import su.nightexpress.ama.api.arena.ArenaChild;
import su.nightexpress.ama.api.arena.Problematic;
import su.nightexpress.ama.arena.editor.game.GameplayEditor;
import su.nightexpress.ama.arena.impl.ArenaConfig;
import su.nightexpress.ama.config.Lang;
import su.nightexpress.ama.hook.HookId;
import su.nightexpress.ama.kit.Kit;

import java.util.*;

public class ArenaGameplayManager extends AbstractConfigHolder<AMA> implements ArenaChild, Problematic, Placeholder {

    public static final String CONFIG_NAME = "gameplay.yml";

    private final ArenaConfig arenaConfig;
    private final PlaceholderMap placeholderMap;

    private GameplayEditor editor;

    private int     timeleft;
    private int     lobbyTime;
    private boolean scoreboardEnabled;
    private String  scoreboardId;
    private boolean isAnnouncesEnabled;
    private boolean isHungerEnabled;
    private boolean isRegenerationEnabled;
    private boolean isItemDropEnabled;
    private boolean isItemPickupEnabled;
    private boolean isItemDurabilityEnabled;
    private boolean isMobDropExpEnabled;
    private boolean isMobDropLootEnabled;
    private boolean isSpectateEnabled;

    private boolean   mobHighlightEnabled;
    private double    mobHighlightAmount;
    private ChatColor mobHighlightColor;

    private Set<Material>                       bannedItems;
    private Set<CreatureSpawnEvent.SpawnReason> allowedSpawnReasons;

    private int         playerMinAmount;
    private int         playerMaxAmount;
    private int         playerLivesAmount;
    private int     playerReviveTime;
    private boolean keepInventory;
    private boolean isPlayerCommandsEnabled;
    private Set<String> playerCommandsAllowed;

    private boolean              isKitsEnabled;
    private Set<String>          kitsAllowed;
    private Map<String, Integer> kitsLimits;

    private boolean petsAllowed;
    private boolean mcmmoAllowed;

    public ArenaGameplayManager(@NotNull ArenaConfig arenaConfig, @NotNull JYML cfg) {
        super(arenaConfig.plugin(), cfg);
        this.arenaConfig = arenaConfig;
        this.bannedItems = new HashSet<>();
        this.allowedSpawnReasons = new HashSet<>();
        this.playerCommandsAllowed = new HashSet<>();
        this.kitsAllowed = new HashSet<>();
        this.kitsLimits = new HashMap<>();
        this.setMobHighlightColor(ChatColor.RED);

        this.placeholderMap = new PlaceholderMap()
            .add(Placeholders.GENERIC_PROBLEMS, () -> String.join("\n", this.getProblems()))
            .add(Placeholders.GAMEPLAY_TIMELEFT, () -> this.hasTimeleft() ? NumberUtil.format(this.getTimeleft()) : LangManager.getPlain(Lang.OTHER_INFINITY))
            .add(Placeholders.GAMEPLAY_LOBBY_TIME, () -> String.valueOf(this.getLobbyTime()))
            .add(Placeholders.GAMEPLAY_ANNOUNCEMENTS, () -> LangManager.getBoolean(this.isAnnouncesEnabled()))
            .add(Placeholders.GAMEPLAY_SCOREBOARD_ENABLED, () -> LangManager.getBoolean(this.isScoreboardEnabled()))
            .add(Placeholders.GAMEPLAY_SCOREBOARD_ID, this::getScoreboardId)
            .add(Placeholders.GAMEPLAY_HUNGER_ENABLED, () -> LangManager.getBoolean(this.isHungerEnabled()))
            .add(Placeholders.GAMEPLAY_REGENERATION_ENABLED, () -> LangManager.getBoolean(this.isRegenerationEnabled()))
            .add(Placeholders.GAMEPLAY_ITEM_DROP_ENABLED, () -> LangManager.getBoolean(this.isItemDropEnabled()))
            .add(Placeholders.GAMEPLAY_ITEM_PICKUP_ENABLED, () -> LangManager.getBoolean(this.isItemPickupEnabled()))
            .add(Placeholders.GAMEPLAY_ITEM_DURABULITY_ENABLED, () -> LangManager.getBoolean(this.isItemDurabilityEnabled()))
            .add(Placeholders.GAMEPLAY_MOB_DROP_EXP, () -> LangManager.getBoolean(this.isMobDropExpEnabled()))
            .add(Placeholders.GAMEPLAY_MOB_DROP_ITEMS, () -> LangManager.getBoolean(this.isMobDropLootEnabled()))
            .add(Placeholders.GAMEPLAY_MOB_HIGHLIGHT_ENABLED, () -> LangManager.getBoolean(this.isMobHighlightEnabled()))
            .add(Placeholders.GAMEPLAY_MOB_HIGHLIGHT_AMOUNT, () -> NumberUtil.format(this.getMobHighlightAmount()))
            .add(Placeholders.GAMEPLAY_MOB_HIGHLIGHT_COLOR, () -> this.getMobHighlightColor().name())
            .add(Placeholders.GAMEPLAY_BANNED_ITEMS, () -> String.join("\n", this.getBannedItems().stream().map(Enum::name).toList()))
            .add(Placeholders.GAMEPLAY_ALLOWED_SPAWN_REASONS, () -> String.join("\n", this.getAllowedSpawnReasons().stream().map(Enum::name).toList()))
            .add(Placeholders.GAMEPLAY_PLAYER_AMOUNT_MIN, () -> String.valueOf(this.getPlayerMinAmount()))
            .add(Placeholders.GAMEPLAY_PLAYER_AMOUNT_MAX, () -> String.valueOf(this.getPlayerMaxAmount()))
            .add(Placeholders.GAMEPLAY_PLAYER_LIFES_AMOUNT, () -> String.valueOf(this.getPlayerLivesAmount()))
            .add(Placeholders.GAMEPLAY_PLAYER_REVIVE_TIME, () -> NumberUtil.format(this.getPlayerReviveTime()))
            .add(Placeholders.GAMEPLAY_KEEP_INVENTORY, () -> LangManager.getBoolean(this.isKeepInventory()))
            .add(Placeholders.GAMEPLAY_SPECTATE_ENABLED, () -> LangManager.getBoolean(this.isSpectateEnabled()))
            .add(Placeholders.GAMEPLAY_COMMAND_USAGE_ENABLED, () -> LangManager.getBoolean(this.isPlayerCommandsEnabled()))
            .add(Placeholders.GAMEPLAY_COMMAND_USAGE_WHITELIST, () -> String.join("\n", this.getPlayerCommandsAllowed()))
            .add(Placeholders.GAMEPLAY_KITS_ENABLED, () -> LangManager.getBoolean(this.isKitsEnabled()))
            .add(Placeholders.GAMEPLAY_KITS_ALLOWED, () -> String.join("\n", this.getKitsAllowed()))
            .add(Placeholders.GAMEPLAY_KITS_LIMITS, () -> String.join("\n", this.getKitsLimits().entrySet().stream().map(e -> e.getKey() + " " + e.getValue()).toList()))
            .add(Placeholders.GAMEPLAY_PETS_ALLOWED, () -> LangManager.getBoolean(this.isPetsAllowed()))
            .add(Placeholders.GAMEPLAY_MCMMO_ALLOWED, () -> LangManager.getBoolean(this.isMcmmoAllowed()));
    }

    @Override
    public boolean load() {
        this.setTimeleft(cfg.getInt("Timeleft", 30));
        this.setLobbyTime(cfg.getInt("Lobby_Prepare_Time", 30));
        this.setAnnouncesEnabled(cfg.getBoolean("Announces_Enabled", true));
        this.setScoreboardEnabled(cfg.getBoolean("Scoreboard.Enabled", true));
        this.setScoreboardId(cfg.getString("Scoreboard.Id", Placeholders.DEFAULT));
        this.setHungerEnabled(cfg.getBoolean("Hunger_Enabled"));
        this.setRegenerationEnabled(cfg.getBoolean("Regeneration_Enabled", true));
        this.setItemDropEnabled(cfg.getBoolean("Item_Drop_Enabled"));
        this.setItemPickupEnabled(cfg.getBoolean("Item_Pickup_Enabled"));
        this.setItemDurabilityEnabled(cfg.getBoolean("Item_Durability_Enabled"));
        this.setMobDropExpEnabled(cfg.getBoolean("Mob_Drop_Exp_Enabled"));
        this.setMobDropLootEnabled(cfg.getBoolean("Mob_Drop_Items_Enabled"));

        this.setMobHighlightEnabled(cfg.getBoolean("Mob_Highlight.Enabled"));
        this.setMobHighlightAmount(cfg.getDouble("Mob_Highlight.Amount"));
        this.setMobHighlightColor(cfg.getEnum("Mob_Highlight.Color", ChatColor.class, ChatColor.RED));

        this.bannedItems = new HashSet<>(cfg.getStringSet("Banned_Items").stream()
            .map(Material::getMaterial).filter(Objects::nonNull).toList());
        this.allowedSpawnReasons = new HashSet<>(cfg.getStringSet("Allowed_Spawn_Reasons")
            .stream().map(raw -> StringUtil.getEnum(raw, CreatureSpawnEvent.SpawnReason.class).orElse(null))
            .filter(Objects::nonNull).toList());

        String path = "Players.";
        this.setPlayerMinAmount(cfg.getInt(path + "Minimum", 1));
        this.setPlayerMaxAmount(cfg.getInt(path + "Maximum", 10));
        this.setPlayerLivesAmount(cfg.getInt(path + "Lives", 1));
        this.setPlayerReviveTime(cfg.getInt(path + "Revive_Time", -1));
        this.setKeepInventory(cfg.getBoolean("Keep_Inventory"));

        path = "Spectate.";
        this.setSpectateEnabled(cfg.getBoolean(path + "Enabled", true));

        path = "Commands.";
        this.setPlayerCommandsEnabled(cfg.getBoolean(path + "Allowed"));
        this.setPlayerCommandsAllowed(cfg.getStringSet(path + "Whitelist"));

        path = "Kits.";
        this.setKitsEnabled(cfg.getBoolean(path + "Enabled", true));
        this.setKitsAllowed(cfg.getStringSet(path + "Allowed"));
        if (this.isKitsEnabled() && this.getKitsAllowed().isEmpty()) {
            this.getKitsAllowed().add(Placeholders.WILDCARD);
        }

        Map<String, Integer> kitLimitsMap = new HashMap<>();
        for (String sId : cfg.getSection(path + "Limits")) {
            kitLimitsMap.put(sId.toLowerCase(), cfg.getInt(path + "Limits." + sId));
        }
        this.setKitsLimits(kitLimitsMap);

        path = "Compatibility.";
        this.setPetsAllowed(cfg.getBoolean(path + "Pets_Enabled"));
        this.setMcmmoAllowed(cfg.getBoolean(path + "Mcmmo_Enabled"));

        cfg.saveChanges();
        return true;
    }

    public void clear() {
        if (this.editor != null) {
            this.editor.clear();
            this.editor = null;
        }
    }

    @Override
    public void onSave() {
        cfg.set("Timeleft", this.getTimeleft());
        cfg.set("Lobby_Prepare_Time", this.getLobbyTime());
        cfg.set("Announces_Enabled", this.isAnnouncesEnabled());
        cfg.set("Scoreboard.Enabled", this.isScoreboardEnabled());
        cfg.set("Scoreboard.Id", this.getScoreboardId());
        cfg.set("Hunger_Enabled", this.isHungerEnabled());
        cfg.set("Regeneration_Enabled", this.isRegenerationEnabled());
        cfg.set("Item_Drop_Enabled", this.isItemDropEnabled());
        cfg.set("Item_Pickup_Enabled", this.isItemPickupEnabled());
        cfg.set("Item_Durability_Enabled", this.isItemDurabilityEnabled());
        cfg.set("Mob_Drop_Exp_Enabled", this.isMobDropExpEnabled());
        cfg.set("Mob_Drop_Items_Enabled", this.isMobDropLootEnabled());
        cfg.set("Banned_Items", this.getBannedItems().stream().map(Material::name).toList());
        cfg.set("Allowed_Spawn_Reasons", this.getAllowedSpawnReasons().stream().map(Enum::name).toList());

        cfg.set("Mob_Highlight.Enabled", this.isMobHighlightEnabled());
        cfg.set("Mob_Highlight.Amount", this.getMobHighlightAmount());
        cfg.set("Mob_Highlight.Color", this.getMobHighlightColor().name());

        String path = "Players.";
        cfg.set(path + "Minimum", this.getPlayerMinAmount());
        cfg.set(path + "Maximum", this.getPlayerMaxAmount());
        cfg.set(path + "Lives", this.getPlayerLivesAmount());
        cfg.set(path + "Revive_Time", this.getPlayerReviveTime());
        cfg.set("Keep_Inventory", this.isKeepInventory());

        path = "Spectate.";
        cfg.set(path + "Enabled", this.isSpectateEnabled());

        path = "Commands.";
        cfg.set(path + "Allowed", this.isPlayerCommandsEnabled());
        cfg.set(path + "Whitelist", this.getPlayerCommandsAllowed());

        cfg.set("Auto_Commands", null);

        path = "Kits.";
        cfg.set(path + "Enabled", this.isKitsEnabled());
        cfg.set(path + "Allowed", new ArrayList<>(this.getKitsAllowed()));
        final String path2 = path;
        cfg.set(path2 + "Limits", null);
        this.getKitsLimits().forEach((id, limit) -> {
            cfg.set(path2 + "Limits." + id, limit);
        });

        path = "Compatibility.";
        cfg.set(path + "Pets_Enabled", this.isPetsAllowed());
        cfg.set(path + "Mcmmo_Enabled", this.isMcmmoAllowed());
    }

    @Override
    @NotNull
    public PlaceholderMap getPlaceholders() {
        return this.placeholderMap;
    }

    @NotNull
    public GameplayEditor getEditor() {
        if (this.editor == null) {
            this.editor = new GameplayEditor(this);
        }
        return this.editor;
    }

    @NotNull
    @Override
    public ArenaConfig getArenaConfig() {
        return this.arenaConfig;
    }

    @Override
    @NotNull
    public List<String> getProblems() {
        List<String> list = new ArrayList<>();
        if (this.isKitsEnabled() && this.getKitsAllowed().isEmpty()) {
            list.add(this.problem("Kits are enabled, but no kits are allowed!"));
        }
        return list;
    }

    public int getTimeleft() {
        return this.timeleft;
    }

    public boolean hasTimeleft() {
        return this.getTimeleft() > 0;
    }

    public void setTimeleft(int timeleft) {
        this.timeleft = timeleft;
    }

    public int getLobbyTime() {
        return this.lobbyTime;
    }

    public void setLobbyTime(int lobbyTime) {
        this.lobbyTime = Math.max(1, lobbyTime);
    }

    public boolean isAnnouncesEnabled() {
        return this.isAnnouncesEnabled;
    }

    public void setAnnouncesEnabled(boolean isAnnouncesEnabled) {
        this.isAnnouncesEnabled = isAnnouncesEnabled;
    }

    public boolean isScoreboardEnabled() {
        return this.scoreboardEnabled;
    }

    public void setScoreboardEnabled(boolean isScoreboardEnabled) {
        this.scoreboardEnabled = isScoreboardEnabled && EngineUtils.hasPlugin(HookId.PROTOCOL_LIB);
    }

    @NotNull
    public String getScoreboardId() {
        return scoreboardId;
    }

    public void setScoreboardId(@NotNull String scoreboardId) {
        this.scoreboardId = scoreboardId;
    }

    public boolean isHungerEnabled() {
        return this.isHungerEnabled;
    }

    public void setHungerEnabled(boolean isHungerEnabled) {
        this.isHungerEnabled = isHungerEnabled;
    }

    public boolean isRegenerationEnabled() {
        return this.isRegenerationEnabled;
    }

    public void setRegenerationEnabled(boolean isRegenerationEnabled) {
        this.isRegenerationEnabled = isRegenerationEnabled;
    }

    public boolean isItemDropEnabled() {
        return this.isItemDropEnabled;
    }

    public void setItemDropEnabled(boolean isItemDropEnabled) {
        this.isItemDropEnabled = isItemDropEnabled;
    }

    public boolean isItemPickupEnabled() {
        return this.isItemPickupEnabled;
    }

    public void setItemPickupEnabled(boolean isItemPickupEnabled) {
        this.isItemPickupEnabled = isItemPickupEnabled;
    }

    public boolean isItemDurabilityEnabled() {
        return this.isItemDurabilityEnabled;
    }

    public void setItemDurabilityEnabled(boolean isItemDurabilityEnabled) {
        this.isItemDurabilityEnabled = isItemDurabilityEnabled;
    }

    @NotNull
    public Set<Material> getBannedItems() {
        return bannedItems;
    }

    @NotNull
    public Set<CreatureSpawnEvent.SpawnReason> getAllowedSpawnReasons() {
        return allowedSpawnReasons;
    }

    public boolean isAllowedSpawnReason(@NotNull CreatureSpawnEvent.SpawnReason reason) {
        return this.getAllowedSpawnReasons().contains(reason);
    }

    public boolean isMobDropExpEnabled() {
        return this.isMobDropExpEnabled;
    }

    public void setMobDropExpEnabled(boolean isMobDropExpEnabled) {
        this.isMobDropExpEnabled = isMobDropExpEnabled;
    }

    public boolean isMobDropLootEnabled() {
        return isMobDropLootEnabled;
    }

    public void setMobDropLootEnabled(boolean mobDropLootEnabled) {
        isMobDropLootEnabled = mobDropLootEnabled;
    }

    public boolean isMobHighlightEnabled() {
        return mobHighlightEnabled;
    }

    public void setMobHighlightEnabled(boolean mobHighlightEnabled) {
        this.mobHighlightEnabled = mobHighlightEnabled;
    }

    public double getMobHighlightAmount() {
        return mobHighlightAmount;
    }

    public void setMobHighlightAmount(double mobHighlightAmount) {
        this.mobHighlightAmount = mobHighlightAmount;
    }

    @NotNull
    public ChatColor getMobHighlightColor() {
        return mobHighlightColor;
    }

    public void setMobHighlightColor(@NotNull ChatColor mobHighlightColor) {
        this.mobHighlightColor = mobHighlightColor;
    }

    public int getPlayerMinAmount() {
        return playerMinAmount;
    }

    public void setPlayerMinAmount(int playerMinAmount) {
        this.playerMinAmount = Math.max(1, playerMinAmount);
    }

    public int getPlayerMaxAmount() {
        return playerMaxAmount;
    }

    public void setPlayerMaxAmount(int playerMaxAmount) {
        this.playerMaxAmount = Math.max(1, playerMaxAmount);
    }

    public int getPlayerLivesAmount() {
        return playerLivesAmount;
    }

    public void setPlayerLivesAmount(int playerLivesAmount) {
        this.playerLivesAmount = Math.max(1, playerLivesAmount);
    }

    public int getPlayerReviveTime() {
        return playerReviveTime;
    }

    public void setPlayerReviveTime(int playerReviveTime) {
        this.playerReviveTime = playerReviveTime;
    }

    public boolean isKeepInventory() {
        return keepInventory;
    }

    public void setKeepInventory(boolean keepInventory) {
        this.keepInventory = keepInventory;
    }

    public boolean isPlayerCommandsEnabled() {
        return isPlayerCommandsEnabled;
    }

    public void setPlayerCommandsEnabled(boolean playerCommandsEnabled) {
        isPlayerCommandsEnabled = playerCommandsEnabled;
    }

    @NotNull
    public Set<String> getPlayerCommandsAllowed() {
        return playerCommandsAllowed;
    }

    public void setPlayerCommandsAllowed(@NotNull Set<String> playerCommandsAllowed) {
        this.playerCommandsAllowed = playerCommandsAllowed;
    }

    public boolean isPlayerCommandAllowed(@NotNull String cmd) {
        Set<String> aliases = CommandRegister.getAliases(cmd, true);
        return aliases.stream().anyMatch(alias -> this.getPlayerCommandsAllowed().contains(alias));
    }

    public boolean isSpectateEnabled() {
        return this.isSpectateEnabled;
    }

    public void setSpectateEnabled(boolean spectateEnabled) {
        isSpectateEnabled = spectateEnabled;
    }

    public boolean isKitsEnabled() {
        return isKitsEnabled;
    }

    public void setKitsEnabled(boolean kitsEnabled) {
        isKitsEnabled = kitsEnabled;
    }

    @NotNull
    public Set<String> getKitsAllowed() {
        return kitsAllowed;
    }

    public void setKitsAllowed(@NotNull Set<String> kitsAllowed) {
        this.kitsAllowed = kitsAllowed;
    }

    public boolean isKitAllowed(@NotNull Kit kit) {
        return this.isKitAllowed(kit.getId());
    }

    public boolean isKitAllowed(@NotNull String id) {
        return this.getKitsAllowed().contains(id) || this.getKitsAllowed().contains(Placeholders.WILDCARD);
    }

    @NotNull
    public Map<String, Integer> getKitsLimits() {
        return kitsLimits;
    }

    public void setKitsLimits(@NotNull Map<String, Integer> kitsLimits) {
        this.kitsLimits = kitsLimits;
    }

    public int getKitLimit(@NotNull Kit kit) {
        return this.getKitLimit(kit.getId());
    }

    public int getKitLimit(@NotNull String id) {
        return this.getKitsLimits().getOrDefault(id, -1);
    }

    public boolean isPetsAllowed() {
        return petsAllowed;
    }

    public void setPetsAllowed(boolean petsAllowed) {
        this.petsAllowed = petsAllowed;
    }

    public boolean isMcmmoAllowed() {
        return mcmmoAllowed;
    }

    public void setMcmmoAllowed(boolean mcmmoAllowed) {
        this.mcmmoAllowed = mcmmoAllowed;
    }
}
