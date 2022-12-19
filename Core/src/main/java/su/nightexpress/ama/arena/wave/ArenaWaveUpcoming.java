package su.nightexpress.ama.arena.wave;

import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.ama.Placeholders;
import su.nightexpress.ama.arena.region.ArenaRegionWave;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ArenaWaveUpcoming {

    private final ArenaRegionWave    regionWave;
    private final List<ArenaWaveMob> mobs;
    private final List<Location>     spawners;

    public ArenaWaveUpcoming(@NotNull ArenaRegionWave regionWave, @NotNull List<ArenaWaveMob> mobs) {
        this.regionWave = regionWave;
        this.mobs = mobs;

        this.spawners = regionWave.getRegion().getMobSpawners().entrySet().stream()
            .filter(entry -> regionWave.getSpawnerIds().contains(entry.getKey()) || regionWave.getSpawnerIds().contains(Placeholders.WILDCARD))
            .map(Map.Entry::getValue).collect(Collectors.toCollection(ArrayList::new));
        Collections.shuffle(this.spawners);
    }

    public boolean isAllMobsSpawned() {
        return this.getPreparedMobs().stream().allMatch(mob -> mob.getAmount() <= 0);
    }

    @NotNull
    public ArenaRegionWave getRegionWave() {
        return regionWave;
    }

    @NotNull
    public List<ArenaWaveMob> getPreparedMobs() {
        return this.mobs;
    }

    @NotNull
    public List<Location> getPreparedSpawners() {
        return spawners;
    }
}
