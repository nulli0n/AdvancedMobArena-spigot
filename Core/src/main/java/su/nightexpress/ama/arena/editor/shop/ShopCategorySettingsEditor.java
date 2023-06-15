package su.nightexpress.ama.arena.editor.shop;

import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nexmedia.engine.api.menu.impl.EditorMenu;
import su.nexmedia.engine.api.menu.impl.MenuViewer;
import su.nexmedia.engine.editor.EditorManager;
import su.nexmedia.engine.utils.ItemUtil;
import su.nightexpress.ama.AMA;
import su.nightexpress.ama.arena.shop.impl.ShopCategory;
import su.nightexpress.ama.config.Lang;
import su.nightexpress.ama.editor.EditorHub;
import su.nightexpress.ama.editor.EditorLocales;

public class ShopCategorySettingsEditor extends EditorMenu<AMA, ShopCategory> {

    private ShopProductListEditor productsEditor;

    public ShopCategorySettingsEditor(@NotNull ShopCategory category) {
        super(category.getShopManager().plugin(), category, EditorHub.TITLE_SHOP_EDITOR, 45);

        this.addReturn(40).setClick((viewer, event) -> {
            category.getShopManager().getEditor().getCategoryListEditor().openNextTick(viewer, 1);
        });

        this.addItem(Material.NAME_TAG, EditorLocales.SHOP_CATEGORY_NAME, 20).setClick((viewer, event) -> {
            this.handleInput(viewer, Lang.EDITOR_GENERIC_ENTER_NAME, wrapper -> {
                category.setName(wrapper.getText());
                category.getShopManager().save();
                return true;
            });
        });

        this.addItem(Material.PAPER, EditorLocales.SHOP_CATEGORY_DESCRIPTION, 21).setClick((viewer, event) -> {
            if (event.isRightClick()) {
                category.getDescription().clear();
                this.save(viewer);
                return;
            }
            this.handleInput(viewer, Lang.EDITOR_GENERIC_ENTER_DESCRIPTION, wrapper -> {
                category.getDescription().add(wrapper.getText());
                category.getShopManager().save();
                return true;
            });
        });

        this.addItem(Material.ITEM_FRAME, EditorLocales.SHOP_CATEGORY_ICON, 22).setClick((viewer, event) -> {
            ItemStack cursor = event.getCursor();
            if (cursor == null || cursor.getType().isAir()) return;

            category.setIcon(cursor);
            event.getView().setCursor(null);
            this.save(viewer);
        }).getOptions().addDisplayModifier((viewer, item) -> {
            item.setType(category.getIcon().getType());
        });

        this.addItem(Material.ARMOR_STAND, EditorLocales.SHOP_CATEGORY_ALLOWED_KITS, 23).setClick((viewer, event) -> {
            if (event.isRightClick()) {
                category.getAllowedKits().clear();
                this.save(viewer);
                return;
            }
            EditorManager.suggestValues(viewer.getPlayer(), plugin.getKitManager().getKitIds(), true);
            this.handleInput(viewer, Lang.EDITOR_KIT_ENTER_ID, wrapper -> {
                category.getAllowedKits().add(wrapper.getTextRaw());
                category.getShopManager().save();
                return true;
            });
        });

        this.addItem(Material.CHEST_MINECART, EditorLocales.SHOP_CATEGORY_PRODUCTS, 24).setClick((viewer, event) -> {
            this.getProductsEditor().openNextTick(viewer, 1);
        });

        this.getItems().forEach(menuItem -> {
            menuItem.getOptions().addDisplayModifier((viewer, item) -> ItemUtil.replace(item, category.replacePlaceholders()));
        });
    }

    private void save(@NotNull MenuViewer viewer) {
        this.object.getShopManager().save();
        this.openNextTick(viewer, viewer.getPage());
    }

    @Override
    public void clear() {
        super.clear();
        if (this.productsEditor != null) {
            this.productsEditor.clear();
            this.productsEditor = null;
        }
    }

    @NotNull
    public ShopProductListEditor getProductsEditor() {
        if (this.productsEditor == null) {
            this.productsEditor = new ShopProductListEditor(this.object);
        }
        return productsEditor;
    }

    @Override
    public void onClick(@NotNull MenuViewer viewer, @Nullable ItemStack item, @NotNull SlotType slotType, int slot, @NotNull InventoryClickEvent event) {
        super.onClick(viewer, item, slotType, slot, event);
        if (slotType == SlotType.PLAYER || slotType == SlotType.PLAYER_EMPTY) {
            event.setCancelled(false);
        }
    }
}
