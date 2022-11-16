package su.nightexpress.ama.hook.external.traits;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

import net.citizensnpcs.api.event.NPCRightClickEvent;
import net.citizensnpcs.api.trait.Trait;
import net.citizensnpcs.api.trait.TraitName;
import su.nightexpress.ama.api.ArenaAPI;

@TraitName("ama-kit-shop")
public class KitShopTrait extends Trait {

    public KitShopTrait() {
        super("ama-kit-shop");
    }

    @EventHandler
    public void click(NPCRightClickEvent e) {
        if (e.getNPC() == this.getNPC()) {
            Player p = e.getClicker();
            ArenaAPI.getKitManager().getShopMenu().open(p, 1);
        }
    }
}