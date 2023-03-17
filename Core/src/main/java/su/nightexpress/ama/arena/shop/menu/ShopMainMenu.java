package su.nightexpress.ama.arena.shop.menu;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.config.JYML;
import su.nexmedia.engine.api.menu.AbstractMenuAuto;
import su.nexmedia.engine.api.menu.MenuClick;
import su.nexmedia.engine.api.menu.MenuItem;
import su.nexmedia.engine.api.menu.MenuItemType;
import su.nexmedia.engine.utils.Colorizer;
import su.nexmedia.engine.utils.ItemUtil;
import su.nexmedia.engine.utils.StringUtil;
import su.nightexpress.ama.AMA;
import su.nightexpress.ama.Placeholders;
import su.nightexpress.ama.arena.impl.ArenaPlayer;
import su.nightexpress.ama.arena.shop.ShopManager;
import su.nightexpress.ama.arena.shop.impl.ShopCategory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ShopMainMenu extends AbstractMenuAuto<AMA, ShopCategory> {

    private static final String PLACEHOLDER_KITS     = "%kits%";
    private static final String PLACEHOLDER_UNLOCKED = "%unlocked%";
    private static final String PLACEHOLDER_LOCKED   = "%locked%";

    private final ShopManager shopManager;

    private final String       categoryName;
    private final List<String> categoryLoreDefault;
    private final List<String> categoryLoreKits;
    private final List<String> categoryLoreUnlocked;
    private final List<String> categoryLoreLocked;
    private final int[]        categorySlots;

    public ShopMainMenu(@NotNull ShopManager shopManager) {
        super(shopManager.plugin(), JYML.loadOrExtract(shopManager.plugin(), "/menu/arena.shop.main.yml"), "");
        this.shopManager = shopManager;

        this.categoryName = Colorizer.apply(cfg.getString("Category.Name", Placeholders.SHOP_CATEGORY_NAME));
        this.categoryLoreDefault = Colorizer.apply(cfg.getStringList("Category.Lore.Default"));
        this.categoryLoreKits = Colorizer.apply(cfg.getStringList("Category.Lore.Kits"));
        this.categoryLoreUnlocked = Colorizer.apply(cfg.getStringList("Category.Lore.Unlocked"));
        this.categoryLoreLocked = Colorizer.apply(cfg.getStringList("Category.Lore.Locked"));
        this.categorySlots = cfg.getIntArray("Category.Slots");

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
    }

    @Override
    public int[] getObjectSlots() {
        return categorySlots;
    }

    @Override
    @NotNull
    protected List<ShopCategory> getObjects(@NotNull Player player) {
        return new ArrayList<>(this.shopManager.getCategories());
    }

    @Override
    @NotNull
    protected ItemStack getObjectStack(@NotNull Player player, @NotNull ShopCategory category) {
        ItemStack item = category.getIcon();
        ItemUtil.mapMeta(item, meta -> {
            List<String> lore = new ArrayList<>(this.categoryLoreDefault);
            lore = StringUtil.replace(lore, PLACEHOLDER_KITS, false, category.getAllowedKits().isEmpty() ? Collections.emptyList() : this.categoryLoreKits);
            lore = StringUtil.replace(lore, PLACEHOLDER_LOCKED, false, category.isLocked() ? this.categoryLoreLocked : Collections.emptyList());
            lore = StringUtil.replace(lore, PLACEHOLDER_UNLOCKED, false, category.isUnlocked() ? this.categoryLoreUnlocked : Collections.emptyList());

            meta.setDisplayName(this.categoryName);
            meta.setLore(lore);
            ItemUtil.replace(meta, category.replacePlaceholders());
        });
        return item;
    }

    @Override
    @NotNull
    protected MenuClick getObjectClick(@NotNull Player player, @NotNull ShopCategory shopCategory) {
        return (player2, type, e) -> {
            ArenaPlayer arenaPlayer = ArenaPlayer.getPlayer(player2);
            if (arenaPlayer != null) {
                shopCategory.open(arenaPlayer);
            }
        };
    }

    @Override
    public boolean cancelClick(@NotNull InventoryClickEvent e, @NotNull SlotType slotType) {
        return true;
    }
}
