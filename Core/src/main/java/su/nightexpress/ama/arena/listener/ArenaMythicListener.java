package su.nightexpress.ama.arena.listener;

import io.lumine.mythic.bukkit.events.MythicMobSpawnEvent;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.manager.AbstractListener;
import su.nightexpress.ama.AMA;
import su.nightexpress.ama.arena.impl.Arena;
import su.nightexpress.ama.arena.type.GameState;
import su.nightexpress.ama.mob.config.MobsConfig;

public class ArenaMythicListener extends AbstractListener<AMA> {

    public ArenaMythicListener(@NotNull AMA plugin) {
        super(plugin);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onMythicSpawn(MythicMobSpawnEvent e) {
        if (!(e.getEntity() instanceof LivingEntity entity)) return;

        Location location = entity.getLocation();
        Arena arena = plugin.getArenaManager().getArenaAtLocation(location);
        if (arena == null || !arena.getConfig().isActive()) return;

        if (arena.getState() == GameState.INGAME && !arena.isAboutToEnd()) {
            if (MobsConfig.ALLY_MYTHIC_MOBS.get().contains(e.getMobType().getInternalName())) {
                if (arena.getMobs().remove(entity)) {
                    arena.setWaveMobsTotalAmount(arena.getWaveMobsTotalAmount() - 1);
                }
                arena.getAllyMobs().add(entity);
            }
        }
        else {
            e.setCancelled();
            entity.remove();
        }
    }

    /*@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onMythicSkill(PlayerCastSkillEvent e) {
        Player player = e.getPlayer();
        ArenaPlayer arenaPlayer = ArenaPlayer.getPlayer(player);
        if (arenaPlayer != null && arenaPlayer.getArena().isAboutToFinish()) {
            e.setCancelled(true);
        }
    }*/
}
