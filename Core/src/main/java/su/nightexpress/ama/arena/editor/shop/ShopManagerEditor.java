package su.nightexpress.ama.arena.editor.shop;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.editor.EditorButtonType;
import su.nexmedia.engine.api.menu.MenuClick;
import su.nexmedia.engine.api.menu.MenuItem;
import su.nexmedia.engine.api.menu.MenuItemType;
import su.nexmedia.engine.editor.AbstractEditorMenu;
import su.nexmedia.engine.utils.ItemUtil;
import su.nightexpress.ama.AMA;
import su.nightexpress.ama.arena.shop.ShopManager;
import su.nightexpress.ama.editor.ArenaEditorHub;
import su.nightexpress.ama.editor.ArenaEditorType;

import java.util.Map;

public class ShopManagerEditor extends AbstractEditorMenu<AMA, ShopManager> {

    private ShopCategoryListEditor categoryEditor;

    public ShopManagerEditor(@NotNull ShopManager shopManager) {
        super(shopManager.plugin(), shopManager, ArenaEditorHub.TITLE_SHOP_EDITOR, 45);

        MenuClick click = (player, type, e) -> {
            if (type instanceof MenuItemType type2) {
                if (type2 == MenuItemType.RETURN) {
                    shopManager.getArenaConfig().getEditor().open(player, 1);
                }
            }
            else if (type instanceof ArenaEditorType type2) {
                switch (type2) {
                    case SHOP_OPEN_CATEGORIES -> {
                        this.getCategoryListEditor().open(player, 1);
                        return;
                    }
                    case SHOP_CHANGE_ACTIVE -> shopManager.setActive(!shopManager.isActive());
                    case SHOP_CHANGE_HIDE_OTHER_KIT_ITEMS -> shopManager.setHideOtherKitProducts(!shopManager.isHideOtherKitProducts());
                }
                shopManager.save();
                this.open(player, 1);
            }
        };

        this.loadItems(click);
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
    public ShopCategoryListEditor getCategoryListEditor() {
        if (this.categoryEditor == null) {
            this.categoryEditor = new ShopCategoryListEditor(this.object);
        }
        return this.categoryEditor;
    }

    @Override
    public void setTypes(@NotNull Map<EditorButtonType, Integer> map) {
        map.put(ArenaEditorType.SHOP_CHANGE_ACTIVE, 4);
        map.put(ArenaEditorType.SHOP_OPEN_CATEGORIES, 22);
        map.put(MenuItemType.RETURN, 40);
    }

    @Override
    public void onItemPrepare(@NotNull Player player, @NotNull MenuItem menuItem, @NotNull ItemStack item) {
        super.onItemPrepare(player, menuItem, item);
        if (menuItem.getType() instanceof ArenaEditorType editorType) {
            if (editorType == ArenaEditorType.SHOP_CHANGE_ACTIVE) {
                item.setType(this.object.isActive() ? Material.LIME_DYE : Material.GRAY_DYE);
            }
        }
        ItemUtil.replace(item, this.object.replacePlaceholders());
    }

    @Override
    public boolean cancelClick(@NotNull InventoryClickEvent e, @NotNull SlotType slotType) {
        return true;
    }
}
