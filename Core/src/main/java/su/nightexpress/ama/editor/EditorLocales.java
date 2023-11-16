package su.nightexpress.ama.editor;

import su.nexmedia.engine.api.editor.EditorLocale;
import su.nexmedia.engine.utils.EngineUtils;
import su.nightexpress.ama.Placeholders;

import static su.nexmedia.engine.utils.Colors2.*;

public class EditorLocales extends su.nexmedia.engine.api.editor.EditorLocales {

    private static final String PREFIX = "Editor.";

    public static final EditorLocale ARENA_EDITOR = builder(PREFIX + "Hub.Arenas")
        .name("Arenas")
        .text("Create and manage arenas here.")
        .build();

    public static final EditorLocale KIT_EDITOR = builder(PREFIX + "Hub.Kits")
        .name("Kits")
        .text("Create and manage kits here.")
        .build();

    public static final EditorLocale MOB_EDITOR = builder(PREFIX + "Hub.Mobs")
        .name("Mobs")
        .text("Create and manage mobs here.")
        .build();

    public static final EditorLocale ARENA_OBJECT = builder(PREFIX + "Arena.Object")
        .name(Placeholders.ARENA_NAME + GRAY + " (ID: " + WHITE + Placeholders.ARENA_ID + GRAY + ")")
        .textRaw(Placeholders.ARENA_REPORT)
        .emptyLine()
        .current("Enabled", Placeholders.ARENA_ACTIVE)
        .emptyLine()
        .click(LMB, "edit")
        .click(DRAG_DROP, "set icon")
        .click(SHIFT_RMB, "delete " + RED + "(no undo)")
        .build();

    public static final EditorLocale ARENA_CREATION = builder(PREFIX + "Arena.Create")
        .name("New Arena")
        .build();

    public static final EditorLocale ARENA_ACTIVE = builder(PREFIX + "Arena.Active")
        .name("Is Active")
        .textRaw(Placeholders.ARENA_REPORT).emptyLine()
        .text("Enables arena for playing.")
        .emptyLine()
        .currentHeader()
        .current("Enabled", Placeholders.ARENA_ACTIVE + GRAY + " (" + WHITE + LMB + GRAY + ")")
        .build();

    public static final EditorLocale ARENA_NAME = builder(PREFIX + "Arena.DisplayName")
        .name("Display Name")
        .text("General arena name.")
        .emptyLine()
        .currentHeader()
        .current("Name", Placeholders.ARENA_NAME + GRAY + " (" + WHITE + LMB + GRAY + ")")
        .build();

    public static final EditorLocale ARENA_SETUP_KIT = builder(PREFIX + "Arena.SetupKit")
        .name("Setup Kit")
        .text("Set of tools to setup", "arena locations.")
        .emptyLine()
        .click(LMB, "get tools")
        .build();

    public static final EditorLocale ARENA_AUTO_STATE_SCHEDULERS = builder(PREFIX + "Arena.AutoStateSchedulers")
        .name("Open / Close Schedulers")
        .text("This setting allows you to setup", "automatic arena opening/closing", "at certain day times.")
        .emptyLine()
        .text(YELLOW + BOLD + "Open Times:")
        .text(Placeholders.ARENA_OPEN_TIMES)
        .emptyLine()
        .text(YELLOW + BOLD + "Close Times:")
        .text(Placeholders.ARENA_CLOSE_TIMES)
        .emptyLine()
        .click(LMB, "add open time")
        .click(RMB, "add close time")
        .click(SHIFT_LMB, "clear open times")
        .click(SHIFT_RMB, "clear close times")
        .build();

    public static final EditorLocale ARENA_GAMEPLAY_SETTINGS = builder(PREFIX + "Arena.GameplaySettings")
        .name("Gameplay Settings")
        .textRaw(Placeholders.ARENA_REPORT_GAMEPLAY).emptyLine()
        .text("Various gameplay features and options.")
        .build();

    public static final EditorLocale ARENA_REGION_MANAGER = builder(PREFIX + "Arena.RegionManager")
        .name("Region Manager")
        .textRaw(Placeholders.ARENA_REPORT_REGIONS).emptyLine()
        .text("Create " + WHITE + "playable zones" + GRAY + " for", "the arena.")
        .text(GREEN + "(scripts not required)")
        .build();

    public static final EditorLocale ARENA_WAVE_MANAGER = builder(PREFIX + "Arena.WaveManager")
        .name("Wave Manager")
        .textRaw(Placeholders.ARENA_REPORT_WAVES).emptyLine()
        .text("Create " + WHITE + "mob groups" + GRAY + " to spawn", "them during the game.")
        .text(RED + "(scripts required)")
        .build();

    public static final EditorLocale ARENA_REWARD_MANAGER = builder(PREFIX + "Arena.RewardManager")
        .name("Reward Manager")
        .textRaw(Placeholders.ARENA_REPORT_REWARDS).emptyLine()
        .text("Motivate players to move", "forward by " + WHITE + "rewarding" + GRAY + " them!")
        .text(RED + "(scripts required)")
        .build();

    public static final EditorLocale ARENA_SUPPLY_MANAGER = builder(PREFIX + "Arena.SupplyManager")
        .name("Supply Manager")
        .text("Create containers with", WHITE + "respawnable" + GRAY + " items inside.")
        .text(RED + "(scripts required)")
        .build();

    public static final EditorLocale ARENA_SCRIPT_MANAGER = builder(PREFIX + "Arena.ScriptManager")
        .name("Script Manager")
        .textRaw(Placeholders.ARENA_REPORT_SCRIPTS).emptyLine()
        .text("Scripts used to define arena behavior.").emptyLine()
        .text(YELLOW + BOLD + "Required to:")
        .text(YELLOW + "→" + GRAY + " Spawn mobs")
        .text(YELLOW + "→" + GRAY + " Lock / Unlock regions")
        .text(YELLOW + "→" + GRAY + " Lock / Unlock shop items")
        .text(YELLOW + "→" + GRAY + " Give rewards")
        .text(YELLOW + "→" + GRAY + " Change spots")
        .text(YELLOW + "→" + GRAY + " Run custom commands")
        .text(YELLOW + "→" + GRAY + " any many other...")
        .build();

    public static final EditorLocale ARENA_SHOP_MANAGER = builder(PREFIX + "Arena.ShopManager")
        .name("Shop Manager")
        .textRaw(Placeholders.ARENA_REPORT_SHOP).emptyLine()
        .text("Let players to " + WHITE + "upgrade" + GRAY + " their stuff", "with in-game shop!")
        .text(GREEN + "(scripts not required)")
        .build();

    public static final EditorLocale ARENA_SPOT_MANAGER = builder(PREFIX + "Arena.SpotManager")
        .name("Spot Manager")
        .textRaw(Placeholders.ARENA_REPORT_SPOTS)
        .emptyLine()
        .text("Create " + WHITE + "dynamic places" + GRAY + ", such as", "openable doors, gates.")
        .text(RED + "(scripts required)")
        .build();

    public static final EditorLocale ARENA_PERMISSION_REQUIREMENT = builder(PREFIX + "Arena.PermissionRequirement")
        .name("Permission Requirement")
        .text("Sets whether or not permission", "is required to join the arena.").emptyLine()
        .currentHeader()
        .current("Enabled", Placeholders.ARENA_PERMISSION_REQUIREMENT + GRAY + " (" + WHITE + LMB + GRAY + ")")
        .current("Node", Placeholders.ARENA_PERMISSION)
        .build();

    public static final EditorLocale ARENA_PAYMENT_REQUIREMENT = builder(PREFIX + "Arena.PaymentRequirement")
        .name("Payment Requirements")
        .text("Makes players to pay", "in order to join the arena.")
        .emptyLine()
        .currentHeader()
        .text(Placeholders.ARENA_PAYMENT_REQUIREMENT)
        .emptyLine()
        .click(LMB, "add payment")
        .click(RMB, "remove all")
        .build();

    public static final EditorLocale ARENA_LEVEL_REQUIREMENT = builder(PREFIX + "Arena.LevelRequirement")
        .name("Level Requirements")
        .text("Sets level requirements to join the arena.")
        .emptyLine()
        .currentHeader()
        .text(Placeholders.ARENA_LEVEL_REQUIREMENT)
        .emptyLine()
        .click(LMB, "add requirement")
        .click(RMB, "remove all")
        .build();


    public static final EditorLocale REGION_OBJECT = builder(PREFIX + "Region.Object")
        .name(Placeholders.REGION_NAME + GRAY + " (" + WHITE + Placeholders.REGION_ID + GRAY + ")")
        .textRaw(Placeholders.REGION_REPORT).emptyLine()
        .current("Enabled", Placeholders.REGION_ACTIVE)
        .current("Is Default", Placeholders.REGION_DEFAULT)
        .emptyLine()
        .click(LMB, "edit")
        .click(SHIFT_RMB, "delete " + RED + "(no undo)")
        .build();

    public static final EditorLocale REGION_CREATE = builder(PREFIX + "Region.Create")
        .name("New Region")
        .build();

    public static final EditorLocale REGION_SETUP_KIT = builder(PREFIX + "Region.SetupKit")
        .name("Setup Kit")
        .text("Set of tools for region setup.").emptyLine()
        .click(LMB, "get tools")
        .build();

    public static final EditorLocale REGION_ACTIVE = builder(PREFIX + "Region.Active")
        .name("Is Active")
        .textRaw(Placeholders.REGION_REPORT).emptyLine()
        .text("Makes region enabled & active.").emptyLine()
        .currentHeader()
        .current("Enabled", Placeholders.REGION_ACTIVE + GRAY + " (" + WHITE + LMB + GRAY + ")")
        .build();

    public static final EditorLocale REGION_DEFAULT = builder(PREFIX + "Region.Default")
        .name("Is Default")
        .text("Default region is the first place,", "where players spawned on game start.").emptyLine()
        .currentHeader()
        .current("Default", Placeholders.REGION_DEFAULT + GRAY + " (" + WHITE + LMB + GRAY + ")")
        .build();

    public static final EditorLocale REGION_NAME = builder(PREFIX + "Region.DisplayName")
        .name("Display Name")
        .text("General region name.").emptyLine()
        .currentHeader()
        .current("Name", Placeholders.REGION_NAME + GRAY + " (" + WHITE + LMB + GRAY + ")")
        .build();

    public static final EditorLocale GAMEPLAY_PAGE_REQUIREMENTS = builder(PREFIX + "Gameplay.Page.Requirements")
        .name("Requirements")
        .build();

    public static final EditorLocale GAMEPLAY_PAGE_GLOBALS = builder(PREFIX + "Gameplay.Page.Globals")
        .name("Globals")
        .build();

    public static final EditorLocale GAMEPLAY_PAGE_PLAYERS = builder(PREFIX + "Gameplay.Page.Players")
        .name("Mobs & Players")
        .build();

    public static final EditorLocale GAMEPLAY_PAGE_COMPAT = builder(PREFIX + "Gameplay.Page.Compat")
        .name("Compatibility")
        .build();

    public static final EditorLocale GAMEPLAY_TIMELEFT = builder(PREFIX + "Gameplay.Timeleft")
        .name("Timeleft")
        .text("Amount of time players have to", "finish the arena until it", "auto-ends as lose.").emptyLine()
        .currentHeader()
        .current("Timeleft", Placeholders.GAMEPLAY_TIMELEFT + " min." + GRAY + " (" + WHITE + LMB + GRAY + ")").emptyLine()
        .click(DROP_KEY, "disable")
        .build();

    public static final EditorLocale GAMEPLAY_LOBBY_COUNTDOWN = builder(PREFIX + "Gameplay.LobbyTime")
        .name("Lobby Countdown")
        .text("Lobby preparation time.").emptyLine()
        .currentHeader()
        .current("Countdown", Placeholders.GAMEPLAY_LOBBY_TIME + " sec." + GRAY + " (" + WHITE + LMB + GRAY + ")")
        .build();

    public static final EditorLocale GAMEPLAY_ANNOUNCEMENTS = builder(PREFIX + "Gameplay.Announces")
        .name("Start/End Announcements")
        .text("Enables server-wide announcements when", "arena is about to start/end.").emptyLine()
        .currentHeader()
        .current("Enabled", Placeholders.GAMEPLAY_ANNOUNCEMENTS + GRAY + " (" + WHITE + LMB + GRAY + ")")
        .build();

    public static final EditorLocale GAMEPLAY_SCOREBOARD = builder(PREFIX + "Gameplay.Scoreboard")
        .name("Scoreboard")
        .text(Placeholders.GAMEPLAY_SCOREBOARD_CHECK).emptyLine()
        .text("Sets scoreboard to display", "for players in arena.")
        .emptyLine()
        .currentHeader()
        .current("Enabled", Placeholders.GAMEPLAY_SCOREBOARD_ENABLED + GRAY + " (" + WHITE + LMB + GRAY + ")")
        .current("Template", Placeholders.GAMEPLAY_SCOREBOARD_ID + GRAY + " (" + WHITE + RMB + GRAY + ")")
        .build();

    public static final EditorLocale GAMEPLAY_HUNGER_REGEN = builder(PREFIX + "Gameplay.HungerRegeneration")
        .name("Natural Hunger & Regen")
        .text("Enables natural hunger and", "regeneration on the arena.").emptyLine()
        .currentHeader()
        .current("Hunger", Placeholders.GAMEPLAY_HUNGER_ENABLED + GRAY + " (" + WHITE + LMB + GRAY + ")")
        .current("Regeneration", Placeholders.GAMEPLAY_REGENERATION_ENABLED + GRAY + " (" + WHITE + RMB + GRAY + ")")
        .build();

    public static final EditorLocale GAMEPLAY_ITEM_PICK_DROP = builder(PREFIX + "Gameplay.Items.PickDrop")
        .name("Item Drop & Pickup")
        .text("Sets whether or not players", "can pickup / drop items.").emptyLine()
        .currentHeader()
        .current("Drop Enabled", Placeholders.GAMEPLAY_ITEM_DROP_ENABLED + GRAY + " (" + WHITE + LMB + GRAY + ")")
        .current("Pickup Enabled", Placeholders.GAMEPLAY_ITEM_PICKUP_ENABLED + GRAY + " (" + WHITE + RMB + GRAY + ")")
        .build();

    public static final EditorLocale GAMEPLAY_ITEM_DURABILITY = builder(PREFIX + "Gameplay.Items.Durability")
        .name("Item Durability")
        .text("Sets whether or not items", "can lose their durability.").emptyLine()
        .currentHeader()
        .current("Enabled", Placeholders.GAMEPLAY_ITEM_DURABULITY_ENABLED + GRAY + " (" + WHITE + LMB + GRAY + ")")
        .build();

    public static final EditorLocale GAMEPLAY_MOB_HIGHLIGHT = builder(PREFIX + "Gameplay.MobHighlight")
        .name("Mob Highlighting")
        .text("Enables mob highlighting when", "amount (in %) of mobs is less or equal", "to specified value.").emptyLine()
        .currentHeader()
        .current("Enabled", Placeholders.GAMEPLAY_MOB_HIGHLIGHT_ENABLED + GRAY + " (" + WHITE + LMB + GRAY + ")")
        .current("Amount", "<= " + Placeholders.GAMEPLAY_MOB_HIGHLIGHT_AMOUNT + "%" + GRAY + " (" + WHITE + RMB + GRAY + ")")
        .current("Color", Placeholders.GAMEPLAY_MOB_HIGHLIGHT_COLOR + GRAY + " (" + WHITE + SHIFT_LMB + GRAY + ")")
        .build();

    public static final EditorLocale GAMEPLAY_MOB_LOOT = builder(PREFIX + "Gameplay.MobLoot")
        .name("Mob Loot")
        .text("Sets whether or not", "mobs will drop items & xp.").emptyLine()
        .currentHeader()
        .current("Drop Items", Placeholders.GAMEPLAY_MOB_DROP_ITEMS + GRAY + " (" + WHITE + LMB + GRAY + ")")
        .current("Drop XP", Placeholders.GAMEPLAY_MOB_DROP_EXP + GRAY + " (" + WHITE + RMB + GRAY + ")")
        .build();

    public static final EditorLocale GAMEPLAY_BANNED_ITEMS = builder(PREFIX + "Gameplay.BannedItems")
        .name("Banned Items")
        .text("A list of items that can not", "be used on the arena.").emptyLine()
        .currentHeader()
        .text(Placeholders.GAMEPLAY_BANNED_ITEMS)
        .emptyLine()
        .click(LMB, "add item")
        .click(RMB, "remove all")
        .build();

    public static final EditorLocale GAMEPLAY_ALLOWED_SPAWN_REASONS = builder(PREFIX + "Gameplay.SpawnReasons")
        .name("Allowed Spawn Reasons")
        .text("A list of spawn reasons that are", "allowed to spawn mobs inside arena.").emptyLine()
        .text("Useful for compatibility with", "other plugins.").emptyLine()
        .currentHeader()
        .text(Placeholders.GAMEPLAY_ALLOWED_SPAWN_REASONS)
        .emptyLine()
        .click(LMB, "add reason")
        .click(DROP_KEY, "remove all")
        .build();

    public static final EditorLocale GAMEPLAY_PLAYER_AMOUNT = builder(PREFIX + "Gameplay.Player.Amount")
        .name("Player Amount")
        .text("Sets min/max players for the arena.")
        .emptyLine()
        .currentHeader()
        .current("Min", Placeholders.GAMEPLAY_PLAYER_AMOUNT_MIN + GRAY + " (" + WHITE + LMB + GRAY + ")")
        .current("Max", Placeholders.GAMEPLAY_PLAYER_AMOUNT_MAX + GRAY + " (" + WHITE + RMB + GRAY + ")")
        .build();

    public static final EditorLocale GAMEPLAY_PLAYER_LIFES = builder(PREFIX + "Gameplay.Player.Lifes")
        .name("Player Lifes")
        .text("Sets amount of player lifes", "and respawn time.")
        .emptyLine()
        .currentHeader()
        .current("Lifes", Placeholders.GAMEPLAY_PLAYER_LIFES_AMOUNT + GRAY + " (" + WHITE + LMB + GRAY + ")")
        .current("Respawn", Placeholders.GAMEPLAY_PLAYER_REVIVE_TIME + GRAY + " (" + WHITE + RMB + GRAY + ")")
        .emptyLine()
        .click(DROP_KEY, "unlimited lifes")
        .click(SWAP_KEY, "disable auto-respawn")
        .build();

    public static final EditorLocale GAMEPLAY_KEEP_INVENTORY = builder(PREFIX + "Gameplay.KeepInventory")
        .name("Keep Inventory")
        .text("Sets whether or not", "players will keep inventory", "on death.")
        .emptyLine()
        .currentHeader()
        .current("Enabled", Placeholders.GAMEPLAY_KEEP_INVENTORY + GRAY + " (" + WHITE + LMB + GRAY + ")")
        .build();

    public static final EditorLocale GAMEPLAY_SPECTATE = builder(PREFIX + "Gameplay.Spectate")
        .name("Spectators Allowed")
        .text("Sets whether or not", "non-arena players can spectate", "this arena.")
        .emptyLine()
        .currentHeader()
        .current("Enabled", Placeholders.GAMEPLAY_SPECTATE_ENABLED + GRAY + " (" + WHITE + LMB + GRAY + ")")
        .build();

    public static final EditorLocale GAMEPLAY_COMMANDS = builder(PREFIX + "Gameplay.Commands")
        .name("Command Whitelist")
        .text("List of commands allowed", "for usage on the arena.")
        .emptyLine()
        .currentHeader()
        .text(Placeholders.GAMEPLAY_COMMAND_WHITELIST)
        .emptyLine()
        .click(LMB, "add command")
        .click(RMB, "allow any command")
        .click(DROP_KEY, "remove all")
        .build();

    public static final EditorLocale GAMEPLAY_KITS = builder(PREFIX + "Gameplay.Kits.Enabled")
        .name("Kits")
        .text("Sets if kits are required", "on this arena.").emptyLine()
        .currentHeader()
        .current("Enabled", Placeholders.GAMEPLAY_KITS_ENABLED + GRAY + " (" + WHITE + LMB + GRAY + ")")
        .build();

    public static final EditorLocale GAMEPLAY_ALLOWED_KITS = builder(PREFIX + "Gameplay.Kits.Limits")
        .name("Allowed Kits & Limits")
        .text("Sets kits allowed with player limits.")
        .emptyLine()
        .currentHeader()
        .text(Placeholders.GAMEPLAY_KITS_LIMITS).emptyLine()
        .click(LMB, "add kit")
        .click(DROP_KEY, "remove limits")
        .build();

    public static final EditorLocale GAMEPLAY_PETS = builder(PREFIX + "Gameplay.Pets")
        .name("Pets")
        .text(Placeholders.GAMEPLAY_PETS_CHECK).emptyLine()
        .text("Sets whether or not", "players can bring pets", "on the arena.").emptyLine()
        .currentHeader()
        .current("Enabled", Placeholders.GAMEPLAY_PETS_ALLOWED + GRAY + " (" + WHITE + LMB + GRAY + ")")
        .build();

    public static final EditorLocale GAMEPLAY_MCMMO = builder(PREFIX + "Gameplay.Mcmmo")
        .name("mcMMO")
        .text("Sets whether or not", "players can use mcMMO abilities.").emptyLine()
        .currentHeader()
        .current("Enabled", Placeholders.GAMEPLAY_MCMMO_ALLOWED + GRAY + " (" + WHITE + LMB + GRAY + ")")
        .build();

    public static final EditorLocale WAVES_ROUND_INTERVAL = builder(PREFIX + "Waves.RoundInterval")
        .name("Round Interval")
        .text("Countdown until next round", "when all mobs are dead.")
        .emptyLine()
        .currentHeader()
        .current("First Round", Placeholders.ARENA_WAVES_FIRST_ROUND_COUNTDOWN + " sec." + GRAY + " (" + WHITE + LMB + GRAY + ")")
        .current("All Other", Placeholders.ARENA_WAVES_ROUND_COUNTDOWN + " sec." + GRAY + " (" + WHITE + RMB + GRAY + ")")
        .build();

    public static final EditorLocale WAVES_FINAL_ROUND = builder(PREFIX + "Waves.FinalRound")
        .name("Final Round")
        .text("Sets final round to", "complete the arena.").emptyLine()
        .currentHeader()
        .current("Final Round", Placeholders.ARENA_WAVES_FINAL_ROUND + GRAY + " (" + WHITE + LMB + GRAY + ")")
        .emptyLine()
        .click(DROP_KEY, "make unlimited")
        .build();

    public static final EditorLocale WAVES_WAVES = builder(PREFIX + "Waves.WaveList")
        .name("Waves")
        .text("Create mob groups and spawn", "them with arena scripts.")
        .emptyLine()
        .click(LMB, "navigate")
        .build();

    public static final EditorLocale WAVES_GRADUAL = builder(PREFIX + "Waves.Graduals")
        .name("Gradual Spawning")
        .current("Enabled", Placeholders.ARENA_WAVES_GRADUAL_ENABLED).emptyLine()
        .text("This feature allows you to", "spawn mobs step by step", "during the round instead")
        .text("of spawn all of them", "at round start.")
        .emptyLine()
        .click(LMB, "navigate")
        .build();

    public static final EditorLocale WAVES_GRADUAL_ENABLED = builder(PREFIX + "Waves.Gradual.Enabled")
        .name("Enabled")
        .text("Enables gradual spawn feature.").emptyLine()
        .currentHeader()
        .current("Enabled", Placeholders.ARENA_WAVES_GRADUAL_ENABLED + GRAY + " (" + WHITE + LMB + GRAY + ")")
        .build();

    public static final EditorLocale WAVES_GRADUAL_FIRST_PERCENT = builder(PREFIX + "Waves.Gradual.FistPercent")
        .name("First Group Amount (%)")
        .text("Sets how many mobs (in %) of total", "amount will be spawned on", "round start.")
        .emptyLine()
        .currentHeader()
        .current("Amount", Placeholders.ARENA_WAVES_GRADUAL_FIRST_PERCENT + "%" + GRAY + " (" + WHITE + LMB + GRAY + ")")
        .build();

    public static final EditorLocale WAVES_GRADUAL_NEXT_PERCENT = builder(PREFIX + "Waves.Gradual.NextPercent")
        .name("Next Group Amount (%)")
        .text("Sets how many mobs (%) of total", "amount will be spawned after", "the first group.")
        .text(RED + "(can't exceed 'First Group Amount')")
        .emptyLine()
        .currentHeader()
        .current("Amount", Placeholders.ARENA_WAVES_GRADUAL_NEXT_PERCENT + "%" + GRAY + " (" + WHITE + LMB + GRAY + ")")
        .build();

    public static final EditorLocale WAVES_GRADUAL_NEXT_INTERVAL = builder(PREFIX + "Waves.Gradual.NextInterval")
        .name("Next Group Interval")
        .text("Sets how often (in seconds)", "arena will attempt to spawn another", "group of mobs.")
        .emptyLine()
        .currentHeader()
        .current("Interval", Placeholders.ARENA_WAVES_GRADUAL_NEXT_INTERVAL + " sec." + GRAY + " (" + WHITE + LMB + GRAY + ")")
        .build();

    public static final EditorLocale WAVES_GRADUAL_NEXT_KILL_PERCENT = builder(PREFIX + "Waves.Gradual.NextKillPercent")
        .name("Next Group for Killed Amount")
        .text("Sets how many mobs (in %) of total", "amount must be killed to prepare", "next group to spawn.")
        .text(RED + "(can't exceed 'Next Group Amount')").emptyLine()
        .text("Set this to 0 to spawn mobs", "using the " + YELLOW + "'Next Group Interval'" + GRAY + " only.").emptyLine()
        .currentHeader()
        .current("Amount", Placeholders.ARENA_WAVES_GRADUAL_NEXT_KILL_PERCENT + "%" + GRAY + " (" + WHITE + LMB + GRAY + ")")
        .build();

    public static final EditorLocale WAVES_WAVE_OBJECT = builder(PREFIX + "Waves.Wave.Object")
        .name("Wave: " + ORANGE + Placeholders.ARENA_WAVE_ID)
        .text(YELLOW + BOLD + "Mobs:")
        .text(Placeholders.ARENA_WAVE_MOBS)
        .emptyLine()
        .click(LMB, "edit")
        .click(DRAG_DROP, "set icon")
        .click(SHIFT_RMB, "delete" + RED + " (no undo)")
        .build();

    public static final EditorLocale WAVES_WAVE_CREATE = builder(PREFIX + "Waves.Wave.Create")
        .name("Create Wave")
        .build();

    public static final EditorLocale WAVES_WAVE_MOB_OBJECT = builder(PREFIX + "Waves.Wave.Mob.Object")
        .name("Mob: " + ORANGE + Placeholders.ARENA_WAVE_MOB_ID)
        .current("Provider", Placeholders.ARENA_WAVE_MOB_PROVIDER + GRAY + " (" + WHITE + SWAP_KEY + GRAY + ")")
        .current("Start Amount", "x" + Placeholders.ARENA_WAVE_MOB_AMOUNT + GRAY + " (" + WHITE + RMB + GRAY + ")")
        .current("Start Level", Placeholders.ARENA_WAVE_MOB_LEVEL + GRAY + " (" + WHITE + SHIFT_LMB + GRAY + ")")
        .current("Spawn Chance", Placeholders.ARENA_WAVE_MOB_CHANCE + "%" + GRAY + " (" + WHITE + DROP_KEY + GRAY + ")")
        .emptyLine()
        .click(LMB, "change mob")
        .click(DRAG_DROP, "set icon")
        .click(SHIFT_RMB, "delete" + RED + " (no undo)")
        .build();

    public static final EditorLocale WAVES_WAVE_MOB_CREATE = builder(PREFIX + "Waves.Wave.Mob.Create")
        .name("Add Mob")
        .text("Adds an empty mob configuration.")
        .build();

    public static final EditorLocale SUPPLY_CHEST_CREATE = builder(PREFIX + "Supply.Chest.Create")
        .name("New Supply Chest")
        .build();

    public static final EditorLocale SUPPLY_CHEST_OBJECT = builder(PREFIX + "Supply.Chest.Object")
        .name("Supply Chest " + GRAY + "(ID: " + WHITE + Placeholders.SUPPLY_CHEST_ID + GRAY + ")")
        .current("Location", Placeholders.SUPPLY_CHEST_LOCATION_X + ", " + Placeholders.SUPPLY_CHEST_LOCATION_Y + ", " + Placeholders.SUPPLY_CHEST_LOCATION_Z + GRAY + " in " + YELLOW + Placeholders.SUPPLY_CHEST_LOCATION_WORLD)
        .emptyLine()
        .click(LMB, "edit")
        .click(RMB, "teleport")
        .click(SHIFT_RMB, "delete " + RED + "(no undo)")
        .build();

    public static final EditorLocale SUPPLY_CHEST_ITEMS = builder(PREFIX + "Supply.Chest.Content")
        .name("Content")
        .text("List of items to be", "spawned in this chest.").emptyLine()
        .click(LMB, "open")
        .build();

    public static final EditorLocale SUPPLY_CHEST_REFILL_AMOUNT = builder(PREFIX + "Supply.Chest.RefillAmount")
        .name("Refill Amount")
        .text("Sets how many items will be", "spawned in this chest.").emptyLine()
        .currentHeader()
        .current("Min", Placeholders.SUPPLY_CHEST_REFILL_ITEMS_MIN + GRAY + " (" + WHITE + LMB + GRAY + ")")
        .current("Max", Placeholders.SUPPLE_CHEST_REFILL_ITEMS_MAX + GRAY + " (" + WHITE + RMB + GRAY + ")")
        .build();

    public static final EditorLocale SUPPLY_CHEST_LOCATION = builder( PREFIX + "Supply.Chest.Location")
        .name("Location")
        .text("Sets chest location.").emptyLine()
        .currentHeader()
        .current("Location", Placeholders.SUPPLY_CHEST_LOCATION_X + ", " + Placeholders.SUPPLY_CHEST_LOCATION_Y + ", " + Placeholders.SUPPLY_CHEST_LOCATION_Z + " in " + Placeholders.SUPPLY_CHEST_LOCATION_WORLD)
        .emptyLine()
        .click(LMB, "assign / change")
        .build();

    public static final EditorLocale REWARDS_RETAIN = builder(PREFIX + "Rewards.Keep")
        .name("Keep Rewards")
        .text("Sets whether or not players", "will keep obtained rewards", "on death / leave.")
        .emptyLine()
        .currentHeader()
        .current("On Death", Placeholders.REWARD_MANAGER_KEEP_ON_DEATH + GRAY + " (" + WHITE + LMB + GRAY + ")")
        .current("On Leave", Placeholders.REWARD_MANAGER_KEEP_ON_LEAVE + GRAY + " (" + WHITE + RMB + GRAY + ")")
        .build();

    public static final EditorLocale REWARD_OBJECT = builder(PREFIX + "Reward.Object")
        .name(Placeholders.REWARD_NAME + GRAY + " (ID: " + WHITE + Placeholders.REWARD_ID + GRAY + ")")
        .textRaw(Placeholders.REWARD_REPORT).emptyLine()
        .click(LMB, "edit")
        .click(SHIFT_RMB, "delete " + RED + "(no undo)")
        .build();

    public static final EditorLocale REWARD_CREATE = builder(PREFIX + "Reward.Create")
        .name("New Reward")
        .build();

    public static final EditorLocale REWARD_NAME = builder(PREFIX + "Reward.DisplayName")
        .name("Display Name")
        .text("General reward name.").emptyLine()
        .currentHeader()
        .current("Name", Placeholders.REWARD_NAME + GRAY + " (" + WHITE + LMB + GRAY + ")")
        .build();

    public static final EditorLocale REWARD_COMPLETION_REQUIRED = builder(PREFIX + "Reward.CompletionRequired")
        .name("Completion Required")
        .text("Sets whether or not player", "must complete the arena to", "receive this reward.")
        .emptyLine()
        .currentHeader()
        .current("Enabled", Placeholders.REWARD_COMPLETTION_REQUIRED + GRAY + " (" + WHITE + LMB + GRAY + ")")
        .build();

    public static final EditorLocale REWARD_COMMANDS = builder(PREFIX + "Reward.Commands")
        .name("Commands")
        .text("Commands to execute when reward", "given to player.").emptyLine()
        .currentHeader().text(Placeholders.REWARD_COMMANDS)
        .emptyLine()
        .text(YELLOW + BOLD + "Placeholders:")
        .current(Placeholders.PLAYER_NAME, "for player name.")
        .current(EngineUtils.PLACEHOLDER_API, "all of them")
        .emptyLine()
        .click(LMB, "add command")
        .click(RMB, "remove all")
        .build();

    public static final EditorLocale REWARD_ITEMS = builder(PREFIX + "Reward.Items")
        .name("Items")
        .text("Items added to player's inventory.").emptyLine()
        .click(LMB, "open")
        .build();

    public static final EditorLocale SHOP_CATEGORIES = builder(PREFIX + "Shop.Categories")
        .name("Item Categories")
        .text("Create and manage item categories here.")
        .emptyLine()
        .click("Left-Click", "Navigate")
        .build();
    
    public static final EditorLocale SHOP_ACTIVE = builder(PREFIX + "Shop.Active")
        .name("Active")
        .text("Enables the shop feature.").emptyLine()
        .currentHeader()
        .current("Enabled", Placeholders.SHOP_MANAGER_IS_ACTIVE + GRAY + " (" + WHITE + LMB + GRAY + ")")
        .build();
    
    public static final EditorLocale SHOP_HIDE_OTHER_KIT_ITEMS = builder(PREFIX + "Shop.HideOtherKitItems")
        .name("Hide Other Kit Items")
        .text("Hides items with kit requirements", "for players without that kits.")
        .emptyLine()
        .currentHeader()
        .current("Enabled", Placeholders.SHOP_MANAGER_HIDE_OTHER_KIT_ITEMS + GRAY + " (" + WHITE + LMB + GRAY + ")")
        .build();

    public static final EditorLocale SHOP_CATEGORY_OBJECT = builder(PREFIX + "Shop.Category.Object")
        .name(Placeholders.SHOP_CATEGORY_NAME + GRAY + " (ID: " + WHITE + Placeholders.SHOP_CATEGORY_ID + GRAY + ")")
        .textRaw(Placeholders.SHOP_CATEGORY_REPORT)
        .emptyLine()
        .click(LMB, "edit")
        .click(SHIFT_RMB, "delete " + RED + "(no undo)")
        .build();

    public static final EditorLocale SHOP_CATEGORY_CREATE = builder(PREFIX + "Shop.Category.Create")
        .name("New Category")
        .build();

    public static final EditorLocale SHOP_CATEGORY_NAME = builder(PREFIX + "Shop.Category.DisplayName")
        .name("Display Name")
        .text("General category name.")
        .emptyLine()
        .currentHeader()
        .current("Name", Placeholders.SHOP_CATEGORY_NAME + GRAY + " (" + WHITE + LMB + GRAY + ")")
        .build();

    public static final EditorLocale SHOP_CATEGORY_DESCRIPTION = builder(PREFIX + "Shop.Category.Description")
        .name("Category Description")
        .text("Describe the category.")
        .emptyLine()
        .currentHeader()
        .text(Placeholders.SHOP_CATEGORY_DESCRIPTION)
        .emptyLine()
        .click(LMB, "add line")
        .click(RMB, "remove all")
        .build();

    public static final EditorLocale SHOP_CATEGORY_ICON = builder(PREFIX + "Shop.Category.Icon")
        .name("Icon")
        .text("Sets category icon.").emptyLine()
        .click(DRAG_DROP, "replace")
        .build();

    public static final EditorLocale SHOP_CATEGORY_KITS_REQUIRED = builder(PREFIX + "Shop.Category.KitsRequired")
        .name("Kits Required")
        .text("This category will be available for", "players with specified kits only.")
        .emptyLine()
        .currentHeader()
        .text(Placeholders.SHOP_CATEGORY_KITS_REQUIRED)
        .emptyLine()
        .click(LMB, "add kit")
        .click(RMB, "remove all")
        .build();

    public static final EditorLocale SHOP_CATEGORY_PRODUCTS = builder(PREFIX + "Shop.Category.Products")
        .name("Products")
        .text("Add some items to the category!").emptyLine()
        .click(LMB, "navigate")
        .build();

    public static final EditorLocale SHOP_PRODUCT_OBJECT = builder(PREFIX + "Shop.Product.Object")
        .name(Placeholders.SHOP_PRODUCT_NAME + GRAY + " (ID: " + WHITE + Placeholders.SHOP_PRODUCT_ID + GRAY + ")")
        .textRaw(Placeholders.SHOP_PRODUCT_REPORT).emptyLine()
        .current("Price", Placeholders.SHOP_PRODUCT_PRICE)
        .emptyLine()
        .click(LMB, "edit")
        .click(SHIFT_RMB, "delete " + RED + "(no undo)")
        .build();

    public static final EditorLocale SHOP_PRODUCT_CREATE = builder(PREFIX + "Shop.Product.Create")
        .name("New Product")
        .build();

    public static final EditorLocale SHOP_PRODUCT_NAME = builder(PREFIX + "Shop.Product.DisplayName")
        .name("Display Name")
        .text("General product name.").emptyLine()
        .currentHeader()
        .current("Name", Placeholders.SHOP_PRODUCT_NAME + GRAY + " (" + WHITE + LMB + GRAY + ")")
        .build();

    public static final EditorLocale SHOP_PRODUCT_DESCRIPTION = builder(PREFIX + "Shop.Product.Description")
        .name("Description")
        .text("Describe the product.").emptyLine()
        .currentHeader()
        .text(Placeholders.SHOP_PRODUCT_DESCRIPTION)
        .emptyLine()
        .click(LMB, "add line")
        .click(RMB, "remove all")
        .build();

    public static final EditorLocale SHOP_PRODUCT_PRICE = builder(PREFIX + "Shop.Product.Price")
        .name("Price & Currency")
        .text("Sets product price & currency.").emptyLine()
        .currentHeader()
        .current("Cost", Placeholders.SHOP_PRODUCT_PRICE + GRAY + " (" + WHITE + LMB + GRAY + ")")
        .current("Currency", Placeholders.SHOP_PRODUCT_CURRENCY + GRAY + " (" + WHITE + RMB + GRAY + ")")
        .build();

    public static final EditorLocale SHOP_PRODUCT_ICON = builder(PREFIX + "Shop.Product.Icon")
        .name("Icon")
        .text("Sets product icon.").emptyLine()
        .click(DRAG_DROP, "replace")
        .build();

    public static final EditorLocale SHOP_PRODUCT_KITS_REQUIRED = builder(PREFIX + "Shop.Product.KitsRequired")
        .name("Kits Required")
        .text("This item will be available for", "players with specified kits only.")
        .emptyLine()
        .currentHeader().text(Placeholders.SHOP_PRODUCT_KITS_REQUIRED)
        .emptyLine()
        .click(LMB, "add kit")
        .click(RMB, "remove all")
        .build();

    public static final EditorLocale SHOP_PRODUCT_ITEMS = builder(PREFIX + "Shop.Product.Items")
        .name("Items")
        .text("Items to be added to player's inventory.").emptyLine()
        .click(LMB, "navigate")
        .build();

    public static final EditorLocale SHOP_PRODUCT_COMMANDS = builder(PREFIX + "Shop.Product.Commands")
        .name("Commands")
        .text("Commands to execute when", "player purchases this product.").emptyLine()
        .currentHeader()
        .text(Placeholders.SHOP_PRODUCT_COMMANDS)
        .emptyLine()
        .text(YELLOW + BOLD + "Placeholders:")
        .current(Placeholders.PLAYER_NAME, "for player name.")
        .current(EngineUtils.PLACEHOLDER_API, "all of them")
        .emptyLine()
        .click(LMB, "add command")
        .click(SHIFT_RMB, "remove all")
        .build();

    public static final EditorLocale SPOT_OBJECT = builder(PREFIX + "Spot.Object")
        .name(Placeholders.SPOT_NAME + GRAY + " (ID: " + WHITE + Placeholders.SPOT_ID + GRAY + ")")
        .textRaw(Placeholders.SPOT_REPORT).emptyLine()
        .current("Active", Placeholders.SPOT_ACTIVE).emptyLine()
        .click(LMB, "edit")
        .click(SHIFT_RMB, "delete " + RED + "(no undo)")
        .build();

    public static final EditorLocale SPOT_CREATE = builder(PREFIX + "Spot.Create")
        .name("New Spot")
        .build();

    public static final EditorLocale SPOT_SETUP_KIT = builder(PREFIX + "Spot.SetupKit")
        .name("Setup Kit")
        .text("Set of tools to setup spot.").emptyLine()
        .click(LMB, "get")
        .build();

    public static final EditorLocale SPOT_ACTIVE = builder(PREFIX + "Spot.Active")
        .name("Active")
        .textRaw(Placeholders.SPOT_REPORT).emptyLine()
        .text("Enables the spot.").emptyLine()
        .currentHeader()
        .current("Enabled", Placeholders.SPOT_ACTIVE + GRAY + " (" + WHITE + LMB + GRAY + ")")
        .build();

    public static final EditorLocale SPOT_NAME = builder(PREFIX + "Spot.DisplayName")
        .name("Display Name")
        .text("General spot name.").emptyLine()
        .currentHeader()
        .current("Name", Placeholders.SPOT_NAME + GRAY + " (" + WHITE + LMB + GRAY + ")")
        .build();

    public static final EditorLocale SPOT_STATES = builder(PREFIX + "Spot.States")
        .name("States")
        .text("Create and setup spot states here.").emptyLine()
        .click(LMB, "navigate")
        .build();

    public static final EditorLocale SPOT_STATE_OBJECT = builder(PREFIX + "Spot.State.Object")
        .name("State: " + ORANGE + Placeholders.SPOT_STATE_ID)
        .text("Simply build something inside", "the spot area and save it!")
        .emptyLine()
        .click(LMB, "build / edit")
        .click(SHIFT_RMB, "delete " + RED + "(no undo)")
        .build();

    public static final EditorLocale SPOT_STATE_CREATE = builder(PREFIX + "Spot.State.Create")
        .name("New State")
        .build();

    public static final EditorLocale SCRIPT_CATEGORY_OBJECT = builder(PREFIX + "Script.Category.Object")
        .name("Category: " + ORANGE + Placeholders.SCRIPT_CATEGORY_ID)
        .emptyLine()
        .click(LMB, "edit")
        .click(SHIFT_RMB, "delete " + RED + "(no undo)")
        .build();

    public static final EditorLocale SCRIPT_CATEGORY_CREATE = builder(PREFIX + "Script.Category.Create")
        .name("New Category")
        .build();

    public static final EditorLocale SCRIPT_OBJECT = builder(PREFIX + "Script.Object")
        .name("Script: " + ORANGE + Placeholders.SCRIPT_ID)
        .current("Event", Placeholders.SCRIPT_EVENT_TYPE + GRAY + " (" + WHITE + DROP_KEY + GRAY + ")")
        .current("In Game Only", Placeholders.SCRIPT_IN_GAME_ONLY + GRAY + " (" + WHITE + SHIFT_LMB + GRAY + ")")
        .emptyLine()
        .text(YELLOW + BOLD + "Conditions:")
        .text(Placeholders.SCRIPT_CONDITION_SECTION_CONDITIONS)
        .emptyLine()
        .text(YELLOW + BOLD + "Actions:")
        .text(Placeholders.SCRIPT_ACTION_PARAMS)
        .emptyLine()
        .click(LMB, "edit Actions")
        .click(RMB, "edit Conditions")
        .click(DRAG_DROP, "set Icon")
        .click(SHIFT_RMB, "delete " + RED + "(no undo)")
        .build();

    public static final EditorLocale SCRIPT_CREATE = builder(PREFIX + "Script.Create")
        .name("New Script")
        .build();

    public static final EditorLocale SCRIPT_ACTION_OBJECT = builder(PREFIX + "Script.Action.Object")
        .name("Action: " + ORANGE + Placeholders.SCRIPT_ACTION_NAME)
        .text(YELLOW + BOLD + "Parameters: ")
        .text(Placeholders.SCRIPT_ACTION_PARAMS)
        .emptyLine()
        .click(LMB, "edit parameters")
        .click(RMB, "clear all")
        .click(SHIFT_RMB, "delete " + RED + "(no undo)")
        .build();

    public static final EditorLocale SCRIPT_ACTION_CREATE = builder(PREFIX + "Script.Action.Create")
        .name("New Action")
        .build();

    public static final EditorLocale SCRIPT_ACTION_PARAMETER_OBJECT = builder(PREFIX + "Script.Action.Parameter.Object")
        .name("Parameter: " + GREEN + Placeholders.SCRIPT_PARAMETER_NAME)
        .current("Value", Placeholders.SCRIPT_PARAMETER_VALUE + GRAY + " (" + WHITE + LMB + GRAY + ")")
        .build();

    public static final EditorLocale SCRIPT_CONDITION_SECTION_OBJECT = builder(PREFIX + "Script.Condition.Section.Object")
        .name("Conditions Section: " + ORANGE + Placeholders.SCRIPT_CONDITION_SECTION_ID)
        .currentHeader().text(Placeholders.SCRIPT_CONDITION_SECTION_CONDITIONS)
        .emptyLine()
        .click(LMB, "add condition")
        .click(RMB, "remove all")
        .click(SHIFT_RMB, "delete " + RED + "(no undo)")
        .build();

    public static final EditorLocale SCRIPT_CONDITION_SECTION_CREATE = builder(PREFIX + "Script.Condition.Section.Create")
        .name("New Section")
        .text("If " + WHITE + "any condition" + GRAY + " from a", "section is failed, then", "the " + WHITE + "whole section" + GRAY + " is failed too.")
        .emptyLine()
        .text("If " + RED + "all sections" + GRAY + " are failed,", "script actions " + RED + "won't" + GRAY + " run.")
        .build();

    public static final EditorLocale KIT_OBJECT = builder(PREFIX + "Kit.Object")
        .name(Placeholders.KIT_NAME + GRAY + " (ID: " + WHITE + Placeholders.KIT_ID + GRAY + ")")
        .current("Is Default", Placeholders.KIT_IS_DEFAULT)
        .current("Cost", Placeholders.KIT_COST)
        .emptyLine()
        .click(LMB, "edit")
        .click(SHIFT_RMB, "delete " + RED + "(no undo)")
        .build();

    public static final EditorLocale KIT_CREATE = builder(PREFIX + "Kit.Create")
        .name("New Kit")
        .build();

    public static final EditorLocale KIT_NAME = builder(PREFIX + "Kit.DisplayName")
        .name("Display Name")
        .text("General kit name.").emptyLine()
        .currentHeader()
        .current("Name", Placeholders.KIT_NAME + GRAY + " (" + WHITE + LMB + GRAY + ")")
        .build();

    public static final EditorLocale KIT_DESCRIPTION = builder(PREFIX + "Kit.Description")
        .name("Description")
        .text("Describe the kit.").emptyLine()
        .currentHeader()
        .text(Placeholders.KIT_DESCRIPTION)
        .emptyLine()
        .click(LMB, "add line")
        .click(RMB, "remove all")
        .build();

    public static final EditorLocale KIT_ICON = builder(PREFIX + "Kit.Icon")
        .name("Icon")
        .text("Sets kit icon.").emptyLine()
        .click(DRAG_DROP, "replace")
        .click(RMB, "get a copy")
        .build();

    public static final EditorLocale KIT_COMMANDS = builder(PREFIX + "Kit.Commands")
        .name("Commands")
        .text("Commands listed below will be executed", "when player obtains the kit.").emptyLine()
        .currentHeader()
        .text(Placeholders.KIT_COMMANDS)
        .emptyLine()
        .text(YELLOW + BOLD + "Placeholders:")
        .current(Placeholders.PLAYER_NAME, "for player name.")
        .current(EngineUtils.PLACEHOLDER_API, "all of them")
        .emptyLine()
        .click(LMB, "add command")
        .click(RMB, "remove all")
        .build();

    public static final EditorLocale KIT_POTIONS = builder(PREFIX + "Kit.PotionEffects")
        .name("Potion Effects")
        .text("Permanent potion effects granted", "by using this kit.").emptyLine()
        .currentHeader()
        .text(Placeholders.KIT_POTION_EFFECTS)
        .emptyLine()
        .click(LMB, "add effect")
        .click(RMB, "remove all")
        .build();

    public static final EditorLocale KIT_ARMOR = builder(PREFIX + "Kit.Armor")
        .name("Armor")
        .text("Items for player's armor slots.").emptyLine()
        .text(RED + BOLD + "Item Order:")
        .text("Boots " + RED + "→" + GRAY + " Legs " + RED + "→" + GRAY + " Chest " + RED + "→" + GRAY + " Head " + RED + "→" + GRAY + " OffHand")
        .emptyLine()
        .click(LMB, "navigate")
        .build();

    public static final EditorLocale KIT_INVENTORY = builder(PREFIX + "Kit.Inventory")
        .name("Inventory")
        .text("Items for player's inventory.").emptyLine()
        .text(RED + BOLD + "Item Order:")
        .text(RED + "→" + GRAY + " 1st row: " + RED + "Hotbar")
        .text(RED + "→" + GRAY + " 2nd+ row: " + RED + "Inventory")
        .emptyLine()
        .click(LMB, "navigate")
        .build();

    public static final EditorLocale KIT_PERMISSION = builder(PREFIX + "Kit.PermissionRequirement")
        .name("Permission Requirement")
        .text("Sets whether or not permission", "is required to use this kit.").emptyLine()
        .currentHeader()
        .current("Enabled", Placeholders.KIT_IS_PERMISSION + GRAY + " (" + WHITE + LMB + GRAY + ")")
        .current("Node", Placeholders.KIT_PERMISSION)
        .build();

    public static final EditorLocale KIT_COST = builder(PREFIX + "Kit.Cost")
        .name("Cost & Currency")
        .text("Sets kit cost & currency.").emptyLine()
        .currentHeader()
        .current("Cost", Placeholders.KIT_COST + GRAY + " (" + WHITE + LMB + GRAY + ")")
        .current("Currency", Placeholders.KIT_CURRENCY + GRAY + " (" + WHITE + RMB + GRAY + ")")
        .emptyLine()
        .click(DROP_KEY, "make free")
        .build();

    public static final EditorLocale KIT_DEFAULT = builder(PREFIX + "Kit.Default")
        .name("Is Default")
        .text("Sets kit as default one.").emptyLine()
        .currentHeader()
        .current("Is Default", Placeholders.KIT_IS_DEFAULT + GRAY + " (" + WHITE + LMB + GRAY + ")")
        .build();

    public static final EditorLocale MOB_OBJECT = builder(PREFIX + "Mob.Object")
        .name(Placeholders.MOB_NAME + GRAY + " (" + WHITE + Placeholders.MOB_ID + GRAY + ")")
        .emptyLine()
        .click(LMB, "edit")
        .click(RMB, "delete " + RED + "(no undo)")
        .build();

    public static final EditorLocale MOB_CREATE = builder(PREFIX + "Mob.Create")
        .name("New Mob")
        .build();

    public static final EditorLocale MOB_NAME = builder(PREFIX + "Mob.DisplayName")
        .name("Display Name")
        .text("Name above mob's head.").emptyLine()
        .currentHeader()
        .current("Name", Placeholders.MOB_NAME + GRAY + " (" + WHITE + LMB + GRAY + ")")
        .current("Visible", Placeholders.MOB_NAME_VISIBLE + GRAY + " (" + WHITE + RMB + GRAY + ")")
        .build();

    public static final EditorLocale MOB_ENTITY_TYPE = builder(PREFIX + "Mob.EntityType")
        .name("Entity Type")
        .text("Sets mob entity type.").emptyLine()
        .currentHeader()
        .current("Type", Placeholders.MOB_ENTITY_TYPE + GRAY + " (" + WHITE + LMB + GRAY + ")")
        .build();

    public static final EditorLocale MOB_LEVEL = builder(PREFIX + "Mob.Levels")
        .name("Mob Levels")
        .text("Sets mob min. and max. levels.").emptyLine()
        .currentHeader()
        .current("Min", Placeholders.MOB_LEVEL_MIN + GRAY + " (" + WHITE + LMB + GRAY + ")")
        .current("Max", Placeholders.MOB_LEVEL_MAX + GRAY + " (" + WHITE + RMB + GRAY + ")")
        .build();

    public static final EditorLocale MOB_BOSSBAR = builder(PREFIX + "Mob.BossBar")
        .name("Boss Bar")
        .text("Mob health bossbar settings.").emptyLine()
        .currentHeader()
        .current("Enabled", Placeholders.MOB_BOSSBAR_ENABLED + GRAY + " (" + WHITE + LMB + GRAY + ")")
        .current("Color", Placeholders.MOB_BOSSBAR_COLOR + GRAY + " (" + WHITE + SHIFT_LMB + GRAY + ")")
        .current("Style", Placeholders.MOB_BOSSBAR_STYLE + GRAY + " (" + WHITE + SHIFT_RMB + GRAY + ")")
        .build();

    public static final EditorLocale MOB_ATTRIBUTES = builder(PREFIX + "Mob.Attributes")
        .name("Attributes")
        .text("Overrides mob's default and per-level attributes.")
        .emptyLine()
        .text(YELLOW + BOLD + "Base:")
        .text(Placeholders.MOB_ATTRIBUTES_BASE)
        .emptyLine()
        .text(YELLOW + BOLD + "Per Level:")
        .text(Placeholders.MOB_ATTRIBUTES_LEVEL)
        .emptyLine()
        .click(LMB, "edit base")
        .click(RMB, "edit per-level")
        .click(SHIFT_LMB, "remove all")
        .build();

    public static final EditorLocale MOB_EQUIPMENT = builder(PREFIX + "Mob.Equipment")
        .name("Equipment")
        .text("Items for mob's armor slots.").emptyLine()
        .text(RED + BOLD + "Item Order:")
        .text("Boots " + RED + "→" + GRAY + " Legs " + RED + "→" + GRAY + " Chest " + RED + "→" + GRAY + " Head " + RED + "→" + GRAY + " Hand " + RED + "→" + GRAY + "OffHand")
        .emptyLine()
        .click(LMB, "navigate")
        .build();

    public static final EditorLocale MOB_STYLES = builder(PREFIX + "Mob.Styles")
        .name("Styles")
        .text("Some extra settings based", "on mob's entity type.").emptyLine()
        .click(LMB, "navigate")
        .build();

    public static final EditorLocale MOB_STYLE_OBJECT = builder(PREFIX + "Mob.Style.Object")
        .name("Style Type: " + ORANGE + Placeholders.MOB_STYLE_TYPE)
        .currentHeader()
        .current("Value", Placeholders.MOB_STYLE_VALUE + GRAY + " (" + WHITE + LMB + GRAY + ")")
        .emptyLine()
        .click(RMB, "remove")
        .build();
}
