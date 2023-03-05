package su.nightexpress.ama.arena.menu;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.config.JYML;
import su.nexmedia.engine.api.menu.AbstractMenu;
import su.nexmedia.engine.api.menu.MenuClick;
import su.nexmedia.engine.api.menu.MenuItem;
import su.nexmedia.engine.api.menu.MenuItemType;
import su.nexmedia.engine.utils.ItemUtil;
import su.nightexpress.ama.AMA;
import su.nightexpress.ama.arena.impl.Arena;

public class ArenaListMenu extends AbstractMenu<AMA> {

    public ArenaListMenu(@NotNull AMA plugin) {
        super(plugin, JYML.loadOrExtract(plugin, "/menu/arena.list.yml"), "");

        MenuClick click = (player, type, e) -> {
            if (type instanceof MenuItemType type2) {
                this.onItemClickDefault(player, type2);
            }
        };

        for (String sId : cfg.getSection("Content")) {
            MenuItem menuItem = cfg.getMenuItem("Content." + sId, MenuItemType.class);

            if (menuItem.getType() != null) {
                menuItem.setClickHandler(click);
            }
            this.addItem(menuItem);
        }

        for (String sId : cfg.getSection("Arenas")) {
            MenuItem menuItem = cfg.getMenuItem("Arenas." + sId);

            Arena arena = plugin.getArenaManager().getArenaById(menuItem.getId());
            if (arena == null) {
                plugin.error("Invalid arena '" + sId + "' in Arenas Menu!");
                continue;
            }

            menuItem.setClickHandler((p, type, e) -> {
                arena.joinLobby(p);
            });
            this.addItem(menuItem);
        }
    }

    @Override
    public void onItemPrepare(@NotNull Player player, @NotNull MenuItem menuItem, @NotNull ItemStack item) {
        super.onItemPrepare(player, menuItem, item);

        Arena arena = plugin.getArenaManager().getArenaById(menuItem.getId());
        if (arena == null) return;

        ItemUtil.replace(item, arena.replacePlaceholders());
    }

    @Override
    public boolean cancelClick(@NotNull InventoryClickEvent e, @NotNull SlotType slotType) {
        return true;
    }
}
