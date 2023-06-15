package su.nightexpress.ama.arena.editor.shop;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.menu.AutoPaged;
import su.nexmedia.engine.api.menu.click.ItemClick;
import su.nexmedia.engine.api.menu.impl.EditorMenu;
import su.nexmedia.engine.api.menu.impl.MenuOptions;
import su.nexmedia.engine.api.menu.impl.MenuViewer;
import su.nexmedia.engine.editor.EditorManager;
import su.nexmedia.engine.utils.ItemUtil;
import su.nexmedia.engine.utils.StringUtil;
import su.nightexpress.ama.AMA;
import su.nightexpress.ama.arena.shop.impl.ShopCategory;
import su.nightexpress.ama.arena.shop.impl.ShopProduct;
import su.nightexpress.ama.config.Lang;
import su.nightexpress.ama.editor.EditorHub;
import su.nightexpress.ama.editor.EditorLocales;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

public class ShopProductListEditor extends EditorMenu<AMA, ShopCategory> implements AutoPaged<ShopProduct> {

    public ShopProductListEditor(@NotNull ShopCategory category) {
        super(category.plugin(), category, EditorHub.TITLE_SHOP_EDITOR, 45);

        this.addReturn(39).setClick((viewer, event) -> {
            category.getEditor().openNextTick(viewer, 1);
        });
        this.addNextPage(44);
        this.addPreviousPage(36);

        this.addCreation(EditorLocales.SHOP_PRODUCT_CREATE, 41).setClick((viewer, event) -> {
            this.handleInput(viewer, Lang.EDITOR_ARENA_SHOP_ENTER_PRODUCT_ID, wrapper -> {
                if (!category.createProduct(StringUtil.lowerCaseUnderscore(wrapper.getTextRaw()))) {
                    EditorManager.error(viewer.getPlayer(), plugin.getMessage(Lang.EDITOR_ARENA_SHOP_ERROR_PRODUCT_EXISTS).getLocalized());
                    return false;
                }
                category.getShopManager().save();
                return true;
            });
        });
    }

    @Override
    public void onPrepare(@NotNull MenuViewer viewer, @NotNull MenuOptions options) {
        super.onPrepare(viewer, options);
        this.getItemsForPage(viewer).forEach(this::addItem);
    }

    @Override
    public int[] getObjectSlots() {
        return IntStream.range(0, 36).toArray();
    }

    @Override
    @NotNull
    public List<ShopProduct> getObjects(@NotNull Player player) {
        return new ArrayList<>(this.object.getProducts());
    }

    @Override
    @NotNull
    public ItemStack getObjectStack(@NotNull Player player, @NotNull ShopProduct shopProduct) {
        ItemStack item = new ItemStack(shopProduct.getIcon());
        ItemUtil.mapMeta(item, meta -> {
            meta.setDisplayName(EditorLocales.SHOP_PRODUCT_OBJECT.getLocalizedName());
            meta.setLore(EditorLocales.SHOP_PRODUCT_OBJECT.getLocalizedLore());
            meta.addItemFlags(ItemFlag.values());
            ItemUtil.replace(meta, shopProduct.replacePlaceholders());
        });
        return item;
    }

    @Override
    @NotNull
    public ItemClick getObjectClick(@NotNull ShopProduct shopProduct) {
        return (viewer, event) -> {
            if (event.isShiftClick() && event.isRightClick()) {
                shopProduct.clear();
                this.object.getProducts().remove(shopProduct);
                this.object.getShopManager().save();
                this.openNextTick(viewer, viewer.getPage());
                return;
            }
            shopProduct.getEditor().openNextTick(viewer, 1);
        };
    }
}
