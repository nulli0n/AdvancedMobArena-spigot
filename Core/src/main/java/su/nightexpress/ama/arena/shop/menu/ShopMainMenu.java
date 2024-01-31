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
import su.nightexpress.ama.arena.impl.ArenaPlayer;
import su.nightexpress.ama.arena.shop.ShopManager;
import su.nightexpress.ama.arena.shop.impl.ShopCategory;
import su.nightexpress.ama.config.Config;
import su.nightexpress.ama.kit.impl.Kit;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.IntStream;

import static su.nexmedia.engine.utils.Colors2.*;
import static su.nightexpress.ama.Placeholders.*;

public class ShopMainMenu extends ConfigMenu<AMA> implements AutoPaged<ShopCategory> {

    public static final String FILE_NAME = "arena_shop.yml";

    private static final String PLACEHOLDER_BAD_KIT = "%bad_kit%";
    private static final String PLACEHOLDER_STATUS  = "%status%";

    private final ShopManager shopManager;

    private String       categoryName;
    private List<String> categoryLore;
    private List<String> categoryBadKitInfo;
    private List<String> categoryUnlockedInfo;
    private List<String> categoryLockedInfo;
    private int[]        categorySlots;

    public ShopMainMenu(@NotNull AMA plugin, @NotNull ShopManager shopManager) {
        super(plugin, JYML.loadOrExtract(plugin, Config.DIR_MENU, FILE_NAME));
        this.shopManager = shopManager;

        this.registerHandler(MenuItemType.class)
            .addClick(MenuItemType.CLOSE, ClickHandler.forClose(this))
            .addClick(MenuItemType.PAGE_NEXT, ClickHandler.forNextPage(this))
            .addClick(MenuItemType.PAGE_PREVIOUS, ClickHandler.forPreviousPage(this));

        this.load();
    }

    @Override
    public void onPrepare(@NotNull MenuViewer viewer, @NotNull MenuOptions options) {
        super.onPrepare(viewer, options);
        this.getItemsForPage(viewer).forEach(this::addItem);

        options.setTitle(this.shopManager.getArenaConfig().replacePlaceholders().apply(options.getTitle()));
    }

    @Override
    public int[] getObjectSlots() {
        return categorySlots;
    }

    @Override
    @NotNull
    public List<ShopCategory> getObjects(@NotNull Player player) {
        return this.shopManager.getCategories().stream().sorted(Comparator.comparing(ShopCategory::getId)).toList();
    }

    @Override
    @NotNull
    public ItemStack getObjectStack(@NotNull Player player, @NotNull ShopCategory category) {
        ItemStack item = category.getIcon();

        ArenaPlayer arenaPlayer = ArenaPlayer.getPlayer(player);
        if (arenaPlayer == null) return item;

        List<String> badKit = new ArrayList<>();
        if (!category.isGoodKit(arenaPlayer)) {
            Kit kit = arenaPlayer.getKit();
            badKit.addAll(this.categoryBadKitInfo);
            badKit.replaceAll(line -> line.replace(KIT_NAME, kit == null ? "?" : kit.getName()));
        }

        List<String> status = new ArrayList<>();
        if (category.isLocked() || !badKit.isEmpty()) {
            status.addAll(this.categoryLockedInfo);
        }
        else status.addAll(this.categoryUnlockedInfo);

        ItemReplacer.create(item).trimmed().hideFlags()
            .setDisplayName(this.categoryName)
            .setLore(this.categoryLore)
            .replaceLoreExact(PLACEHOLDER_STATUS, status)
            .replaceLoreExact(PLACEHOLDER_BAD_KIT, badKit)
            .replace(category.getPlaceholders())
            .replace(Colorizer::apply)
            .writeMeta();

        return item;
    }

    @Override
    @NotNull
    public ItemClick getObjectClick(@NotNull ShopCategory shopCategory) {
        return (viewer, event) -> {
            ArenaPlayer arenaPlayer = ArenaPlayer.getPlayer(viewer.getPlayer());
            if (arenaPlayer != null) {
                shopCategory.open(arenaPlayer);
            }
        };
    }

    @Override
    public boolean isCodeCreation() {
        return true;
    }

    @Override
    @NotNull
    protected MenuOptions createDefaultOptions() {
        return new MenuOptions(BOLD + ARENA_NAME + " Shop", 27, InventoryType.CHEST);
    }

    @Override
    @NotNull
    protected List<MenuItem> createDefaultItems() {
        List<MenuItem> list = new ArrayList<>();

        ItemStack blackFillItem = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
        list.add(new MenuItem(blackFillItem).setSlots(0,8,18,26));

        ItemStack whiteFillItem = new ItemStack(Material.WHITE_STAINED_GLASS_PANE);
        list.add(new MenuItem(whiteFillItem).setSlots(1,2,3,4,5,6,7,9,17,19,20,21,22,23,24,25));

        ItemStack exitItem = ItemUtil.createCustomHead("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYmViNTg4YjIxYTZmOThhZDFmZjRlMDg1YzU1MmRjYjA1MGVmYzljYWI0MjdmNDYwNDhmMThmYzgwMzQ3NWY3In19fQ==");
        ItemUtil.mapMeta(exitItem, meta -> {
            meta.setDisplayName(LIGHT_RED + BOLD + "Close");
        });
        list.add(new MenuItem(exitItem).setType(MenuItemType.CLOSE).setSlots(22).setPriority(10));

        ItemStack nextPageItem = ItemUtil.createCustomHead("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNjgyYWQxYjljYjRkZDIxMjU5YzBkNzVhYTMxNWZmMzg5YzNjZWY3NTJiZTM5NDkzMzgxNjRiYWM4NGE5NmUifX19");
        ItemUtil.mapMeta(nextPageItem, meta -> {
            meta.setDisplayName(EditorLocales.NEXT_PAGE.getLocalizedName());
        });
        list.add(new MenuItem(nextPageItem).setType(MenuItemType.PAGE_NEXT).setSlots(17).setPriority(10));

        ItemStack backPageItem = ItemUtil.createCustomHead("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMzdhZWU5YTc1YmYwZGY3ODk3MTgzMDE1Y2NhMGIyYTdkNzU1YzYzMzg4ZmYwMTc1MmQ1ZjQ0MTlmYzY0NSJ9fX0=");
        ItemUtil.mapMeta(backPageItem, meta -> {
            meta.setDisplayName(EditorLocales.PREVIOUS_PAGE.getLocalizedName());
        });
        list.add(new MenuItem(backPageItem).setType(MenuItemType.PAGE_PREVIOUS).setSlots(9).setPriority(10));

        return list;
    }

    @Override
    protected void loadAdditional() {
        this.categoryName = JOption.create("Category.Name",
            LIGHT_YELLOW + BOLD + SHOP_CATEGORY_NAME
        ).read(cfg);

        this.categoryLore = JOption.create("Category.Lore", List.of(
            PLACEHOLDER_STATUS,
            PLACEHOLDER_BAD_KIT,
            "",
            SHOP_CATEGORY_DESCRIPTION,
            "",
            LIGHT_YELLOW + "[▶] " + LIGHT_GRAY + "Click to " + LIGHT_YELLOW + "open" + LIGHT_GRAY + "."
        )).read(cfg);

        this.categoryBadKitInfo = JOption.create("Category.Info.BadKit", List.of(
            RED + "✘ " + GRAY + "You (" + RED + KIT_NAME + GRAY + ") can't use this category."
        )).read(cfg);

        this.categoryUnlockedInfo = JOption.create("Category.Info.Unlocked", List.of(
            GREEN + "✔ " + GRAY + "Available"
        )).read(cfg);

        this.categoryLockedInfo = JOption.create("Category.Info.Locked", List.of(
            RED + "✘ " + GRAY + "Not available now"
        )).read(cfg);

        this.categorySlots = new JOption<>("Category.Slots",
            JYML::getIntArray,
            IntStream.range(10, 17).toArray()
        ).setWriter(JYML::setIntArray).read(cfg);
    }
}
