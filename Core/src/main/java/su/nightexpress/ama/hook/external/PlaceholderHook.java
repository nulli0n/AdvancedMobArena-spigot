package su.nightexpress.ama.hook.external;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.utils.CollectionsUtil;
import su.nexmedia.engine.utils.NumberUtil;
import su.nexmedia.engine.utils.TimeUtil;
import su.nightexpress.ama.AMA;
import su.nightexpress.ama.api.ArenaAPI;
import su.nightexpress.ama.arena.AbstractArena;
import su.nightexpress.ama.arena.ArenaPlayer;
import su.nightexpress.ama.data.ArenaUser;
import su.nightexpress.ama.stats.object.StatType;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class PlaceholderHook {

    private static AMAExpansion expansion;

    public static void setup() {
        if (expansion == null) {
            expansion = new AMAExpansion();
            expansion.register();
        }
    }

    public void shutdown() {
        if (expansion != null) {
            expansion.unregister();
            expansion = null;
        }
    }

    static class AMAExpansion extends PlaceholderExpansion {

        @Override
        @NotNull
        public String getAuthor() {
            return ArenaAPI.PLUGIN.getDescription().getAuthors().get(0);
        }

        @Override
        @NotNull
        public String getIdentifier() {
            return "ama";
        }

        @Override
        @NotNull
        public String getVersion() {
            return ArenaAPI.PLUGIN.getDescription().getVersion();
        }

        @Override
        public boolean persist() {
            return true;
        }

        @Override
        public String onPlaceholderRequest(Player player, String tmp) {
            AMA plugin = ArenaAPI.PLUGIN;

            if (tmp.startsWith("stats_")) {
                String typeRaw = tmp.replace("stats_", "");

                StatType type = CollectionsUtil.getEnum(typeRaw, StatType.class);
                if (type == null) return "NaN";

                ArenaUser user = plugin.getUserManager().getUserData(player);
                return String.valueOf(user.getStats(type));
            }
            if (tmp.equalsIgnoreCase("coins")) {
                ArenaUser user = plugin.getUserManager().getUserData(player);
                return NumberUtil.format(user.getCoins());
            }
            if (tmp.equalsIgnoreCase("coins_raw")) {
                ArenaUser user = plugin.getUserManager().getUserData(player);
                return String.valueOf(user.getCoins());
            }
            if (tmp.startsWith("arena_")) {
                ArenaPlayer arenaPlayer = ArenaPlayer.getPlayer(player);
                if (arenaPlayer == null) return "-";

                AbstractArena arena = arenaPlayer.getArena();
                String var = tmp.replace("arena_", "");

                if (var.equalsIgnoreCase("mobs")) { // TODO mobs/ total mobs
                    return String.valueOf(arena.getMobs().size());
                }
                if (var.equalsIgnoreCase("name")) {
                    return arena.getConfig().getName();
                }
                if (var.equalsIgnoreCase("score")) {
                    return String.valueOf(arenaPlayer.getScore());
                }
                if (var.equalsIgnoreCase("streak_length")) {
                    return String.valueOf(arenaPlayer.getKillStreak());
                }
                if (var.equalsIgnoreCase("streak_decay")) {
                    DateTimeFormatter FORMAT_STREAK = DateTimeFormatter.ofPattern("ss");
                    LocalTime timeStreak = TimeUtil.getLocalTimeOf(arenaPlayer.getKillStreakDecay());
                    return timeStreak.format(FORMAT_STREAK);
                }
                if (var.equalsIgnoreCase("kills")) {
                    return String.valueOf(arenaPlayer.getStats(StatType.MOB_KILLS));
                }
                if (var.equalsIgnoreCase("timeleft")) {
                    DateTimeFormatter FORMAT_TIMELEFT = DateTimeFormatter.ofPattern("HH:mm:ss");
                    return TimeUtil.getLocalTimeOf(arena.getGameTimeleft()).format(FORMAT_TIMELEFT);
                }
                if (var.equalsIgnoreCase("wave")) {
                    return String.valueOf(arena.getWaveNumber());
                }
                if (var.equalsIgnoreCase("players")) {
                    return String.valueOf(arena.getPlayersIngame().size());
                }
	        	/*if (var.equalsIgnoreCase("balance")) {
	        		return String.valueOf(plugin.getEconomy().getBalance(player));
	        	}*/
                if (var.equalsIgnoreCase("next_wave")) {
                    return String.valueOf(arena.getWaveNextTimeleft());
                }
            }

            return null;
        }
    }
}
