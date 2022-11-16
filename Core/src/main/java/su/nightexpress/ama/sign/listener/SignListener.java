package su.nightexpress.ama.sign.listener;

import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.manager.AbstractListener;
import su.nightexpress.ama.AMA;
import su.nightexpress.ama.Perms;
import su.nightexpress.ama.sign.type.SignType;
import su.nightexpress.ama.sign.SignManager;

public class SignListener extends AbstractListener<AMA> {

    private final SignManager signManager;

    public SignListener(@NotNull SignManager signManager) {
        super(signManager.plugin());
        this.signManager = signManager;
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onSignInteract(PlayerInteractEvent e) {
        if (e.getHand() == EquipmentSlot.OFF_HAND) return;
        if (e.useInteractedBlock() == Event.Result.DENY) return;
        if (e.getAction() != Action.RIGHT_CLICK_BLOCK) return;

        Block block = e.getClickedBlock();
        if (block == null) return;

        BlockState state = block.getState();
        if (!(state instanceof Sign sign)) return;

        SignType signType = this.signManager.getSignType(sign);
        if (signType == null) return;

        e.setUseInteractedBlock(Event.Result.DENY);
        e.setUseItemInHand(Event.Result.DENY);

        Player player = e.getPlayer();
        this.signManager.signInteract(player, sign);
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onSignCreate(SignChangeEvent e) {
        if (!e.getPlayer().hasPermission(Perms.CREATOR)) return;
        if (!(e.getBlock().getState() instanceof Sign sign)) return;
        this.signManager.signCreate(sign, e.getLines());
    }
}
