package su.nightexpress.ama.stats.menu;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.config.JYML;
import su.nexmedia.engine.api.menu.AbstractMenu;
import su.nexmedia.engine.api.menu.MenuClick;
import su.nexmedia.engine.api.menu.MenuItem;
import su.nexmedia.engine.api.menu.MenuItemType;
import su.nightexpress.ama.AMA;
import su.nightexpress.ama.Placeholders;
import su.nightexpress.ama.arena.AbstractArena;
import su.nightexpress.ama.data.ArenaUser;
import su.nightexpress.ama.stats.object.StatType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class StatsMenu extends AbstractMenu<AMA> {

    private static final String PLACEHOLDER_SCORE = "%score%";
    private static final String PLACEHOLDER_TOTAL = "%total%";

    public StatsMenu(@NotNull AMA plugin, @NotNull JYML cfg) {
        super(plugin, cfg, "");

        int slot = 0;
        for (StatType statType : StatType.values()) {
            String path2 = "Stat_Icons." + statType.name() + ".";
            cfg.addMissing(path2 + "Display.default.Item.Material", Material.MAP.name());
            cfg.addMissing(path2 + "Display.default.Item.Name", plugin.getLangManager().getEnum(statType));
            cfg.addMissing(path2 + "Display.default.Item.Lore", Arrays.asList(Placeholders.ARENA_NAME + ": %score%", "%total%"));
            cfg.addMissing(path2 + "Slots", slot++);
            cfg.addMissing(path2 + "Type", statType.name());
        }
        cfg.saveChanges();

        MenuClick click = (player, type, e) -> {
            if (type instanceof MenuItemType type2) this.onItemClickDefault(player, type2);
        };

        for (String sId : cfg.getSection("Content")) {
            MenuItem menuItem = cfg.getMenuItem("Content." + sId, MenuItemType.class);
            if (menuItem.getType() != null) {
                menuItem.setClickHandler(click);
            }
            this.addItem(menuItem);
        }

        for (String sId : cfg.getSection("Stat_Icons")) {
            MenuItem menuItem = cfg.getMenuItem("Stat_Icons." + sId, StatType.class);
            this.addItem(menuItem);
        }
    }

    @Override
    public void onItemPrepare(@NotNull Player player, @NotNull MenuItem menuItem, @NotNull ItemStack item) {
        super.onItemPrepare(player, menuItem, item);

        ItemMeta meta = item.getItemMeta();
        if (meta == null) return;

        List<String> lore = meta.getLore();
        if (lore == null) return;
        if (!(menuItem.getType() instanceof StatType statType)) return;

        ArenaUser user = plugin.getUserManager().getUserData(player);

        List<String> lore2 = new ArrayList<>();
        for (String line : lore) {
            if (line.contains(Placeholders.ARENA_NAME)) {
                for (AbstractArena arena : plugin.getArenaManager().getArenas()) {
                    String score = String.valueOf(user.getStats(statType, arena.getId()));
                    lore2.add(arena.getConfig().replacePlaceholders().apply(line).replace(PLACEHOLDER_SCORE, score));
                }
                continue;
            }
            lore2.add(line.replace(PLACEHOLDER_TOTAL, String.valueOf(user.getStats(statType))));
        }
        meta.setLore(lore2);
        item.setItemMeta(meta);
    }

    @Override
    public boolean cancelClick(@NotNull InventoryClickEvent e, @NotNull SlotType slotType) {
        return true;
    }
}
