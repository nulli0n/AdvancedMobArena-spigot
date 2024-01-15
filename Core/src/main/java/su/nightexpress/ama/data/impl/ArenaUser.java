package su.nightexpress.ama.data.impl;

import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.data.AbstractUser;
import su.nightexpress.ama.AMA;
import su.nightexpress.ama.arena.impl.Arena;
import su.nightexpress.ama.kit.impl.Kit;
import su.nightexpress.ama.stats.object.StatType;

import javax.annotation.Nullable;
import java.util.*;

public class ArenaUser extends AbstractUser<AMA> {

    private int                                 coins;
    private Set<String>                         kits;
    private Map<String, Map<StatType, Integer>> stats;

    private final Map<String, Long> arenaCooldownMap;

    public ArenaUser(@NotNull AMA plugin, @NotNull UUID uuid, @NotNull String name) {
        this(plugin, uuid, name, System.currentTimeMillis(), System.currentTimeMillis(),
            0, // Coins
            new HashSet<>(), // Kits
            new HashMap<>(), // Stats
            new HashMap<>() // Arena Cooldown
        );
    }

    public ArenaUser(
        @NotNull AMA plugin,
        @NotNull UUID uuid,
        @NotNull String name,
        long dateCreated,
        long lastLogin,

        int coins,
        @NotNull Set<String> kits,
        @NotNull Map<String, Map<StatType, Integer>> stats,
        @NotNull Map<String, Long> arenaCooldownMap
    ) {
        super(plugin, uuid, name, dateCreated, lastLogin);
        this.setCoins(coins);
        this.setKits(kits);
        this.setStats(stats);
        this.arenaCooldownMap = new HashMap<>(arenaCooldownMap);
    }

    public int getCoins() {
        return this.coins;
    }

    public void setCoins(int coins) {
        this.coins = Math.max(0, coins);
    }

    public void takeCoins(int amount) {
        this.setCoins(this.getCoins() - amount);
    }

    public void addCoins(int amount) {
        this.setCoins(this.getCoins() + amount);
    }

    @NotNull
    public Set<String> getKits() {
        return this.kits;
    }

    public void setKits(@NotNull Set<String> kits) {
        this.kits = kits;
    }

    public boolean addKit(@NotNull Kit kit) {
        return this.addKit(kit.getId());
    }

    public boolean addKit(@NotNull String kit) {
        return this.getKits().add(kit.toLowerCase());
    }

    public boolean hasKit(@NotNull Kit kit) {
        return this.hasKit(kit.getId());
    }

    public boolean hasKit(@NotNull String kit) {
        return this.getKits().contains(kit.toLowerCase());
    }

    public boolean removeKit(@NotNull Kit kit) {
        return this.removeKit(kit.getId());
    }

    public boolean removeKit(@NotNull String kit) {
        return this.getKits().remove(kit.toLowerCase());
    }

    @NotNull
    public Map<String, Map<StatType, Integer>> getStats() {
        return this.stats;
    }

    @NotNull
    public Map<StatType, Integer> getStats(@NotNull String arena) {
        return this.stats.computeIfAbsent(arena.toLowerCase(), map -> new HashMap<>());
    }

    public int getStats(@NotNull StatType type) {
        return this.getStats(type, null);
    }

    public int getStats(@NotNull StatType type, @Nullable String arena) {
        if (arena != null) {
            return this.getStats(arena).computeIfAbsent(type, score -> 0);
        }

        int count = 0;
        for (Map<StatType, Integer> map : this.stats.values()) {
            count += map.computeIfAbsent(type, score -> 0);
        }
        return count;
    }

    public void setStats(@NotNull Map<String, Map<StatType, Integer>> stats) {
        this.stats = stats;
    }

    @NotNull
    public Map<String, Long> getArenaCooldownMap() {
        this.arenaCooldownMap.values().removeIf(date -> System.currentTimeMillis() > date);
        return this.arenaCooldownMap;
    }

    public boolean isOnCooldown(@NotNull Arena arena) {
        return this.isOnCooldown(arena.getId());
    }

    public boolean isOnCooldown(@NotNull String arenaId) {
        return this.getArenaCooldown(arenaId) > System.currentTimeMillis();
    }

    public long getArenaCooldown(@NotNull Arena arena) {
        return this.getArenaCooldown(arena.getId());
    }

    public long getArenaCooldown(@NotNull String arenaId) {
        return this.getArenaCooldownMap().getOrDefault(arenaId.toLowerCase(), 0L);
    }

    public void setArenaCooldown(@NotNull Arena arena, long expireDate) {
        this.setArenaCooldown(arena.getId(), expireDate);
    }

    public void setArenaCooldown(@NotNull String arenaId, long expireDate) {
        this.getArenaCooldownMap().put(arenaId.toLowerCase(), expireDate);
    }
}
