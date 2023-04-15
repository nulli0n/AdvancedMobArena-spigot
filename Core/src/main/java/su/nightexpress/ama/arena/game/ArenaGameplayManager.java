package su.nightexpress.ama.arena.game;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.config.JYML;
import su.nexmedia.engine.api.manager.ConfigHolder;
import su.nexmedia.engine.api.manager.IEditable;
import su.nexmedia.engine.api.manager.ILoadable;
import su.nexmedia.engine.api.placeholder.Placeholder;
import su.nexmedia.engine.api.placeholder.PlaceholderMap;
import su.nexmedia.engine.command.CommandRegister;
import su.nexmedia.engine.hooks.Hooks;
import su.nexmedia.engine.lang.LangManager;
import su.nexmedia.engine.utils.CollectionsUtil;
import su.nexmedia.engine.utils.NumberUtil;
import su.nexmedia.engine.utils.StringUtil;
import su.nightexpress.ama.Placeholders;
import su.nightexpress.ama.api.arena.ArenaChild;
import su.nightexpress.ama.api.arena.Problematic;
import su.nightexpress.ama.api.arena.type.ArenaGameEventType;
import su.nightexpress.ama.api.arena.type.ArenaTargetType;
import su.nightexpress.ama.arena.editor.game.GameplayEditor;
import su.nightexpress.ama.arena.impl.ArenaConfig;
import su.nightexpress.ama.arena.script.action.ParameterResult;
import su.nightexpress.ama.arena.script.action.Parameters;
import su.nightexpress.ama.arena.script.action.ScriptActions;
import su.nightexpress.ama.arena.script.action.ScriptPreparedAction;
import su.nightexpress.ama.arena.script.condition.ScriptPreparedCondition;
import su.nightexpress.ama.arena.script.impl.ArenaScript;
import su.nightexpress.ama.config.Lang;
import su.nightexpress.ama.hook.HookId;
import su.nightexpress.ama.kit.Kit;

import java.util.*;

public class ArenaGameplayManager implements ArenaChild, ConfigHolder, ILoadable, Problematic, Placeholder, IEditable {

    private final ArenaConfig arenaConfig;
    private final JYML        config;

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

    private boolean   mobHighlightEnabled;
    private double    mobHighlightAmount;
    private ChatColor mobHighlightColor;

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

    private final PlaceholderMap placeholderMap;

    private static final String CONFIG_NAME = "gameplay.yml";

    public ArenaGameplayManager(@NotNull ArenaConfig arenaConfig) {
        this.arenaConfig = arenaConfig;
        this.config = new JYML(arenaConfig.getFile().getParentFile().getAbsolutePath(), CONFIG_NAME);
        this.bannedItems = new HashSet<>();
        this.allowedSpawnReasons = new HashSet<>();
        this.playerCommandsAllowed = new HashSet<>();
        this.kitsAllowed = new HashSet<>();
        this.kitsLimits = new HashMap<>();
        this.setMobHighlightColor(ChatColor.RED);

        this.placeholderMap = new PlaceholderMap()
            .add(Placeholders.GENERIC_PROBLEMS, () -> String.join("\n", this.getProblems()))
            .add(Placeholders.GAMEPLAY_TIMELEFT, () -> this.hasTimeleft() ? String.valueOf(this.getTimeleft()) : LangManager.getPlain(Lang.OTHER_INFINITY))
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
            .add(Placeholders.GAMEPLAY_PLAYERS_AMOUNT_MIN, () -> String.valueOf(this.getPlayerMinAmount()))
            .add(Placeholders.GAMEPLAY_PLAYERS_AMOUNT_MAX, () -> String.valueOf(this.getPlayerMaxAmount()))
            .add(Placeholders.GAMEPLAY_PLAYER_DEATH_LIVES_AMOUNT, () -> String.valueOf(this.getPlayerLivesAmount()))
            .add(Placeholders.GAMEPLAY_PLAYER_DEATH_DROP_ITEMS, () -> LangManager.getBoolean(this.isPlayerDropItemsOnDeathEnabled()))
            .add(Placeholders.GAMEPLAY_SPECTATE_ENABLED, () -> LangManager.getBoolean(this.isSpectateEnabled()))
            .add(Placeholders.GAMEPLAY_SPECTATE_ON_DEATH, () -> LangManager.getBoolean(this.isSpectateOnDeathEnabled()))
            .add(Placeholders.GAMEPLAY_COMMAND_USAGE_ENABLED, () -> LangManager.getBoolean(this.isPlayerCommandsEnabled()))
            .add(Placeholders.GAMEPLAY_COMMAND_USAGE_WHITELIST, () -> String.join("\n", this.getPlayerCommandsAllowed()))
            .add(Placeholders.GAMEPLAY_KITS_ENABLED, () -> LangManager.getBoolean(this.isKitsEnabled()))
            .add(Placeholders.GAMEPLAY_KITS_ALLOWED, () -> String.join("\n", this.getKitsAllowed()))
            .add(Placeholders.GAMEPLAY_KITS_LIMITS, () -> String.join("\n", this.getKitsLimits().entrySet().stream().map(e -> e.getKey() + " " + e.getValue()).toList()))
            .add(Placeholders.GAMEPLAY_PETS_ALLOWED, () -> LangManager.getBoolean(this.isExternalPetsEnabled()))
            .add(Placeholders.GAMEPLAY_MCMMO_ALLOWED, () -> LangManager.getBoolean(this.isExternalMcmmoEnabled()));
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

        for (String cmdId : config.getSection("Auto_Commands")) {
            String path2 = "Auto_Commands." + cmdId + ".";
            ArenaTargetType targetType = config.getEnum(path2 + "Target", ArenaTargetType.class, ArenaTargetType.GLOBAL);
            List<String> commands = config.getStringList(path2 + "Commands");

            // ----------- CONVERT SCRIPTS START -----------
            for (String eventRaw : config.getSection(path2 + "Triggers")) {
                ArenaGameEventType eventType = StringUtil.getEnum(eventRaw, ArenaGameEventType.class).orElse(null);
                if (eventType == null) continue;

                String name = "command_" + cmdId;
                ArenaScript script = new ArenaScript(this.arenaConfig, name, eventType);

                String values = config.getString(path2 + "Triggers." + eventRaw, "");
                Map<String, List<ScriptPreparedCondition>> conditions = ArenaScript.ofGameTrigger(eventType, values);
                script.getConditions().putAll(conditions);

                for (String command : commands) {
                    ScriptPreparedAction action = new ScriptPreparedAction(ScriptActions.RUN_COMMAND, new ParameterResult());
                    action.getParameters().add(Parameters.NAME, command);
                    action.getParameters().add(Parameters.TARGET, targetType.name());
                    script.getActions().add(action);
                }

                this.getArenaConfig().getScriptManager().addConverted(script);
            }
            config.remove(path2 + "Triggers");
            // ----------- CONVERT SCRIPTS END -----------
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
            this.getKitsAllowed().add(Placeholders.WILDCARD);
        }

        Map<String, Integer> kitLimitsMap = new HashMap<>();
        for (String sId : config.getSection(path + "Limits")) {
            kitLimitsMap.put(sId.toLowerCase(), config.getInt(path + "Limits." + sId));
        }
        this.setKitsLimits(kitLimitsMap);

        path = "Compatibility.";
        this.setExternalPetsEnabled(config.getBoolean(path + "Pets_Enabled"));
        this.setExternalMcmmoEnabled(config.getBoolean(path + "Mcmmo_Enabled"));

        config.saveChanges();
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
    public PlaceholderMap getPlaceholders() {
        return this.placeholderMap;
    }

    @Override
    @NotNull
    public GameplayEditor getEditor() {
        if (this.editor == null) {
            this.editor = new GameplayEditor(this);
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
            list.add(problem("Kits are enabled, but no kits are allowed!"));
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

    @Deprecated
    public boolean isSpectateOnDeathEnabled() {
        return isSpectateOnDeathEnabled;
    }

    @Deprecated
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
