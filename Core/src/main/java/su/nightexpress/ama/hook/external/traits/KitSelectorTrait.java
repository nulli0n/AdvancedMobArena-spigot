package su.nightexpress.ama.hook.external.traits;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

import net.citizensnpcs.api.event.NPCRightClickEvent;
import net.citizensnpcs.api.trait.Trait;
import net.citizensnpcs.api.trait.TraitName;
import su.nightexpress.ama.api.ArenaAPI;

@TraitName("ama-kit-select")
public class KitSelectorTrait extends Trait {

    public KitSelectorTrait() {
        super("ama-kit-select");
    }

    @EventHandler
    public void click(NPCRightClickEvent e) {
        if (e.getNPC() == this.getNPC()) {
            Player p = e.getClicker();
            ArenaAPI.getKitManager().getSelectMenu().open(p, 1);
        }
    }
}
