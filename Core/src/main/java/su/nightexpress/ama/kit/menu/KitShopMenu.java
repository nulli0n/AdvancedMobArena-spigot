package su.nightexpress.ama.kit.menu;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nexmedia.engine.api.config.JYML;
import su.nexmedia.engine.api.menu.click.ItemClick;
import su.nightexpress.ama.AMA;
import su.nightexpress.ama.arena.impl.ArenaPlayer;
import su.nightexpress.ama.data.ArenaUser;
import su.nightexpress.ama.kit.Kit;

import java.util.function.Predicate;

public class KitShopMenu extends AbstractKitListMenu {

    public KitShopMenu(@NotNull AMA plugin, @NotNull JYML cfg) {
        super(plugin, cfg);
    }

    @Override
    @NotNull
    public Predicate<Kit> getFilter(@Nullable ArenaUser user) {
        return kit -> user != null && !user.hasKit(kit.getId());
    }

    @Override
    @NotNull
    public ItemClick getObjectClick(@NotNull Kit kit) {
        return (viewer, event) -> {
            Player player = viewer.getPlayer();
            ArenaPlayer arenaPlayer = ArenaPlayer.getPlayer(player);
            if (arenaPlayer == null) return;

            if (event.isLeftClick()) {
                if (kit.buy(arenaPlayer)) {
                    this.openNextTick(player, viewer.getPage());
                }
            }
            else if (event.isRightClick()) {
                kit.getPreview().openNextTick(player, 1);
            }
        };
    }
}
