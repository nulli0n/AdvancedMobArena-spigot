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
import su.nightexpress.ama.arena.shop.ShopManager;
import su.nightexpress.ama.arena.shop.impl.ShopCategory;
import su.nightexpress.ama.kit.Kit;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class ShopMainMenu extends ConfigMenu<AMA> implements AutoPaged<ShopCategory> {

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
        super(shopManager.plugin(), JYML.loadOrExtract(shopManager.plugin(), "/menu/arena.shop.main.yml"));
        this.shopManager = shopManager;

        this.categoryName = Colorizer.apply(cfg.getString("Category.Name", Placeholders.SHOP_CATEGORY_NAME));
        this.categoryLoreDefault = Colorizer.apply(cfg.getStringList("Category.Lore.Default"));
        this.categoryLoreKits = Colorizer.apply(cfg.getStringList("Category.Lore.Kits"));
        this.categoryLoreUnlocked = Colorizer.apply(cfg.getStringList("Category.Lore.Unlocked"));
        this.categoryLoreLocked = Colorizer.apply(cfg.getStringList("Category.Lore.Locked"));
        this.categorySlots = cfg.getIntArray("Category.Slots");

        this.registerHandler(MenuItemType.class)
            .addClick(MenuItemType.CLOSE, (viewer, event) -> plugin.runTask(task -> viewer.getPlayer().closeInventory()))
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
        return categorySlots;
    }

    @Override
    @NotNull
    public List<ShopCategory> getObjects(@NotNull Player player) {
        return new ArrayList<>(this.shopManager.getCategories());
    }

    @Override
    @NotNull
    public ItemStack getObjectStack(@NotNull Player player, @NotNull ShopCategory category) {
        ItemStack item = category.getIcon();
        ItemUtil.mapMeta(item, meta -> {
            List<String> lore = new ArrayList<>(this.categoryLoreDefault);
            lore = StringUtil.replaceInList(lore, PLACEHOLDER_KITS, category.getKitsRequired().isEmpty() ? Collections.emptyList() : this.categoryLoreKits);
            lore = StringUtil.replaceInList(lore, PLACEHOLDER_LOCKED, category.isLocked() ? this.categoryLoreLocked : Collections.emptyList());
            lore = StringUtil.replaceInList(lore, PLACEHOLDER_UNLOCKED, category.isUnlocked() ? this.categoryLoreUnlocked : Collections.emptyList());

            String kits = category.getKitsRequired().stream()
                .map(id -> plugin.getKitManager().getKitById(id))
                .filter(Objects::nonNull).map(Kit::getName)
                .collect(Collectors.joining(", "));

            meta.setDisplayName(this.categoryName);
            meta.setLore(lore);
            ItemUtil.replace(meta, category.replacePlaceholders());
            ItemUtil.replace(meta, str -> str.replace(Placeholders.KIT_NAME, kits));
        });
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
}
