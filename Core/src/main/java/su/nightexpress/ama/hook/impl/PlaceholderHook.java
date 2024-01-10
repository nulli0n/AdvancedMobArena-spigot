package su.nightexpress.ama.hook.impl;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nexmedia.engine.lang.LangManager;
import su.nexmedia.engine.utils.NumberUtil;
import su.nexmedia.engine.utils.StringUtil;
import su.nexmedia.engine.utils.TimeUtil;
import su.nightexpress.ama.AMA;
import su.nightexpress.ama.api.type.GameState;
import su.nightexpress.ama.api.type.PlayerType;
import su.nightexpress.ama.arena.impl.Arena;
import su.nightexpress.ama.arena.impl.ArenaPlayer;
import su.nightexpress.ama.data.impl.ArenaUser;
import su.nightexpress.ama.stats.object.StatType;

import java.time.LocalTime;

public class PlaceholderHook {

    public static final String ID = "ama";

    private static AMAExpansion expansion;

    public static void setup(@NotNull AMA plugin) {
        if (expansion == null) {
            expansion = new AMAExpansion(plugin);
            expansion.register();
        }
    }

    public void shutdown() {
        if (expansion != null) {
            expansion.unregister();
            expansion = null;
        }
    }

    private static class AMAExpansion extends PlaceholderExpansion {

        private final AMA plugin;

        public AMAExpansion(@NotNull AMA plugin) {
            this.plugin = plugin;
        }

        @Override
        @NotNull
        public String getAuthor() {
            return plugin.getDescription().getAuthors().get(0);
        }

        @Override
        @NotNull
        public String getIdentifier() {
            return ID;
        }

        @Override
        @NotNull
        public String getVersion() {
            return plugin.getDescription().getVersion();
        }

        @Override
        public boolean persist() {
            return true;
        }

        @Override
        public String onPlaceholderRequest(Player player, String params) {
            if (params.startsWith("stats_")) {
                String typeRaw = params.substring("stats_".length());

                StatType type = StringUtil.getEnum(typeRaw, StatType.class).orElse(null);
                if (type == null) return "-";

                ArenaUser user = plugin.getUserManager().getUserData(player);
                return NumberUtil.format(user.getStats(type));
            }
            if (params.equalsIgnoreCase("coins")) {
                ArenaUser user = plugin.getUserManager().getUserData(player);
                return NumberUtil.format(user.getCoins());
            }
            if (params.equalsIgnoreCase("coins_raw")) {
                ArenaUser user = plugin.getUserManager().getUserData(player);
                return String.valueOf(user.getCoins());
            }

            if (params.startsWith("game_")) {
                ArenaPlayer arenaPlayer = ArenaPlayer.getPlayer(player);
                if (arenaPlayer == null) return "-";

                Arena arena = arenaPlayer.getArena();
                String var = params.substring("game_".length());

                if (var.equalsIgnoreCase("score")) {
                    return NumberUtil.format(arenaPlayer.getScore());
                }
                if (var.equalsIgnoreCase("streak_length")) {
                    return NumberUtil.format(arenaPlayer.getKillStreak());
                }
                if (var.equalsIgnoreCase("streak_decay")) {
                    LocalTime timeStreak = TimeUtil.getLocalTimeOf(arenaPlayer.getKillStreakDecay());
                    return timeStreak.format(ArenaPlayer.FORMAT_STREAK);
                }
                if (var.equalsIgnoreCase("kills")) {
                    return NumberUtil.format(arenaPlayer.getStats(StatType.MOB_KILLS));
                }
                return forArena(arena, var);
            }


            if (params.startsWith("arena_")) { // ama_arena_tutorial_name
                String raw = params.substring("arena_".length()); // tutorial_name_something
                int index = raw.indexOf("_");
                if (index < 0) return null;

                String arenaId = raw.substring(0, index); // tutorial
                String var = raw.substring(index + 1); // name_something
                Arena arena = plugin.getArenaManager().getArenaById(arenaId);
                if (arena == null) return "-";

                return forArena(arena, var);
            }

            return null;
        }

        @Nullable
        public String forArena(@NotNull Arena arena, @NotNull String var) {
            if (var.equalsIgnoreCase("name")) {
                return arena.getConfig().getName();
            }
            if (var.equalsIgnoreCase("empty")) {
                return LangManager.getBoolean(arena.getPlayers().all().isEmpty());
            }
            if (var.equalsIgnoreCase("state")) {
                return plugin.getLangManager().getEnum(arena.getState());
            }

            if (var.equalsIgnoreCase("mobs_alive")) {
                return NumberUtil.format(arena.getMobs().getEnemies().size());
            }
            if (var.equalsIgnoreCase("mobs_total")) {
                return NumberUtil.format(arena.getRoundTotalMobsAmount());
            }
            if (var.equalsIgnoreCase("mobs_left")) {
                return NumberUtil.format(arena.getMobsAwaitingSpawn());
            }

            if (var.equalsIgnoreCase("players_alive")) {
                return NumberUtil.format(arena.getPlayers().getAlive().size());
            }
            if (var.equalsIgnoreCase("players_dead")) {
                return NumberUtil.format(arena.getPlayers().getDead().size());
            }
            if (var.equalsIgnoreCase("players_ghost")) {
                return NumberUtil.format(arena.getPlayers().select(GameState.INGAME, PlayerType.GHOST).size());
            }
            if (var.equalsIgnoreCase("players_playing")) {
                return NumberUtil.format(arena.getPlayers().select(GameState.INGAME).size());
            }
            if (var.equalsIgnoreCase("players_total")) {
                return NumberUtil.format(arena.getPlayers().all().size());
            }

            if (var.equalsIgnoreCase("round_number")) {
                return NumberUtil.format(arena.getRoundNumber());
            }
            if (var.equalsIgnoreCase("next_round_countdown")) {
                return NumberUtil.format(arena.getNextRoundCountdown());
            }

            if (var.equalsIgnoreCase("timeleft")) {
                return TimeUtil.getLocalTimeOf(arena.getGameTimeleft()).format(Arena.FORMAT_TIMELEFT);
            }
            if (var.equalsIgnoreCase("score")) {
                return NumberUtil.format(arena.getGameScore());
            }
            return null;
        }
    }
}
