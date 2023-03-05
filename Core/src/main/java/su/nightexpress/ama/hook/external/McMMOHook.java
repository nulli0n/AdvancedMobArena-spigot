package su.nightexpress.ama.hook.external;

import com.gmail.nossr50.events.experience.McMMOPlayerXpGainEvent;
import com.gmail.nossr50.events.skills.abilities.McMMOPlayerAbilityActivateEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.manager.AbstractListener;
import su.nightexpress.ama.AMA;
import su.nightexpress.ama.api.ArenaAPI;
import su.nightexpress.ama.arena.impl.Arena;
import su.nightexpress.ama.arena.impl.ArenaPlayer;

public final class McMMOHook extends AbstractListener<AMA> {

    private static McMMOHook instance;

    private McMMOHook(@NotNull AMA plugin) {
        super(plugin);
        this.registerListeners();
    }

    public static void setup() {
        if (instance == null) {
            instance = new McMMOHook(ArenaAPI.PLUGIN);
        }
    }

    public static void shutdown() {
        if (instance != null) {
            instance.unregisterListeners();
            instance = null;
        }
    }

    @EventHandler
    public void onUseSkill(McMMOPlayerAbilityActivateEvent e) {
        Player p = e.getPlayer();
        ArenaPlayer arenaPlayer = ArenaPlayer.getPlayer(p);
        if (arenaPlayer == null) return;

        Arena arena = arenaPlayer.getArena();
        if (!arena.getConfig().getGameplayManager().isExternalMcmmoEnabled()) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onExpGain(McMMOPlayerXpGainEvent e) {
        Player p = e.getPlayer();
        ArenaPlayer arenaPlayer = ArenaPlayer.getPlayer(p);
        if (arenaPlayer == null) return;

        Arena arena = arenaPlayer.getArena();
        if (!arena.getConfig().getGameplayManager().isExternalMcmmoEnabled()) {
            e.setCancelled(true);
        }
    }
}
