package su.nightexpress.ama.config;

import org.bukkit.Sound;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.lang.LangKey;
import su.nexmedia.engine.lang.EngineLang;
import su.nightexpress.ama.Placeholders;
import su.nightexpress.ama.api.arena.type.LeaveReason;
import su.nightexpress.ama.hook.HookId;

public class Lang extends EngineLang {

    @NotNull
    public static LangKey getLeaveReason(@NotNull LeaveReason reason) {
        return switch (reason) {
            case DEATH -> Arena_Game_Leave_Death;
            case FINISH -> Arena_Game_Leave_Finish;
            case FORCE -> Arena_Game_Leave_ForceEnd;
            case KICK -> Arena_Game_Leave_Kick;
            case NO_KIT -> Arena_Game_Leave_NoKit;
            case OUTSIDE -> Arena_Game_Leave_Outside;
            case SELF -> Arena_Game_Leave_Self;
            case TIMELEFT -> Arena_Game_Leave_Timeleft;
            case NO_REGION -> Arena_Game_Leave_NoRegion;
        };
    }

    @Deprecated
    public static final LangKey Help_Score = new LangKey(
        "Help.Score", """
        {message: ~prefix: false;}
        &a/ama score add <player> <amount> &7- Add score to a player.
        &a/ama score take <player> <amount> &7- Take score from a player.
        &a/ama score set <player> <amount> &7- Set score for a player.
        """);

    public static final LangKey COMMAND_BALANCE_DESC        = LangKey.of("Command.Balance.Desc", "View your or other player's balance.");
    public static final LangKey COMMAND_BALANCE_USAGE       = LangKey.of("Command.Balance.Usage", "[player]");
    public static final LangKey COMMAND_BALANCE_DONE_SELF   = LangKey.of("Command.Balance.Done.Self", "Your balance: &a%amount%&7.");
    public static final LangKey COMMAND_BALANCE_DONE_OTHERS = LangKey.of("Command.Balance.Done.Others", "&a%player_name%&7's balance: &a%amount%&7.");

    public static final LangKey COMMAND_CURRENCY_DESC  = LangKey.of("Command.Currency.Desc", "Manage plugin currencies.");
    public static final LangKey COMMAND_CURRENCY_USAGE = LangKey.of("Command.Currency.Usage", "[help]");

    public static final LangKey COMMAND_CURRENCY_GIVE_DESC  = LangKey.of("Command.Currency.Give.Desc", "Give specified currency to a player.");
    public static final LangKey COMMAND_CURRENCY_GIVE_USAGE = LangKey.of("Command.Currency.Give.Usage", "<currency> <player> <amount>");
    public static final LangKey COMMAND_CURRENCY_GIVE_DONE  = LangKey.of("Command.Currency.Give.Done", "Given &ax%amount% %currency_name%&7 to &a%player_name%&7.");

    public static final LangKey COMMAND_CURRENCY_TAKE_DESC  = LangKey.of("Command.Currency.Take.Desc", "Take specified currency from a player.");
    public static final LangKey COMMAND_CURRENCY_TAKE_USAGE = LangKey.of("Command.Currency.Take.Usage", "<currency> <player> <amount>");
    public static final LangKey COMMAND_CURRENCY_TAKE_DONE  = LangKey.of("Command.Currency.Take.Done", "Took &ax%amount% %currency_name%&7 from &a%player_name%&7.");

    public static final LangKey COMMAND_FORCE_START_DESC            = new LangKey("Command.ForceStart.Desc", "Force starts the specified arena.");
    public static final LangKey COMMAND_FORCE_START_USAGE           = new LangKey("Command.ForceStart.Usage", "<arenaId>");
    public static final LangKey COMMAND_FORCE_START_DONE            = new LangKey("Command.ForceStart.Done", "Force started arena &a%arena_id%&7!");
    public static final LangKey COMMAND_FORCE_START_ERROR_NOT_READY = new LangKey("Command.ForceStart.Error.NotReady", "Arena &c%arena_id%&7 is not ready to start or already in-game");

    public static final LangKey COMMAND_FORCE_END_DESC              = new LangKey("Command.ForceEnd.Desc", "Force stops the specified arena.");
    public static final LangKey COMMAND_FORCE_END_USAGE             = new LangKey("Command.ForceEnd.Usage", "<arenaId> [reason]");
    public static final LangKey COMMAND_FORCE_END_DONE              = new LangKey("Command.ForceEnd.Done", "Force ended arena &a%arena_id%&7!");
    public static final LangKey COMMAND_FORCE_END_ERROR_NOT_IN_GAME = new LangKey("Command.ForceEnd.Error.NotInGame", "Arena &c%arena_id% &7is not in game.");

    public static final LangKey COMMAND_HOLOGRAM_DESC         = new LangKey("Command.Hologram.Desc", "Arena hologram management.");
    public static final LangKey COMMAND_HOLOGRAM_USAGE        = new LangKey("Command.Hologram.Usage", "[help]");
    public static final LangKey COMMAND_HOLOGRAM_REMOVE_DESC  = new LangKey("Command.Hologram.Remove.Desc", "Removes nearest stats hologram.");
    public static final LangKey COMMAND_HOLOGRAM_REMOVE_USAGE = new LangKey("Command.Hologram.Remove.Usage", "<HologramType> [args]");
    public static final LangKey COMMAND_HOLOGRAM_REMOVE_DONE  = new LangKey("Command.Hologram.Remove.Done", "&7Removed all &a%type%&7 holograms!");
    public static final LangKey COMMAND_HOLOGRAM_ADD_DESC     = new LangKey("Command.Hologram.Add.Desc", "Creates a hologram for specified object.");
    public static final LangKey COMMAND_HOLOGRAM_ADD_USAGE    = new LangKey("Command.Hologram.Add.Usage", "<HologramType> [args]");
    public static final LangKey COMMAND_HOLOGRAM_ADD_DONE     = new LangKey("Command.Hologram.Add.Done", "&7Successfully created &a%type%&7 hologram!");
    public static final LangKey COMMAND_HOLOGRAM_ERROR        = new LangKey("Command.Hologram.Error", "&cCould not perform operation! (Invalid arguments)");

    public static final LangKey COMMAND_JOIN_DESC    = new LangKey("Command.Join.Desc", "Join the specified (or random) arena.");
    public static final LangKey COMMAND_JOIN_USAGE   = new LangKey("Command.Join.Usage", "[arena]");
    public static final LangKey COMMAND_JOIN_NOTHING = new LangKey("Command.Join.Nothing", "&cNo available arenas to join.");

    public static final LangKey Command_Region_Desc                      = new LangKey("Command.Region.Desc", "Manage in-game arena regions.");
    public static final LangKey Command_Region_Usage                     = new LangKey("Command.Region.Usage", "<lock|unlock>");
    public static final LangKey Command_Region_State_Done                = new LangKey("Command.Region.State.Done", "Region &a%region%'s &7lock state to &a%state%&7!");
    public static final LangKey Command_Region_State_Error_NotInGame     = new LangKey("Command.Region.State.Error.NotInGame", "&cYou must be in-game to do that!");
    public static final LangKey Command_Region_State_Error_InvalidRegion = new LangKey("Command.Region.State.Error.InvalidRegion", "&cInvalid region id!");

    public static final LangKey Command_Score_Desc            = new LangKey("Command.Score.Desc", "Manage player's game score.");
    public static final LangKey Command_Score_Usage           = new LangKey("Command.Score.Usage", "<add|take|set>");
    public static final LangKey Command_Score_Add_Usage       = new LangKey("Command.Score.Add.Usage", "&cUsage: &f/ama score add <player> <amount>");
    public static final LangKey Command_Score_Add_Done        = new LangKey("Command.Score.Add.Done", "&aAdded &f%points% points to &a%player%'s &fscore!");
    public static final LangKey Command_Score_Take_Usage      = new LangKey("Command.Score.Take.Usage", "&cUsage: &f/ama score take <player> <amount>");
    public static final LangKey Command_Score_Take_Done       = new LangKey("Command.Score.Take.Done", "&aTaken &f%points% points from &a%player%'s &fscore!");
    public static final LangKey Command_Score_Set_Usage       = new LangKey("Command.Score.Set.Usage", "&cUsage: &f/ama score set <player> <amount>");
    public static final LangKey Command_Score_Set_Done        = new LangKey("Command.Score.Set.Done", "&aSet &a%player%'s &fscore to &a%score%&f!");
    public static final LangKey Command_Score_Error_NotInGame = new LangKey("Command.Score.Error.NotInGame", "&cThis player is not in game!");

    public static final LangKey Command_Spot_Desc                     = new LangKey("Command.Spot.Desc", "Manage in-game arena spots.");
    public static final LangKey Command_Spot_Usage                    = new LangKey("Command.Spot.Usage", "[state]");
    public static final LangKey Command_Spot_State_Done               = new LangKey("Command.Spot.State.Done", "Changed &a%spot%'s &7state to &a%state%&7!");
    public static final LangKey Command_Spot_State_Error_NotInGame    = new LangKey("Command.Spot.State.Error.NotInGame", "&cYou must be in-game to do that!");
    public static final LangKey Command_Spot_State_Error_InvalidSpot  = new LangKey("Command.Spot.State.Error.InvalidSpot", "&cInvalid spot id!");
    public static final LangKey Command_Spot_State_Error_InvalidState = new LangKey("Command.Spot.State.Error.InvalidState", "&cInvalid spot state id!");

    public static final LangKey COMMAND_LEAVE_DESC = new LangKey("Command.Leave.Desc", "Leave the current arena.");

    public static final LangKey Command_List_Desc = new LangKey("Command.List.Desc", "Shows arenas list.");

    public static final LangKey Command_Skipwave_Desc  = new LangKey("Command.Skipwave.Desc", "Skips current arena wave.");
    public static final LangKey Command_Skipwave_Usage = new LangKey("Command.Skipwave.Usage", "[amount]");

    public static final LangKey Command_Spectate_Desc  = new LangKey("Command.Spectate.Desc", "Join as spectator on specified arena.");
    public static final LangKey Command_Spectate_Usage = new LangKey("Command.Spectate.Usage", "<arena>");

    public static final LangKey COMMAND_SHOP_DESC  = new LangKey("Command.Shop.Desc", "Open arena shop.");
    public static final LangKey COMMAND_STATS_DESC = new LangKey("Command.Stats.Desc", "View your current stats.");

    public static final LangKey ERROR_CURRENCY_INVALID = LangKey.of("Error.Currency.Invalid", "&cInvalid currency!");

    public static final LangKey ARENA_ERROR_DISABLED = new LangKey("Arena.Error.Disabled", "Arena &c%arena_name% &7is disabled.");
    public static final LangKey ARENA_ERROR_INVALID  = new LangKey("Arena.Error.Invalid", "Arena does not exists.");

    public static final LangKey ARENA_JOIN_ERROR_PERMISSION = new LangKey("Arena.Join.Error.Permission", "&cYou don't have permission to join this arena!");
    public static final LangKey ARENA_JOIN_ERROR_PAYMENT    = new LangKey("Arena.Join.Error.Payment", "&cYou must have &e%arena_requirement_payment% &cto join the arena!");
    public static final LangKey ARENA_JOIN_ERROR_IN_GAME    = new LangKey("Arena.Join.Error.InGame", "&cYou are already in game!");
    public static final LangKey ARENA_JOIN_ERROR_MAXIMUM    = new LangKey("Arena.Join.Error.Maximum", "&cThere is maximum players on the arena.");
    public static final LangKey ARENA_JOIN_ERROR_STARTED    = new LangKey("Arena.Join.Error.Started", "Arena &a%arena_name% &7is already in game. You can not join now.");

    public static final LangKey ARENA_JOIN_SPECTATE_SUCCESS        = new LangKey("Arena.Join.Spectate.Success", "Now you are spectating arena &a%arena_name%");
    public static final LangKey ARENA_JOIN_SPECTATE_ERROR_DISABLED = new LangKey("Arena.Join.Spectate.Error.Disabled", "Spectating is disabled on this arena.");

    public static final LangKey ARENA_SCHEDULER_OPEN_ANNOUNCE = LangKey.of("Arena.Scheduler.Open.Announce", "&7Arena &a" + Placeholders.ARENA_NAME + "&7 is opened for play!");
    public static final LangKey ARENA_SCHEDULER_CLOSE_ANNOUNCE = LangKey.of("Arena.Scheduler.Close.Announce", "&7Arena &c" + Placeholders.ARENA_NAME + "&7 have been closed!");

    public static final LangKey Arena_Game_Notify_Start  = new LangKey("Arena.Game.Notify.Start", "{message: ~type: TITLES; ~fadeIn: 10; ~stay: 40; ~fadeOut: 10; ~sound: ENTITY_ENDERMAN_TELEPORT;}&a&lYou have joined the arena! \n &2&lPickup your weapons and fight!");
    public static final LangKey Arena_Game_Notify_Reward = new LangKey("Arena.Game.Notify.Reward", "You recieved reward: &a%reward_name%");

    public static final LangKey Arena_Game_Announce_End   = new LangKey("Arena.Game.Announce.End", "Game on the arena &a%arena_name% &7has ended. Type &a/ama join &a%arena_id% &7to play!");
    public static final LangKey Arena_Game_Announce_Start = new LangKey("Arena.Game.Announce.Start", "Arena &a%arena_name% &7will start in &2%time% &7seconds. Type &a/ama join &a%arena_id% &7to join!");

    public static final LangKey Arena_Game_Restrict_Commands = new LangKey("Arena.Game.Restrict.Commands", "External commands are disabled on this arena. Type &c/ama leave&7 to leave.");
    public static final LangKey Arena_Game_Restrict_Kits     = new LangKey("Arena.Game.Restrict.Kits", "Kits are disabled on this arena.");
    public static final LangKey Arena_Game_Restrict_NoPets   = new LangKey("Arena.Game.Restrict.NoPets", "Pets are not allowed on this arena. Your pet has gone.");
    public static final LangKey ARENA_GAME_ERROR_NOT_IN_GAME = new LangKey("Arena.Game.Error.NotInGame", "You are not in game!");

    public static final LangKey Arena_Game_Leave_Death    = new LangKey("Arena.Game.Leave.Death", "{message: ~type: TITLES; ~fadeIn: 10; ~stay: 60; ~fadeOut: 10;}&c&lYou died! \n &7&lYou has left the arena.");
    public static final LangKey Arena_Game_Leave_Finish   = new LangKey("Arena.Game.Leave.Finish", "{message: ~type: TITLES; ~fadeIn: 10; ~stay: 60; ~fadeOut: 10;}&a&lCongratulations! You finished the arena! \n &2&lCheck your inventory for rewards!");
    public static final LangKey Arena_Game_Leave_Timeleft = new LangKey("Arena.Game.Leave.Timeleft", "{message: ~type: TITLES; ~fadeIn: 10; ~stay: 60; ~fadeOut: 10;}&4&lTime is ended! \n &7&lYou has left the arena.");
    public static final LangKey Arena_Game_Leave_NoRegion = new LangKey("Arena.Game.Leave.NoRegion", "{message: ~type: TITLES; ~fadeIn: 10; ~stay: 60; ~fadeOut: 10;}&4&lNo Regions! \n &7&lYou has left the arena.");
    public static final LangKey Arena_Game_Leave_ForceEnd = new LangKey("Arena.Game.Leave.ForceEnd", "{message: ~type: TITLES; ~fadeIn: 10; ~stay: 60; ~fadeOut: 10;}&4&lForce End! \n &7&lYou has left the arena.");
    public static final LangKey Arena_Game_Leave_Outside  = new LangKey("Arena.Game.Leave.Outside", "{message: ~type: TITLES; ~fadeIn: 10; ~stay: 60; ~fadeOut: 10;}&4&lYou're out of the arena! \n &7&lYou has left the arena.");
    public static final LangKey Arena_Game_Leave_Self     = new LangKey("Arena.Game.Leave.Self", "{message: ~type: TITLES; ~fadeIn: 10; ~stay: 60; ~fadeOut: 10;}&4&lMob Arena \n &7&lYou has left the arena.");
    public static final LangKey Arena_Game_Leave_NoKit    = new LangKey("Arena.Game.Leave.NoKit", "{message: ~type: TITLES; ~fadeIn: 10; ~stay: 60; ~fadeOut: 10;}&4&lYou don't have a kit! \n &7&lYou has left the arena.");
    public static final LangKey Arena_Game_Leave_Kick     = new LangKey("Arena.Game.Leave.Kick", "{message: ~type: TITLES; ~fadeIn: 10; ~stay: 60; ~fadeOut: 10;}&4&lKICKED! \n &7&lYou has left the arena.");

    public static final LangKey Arena_Game_Lobby_Ready_True  = new LangKey("Arena.Game.Lobby.Ready.True", "&a%player_name% &7is ready to play!");
    public static final LangKey Arena_Game_Lobby_Ready_False = new LangKey("Arena.Game.Lobby.Ready.False", "&c%player_name% &7is not ready to play...");
    public static final LangKey Arena_Game_Lobby_Enter       = new LangKey("Arena.Game.Lobby.Enter", "{message: ~type: TITLES; ~fadeIn: 10; ~stay: 40; ~fadeOut: 10;}&a&lWelcome to Mob Arena! \n &2&lPlease, choose your kit");
    public static final LangKey Arena_Game_Lobby_Timer       = new LangKey("Arena.Game.Lobby.Timer", "{message: ~type: TITLES; ~fadeIn: 10; ~stay: 40; ~fadeOut: 10; ~sound: BLOCK_NOTE_BLOCK_PLING;}&e&lThe game will start in \n &a&l%time% seconds!");
    public static final LangKey Arena_Game_Lobby_MinPlayers  = new LangKey("Arena.Game.Lobby.MinPlayers", "Minimum players to start: &c%min%");
    public static final LangKey Arena_Game_Lobby_Joined      = new LangKey("Arena.Game.Lobby.Joined", "&a%player_name% &7has joined the arena.");

    public static final LangKey Arena_Game_Death_Lives  = new LangKey("Arena.Game.Death.Lives", "{message: ~type: TITLES; ~fadeIn: 10; ~stay: 40; ~fadeOut: 10;}&4&lYou Died! \n &cLives left: &e&lx%player_lives%");
    public static final LangKey Arena_Game_Death_Player = new LangKey("Arena.Game.Death.Player", "&c%player_name% &7died! Players left: &c%arena_players%");

    public static final LangKey Arena_Game_Wave_Latest   = new LangKey("Arena.Game.Wave.Latest", "&a&lCongrats! &7You just reached the latest arena wave!");
    public static final LangKey Arena_Game_Wave_Start    = new LangKey("Arena.Game.Wave.Start", "{message: ~type: TITLES; ~fadeIn: 10; ~stay: 40; ~fadeOut: 10;}&6&lWave &e&l#%arena_wave_number% \n &4&lPrepare to fight!");
    public static final LangKey Arena_Game_Wave_Timer    = new LangKey("Arena.Game.Wave.Timer", "{message: ~type: ACTION_BAR;}&6&lNew wave in: &e&l%arena_wave_next_in% &6&lseconds!");
    public static final LangKey Arena_Game_Wave_TimerEnd = new LangKey("Arena.Game.Wave.TimerEnd", "{message: ~type: ACTION_BAR;}&b&l&nGame Ends in:&d&l %arena_wave_next_in% &b&lseconds!");
    public static final LangKey Arena_Game_Wave_Progress = new LangKey("Arena.Game.Wave.Progress", "{message: ~type: ACTION_BAR;}&b[Mobs] &aAlive: &2" + Placeholders.ARENA_MOBS_ALIVE + " &7| &eLeft: &6" + Placeholders.ARENA_MOBS_LEFT);

    public static final LangKey ARENA_REGION_UNLOCKED_NOTIFY = new LangKey("Arena.Region.Unlocked.Notify", "{message: ~type: TITLES; ~fadeIn: 0; ~stay: 30; ~fadeOut: 10; ~sound: " + Sound.BLOCK_NOTE_BLOCK_BELL.name() + ";}&a&lRegion Unlocked!\n&f" + Placeholders.REGION_NAME);
    public static final LangKey ARENA_REGION_LOCKED_NOTIFY   = new LangKey("Arena.Region.Locked.Notify", "{message: ~type: TITLES; ~fadeIn: 0; ~stay: 30; ~fadeOut: 10;}&c&lNew Region!\n&4&lFollow to the next arena region!");

    public static final LangKey Kit_Buy_Error_NoMoney         = new LangKey("Kit.Buy.Error.NoMoney", "You can't afford &c%kit_name% &7kit!");
    public static final LangKey Kit_Buy_Success               = new LangKey("Kit.Buy.Success", "You successfully bought the &a%kit_name% &7kit for &a%kit_cost%&7!");
    public static final LangKey Kit_Buy_Error_NoPermission    = new LangKey("Kit.Buy.Error.NoPermission", "&cYou don't have permission to purchase this kit!");
    public static final LangKey Kit_Select_Error_NoPermission = new LangKey("Kit.Select.Error.NoPermission", "&cYou don't have permission to use this kit!");
    public static final LangKey Kit_Select_Error_Disabled     = new LangKey("Kit.Select.Error.Disabled", "This kit is disabled in this arena.");
    public static final LangKey Kit_Select_Error_NotObtained  = new LangKey("Kit.Select.Error.NotObtained", "You don't have this kit!");
    public static final LangKey Kit_Select_Error_Limit        = new LangKey("Kit.Select.Error.Limit", "You can not use this kit, because there is already enough players with it.");
    public static final LangKey Kit_Select_Success            = new LangKey("Kit.Select.Success", "You choosen &a%kit_name%&7 as your kit.");

    @Deprecated
    public static final LangKey Shop_Notify_NewItems            = new LangKey("Shop.Notify.NewItems", "&aNew items appeared in the shop!");
    @Deprecated
    public static final LangKey Shop_Buy_Success                = new LangKey("Shop.Buy.Success", "You successfully bought &a%shop_product_name% &7for &a%shop_product_price%7!");
    @Deprecated
    public static final LangKey Shop_Buy_Error_NoMoney          = new LangKey("Shop.Buy.Error.NoMoney", "&cYou don't have enough money to buy &e%shop_product_name%&c!");
    @Deprecated
    public static final LangKey Shop_Buy_Error_Locked           = new LangKey("Shop.Buy.Error.Locked", "&cThis item is not available yet!");
    @Deprecated
    public static final LangKey Shop_Buy_Error_BadKit           = new LangKey("Shop.Buy.Error.BadKit", "&cThis item is not available for your kit!");
    public static final LangKey SHOP_OPEN_ERROR_LOCKED          = new LangKey("Shop.Open.Error.Locked", "&cShop is not available yet!");
    public static final LangKey SHOP_OPEN_ERROR_DISABLED        = LangKey.of("Shop.Open.Error.Disabled", "&cShop is disabled on this arena.");
    public static final LangKey SHOP_CATEGORY_OPEN_ERROR_LOCKED = new LangKey("Shop.Category.Open.Error.Locked", "&cShop Category &e%category_name%&c is not available yet!");

    public static final LangKey Editor_Tip_Triggers = new LangKey("Editor.Tip.Triggers", """
        {message: ~prefix: false;}
        &7
        &b&lTrigger Tips:
        &2▸ &aMain Syntax: &2<Trigger_Type> <Value(s)>
        &2▸ &aGroup Syntax: &2<Trigger_Type> <Value(s) OR Value(s) OR Value(s)...>
        &2▸ &aExample #1: &2WAVE_START %5,!10
        &2▸ &aExample #2: &2WAVE_START %2 OR %5 OR >15
        &2▸ &aExample #3: &2WAVE_START !%5,>10
        &7
        &b&lValue Operators:
        &6▸ &ePercent (%): &6Means each X number.
        &dExample #1: &bWAVE_START %5 &7- Each 5th wave = 5/10/15/20/etc.
        &7
        &6▸ &eGreater/Smaller (> or <): &6Means greater or smaller than X number.
        &dExample #1: &bWAVE_START >10,<20 &7- Any wave above 10th and below 20th.
        &dExample #2: &bWAVE_START >10 OR <5 &7- Any wave above 10th or below 5th.
        &7
        &6▸ &eNegate (!): &6Any except X value.
        &dExample #1: &bWAVE_START %5,!10 &7- Each 5th wave, but 10th.
        &dExample #2: &bWAVE_START !%5 &7- Each NOT 5th wave = 1/2/3/4/6/7/8/9/11/etc.
        &dExample #3: &bREGION_UNLOCKED !my_region &7- Any region except 'my_region'.
        &7
        &bList of all available trigger types is below.
        &dFor more details please visit &aPlugin Wiki
        """);

    public static final LangKey Editor_Enter_Triggers            = new LangKey("Editor.Enter.Triggers", "&7Enter trigger type and value(s)...");
    public static final LangKey Editor_Error_Triggers            = new LangKey("Editor.Error.Triggers", "&7Invalid trigger &ctype &7or &cvalue!");
    public static final LangKey EDITOR_GENERIC_ENTER_CURRENCY    = LangKey.of("Editor.Generic.Enter.Currency", "&7Enter &acurrency&7 id...");
    public static final LangKey EDITOR_GENERIC_ENTER_NAME        = new LangKey("Editor.Generic.Enter.Name", "&7Enter display &aname&7...");
    public static final LangKey EDITOR_GENERIC_ENTER_DESCRIPTION = new LangKey("Editor.Generic.Enter.Description", "&7Enter &adescription&7...");

    public static final LangKey Editor_Arena_Tip_Create         = new LangKey("Editor.Arena.Tip.Create", "&7Enter &aunqiue &7arena &aidentifier&7...");
    public static final LangKey Editor_Arena_Error_Exist        = new LangKey("Editor.Arena.Error.Exist", "&cArena already exists!");
    public static final LangKey EDITOR_ARENA_ENTER_NAME         = LangKey.of("Editor.Arena.Enter.Name", "&7Enter arena &aname&7...");
    public static final LangKey EDITOR_ARENA_ENTER_SCHEDULER_TIME = new LangKey("Editor.Arena.Enter.SchedulerTime", "&7Syntax: &a<day> <time>&8 | &aMonday 15:30:00");
    public static final LangKey EDITOR_ARENA_ENTER_JOIN_PAYMENT = new LangKey("Editor.Arena.Enter.JoinPayment", "&7Syntax: &a<currency> <amount>&8 | &avault 500");
    public static final LangKey EDITOR_ARENA_ENTER_JOIN_LEVEL = new LangKey("Editor.Arena.Enter.JoinLevel", "&7Syntax: &a<plugin> <value>&8 | &a" + HookId.MMOCORE + " 10");
    public static final LangKey EDITOR_ARENA_ERROR_LEVEL_PROVIDER = LangKey.of("Editor.Arena.Error.Level_Provider", "&cSuch plugin is not supported!");
    public static final LangKey EDITOR_ARENA_ERROR_SCHEDULER_TIME = LangKey.of("Editor.Arena.Error.SchedulerTime", "&cInvalid day/time!");

    public static final LangKey Editor_Arena_Gameplay_Enter_Timeleft             = new LangKey("Editor.Arena.Gameplay.Enter.Timeleft", "&7Enter time &c(in minutes)");
    public static final LangKey Editor_Arena_Gameplay_Enter_LobbyTime            = new LangKey("Editor.Arena.Gameplay.Enter.LobbyTime", "&7Enter time &c(in seconds)");
    public static final LangKey Editor_Arena_Gameplay_Enter_ScoreboardId         = new LangKey("Editor.Arena.Gameplay.Enter.ScoreboardId", "&7Enter &ascoreboard&7 id...");
    public static final LangKey Editor_Arena_Gameplay_Enter_Mob_Highlight_Amount = new LangKey("Editor.Arena.Gameplay.Enter.Mob.Highlight.Amount", "&7Enter mob &aamount percent&7...");
    public static final LangKey Editor_Arena_Gameplay_Enter_Players_Lives        = new LangKey("Editor.Arena.Gameplay.Enter.Players.Lives", "&7Enter lives amount");
    public static final LangKey Editor_Arena_Gameplay_Enter_Players_MinMax       = new LangKey("Editor.Arena.Gameplay.Enter.Players.MinMax", "&7Enter players amount");
    public static final LangKey Editor_Arena_Gameplay_Enter_BannedItems          = new LangKey("Editor.Arena.Gameplay.Enter.BannedItems", "&7Enter &amaterial &7name...");
    public static final LangKey Editor_Arena_Gameplay_Enter_AllowedSpawnReason   = new LangKey("Editor.Arena.Gameplay.Enter.AllowedSpawnReason", "&7Enter &aspawn reason&7...");
    public static final LangKey Editor_Arena_Gameplay_Enter_Commands_AddWhite    = new LangKey("Editor.Arena.Gameplay.Enter.Commands.AddWhite", "&7Enter a command");
    public static final LangKey Editor_Arena_Gameplay_Enter_Kits_AddLimit        = new LangKey("Editor.Arena.Gameplay.Enter.Kits.AddLimit", "&7Enter a limit like: &a2 warrior");
    public static final LangKey Editor_Arena_Gameplay_Enter_Kits_AddAllowed      = new LangKey("Editor.Arena.Gameplay.Enter.Kits.AddAllowed", "&7Enter a kit id");
    public static final LangKey Editor_Arena_Gameplay_Error_BannedItems          = new LangKey("Editor.Arena.Gameplay.Error.BannedItems", "&7Invalid material!");
    public static final LangKey Editor_Arena_Gameplay_Error_Kits_InvalidKit      = new LangKey("Editor.Arena.Gameplay.Error.Kits.InvalidKit", "&7Invalid kit!");
    public static final LangKey Editor_Arena_Gameplay_Error_Kits_InvalidLimit    = new LangKey("Editor.Arena.Gameplay.Error.Kits.InvalidLimit", "&7Use format like: &c2 warrior");

    public static final LangKey Editor_Arena_Waves_Enter_Delay_First   = new LangKey("Editor.Arena.Waves.Enter.Delay.First", "&7Enter &afirst&7 wave delay...");
    public static final LangKey Editor_Arena_Waves_Enter_Delay_Default = new LangKey("Editor.Arena.Waves.Enter.Delay.Default", "&7Enter &adefault&7 wave delay...");
    public static final LangKey Editor_Arena_Waves_Enter_FinalWave     = new LangKey("Editor.Arena.Waves.Enter.FinalWave", "&7Enter &afinal wave&7...");

    public static final LangKey Editor_Arena_Waves_Enter_Gradual_First_Percent    = new LangKey("Editor.Arena.Waves.Enter.Gradual.First.Percent", "&7Enter first spawn &apercent&7...");
    public static final LangKey Editor_Arena_Waves_Enter_Gradual_Next_Percent     = new LangKey("Editor.Arena.Waves.Enter.Gradual.Next.Percent", "&7Enter next spawn &apercent&7...");
    public static final LangKey Editor_Arena_Waves_Enter_Gradual_Next_Interval    = new LangKey("Editor.Arena.Waves.Enter.Gradual.Next.Interval", "&7Enter next spawn &ainterval&7...");
    public static final LangKey Editor_Arena_Waves_Enter_Gradual_Next_KillPercent = new LangKey("Editor.Arena.Waves.Enter.Gradual.Next.KillPercent", "&7Enter next spawn kill &apercent&7...");

    public static final LangKey Editor_Arena_Waves_Enter_Wave_Create         = new LangKey("Editor.Arena.Waves.Enter.Wave.Create", "&7Enter &aunique &7wave &aidentifier&7...");
    public static final LangKey Editor_Arena_Waves_Enter_Mob_Create          = new LangKey("Editor.Arena.Waves.Enter.Mob.Create", "&7Enter mob &aidentifier&7...");
    public static final LangKey Editor_Arena_Waves_Enter_Mob_Amount          = new LangKey("Editor.Arena.Waves.Enter.Mob.Amount", "&7Enter mob &astart amount&7...");
    public static final LangKey Editor_Arena_Waves_Enter_Mob_Level           = new LangKey("Editor.Arena.Waves.Enter.Mob.Level", "&7Enter mob &astart level&7...");
    public static final LangKey Editor_Arena_Waves_Enter_Mob_Chance          = new LangKey("Editor.Arena.Waves.Enter.Mob.Chance", "&7Enter mob &aspawn chance&7...");
    public static final LangKey Editor_Arena_Waves_Enter_Amplificator_Create = new LangKey("Editor.Arena.Waves.Enter.Amplificator.Create", "&7Enter &aunique &7amplificator &aidentifier&7...");
    public static final LangKey Editor_Arena_Waves_Enter_Amplificator_Value  = new LangKey("Editor.Arena.Waves.Enter.Amplificator.Value", "&7Enter amplificator &avalue&7...");

    public static final LangKey Editor_Arena_Waves_Error_Wave_Exist         = new LangKey("Editor.Arena.Waves.Error.Wave.Exist", "&7Wave already exist!");
    public static final LangKey Editor_Arena_Waves_Error_Mob_Exist          = new LangKey("Editor.Arena.Waves.Error.Mob.Exist", "&7Mob already exist!");
    public static final LangKey Editor_Arena_Waves_Error_Mob_Invalid        = new LangKey("Editor.Arena.Waves.Error.Mob.Invalid", "&7No such Arena Mob or Mythic Mob!");
    public static final LangKey Editor_Arena_Waves_Error_Amplificator_Exist = new LangKey("Editor.Arena.Waves.Error.Amplificator.Exist", "&7Amplificator already exist!");

    public static final LangKey Editor_Arena_Shop_Enter_Product_Create      = new LangKey("Editor.Arena.Shop.Enter.Product.Create", "&7Enter &aunique &7product &aidentifier&7...");
    public static final LangKey Editor_Arena_Shop_Enter_Product_Price       = new LangKey("Editor.Arena.Shop.Enter.Product.Price", "&7Enter product &aprice&7...");
    public static final LangKey Editor_Arena_Shop_Enter_Product_Command     = new LangKey("Editor.Arena.Shop.Enter.Product.Command", "&7Enter a command...");
    public static final LangKey Editor_Arena_Shop_Enter_Product_RequiredKit = new LangKey("Editor.Arena.Shop.Enter.Product.RequiredKit", "&7Enter &akit identifier&7...");
    public static final LangKey Editor_Arena_Shop_Error_Product_Exist       = new LangKey("Editor.Arena.Shop.Error.Product.Exist", "&cProduct already exist!");

    public static final LangKey Editor_Region_Enter_Create           = new LangKey("Editor.Region.Enter.Create", "&7Enter &aunique &7region identifier...");
    public static final LangKey Editor_Region_Enter_Id               = new LangKey("Editor.Region.Enter.Id", "&7Enter region &aidentifier&7...");
    public static final LangKey Editor_Region_Enter_Name             = new LangKey("Editor.Region.Enter.Name", "&7Enter region name...");
    public static final LangKey Editor_Region_Error_Create           = new LangKey("Editor.Region.Error.Create", "&7Region already exists!");
    public static final LangKey Editor_Region_Wave_Enter_Id          = new LangKey("Editor.Region.Wave.Enter.Id", "&7Enter &aarena wave &7identifier...");
    public static final LangKey Editor_Region_Wave_Enter_SpawnerId   = new LangKey("Editor.Region.Wave.Enter.SpawnerId", "&7Enter &aspawner &7identifier...");
    public static final LangKey Editor_Region_Wave_Enter_Create      = new LangKey("Editor.Region.Wave.Enter.Create", "&7Enter &aunique &7wave identifier");
    public static final LangKey Editor_Region_Wave_Error_Create      = new LangKey("Editor.Region.Wave.Error.Create", "&7Wave already exist!");
    public static final LangKey Editor_Region_Container_Enter_Amount = new LangKey("Editor.Region.Container.Enter.Amount", "&7Enter &aitems &7amount...");

    public static final LangKey Editor_Reward_Enter_Name    = new LangKey("Editor.Reward.Enter.Name", "&7Enter reward &aname&7...");
    public static final LangKey Editor_Reward_Enter_Command = new LangKey("Editor.Reward.Enter.Command", "&7Enter a &acommand&7...");
    public static final LangKey Editor_Reward_Enter_Chance  = new LangKey("Editor.Reward.Enter.Chance", "&7Enter reward &achance&7...");

    public static final LangKey Editor_Spot_Enter_Id   = new LangKey("Editor.Spot.Enter.Id", "&7Enter &aunique&7 spot identifier...");
    public static final LangKey Editor_Spot_Enter_Name = new LangKey("Editor.Spot.Enter.Name", "&7Enter spot name...");
    public static final LangKey Editor_Spot_Error_Id   = new LangKey("Editor.Spot.Error.Id", "&7Such spot already exist!");

    public static final LangKey Editor_Spot_State_Enter_Id       = new LangKey("Editor.Spot.State.Enter.Id", "&7Enter &aunique&7 state identifier...");
    public static final LangKey Editor_Spot_State_Error_Id       = new LangKey("Editor.Spot.State.Error.Id", "&7Such state already exist!");
    public static final LangKey Editor_Spot_State_Error_NoCuboid = new LangKey("Editor.Spot.State.Error.NoCuboid", "&cYou must set a spot cuboid!");

    public static final LangKey Editor_Kit_Enter_Create  = new LangKey("Editor.Kit.Enter.Create", "&7Enter &aunique &7kit &aidentifier&7...");
    public static final LangKey Editor_Kit_Enter_Name    = new LangKey("Editor.Kit.Enter.Name", "&7Enter new &aname&7...");
    public static final LangKey Editor_Kit_Enter_Command = new LangKey("Editor.Kit.Enter.Command", "&7Enter &acommand&7...");
    public static final LangKey Editor_Kit_Enter_Effect  = new LangKey("Editor.Kit.Enter.Effect", "&7Enter &aEffect:Level&7...");
    public static final LangKey Editor_Kit_Enter_Cost    = new LangKey("Editor.Kit.Enter.Cost", "&7Enter &acost&7...");
    public static final LangKey Editor_Kit_Error_Exist   = new LangKey("Editor.Kit.Error.Exist", "&cKit already exists!");

    public static final LangKey Editor_Mob_Enter_Create        = new LangKey("Editor.Mob.Enter.Create", "&7Enter &aunique &7mob &aidentifier&7...");
    public static final LangKey Editor_Mob_Enter_Name          = new LangKey("Editor.Mob.Enter.Name", "&7Enter &acustom &7mob &aname&7...");
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

    public static final LangKey Setup_Region_Error_Enabled        = new LangKey("Setup.Region.Error.Enabled", "&cYou must disable region first!");
    public static final LangKey Setup_Region_Spawn_Set            = new LangKey("Setup.Region.Spawn.Set", "&7Defined spawn location for &a%region_id% &7region!");
    public static final LangKey Setup_Region_Spawner_Add          = new LangKey("Setup.Region.Spawner.Add", "&7Added spawner to &a%region_id% &7region!");
    public static final LangKey Setup_Region_Spawner_Remove       = new LangKey("Setup.Region.Spawner.Remove", "&7Removed spawner from &a%region_id% &7region!");
    public static final LangKey Setup_Region_Container_Add        = new LangKey("Setup.Region.Container.Add", "&7Added container to &a%region_id% &7region!");
    public static final LangKey Setup_Region_Container_Remove     = new LangKey("Setup.Region.Container.Remove", "&7Removed container from &a%region_id% &7region!");
    public static final LangKey Setup_Region_Cuboid_Error_Overlap = new LangKey("Setup.Region.Cuboid.Error.Overlap", "&7This position is overlaps with &c%region_id% &7arena region!");
    public static final LangKey Setup_Region_Cuboid_Set           = new LangKey("Setup.Region.Cuboid.Set", "&7Set &a#%corner% &7corner for the &a%region_id% &7region!");
    public static final LangKey Setup_Reigon_Cuboid_Preview       = new LangKey(
        "Setup.Region.Cuboid.Preview", """
        {message: ~prefix: false;}
        &c&m----------------------------------------
        &eYou defined new region position(s). The following changes will be made:
        &c▸ &7Mob Spawners Lost: &c%spawners-lost%
        &c▸ &7Region Containers Lost: &c%containers-lost%
        &c▸ &7Region Spawn Location Lost: &c%spawn-lost%
        &7
        &eIf you want to cancel these changes simply use &cExit Tool&7.
        &c&m----------------------------------------""");
    public static final LangKey Setup_Region_Error_Outside        = new LangKey("Setup.Region.Error.Outside", "&cLocation is outside of the editing region!");

    public static final LangKey Setup_Spot_Cuboid_Error_Overlap = new LangKey("Setup.Spot.Cuboid.Error.Overlap", "&cThis location is overlaps with other &e%spot_id% &cspot!");
    public static final LangKey Setup_Spot_Cuboid_Set           = new LangKey("Setup.Spot.Cuboid.Set", "&7Set &a#%corner% &7corner for the &a%spot_id% &7spot!");
    public static final LangKey Setup_Spot_State_Error_Outside  = new LangKey("Setup.Spot.State.Error.Outside", "&cLocation is outside of the editing spot!");
}
