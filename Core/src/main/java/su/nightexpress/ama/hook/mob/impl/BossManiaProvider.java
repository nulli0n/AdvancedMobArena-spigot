package su.nightexpress.ama.hook.mob.impl;

import it.dado997.BossMania.BossMania;
import it.dado997.BossMania.Objects.Boss;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.ama.arena.impl.Arena;
import su.nightexpress.ama.hook.HookId;
import su.nightexpress.ama.hook.mob.MobProvider;

import java.util.List;
import java.util.Optional;

public class BossManiaProvider implements MobProvider {

    @NotNull
    @Override
    public String getName() {
        return HookId.BOSS_MANIA;
    }

    @NotNull
    @Override
    public Optional<LivingEntity> spawn(@NotNull Arena arena, @NotNull String mobId, @NotNull Location location, int level) {
        Boss boss = BossMania.api.getBosses().find(mobId);
        if (boss == null) return Optional.empty();

        return Optional.of(boss.spawn(location).getLivingEntity());
    }

    @NotNull
    @Override
    public List<String> getMobNames() {
        return BossMania.api.getBosses().stream().map(Boss::getKey).toList();
    }
}
