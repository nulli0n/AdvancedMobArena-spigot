package su.nightexpress.ama.arena.shop.menu;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.config.JYML;
import su.nexmedia.engine.api.menu.AutoPaged;
import su.nexmedia.engine.api.menu.MenuItemType;
import su.nexmedia.engine.api.menu.click.ClickHandler;
import su.nexmedia.engine.api.menu.click.ItemClick;
import su.nexmedia.engine.api.menu.impl.ConfigMenu;
import su.nexmedia.engine.api.menu.impl.MenuOptions;
import su.nexmedia.engine.api.menu.impl.MenuViewer;
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

public class ShopCategoryMenu extends ConfigMenu<AMA> implements AutoPaged<ShopProduct> {

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
        super(shopCategory.plugin(), JYML.loadOrExtract(shopCategory.plugin(), "/menu/arena.shop.category.yml"));
        this.shopCategory = shopCategory;

        this.itemName = Colorizer.apply(cfg.getString("Product.Name", Placeholders.SHOP_PRODUCT_NAME));
        this.itemLoreDefault = Colorizer.apply(cfg.getStringList("Product.Lore.Default"));
        this.itemLoreKits = Colorizer.apply(cfg.getStringList("Product.Lore.Kits"));
        this.itemLoreUnlocked = Colorizer.apply(cfg.getStringList("Product.Lore.Unlocked"));
        this.itemLoreLocked = Colorizer.apply(cfg.getStringList("Product.Lore.Locked"));
        this.itemSlots = cfg.getIntArray("Product.Slots");

        this.registerHandler(MenuItemType.class)
            .addClick(MenuItemType.CLOSE, (viewer, event) -> plugin.runTask(task -> viewer.getPlayer().closeInventory()))
            .addClick(MenuItemType.RETURN, (viewer, event) -> {
                ArenaPlayer arenaPlayer = ArenaPlayer.getPlayer(viewer.getPlayer());
                if (arenaPlayer != null) {
                    this.shopCategory.open(arenaPlayer);
                }
            })
            .addClick(MenuItemType.PAGE_NEXT, ClickHandler.forNextPage(this))
            .addClick(MenuItemType.PAGE_PREVIOUS, ClickHandler.forPreviousPage(this));

        this.load();
    }

    @Override
    public void onPrepare(@NotNull MenuViewer viewer, @NotNull MenuOptions options) {
        super.onPrepare(viewer, options);
        this.getItemsForPage(viewer).forEach(this::addItem);
    }

    @Override
    public int[] getObjectSlots() {
        return itemSlots;
    }

    @Override
    @NotNull
    public List<ShopProduct> getObjects(@NotNull Player player) {
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
    public ItemStack getObjectStack(@NotNull Player player, @NotNull ShopProduct product) {
        ItemStack item = product.getIcon();
        ItemUtil.mapMeta(item, meta -> {
            List<String> lore = new ArrayList<>(this.itemLoreDefault);
            lore = StringUtil.replaceInList(lore, PLACEHOLDER_KITS, product.getAllowedKits().isEmpty() ? Collections.emptyList() : this.itemLoreKits);
            lore = StringUtil.replaceInList(lore, PLACEHOLDER_LOCKED, product.isLocked() ? this.itemLoreLocked : Collections.emptyList());
            lore = StringUtil.replaceInList(lore, PLACEHOLDER_UNLOCKED, product.isUnlocked() ? this.itemLoreUnlocked : Collections.emptyList());

            meta.setDisplayName(this.itemName);
            meta.setLore(lore);
            ItemUtil.replace(meta, product.replacePlaceholders());
        });
        return item;
    }

    @Override
    @NotNull
    public ItemClick getObjectClick(@NotNull ShopProduct shopProduct) {
        return (viewer, event) -> {
            ArenaPlayer arenaPlayer = ArenaPlayer.getPlayer(viewer.getPlayer());
            if (arenaPlayer == null) {
                viewer.getPlayer().closeInventory();
                return;
            }
            shopProduct.purchase(arenaPlayer);
        };
    }
}
