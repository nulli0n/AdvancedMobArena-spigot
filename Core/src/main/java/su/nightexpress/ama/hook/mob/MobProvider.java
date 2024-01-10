package su.nightexpress.ama.hook.mob;

import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.ama.arena.impl.Arena;

import java.util.List;
import java.util.Optional;

public interface MobProvider {

    @NotNull String getName();

    @NotNull Optional<LivingEntity> spawn(@NotNull Arena arena, @NotNull String mobId, @NotNull Location location, int level);

    @NotNull List<String> getMobNames();
}
