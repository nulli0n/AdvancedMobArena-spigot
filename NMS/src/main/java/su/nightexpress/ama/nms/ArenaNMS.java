package su.nightexpress.ama.nms;

import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nightexpress.ama.api.arena.IArena;
import su.nightexpress.ama.api.type.MobFaction;

public interface ArenaNMS {

    @Nullable LivingEntity spawnMob(@NotNull IArena arena, @NotNull MobFaction faction, @NotNull EntityType type, @NotNull Location location);

    int visualEntityAdd(@NotNull Player player, @NotNull String name, @NotNull Location loc);

    int visualGlowBlockAdd(@NotNull Player player, @NotNull Location loc);

    void visualEntityRemove(@NotNull Player player, int... id);
}
