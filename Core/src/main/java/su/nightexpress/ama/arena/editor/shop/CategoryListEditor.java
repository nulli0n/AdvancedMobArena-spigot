package su.nightexpress.ama.arena.editor.shop;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.menu.AutoPaged;
import su.nexmedia.engine.api.menu.click.ItemClick;
import su.nexmedia.engine.api.menu.impl.EditorMenu;
import su.nexmedia.engine.api.menu.impl.MenuOptions;
import su.nexmedia.engine.api.menu.impl.MenuViewer;
import su.nexmedia.engine.editor.EditorManager;
import su.nexmedia.engine.utils.ItemReplacer;
import su.nightexpress.ama.AMA;
import su.nightexpress.ama.arena.shop.ShopManager;
import su.nightexpress.ama.arena.shop.impl.ShopCategory;
import su.nightexpress.ama.config.Lang;
import su.nightexpress.ama.editor.EditorLocales;

import java.util.Comparator;
import java.util.List;
import java.util.stream.IntStream;

public class CategoryListEditor extends EditorMenu<AMA, ShopManager> implements AutoPaged<ShopCategory> {

    public CategoryListEditor(@NotNull ShopManager shopManager) {
        super(shopManager.plugin(), shopManager, "Shop Editor [" + shopManager.getCategories().size() + " categories]", 45);

        this.addReturn(39).setClick((viewer, event) -> {
            shopManager.getEditor().openNextTick(viewer, 1);
        });
        this.addNextPage(44);
        this.addPreviousPage(36);

        this.addCreation(EditorLocales.SHOP_CATEGORY_CREATE, 41).setClick((viewer, event) -> {
            this.handleInput(viewer, Lang.EDITOR_ARENA_SHOP_ENTER_CATEGORY_ID, wrapper -> {
                if (!shopManager.createCategory(wrapper.getTextRaw())) {
                    EditorManager.error(viewer.getPlayer(), plugin.getMessage(Lang.EDITOR_ARENA_SHOP_ERROR_CATEGORY_EXISTS).getLocalized());
                    return false;
                }
                shopManager.save();
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
    public List<ShopCategory> getObjects(@NotNull Player player) {
        return this.object.getCategories().stream().sorted(Comparator.comparing(ShopCategory::getId)).toList();
    }

    @Override
    @NotNull
    public ItemStack getObjectStack(@NotNull Player player, @NotNull ShopCategory category) {
        ItemStack item = new ItemStack(category.getIcon());
        ItemReplacer.create(item).readLocale(EditorLocales.SHOP_CATEGORY_OBJECT).trimmed().hideFlags()
            .replace(category.getPlaceholders())
            .writeMeta();
        return item;
    }

    @Override
    @NotNull
    public ItemClick getObjectClick(@NotNull ShopCategory category) {
        return (viewer, event) -> {
            if (event.isShiftClick() && event.isRightClick()) {
                this.object.removeCategory(category);
                this.openNextTick(viewer, viewer.getPage());
                return;
            }
            category.getEditor().openNextTick(viewer, 1);
        };
    }
}
