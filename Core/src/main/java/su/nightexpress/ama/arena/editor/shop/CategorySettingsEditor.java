package su.nightexpress.ama.arena.editor.shop;

import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nexmedia.engine.api.menu.impl.EditorMenu;
import su.nexmedia.engine.api.menu.impl.MenuViewer;
import su.nexmedia.engine.editor.EditorManager;
import su.nexmedia.engine.utils.ItemReplacer;
import su.nexmedia.engine.utils.ItemUtil;
import su.nightexpress.ama.AMA;
import su.nightexpress.ama.arena.shop.impl.ShopCategory;
import su.nightexpress.ama.config.Lang;
import su.nightexpress.ama.editor.EditorLocales;

public class CategorySettingsEditor extends EditorMenu<AMA, ShopCategory> {

    private static final String TEXTURE_ITEMS = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOTAwZDI4ZmY3YjU0M2RkMDg4ZDAwNGIxYjFmOTViMzhkNDQ0ZWEwNDYxZmY1YWUzYzY4ZDc2YzBjMTZlMjUyNyJ9fX0=";

    private ProductListEditor productsEditor;

    public CategorySettingsEditor(@NotNull ShopCategory category) {
        super(category.getShopManager().plugin(), category, "Category Editor [" + category.getId() + "]", 45);

        this.addReturn(40).setClick((viewer, event) -> {
            category.getShopManager().getEditor().getCategoryListEditor().openNextTick(viewer, 1);
        });

        this.addItem(Material.NAME_TAG, EditorLocales.SHOP_CATEGORY_NAME, 10).setClick((viewer, event) -> {
            this.handleInput(viewer, Lang.EDITOR_GENERIC_ENTER_NAME, wrapper -> {
                category.setName(wrapper.getText());
                category.getShopManager().save();
                return true;
            });
        });

        this.addItem(Material.WRITABLE_BOOK, EditorLocales.SHOP_CATEGORY_DESCRIPTION, 12).setClick((viewer, event) -> {
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

        this.addItem(Material.ITEM_FRAME, EditorLocales.SHOP_CATEGORY_ICON, 14).setClick((viewer, event) -> {
            ItemStack cursor = event.getCursor();
            if (cursor == null || cursor.getType().isAir()) return;

            category.setIcon(cursor);
            event.getView().setCursor(null);
            this.save(viewer);
        }).getOptions().addDisplayModifier((viewer, item) -> {
            item.setType(category.getIcon().getType());
        });

        this.addItem(Material.ARMOR_STAND, EditorLocales.SHOP_CATEGORY_KITS_REQUIRED, 16).setClick((viewer, event) -> {
            if (event.isRightClick()) {
                category.getKitsRequired().clear();
                this.save(viewer);
                return;
            }
            EditorManager.suggestValues(viewer.getPlayer(), plugin.getKitManager().getKitIds(), true);
            this.handleInput(viewer, Lang.EDITOR_KIT_ENTER_ID, wrapper -> {
                category.getKitsRequired().add(wrapper.getTextRaw());
                category.getShopManager().save();
                return true;
            });
        });

        this.addItem(ItemUtil.createCustomHead(TEXTURE_ITEMS), EditorLocales.SHOP_CATEGORY_PRODUCTS, 22).setClick((viewer, event) -> {
            this.getProductsEditor().openNextTick(viewer, 1);
        });

        this.getItems().forEach(menuItem -> menuItem.getOptions().addDisplayModifier((viewer, item) -> {
            ItemReplacer.replace(item, category.replacePlaceholders());
        }));
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
    public ProductListEditor getProductsEditor() {
        if (this.productsEditor == null) {
            this.productsEditor = new ProductListEditor(this.plugin, this.object);
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
