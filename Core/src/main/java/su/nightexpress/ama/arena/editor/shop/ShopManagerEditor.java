package su.nightexpress.ama.arena.editor.shop;

import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.menu.impl.EditorMenu;
import su.nexmedia.engine.api.menu.impl.MenuViewer;
import su.nexmedia.engine.utils.ItemReplacer;
import su.nightexpress.ama.AMA;
import su.nightexpress.ama.arena.shop.ShopManager;
import su.nightexpress.ama.editor.EditorLocales;

public class ShopManagerEditor extends EditorMenu<AMA, ShopManager> {

    private CategoryListEditor categoryEditor;

    public ShopManagerEditor(@NotNull ShopManager shopManager) {
        super(shopManager.plugin(), shopManager, "Shop Manager [" + shopManager.getArenaConfig().getId() + "]", 45);

        this.addReturn(40).setClick((viewer, event) -> {
            shopManager.getArenaConfig().getEditor().openNextTick(viewer, 1);
        });

        this.addItem(Material.LIME_DYE, EditorLocales.SHOP_ACTIVE, 4).setClick((viewer, event) -> {
            shopManager.setActive(!shopManager.isActive());
            this.save(viewer);
        }).getOptions().addDisplayModifier((viewer, item) -> {
            item.setType(shopManager.isActive() ? Material.LIME_DYE : Material.GRAY_DYE);
        });

        this.addItem(Material.GLASS_BOTTLE, EditorLocales.SHOP_HIDE_OTHER_KIT_ITEMS, 21).setClick((viewer, event) -> {
            shopManager.setHideOtherKitProducts(!shopManager.isHideOtherKitProducts());
            this.save(viewer);
        });

        this.addItem(Material.CHEST_MINECART, EditorLocales.SHOP_CATEGORIES, 23).setClick((viewer, event) -> {
            this.getCategoryListEditor().openNextTick(viewer, 1);
        });

        this.getItems().forEach(menuItem -> menuItem.getOptions().addDisplayModifier((viewer, item) -> {
            ItemReplacer.replace(item, shopManager.replacePlaceholders());
        }));
    }

    private void save(@NotNull MenuViewer viewer) {
        this.object.save();
        this.openNextTick(viewer, viewer.getPage());
    }

    @Override
    public void clear() {
        if (this.categoryEditor != null) {
            this.categoryEditor.clear();
            this.categoryEditor = null;
        }
        super.clear();
    }

    @NotNull
    public CategoryListEditor getCategoryListEditor() {
        if (this.categoryEditor == null) {
            this.categoryEditor = new CategoryListEditor(this.object);
        }
        return this.categoryEditor;
    }
}
