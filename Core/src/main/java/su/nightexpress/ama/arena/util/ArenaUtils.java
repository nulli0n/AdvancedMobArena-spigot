package su.nightexpress.ama.arena.util;

import org.bukkit.entity.LivingEntity;
import org.bukkit.scoreboard.ScoreboardManager;
import org.bukkit.scoreboard.Team;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.ama.api.ArenaAPI;
import su.nightexpress.ama.arena.impl.Arena;
import su.nightexpress.ama.hook.pet.PluginPetProvider;

import java.time.format.DateTimeFormatter;
import java.util.Optional;

public class ArenaUtils {

    public static final  DateTimeFormatter  TIME_FORMATTER = DateTimeFormatter.ISO_LOCAL_TIME;

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

        team.setColor(arena.getConfig().getGameplaySettings().getMobHighlightColor());
        return Optional.of(team);
    }

    public static boolean isPet(@NotNull LivingEntity entity) {
        return PluginPetProvider.getProviders().stream().anyMatch(provider -> provider.isPet(entity));
    }
}
