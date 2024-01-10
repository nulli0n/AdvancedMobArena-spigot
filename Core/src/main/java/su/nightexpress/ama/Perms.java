package su.nightexpress.ama;

import su.nexmedia.engine.api.server.JPermission;

public class Perms {

    public static final String PREFIX       = "advancedmobarena.";
    public static final String PREFIX_ARENA = PREFIX + "arena.";
    public static final String PREFIX_KIT   = PREFIX + "kit.";

    public static final JPermission PLUGIN         = new JPermission(PREFIX + Placeholders.WILDCARD, "Access to all the plugin functions.");
    public static final JPermission PLUGIN_COMMAND = new JPermission(PREFIX + "command", "Access to all the plugin commands.");
    public static final JPermission PLUGIN_BYPASS  = new JPermission(PREFIX + "bypass", "Bypass all the plugin and arena restrictions.");

    public static final JPermission CREATOR   = new JPermission(PREFIX + "creator", "Allows to create signs and terraform arena regions.");
    public static final JPermission ARENA_ALL = new JPermission(PREFIX_ARENA + Placeholders.WILDCARD, "Access to all the arenas.");
    public static final JPermission KIT_ALL   = new JPermission(PREFIX_KIT + Placeholders.WILDCARD, "Access to all the arena kits.");

    public static final JPermission BYPASS_ARENA               = new JPermission(PREFIX + "bypass.arena", "Bypass all arena restrictions.");
    public static final JPermission BYPASS_KIT                 = new JPermission(PREFIX + "bypass.kit", "Bypass all kit restrictions.");
    public static final JPermission BYPASS_ARENA_JOIN_INGAME   = new JPermission(PREFIX + "bypass.arena.join.ingame", "Allows to join the arena during the game.");
    public static final JPermission BYPASS_ARENA_JOIN_PAYMENT  = new JPermission(PREFIX + "bypass.arena.join.payment", "Bypasses arena join payments.");
    public static final JPermission BYPASS_ARENA_JOIN_LEVEL    = new JPermission(PREFIX + "bypass.arena.join.level", "Bypasses arena level requirements.");
    public static final JPermission BYPASS_ARENA_GAME_COMMANDS = new JPermission(PREFIX + "bypass.arena.game.commands", "Allows to use non-arena commands in game.");
    public static final JPermission BYPASS_ARENA_JOIN_COOLDOWN = new JPermission(PREFIX + "bypass.arena.join.cooldown", "Bypasses arena join cooldowns.");
    public static final JPermission BYPASS_KIT_COST            = new JPermission(PREFIX + "bypass.kit.cost", "Allows to purchase kits from kit shop for free.");

    public static final JPermission COMMAND_EDITOR          = new JPermission(PREFIX + "command.editor", "Allows to use '/ama editor' command.");
    public static final JPermission COMMAND_RELOAD          = new JPermission(PREFIX + "command.reload", "Allows to use '/ama reload' command.");
    public static final JPermission COMMAND_BALANCE         = new JPermission(PREFIX + "command.balance", "Allows to use '/ama coins balance' command.");
    public static final JPermission COMMAND_BALANCE_OTHERS  = new JPermission(PREFIX + "command.balance.others", "Allows to use '/ama coins balance' command for other players.");
    public static final JPermission COMMAND_COINS           = new JPermission(PREFIX + "command.coins", "Allows to use '/ama coins' command (without sub-commands).");
    public static final JPermission COMMAND_COINS_GIVE      = new JPermission(PREFIX + "command.coins.give", "Allows to use '/ama coins add' sub-command.");
    public static final JPermission COMMAND_COINS_TAKE      = new JPermission(PREFIX + "command.coins.take", "Allows to use '/ama coins remove' sub-command.");
    public static final JPermission COMMAND_COINS_SET       = new JPermission(PREFIX + "command.coins.set", "Allows to use '/ama coins set' sub-command.");
    public static final JPermission COMMAND_FORCEEND        = new JPermission(PREFIX + "command.forceend", "Allows to use '/ama forceend' command.");
    public static final JPermission COMMAND_FORCESTART      = new JPermission(PREFIX + "command.forcestart", "Allows to use '/ama forcestart' command.");
    public static final JPermission COMMAND_JOIN            = new JPermission(PREFIX + "command.join", "Allows to use '/ama join' command.");
    public static final JPermission COMMAND_JOIN_OTHERS            = new JPermission(PREFIX + "command.join.others", "Allows to use '/ama join' command on other players.");
    public static final JPermission COMMAND_HOLOGRAM        = new JPermission(PREFIX + "command.hologram", "Allows to use '/ama hologram' command (without of sub-commands).");
    public static final JPermission COMMAND_HOLOGRAM_ADD    = new JPermission(PREFIX + "command.hologram.add", "Allows to use '/ama hologram add' command.");
    public static final JPermission COMMAND_HOLOGRAM_REMOVE = new JPermission(PREFIX + "command.hologram.remove", "Allows to use '/ama hologram remove' command.");
    public static final JPermission COMMAND_LEAVE           = new JPermission(PREFIX + "command.leave", "Allows to use '/ama leave' commmand.");
    public static final JPermission COMMAND_LIST            = new JPermission(PREFIX + "command.list", "Allows to use '/ama list' command.");
    public static final JPermission COMMAND_REGION          = new JPermission(PREFIX + "command.region", "Allows to use '/ama region' command.");
    public static final JPermission COMMAND_SET_ACTIVE      = new JPermission(PREFIX + "command.setactive", "Allows to use '/ama setactive' command.");
    public static final JPermission COMMAND_SCORE           = new JPermission(PREFIX + "command.score", "Allows to use '/ama score' command.");
    public static final JPermission COMMAND_SHOP            = new JPermission(PREFIX + "command.shop", "Allows to use '/ama shop' command.");
    public static final JPermission COMMAND_SKIPROUND       = new JPermission(PREFIX + "command.skipround", "Allows to use '/ama skipround' command.");
    public static final JPermission COMMAND_SPECTATE        = new JPermission(PREFIX + "command.spectate", "Allows to use '/ama spectate' command.");
    public static final JPermission COMMAND_SPOT            = new JPermission(PREFIX + "command.spot", "Allows to use '/ama spot' command.");
    public static final JPermission COMMAND_STATS           = new JPermission(PREFIX + "command.stats", "Allows to use '/ama stats' command.");

    static {
        PLUGIN.addChildren(PLUGIN_COMMAND, PLUGIN_BYPASS, CREATOR, ARENA_ALL, KIT_ALL);

        PLUGIN_BYPASS.addChildren(BYPASS_ARENA, BYPASS_KIT);

        PLUGIN_COMMAND.addChildren(
            COMMAND_EDITOR,
            COMMAND_RELOAD,
            COMMAND_BALANCE, COMMAND_BALANCE_OTHERS,
            COMMAND_COINS,
            COMMAND_HOLOGRAM,
            COMMAND_FORCEEND, COMMAND_FORCESTART,
            COMMAND_JOIN, COMMAND_JOIN_OTHERS,
            COMMAND_LEAVE,
            COMMAND_LIST,
            COMMAND_REGION,
            COMMAND_SET_ACTIVE,
            COMMAND_SCORE,
            COMMAND_SHOP,
            COMMAND_SKIPROUND,
            COMMAND_SPECTATE,
            COMMAND_SPOT,
            COMMAND_STATS
        );

        BYPASS_ARENA.addChildren(
            BYPASS_ARENA_GAME_COMMANDS,
            BYPASS_ARENA_JOIN_INGAME,
            BYPASS_ARENA_JOIN_LEVEL,
            BYPASS_ARENA_JOIN_PAYMENT,
            BYPASS_ARENA_JOIN_COOLDOWN
        );

        BYPASS_KIT.addChildren(BYPASS_KIT_COST);

        COMMAND_COINS.addChildren(COMMAND_COINS_GIVE, COMMAND_COINS_TAKE);
        COMMAND_HOLOGRAM.addChildren(COMMAND_HOLOGRAM_ADD, COMMAND_HOLOGRAM_REMOVE);
    }
}
