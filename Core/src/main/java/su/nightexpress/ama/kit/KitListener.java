package su.nightexpress.ama.kit;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.manager.AbstractListener;
import su.nightexpress.ama.AMA;
import su.nightexpress.ama.api.event.ArenaPlayerJoinEvent;
import su.nightexpress.ama.arena.impl.ArenaPlayer;
import su.nightexpress.ama.config.Config;
import su.nightexpress.ama.data.impl.ArenaUser;
import su.nightexpress.ama.kit.impl.Kit;

public class KitListener extends AbstractListener<AMA> {

    private final KitManager kitManager;

    public KitListener(@NotNull AMA plugin, KitManager kitManager) {
        super(plugin);
        this.kitManager = kitManager;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onArenaJoin(ArenaPlayerJoinEvent event) {
        Player player = event.getPlayer();
        ArenaUser user = plugin.getUserManager().getUserData(player);

        this.kitManager.getKits().forEach(kit -> {
            if (!user.hasKit(kit) && kit.hasPermission(player) && kit.isFree()) {
                user.addKit(kit);
            }
        });
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onPickupKitItem(EntityPickupItemEvent event) {
        if (!Config.KITS_PREVENT_ITEM_SHARE.get()) return;
        if (!(event.getEntity() instanceof Player player)) return;

        ArenaPlayer arenaPlayer = ArenaPlayer.getPlayer(player);
        if (arenaPlayer == null) return;

        ItemStack item = event.getItem().getItemStack();
        Kit kit = this.kitManager.getKitByItem(item);
        if (kit == null) return;

        if (arenaPlayer.getKit() != kit) {
            event.setCancelled(true);
        }
    }
}
