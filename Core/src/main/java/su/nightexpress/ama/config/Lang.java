package su.nightexpress.ama.config;

import org.bukkit.Sound;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.lang.LangKey;
import su.nexmedia.engine.lang.EngineLang;
import su.nightexpress.ama.Placeholders;
import su.nightexpress.ama.api.arena.type.LeaveReason;
import su.nightexpress.ama.currency.CurrencyId;
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

    public static final LangKey COMMAND_SETACTIVE_DESC              = new LangKey("Command.SetActive.Desc", "Set arena active state.");
    public static final LangKey COMMAND_SETACTIVE_USAGE             = new LangKey("Command.SetActive.Usage", "<arenaId> <true/false>");
    public static final LangKey COMMAND_SETACTIVE_DONE              = new LangKey("Command.SetActive.Done", "Arena &a%arena_id%&7 active state: &f%state%&7.");

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
    public static final LangKey ARENA_JOIN_ERROR_LEVEL    = new LangKey("Arena.Join.Error.Level", "&cYour level is not suitable to join the arena!");
    public static final LangKey ARENA_JOIN_ERROR_IN_GAME    = new LangKey("Arena.Join.Error.InGame", "&cYou are already in game!");
    public static final LangKey ARENA_JOIN_ERROR_MAXIMUM    = new LangKey("Arena.Join.Error.Maximum", "&cThere is maximum players on the arena.");
    public static final LangKey ARENA_JOIN_ERROR_STARTED    = new LangKey("Arena.Join.Error.Started", "Arena &a%arena_name% &7is already in game. You can not join now.");

    public static final LangKey ARENA_JOIN_SPECTATE_SUCCESS        = new LangKey("Arena.Join.Spectate.Success", "Now you are spectating arena &a%arena_name%");
    public static final LangKey ARENA_JOIN_SPECTATE_ERROR_DISABLED = new LangKey("Arena.Join.Spectate.Error.Disabled", "Spectating is disabled on this arena.");

    public static final LangKey ARENA_SCHEDULER_OPEN_ANNOUNCE = LangKey.of("Arena.Scheduler.Open.Announce", "&7Arena &a" + Placeholders.ARENA_NAME + "&7 is opened for play!");
    public static final LangKey ARENA_SCHEDULER_CLOSE_ANNOUNCE = LangKey.of("Arena.Scheduler.Close.Announce", "&7Arena &c" + Placeholders.ARENA_NAME + "&7 have been closed!");

    public static final LangKey Arena_Game_Notify_Start  = new LangKey("Arena.Game.Notify.Start", "{message: ~type: TITLES; ~fadeIn: 10; ~stay: 40; ~fadeOut: 10; ~sound: ENTITY_ENDERMAN_TELEPORT;}&a&lYou have joined the arena! \n &2&lPickup your weapons and fight!");
    public static final LangKey ARENA_GAME_NOTIFY_REWARD = new LangKey("Arena.Game.Notify.Reward", "You recieved reward: &a" + Placeholders.REWARD_NAME);

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

    public static final LangKey EDITOR_ARENA_ENTER_ID             = LangKey.of("Editor.Arena.Enter.Id", "&7Enter &a[Arena Identifier]");
    public static final LangKey EDITOR_ARENA_ENTER_SCHEDULER_TIME = LangKey.of("Editor.Arena.Enter.SchedulerTime", "&7Enter &a[Day] [Time] &7| &aMonday 15:30");
    public static final LangKey EDITOR_ARENA_ENTER_JOIN_PAYMENT   = LangKey.of("Editor.Arena.Enter.JoinPayment", "&7Enter &a[Currency] [Amount] &7| &a" + CurrencyId.VAULT + " 500");
    public static final LangKey EDITOR_ARENA_ENTER_JOIN_LEVEL     = LangKey.of("Editor.Arena.Enter.JoinLevel", "&7Enter &a[Plugin] [Value] &7| &a" + HookId.MMOCORE + " 10");
    public static final LangKey EDITOR_ARENA_ERROR_EXISTS         = LangKey.of("Editor.Arena.Error.Exists", "&cArena is already exists!");
    public static final LangKey EDITOR_ARENA_ERROR_LEVEL_PROVIDER = LangKey.of("Editor.Arena.Error.Level_Provider", "&cSuch plugin is not supported!");
    public static final LangKey EDITOR_ARENA_ERROR_SCHEDULER_TIME = LangKey.of("Editor.Arena.Error.SchedulerTime", "&cInvalid day/time!");

    public static final LangKey EDITOR_ARENA_GAMEPLAY_ENTER_SCOREBOARD_ID        = LangKey.of("Editor.Arena.Gameplay.Enter.ScoreboardId", "&7Enter &a[Scoreboard Id]");
    public static final LangKey EDITOR_ARENA_GAMEPLAY_ENTER_BANNED_ITEMS         = LangKey.of("Editor.Arena.Gameplay.Enter.BannedItem", "&7Enter &a[Material Name]");
    public static final LangKey EDITOR_ARENA_GAMEPLAY_ENTER_ALLOWED_SPAWN_REASON = LangKey.of("Editor.Arena.Gameplay.Enter.AllowedSpawnReason", "&7Enter &a[Spawn Reason]");
    public static final LangKey EDITOR_ARENA_GAMEPLAY_ENTER_KIT_LIMIT            = LangKey.of("Editor.Arena.Gameplay.Enter.KitLimit", "&7Enter [Kit Id] [Limit] &7| &awarrior 3");

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
    public static final LangKey EDITOR_ARENA_SCRIPT_ENTER_ACTION_PARAMETER  = LangKey.of("Editor.Arena.Script.Enter.Action.Parameter", "&7Enter &a[Parameter] [Value]");
    public static final LangKey EDITOR_ARENA_SCRIPT_ENTER_CONDITION_SECTION = LangKey.of("Editor.Arena.Script.Enter.Condition.Section", "&7Enter &a[Section Name]");
    public static final LangKey EDITOR_ARENA_SCRIPT_ENTER_CONDITION_VALUE   = LangKey.of("Editor.Arena.Script.Enter.Condition.Value", "&7Enter &a[Condition] [Operator] [Value]");
    public static final LangKey EDITOR_ARENA_SCRIPT_ERROR_INVALID_ACTION    = LangKey.of("Editor.Arena.Script.Error.InvalidAction", "&cInvalid action!");
    public static final LangKey EDITOR_ARENA_SCRIPT_ERROR_INVALID_PARAMETER = LangKey.of("Editor.Arena.Script.Error.InvalidParameter", "&cInvalid parameter!");
    public static final LangKey EDITOR_ARENA_SCRIPT_ERROR_INVALID_CONDITION = LangKey.of("Editor.Arena.Script.Error.InvalidCondition", "&cInvalid condition!");
    public static final LangKey EDITOR_ARENA_SCRIPT_ERROR_INVALID_INPUT     = LangKey.of("Editor.Arena.Script.Error.InvalidInput", "&cInvalid input!");

    public static final LangKey EDITOR_REGION_ENTER_ID     = LangKey.of("Editor.Region.Enter.Id", "&7Enter &a[Region Identifier]");
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

    public static final LangKey             EDITOR_KIT_ENTER_ID   = new LangKey("Editor.Kit.Enter.Create", "&7Enter &aunique &7kit &aidentifier&7...");
    @Deprecated public static final LangKey Editor_Kit_Enter_Name = new LangKey("Editor.Kit.Enter.Name", "&7Enter new &aname&7...");
    @Deprecated public static final LangKey Editor_Kit_Enter_Command = new LangKey("Editor.Kit.Enter.Command", "&7Enter &acommand&7...");
    public static final LangKey Editor_Kit_Enter_Effect  = new LangKey("Editor.Kit.Enter.Effect", "&7Enter &aEffect:Level&7...");
    public static final LangKey Editor_Kit_Enter_Cost    = new LangKey("Editor.Kit.Enter.Cost", "&7Enter &acost&7...");
    public static final LangKey Editor_Kit_Error_Exist   = new LangKey("Editor.Kit.Error.Exist", "&cKit already exists!");

    public static final LangKey Editor_Mob_Enter_Create        = new LangKey("Editor.Mob.Enter.Create", "&7Enter &aunique &7mob &aidentifier&7...");
    @Deprecated public static final LangKey Editor_Mob_Enter_Name          = new LangKey("Editor.Mob.Enter.Name", "&7Enter &acustom &7mob &aname&7...");
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
    public static final LangKey Setup_Region_Cuboid_Error_Overlap = new LangKey("Setup.Region.Cuboid.Error.Overlap", "&7This position is overlaps with &c%region_id% &7arena region!");
    public static final LangKey Setup_Region_Cuboid_Set           = new LangKey("Setup.Region.Cuboid.Set", "&7Set &a#%corner% &7corner for the &a%region_id% &7region!");
    public static final LangKey Setup_Region_Error_Outside        = new LangKey("Setup.Region.Error.Outside", "&cLocation is outside of the editing region!");

    public static final LangKey SETUP_SPOT_CUBOID_SET           = LangKey.of("Setup.Spot.Cuboid.Set", "&7Set &a#%corner% &7corner for the &a" + Placeholders.SPOT_ID + " &7spot!");
    public static final LangKey SETUP_SPOT_CUBOID_ERROR_OVERLAP = LangKey.of("Setup.Spot.Cuboid.Error.Overlap", "&cThis location overlaps with &e" + Placeholders.SPOT_ID + " &cspot!");
    public static final LangKey SETUP_SPOT_STATE_ERROR_OUTSIDE  = LangKey.of("Setup.Spot.State.Error.Outside", "&cLocation is outside of the editing spot!");
}
