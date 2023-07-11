package su.nightexpress.ama.kit.menu;

import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nexmedia.engine.api.config.JYML;
import su.nexmedia.engine.api.menu.click.ItemClick;
import su.nightexpress.ama.AMA;
import su.nightexpress.ama.arena.impl.ArenaPlayer;
import su.nightexpress.ama.config.Lang;
import su.nightexpress.ama.data.ArenaUser;
import su.nightexpress.ama.kit.Kit;

import java.util.function.Predicate;

public class KitSelectMenu extends AbstractKitListMenu {

    public KitSelectMenu(@NotNull AMA plugin, @NotNull JYML cfg) {
        super(plugin, cfg);
    }

    @Override
    @NotNull
    public Predicate<Kit> getFilter(@Nullable ArenaUser user) {
        return kit -> user != null && user.hasKit(kit.getId());
    }

    @Override
    @NotNull
    public ItemClick getObjectClick(@NotNull Kit kit) {
        return (viewer, event) -> {
            Player player = viewer.getPlayer();
            ArenaPlayer arenaPlayer = ArenaPlayer.getPlayer(player);
            if (arenaPlayer == null) return;

            if (event.isLeftClick()) {
                if (!kit.isAvailable(arenaPlayer, true)) {
                    player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_BREAK, 1.0f, 1.0f);
                    return;
                }

                arenaPlayer.setKit(kit);
                plugin.getMessage(Lang.Kit_Select_Success).replace(kit.replacePlaceholders()).send(player);
                player.playSound(player.getLocation(), Sound.ITEM_ARMOR_EQUIP_LEATHER, 1.0f, 1.0f);
                player.closeInventory();

            }
            else if (event.isRightClick()) {
                kit.getPreview().open(player, 1);
            }
        };
    }
}
