package su.nightexpress.ama.arena.impl;

import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.ama.arena.region.ArenaRegion;
import su.nightexpress.ama.arena.wave.ArenaWaveMob;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ArenaUpcomingWave {

    private final List<ArenaWaveMob> mobs;
    private final List<Location>     spawners;

    public ArenaUpcomingWave(@NotNull ArenaRegion region, @NotNull List<ArenaWaveMob> mobs) {
        this.mobs = mobs;
        this.spawners = new ArrayList<>(region.getMobSpawners().values());
        Collections.shuffle(this.spawners);
    }

    public boolean isAllMobsSpawned() {
        return this.getPreparedMobs().stream().allMatch(mob -> mob.getAmount() <= 0);
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
