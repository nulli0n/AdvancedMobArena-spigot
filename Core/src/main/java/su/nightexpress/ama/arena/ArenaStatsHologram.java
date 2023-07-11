package su.nightexpress.ama.arena;

import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.ama.Placeholders;
import su.nightexpress.ama.api.arena.ArenaChild;
import su.nightexpress.ama.api.hologram.HologramHolder;
import su.nightexpress.ama.api.hologram.HologramType;
import su.nightexpress.ama.arena.impl.ArenaConfig;
import su.nightexpress.ama.hologram.HologramManager;
import su.nightexpress.ama.stats.object.StatType;
import su.nightexpress.ama.stats.object.StatsScore;

import java.util.*;

public class ArenaStatsHologram implements ArenaChild, HologramHolder {

    private final ArenaConfig arenaConfig;
    private final StatType    statType;
    private final Set<UUID>     hologramIds;
    private final Set<Location> hologramLocations;

    public ArenaStatsHologram(@NotNull ArenaConfig arenaConfig, @NotNull StatType statType, @NotNull Set<Location> hologramLocations) {
        this.arenaConfig = arenaConfig;
        this.statType = statType;
        this.hologramIds = new HashSet<>();
        this.hologramLocations = new HashSet<>(hologramLocations);
    }

    @Override
    @NotNull
    public ArenaConfig getArenaConfig() {
        return arenaConfig;
    }

    @NotNull
    public StatType getStatType() {
        return statType;
    }

    @NotNull
    @Override
    public HologramType getHologramType() {
        return HologramType.ARENA_STATS;
    }

    @NotNull
    @Override
    public Set<UUID> getHologramIds() {
        return hologramIds;
    }

    @NotNull
    @Override
    public Set<Location> getHologramLocations() {
        return hologramLocations;
    }

    @NotNull
    @Override
    public List<String> getHologramFormat() {
        List<String> text = HologramManager.getFormat(this.getHologramType());
        List<String> format = new ArrayList<>();
        for (String line : text) {
            if (line.contains(Placeholders.STATS_SCORE_NAME)) {
                int pos = 1;
                for (StatsScore score : arenaConfig.plugin().getStatsManager().getScores(statType, 10, arenaConfig.getId())) {
                    format.add(score.replacePlaceholders(pos++).apply(line));
                }
                continue;
            }
            line = line.replace(Placeholders.STATS_SCORE_TYPE, arenaConfig.plugin().getLangManager().getEnum(statType));
            line = arenaConfig.replacePlaceholders().apply(line);
            format.add(line);
        }
        return format;
    }
}
