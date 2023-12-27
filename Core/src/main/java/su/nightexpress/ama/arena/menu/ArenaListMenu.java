package su.nightexpress.ama.arena.menu;

import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.config.JYML;
import su.nexmedia.engine.api.menu.MenuItemType;
import su.nexmedia.engine.api.menu.click.ClickHandler;
import su.nexmedia.engine.api.menu.impl.ConfigMenu;
import su.nexmedia.engine.api.menu.item.MenuItem;
import su.nexmedia.engine.utils.ItemUtil;
import su.nightexpress.ama.AMA;
import su.nightexpress.ama.arena.impl.Arena;

public class ArenaListMenu extends ConfigMenu<AMA> {

    public ArenaListMenu(@NotNull AMA plugin) {
        super(plugin, JYML.loadOrExtract(plugin, "/menu/arena.list.yml"));

        this.registerHandler(MenuItemType.class)
            .addClick(MenuItemType.CLOSE, ClickHandler.forClose(this))
            .addClick(MenuItemType.PAGE_NEXT, ClickHandler.forNextPage(this))
            .addClick(MenuItemType.PAGE_PREVIOUS, ClickHandler.forPreviousPage(this));

        this.load();

        for (String arenaId : cfg.getSection("Arenas")) {
            MenuItem menuItem = this.readItem("Arenas." + arenaId);

            Arena arena = plugin.getArenaManager().getArenaById(arenaId);
            if (arena == null) {
                plugin.error("Invalid arena '" + arenaId + "' in Arenas Menu!");
                continue;
            }

            menuItem.setClick((viewer, event) -> {
                arena.joinLobby(viewer.getPlayer());
            });
            menuItem.getOptions().addDisplayModifier((viewer, item) -> ItemUtil.replace(item, arena.replacePlaceholders()));
            this.addItem(menuItem);
        }
    }
}
