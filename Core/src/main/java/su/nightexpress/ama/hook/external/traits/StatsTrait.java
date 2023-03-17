package su.nightexpress.ama.hook.external.traits;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

import net.citizensnpcs.api.event.NPCRightClickEvent;
import net.citizensnpcs.api.trait.Trait;
import net.citizensnpcs.api.trait.TraitName;
import su.nightexpress.ama.api.ArenaAPI;

@TraitName("ama-stats")
@Deprecated
public class StatsTrait extends Trait {

    public StatsTrait() {
        super("ama-stats");
    }

    @EventHandler
    public void click(NPCRightClickEvent e) {
        if (e.getNPC() == this.getNPC()) {
            Player p = e.getClicker();
            ArenaAPI.getStatsManager().getStatsMenu().open(p, 1);
        }
    }
}
