package su.nightexpress.ama.hook.external;

import com.gmail.nossr50.events.experience.McMMOPlayerXpGainEvent;
import com.gmail.nossr50.events.skills.abilities.McMMOPlayerAbilityActivateEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.hook.AbstractHook;
import su.nightexpress.ama.AMA;
import su.nightexpress.ama.arena.ArenaPlayer;
import su.nightexpress.ama.arena.AbstractArena;

public class McMMOHook extends AbstractHook<AMA> {

    public McMMOHook(@NotNull AMA plugin, @NotNull String pluginName) {
        super(plugin, pluginName);
    }

    @Override
    public boolean setup() {
        this.registerListeners();
        return true;
    }

    @Override
    public void shutdown() {
        this.unregisterListeners();
    }

    @EventHandler
    public void onUseSkill(McMMOPlayerAbilityActivateEvent e) {
        Player p = e.getPlayer();
        ArenaPlayer arenaPlayer = ArenaPlayer.getPlayer(p);
        if (arenaPlayer == null) return;

        AbstractArena arena = arenaPlayer.getArena();
        if (!arena.getConfig().getGameplayManager().isExternalMcmmoEnabled()) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onExpGain(McMMOPlayerXpGainEvent e) {
        Player p = e.getPlayer();
        ArenaPlayer arenaPlayer = ArenaPlayer.getPlayer(p);
        if (arenaPlayer == null) return;

        AbstractArena arena = arenaPlayer.getArena();
        if (!arena.getConfig().getGameplayManager().isExternalMcmmoEnabled()) {
            e.setCancelled(true);
        }
    }
}
