package su.nightexpress.ama.arena.impl;

import org.bukkit.Location;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.ama.Placeholders;
import su.nightexpress.ama.arena.region.Region;
import su.nightexpress.ama.arena.wave.impl.WaveMob;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ArenaUpcomingWave {

    private final List<WaveMob>  mobs;
    private final List<Location> spawners;

    public ArenaUpcomingWave(@NotNull Region region, @NotNull List<WaveMob> mobs, @NotNull List<String> spawnerGroups) {
        this.mobs = mobs;
        this.spawners = new ArrayList<>();

        spawnerGroups.forEach(spawnerGroup -> {
            this.spawners.addAll(region.getMobSpawnersLocations(spawnerGroup));
        });

        if (this.spawners.isEmpty() || spawnerGroups.contains(Placeholders.WILDCARD)) {
            World world = region.getArenaConfig().getWorld();

            region.getMobSpawners().values().forEach(set -> {
                set.forEach(pos -> this.spawners.add(pos.toLocation(world)));
            });
        }

        Collections.shuffle(this.spawners);
    }

    public boolean isAllMobsSpawned() {
        return this.getPreparedMobs().stream().allMatch(mob -> mob.getAmount() <= 0);
    }

    public int getMobsAmount() {
        return this.getPreparedMobs().stream().mapToInt(WaveMob::getAmount).sum();
    }

    @NotNull
    public List<WaveMob> getPreparedMobs() {
        return this.mobs;
    }

    @NotNull
    public List<Location> getPreparedSpawners() {
        return spawners;
    }
}
