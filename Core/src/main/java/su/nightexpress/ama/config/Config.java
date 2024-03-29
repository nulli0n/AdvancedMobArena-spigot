package su.nightexpress.ama.config;

import org.bukkit.inventory.ItemStack;
import su.nexmedia.engine.api.config.JOption;
import su.nexmedia.engine.utils.Colorizer;
import su.nightexpress.ama.Placeholders;
import su.nightexpress.ama.api.hologram.HologramType;
import su.nightexpress.ama.arena.board.ArenaBoardConfig;
import su.nightexpress.ama.arena.util.LobbyItem;
import su.nightexpress.ama.hook.HookId;
import su.nightexpress.ama.sign.type.SignType;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Config {

    public static final String DIR_MENU = "/menu/";
    public static final String DIR_KITS = "/kits/";

    public static final JOption<Boolean> DEBUG_MOB_SPAWN = JOption.create("Debug.Mob_Spawning", false,
        "Enable this setting if you're experiencing issues with mob spawn on arenas.",
        "Then send debug logs from the console to the developer."
    );

    public static final JOption<Boolean> CHAT_ENABLED = JOption.create("Chat.Enabled",
        false,
        "When enabled, players in arenas will have their own chat.");

    public static final JOption<String>  CHAT_FORMAT  = JOption.create("Chat.Format",
        "&7(&6" + Placeholders.PLAYER_KIT_NAME + "&7) &a" + Placeholders.PLAYER_NAME + ": &f" + Placeholders.GENERIC_MESSAGE,
        "Sets the chat format.",
        "You can use 'Arena', 'Arena Player' placeholders: " + Placeholders.WIKI_AMA_PLACEHOLDERS,
        "Default Placeholders:",
        "- %player_name% - Player name.",
        "- %message% - Message text.",
        "PlaceholderAPI is also supported."
    ).mapReader(Colorizer::apply);

    public static final JOption<Boolean> ARENA_CLEAR_POTION_EFFECTS = JOption.create("Arena.Clear_Potion_Effects", true,
        "Sets whether or not player's potion effects should be removed when join/leave arena.");

    public static final JOption<Boolean> ARENA_ALWAYS_RESTORE_INVENTORY = JOption.create("Arena.Always_Restore_Inventory",
        false,
        "Sets whether or not player inventories will be restored to pre-arena state even if kits are disabled.");

    public static final JOption<Boolean> ARENA_FIX_SPLASH_POTIONS = JOption.create("Arena.Items.Fix_Splash_Potions",
        true,
        "Sets whether or not plugin will 'fix' splash and lingering potions so they will not injure allied players and mobs.");

    public static final JOption<Boolean> ARENA_TNT_ALLOWED_PLACEMENT = JOption.create("Arena.Items.TNT.Allow_Placement",
        true,
        "Sets whether or not players will be able to place TNTs and auto ignite it.");

    public static final JOption<Integer> ARENA_TNT_FUSE_TICKS = JOption.create("Arena.Items.TNT.Fuse_Ticks",
        30,
        "Sets how soon (in ticks) placed TNTs will explode.",
        "[20 ticks = 1 second]",
        "[Plugin default is 30]",
        "[Game default is 80]");

    public static final JOption<Boolean> ARENA_FIRE_CHARGE_ALLOW_LAUNCH = JOption.create("Arena.Items.FireCharge.Allow_Launch",
        true,
        "Sets whether or not players will be able to launch fireballs using Fire Charge items.");

    public static final JOption<Integer> ARENA_END_COUNTDOWN_DEFEAT = JOption.create("Arena.End_Countdown.Defeat", 10,
        "Sets how soon (in seconds) game on the arena will be stopped in case of defeat?");

    public static final JOption<Integer> ARENA_END_COUNTDOWN_VICTORY = JOption.create("Arena.End_Countdown.Victory", 10,
        "Sets how soon (in seconds) game on the arena will be stopped in case of victory?");

    public static final JOption<Integer> LOBBY_READY_DROP_TIMER = JOption.create("Lobby.Ready_State.Drop_Timer_To", 15,
        "Instantly decreases lobby countdown timer to the specified value, if all players are ready to play.",
        "Set this to -1 to disable feature."
    );

    public static final JOption<Boolean> LOBBY_READY_FREEZE_TIMER_WHEN_DROPPED = JOption.create("Lobby.Ready_State.Freeze_Dropped_Timer_When_Not_Ready",
        false,
        "When enabled, lobby timer will be stopped, when someone changes his state as 'not ready'.",
        "This option will only have effect if lobby timer was already decreased by the setting above."
    );

    public static final JOption<Map<LobbyItem.Type, LobbyItem>> LOBBY_ITEMS = new JOption<Map<LobbyItem.Type, LobbyItem>>("Lobby.Items",
        (cfg, path, def) -> {
            return Stream.of(LobbyItem.Type.values()).collect(Collectors.toMap(k -> k, v -> {
                boolean isEnabled = cfg.getBoolean(path + "." + v.name() + ".Enabled");
                int slot = cfg.getInt(path + "." + v.name() + ".Slot");
                ItemStack item = cfg.getItem(path + "." + v.name() + ".Item");
                return new LobbyItem(v, isEnabled, item, slot);
            }));
        },
        () -> {
            return Stream.of(LobbyItem.Type.values()).collect(Collectors.toMap(k -> k, v -> {
                return new LobbyItem(v, true, v.getDefaultItem(), v.getDefaultSlot());
            }));
        },
        "Lobby items settings.",
        "Disabled items won't be given to players and will have no effect.",
        "Slot is where lobby item will be added in player's inventory.",
        "For 'Item' option, please check: " + Placeholders.WIKI_ITEMS_URL
    ).setWriter((cfg, path, map) -> map.forEach((type, lobbyItem) -> LobbyItem.write(lobbyItem, cfg, path + "." + type.name())));

    public static final JOption<Boolean> KITS_PERMANENT_PURCHASES = JOption.create("Kits.Permanent_Purchases",
        true,
        "When enabled, purchased kits will last forever on player accounts.",
        "When disabled, purchased kits will last until played once.");

    public static final JOption<Boolean> KITS_PREVENT_ITEM_SHARE = JOption.create("Kits.Prevent_Item_Share",
        true,
        "Sets whether or not to prevent players from pickup items of other kits.",
        "Example: Player with 'tank' kit can not pickup 'archer' kit items and vice versa.");

    public static final JOption<Boolean>                     SIGNS_ENABLED = JOption.create("Signs.Enabled", true, "Enables/Disables usable signs feature.");

    public static final JOption<Boolean>                     SIGNS_GLOWING = JOption.create("Signs.Glowing", true, "Makes the sign text glowing.");

    public static final JOption<Map<SignType, List<String>>> SIGNS_FORMAT  = new JOption<Map<SignType, List<String>>>("Signs.Format",
        (cfg, path, def) -> {
            return Stream.of(SignType.values()).collect(Collectors.toMap(k -> k, v -> Colorizer.apply(cfg.getStringList(path + "." + v.name()))));
        },
        () -> {
            return Stream.of(SignType.values()).collect(Collectors.toMap(k -> k, SignType::getDefaultText));
        },
        "Text to dispay on arena signs.",
        "Depends on a Sign Type, you can use different placeholders: " + Placeholders.WIKI_AMA_PLACEHOLDERS
    ).setWriter((cfg, path, map) -> map.forEach((type, list) -> cfg.set(path + "." + type.name(), list)));

    public static final JOption<Boolean> HOLOGRAMS_ENABLED = JOption.create("Holograms.Enabled", true,
        "Enables the Holograms feature.",
        "Supported Plugins: " + HookId.HOLOGRAPHIC_DISPLAYS + ", " + HookId.DECENT_HOLOGRAMS
    );

    public static final JOption<Map<HologramType, List<String>>> HOLOGRAMS_FORMAT  = new JOption<Map<HologramType, List<String>>>("Holograms.Format",
        (cfg, path, def) -> Stream.of(HologramType.values()).collect(Collectors.toMap(k -> k, v -> Colorizer.apply(cfg.getStringList(path + "." + v.name())))),
        () -> Stream.of(HologramType.values()).collect(Collectors.toMap(k -> k, HologramType::getDefaultFormat)),
        "Text to dispay on arena holograms.",
        "Depends on a Hologram Type, you can use different placeholders: " + Placeholders.WIKI_AMA_PLACEHOLDERS
    ).setWriter((cfg, path, map) -> map.forEach((type, list) -> cfg.set(path + "." + type.name(), list)));

    public static final JOption<Map<String, ArenaBoardConfig>> SCOREBOARDS = new JOption<Map<String, ArenaBoardConfig>>("Scoreboard",
        (cfg, path, def) -> cfg.getSection(path).stream().collect(Collectors.toMap(id -> id, v -> ArenaBoardConfig.read(cfg, path + "." + v, v))),
        () -> {
            Map<String, ArenaBoardConfig> map = new HashMap<>();
            ArenaBoardConfig boardConfig = new ArenaBoardConfig(Placeholders.DEFAULT, "&c&lMOB ARENA", Arrays.asList(
                "&c▸ &7Arena: &c" + Placeholders.ARENA_NAME, "&c▸ &7Current wave: &c" + Placeholders.ARENA_WAVE_NUMBER,
                "&c▸ &7Mobs: &a" + Placeholders.ARENA_MOBS_ALIVE + "&7/&e" + Placeholders.ARENA_MOBS_LEFT + "&7/&c" + Placeholders.ARENA_MOBS_TOTAL,
                "&c▸ &7Players: &c" + Placeholders.ARENA_ALIVE_PLAYERS + "&7/&c" + Placeholders.ARENA_PLAYERS_MAX,
                "&c▸ &7Timeleft: &c" + Placeholders.ARENA_TIMELEFT,
                "&c▸ &7Next wave in: &c" + Placeholders.ARENA_WAVE_NEXT_IN + " sec.",
                "&7       &e&lYOUR STATS     &7",
                "&e▸ &6Score: &e" + Placeholders.PLAYER_SCORE, "&e▸ &6Kills: &e" + Placeholders.PLAYER_KILLS,
                "&e▸ &6Streak: &ex" + Placeholders.PLAYER_STREAK + " &7(" + Placeholders.PLAYER_STREAK_DECAY + " sec.)",
                "&e▸ &6Coins: &e%ama_coins%",
                "&r"
            ));
            map.put(boardConfig.getId(), boardConfig);
            return map;
        },
        "Here you can create your own scoreboard format(s) for different arenas.",
        "To set the scoreboard format per arena, use in-game editor.",
        "You can use 'Arena', 'Arena Player' placeholders: " + Placeholders.WIKI_AMA_PLACEHOLDERS,
        "PlaceholderAPI is also supported here."
    ).setWriter((cfg, path, map) -> map.forEach((id, board) -> ArenaBoardConfig.write(board, cfg, path + "." + id)));
}
