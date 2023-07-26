package su.nightexpress.ama.editor;

import su.nexmedia.engine.api.editor.EditorLocale;
import su.nightexpress.ama.Placeholders;
import su.nightexpress.ama.hook.HookId;

public class EditorLocales extends su.nexmedia.engine.api.editor.EditorLocales {

    private static final String PREFIX_OLD = "Editor.ArenaEditorType.";
    private static final String PREFIX = "Editor.";

    public static final EditorLocale ARENA_EDITOR = builder(PREFIX_OLD + "EDITOR_ARENA")
        .name("Arena Editor")
        .text("Create and manage your arenas here!").breakLine()
        .actionsHeader().action("Left-Click", "Navigate")
        .build();

    public static final EditorLocale KIT_EDITOR = builder(PREFIX_OLD + "EDITOR_KITS")
        .name("Kit Editor")
        .text("Create and manage your kits here!").breakLine()
        .actionsHeader().action("Left-Click", "Navigate")
        .build();

    public static final EditorLocale MOB_EDITOR = builder(PREFIX_OLD + "EDITOR_MOBS")
        .name("Mob Editor")
        .text("Create and manage your mobs here!").breakLine()
        .actionsHeader().action("Left-Click", "Navigate")
        .build();

    public static final EditorLocale ARENA_OBJECT = builder(PREFIX_OLD + "ARENA_OBJECT")
        .name(Placeholders.ARENA_NAME + GRAY + " (ID: &f" + Placeholders.ARENA_ID + GRAY + ")")
        .current("Enabled", Placeholders.ARENA_ACTIVE).breakLine()
        .textRaw(Placeholders.GENERIC_PROBLEMS).breakLine()
        .actionsHeader().action("Left-Click", "Edit").action("Shift-Right", "Delete " + RED + "(No Undo)")
        .build();

    public static final EditorLocale ARENA_CREATION = builder(PREFIX_OLD + "ARENA_CREATE")
        .name("Create Arena")
        .build();

    public static final EditorLocale ARENA_ACTIVE = builder(PREFIX_OLD + "ARENA_CHANGE_ACTIVE")
        .name("Is Active")
        .text("Sets whether or nor arena", "is enabled for players.").breakLine()
        .currentHeader().current("Enabled", Placeholders.ARENA_ACTIVE).breakLine()
        .textRaw(Placeholders.GENERIC_PROBLEMS).breakLine()
        .actionsHeader().action("Left-Click", "Toggle")
        .build();

    public static final EditorLocale ARENA_NAME = builder(PREFIX_OLD + "ARENA_CHANGE_NAME")
        .name("Arena Display Name")
        .text("Sets the arena display name.", "It's used in messages, GUIs, holograms, etc.").breakLine()
        .currentHeader().current("Name", Placeholders.ARENA_NAME).breakLine()
        .actionsHeader().action("Left-Click", "Change")
        .build();

    public static final EditorLocale ARENA_SETUP_KIT = builder(PREFIX_OLD + "ARENA_SETUP_KIT")
        .name("Setup Kit")
        .text("A set of tools to setup arena location.").breakLine()
        .actionsHeader().action("Left-Click", "Get")
        .build();

    public static final EditorLocale ARENA_AUTO_STATE_SCHEDULERS = builder(PREFIX_OLD + "ARENA_AUTO_STATE_SCHEDULERS")
        .name("Auto State Schedulers")
        .text("This setting allows you to setup", "automatic arena opening/closing", "at certain day times.").breakLine()
        .currentHeader().current("Open Times", "").text(Placeholders.ARENA_AUTO_STATE_OPEN_TIMES)
        .current("Close Times", "").text(Placeholders.ARENA_AUTO_STATE_CLOSE_TIMES).breakLine()
        .actionsHeader()
        .action("Left-Click", "Add Open Time").action("Right-Click", "Add Close Time")
        .action("Shift-Left", "Clear Open Times").action("Shift-Right", "Clear Close Times")
        .build();

    public static final EditorLocale ARENA_GAMEPLAY_MANAGER = builder(PREFIX_OLD + "ARENA_OPEN_GAMEPLAY_MANAGER")
        .name("Gameplay Manager")
        .text("A set of various gameplay features and options.").breakLine()
        .textRaw(Placeholders.GENERIC_PROBLEMS).breakLine()
        .actionsHeader().action("Left-Click", "Navigate")
        .build();

    public static final EditorLocale ARENA_WAVE_MANAGER = builder(PREFIX_OLD + "ARENA_OPEN_WAVE_MANAGER")
        .name("Wave Manager")
        .text("Here you predefine mob groups", "to be spawned during the game.").breakLine()
        .noteHeader().text("You have to create " + ORANGE + "script(s)" + GRAY + " to", "spawn your mobs!").breakLine()
        .textRaw(Placeholders.GENERIC_PROBLEMS).breakLine()
        .actionsHeader().action("Left-Click", "Navigate")
        .build();

    public static final EditorLocale ARENA_REGION_MANAGER = builder(PREFIX_OLD + "ARENA_OPEN_REGION_MANAGER")
        .name("Region Manager")
        .text("Here you predefine arena regions where", "players will fight against mobs.").breakLine()
        .noteHeader().text("You have to create " + ORANGE + "script(s)" + GRAY + " to", "lock or unlock regions!").breakLine()
        .textRaw(Placeholders.GENERIC_PROBLEMS).breakLine()
        .actionsHeader().action("Left-Click", "Navigate")
        .build();

    public static final EditorLocale ARENA_REWARD_MANAGER = builder(PREFIX_OLD + "ARENA_OPEN_REWARD_MANAGER")
        .name("Reward Manager")
        .text("Here you predefine arena rewards that", "can be given during the game to players.").breakLine()
        .noteHeader().text("You have to create " + ORANGE + "script(s)" + GRAY + " to", "give rewards to players!").breakLine()
        .textRaw(Placeholders.GENERIC_PROBLEMS).breakLine()
        .actionsHeader().action("Left-Click", "Navigate")
        .build();

    public static final EditorLocale ARENA_SUPPLY_MANAGER = builder(PREFIX_OLD + "ARENA_OPEN_SUPPLY_MANAGER")
        .name("Supply Manager")
        .text("Here you predefine arena chests that", "can be used to generate items in.").breakLine()
        .noteHeader().text("You have to create " + ORANGE + "script(s)" + GRAY + " to", "refill supply chests!").breakLine()
        //.text(Placeholders.GENERIC_PROBLEMS).breakLine()
        .actionsHeader().action("Left-Click", "Navigate")
        .build();

    public static final EditorLocale ARENA_SCRIPT_MANAGER = builder(PREFIX_OLD + "ARENA_OPEN_SCRIPT_MANAGER")
        .name("Script Manager")
        .text("Here you create scripts for all", "the arena aspects and mechanics.")
        .text("Without them you arena will be unplayable.").breakLine()
        //.text(Placeholders.GENERIC_PROBLEMS).breakLine()
        .actionsHeader().action("Left-Click", "Navigate")
        .build();

    public static final EditorLocale ARENA_SHOP_MANAGER = builder(PREFIX_OLD + "ARENA_OPEN_SHOP_MANAGER")
        .name("Shop Manager")
        .text("Arena in-game shop.", "Allows players to purchase items", "during the game.").breakLine()
        .noteHeader().text("You have to create " + ORANGE + "script(s)" + GRAY + " to", "lock or unlock shop items!").breakLine()
        .textRaw(Placeholders.GENERIC_PROBLEMS).breakLine()
        .actionsHeader().action("Left-Click", "Navigate")
        .build();

    public static final EditorLocale ARENA_SPOT_MANAGER = builder(PREFIX_OLD + "ARENA_OPEN_SPOT_MANAGER")
        .name("Spot Manager")
        .text("Here you can create special zones,", "that will be changed during the game", "It could be openable doors or gates,", "landscape changing, etc.").breakLine()
        .noteHeader().text("You have to create " + ORANGE + "script(s)" + GRAY + " to", "activate your spots!").breakLine()
        .textRaw(Placeholders.GENERIC_PROBLEMS).breakLine()
        .actionsHeader().action("Left-Click", "Navigate")
        .build();

    public static final EditorLocale ARENA_PERMISSION_REQUIREMENT = builder(PREFIX_OLD + "ARENA_CHANGE_REQUIREMENT_PERMISSION")
        .name("Permission Requirement(s)")
        .text("Sets whether or not permission", "is required to join the arena.").breakLine()
        .currentHeader().current("Enabled", Placeholders.ARENA_REQUIREMENT_PERMISSION)
        .current("Node", Placeholders.ARENA_PERMISSION).breakLine()
        .actionsHeader().action("Left-Click", "Toggle")
        .build();

    public static final EditorLocale ARENA_PAYMENT_REQUIREMENT = builder(PREFIX_OLD + "ARENA_CHANGE_REQUIREMENT_PAYMENT")
        .name("Payment Requirement")
        .text("Makes players to pay", "in order to join the arena.").breakLine()
        .currentHeader().text(Placeholders.ARENA_REQUIREMENT_PAYMENT).breakLine()
        .actionsHeader().action("Left-Click", "Add/Change").action("Right-Click", "Clear/Disable")
        .build();

    public static final EditorLocale ARENA_LEVEL_REQUIREMENT = builder(PREFIX_OLD + "ARENA_CHANGE_REQUIREMENT_LEVEL")
        .name("Level Requirement(s)")
        .text("Sets level requirements to join the arena.").breakLine()
        .currentHeader().text(Placeholders.ARENA_REQUIREMENT_LEVEL).breakLine()
        .noteHeader().notes("Supported plugins:", "&f" + HookId.MMOCORE).breakLine()
        .actionsHeader().action("Left-Click", "Add/Change").action("Right-Click", "Clear/Disable")
        .build();


    public static final EditorLocale REGION_OBJECT = builder(PREFIX_OLD + "REGION_OBJECT")
        .name(Placeholders.REGION_NAME + GRAY + " (&f" + Placeholders.REGION_ID + GRAY + ")")
        .text("Enabled: &f" + Placeholders.REGION_ACTIVE, "Is Default: &f" + Placeholders.REGION_DEFAULT).breakLine()
        .textRaw(Placeholders.GENERIC_PROBLEMS).breakLine()
        .actionsHeader().action("Left-Click", "Edit").action("Shift-Right", "Delete " + RED + "(No Undo)")
        .build();

    public static final EditorLocale REGION_CREATE = builder(PREFIX_OLD + "REGION_CREATE")
        .name("Create Region")
        .build();

    public static final EditorLocale REGION_SETUP_KIT = builder(PREFIX_OLD + "REGION_SETUP_KIT")
        .name("Setup Kit")
        .text("A set of tools to setup", "region playground.").breakLine()
        .actionsHeader().action("Left-Click", "Get")
        .build();

    public static final EditorLocale REGION_ACTIVE = builder(PREFIX_OLD + "REGION_CHANGE_ACTIVE")
        .name("Is Active")
        .text("Enable/Disable region.").breakLine()
        .currentHeader().current("Enabled", Placeholders.REGION_ACTIVE).breakLine()
        .textRaw(Placeholders.GENERIC_PROBLEMS).breakLine()
        .actionsHeader().action("Left-Click", "Toggle")
        .build();

    public static final EditorLocale REGION_DEFAULT = builder(PREFIX_OLD + "REGION_CHANGE_DEFAULT")
        .name("Is Default")
        .text("Sets whether or not this region", "is default arena region.", "", "Default region is the first place,", "where players will be spawned.").breakLine()
        .currentHeader().current("Default", Placeholders.REGION_DEFAULT).breakLine()
        .actionsHeader().action("Left-Click", "Toggle")
        .build();

    public static final EditorLocale REGION_NAME = builder(PREFIX_OLD + "REGION_CHANGE_NAME")
        .name("Display Name")
        .text("Sets region display name.", "It's used in messages, GUIs and holograms.").breakLine()
        .actionsHeader().action("Left-Click", "Change")
        .build();

    public static final EditorLocale GAMEPLAY_TIMELEFT = builder(PREFIX + "Arena.Gameplay.Timeleft")
        .name("Timeleft")
        .text("Amount of time players have to", "finish the arena until it", "auto-ends as lose.").breakLine()
        .currentHeader().current("Timeleft", Placeholders.GAMEPLAY_TIMELEFT + " min.").breakLine()
        .actionsHeader().action("Left-Click", "Edit").action("[Q/Drop] Key", "Disable")
        .build();

    public static final EditorLocale GAMEPLAY_LOBBY_COUNTDOWN = builder(PREFIX_OLD + "GAMEPLAY_CHANGE_LOBBY_TIME")
        .name("Lobby Countdown")
        .text("Amount of time players have to", "prepare before arena game starts.").breakLine()
        .currentHeader().current("Countdown", Placeholders.GAMEPLAY_LOBBY_TIME + " sec.").breakLine()
        .actionsHeader().action("Left-Click", "Change")
        .build();

    public static final EditorLocale GAMEPLAY_ANNOUNCEMENTS = builder(PREFIX_OLD + "GAMEPLAY_CHANGE_ANNOUNCES")
        .name("Start/End Announcements")
        .text("Enables server-wide announcements when", "arena is about to start/end.").breakLine()
        .currentHeader().current("Enabled", Placeholders.GAMEPLAY_ANNOUNCEMENTS).breakLine()
        .actionsHeader().action("Left-Click", "Toggle")
        .build();

    public static final EditorLocale GAMEPLAY_SCOREBOARD = builder(PREFIX_OLD + "GAMEPLAY_CHANGE_SCOREBOARD")
        .name("Scoreboard")
        .text("Sets whether or not", "scoreboard is enabled.", "", "You can create multiple scoreboards", "in the main config.").breakLine()
        .currentHeader().current("Enabled", Placeholders.GAMEPLAY_SCOREBOARD_ENABLED)
        .current("Scoreboard", Placeholders.GAMEPLAY_SCOREBOARD_ID).breakLine()
        .warningHeader().warning(HookId.PROTOCOL_LIB + " required!").breakLine()
        .actionsHeader().action("Left-Click", "Toggle").action("Right-Click", "Change Scorebaord")
        .build();

    public static final EditorLocale GAMEPLAY_HUNGER = builder(PREFIX_OLD + "GAMEPLAY_CHANGE_HUNGER")
        .name("Natural Hunger")
        .text("Sets whether or not", "natural hunger is enabled.").breakLine()
        .currentHeader().current("Enabled", Placeholders.GAMEPLAY_HUNGER_ENABLED).breakLine()
        .actionsHeader().action("Left-Click", "Toggle")
        .build();

    public static final EditorLocale GAMEPLAY_REGENERATION = builder(PREFIX_OLD + "GAMEPLAY_CHANGE_REGENERATION")
        .name("Natural Regeneration")
        .text("Sets whether or not", "natural regeneration is enabled").breakLine()
        .currentHeader().current("Enabled", Placeholders.GAMEPLAY_REGENERATION_ENABLED).breakLine()
        .actionsHeader().action("Left-Click", "Toggle")
        .build();

    @Deprecated
    public static final EditorLocale GAMEPLAY_ITEM_SETTINGS = builder(PREFIX_OLD + "GAMEPLAY_CHANGE_ITEM")
        .name("Item Settings")
        .text("Sets whether or not:", "- Item pickup is enabled", "- Item drop is enabled", "- Item durability is enabled.").breakLine()
        .currentHeader().current("Pickup Enabled", Placeholders.GAMEPLAY_ITEM_PICKUP_ENABLED)
        .current("Drop Enabled", Placeholders.GAMEPLAY_ITEM_DROP_ENABLED)
        .current("Durability Enabled", Placeholders.GAMEPLAY_ITEM_DURABULITY_ENABLED).breakLine()
        .actionsHeader().action("Left-Click", "Toggle Drop").action("Right-Click", "Toggle Pickup")
        .action("Shift-Left", "Toggle Durability")
        .build();

    public static final EditorLocale GAMEPLAY_MOB_HIGHLIGHT = builder(PREFIX_OLD + "GAMEPLAY_CHANGE_MOB_HIGHLIGHT")
        .name("Mob Highlighting")
        .text("Sets whether or not", "mob highlighting is enabled when", "amount (%) of mobs is less or equal", "to specified value.").breakLine()
        .currentHeader().current("Enabled", Placeholders.GAMEPLAY_MOB_HIGHLIGHT_ENABLED)
        .current("Amount", "<= " + Placeholders.GAMEPLAY_MOB_HIGHLIGHT_AMOUNT + "%")
        .current("Color", Placeholders.GAMEPLAY_MOB_HIGHLIGHT_COLOR).breakLine()
        .noteHeader().text("Due to game limitations,", "set of colors is limited and", "can not be really customized.").breakLine()
        .actionsHeader().action("Left-Click", "Toggle").action("Right-Click", "Change Amount")
        .action("Shift-Left", "Toggle Color")
        .build();

    public static final EditorLocale GAMEPLAY_MOB_LOOT = builder(PREFIX_OLD + "GAMEPLAY_CHANGE_MOB_DROP")
        .name("Mob Loot")
        .text("Sets whether or not", "mobs will drop items & xp.").breakLine()
        .currentHeader().current("Drop Items", Placeholders.GAMEPLAY_MOB_DROP_ITEMS)
        .current("Drop XP", Placeholders.GAMEPLAY_MOB_DROP_EXP).breakLine()
        .actionsHeader().action("Left-Click", "Toggle Items").action("Right-Click", "Toggle XP")
        .build();

    public static final EditorLocale GAMEPLAY_BANNED_ITEMS = builder(PREFIX + "Arena.Gameplay.BannedItems")
        .name("Banned Items")
        .text("A list of items that can not", "be used on the arena.").breakLine()
        .currentHeader().text(Placeholders.GAMEPLAY_BANNED_ITEMS).breakLine()
        .noteHeader().text("This option is only useful", "when kits are disabled.").breakLine()
        .actionsHeader().action("Left-Click", "Add Item").action("[Q/Drop] Key", "Clear List")
        .build();

    public static final EditorLocale GAMEPLAY_ALLOWED_SPAWN_REASONS = builder(PREFIX + "Arena.Gameplay.SpawnReasons")
        .name("Allowed Spawn Reasons")
        .text("A list of spawn reasons that are", "allowed to spawn mobs inside arena.", "")
        .text("This might be useful for", "integration with other plugins.").breakLine()
        .currentHeader().text(Placeholders.GAMEPLAY_ALLOWED_SPAWN_REASONS).breakLine()
        .actionsHeader().action("Left-Click", "Add Reason").action("[Q/Drop] Key", "Clear List")
        .build();

    public static final EditorLocale GAMEPLAY_PLAYER_AMOUNT = builder(PREFIX_OLD + "GAMEPLAY_CHANGE_PLAYERS_AMOUNT")
        .name("Player Amount")
        .text("Sets how many players can", "join arena at the same time.", "And how many players required", "for the game start.").breakLine()
        .currentHeader().current("Min", Placeholders.GAMEPLAY_PLAYER_AMOUNT_MIN)
        .current("Max", Placeholders.GAMEPLAY_PLAYER_AMOUNT_MAX).breakLine()
        .actionsHeader().action("Left-Click", "Change Min").action("Right-Click", "Change Max")
        .build();

    public static final EditorLocale GAMEPLAY_PLAYER_LIFES = builder("Editor.Arena.Gameplay.PlayerLifes")
        .name("Player Lifes")
        .text("Sets how many lifes players", "will have on game start.", "And how many time player")
        .text("will have to wait until", "being auto-revived.").breakLine()
        .currentHeader().current("Lifes", Placeholders.GAMEPLAY_PLAYER_LIFES_AMOUNT)
        .current("Auto-Revive", Placeholders.GAMEPLAY_PLAYER_REVIVE_TIME).breakLine()
        .noteHeader()
        .notes("Set " + ORANGE + "Lifes" + GRAY + " to " + ORANGE + "1" + GRAY + " to disable.")
        .notes("Set " + ORANGE + "Auto-Revive" + GRAY + " to " + ORANGE + "-1" + GRAY + " to revive on round end.")
        .actionsHeader().action("Left-Click", "Change Lifes").action("Right-Click", "Change Time")
        .build();

    public static final EditorLocale GAMEPLAY_KEEP_INVENTORY = builder("Editor.Arena.Gameplay.KeepInventory")
        .name("Keep Inventory")
        .text("Sets whether or not", "players will drop items", "on death.").breakLine()
        .currentHeader().current("Enabled", Placeholders.GAMEPLAY_KEEP_INVENTORY).breakLine()
        .actionsHeader().action("Left-Click", "Toggle")
        .build();

    public static final EditorLocale GAMEPLAY_SPECTATE = builder(PREFIX_OLD + "GAMEPLAY_CHANGE_SPECTATE")
        .name("Spectate Allowed")
        .text("Sets whether or not", "non-arena players can spectate", "this arena.").breakLine()
        .currentHeader().current("Enabled", Placeholders.GAMEPLAY_SPECTATE_ENABLED).breakLine()
        .actionsHeader().action("Left-Click", "Toggle")
        .build();

    public static final EditorLocale GAMEPLAY_COMMANDS = builder(PREFIX + "Arena.Gameplay.Commands")
        .name("Commands Allowed")
        .text("Sets whether or not", "non-arena commands are allowed.").breakLine()
        .currentHeader().current("Enabled", Placeholders.GAMEPLAY_COMMAND_USAGE_ENABLED)
        .current("Whitelist", "").text(Placeholders.GAMEPLAY_COMMAND_USAGE_WHITELIST).breakLine()
        .actionsHeader().action("Left-Click", "Toggle").action("Right-Click", "Add Whitelisted")
        .action("[Q/Drop] Key", "Clear List")
        .build();

    public static final EditorLocale GAMEPLAY_KITS = builder(PREFIX + "Arena.Gameplay.Kits")
        .name("Kits Allowed")
        .text("Sets whether or not", "kits are allowed to use.").breakLine()
        .currentHeader().current("Enabled", Placeholders.GAMEPLAY_KITS_ENABLED)
        .current("Allowed", "").text(Placeholders.GAMEPLAY_KITS_ALLOWED).breakLine()
        .actionsHeader().action("Left-Click", "Toggle").action("Right-Click", "Add Allowed")
        .action("[Q/Drop] Key", "Clear List")
        .build();

    public static final EditorLocale GAMEPLAY_KIT_LIMITS = builder(PREFIX_OLD + "GAMEPLAY_CHANGE_KITS_LIMITS")
        .name("Kits Limits")
        .text("Sets how many players", "can use certain kit(s)", "at the same time.").breakLine()
        .currentHeader().text(Placeholders.GAMEPLAY_KITS_LIMITS).breakLine()
        .actionsHeader().action("Left-Click", "Add Limit").action("[Q/Drop] Key", "Clear List")
        .build();

    public static final EditorLocale GAMEPLAY_PETS = builder(PREFIX_OLD + "GAMEPLAY_CHANGE_PETS_ALLOWED")
        .name("Pets Allowed")
        .text("Sets whether or not", "player pets are allowed.").breakLine()
        .currentHeader().current("Enabled", Placeholders.GAMEPLAY_PETS_ALLOWED).breakLine()
        .actionsHeader().action("Left-Click", "Toggle")
        .build();

    public static final EditorLocale GAMEPLAY_MCMMO = builder(PREFIX_OLD + "GAMEPLAY_CHANGE_MCMMO_ALLOWED")
        .name("mcMMO Allowed")
        .text("Sets whether or not", "players can use mcMMO abilities.").breakLine()
        .currentHeader().current("Enabled", Placeholders.GAMEPLAY_MCMMO_ALLOWED).breakLine()
        .actionsHeader().action("Left-Click", "Toggle")
        .build();

    public static final EditorLocale WAVES_ROUND_INTERVAL = builder(PREFIX_OLD + "WAVES_CHANGE_DELAY")
        .name("Round Interval")
        .text("Sets the countdown before new round.", "")
        .text("Countdown will start only when", "all mobs from current round are killed.").breakLine()
        .currentHeader().current("First Round", Placeholders.ARENA_WAVES_FIRST_ROUND_COUNTDOWN + " sec.")
        .current("All Other", Placeholders.ARENA_WAVES_ROUND_COUNTDOWN + " sec.").breakLine()
        .actionsHeader().action("Left-Click", "Change First").action("Right-Click", "Change Other")
        .build();

    public static final EditorLocale WAVES_FINAL_ROUND = builder(PREFIX_OLD + "WAVES_CHANGE_FINAL_WAVE")
        .name("Final Round")
        .text("Sets which round will be final.", "")
        .text("When final round is completed,", "game ends as victory.", "")
        .text("If final round is disabled,", "game will continue until", "all players are dead.").breakLine()
        .currentHeader().current("Final Round", Placeholders.ARENA_WAVES_FINAL_ROUND).breakLine()
        .actionsHeader().action("Left-Click", "Change")
        .build();

    public static final EditorLocale WAVES_WAVES = builder(PREFIX_OLD + "WAVES_CHANGE_WAVES")
        .name("Waves")
        .text("Here you predefine waves", "aka mob groups that will be", "spawned during the game by", "arena scripts").breakLine()
        .actionsHeader().action("Left-Click", "Navigate")
        .build();

    public static final EditorLocale WAVES_GRADUAL = builder(PREFIX_OLD + "WAVES_CHANGE_GRADUAL")
        .name("Gradual Spawning")
        .text("This features allows you to configure", "mobs spawn that way so they will", "spawn by small groups")
        .text("during the round instead of", "spawn all together at round start.").breakLine()
        .currentHeader().current("Enabled", Placeholders.ARENA_WAVES_GRADUAL_ENABLED).breakLine()
        .actionsHeader().action("Left-Click", "Navigate")
        .build();

    public static final EditorLocale WAVES_GRADUAL_ENABLED = builder(PREFIX_OLD + "WAVES_CHANGE_GRADUAL_ENABLED")
        .name("Enabled")
        .text("Sets whether or not", "gradual spawning feature is enabled.").breakLine()
        .currentHeader().current("Enabled", Placeholders.ARENA_WAVES_GRADUAL_ENABLED).breakLine()
        .actionsHeader().action("Left-Click", "Toggle")
        .build();

    public static final EditorLocale WAVES_GRADUAL_FIRST_PERCENT = builder(PREFIX_OLD + "WAVES_CHANGE_GRADUAL_FIRST_PERCENT")
        .name("First Group Amount (%)")
        .text("Sets how many mobs (%) of total", "amount will be spawned on", "round start.").breakLine()
        .currentHeader().current("Amount", Placeholders.ARENA_WAVES_GRADUAL_FIRST_PERCENT + "%").breakLine()
        .actionsHeader().action("Left-Click", "Change")
        .build();

    public static final EditorLocale WAVES_GRADUAL_NEXT_PERCENT = builder(PREFIX_OLD + "WAVES_CHANGE_GRADUAL_NEXT_PERCENT")
        .name("Next Group Amount (%)")
        .text("Sets how many mobs (%) of total", "amount will be spawned during", "the round.").breakLine()
        .currentHeader().current("Amount", Placeholders.ARENA_WAVES_GRADUAL_NEXT_PERCENT + "%").breakLine()
        .warningHeader().warning("Can not exceed " + RED + "First Group Amount").breakLine()
        .actionsHeader().action("Left-Click", "Change")
        .build();

    public static final EditorLocale WAVES_GRADUAL_NEXT_INTERVAL = builder(PREFIX_OLD + "WAVES_CHANGE_GRADUAL_NEXT_INTERVAL")
        .name("Next Group Interval")
        .text("Sets how often (in seconds)", "arena will attempt to spawn another", "group of mobs.").breakLine()
        .currentHeader().current("Interval", Placeholders.ARENA_WAVES_GRADUAL_NEXT_INTERVAL + " sec.").breakLine()
        .actionsHeader().action("Left-Click", "Change")
        .build();

    public static final EditorLocale WAVES_GRADUAL_NEXT_KILL_PERCENT = builder(PREFIX_OLD + "WAVES_CHANGE_GRADUAL_NEXT_KILL_PERCENT")
        .name("Next Group for Killed Amount")
        .text("Sets how many mobs (%) of total", "amount must be killed to prepare", "new mob group to spawn.", "")
        .text("Set this to 0 to spawn mobs", "using the " + YELLOW + "Next Group Interval" + GRAY + " only.").breakLine()
        .currentHeader().current("Amount", Placeholders.ARENA_WAVES_GRADUAL_NEXT_KILL_PERCENT + "%").breakLine()
        .warningHeader().warning("Can not exceed " + RED + "Next Group Amount").breakLine()
        .actionsHeader().action("Left-Click", "Change")
        .breakLine()
        .build();

    public static final EditorLocale WAVES_WAVE_OBJECT = builder(PREFIX_OLD + "WAVES_WAVE_OBJECT")
        .name("Wave: " + BLUE + Placeholders.ARENA_WAVE_ID)
        .text("Mobs:").text(Placeholders.ARENA_WAVE_MOBS).breakLine()
        .actionsHeader().action("Left-Click", "Edit").action("Shift-Right", "Delete" + RED + " (No Undo)")
        .build();

    public static final EditorLocale WAVES_WAVE_CREATE = builder(PREFIX_OLD + "WAVES_WAVE_CREATE")
        .name("Create Wave")
        .build();

    public static final EditorLocale WAVES_WAVE_MOB_OBJECT = builder(PREFIX_OLD + "WAVES_WAVE_MOB_OBJECT")
        .name("Mob: " + BLUE + Placeholders.ARENA_WAVE_MOB_ID)
        .current("Provider", Placeholders.ARENA_WAVE_MOB_PROVIDER)
        .current("Start Amount", Placeholders.ARENA_WAVE_MOB_AMOUNT)
        .current("Start Level", Placeholders.ARENA_WAVE_MOB_LEVEL)
        .current("Spawn Chance", Placeholders.ARENA_WAVE_MOB_CHANCE).breakLine()
        .noteHeader()
        .notes("Level & amount can be adjusted with scripts.")
        .actionsHeader()
        .action("Left-Click", "Change Mob Type")
        .action("Right-Click", "Change Amount")
        .action("[Q/Drop] Key", "Change Chance")
        .action("[F/Swap] Key", "Change Provider")
        .action("Shift-Right", "Delete" + RED + " (No Undo)")
        .build();

    public static final EditorLocale WAVES_WAVE_MOB_CREATE = builder(PREFIX_OLD + "WAVES_WAVE_MOB_CREATE")
        .name("Add Mob")
        .text("Adds an empty mob configuration.")
        .build();

    public static final EditorLocale SUPPLY_CHEST_CREATE = builder(PREFIX_OLD + "SUPPLY_CHEST_CREATE")
        .name("Create Supply Chest")
        .build();

    public static final EditorLocale SUPPLY_CHEST_OBJECT = builder(PREFIX_OLD + "SUPPLY_CHEST_OBJECT")
        .name("Supply Chest &7(ID: &f" + Placeholders.SUPPLY_CHEST_ID + "&7)")
        .current("Location", Placeholders.SUPPLY_CHEST_LOCATION_X + ", " + Placeholders.SUPPLY_CHEST_LOCATION_Y + ", " + Placeholders.SUPPLY_CHEST_LOCATION_Z + GRAY + " in " + YELLOW + Placeholders.SUPPLY_CHEST_LOCATION_WORLD).breakLine()
        .actionsHeader().action("Left-Click", "Edit").action("Right-Click", "Teleport")
        .action("Shift-Right", "Delete " + RED + "(No Undo)")
        .build();

    public static final EditorLocale SUPPLY_CHEST_ITEMS = builder(PREFIX_OLD + "SUPPLY_CHEST_CHANGE_ITEMS")
        .name("Content")
        .text("Here you can add items that", "will be used to (re)fill this chest.", "")
        .text("Put items in opened menu and close it", "to return back here.").breakLine()
        .actionsHeader().action("Left-Click", "Open")
        .build();

    public static final EditorLocale SUPPLY_CHEST_REFILL_AMOUNT = builder(PREFIX_OLD + "SUPPLY_CHEST_CHANGE_REFILL_AMOUNT")
        .name("Item Refill Amount")
        .text("Sets how many items will be", "added to the chest on refill.").breakLine()
        .currentHeader()
        .current("Min", Placeholders.SUPPLY_CHEST_REFILL_ITEMS_MIN)
        .current("Max", Placeholders.SUPPLE_CHEST_REFILL_ITEMS_MAX).breakLine()
        .noteHeader().text("On each refill previous items", "are removed!").breakLine()
        .actionsHeader().action("Left-Click", "Change Min").action("Right-Click", "Change Max")
        .build();

    public static final EditorLocale SUPPLY_CHEST_LOCATION = builder( PREFIX_OLD + "SUPPLY_CHEST_CHANGE_LOCATION")
        .name("Location")
        .text("Sets the supply chest location.").breakLine()
        .currentHeader().current("Location", Placeholders.SUPPLY_CHEST_LOCATION_X + ", " + Placeholders.SUPPLY_CHEST_LOCATION_Y + ", " + Placeholders.SUPPLY_CHEST_LOCATION_Z + " in " + Placeholders.SUPPLY_CHEST_LOCATION_WORLD).breakLine()
        .noteHeader().text("Left click a chest/container to", "assign it with supply chest.").breakLine()
        .actionsHeader().action("Left-Click", "Change")
        .build();

    public static final EditorLocale REWARDS_RETAIN = builder(PREFIX_OLD + "REWARDS_CHANGE_RETAIN")
        .name("Retain Settings")
        .text("Sets whether or not rewards obtained during", "the game will be saved when player", "dies or leaves the arena before", "final round.").breakLine()
        .currentHeader()
        .current("On Death", Placeholders.REWARD_MANAGER_RETAIN_ON_DEATH)
        .current("On Leave", Placeholders.REWARD_MANAGER_RETAIN_ON_LEAVE).breakLine()
        .noteHeader().text("This setting will have effect on 'Late' rewards only.").breakLine()
        .actionsHeader().action("Left-Click", "Toggle Death").action("Right-Click", "Toggle Leave")
        .build();

    public static final EditorLocale REWARD_OBJECT = builder(PREFIX_OLD + "REWARD_OBJECT")
        .name(Placeholders.REWARD_NAME + GRAY + " (ID: &f" + Placeholders.REWARD_ID + GRAY + ")")
        .actionsHeader().action("Left-Click", "Edit").action("Shift-Right", "Delete " + RED + "(No Undo)")
        .build();

    public static final EditorLocale REWARD_CREATE = builder(PREFIX_OLD + "REWARD_CREATE")
        .name("Create Reward")
        .build();

    public static final EditorLocale REWARD_NAME = builder(PREFIX_OLD + "REWARD_CHANGE_NAME")
        .name("Display Name")
        .text("Name used in messages, GUIs, holograms.").breakLine()
        .currentHeader().current("Name", Placeholders.REWARD_NAME).breakLine()
        .actionsHeader().action("Left-Click", "Change")
        .build();

    public static final EditorLocale REWARD_LATE = builder(PREFIX_OLD + "REWARD_CHANGE_LATE")
        .name("Is Late")
        .text("Sets whether or not", "reward will be given on game end", "or immediately.").breakLine()
        .currentHeader().current("Enabled", Placeholders.REWARD_IS_LATE).breakLine()
        .actionsHeader().action("Left-Click", "Toggle")
        .build();

    public static final EditorLocale REWARD_COMMANDS = builder(PREFIX_OLD + "REWARD_CHANGE_COMMANDS")
        .name("Commands")
        .text("Commands to execute when player", "obtains this reward.").breakLine()
        .currentHeader().text(Placeholders.REWARD_COMMANDS).breakLine()
        .actionsHeader().action("Left-Click", "Add Command").action("Right-Click", "Clear List")
        .build();

    public static final EditorLocale REWARD_ITEMS = builder(PREFIX_OLD + "REWARD_CHANGE_ITEMS")
        .name("Items")
        .text("Items to give when player", "obtains this reward.").breakLine()
        .actionsHeader().action("Left-Click", "Open")
        .build();

    public static final EditorLocale SHOP_CATEGORIES = builder(PREFIX_OLD + "SHOP_OPEN_CATEGORIES")
        .name("Categories")
        .text("Here you can create and", "manage shop categories").breakLine()
        .actionsHeader().action("Left-Click", "Navigate")
        .build();
    
    public static final EditorLocale SHOP_ACTIVE = builder(PREFIX_OLD + "SHOP_CHANGE_ACTIVE")
        .name("Active")
        .text("Sets whether or not", "shop is enabled for arena.").breakLine()
        .currentHeader().current("Enabled", Placeholders.SHOP_MANAGER_IS_ACTIVE).breakLine()
        .actionsHeader().action("Left-Click", "Toggle")
        .build();
    
    public static final EditorLocale SHOP_HIDE_OTHER_KIT_ITEMS = builder(PREFIX_OLD + "SHOP_CHANGE_HIDE_OTHER_KIT_ITEMS")
        .name("Hide Other Kit Items")
        .text("Sets whether or not", "items requires certain kit(s)", "will be hidden for players", "without that kits.").breakLine()
        .currentHeader().current("Enabled", Placeholders.SHOP_MANAGER_HIDE_OTHER_KIT_ITEMS).breakLine()
        .actionsHeader().action("Left-Click", "Toggle")
        .build();

    public static final EditorLocale SHOP_CATEGORY_OBJECT = builder(PREFIX_OLD + "SHOP_CATEGORY_OBJECT")
        .name(Placeholders.SHOP_CATEGORY_NAME + GRAY + " (ID: &f" + Placeholders.SHOP_CATEGORY_ID + GRAY + ")")
        .actionsHeader()
        .action("Left-Click", "Change")
        .action("Shift-Right", "Delete " + RED + "(No Undo)")
        .build();

    public static final EditorLocale SHOP_CATEGORY_CREATE = builder(PREFIX_OLD + "SHOP_CATEGORY_CREATE")
        .name("Create Category")
        .build();

    public static final EditorLocale SHOP_CATEGORY_NAME = builder(PREFIX_OLD + "SHOP_CATEGORY_CHANGE_NAME")
        .name("Display Name")
        .text("Sets category display name.", "It's used in messages, GUIs, holograms, etc.").breakLine()
        .currentHeader().current("Name", Placeholders.SHOP_CATEGORY_NAME).breakLine()
        .actionsHeader().action("Left-Click", "Change").build();

    public static final EditorLocale SHOP_CATEGORY_DESCRIPTION = builder(PREFIX_OLD + "SHOP_CATEGORY_CHANGE_DESCRIPTION")
        .name("Category Description")
        .text("Sets category description.", "It's used in GUIs, holograms, etc.").breakLine()
        .currentHeader().text(Placeholders.SHOP_CATEGORY_DESCRIPTION).breakLine()
        .actionsHeader().action("Left-Click", "Add Line").action("Right-Click", "Clear")
        .build();

    public static final EditorLocale SHOP_CATEGORY_ICON = builder(PREFIX_OLD + "SHOP_CATEGORY_CHANGE_ICON")
        .name("Icon")
        .text("Sets icon for the category", "to be displayed in shop GUIs.").breakLine()
        .actionsHeader().action("Drag & Drop", "Replace").build();

    public static final EditorLocale SHOP_CATEGORY_ALLOWED_KITS = builder(PREFIX_OLD + "SHOP_CATEGORY_CHANGE_ALLOWED_KITS")
        .name("Allowed Kits")
        .text("A list of kits which can", "access the category.").breakLine()
        .currentHeader().text(Placeholders.SHOP_CATEGORY_ALLOWED_KITS).breakLine()
        .noteHeader().text("If no kits are set, then anyone can access the category.").breakLine()
        .actionsHeader().action("Left-Click", "Add Kit").action("Right-Click", "Clear List")
        .build();

    public static final EditorLocale SHOP_CATEGORY_PRODUCTS = builder(PREFIX_OLD + "SHOP_CATEGORY_CHANGE_PRODUCTS")
        .name("Products")
        .text("Here you can create and configure", "category products.").breakLine()
        .actionsHeader().action("Left-Click", "Navigate").build();

    public static final EditorLocale SHOP_PRODUCT_OBJECT = builder(PREFIX_OLD + "SHOP_PRODUCT_OBJECT")
        .name(Placeholders.SHOP_PRODUCT_NAME + GRAY + " (ID: &f" + Placeholders.SHOP_PRODUCT_ID + GRAY + ")")
        .current("Price", Placeholders.SHOP_PRODUCT_PRICE).breakLine()
        .actionsHeader().action("Left-Click", "Edit").action("Shift-Right", "Delete " + RED + "(No Undo)")
        .build();

    public static final EditorLocale SHOP_PRODUCT_CREATE = builder(PREFIX_OLD + "SHOP_PRODUCT_CREATE")
        .name("Create Product")
        .build();

    public static final EditorLocale SHOP_PRODUCT_NAME = builder(PREFIX_OLD + "SHOP_PRODUCT_CHANGE_NAME")
        .name("Display Name")
        .text("Sets product display name.", "It's used in messages, GUIs, holograms, etc.").breakLine()
        .currentHeader().current("Name", Placeholders.SHOP_PRODUCT_NAME).breakLine()
        .actionsHeader().action("Left-Click", "Change")
        .build();

    public static final EditorLocale SHOP_PRODUCT_DESCRIPTION = builder(PREFIX_OLD + "SHOP_PRODUCT_CHANGE_DESCRIPTION")
        .name("Description")
        .text("Sets product description.", "It's used in GUIs, holograms, etc.").breakLine()
        .currentHeader().text(Placeholders.SHOP_PRODUCT_DESCRIPTION).breakLine()
        .actionsHeader().action("Left-Click", "Add Line").action("Right-Click", "Clear")
        .build();

    public static final EditorLocale SHOP_PRODUCT_CURRENCY = builder(PREFIX_OLD + "SHOP_PRODUCT_CHANGE_CURRENCY")
        .name("Currency")
        .text("Sets product currency.").breakLine()
        .currentHeader().current("Currency", Placeholders.SHOP_PRODUCT_CURRENCY).breakLine()
        .actionsHeader().action("Left-Click", "Change")
        .build();

    public static final EditorLocale SHOP_PRODUCT_PRICE = builder(PREFIX_OLD + "SHOP_PRODUCT_CHANGE_PRICE")
        .name("Price")
        .text("Sets product price.").breakLine()
        .currentHeader().current("Price", Placeholders.SHOP_PRODUCT_PRICE).breakLine()
        .actionsHeader().action("Left-Click", "Change")
        .build();

    public static final EditorLocale SHOP_PRODUCT_ICON = builder(PREFIX_OLD + "SHOP_PRODUCT_CHANGE_ICON")
        .name("Icon")
        .text("Sets product icon.", "It's displayed in GUIs.").breakLine()
        .actionsHeader().action("Drag & Drop", "Replace")
        .build();

    public static final EditorLocale SHOP_PRODUCT_REQUIRED_KITS = builder(PREFIX_OLD + "SHOP_PRODUCT_CHANGE_REQUIRED_KITS")
        .name("Allowed Kits")
        .text("A list of kits which can", "access this product.").breakLine()
        .currentHeader().text(Placeholders.SHOP_PRODUCT_ALLOWED_KITS).breakLine()
        .noteHeader().text("If no kits are set, then anyone can access the product.").breakLine()
        .actionsHeader().action("Left-Click", "Add Kit").action("Right-Click", "Clear List")
        .build();

    public static final EditorLocale SHOP_PRODUCT_ITEMS = builder(PREFIX_OLD + "SHOP_PRODUCT_CHANGE_ITEMS")
        .name("Items")
        .text("Items to give when", "player purchases this product.").breakLine()
        .actionsHeader().action("Left-Click", "Navigate")
        .build();

    public static final EditorLocale SHOP_PRODUCT_COMMANDS = builder(PREFIX_OLD + "SHOP_PRODUCT_CHANGE_COMMANDS")
        .name("Commands")
        .text("Commands to execute when", "player purchases this product.").breakLine()
        .currentHeader().text(Placeholders.SHOP_PRODUCT_COMMANDS).breakLine()
        .actionsHeader().action("Left-Click", "Add Command").action("Shift-Right", "Clear List")
        .build();

    public static final EditorLocale SPOT_OBJECT = builder(PREFIX_OLD + "SPOT_OBJECT")
        .name(Placeholders.SPOT_NAME + GRAY + " (ID: &f" + Placeholders.SPOT_ID + GRAY + ")")
        .current("Active", Placeholders.SPOT_ACTIVE).breakLine()
        .textRaw(Placeholders.GENERIC_PROBLEMS).breakLine()
        .actionsHeader().action("Left-Click", "Edit").action("Shift-Right", "Delete " + RED + "(No Undo)")
        .build();

    public static final EditorLocale SPOT_CREATE = builder(PREFIX_OLD + "SPOT_CREATE")
        .name("Create Spot")
        .build();

    public static final EditorLocale SPOT_SETUP_KIT = builder(PREFIX_OLD + "SPOT_SETUP_KIT")
        .name("Setup Kit")
        .text("A set of tools to setup a spot.").breakLine()
        .actionsHeader().action("Left-Click", "Get")
        .build();

    public static final EditorLocale SPOT_ACTIVE = builder(PREFIX_OLD + "SPOT_CHANGE_ACTIVE")
        .name("Active")
        .text("Sets whether or not", "spot is enabled and active.").breakLine()
        .currentHeader().current("Enabled", Placeholders.SPOT_ACTIVE).breakLine()
        .textRaw(Placeholders.GENERIC_PROBLEMS).breakLine()
        .actionsHeader().action("Left-Click", "Toggle")
        .build();

    public static final EditorLocale SPOT_NAME = builder(PREFIX_OLD + "SPOT_CHANGE_NAME")
        .name("Display Name")
        .text("Sets spot display name.", "It's used in messages, GUIs, etc.").breakLine()
        .currentHeader().current("Name", Placeholders.SPOT_NAME).breakLine()
        .actionsHeader().action("Left-Click", "Change")
        .build();

    public static final EditorLocale SPOT_STATES = builder(PREFIX_OLD + "SPOT_CHANGE_STATES")
        .name("States")
        .text("Here you can create and setup", "different spot states.").breakLine()
        .actionsHeader().action("Left-Click", "Navigate")
        .build();

    public static final EditorLocale SPOT_STATE_OBJECT = builder(PREFIX_OLD + "SPOT_STATE_OBJECT")
        .name("State: " + GREEN + Placeholders.SPOT_STATE_ID)
        .text("Simply build something inside", "the spot area and save it.").breakLine()
        .actionsHeader()
        .action("Left-Click", "Build/Edit")
        .action("Shift-Right", "Delete " + RED + "(No Undo)")
        .build();

    public static final EditorLocale SPOT_STATE_CREATE = builder(PREFIX_OLD + "SPOT_STATE_CREATE")
        .name("Create State")
        .build();

    public static final EditorLocale SCRIPT_CATEGORY_OBJECT = builder(PREFIX_OLD + "SCRIPT_CATEGORY_OBJECT")
        .name("Category: " + GREEN + Placeholders.SCRIPT_CATEGORY_ID)
        .actionsHeader().action("Left-Click", "Edit")
        .action("Shift-Right", "Delete " + RED + "(No Undo)")
        .build();

    public static final EditorLocale SCRIPT_CATEGORY_CREATE = builder(PREFIX_OLD + "SCRIPT_CATEGORY_CREATE")
        .name("Create Category")
        .build();

    public static final EditorLocale SCRIPT_OBJECT = builder(PREFIX_OLD + "SCRIPT_OBJECT")
        .name("Script: &6" + Placeholders.SCRIPT_ID)
        .currentHeader()
        .current("Event", Placeholders.SCRIPT_EVENT_TYPE)
        .current("Conditions", "").text(Placeholders.SCRIPT_CONDITION_SECTION_CONDITIONS)
        .current("Actions", "").text(Placeholders.SCRIPT_ACTION_PARAMS)
        .breakLine()
        .actionsHeader()
        .action("Left-Click", "Edit Actions")
        .action("Right-Click", "Edit Conditions")
        .action("[Q/Drop] Key", "Toggle Event")
        .action("Shift-Right", "Delete " + RED + "(No Undo)")
        .build();

    public static final EditorLocale SCRIPT_CREATE = builder(PREFIX_OLD + "SCRIPT_CREATE")
        .name("Create Script")
        .build();

    public static final EditorLocale SCRIPT_ACTION_OBJECT = builder(PREFIX_OLD + "SCRIPT_ACTION_OBJECT")
        .name("Action: " + GREEN + Placeholders.SCRIPT_ACTION_NAME)
        .currentHeader().text(Placeholders.SCRIPT_ACTION_PARAMS)
        .breakLine()
        .actionsHeader()
        .action("Left-Click", "Add Parameter")
        .action("Right-Click", "Clear Parameters")
        .action("Shift-Right", "Delete " + RED + "(No Undo)")
        .build();

    public static final EditorLocale SCRIPT_ACTION_CREATE = builder(PREFIX_OLD + "SCRIPT_ACTION_CREATE")
        .name("Create Action")
        .build();

    public static final EditorLocale SCRIPT_CONDITION_SECTION_OBJECT = builder(PREFIX_OLD + "SCRIPT_CONDITION_SECTION_OBJECT")
        .name("Conditions Section: " + GREEN + Placeholders.SCRIPT_CONDITION_SECTION_ID)
        .currentHeader().text(Placeholders.SCRIPT_CONDITION_SECTION_CONDITIONS)
        .breakLine()
        .actionsHeader()
        .action("Left-Click", "Add Condition")
        .action("Right-Click", "Clear Conditions")
        .action("Shift-Right", "Delete " + RED + "(No Undo)")
        .build();

    public static final EditorLocale SCRIPT_CONDITION_SECTION_CREATE = builder(PREFIX_OLD + "SCRIPT_CONDITION_SECTION_CREATE")
        .name("Create Conditions Section")
        .build();

    public static final EditorLocale KIT_OBJECT = builder(PREFIX_OLD + "KIT_OBJECT")
        .name(Placeholders.KIT_NAME + GRAY + " (ID: &f" + Placeholders.KIT_ID + GRAY + ")")
        .current("Is Default", Placeholders.KIT_IS_DEFAULT).breakLine().breakLine()
        .actionsHeader()
        .action("Left-Click", "Edit")
        .action("Shift-Right", "Delete " + RED + "(No Undo)")
        .build();

    public static final EditorLocale KIT_CREATE = builder(PREFIX_OLD + "KIT_CREATE")
        .name("Create Kit")
        .build();

    public static final EditorLocale KIT_NAME = builder(PREFIX_OLD + "KIT_CHANGE_NAME")
        .name("Display Name")
        .text("Sets kit display name.", "It's used in messages, GUIs, signs, holograms, etc.").breakLine()
        .currentHeader().current("Name", Placeholders.KIT_NAME).breakLine()
        .actionsHeader().action("Left-Click", "Change")
        .build();

    public static final EditorLocale KIT_DESCRIPTION = builder(PREFIX_OLD + "KIT_CHANGE_DESCRIPTION")
        .name("Description")
        .text("Sets kit description.", "It's used in GUIs, holograms, etc.").breakLine()
        .currentHeader().text(Placeholders.KIT_DESCRIPTION).breakLine()
        .actionsHeader().action("Left-Click", "Add Line").action("Right-Click", "Clear")
        .build();

    public static final EditorLocale KIT_ICON = builder(PREFIX_OLD + "KIT_CHANGE_ICON")
        .name("Icon")
        .text("Represents a kit icon used", "in Kit Selector and Kit Shop GUIs.").breakLine()
        .actionsHeader().action("Drag & Drop", "Replace").action("Right-Click", "Get Copy")
        .build();

    public static final EditorLocale KIT_COMMANDS = builder(PREFIX_OLD + "KIT_CHANGE_COMMANDS")
        .name("Commands")
        .text("Commands to execute on game start", "for players selected this kit.").breakLine()
        .currentHeader().text(Placeholders.KIT_COMMANDS).breakLine()
        .actionsHeader().action("Left-Click", "Add Command").action("Right-Click", "Clear List")
        .build();

    public static final EditorLocale KIT_POTIONS = builder(PREFIX_OLD + "KIT_CHANGE_POTIONS")
        .name("Potion Effects")
        .text("Potion effects, that will be permanently", "applied to a player.").breakLine()
        .currentHeader().text(Placeholders.KIT_POTION_EFFECTS).breakLine()
        .actionsHeader().action("Left-Click", "Add Effect").action("Right-Click", "Clear List")
        .build();

    public static final EditorLocale KIT_ARMOR = builder(PREFIX_OLD + "KIT_CHANGE_ARMOR")
        .name("Armor")
        .text("A set of armors that will be", "equipped to a player.").breakLine()
        .warningHeader().warning("Boots → Legs → Chest → Head → OffHand").breakLine()
        .actionsHeader().action("Left-Click", "Navigate")
        .build();

    public static final EditorLocale KIT_INVENTORY = builder(PREFIX_OLD + "KIT_CHANGE_INVENTORY")
        .name("Inventory")
        .text("A list of items that will be", "added to a player's inventory.").breakLine()
        .actionsHeader().action("Left-Click", "Navigate")
        .build();

    public static final EditorLocale KIT_PERMISSION = builder(PREFIX_OLD + "KIT_CHANGE_PERMISSION")
        .name("Permission Requirement")
        .text("Sets whether or not permission", "is required to use this kit.").breakLine()
        .currentHeader()
        .current("Enabled", Placeholders.KIT_IS_PERMISSION)
        .current("Node", Placeholders.KIT_PERMISSION).breakLine()
        .actionsHeader().action("Left-Click", "Toggle")
        .build();

    public static final EditorLocale KIT_COST = builder(PREFIX_OLD + "KIT_CHANGE_COST")
        .name("Kit Cost")
        .text("Sets kit cost.").breakLine()
        .currentHeader().current("Cost", Placeholders.KIT_COST).breakLine()
        .noteHeader().text("Set this to 0 to make kit", "available without purchase.").breakLine()
        .actionsHeader().action("Left-Click", "Change")
        .build();

    public static final EditorLocale KIT_DEFAULT = builder(PREFIX_OLD + "KIT_CHANGE_DEFAULT")
        .name("Is Default")
        .text("Sets whether or not this kit", "is default kit.").breakLine()
        .currentHeader().current("Is Default", Placeholders.KIT_IS_DEFAULT).breakLine()
        .noteHeader().text("Default kit is used when", "player didn't select kit in lobby.").breakLine()
        .actionsHeader().action("Left-Click", "Toggle")
        .build();

    public static final EditorLocale KIT_CURRENCY = builder(PREFIX_OLD + "KIT_CHANGE_CURRENCY")
        .name("Currency")
        .text("Sets the currency used to", "purchase this kit.").breakLine()
        .currentHeader().current("Currency", Placeholders.KIT_CURRENCY).breakLine()
        .actionsHeader().action("Left-Click", "Change")
        .build();

    public static final EditorLocale MOB_OBJECT = builder(PREFIX_OLD + "MOB_OBJECT")
        .name(Placeholders.MOB_NAME + GRAY + " (&f" + Placeholders.MOB_ID + GRAY + ")")
        .actionsHeader().action("Left-Click", "Edit").action("Right-Click", "Delete " + RED + "(No Undo)")
        .build();

    public static final EditorLocale MOB_CREATE = builder(PREFIX_OLD + "MOB_CREATE")
        .name("Create Mob")
        .build();

    public static final EditorLocale MOB_NAME = builder(PREFIX_OLD + "MOB_CHANGE_NAME")
        .name("Display Name")
        .text("Sets mob display name and", "whether or not this name is", "always visible.").breakLine()
        .currentHeader()
        .current("Name", Placeholders.MOB_NAME)
        .current("Is Visible", Placeholders.MOB_NAME_VISIBLE).breakLine()
        .actionsHeader()
        .action("Left-Click", "Change Name")
        .action("Right-Click", "Toggle Visibility")
        .build();

    public static final EditorLocale MOB_ENTITY_TYPE = builder(PREFIX_OLD + "MOB_CHANGE_ENTITY_TYPE")
        .name("Entity Type")
        .text("Sets mob entity type.").breakLine()
        .currentHeader().current("Type", Placeholders.MOB_ENTITY_TYPE).breakLine()
        .actionsHeader().action("Left-Click", "Change")
        .build();

    public static final EditorLocale MOB_LEVEL = builder(PREFIX_OLD + "MOB_CHANGE_LEVEL")
        .name("Mob Levels")
        .text("Sets mob min. and max. levels.").breakLine()
        .currentHeader()
        .current("Min", Placeholders.MOB_LEVEL_MIN)
        .current("Max", Placeholders.MOB_LEVEL_MAX).breakLine()
        .noteHeader().text("Mob level can be adjusted during", "the game with Arena Scripts.").breakLine()
        .actionsHeader().action("Left-Click", "Edit Min").action("Right-Click", "Edit Max")
        .build();

    public static final EditorLocale MOB_BOSSBAR = builder(PREFIX_OLD + "MOB_CHANGE_BOSSBAR")
        .name("Boss Bar")
        .text("Mob health bossbar settings.").breakLine()
        .currentHeader()
        .current("Enabled", Placeholders.MOB_BOSSBAR_ENABLED)
        //.current("Title", Placeholders.MOB_BOSSBAR_TITLE)
        .current("Color", Placeholders.MOB_BOSSBAR_COLOR)
        .current("Style", Placeholders.MOB_BOSSBAR_STYLE).breakLine()
        .actionsHeader()
        .action("Left-Click", "Toggle")
        //.action("Right-Click", "Edit Title")
        .action("Shift-Left", "Change Color")
        .action("Shift-Right", "Change Style")
        .build();

    public static final EditorLocale MOB_ATTRIBUTES = builder(PREFIX_OLD + "MOB_CHANGE_ATTRIBUTES")
        .name("Mob Attributes")
        .text("Sets mob default and per-level attributes.").breakLine()
        .currentHeader()
        .current("Base", "").text(Placeholders.MOB_ATTRIBUTES_BASE)
        .current("Per Level", "").text(Placeholders.MOB_ATTRIBUTES_LEVEL)
        .breakLine()
        .noteHeader().text("Base attribute with zero value means", "that default mob's attribute value", "won't be changed.")
        .breakLine()
        .actionsHeader()
        .action("Left-Click", "Change Base")
        .action("Right-Click", "Change Per-Level")
        .action("Shift-Left", "Clear All")
        .build();

    public static final EditorLocale MOB_EQUIPMENT = builder(PREFIX_OLD + "MOB_CHANGE_EQUIPMENT")
        .name("Mob Equipment")
        .text("Items to be equipped on a mob.").breakLine()
        .warningHeader().warning("Boots → Legs → Chest → Head → Hand → Off Hand").breakLine()
        .actionsHeader().action("Left-Click", "Navigate")
        .build();

    public static final EditorLocale MOB_STYLES = builder(PREFIX_OLD + "MOB_CHANGE_STYLES")
        .name("Mob Styles")
        .actionsHeader().action("Left-Click", "Navigate")
        .build();

    public static final EditorLocale MOB_STYLE_OBJECT = builder(PREFIX_OLD + "MOB_STYLE_OBJECT")
        .name("Style Type: " + GREEN + Placeholders.MOB_STYLE_TYPE)
        .currentHeader().current("Value", Placeholders.MOB_STYLE_VALUE).breakLine()
        .actionsHeader().action("Left-Click", "Change").action("Right-Click", "Remove")
        .build();
}
