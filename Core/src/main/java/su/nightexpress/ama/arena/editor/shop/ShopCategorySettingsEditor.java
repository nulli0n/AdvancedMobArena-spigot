package su.nightexpress.ama.arena.editor.shop;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.editor.EditorButtonType;
import su.nexmedia.engine.api.editor.EditorInput;
import su.nexmedia.engine.api.menu.MenuClick;
import su.nexmedia.engine.api.menu.MenuItem;
import su.nexmedia.engine.api.menu.MenuItemType;
import su.nexmedia.engine.editor.AbstractEditorMenu;
import su.nexmedia.engine.editor.EditorManager;
import su.nexmedia.engine.utils.Colorizer;
import su.nexmedia.engine.utils.ItemUtil;
import su.nightexpress.ama.AMA;
import su.nightexpress.ama.arena.shop.impl.ShopCategory;
import su.nightexpress.ama.config.Lang;
import su.nightexpress.ama.editor.ArenaEditorHub;
import su.nightexpress.ama.editor.ArenaEditorType;

import java.util.Map;

public class ShopCategorySettingsEditor extends AbstractEditorMenu<AMA, ShopCategory> {

    private ShopProductListEditor editorProducts;

    public ShopCategorySettingsEditor(@NotNull ShopCategory shopCategory) {
        super(shopCategory.getShopManager().plugin(), shopCategory, ArenaEditorHub.TITLE_SHOP_EDITOR, 45);

        EditorInput<ShopCategory, ArenaEditorType> input = (player, category, type, e) -> {
            String msg = e.getMessage();
            switch (type) {
                case SHOP_CATEGORY_CHANGE_NAME -> category.setName(msg);
                case SHOP_CATEGORY_CHANGE_DESCRIPTION -> category.getDescription().add(Colorizer.apply(msg));
                case SHOP_CATEGORY_CHANGE_ALLOWED_KITS -> category.getAllowedKits().add(Colorizer.strip(msg));
            }
            category.getShopManager().save();
            return true;
        };

        MenuClick click = (player, type, e) -> {
            if (type instanceof MenuItemType type2) {
                if (type2 == MenuItemType.RETURN) {
                    shopCategory.getShopManager().getEditor().getCategoryListEditor().open(player, 1);
                }
            }
            else if (type instanceof ArenaEditorType type2) {
                switch (type2) {
                    case SHOP_CATEGORY_CHANGE_NAME -> {
                        EditorManager.startEdit(player, shopCategory, type2, input);
                        EditorManager.prompt(player, plugin.getMessage(Lang.EDITOR_GENERIC_ENTER_NAME).getLocalized());
                        player.closeInventory();
                    }
                    case SHOP_CATEGORY_CHANGE_DESCRIPTION -> {
                        if (e.isRightClick()) {
                            shopCategory.getDescription().clear();
                            shopCategory.getShopManager().save();
                            this.open(player, 1);
                            break;
                        }
                        EditorManager.startEdit(player, shopCategory, type2, input);
                        EditorManager.prompt(player, plugin.getMessage(Lang.EDITOR_GENERIC_ENTER_DESCRIPTION).getLocalized());
                        player.closeInventory();
                    }
                    case SHOP_CATEGORY_CHANGE_ICON -> {
                        ItemStack cursor = e.getCursor();
                        if (cursor == null || cursor.getType().isAir()) return;

                        shopCategory.setIcon(cursor);
                        e.getView().setCursor(null);
                        shopCategory.getShopManager().save();
                        this.open(player, 1);
                    }
                    case SHOP_CATEGORY_CHANGE_ALLOWED_KITS -> {
                        if (e.isRightClick()) {
                            shopCategory.getAllowedKits().clear();
                            shopCategory.getShopManager().save();
                            this.open(player, 1);
                            break;
                        }
                        EditorManager.startEdit(player, shopCategory, type2, input);
                        EditorManager.prompt(player, plugin.getMessage(Lang.EDITOR_KIT_ENTER_ID).getLocalized());
                        EditorManager.suggestValues(player, plugin.getKitManager().getKitIds(), true);
                        player.closeInventory();
                    }
                    case SHOP_CATEGORY_CHANGE_PRODUCTS -> this.getEditorProducts().open(player, 1);
                }
            }
        };

        this.loadItems(click);
    }

    @Override
    public void clear() {
        super.clear();
        if (this.editorProducts != null) {
            this.editorProducts.clear();
            this.editorProducts = null;
        }
    }

    @NotNull
    public ShopProductListEditor getEditorProducts() {
        if (this.editorProducts == null) {
            this.editorProducts = new ShopProductListEditor(this.object);
        }
        return editorProducts;
    }

    @Override
    public void setTypes(@NotNull Map<EditorButtonType, Integer> map) {
        map.put(ArenaEditorType.SHOP_CATEGORY_CHANGE_NAME, 20);
        map.put(ArenaEditorType.SHOP_CATEGORY_CHANGE_DESCRIPTION, 21);
        map.put(ArenaEditorType.SHOP_CATEGORY_CHANGE_ICON, 22);
        map.put(ArenaEditorType.SHOP_CATEGORY_CHANGE_ALLOWED_KITS, 23);
        map.put(ArenaEditorType.SHOP_CATEGORY_CHANGE_PRODUCTS, 24);
        map.put(MenuItemType.RETURN, 40);

    }

    @Override
    public void onItemPrepare(@NotNull Player player, @NotNull MenuItem menuItem, @NotNull ItemStack item) {
        super.onItemPrepare(player, menuItem, item);
        if (menuItem.getType() instanceof ArenaEditorType type) {
            if (type == ArenaEditorType.SHOP_CATEGORY_CHANGE_ICON) {
                item.setType(this.object.getIcon().getType());
            }
        }
        ItemUtil.replace(item, this.object.replacePlaceholders());
    }

    @Override
    public boolean cancelClick(@NotNull InventoryClickEvent e, @NotNull SlotType slotType) {
        return slotType != SlotType.PLAYER && slotType != SlotType.EMPTY_PLAYER;
    }
}
