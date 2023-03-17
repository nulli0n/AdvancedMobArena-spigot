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
import su.nightexpress.ama.arena.shop.impl.ShopCategory;
import su.nightexpress.ama.arena.shop.impl.ShopProduct;
import su.nightexpress.ama.arena.type.GameState;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ShopCategoryMenu extends AbstractMenuAuto<AMA, ShopProduct> {

    private static final String PLACEHOLDER_KITS     = "%kits%";
    private static final String PLACEHOLDER_UNLOCKED = "%unlocked%";
    private static final String PLACEHOLDER_LOCKED   = "%locked%";

    private final ShopCategory shopCategory;

    private final String       itemName;
    private final List<String> itemLoreDefault;
    private final List<String> itemLoreKits;
    private final List<String> itemLoreUnlocked;
    private final List<String> itemLoreLocked;
    private final int[]        itemSlots;

    public ShopCategoryMenu(@NotNull ShopCategory shopCategory) {
        super(shopCategory.plugin(), JYML.loadOrExtract(shopCategory.plugin(), "/menu/arena.shop.category.yml"), "");
        this.shopCategory = shopCategory;

        this.itemName = Colorizer.apply(cfg.getString("Product.Name", Placeholders.SHOP_PRODUCT_NAME));
        this.itemLoreDefault = Colorizer.apply(cfg.getStringList("Product.Lore.Default"));
        this.itemLoreKits = Colorizer.apply(cfg.getStringList("Product.Lore.Kits"));
        this.itemLoreUnlocked = Colorizer.apply(cfg.getStringList("Product.Lore.Unlocked"));
        this.itemLoreLocked = Colorizer.apply(cfg.getStringList("Product.Lore.Locked"));
        this.itemSlots = cfg.getIntArray("Product.Slots");

        MenuClick click = (player, type, e) -> {
            if (type instanceof MenuItemType type2) {
                if (type2 == MenuItemType.RETURN) {
                    ArenaPlayer arenaPlayer = ArenaPlayer.getPlayer(player);
                    if (arenaPlayer != null) {
                        this.shopCategory.open(arenaPlayer);
                    }
                }
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
        return itemSlots;
    }

    @Override
    @NotNull
    protected List<ShopProduct> getObjects(@NotNull Player player) {
        ArenaPlayer arenaPlayer = ArenaPlayer.getPlayer(player);
        if (arenaPlayer == null || arenaPlayer.getArena().getState() != GameState.INGAME) return Collections.emptyList();

        List<ShopProduct> items = new ArrayList<>(this.shopCategory.getProducts());
        if (shopCategory.getShopManager().isHideOtherKitProducts()) {
            items.removeIf(item -> !item.isAvailable(arenaPlayer));
        }
        return items;
    }

    @Override
    @NotNull
    protected ItemStack getObjectStack(@NotNull Player player, @NotNull ShopProduct product) {
        ItemStack item = product.getIcon();
        ItemUtil.mapMeta(item, meta -> {
            List<String> lore = new ArrayList<>(this.itemLoreDefault);
            lore = StringUtil.replace(lore, PLACEHOLDER_KITS, false, product.getAllowedKits().isEmpty() ? Collections.emptyList() : this.itemLoreKits);
            lore = StringUtil.replace(lore, PLACEHOLDER_LOCKED, false, product.isLocked() ? this.itemLoreLocked : Collections.emptyList());
            lore = StringUtil.replace(lore, PLACEHOLDER_UNLOCKED, false, product.isUnlocked() ? this.itemLoreUnlocked : Collections.emptyList());

            meta.setDisplayName(this.itemName);
            meta.setLore(lore);
            ItemUtil.replace(meta, product.replacePlaceholders());
        });
        return item;
    }

    @Override
    @NotNull
    protected MenuClick getObjectClick(@NotNull Player player, @NotNull ShopProduct shopProduct) {
        return (player2, type, e) -> {
            ArenaPlayer arenaPlayer = ArenaPlayer.getPlayer(player2);
            if (arenaPlayer == null) {
                player2.closeInventory();
                return;
            }
            shopProduct.purchase(arenaPlayer);
        };
    }

    @Override
    public boolean cancelClick(@NotNull InventoryClickEvent e, @NotNull SlotType slotType) {
        return true;
    }
}
