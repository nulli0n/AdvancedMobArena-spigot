package su.nightexpress.ama.hook.mob.impl;

import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.ama.AMA;
import su.nightexpress.ama.api.type.MobFaction;
import su.nightexpress.ama.arena.impl.Arena;
import su.nightexpress.ama.hook.mob.MobProvider;

import java.util.List;
import java.util.Optional;

public class InternalMobProvider implements MobProvider {

    public static final String NAME = "AMA";

    private final AMA plugin;

    public InternalMobProvider(@NotNull AMA plugin) {
        this.plugin = plugin;
    }

    @NotNull
    @Override
    public String getName() {
        return NAME;
    }

    @NotNull
    @Override
    public Optional<LivingEntity> spawn(@NotNull Arena arena, @NotNull String mobId, @NotNull Location location, int level) {
        return Optional.ofNullable(plugin.getMobManager().spawnMob(arena, MobFaction.ENEMY, mobId, location, level));
    }

    @NotNull
    @Override
    public List<String> getMobNames() {
        return plugin.getMobManager().getMobIds();
    }
}
