package su.nightexpress.ama.arena.util;

import org.bukkit.boss.BossBar;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.ScoreboardManager;
import org.bukkit.scoreboard.Team;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nightexpress.ama.api.ArenaAPI;
import su.nightexpress.ama.arena.impl.Arena;
import su.nightexpress.ama.arena.impl.ArenaPlayer;
import su.nightexpress.ama.api.type.PlayerType;
import su.nightexpress.ama.hook.pet.PluginPetProvider;

import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class ArenaUtils {

    public static final  DateTimeFormatter  TIME_FORMATTER = DateTimeFormatter.ISO_LOCAL_TIME;
    private static final Map<UUID, BossBar> MOB_BARS       = new HashMap<>();

    @NotNull
    public static Optional<Team> getHighlightTeam(@NotNull Arena arena, boolean create) {
        ScoreboardManager scoreboardManager = ArenaAPI.PLUGIN.getServer().getScoreboardManager();
        if (scoreboardManager == null) return Optional.empty();

        String teamId = "mob_hl_" + arena.getId();
        if (teamId.length() > 16) teamId = teamId.substring(0, 16);

        Team team = scoreboardManager.getMainScoreboard().getTeam(teamId);
        if (team == null) {
            if (create) team = scoreboardManager.getMainScoreboard().registerNewTeam(teamId);
            else return Optional.empty();
        }

        team.setColor(arena.getConfig().getGameplayManager().getMobHighlightColor());
        return Optional.of(team);
    }

    @Nullable
    public static BossBar getMobBossBar(@NotNull LivingEntity entity) {
        return getMobBossBar(entity.getUniqueId());
    }

    @Nullable
    public static BossBar getMobBossBar(@NotNull UUID id) {
        return MOB_BARS.get(id);
    }

    public static void addMobBossBar(@NotNull Arena arena, @NotNull LivingEntity mob, @NotNull BossBar bossBar) {
        arena.getPlayers().select(PlayerType.REAL).stream().map(ArenaPlayer::getPlayer).forEach(bossBar::addPlayer);
        MOB_BARS.put(mob.getUniqueId(), bossBar);
    }

    public static void removeMobBossBars(@NotNull Player player) {
        MOB_BARS.values().forEach(bossBar -> bossBar.removePlayer(player));
    }

    public static void removeMobBossBar(@NotNull LivingEntity entity) {
        removeMobBossBar(entity.getUniqueId());
    }

    public static void removeMobBossBar(@NotNull UUID id) {
        BossBar bossBar = getMobBossBar(id);
        if (bossBar == null) return;

        bossBar.removeAll();
        MOB_BARS.remove(id);
    }

    public static boolean isPet(@NotNull LivingEntity entity) {
        return PluginPetProvider.getProviders().stream().anyMatch(provider -> provider.isPet(entity));
    }
}
