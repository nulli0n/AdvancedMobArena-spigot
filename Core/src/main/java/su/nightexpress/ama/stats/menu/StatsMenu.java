package su.nightexpress.ama.stats.menu;

import org.bukkit.Material;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.config.JYML;
import su.nexmedia.engine.api.menu.MenuItemType;
import su.nexmedia.engine.api.menu.click.ClickHandler;
import su.nexmedia.engine.api.menu.impl.ConfigMenu;
import su.nexmedia.engine.api.menu.item.MenuItem;
import su.nightexpress.ama.AMA;
import su.nightexpress.ama.Placeholders;
import su.nightexpress.ama.arena.impl.Arena;
import su.nightexpress.ama.data.ArenaUser;
import su.nightexpress.ama.stats.object.StatType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class StatsMenu extends ConfigMenu<AMA> {

    private static final String PLACEHOLDER_SCORE = "%score%";
    private static final String PLACEHOLDER_TOTAL = "%total%";

    public StatsMenu(@NotNull AMA plugin, @NotNull JYML cfg) {
        super(plugin, cfg);

        int slot = 0;
        for (StatType statType : StatType.values()) {
            String path2 = "Stat_Icons." + statType.name() + ".";
            cfg.addMissing(path2 + "Item.Material", Material.MAP.name());
            cfg.addMissing(path2 + "Item.Name", plugin.getLangManager().getEnum(statType));
            cfg.addMissing(path2 + "Item.Lore", Arrays.asList(Placeholders.ARENA_NAME + ": %score%", "%total%"));
            cfg.addMissing(path2 + "Slots", slot++);
            cfg.addMissing(path2 + "Type", statType.name());
        }
        cfg.saveChanges();

        this.registerHandler(MenuItemType.class)
            .addClick(MenuItemType.CLOSE, (viewer, event) -> plugin.runTask(task -> viewer.getPlayer().closeInventory()))
            .addClick(MenuItemType.PAGE_NEXT, ClickHandler.forNextPage(this))
            .addClick(MenuItemType.PAGE_PREVIOUS, ClickHandler.forPreviousPage(this));

        this.load();

        for (String sId : cfg.getSection("Stat_Icons")) {
            MenuItem menuItem = this.readItem("Stat_Icons." + sId);

            menuItem.getOptions().addDisplayModifier((viewer, item) -> {
                ItemMeta meta = item.getItemMeta();
                if (meta == null) return;

                List<String> lore = meta.getLore();
                if (lore == null) return;
                if (!(menuItem.getType() instanceof StatType statType)) return;

                ArenaUser user = plugin.getUserManager().getUserData(viewer.getPlayer());

                List<String> lore2 = new ArrayList<>();
                for (String line : lore) {
                    if (line.contains(Placeholders.ARENA_NAME)) {
                        for (Arena arena : plugin.getArenaManager().getArenas()) {
                            String score = String.valueOf(user.getStats(statType, arena.getId()));
                            lore2.add(arena.getConfig().replacePlaceholders().apply(line).replace(PLACEHOLDER_SCORE, score));
                        }
                        continue;
                    }
                    lore2.add(line.replace(PLACEHOLDER_TOTAL, String.valueOf(user.getStats(statType))));
                }
                meta.setLore(lore2);
                item.setItemMeta(meta);
            });

            this.addItem(menuItem);
        }
    }
}
