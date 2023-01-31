package su.nightexpress.ama.arena.listener;

import io.lumine.mythic.bukkit.events.MythicMobSpawnEvent;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.manager.AbstractListener;
import su.nightexpress.ama.AMA;
import su.nightexpress.ama.api.arena.type.ArenaState;
import su.nightexpress.ama.arena.AbstractArena;
import su.nightexpress.ama.mob.config.MobsConfig;

public class ArenaMythicListener extends AbstractListener<AMA> {

    public ArenaMythicListener(@NotNull AMA plugin) {
        super(plugin);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onMythicSpawn(MythicMobSpawnEvent e) {
        if (!(e.getEntity() instanceof LivingEntity entity)) return;

        Location location = entity.getLocation();
        AbstractArena arena = plugin.getArenaManager().getArenaAtLocation(location);
        if (arena == null || !arena.getConfig().isActive()) return;

        if (arena.getState() == ArenaState.INGAME) {
            if (MobsConfig.ALLY_MYTHIC_MOBS.get().contains(e.getMobType().getInternalName())) {
                if (arena.getMobs().remove(entity)) {
                    arena.setWaveMobsTotalAmount(arena.getWaveMobsTotalAmount() - 1);
                }
                arena.getAllyMobs().add(entity);
            }
        }
    }
}
