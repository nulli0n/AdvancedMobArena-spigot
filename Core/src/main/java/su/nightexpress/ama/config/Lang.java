package su.nightexpress.ama.config;

import org.bukkit.Sound;
import su.nexmedia.engine.api.lang.LangKey;
import su.nexmedia.engine.lang.EngineLang;
import su.nightexpress.ama.Placeholders;
import su.nightexpress.ama.currency.CurrencyManager;
import su.nightexpress.ama.hook.HookId;

import static su.nexmedia.engine.utils.Colors.*;
import static su.nightexpress.ama.Placeholders.*;

public class Lang extends EngineLang {

    public static final LangKey COMMAND_COINS_DESC  = LangKey.of("Command.Coins.Desc", "Arena coins management.");
    public static final LangKey COMMAND_COINS_USAGE = LangKey.of("Command.Coins.Usage", "[help]");

    public static final LangKey COMMAND_COINS_BALANCE_DESC        = LangKey.of("Command.Coins.Balance.Desc", "View [player's] coins balance.");
    public static final LangKey COMMAND_COINS_BALANCE_USAGE       = LangKey.of("Command.Coins.Balance.Usage", "[player]");
    public static final LangKey COMMAND_COINS_BALANCE_DONE_SELF   = LangKey.of("Command.Coins.Balance.Done.Self", LIGHT_YELLOW + "Your have: " + ORANGE + Placeholders.GENERIC_AMOUNT + LIGHT_YELLOW + ".");
    public static final LangKey COMMAND_COINS_BALANCE_DONE_OTHERS = LangKey.of("Command.Coins.Balance.Done.Others", ORANGE + Placeholders.PLAYER_NAME + LIGHT_YELLOW + "'s balance: " + ORANGE + Placeholders.GENERIC_AMOUNT + LIGHT_YELLOW + ".");

    public static final LangKey COMMAND_COINS_ADD_DESC       = LangKey.of("Command.Coins.Add.Desc", "Add coins to a player.");
    public static final LangKey COMMAND_COINS_ADD_GIVE_USAGE = LangKey.of("Command.Coins.Add.Usage", "<player> <amount>");
    public static final LangKey COMMAND_COINS_ADD_GIVE_DONE  = LangKey.of("Command.Coins.Add.Done", LIGHT_YELLOW + "Added " + GREEN + Placeholders.GENERIC_AMOUNT + " " + LIGHT_YELLOW + " to " + GREEN + Placeholders.PLAYER_NAME + LIGHT_YELLOW + "'s balance.");

    public static final LangKey COMMAND_COINS_REMOVE_DESC  = LangKey.of("Command.Coins.Remove.Desc", "Remove coins off a player.");
    public static final LangKey COMMAND_COINS_REMOVE_USAGE = LangKey.of("Command.Coins.Remove.Usage", "<player> <amount>");
    public static final LangKey COMMAND_COINS_REMOVE_DONE  = LangKey.of("Command.Coins.Remove.Done", LIGHT_YELLOW + "Removed " + RED + Placeholders.GENERIC_AMOUNT + " " + LIGHT_YELLOW + " from " + RED + Placeholders.PLAYER_NAME + LIGHT_YELLOW + "'s balance.");

    public static final LangKey COMMAND_COINS_SET_DESC  = LangKey.of("Command.Coins.Set.Desc", "Set player's coins amount.");
    public static final LangKey COMMAND_COINS_SET_USAGE = LangKey.of("Command.Coins.Set.Usage", "<player> <amount>");
    public static final LangKey COMMAND_COINS_SET_DONE  = LangKey.of("Command.Coins.Set.Done", LIGHT_YELLOW + "Set " + ORANGE + Placeholders.PLAYER_NAME + LIGHT_YELLOW + "'s coins balance to " + ORANGE + Placeholders.GENERIC_AMOUNT + LIGHT_YELLOW + ".");

    public static final LangKey COMMAND_EDITOR_DESC = LangKey.of("Command.Editor.Desc", "Open AMA Editor");

    public static final LangKey COMMAND_FORCE_END_DESC              = LangKey.of("Command.ForceEnd.Desc", "Force stop an arena.");
    public static final LangKey COMMAND_FORCE_END_USAGE             = LangKey.of("Command.ForceEnd.Usage", "<arena>");
    public static final LangKey COMMAND_FORCE_END_DONE              = LangKey.of("Command.ForceEnd.Done", LIGHT_YELLOW + "Forced arena " + RED + Placeholders.ARENA_NAME + LIGHT_YELLOW + " to end.");
    public static final LangKey COMMAND_FORCE_END_ERROR_NOT_IN_GAME = LangKey.of("Command.ForceEnd.Error.NotInGame", LIGHT_YELLOW + "There is no activity in the " + RED + Placeholders.ARENA_NAME + LIGHT_YELLOW + " arena.");

    public static final LangKey COMMAND_FORCE_START_DESC            = LangKey.of("Command.ForceStart.Desc", "Force start an arena.");
    public static final LangKey COMMAND_FORCE_START_USAGE           = LangKey.of("Command.ForceStart.Usage", "<arena>");
    public static final LangKey COMMAND_FORCE_START_DONE            = LangKey.of("Command.ForceStart.Done", LIGHT_YELLOW + "Forced arena " + GREEN + Placeholders.ARENA_NAME + LIGHT_YELLOW + " to start.");
    public static final LangKey COMMAND_FORCE_START_ERROR_NOT_READY = LangKey.of("Command.ForceStart.Error.NotReady", LIGHT_YELLOW + "Arena " + RED + Placeholders.ARENA_NAME + LIGHT_YELLOW + " currently can not be forced to start.");

    public static final LangKey COMMAND_HOLOGRAM_DESC         = new LangKey("Command.Hologram.Desc", "Arena hologram management.");
    public static final LangKey COMMAND_HOLOGRAM_USAGE        = new LangKey("Command.Hologram.Usage", "[help]");
    public static final LangKey COMMAND_HOLOGRAM_REMOVE_DESC  = new LangKey("Command.Hologram.Remove.Desc", "Removes nearest stats hologram.");
    public static final LangKey COMMAND_HOLOGRAM_REMOVE_USAGE = new LangKey("Command.Hologram.Remove.Usage", "<HologramType> [args]");
    public static final LangKey COMMAND_HOLOGRAM_REMOVE_DONE  = new LangKey("Command.Hologram.Remove.Done", "&7Removed all &a%type%&7 holograms!");
    public static final LangKey COMMAND_HOLOGRAM_ADD_DESC     = new LangKey("Command.Hologram.Add.Desc", "Creates a hologram for specified object.");
    public static final LangKey COMMAND_HOLOGRAM_ADD_USAGE    = new LangKey("Command.Hologram.Add.Usage", "<HologramType> [args]");
    public static final LangKey COMMAND_HOLOGRAM_ADD_DONE     = new LangKey("Command.Hologram.Add.Done", "&7Successfully created &a%type%&7 hologram!");
    public static final LangKey COMMAND_HOLOGRAM_ERROR        = new LangKey("Command.Hologram.Error", "&cCould not perform operation! (Invalid arguments)");

    public static final LangKey COMMAND_JOIN_DESC    = LangKey.of("Command.Join.Desc", "Join the arena.");
    public static final LangKey COMMAND_JOIN_USAGE   = LangKey.of("Command.Join.Usage", "[arena] [player]");
    public static final LangKey COMMAND_JOIN_NOTHING = LangKey.of("Command.Join.Nothing", RED + "There is no available arenas to join right now.");

    public static final LangKey Command_Region_Desc                      = new LangKey("Command.Region.Desc", "Manage in-game arena regions.");
    public static final LangKey Command_Region_Usage                     = new LangKey("Command.Region.Usage", "<lock|unlock>");
    public static final LangKey Command_Region_State_Done                = new LangKey("Command.Region.State.Done", "Region &a%region%'s &7lock state to &a%state%&7!");
    public static final LangKey Command_Region_State_Error_NotInGame     = new LangKey("Command.Region.State.Error.NotInGame", "&cYou must be in-game to do that!");
    public static final LangKey Command_Region_State_Error_InvalidRegion = new LangKey("Command.Region.State.Error.InvalidRegion", "&cInvalid region id!");

    public static final LangKey COMMAND_SETACTIVE_DESC              = new LangKey("Command.SetActive.Desc", "Set arena active state.");
    public static final LangKey COMMAND_SETACTIVE_USAGE             = new LangKey("Command.SetActive.Usage", "<arena> <true/false>");
    public static final LangKey COMMAND_SETACTIVE_DONE              = new LangKey("Command.SetActive.Done", "Arena &a%arena_id%&7 active state: &f%state%&7.");

    public static final LangKey COMMAND_SCORE_DESC  = LangKey.of("Command.Score.Desc", "Manage score of arena players.");
    public static final LangKey COMMAND_SCORE_USAGE = LangKey.of("Command.Score.Usage", "[help]");

    public static final LangKey COMMAND_SCORE_ADD_DESC = LangKey.of("Command.Score.Add.Desc", "Add score to a player.");
    public static final LangKey COMMAND_SCORE_ADD_USAGE = LangKey.of("Command.Score.Add.Usage", "<player> <amount>");
    public static final LangKey COMMAND_SCORE_ADD_DONE  = LangKey.of("Command.Score.Add.Done", LIGHT_YELLOW + "Added " + ORANGE + GENERIC_AMOUNT + LIGHT_YELLOW + " to " + ORANGE + PLAYER_DISPLAY_NAME + LIGHT_YELLOW + "'s score!");

    public static final LangKey COMMAND_SCORE_REMOVE_DESC = LangKey.of("Command.Score.Remove.Desc", "Remove score from a player.");
    public static final LangKey COMMAND_SCORE_REMOVE_USAGE = LangKey.of("Command.Score.Remove.Usage", "<player> <amount>");
    public static final LangKey COMMAND_SCORE_REMOVE_DONE  = LangKey.of("Command.Score.Remove.Done", LIGHT_YELLOW + "Removed " + ORANGE + GENERIC_AMOUNT + LIGHT_YELLOW + " from " + ORANGE + PLAYER_DISPLAY_NAME + LIGHT_YELLOW + "'s score!");

    public static final LangKey COMMAND_SCORE_SET_DESC = LangKey.of("Command.Score.Set.Desc", "Set player's score.");
    public static final LangKey COMMAND_SCORE_SET_USAGE = LangKey.of("Command.Score.Set.Usage", "<player> <amount>");
    public static final LangKey COMMAND_SCORE_SET_DONE  = LangKey.of("Command.Score.Set.Done", LIGHT_YELLOW + "Set " + ORANGE + PLAYER_DISPLAY_NAME + LIGHT_YELLOW + "'s score to " + ORANGE + GENERIC_AMOUNT + LIGHT_YELLOW + "!");

    public static final LangKey Command_Spot_Desc                     = new LangKey("Command.Spot.Desc", "Manage in-game arena spots.");
    public static final LangKey Command_Spot_Usage                    = new LangKey("Command.Spot.Usage", "[state]");
    public static final LangKey Command_Spot_State_Done               = new LangKey("Command.Spot.State.Done", "Changed &a%spot%'s &7state to &a%state%&7!");
    public static final LangKey Command_Spot_State_Error_NotInGame    = new LangKey("Command.Spot.State.Error.NotInGame", "&cYou must be in-game to do that!");
    public static final LangKey Command_Spot_State_Error_InvalidSpot  = new LangKey("Command.Spot.State.Error.InvalidSpot", "&cInvalid spot id!");
    public static final LangKey Command_Spot_State_Error_InvalidState = new LangKey("Command.Spot.State.Error.InvalidState", "&cInvalid spot state id!");

    public static final LangKey COMMAND_LEAVE_DESC = LangKey.of("Command.Leave.Desc", "Leave the arena.");

    public static final LangKey Command_List_Desc = new LangKey("Command.List.Desc", "Shows arenas list.");

    public static final LangKey COMMAND_SKIP_ROUND_DESC  = LangKey.of("Command.SkipRound.Desc", "Skips arena round(s).");
    public static final LangKey COMMAND_SKIP_ROUND_USAGE = LangKey.of("Command.SkipRound.Usage", "[arena] [amount]");
    public static final LangKey COMMAND_SKIP_ROUND_DONE = LangKey.of("Command.SkipRound.Done", LIGHT_YELLOW + "Set " + ORANGE + GENERIC_AMOUNT + LIGHT_YELLOW + " rounds to skip for " + ORANGE + ARENA_NAME + LIGHT_YELLOW + ".");
    public static final LangKey COMMAND_SKIP_ROUND_ERROR_NOT_IN_GAME = LangKey.of("Command.SkipRound.Error.NotInGame", RED + "Can not skip rounds: " + LIGHT_YELLOW + "Arena is not in game or is about to end.");

    public static final LangKey COMMAND_SPECTATE_DESC  = LangKey.of("Command.Spectate.Desc", "Spectate an arena.");
    public static final LangKey COMMAND_SPECTATE_USAGE = LangKey.of("Command.Spectate.Usage", "<arena>");

    public static final LangKey COMMAND_SHOP_DESC  = LangKey.of("Command.Shop.Desc", "Open arena shop.");
    public static final LangKey COMMAND_STATS_DESC = new LangKey("Command.Stats.Desc", "View your current stats.");

    public static final LangKey ERROR_CURRENCY_INVALID = LangKey.of("Error.Currency.Invalid", "&cInvalid currency!");

    public static final LangKey ARENA_ERROR_DISABLED = new LangKey("Arena.Error.Disabled", "Arena &c%arena_name% &7is disabled.");
    public static final LangKey ARENA_ERROR_INVALID  = LangKey.of("Arena.Error.Invalid", RED + "Arena does not exists.");

    public static final LangKey ARENA_JOIN_ERROR_PERMISSION = new LangKey("Arena.Join.Error.Permission", "&cYou don't have permission to join this arena!");
    public static final LangKey ARENA_JOIN_ERROR_PAYMENT    = new LangKey("Arena.Join.Error.Payment", "&cYou must have &e%arena_requirement_payment% &cto join the arena!");
    public static final LangKey ARENA_JOIN_ERROR_COOLDOWN    = new LangKey("Arena.Join.Error.Cooldown", RED + "You can join this arena again in " + ORANGE + Placeholders.GENERIC_TIME + RED + "!");
    public static final LangKey ARENA_JOIN_ERROR_LEVEL    = new LangKey("Arena.Join.Error.Level", "&cYour level is not suitable to join the arena!");
    public static final LangKey ARENA_JOIN_ERROR_IN_GAME    = new LangKey("Arena.Join.Error.InGame", "&cYou are already in game!");
    public static final LangKey ARENA_JOIN_ERROR_MAXIMUM    = new LangKey("Arena.Join.Error.Maximum", "&cThere is maximum players on the arena.");
    public static final LangKey ARENA_JOIN_ERROR_STARTED    = new LangKey("Arena.Join.Error.Started", "Arena &a%arena_name% &7is already in game. You can not join now.");
    public static final LangKey ARENA_JOIN_ERROR_ENDING    = LangKey.of("Arena.Join.Error.Ending", "Arena " + RED + Placeholders.ARENA_NAME + GRAY + " is about to end. Try again in a few seconds.");
    public static final LangKey ARENA_JOIN_ERROR_NO_KIT    = LangKey.of("Arena.Join.Error.NoKit", "You don't have any kit to play on this arena.");
    public static final LangKey ARENA_JOIN_ERROR_NO_REGION    = LangKey.of("Arena.Join.Error.NoRegion", "No regions are available to play on.");

    public static final LangKey ARENA_JOIN_SPECTATE_SUCCESS        = new LangKey("Arena.Join.Spectate.Success", "Now you are spectating arena &a%arena_name%");
    public static final LangKey ARENA_JOIN_SPECTATE_ERROR_DISABLED = new LangKey("Arena.Join.Spectate.Error.Disabled", "Spectating is disabled on this arena.");
    public static final LangKey ARENA_SPECTATE_ERROR_NOTHING = new LangKey("Arena.Spectate.Error.Nothing", "There is nothing to spectate for at the moment.");

    public static final LangKey ARENA_SCHEDULER_OPEN_ANNOUNCE = LangKey.of("Arena.Scheduler.Open.Announce", "&7Arena &a" + Placeholders.ARENA_NAME + "&7 is opened for play!");
    public static final LangKey ARENA_SCHEDULER_CLOSE_ANNOUNCE = LangKey.of("Arena.Scheduler.Close.Announce", "&7Arena &c" + Placeholders.ARENA_NAME + "&7 have been closed!");

    public static final LangKey Arena_Game_Notify_Start  = new LangKey("Arena.Game.Notify.Start", "<! type:\"titles:10:50:10\" sound:\"" + Sound.ENTITY_ENDERMAN_TELEPORT.name() + "\" !>" + "&a&lYou have joined the arena! \n &2&lPickup your weapons and fight!");
    public static final LangKey ARENA_GAME_NOTIFY_REWARD = new LangKey("Arena.Game.Notify.Reward", "You recieved reward: &a" + Placeholders.REWARD_NAME);

    public static final LangKey Arena_Game_Announce_End   = new LangKey("Arena.Game.Announce.End", "Game on the arena &a%arena_name% &7has ended. Type &a/ama join &a%arena_id% &7to play!");
    public static final LangKey Arena_Game_Announce_Start = new LangKey("Arena.Game.Announce.Start", "Arena &a%arena_name% &7will start in &2%time% &7seconds. Type &a/ama join &a%arena_id% &7to join!");

    public static final LangKey ARENA_GAME_RESTRICT_COMMANDS = LangKey.of("Arena.Game.Restrict.Commands", RED + "You can not use this command while on arena!");
    public static final LangKey Arena_Game_Restrict_Kits     = new LangKey("Arena.Game.Restrict.Kits", "Kits are disabled on this arena.");
    public static final LangKey Arena_Game_Restrict_NoPets   = new LangKey("Arena.Game.Restrict.NoPets", "Pets are not allowed on this arena. Your pet has gone.");
    public static final LangKey ARENA_GAME_ERROR_NOT_IN_GAME = new LangKey("Arena.Game.Error.NotInGame", "You are not in game!");

    public static final LangKey ARENA_ERROR_PLAYER_NOT_IN_GAME = LangKey.of("Arena.Error.PlayerNotInGame", RED + "This player is not in game!");

    public static final LangKey ARENA_GAME_LEAVE_INFO = LangKey.of("Arena.Game.Leave.Info", "You has left the arena.");

    public static final LangKey ARENA_LOBBY_JOIN = LangKey.of("Arena.Game.Lobby.Enter",
        "<! type:\"titles:20:60:20\" !>" +
            "\n" + GREEN + BOLD + "Welcome, Mobfighter!" +
            "\n" + GRAY + "Prepare your equipment.");

    public static final LangKey ARENA_LOBBY_COUNTDOWN = LangKey.of("Arena.Game.Lobby.Timer",
        "<! type:\"titles:10:40:10\" sound:\"" + Sound.BLOCK_NOTE_BLOCK_PLING.name() + "\" !>" +
            "\n" + YELLOW + BOLD + "Prepare to Fight!" +
            "\n" + GRAY + "We start in " + YELLOW + GENERIC_TIME + GRAY + " seconds!");

    public static final LangKey ARENA_LOBBY_MIN_PLAYERS = LangKey.of("Arena.Game.Lobby.MinPlayers",
        GRAY + "We need " + RED + GENERIC_AMOUNT + GRAY + " more players to start!");

    public static final LangKey ARENA_LOBBY_PLAYER_JOINED = LangKey.of("Arena.Game.Lobby.Joined",
        "<! prefix:\"false\" !>" +
        GREEN + PLAYER_DISPLAY_NAME + GRAY + " joined the arena.");

    public static final LangKey ARENA_GAME_DEATH_WITH_LIFES = LangKey.of("Arena.Game.Death.WithLifes",
        "<! type:\"titles:10:60:10\" sound:\"" + Sound.ENTITY_ZOMBIE_DEATH.name() + "\" !>" +
            "\n" + RED + "&lYou Died!" +
            "\n" + GRAY + "You have " + RED + PLAYER_LIVES + "❤" + GRAY + " extra lifes!");

    public static final LangKey ARENA_GAME_DEATH_NO_LIFES = LangKey.of("Arena.Game.Death.NoLifes",
        "<! type:\"titles:10:60:10\" sound:\"" + Sound.ENTITY_ZOMBIE_DEATH.name() + "\" !>" +
            "\n" + RED + BOLD + "You Died!" +
            "\n" + GRAY + "You don't have extra lifes. Leave: " + RED + "/ama leave");

    public static final LangKey ARENA_GAME_REVIVE_WITH_LIFES = LangKey.of("Arena.Game.Revive.WithLifes",
        "<! type:\"titles:10:60:10\" sound:\"" + Sound.ITEM_TOTEM_USE.name() + "\" !>" +
            "\n" + GREEN + BOLD + "You've been revived!" +
            "\n" + GRAY + "You have " + GREEN + PLAYER_LIVES + "❤" + GRAY + " extra lifes!");

    public static final LangKey ARENA_GAME_REVIVE_NO_LIFES = LangKey.of("Arena.Game.Revive.NoLifes",
        "<! type:\"titles:10:60:10\" sound:\"" + Sound.ITEM_TOTEM_USE.name() + "\" !>" +
            "\n" + GREEN + BOLD + "You've been revived!" +
            "\n" + GRAY + "This is your " + RED + "last" + GRAY + " chance!");

    public static final LangKey ARENA_GAME_INFO_PLAYER_READY = LangKey.of("Arena.Game.Info.Player.Ready",
        GREEN + PLAYER_NAME + GRAY + " is ready to play!");

    public static final LangKey ARENA_GAME_INFO_PLAYER_NOT_READY = LangKey.of("Arena.Game.Info.Player.NotReady",
        RED + PLAYER_NAME + GRAY + " is not ready to play.");

    public static final LangKey ARENA_GAME_INFO_PLAYER_DEATH = LangKey.of("Arena.Game.Info.Player.Death",
        RED + PLAYER_NAME + GRAY + " died!");

    public static final LangKey ARENA_GAME_END_ALL_DEAD = LangKey.of("Arena.Game.End.AllDead",
        "<! type:\"titles:10:100:10\" sound:\"" + Sound.ENTITY_BLAZE_DEATH.name() + "\" !>" +
            "\n" + RED + BOLD + "Defeat" +
            "\n" + GRAY + "All players died...");

    public static final LangKey ARENA_GAME_END_TIMEOUT = LangKey.of("Arena.Game.End.Timeout",
        "<! type:\"titles:10:100:10\" sound:\"" + Sound.ENTITY_BLAZE_DEATH.name() + "\" !>" +
            "\n" + RED + BOLD + "Time Out" +
            "\n" + GRAY + "You were unable to beat them in a time...");

    public static final LangKey ARENA_GAME_END_COMPLETED = LangKey.of("Arena.Game.End.Completed",
        "<! type:\"titles:10:100:10\" sound:\"" + Sound.ENTITY_PLAYER_LEVELUP.name() + "\" !>" +
            "\n" + GREEN + BOLD + "Victory!" +
            "\n" + GRAY + "You've passed all the waves!");

    public static final LangKey ARENA_GAME_STATUS_SPECTATE = LangKey.of("Arena.Game.Status.Spectate",
        "<! type:\"action_bar\" !>" + GRAY + "You're spectating the arena. Type " + LIME + "/ama leave" + GRAY + " to leave.");

    public static final LangKey ARENA_GAME_STATUS_DEAD_WITH_LIFES = LangKey.of("Arena.Game.Status.Dead.WithLifes",
        "<! type:\"action_bar\" !>" + RED + "You're dead. You will be revived at the round end.");

    public static final LangKey ARENA_GAME_STATUS_DEAD_NO_LIFES = LangKey.of("Arena.Game.Status.Dead.NoLifes",
        "<! type:\"action_bar\" !>" + RED + "You're dead. You don't have extra lifes and can spectate only.");

    public static final LangKey ARENA_GAME_STATUS_ROUND_PREPARE = LangKey.of("Arena.Game.Status.Round.Prepare",
        "<! type:\"action_bar\" !>&6&lNew round in: &e&l%arena_wave_next_in% &6&lseconds!");

    public static final LangKey ARENA_GAME_STATUS_ROUND_ACTIVE  = LangKey.of("Arena.Game.Status.Round.Active",
        "<! type:\"action_bar\" !>&b[Mobs] &aAlive: &2" + Placeholders.ARENA_MOBS_ALIVE + " &7| &eLeft: &6" + Placeholders.ARENA_MOBS_LEFT);

    public static final LangKey ARENA_GAME_STATUS_ENDING = LangKey.of("Arena.Game.Status.Ending",
        "<! type:\"action_bar\" !>" + CYAN + "Game ends in " + GRAY + Placeholders.ARENA_END_COUNTDOWN + CYAN + " seconds.");

    public static final LangKey Arena_Game_Wave_Start    = new LangKey("Arena.Game.Wave.Start", "<! type:\"titles:10:50:10\" !>" + "&6&lWave &e&l#%arena_wave_number% \n &4&lPrepare to fight!");

    public static final LangKey ARENA_REGION_UNLOCKED_NOTIFY = new LangKey("Arena.Region.Unlocked.Notify", "<! type:\"titles:0:30:10\" sound: " + Sound.BLOCK_NOTE_BLOCK_BELL.name() + "!>&a&lRegion Unlocked!\n&f" + Placeholders.REGION_NAME);
    public static final LangKey ARENA_REGION_LOCKED_NOTIFY   = new LangKey("Arena.Region.Locked.Notify", "<! type:\"titles:0:30:10\" !>" + "&c&lNew Region!\n&4&lFollow to the next arena region!");

    public static final LangKey Kit_Buy_Error_NoMoney         = new LangKey("Kit.Buy.Error.NoMoney", "You can't afford &c%kit_name% &7kit!");
    public static final LangKey Kit_Buy_Success               = new LangKey("Kit.Buy.Success", "You successfully bought the &a%kit_name% &7kit for &a%kit_cost%&7!");
    public static final LangKey Kit_Buy_Error_NoPermission    = new LangKey("Kit.Buy.Error.NoPermission", "&cYou don't have permission to purchase this kit!");
    public static final LangKey Kit_Select_Error_NoPermission = new LangKey("Kit.Select.Error.NoPermission", "&cYou don't have permission to use this kit!");
    public static final LangKey Kit_Select_Error_Disabled     = new LangKey("Kit.Select.Error.Disabled", "This kit is disabled in this arena.");
    public static final LangKey Kit_Select_Error_NotObtained  = new LangKey("Kit.Select.Error.NotObtained", "You don't have this kit!");
    public static final LangKey Kit_Select_Error_Limit        = new LangKey("Kit.Select.Error.Limit", "You can not use this kit, because there is already enough players with it.");
    public static final LangKey Kit_Select_Success            = new LangKey("Kit.Select.Success", "You choosen &a%kit_name%&7 as your kit.");

    public static final LangKey SHOP_OPEN_ERROR_LOCKED              = LangKey.of("Shop.Open.Error.Locked", "&cShop is not available yet!");
    public static final LangKey SHOP_OPEN_ERROR_DISABLED        = LangKey.of("Shop.Open.Error.Disabled", "&cShop is disabled on this arena.");
    public static final LangKey SHOP_CATEGORY_OPEN_ERROR_LOCKED = LangKey.of("Shop.Category.Open.Error.Locked", "&cSorry, but this category is still locked!");
    public static final LangKey SHOP_CATEGORY_OPEN_ERROR_UNAVAILABLE = LangKey.of("Shop.Category.Open.Error.Unavailable", "&cSorry, but you can't use this category!");
    public static final LangKey SHOP_PRODUCT_PURCHASE                = LangKey.of("Shop.Product.Purchase", "You purchased &a" + Placeholders.SHOP_PRODUCT_NAME + " &7for &a" + Placeholders.SHOP_PRODUCT_PRICE + "&7!");
    public static final LangKey SHOP_PRODUCT_ERROR_LOCKED            = LangKey.of("Shop.Product.Error.Locked", "&cSorry, but this product is still locked!");
    public static final LangKey SHOP_PRODUCT_ERROR_UNAVAILABLE = LangKey.of("Shop.Product.Error.Unavailable", "&cSorry, but you can't use this product!");
    public static final LangKey SHOP_PRODUCT_ERROR_NOT_ENOUGH_FUNDS = LangKey.of("Shop.Product.Error.NotEnoughFunds", "&cYou need &e" + Placeholders.SHOP_PRODUCT_PRICE + "&c to purchase " + Placeholders.SHOP_PRODUCT_NAME + "&c!");

    public static final LangKey EDITOR_GENERIC_ENTER_CURRENCY    = LangKey.of("Editor.Generic.Enter.Currency", "&7Enter &a[Currency]");
    public static final LangKey EDITOR_GENERIC_ENTER_NAME        = LangKey.of("Editor.Generic.Enter.Name", "&7Enter &a[Display Name]");
    public static final LangKey EDITOR_GENERIC_ENTER_DESCRIPTION = LangKey.of("Editor.Generic.Enter.Description", "&7Enter &a[Description]");
    public static final LangKey EDITOR_GENERIC_ENTER_COMMAND     = LangKey.of("Editor.Generic.Enter.Command", "&7Enter &a[Command]");
    public static final LangKey EDITOR_GENERIC_ENTER_NUMBER      = LangKey.of("Editor.Generic.Enter.Number", "&7Enter &a[Number]");
    public static final LangKey EDITOR_GENERIC_ENTER_PERCENT     = LangKey.of("Editor.Generic.Enter.Percent", "&7Enter &a[Percent Amount]");
    public static final LangKey EDITOR_GENERIC_ENTER_SECONDS     = LangKey.of("Editor.Generic.Enter.Seconds", "&7Enter &a[Seconds Amount]");
    public static final LangKey EDITOR_GENERIC_ENTER_PRIORITY     = LangKey.of("Editor.Generic.Enter.Priority", "&7Enter &a[Priority]");
    public static final LangKey EDITOR_GENERIC_ENTER_EVENT_TYPE        = LangKey.of("Editor.Generic.Enter.EventType", GRAY + "Enter " + GREEN + "[Event Type]");

    public static final LangKey EDITOR_ARENA_ENTER_ID             = LangKey.of("Editor.Arena.Enter.Id", "&7Enter &a[Arena Identifier]");
    public static final LangKey EDITOR_ARENA_ENTER_SCHEDULER_TIME = LangKey.of("Editor.Arena.Enter.SchedulerTime", "&7Enter &a[Day] [Time] &7| &aMonday 15:30");
    public static final LangKey EDITOR_ARENA_ENTER_JOIN_PAYMENT   = LangKey.of("Editor.Arena.Enter.JoinPayment", "&7Enter &a[Currency] [Amount] &7| &a" + CurrencyManager.COINS + " 500");
    public static final LangKey EDITOR_ARENA_ENTER_JOIN_LEVEL     = LangKey.of("Editor.Arena.Enter.JoinLevel", "&7Enter &a[Plugin] [Value] &7| &a" + HookId.MMOCORE + " 10");
    public static final LangKey EDITOR_ARENA_ERROR_EXISTS         = LangKey.of("Editor.Arena.Error.Exists", "&cArena is already exists!");
    public static final LangKey EDITOR_ARENA_ERROR_LEVEL_PROVIDER = LangKey.of("Editor.Arena.Error.Level_Provider", "&cSuch plugin is not supported!");
    public static final LangKey EDITOR_ARENA_ERROR_SCHEDULER_TIME = LangKey.of("Editor.Arena.Error.SchedulerTime", "&cInvalid day/time!");

    public static final LangKey EDITOR_ARENA_GAMEPLAY_ENTER_SCOREBOARD_ID        = LangKey.of("Editor.Arena.Gameplay.Enter.ScoreboardId", "&7Enter &a[Scoreboard Id]");
    public static final LangKey EDITOR_ARENA_GAMEPLAY_ENTER_BANNED_ITEMS = LangKey.of("Editor.Arena.Gameplay.Enter.BannedItem", "&7Enter &a[Material Name]");
    public static final LangKey EDITOR_ARENA_GAMEPLAY_ENTER_SPAWN_REASON = LangKey.of("Editor.Arena.Gameplay.Enter.AllowedSpawnReason", "&7Enter &a[Spawn Reason]");
    public static final LangKey EDITOR_ARENA_GAMEPLAY_ENTER_KIT_LIMIT    = LangKey.of("Editor.Arena.Gameplay.Enter.KitLimit", GRAY + "Enter " + GREEN + "[Kit] [Amount]");

    public static final LangKey EDITOR_ARENA_WAVES_ENTER_WAVE_ID     = LangKey.of("Editor.Arena.Waves.Enter.WaveId", "&7Enter &a[Wave Identifier]");
    public static final LangKey EDITOR_ARENA_WAVES_ENTER_MOB_ID      = LangKey.of("Editor.Arena.Waves.Enter.MobId", "&7Enter &a[Mob Identifier]");
    public static final LangKey EDITOR_ARENA_WAVES_ERROR_WAVE_EXISTS = LangKey.of("Editor.Arena.Waves.Error.Wave_Exists", "&сWave is already exists!");
    public static final LangKey EDITOR_ARENA_WAVES_ERROR_MOB_INVALID = LangKey.of("Editor.Arena.Waves.Error.InvalidMob", "&cMob not found! Wrong Mob Provider?");

    public static final LangKey EDITOR_ARENA_SHOP_ENTER_PRODUCT_ID      = LangKey.of("Editor.Arena.Shop.Enter.ProductId", "&7Enter &a[Product Identifier]");
    public static final LangKey EDITOR_ARENA_SHOP_ENTER_CATEGORY_ID     = LangKey.of("Editor.Arena.Shop.Enter.CategoryId", "&7Enter &a[Category Identifier]");
    public static final LangKey EDITOR_ARENA_SHOP_ENTER_PRODUCT_PRICE   = LangKey.of("Editor.Arena.Shop.Enter.Price", "&7Enter &a[Price]");
    public static final LangKey EDITOR_ARENA_SHOP_ERROR_PRODUCT_EXISTS  = LangKey.of("Editor.Arena.Shop.Error.Product_Exists", "&cProduct is already exists!");
    public static final LangKey EDITOR_ARENA_SHOP_ERROR_CATEGORY_EXISTS = LangKey.of("Editor.Arena.Shop.Error.Category_Exists", "&cCategory is already exists!");

    public static final LangKey EDITOR_ARENA_SCRIPT_ENTER_CATEGORY          = LangKey.of("Editor.Arena.Script.Enter.Category", "&7Enter &a[Category Name]");
    public static final LangKey EDITOR_ARENA_SCRIPT_ENTER_SCRIPT            = LangKey.of("Editor.Arena.Script.Enter.Script", "&7Enter &a[Script Name]");
    public static final LangKey EDITOR_ARENA_SCRIPT_ENTER_ACTION_NAME       = LangKey.of("Editor.Arena.Script.Enter.Action.Name", "&7Enter &a[Action Name]");
    public static final LangKey EDITOR_ARENA_SCRIPT_ENTER_PARAMETER_VALUE  = LangKey.of("Editor.Arena.Script.Enter.Action.ParameterValue", GRAY + "Enter " + GREEN + "[Value]");
    public static final LangKey EDITOR_ARENA_SCRIPT_ENTER_CONDITION_SECTION = LangKey.of("Editor.Arena.Script.Enter.Condition.Section", "&7Enter &a[Section Name]");
    public static final LangKey EDITOR_ARENA_SCRIPT_ENTER_CONDITION_VALUE   = LangKey.of("Editor.Arena.Script.Enter.Condition.Value", "&7Enter &a[Condition] [Operator] [Value]");
    public static final LangKey EDITOR_ARENA_SCRIPT_ERROR_INVALID_ACTION    = LangKey.of("Editor.Arena.Script.Error.InvalidAction", "&cInvalid action!");
    public static final LangKey EDITOR_ARENA_SCRIPT_ERROR_INVALID_CONDITION = LangKey.of("Editor.Arena.Script.Error.InvalidCondition", "&cInvalid condition!");
    public static final LangKey EDITOR_ARENA_SCRIPT_ERROR_INVALID_INPUT     = LangKey.of("Editor.Arena.Script.Error.InvalidInput", "&cInvalid input!");

    public static final LangKey EDITOR_REGION_ENTER_ID     = LangKey.of("Editor.Region.Enter.Id", "&7Enter &a[Region Identifier]");
    public static final LangKey EDITOR_REGION_ENTER_GROUP_ID     = LangKey.of("Editor.Region.Enter.GroupId", "&7Enter unique &a[Group Identifier]");
    public static final LangKey EDITOR_REGION_ERROR_EXISTS = LangKey.of("Editor.Region.Error.Exists", "&cRegion is already exists!");

    public static final LangKey EDITOR_SUPPLY_CHEST_ENTER_ID      = LangKey.of("Editor.SupplyChest.Enter.Id", "&7Enter &a[Chest Identifier]");
    public static final LangKey EDITOR_SUPPLY_CHEST_SET_CONTAINER = LangKey.of("Editor.SupplyChest.SetContainer", "&7Click on &a[Container]");
    public static final LangKey EDITOR_SUPPLY_CHEST_ERROR_EXISTS  = LangKey.of("Editor.SupplyChest.Error.Exists", "&cSupply chest is already exists!");

    public static final LangKey EDITOR_REWARD_ENTER_ID    = LangKey.of("Editor.Reward.Enter.Id", "&7Enter &a[Reward Identifier]");
    public static final LangKey EDITOR_REWARD_ERROR_EXIST = LangKey.of("Editor.Reward.Error.Exist", "&cReward is already exists!");

    public static final LangKey EDITOR_SPOT_ENTER_ID     = LangKey.of("Editor.Spot.Enter.Id", "&7Enter &a[Spot Identifier]");
    public static final LangKey EDITOR_SPOT_ERROR_EXISTS = LangKey.of("Editor.Spot.Error.Exists", "&cSpot is already exists!");

    public static final LangKey EDITOR_SPOT_STATE_ENTER_ID        = LangKey.of("Editor.Spot.State.Enter.Id", "&7Enter &a[State Identifier]");
    public static final LangKey EDITOR_SPOT_STATE_ERROR_EXISTS    = LangKey.of("Editor.Spot.State.Error.Exists", "&cState is already exists!");
    public static final LangKey EDITOR_SPOT_STATE_ERROR_NO_CUBOID = LangKey.of("Editor.Spot.State.Error.NoCuboid", "&cYou must set a spot cuboid!");

    public static final LangKey EDITOR_KIT_ENTER_ID     = LangKey.of("Editor.Kit.Enter.Create", GRAY + "Enter " + GREEN + "[Kit Identifier]");
    public static final LangKey EDITOR_KIT_ENTER_EFFECT = LangKey.of("Editor.Kit.Enter.Effect", GRAY + "Enter " + GREEN + "[Effect] [Level]");
    public static final LangKey EDITOR_KIT_ENTER_COST   = LangKey.of("Editor.Kit.Enter.Cost", GRAY + "Enter " + GREEN + "[Cost]");
    public static final LangKey EDITOR_KIT_ERROR_EXIST  = LangKey.of("Editor.Kit.Error.Exist", RED + "Kit already exists!");

    public static final LangKey Editor_Mob_Enter_Create        = new LangKey("Editor.Mob.Enter.Create", "&7Enter &aunique &7mob &aidentifier&7...");
    public static final LangKey Editor_Mob_Enter_Type          = new LangKey("Editor.Mob.Enter.Type", "&7Enter &aentity type&7...");
    public static final LangKey Editor_Mob_Enter_Level         = new LangKey("Editor.Mob.Enter.Level", "&7Enter &alevel&7 value...");
    public static final LangKey Editor_Mob_Enter_BossBar_Title = new LangKey("Editor.Mob.Enter.BossBar.Title", "&7Enter bossbar &atitle&7...");
    public static final LangKey Editor_Mob_Enter_Attribute     = new LangKey("Editor.Mob.Enter.Attribute", "&7Enter &aattribute &7and &avalue&7...");
    public static final LangKey EDITOR_MOB_ENTER_STYLE         = new LangKey("Editor.Mob.Enter.Style", "&7Enter &avalue&7...");
    public static final LangKey Editor_Mob_Error_Exist         = new LangKey("Editor.Mob.Error.Exist", "&cMob already exists!");

    public static final LangKey Setup_Arena_Lobby_Set    = new LangKey("Setup.Arena.Lobby.Set", "&7Defined lobby location for &a%arena_id% &7arena!");
    public static final LangKey Setup_Arena_Leave_Set    = new LangKey("Setup.Arena.Leave.Set", "&7Defined leave location for &a%arena_id% &7arena!");
    public static final LangKey Setup_Arena_Leave_UnSet  = new LangKey("Setup.Arena.Leave.Unset", "&7Undefined leave location for &a%arena_id% &7arena!");
    public static final LangKey Setup_Arena_Spectate_Set = new LangKey("Setup.Arena.Spectate.Set", "&7Defined spectate location for &a%arena_id% &7arena!");
    public static final LangKey Setup_Arena_Cuboid_Set           = new LangKey("Setup.Arena.Cuboid.Set", "&7Set &a#%corner% &7corner for the &a%arena_id% &7arena!");

    public static final LangKey SETUP_REGION_ERROR_ENABLED = LangKey.of("Setup.Region.Error.Enabled", RED + "You must disable the region first!");
    public static final LangKey Setup_Region_Spawn_Set     = new LangKey("Setup.Region.Spawn.Set", "&7Defined spawn location for &a%region_id% &7region!");
    public static final LangKey Setup_Region_Spawner_Add          = new LangKey("Setup.Region.Spawner.Add", "&7Added spawner to &a%region_id% &7region!");
    public static final LangKey Setup_Region_Spawner_Remove       = new LangKey("Setup.Region.Spawner.Remove", "&7Removed spawner from &a%region_id% &7region!");
    public static final LangKey Setup_Region_Cuboid_Error_Overlap = new LangKey("Setup.Region.Cuboid.Error.Overlap", "&7This position is overlaps with &c%region_id% &7arena region!");
    public static final LangKey Setup_Region_Cuboid_Set           = new LangKey("Setup.Region.Cuboid.Set", "&7Set &a#%corner% &7corner for the &a%region_id% &7region!");
    public static final LangKey Setup_Region_Error_Outside        = new LangKey("Setup.Region.Error.Outside", "&cLocation is outside of the editing region!");

    public static final LangKey SETUP_SPOT_CUBOID_SET           = LangKey.of("Setup.Spot.Cuboid.Set", "&7Set &a#%corner% &7corner for the &a" + Placeholders.SPOT_ID + " &7spot!");
    public static final LangKey SETUP_SPOT_CUBOID_ERROR_OVERLAP = LangKey.of("Setup.Spot.Cuboid.Error.Overlap", "&cThis location overlaps with &e" + Placeholders.SPOT_ID + " &cspot!");
    public static final LangKey SETUP_SPOT_STATE_ERROR_OUTSIDE  = LangKey.of("Setup.Spot.State.Error.Outside", "&cLocation is outside of the editing spot!");
    public static final LangKey SETUP_SPOT_STATE_LOADED  = LangKey.of("Setup.Spot.State.Loaded", "State loaded!");

}
