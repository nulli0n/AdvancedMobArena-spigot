package su.nightexpress.ama.arena.editor.shop;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.editor.EditorButtonType;
import su.nexmedia.engine.api.editor.EditorInput;
import su.nexmedia.engine.api.menu.IMenu;
import su.nexmedia.engine.api.menu.IMenuClick;
import su.nexmedia.engine.api.menu.IMenuItem;
import su.nexmedia.engine.api.menu.MenuItemType;
import su.nexmedia.engine.editor.AbstractEditorMenu;
import su.nexmedia.engine.editor.EditorManager;
import su.nexmedia.engine.utils.ItemUtil;
import su.nexmedia.engine.utils.StringUtil;
import su.nightexpress.ama.AMA;
import su.nightexpress.ama.arena.shop.ArenaShopCategory;
import su.nightexpress.ama.config.Lang;
import su.nightexpress.ama.editor.ArenaEditorType;
import su.nightexpress.ama.editor.ArenaEditorUtils;

import java.util.Map;

public class EditorShopCategorySettings extends AbstractEditorMenu<AMA, ArenaShopCategory> {

    private EditorShopProductList editorProducts;

    public EditorShopCategorySettings(@NotNull ArenaShopCategory shopCategory) {
        super(shopCategory.getShopManager().plugin(), shopCategory, ArenaEditorUtils.TITLE_SHOP_EDITOR, 45);

        EditorInput<ArenaShopCategory, ArenaEditorType> input = (player, category, type, e) -> {
            String msg = StringUtil.color(e.getMessage());
            switch (type) {
                case SHOP_CATEGORY_CHANGE_NAME -> category.setName(msg);
                case SHOP_CATEGORY_CHANGE_DESCRIPTION -> category.getDescription().add(msg);
                case SHOP_CATEGORY_CHANGE_ALLOWED_KITS -> category.getAllowedKits().add(StringUtil.colorOff(msg));
                default -> {return true;}
            }

            category.getShopManager().save();
            return true;
        };

        IMenuClick click = (player, type, e) -> {
            if (type == null) return;

            if (type instanceof MenuItemType type2) {
                if (type2 == MenuItemType.RETURN) {
                    shopCategory.getShopManager().getEditor().getEditorCategories().open(player, 1);
                }
            }
            else if (type instanceof ArenaEditorType type2) {
                switch (type2) {
                    case SHOP_CATEGORY_CHANGE_NAME -> {
                        EditorManager.startEdit(player, shopCategory, type2, input);
                        EditorManager.tip(player, StringUtil.color(plugin.getMessage(Lang.EDITOR_GENERIC_ENTER_NAME).getLocalized()));
                        player.closeInventory();
                        return;
                    }
                    case SHOP_CATEGORY_CHANGE_DESCRIPTION -> {
                        if (e.isRightClick()) {
                            shopCategory.getDescription().clear();
                            break;
                        }
                        EditorManager.startEdit(player, shopCategory, type2, input);
                        EditorManager.tip(player, StringUtil.color(plugin.getMessage(Lang.EDITOR_GENERIC_ENTER_DESCRIPTION).getLocalized()));
                        player.closeInventory();
                        return;
                    }
                    case SHOP_CATEGORY_CHANGE_ICON -> {
                        ItemStack cursor = e.getCursor();
                        if (cursor == null || cursor.getType().isAir()) return;

                        shopCategory.setIcon(cursor);
                        e.getView().setCursor(null);
                    }
                    case SHOP_CATEGORY_CHANGE_ALLOWED_KITS -> {
                        if (e.isRightClick()) {
                            shopCategory.getAllowedKits().clear();
                            break;
                        }
                        EditorManager.startEdit(player, shopCategory, type2, input);
                        EditorManager.tip(player, StringUtil.color(plugin.getMessage(Lang.Editor_Arena_Shop_Enter_Product_RequiredKit).getLocalized()));
                        EditorManager.suggestValues(player, plugin.getKitManager().getKitIds(), true);
                        player.closeInventory();
                        return;
                    }
                    case SHOP_CATEGORY_CHANGE_PRODUCTS -> {
                        this.getEditorProducts().open(player, 1);
                        return;
                    }
                    case SHOP_CATEGORY_CHANGE_TRIGGERS_LOCKED, SHOP_CATEGORY_CHANGE_TRIGGERS_UNLOCKED -> {
                        ArenaEditorUtils.handleTriggersClick(player, shopCategory, type2, e.isRightClick());
                        if (e.isRightClick()) break;
                        return;
                    }
                    default -> {
                        return;
                    }
                }
                shopCategory.getShopManager().save();
                this.open(player, 1);
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
    public EditorShopProductList getEditorProducts() {
        if (this.editorProducts == null) {
            this.editorProducts = new EditorShopProductList(this.object);
        }
        return editorProducts;
    }

    @Override
    public void setTypes(@NotNull Map<EditorButtonType, Integer> map) {
        map.put(ArenaEditorType.SHOP_CATEGORY_CHANGE_NAME, 3);
        map.put(ArenaEditorType.SHOP_CATEGORY_CHANGE_DESCRIPTION, 5);
        map.put(ArenaEditorType.SHOP_CATEGORY_CHANGE_ICON, 13);
        map.put(ArenaEditorType.SHOP_CATEGORY_CHANGE_ALLOWED_KITS, 11);
        map.put(ArenaEditorType.SHOP_CATEGORY_CHANGE_TRIGGERS_LOCKED, 21);
        map.put(ArenaEditorType.SHOP_CATEGORY_CHANGE_TRIGGERS_UNLOCKED, 23);
        map.put(ArenaEditorType.SHOP_CATEGORY_CHANGE_PRODUCTS, 15);
        map.put(MenuItemType.RETURN, 40);

    }

    @Override
    public void onItemPrepare(@NotNull Player player, @NotNull IMenuItem menuItem, @NotNull ItemStack item) {
        super.onItemPrepare(player, menuItem, item);
        ItemUtil.replace(item, this.object.replacePlaceholders());
    }

    @Override
    public boolean cancelClick(@NotNull InventoryClickEvent e, @NotNull IMenu.SlotType slotType) {
        return slotType != SlotType.PLAYER && slotType != SlotType.EMPTY_PLAYER;
    }
}
