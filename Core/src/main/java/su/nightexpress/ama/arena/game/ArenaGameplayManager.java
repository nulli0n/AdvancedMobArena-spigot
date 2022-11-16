package su.nightexpress.ama.arena.game;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.config.JYML;
import su.nexmedia.engine.api.manager.ConfigHolder;
import su.nexmedia.engine.api.manager.IEditable;
import su.nexmedia.engine.api.manager.ILoadable;
import su.nexmedia.engine.command.CommandRegister;
import su.nexmedia.engine.hooks.Hooks;
import su.nexmedia.engine.lang.LangManager;
import su.nexmedia.engine.utils.CollectionsUtil;
import su.nexmedia.engine.utils.NumberUtil;
import su.nightexpress.ama.Placeholders;
import su.nightexpress.ama.api.arena.IArenaObject;
import su.nightexpress.ama.api.arena.IProblematic;
import su.nightexpress.ama.api.arena.game.ArenaGameEventTrigger;
import su.nightexpress.ama.api.arena.type.ArenaTargetType;
import su.nightexpress.ama.arena.config.ArenaConfig;
import su.nightexpress.ama.arena.editor.game.EditorArenaGameplay;
import su.nightexpress.ama.hook.HookId;
import su.nightexpress.ama.kit.Kit;

import java.util.*;
import java.util.function.UnaryOperator;

public class ArenaGameplayManager implements IArenaObject, ConfigHolder, ILoadable, IProblematic, IEditable {

    private final ArenaConfig arenaConfig;
    private final JYML        config;

    private EditorArenaGameplay editor;

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

    private boolean   mobHighlightEnabled;
    private double    mobHighlightAmount;
    private ChatColor mobHighlightColor;

    private Set<ArenaGameCommand>               autoCommands;
    private Set<Material>                       bannedItems;
    private Set<CreatureSpawnEvent.SpawnReason> allowedSpawnReasons;

    private int         playerMinAmount;
    private int         playerMaxAmount;
    private int         playerLivesAmount;
    private boolean     isPlayerDropItemsOnDeathEnabled;
    private boolean     isPlayerCommandsEnabled;
    private Set<String> playerCommandsAllowed;

    private boolean isSpectateEnabled;
    private boolean isSpectateOnDeathEnabled;

    private boolean              isKitsEnabled;
    private Set<String>          kitsAllowed;
    private Map<String, Integer> kitsLimits;

    private boolean isExternalPetsEnabled;
    private boolean isExternalMcmmoEnabled;

    private static final String CONFIG_NAME = "gameplay.yml";

    public ArenaGameplayManager(@NotNull ArenaConfig arenaConfig) {
        this.arenaConfig = arenaConfig;
        this.config = new JYML(arenaConfig.getFile().getParentFile().getAbsolutePath(), CONFIG_NAME);
    }

    @Override
    public void setup() {
        this.setTimeleft(config.getInt("Timeleft", 30));
        this.setLobbyTime(config.getInt("Lobby_Prepare_Time", 30));
        this.setAnnouncesEnabled(config.getBoolean("Announces_Enabled", true));
        this.setScoreboardEnabled(config.getBoolean("Scoreboard.Enabled", true));
        this.setScoreboardId(config.getString("Scoreboard.Id", Placeholders.DEFAULT));
        this.setHungerEnabled(config.getBoolean("Hunger_Enabled"));
        this.setRegenerationEnabled(config.getBoolean("Regeneration_Enabled", true));
        this.setItemDropEnabled(config.getBoolean("Item_Drop_Enabled"));
        this.setItemPickupEnabled(config.getBoolean("Item_Pickup_Enabled"));
        this.setItemDurabilityEnabled(config.getBoolean("Item_Durability_Enabled"));
        this.setMobDropExpEnabled(config.getBoolean("Mob_Drop_Exp_Enabled"));
        this.setMobDropLootEnabled(config.getBoolean("Mob_Drop_Items_Enabled"));

        this.setMobHighlightEnabled(config.getBoolean("Mob_Highlight.Enabled"));
        this.setMobHighlightAmount(config.getDouble("Mob_Highlight.Amount"));
        this.setMobHighlightColor(config.getEnum("Mob_Highlight.Color", ChatColor.class, ChatColor.RED));

        this.bannedItems = new HashSet<>(config.getStringSet("Banned_Items").stream()
            .map(Material::getMaterial).filter(Objects::nonNull).toList());
        this.allowedSpawnReasons = new HashSet<>(config.getStringSet("Allowed_Spawn_Reasons")
            .stream().map(raw -> CollectionsUtil.getEnum(raw, CreatureSpawnEvent.SpawnReason.class))
            .filter(Objects::nonNull).toList());

        this.autoCommands = new HashSet<>();
        for (String cmdId : config.getSection("Auto_Commands")) {
            String path2 = "Auto_Commands." + cmdId + ".";
            Set<ArenaGameEventTrigger<?>> triggers = ArenaGameEventTrigger.parse(config, path2 + "Triggers");
            ArenaTargetType targetType = config.getEnum(path2 + "Target", ArenaTargetType.class, ArenaTargetType.GLOBAL);
            List<String> commands = config.getStringList(path2 + "Commands");

            ArenaGameCommand gameCommand = new ArenaGameCommand(arenaConfig, triggers, targetType, commands);
            this.getAutoCommands().add(gameCommand);
        }

        String path = "Players.";
        this.setPlayerMinAmount(config.getInt(path + "Minimum", 1));
        this.setPlayerMaxAmount(config.getInt(path + "Maximum", 10));
        this.setPlayerLivesAmount(config.getInt(path + "Lives", 1));
        this.setPlayerDropItemsOnDeathEnabled(config.getBoolean(path + "Drop_Items_On_Death"));

        path = "Spectate.";
        this.setSpectateEnabled(config.getBoolean(path + "Enabled", true));
        this.setSpectateOnDeathEnabled(config.getBoolean(path + "After_Death", true));

        path = "Commands.";
        this.setPlayerCommandsEnabled(config.getBoolean(path + "Allowed"));
        this.setPlayerCommandsAllowed(config.getStringSet(path + "Whitelist"));

        path = "Kits.";
        this.setKitsEnabled(config.getBoolean(path + "Enabled", true));
        this.setKitsAllowed(config.getStringSet(path + "Allowed"));
        if (this.isKitsEnabled() && this.getKitsAllowed().isEmpty()) {
            this.getKitsAllowed().add(Placeholders.MASK_ANY);
        }

        Map<String, Integer> kitLimitsMap = new HashMap<>();
        for (String sId : config.getSection(path + "Limits")) {
            kitLimitsMap.put(sId.toLowerCase(), config.getInt(path + "Limits." + sId));
        }
        this.setKitsLimits(kitLimitsMap);

        path = "Compatibility.";
        this.setExternalPetsEnabled(config.getBoolean(path + "Pets_Enabled"));
        this.setExternalMcmmoEnabled(config.getBoolean(path + "Mcmmo_Enabled"));
    }

    @Override
    public void shutdown() {
        if (this.editor != null) {
            this.editor.clear();
            this.editor = null;
        }
    }

    @Override
    public void onSave() {
        config.set("Timeleft", this.getTimeleft());
        config.set("Lobby_Prepare_Time", this.getLobbyTime());
        config.set("Announces_Enabled", this.isAnnouncesEnabled());
        config.set("Scoreboard.Enabled", this.isScoreboardEnabled());
        config.set("Scoreboard.Id", this.getScoreboardId());
        config.set("Hunger_Enabled", this.isHungerEnabled());
        config.set("Regeneration_Enabled", this.isRegenerationEnabled());
        config.set("Item_Drop_Enabled", this.isItemDropEnabled());
        config.set("Item_Pickup_Enabled", this.isItemPickupEnabled());
        config.set("Item_Durability_Enabled", this.isItemDurabilityEnabled());
        config.set("Mob_Drop_Exp_Enabled", this.isMobDropExpEnabled());
        config.set("Mob_Drop_Items_Enabled", this.isMobDropLootEnabled());
        config.set("Banned_Items", this.getBannedItems().stream().map(Material::name).toList());
        config.set("Allowed_Spawn_Reasons", this.getAllowedSpawnReasons().stream().map(Enum::name).toList());

        config.set("Mob_Highlight.Enabled", this.isMobHighlightEnabled());
        config.set("Mob_Highlight.Amount", this.getMobHighlightAmount());
        config.set("Mob_Highlight.Color", this.getMobHighlightColor().name());

        String path = "Players.";
        config.set(path + "Minimum", this.getPlayerMinAmount());
        config.set(path + "Maximum", this.getPlayerMaxAmount());
        config.set(path + "Lives", this.getPlayerLivesAmount());
        config.set(path + "Drop_Items_On_Death", this.isPlayerDropItemsOnDeathEnabled());

        path = "Spectate.";
        config.set(path + "Enabled", this.isSpectateEnabled());
        config.set(path + "After_Death", this.isSpectateOnDeathEnabled());

        path = "Commands.";
        config.set(path + "Allowed", this.isPlayerCommandsEnabled());
        config.set(path + "Whitelist", this.getPlayerCommandsAllowed());

        config.set("Auto_Commands", null);
        this.getAutoCommands().forEach(gameCommand -> {
            String path2 = "Auto_Commands." + UUID.randomUUID() + ".";

            gameCommand.getTriggers().forEach(trigger -> {
                config.set(path2 + "Triggers." + trigger.getType().name(), trigger.getValuesRaw());
            });
            config.set(path2 + "Target", gameCommand.getTargetType().name());
            config.set(path2 + "Commands", gameCommand.getCommands());
        });

        path = "Kits.";
        config.set(path + "Enabled", this.isKitsEnabled());
        config.set(path + "Allowed", new ArrayList<>(this.getKitsAllowed()));
        final String path2 = path;
        config.set(path2 + "Limits", null);
        this.getKitsLimits().forEach((id, limit) -> {
            config.set(path2 + "Limits." + id, limit);
        });

        path = "Compatibility.";
        config.set(path + "Pets_Enabled", this.isExternalPetsEnabled());
        config.set(path + "Mcmmo_Enabled", this.isExternalMcmmoEnabled());
    }

    @Override
    @NotNull
    public UnaryOperator<String> replacePlaceholders() {
        return str -> str
            .replace(Placeholders.GENERIC_PROBLEMS, Placeholders.formatProblems(this.getProblems()))
            .replace(Placeholders.GAMEPLAY_TIMELEFT, this.getTimeleft() > 0 ? String.valueOf(this.getTimeleft()) : "-")
            .replace(Placeholders.GAMEPLAY_LOBBY_TIME, String.valueOf(this.getLobbyTime()))
            .replace(Placeholders.GAMEPLAY_ANNOUNCEMENTS, LangManager.getBoolean(this.isAnnouncesEnabled()))
            .replace(Placeholders.GAMEPLAY_SCOREBOARD_ENABLED, LangManager.getBoolean(this.isScoreboardEnabled()))
            .replace(Placeholders.GAMEPLAY_SCOREBOARD_ID, this.getScoreboardId())
            .replace(Placeholders.GAMEPLAY_HUNGER_ENABLED, LangManager.getBoolean(this.isHungerEnabled()))
            .replace(Placeholders.GAMEPLAY_REGENERATION_ENABLED, LangManager.getBoolean(this.isRegenerationEnabled()))
            .replace(Placeholders.GAMEPLAY_ITEM_DROP_ENABLED, LangManager.getBoolean(this.isItemDropEnabled()))
            .replace(Placeholders.GAMEPLAY_ITEM_PICKUP_ENABLED, LangManager.getBoolean(this.isItemPickupEnabled()))
            .replace(Placeholders.GAMEPLAY_ITEM_DURABULITY_ENABLED, LangManager.getBoolean(this.isItemDurabilityEnabled()))
            .replace(Placeholders.GAMEPLAY_MOB_DROP_EXP, LangManager.getBoolean(this.isMobDropExpEnabled()))
            .replace(Placeholders.GAMEPLAY_MOB_DROP_ITEMS, LangManager.getBoolean(this.isMobDropLootEnabled()))
            .replace(Placeholders.GAMEPLAY_MOB_HIGHLIGHT_ENABLED, LangManager.getBoolean(this.isMobHighlightEnabled()))
            .replace(Placeholders.GAMEPLAY_MOB_HIGHLIGHT_AMOUNT, NumberUtil.format(this.getMobHighlightAmount()))
            .replace(Placeholders.GAMEPLAY_MOB_HIGHLIGHT_COLOR, this.getMobHighlightColor().name())
            .replace(Placeholders.GAMEPLAY_BANNED_ITEMS, String.join("\n", this.getBannedItems().stream().map(Enum::name).toList()))
            .replace(Placeholders.GAMEPLAY_ALLOWED_SPAWN_REASONS, String.join("\n", this.getAllowedSpawnReasons().stream().map(Enum::name).toList()))
            .replace(Placeholders.GAMEPLAY_PLAYERS_AMOUNT_MIN, String.valueOf(this.getPlayerMinAmount()))
            .replace(Placeholders.GAMEPLAY_PLAYERS_AMOUNT_MAX, String.valueOf(this.getPlayerMaxAmount()))
            .replace(Placeholders.GAMEPLAY_PLAYER_DEATH_LIVES_AMOUNT, String.valueOf(this.getPlayerLivesAmount()))
            .replace(Placeholders.GAMEPLAY_PLAYER_DEATH_DROP_ITEMS, LangManager.getBoolean(this.isPlayerDropItemsOnDeathEnabled()))
            .replace(Placeholders.GAMEPLAY_SPECTATE_ENABLED, LangManager.getBoolean(this.isSpectateEnabled()))
            .replace(Placeholders.GAMEPLAY_SPECTATE_ON_DEATH, LangManager.getBoolean(this.isSpectateOnDeathEnabled()))
            .replace(Placeholders.GAMEPLAY_COMMAND_USAGE_ENABLED, LangManager.getBoolean(this.isPlayerCommandsEnabled()))
            .replace(Placeholders.GAMEPLAY_COMMAND_USAGE_WHITELIST, String.join("\n", this.getPlayerCommandsAllowed()))
            .replace(Placeholders.GAMEPLAY_KITS_ENABLED, LangManager.getBoolean(this.isKitsEnabled()))
            .replace(Placeholders.GAMEPLAY_KITS_ALLOWED, String.join("\n", this.getKitsAllowed()))
            .replace(Placeholders.GAMEPLAY_KITS_LIMITS, String.join("\n", this.getKitsLimits().entrySet().stream().map(e -> e.getKey() + " " + e.getValue()).toList()))
            .replace(Placeholders.GAMEPLAY_PETS_ALLOWED, LangManager.getBoolean(this.isExternalPetsEnabled()))
            .replace(Placeholders.GAMEPLAY_MCMMO_ALLOWED, LangManager.getBoolean(this.isExternalMcmmoEnabled()));
    }

    @Override
    @NotNull
    public EditorArenaGameplay getEditor() {
        if (this.editor == null) {
            this.editor = new EditorArenaGameplay(this);
        }
        return this.editor;
    }

    @NotNull
    @Override
    public JYML getConfig() {
        return config;
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
            list.add("Kits are enabled, but no kits are allowed!");
        }
        return list;
    }

    public int getTimeleft() {
        return this.timeleft;
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
        this.scoreboardEnabled = isScoreboardEnabled && Hooks.hasPlugin(HookId.PROTOCOL_LIB);
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

    @NotNull
    public Set<ArenaGameCommand> getAutoCommands() {
        return autoCommands;
    }

    public int getPlayerMinAmount() {
        return playerMinAmount;
    }

    public void setPlayerMinAmount(int playerMinAmount) {
        this.playerMinAmount = playerMinAmount;
    }

    public int getPlayerMaxAmount() {
        return playerMaxAmount;
    }

    public void setPlayerMaxAmount(int playerMaxAmount) {
        this.playerMaxAmount = playerMaxAmount;
    }

    public int getPlayerLivesAmount() {
        return playerLivesAmount;
    }

    public void setPlayerLivesAmount(int playerLivesAmount) {
        this.playerLivesAmount = playerLivesAmount;
    }

    public boolean isPlayerDropItemsOnDeathEnabled() {
        return isPlayerDropItemsOnDeathEnabled;
    }

    public void setPlayerDropItemsOnDeathEnabled(boolean playerDropItemsOnDeathEnabled) {
        isPlayerDropItemsOnDeathEnabled = playerDropItemsOnDeathEnabled;
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

    public boolean isSpectateOnDeathEnabled() {
        return isSpectateOnDeathEnabled;
    }

    public void setSpectateOnDeathEnabled(boolean spectateOnDeathEnabled) {
        isSpectateOnDeathEnabled = spectateOnDeathEnabled;
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
        return this.getKitsAllowed().contains(id) || this.getKitsAllowed().contains(Placeholders.MASK_ANY);
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

    public boolean isExternalPetsEnabled() {
        return isExternalPetsEnabled;
    }

    public void setExternalPetsEnabled(boolean externalPetsEnabled) {
        isExternalPetsEnabled = externalPetsEnabled;
    }

    public boolean isExternalMcmmoEnabled() {
        return isExternalMcmmoEnabled;
    }

    public void setExternalMcmmoEnabled(boolean externalMcmmoEnabled) {
        isExternalMcmmoEnabled = externalMcmmoEnabled;
    }
}
