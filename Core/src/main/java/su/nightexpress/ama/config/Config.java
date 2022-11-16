package su.nightexpress.ama.config;

import org.bukkit.inventory.ItemStack;
import su.nexmedia.engine.api.config.JOption;
import su.nexmedia.engine.utils.StringUtil;
import su.nightexpress.ama.Placeholders;
import su.nightexpress.ama.api.hologram.HologramType;
import su.nightexpress.ama.arena.LobbyItem;
import su.nightexpress.ama.arena.board.ArenaBoardConfig;
import su.nightexpress.ama.hook.HookId;
import su.nightexpress.ama.sign.type.SignType;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Config {

    public static final JOption<Boolean> DEBUG_MOB_SPAWN = JOption.create("Debug.Mob_Spawning", "Enable this setting if you're experiencing issues with mob spawn on arenas.\nThen send debug logs from the console to the developer.", false);

    public static final JOption<Boolean> CHAT_ENABLED = JOption.create("Chat.Enabled", "When enabled, players in arenas will have their own chat.", false);
    public static final JOption<String>  CHAT_FORMAT  = JOption.create("Chat.Format", "Sets the chat format. You can use 'Arena', 'Arena Player' placeholders here: https://github.com/nulli0n/AdvancedMobArena-spigot/wiki/Internal-Placeholders\nDefault Placeholders:\n- %player_name% - Player name.\n- %message% - Message text.\nPlaceholderAPI is also supported.", "&7(&6" + Placeholders.PLAYER_KIT_NAME + "&7) &a" + Placeholders.Player.NAME + ": &f" + Placeholders.GENERIC_MESSAGE);

    public static final JOption<Integer>                        LOBBY_READY_DROP_TIMER                = JOption.create("Lobby.Ready_State.Drop_Timer_To", "Instantly decreases lobby countdown timer to the specified value, if all players are ready to play.\nSet this to -1 to disable feature.", 15);
    public static final JOption<Boolean>                        LOBBY_READY_FREEZE_TIMER_WHEN_DROPPED = JOption.create("Lobby.Ready_State.Freeze_Dropped_Timer_When_Not_Ready", "When enabled, lobby timer will be stopped, when someone changes his state as 'not ready'.\nThis option will only have effect if lobby timer was already decreased by the setting above.", false);
    public static final JOption<Map<LobbyItem.Type, LobbyItem>> LOBBY_ITEMS                           = new JOption<Map<LobbyItem.Type, LobbyItem>>("Lobby.Items",
        "Lobby items settings.\nDisabled items won't be given to players and will have no effect.\nSlot is where lobby item will be added in player's inventory.\nFor 'Item' option, please check: https://github.com/nulli0n/NexEngine-spigot/wiki/Configuration-Tips#item-sections",
        (cfg, path, def) -> {
            return Stream.of(LobbyItem.Type.values()).collect(Collectors.toMap(k -> k, v -> {
                boolean isEnabled = cfg.getBoolean(path + "." + v.name() + ".Enabled");
                int slot = cfg.getInt(path + "." + v.name() + ".Slot");
                ItemStack item = cfg.getItem(path + "." + v.name() + ".Item");
                return new LobbyItem(v, isEnabled, item, slot);
            }));
        }, () -> {
        return Stream.of(LobbyItem.Type.values()).collect(Collectors.toMap(k -> k, v -> {
            return new LobbyItem(v, true, v.getDefaultItem(), v.getDefaultSlot());
        }));
    });

    public static final JOption<Boolean>                     SIGNS_ENABLED = JOption.create("Signs.Enabled", "Enables/Disables usable signs feature.", true);
    public static final JOption<Boolean>                     SIGNS_GLOWING = JOption.create("Signs.Glowing", "Makes the sign text glowing.", true);
    public static final JOption<Map<SignType, List<String>>> SIGNS_FORMAT  = new JOption<Map<SignType, List<String>>>("Signs.Format", "Text to dispay on arena signs.\nDepends on a Sign Type, you can use different placeholders: https://github.com/nulli0n/AdvancedMobArena-spigot/wiki/Internal-Placeholders", (cfg, path, def) -> {
        return Stream.of(SignType.values()).collect(Collectors.toMap(k -> k, v -> StringUtil.color(cfg.getStringList(path + "." + v.name()))));
    }, () -> {
        return Stream.of(SignType.values()).collect(Collectors.toMap(k -> k, SignType::getDefaultText));
    });

    public static final JOption<Boolean>                         HOLOGRAMS_ENABLED = JOption.create("Holograms.Enabled", "Enables the Holograms feature.\nSupported Plugins: " + HookId.HOLOGRAPHIC_DISPLAYS + ", " + HookId.DECENT_HOLOGRAMS, true);
    public static final JOption<Map<HologramType, List<String>>> HOLOGRAMS_FORMAT  = new JOption<Map<HologramType, List<String>>>("Holograms.Format", "Text to dispay on arena holograms.\nDepends on a Hologram Type, you can use different placeholders: https://github.com/nulli0n/AdvancedMobArena-spigot/wiki/Internal-Placeholders", (cfg, path, def) -> {
        return Stream.of(HologramType.values()).collect(Collectors.toMap(k -> k, v -> StringUtil.color(cfg.getStringList(path + "." + v.name()))));
    }, () -> {
        return Stream.of(HologramType.values()).collect(Collectors.toMap(k -> k, HologramType::getDefaultFormat));
    });

    public static final JOption<Map<String, ArenaBoardConfig>> SCOREBOARDS = new JOption<Map<String, ArenaBoardConfig>>("Scoreboard",
        "Here you can create your own scoreboard format(s) for different arenas.\nTo set the scoreboard format per arena, use in-game editor.\nYou can use 'Arena', 'Arena Player' placeholders: https://github.com/nulli0n/AdvancedMobArena-spigot/wiki/Internal-Placeholders\nPlaceholderAPI is also supported here.",
        (cfg, path, def) -> {
            Map<String, ArenaBoardConfig> map = new HashMap<>();
            for (String sId : cfg.getSection(path)) {
                String path2 = path + "." + sId + ".";
                String title = cfg.getString(path2 + "Title", "");
                List<String> lines = cfg.getStringList(path2 + "List");
                ArenaBoardConfig boardConfig = new ArenaBoardConfig(sId, title, lines);
                map.put(boardConfig.getId(), boardConfig);
            }
            return map;
        }, () -> {
        Map<String, ArenaBoardConfig> map = new HashMap<>();
        ArenaBoardConfig boardConfig = new ArenaBoardConfig(Placeholders.DEFAULT, "&c&lMOB ARENA", Arrays.asList(
            "&c▸ &7Arena: &c" + Placeholders.ARENA_NAME, "&c▸ &7Current wave: &c" + Placeholders.ARENA_WAVE_NUMBER,
            "&c▸ &7Mobs: &a" + Placeholders.ARENA_MOBS_ALIVE + "&7/&e" + Placeholders.ARENA_MOBS_LEFT + "&7/&c" + Placeholders.ARENA_MOBS_TOTAL,
            "&c▸ &7Players: &c" + Placeholders.ARENA_PLAYERS + "&7/&c" + Placeholders.ARENA_PLAYERS_MAX,
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
    });
}
