package su.nightexpress.ama.hook.external.traits;

import net.citizensnpcs.api.event.NPCRightClickEvent;
import net.citizensnpcs.api.trait.Trait;
import net.citizensnpcs.api.trait.TraitName;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import su.nightexpress.ama.arena.ArenaPlayer;

@TraitName("ama-shop")
public class ShopTrait extends Trait {

	public ShopTrait() {
		super("ama-shop");
	}

    @EventHandler
    public void click(NPCRightClickEvent e) {
        if (e.getNPC() == this.getNPC()) {
        	Player player = e.getClicker();
        	ArenaPlayer arenaPlayer = ArenaPlayer.getPlayer(player);
        	if (arenaPlayer == null) return;
        	
        	arenaPlayer.getArena().getConfig().getShopManager().open(player);
        }
    }
}
