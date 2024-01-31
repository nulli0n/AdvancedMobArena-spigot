package su.nightexpress.ama.arena.shop.menu;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.config.JOption;
import su.nexmedia.engine.api.config.JYML;
import su.nexmedia.engine.api.editor.EditorLocales;
import su.nexmedia.engine.api.menu.AutoPaged;
import su.nexmedia.engine.api.menu.MenuItemType;
import su.nexmedia.engine.api.menu.click.ClickHandler;
import su.nexmedia.engine.api.menu.click.ItemClick;
import su.nexmedia.engine.api.menu.impl.ConfigMenu;
import su.nexmedia.engine.api.menu.impl.MenuOptions;
import su.nexmedia.engine.api.menu.impl.MenuViewer;
import su.nexmedia.engine.api.menu.item.MenuItem;
import su.nexmedia.engine.utils.Colorizer;
import su.nexmedia.engine.utils.ItemReplacer;
import su.nexmedia.engine.utils.ItemUtil;
import su.nightexpress.ama.AMA;
import su.nightexpress.ama.api.type.GameState;
import su.nightexpress.ama.arena.impl.ArenaPlayer;
import su.nightexpress.ama.arena.shop.impl.ShopCategory;
import su.nightexpress.ama.arena.shop.impl.ShopProduct;
import su.nightexpress.ama.config.Config;
import su.nightexpress.ama.kit.impl.Kit;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static su.nexmedia.engine.utils.Colors2.*;
import static su.nightexpress.ama.Placeholders.*;

public class ShopCategoryMenu extends ConfigMenu<AMA> implements AutoPaged<ShopProduct> {

    public static final String FILE_NAME = "arena_shop_category.yml";

    private static final String PLACEHOLDER_BAD_KIT = "%bad_kit%";
    private static final String PLACEHOLDER_STATUS  = "%status%";
    private static final String PLACEHOLDER_PRICE = "%price%";

    private final ShopCategory shopCategory;

    private String       itemName;
    private List<String> itemLore;
    private List<String> itemBadKitInfo;
    private List<String> itemUnlockedInfo;
    private List<String> itemLockedInfo;
    private List<String> itemPriceGoodInfo;
    private List<String> itemPriceBadInfo;
    private int[]        itemSlots;

    public ShopCategoryMenu(@NotNull AMA plugin, @NotNull ShopCategory shopCategory) {
        super(plugin, JYML.loadOrExtract(plugin, Config.DIR_MENU, FILE_NAME));
        this.shopCategory = shopCategory;

        this.registerHandler(MenuItemType.class)
            .addClick(MenuItemType.CLOSE, ClickHandler.forClose(this))
            .addClick(MenuItemType.RETURN, (viewer, event) -> {
                ArenaPlayer arenaPlayer = ArenaPlayer.getPlayer(viewer.getPlayer());
                if (arenaPlayer != null) {
                    this.shopCategory.getArenaConfig().getShopManager().open(arenaPlayer.getPlayer());
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

        options.setTitle(this.shopCategory.getArenaConfig().replacePlaceholders().apply(options.getTitle()));
        options.setTitle(this.shopCategory.replacePlaceholders().apply(options.getTitle()));
    }

    @Override
    public int[] getObjectSlots() {
        return this.itemSlots;
    }

    @Override
    @NotNull
    public List<ShopProduct> getObjects(@NotNull Player player) {
        ArenaPlayer arenaPlayer = ArenaPlayer.getPlayer(player);
        if (arenaPlayer == null || arenaPlayer.getArena().getState() != GameState.INGAME) return Collections.emptyList();

        return this.shopCategory.getProducts().stream()
            .filter(product -> product.isGoodKit(arenaPlayer) || !shopCategory.getShopManager().isHideOtherKitProducts())
            .sorted(Comparator.comparing(ShopProduct::getId))
            .toList();
    }

    @Override
    @NotNull
    public ItemStack getObjectStack(@NotNull Player player, @NotNull ShopProduct product) {
        ArenaPlayer arenaPlayer = ArenaPlayer.getPlayer(player);
        if (arenaPlayer == null) return new ItemStack(Material.AIR);

        ItemStack item = product.getIcon();

        List<String> badKit = new ArrayList<>();
        if (!product.isGoodKit(arenaPlayer)) {
            Kit kit = arenaPlayer.getKit();
            badKit.addAll(this.itemBadKitInfo);
            badKit.replaceAll(line -> line.replace(KIT_NAME, kit == null ? "?" : kit.getName()));
        }

        List<String> status = new ArrayList<>();
        if (product.isLocked()) {
            status.addAll(this.itemLockedInfo);
        }
        else status.addAll(this.itemUnlockedInfo);

        List<String> price = new ArrayList<>();
        if (!product.canAfford(player)) {
            price.addAll(this.itemPriceBadInfo);
        }
        else price.addAll(this.itemPriceGoodInfo);

        ItemReplacer.create(item).trimmed().hideFlags()
            .setDisplayName(this.itemName)
            .setLore(this.itemLore)
            .replaceLoreExact(PLACEHOLDER_STATUS, status)
            .replaceLoreExact(PLACEHOLDER_BAD_KIT, badKit)
            .replaceLoreExact(PLACEHOLDER_PRICE, price)
            .replace(product.getPlaceholders())
            .replace(Colorizer::apply)
            .writeMeta();

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

    @Override
    public boolean isCodeCreation() {
        return true;
    }

    @Override
    @NotNull
    protected MenuOptions createDefaultOptions() {
        return new MenuOptions(BOLD + ARENA_NAME + " " + SHOP_CATEGORY_NAME + " Shop", 45, InventoryType.CHEST);
    }

    @Override
    @NotNull
    protected List<MenuItem> createDefaultItems() {
        List<MenuItem> list = new ArrayList<>();

        ItemStack blackFillItem = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
        list.add(new MenuItem(blackFillItem).setSlots(0,8,44,36));

        ItemStack whiteFillItem = new ItemStack(Material.WHITE_STAINED_GLASS_PANE);
        list.add(new MenuItem(whiteFillItem).setSlots(1,2,3,4,5,6,7,9,18,27,17,26,35,37,38,39,40,41,42,43));

        ItemStack exitItem = ItemUtil.createCustomHead("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYTM4NTJiZjYxNmYzMWVkNjdjMzdkZTRiMGJhYTJjNWY4ZDhmY2E4MmU3MmRiY2FmY2JhNjY5NTZhODFjNCJ9fX0=");
        ItemUtil.mapMeta(exitItem, meta -> {
            meta.setDisplayName(LIGHT_YELLOW + BOLD + "Return");
        });
        list.add(new MenuItem(exitItem).setType(MenuItemType.RETURN).setSlots(40).setPriority(10));

        ItemStack nextPageItem = ItemUtil.createCustomHead("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNjgyYWQxYjljYjRkZDIxMjU5YzBkNzVhYTMxNWZmMzg5YzNjZWY3NTJiZTM5NDkzMzgxNjRiYWM4NGE5NmUifX19");
        ItemUtil.mapMeta(nextPageItem, meta -> {
            meta.setDisplayName(EditorLocales.NEXT_PAGE.getLocalizedName());
        });
        list.add(new MenuItem(nextPageItem).setType(MenuItemType.PAGE_NEXT).setSlots(44).setPriority(10));

        ItemStack backPageItem = ItemUtil.createCustomHead("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMzdhZWU5YTc1YmYwZGY3ODk3MTgzMDE1Y2NhMGIyYTdkNzU1YzYzMzg4ZmYwMTc1MmQ1ZjQ0MTlmYzY0NSJ9fX0=");
        ItemUtil.mapMeta(backPageItem, meta -> {
            meta.setDisplayName(EditorLocales.PREVIOUS_PAGE.getLocalizedName());
        });
        list.add(new MenuItem(backPageItem).setType(MenuItemType.PAGE_PREVIOUS).setSlots(36).setPriority(10));

        return list;
    }

    @Override
    protected void loadAdditional() {
        this.itemName = JOption.create("Product.Name",
            LIGHT_YELLOW + BOLD + SHOP_PRODUCT_NAME
        ).read(cfg);

        this.itemLore = JOption.create("Product.Lore", List.of(
            PLACEHOLDER_STATUS,
            PLACEHOLDER_BAD_KIT,
            PLACEHOLDER_PRICE,
            "",
            SHOP_PRODUCT_DESCRIPTION,
            "",
            LIGHT_YELLOW + "[▶] " + LIGHT_GRAY + "Click to " + LIGHT_YELLOW + "purchase" + LIGHT_GRAY + "."
        )).read(cfg);

        this.itemBadKitInfo = JOption.create("Product.Info.BadKit", List.of(
            RED + "✘ " + GRAY + "You (" + RED + KIT_NAME + GRAY + ") can't access this item."
        )).read(cfg);

        this.itemUnlockedInfo = JOption.create("Product.Info.Unlocked", List.of(
            GREEN + "✔ " + GRAY + "Unlocked"
        )).read(cfg);

        this.itemLockedInfo = JOption.create("Product.Info.Locked", List.of(
            RED + "✘ " + GRAY + "Not available now"
        )).read(cfg);

        this.itemPriceGoodInfo = JOption.create("Product.Info.CanPurchase", List.of(
            GREEN + "✔ " + GRAY + "Price: " + LIGHT_GRAY + SHOP_PRODUCT_PRICE
        )).read(cfg);

        this.itemPriceBadInfo = JOption.create("Product.Info.CantAfford", List.of(
            RED + "✘ " + GRAY + "Price: " + LIGHT_GRAY + SHOP_PRODUCT_PRICE
        )).read(cfg);

        this.itemSlots = new JOption<>("Product.Slots",
            JYML::getIntArray,
            new int[] {10,11,12,13,14,15,16,19,20,21,22,23,24,25,28,29,30,31,32,33,34}
        ).setWriter(JYML::setIntArray).read(cfg);
    }
}
