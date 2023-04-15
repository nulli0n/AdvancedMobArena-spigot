package su.nightexpress.ama.arena.editor.shop;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.editor.EditorButtonType;
import su.nexmedia.engine.api.editor.EditorInput;
import su.nexmedia.engine.api.menu.MenuClick;
import su.nexmedia.engine.api.menu.MenuItemType;
import su.nexmedia.engine.editor.AbstractEditorMenuAuto;
import su.nexmedia.engine.editor.EditorManager;
import su.nexmedia.engine.utils.ItemUtil;
import su.nightexpress.ama.AMA;
import su.nightexpress.ama.arena.shop.ShopManager;
import su.nightexpress.ama.arena.shop.impl.ShopCategory;
import su.nightexpress.ama.config.Lang;
import su.nightexpress.ama.editor.ArenaEditorHub;
import su.nightexpress.ama.editor.ArenaEditorType;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

public class ShopCategoryListEditor extends AbstractEditorMenuAuto<AMA, ShopManager, ShopCategory> {

    public ShopCategoryListEditor(@NotNull ShopManager shopManager) {
        super(shopManager.plugin(), shopManager, ArenaEditorHub.TITLE_SHOP_EDITOR, 45);

        EditorInput<ShopManager, ArenaEditorType> input = (player, shopManager2, type, e) -> {
            String msg = e.getMessage();
            if (type == ArenaEditorType.SHOP_CATEGORY_CREATE) {
                if (!shopManager2.createCategory(EditorManager.fineId(msg))) {
                    EditorManager.error(player, plugin.getMessage(Lang.EDITOR_ARENA_SHOP_ERROR_CATEGORY_EXISTS).getLocalized());
                    return false;
                }
            }
            shopManager2.save();
            return true;
        };

        MenuClick click = (player, type, e) -> {
            if (type instanceof MenuItemType type2) {
                if (type2 == MenuItemType.RETURN) {
                    shopManager.getEditor().open(player, 1);
                }
            }
            else if (type instanceof ArenaEditorType type2) {
                if (type2 == ArenaEditorType.SHOP_CATEGORY_CREATE) {
                    EditorManager.startEdit(player, shopManager, type2, input);
                    EditorManager.prompt(player, plugin.getMessage(Lang.EDITOR_ARENA_SHOP_ENTER_CATEGORY_ID).getLocalized());
                    player.closeInventory();
                }
            }
        };

        this.loadItems(click);
    }

    @Override
    public void setTypes(@NotNull Map<EditorButtonType, Integer> map) {
        map.put(ArenaEditorType.SHOP_CATEGORY_CREATE, 41);
        map.put(MenuItemType.RETURN, 39);
        map.put(MenuItemType.PAGE_NEXT, 44);
        map.put(MenuItemType.PAGE_PREVIOUS, 36);
    }

    @Override
    public int[] getObjectSlots() {
        return IntStream.range(0, 36).toArray();
    }

    @Override
    @NotNull
    protected List<ShopCategory> getObjects(@NotNull Player player) {
        return new ArrayList<>(this.parent.getCategories());
    }

    @Override
    @NotNull
    protected ItemStack getObjectStack(@NotNull Player player, @NotNull ShopCategory shopCategory) {
        ItemStack item = ArenaEditorType.SHOP_CATEGORY_OBJECT.getItem();
        item.setType(shopCategory.getIcon().getType());
        ItemUtil.replace(item, shopCategory.replacePlaceholders());
        return item;
    }

    @Override
    @NotNull
    protected MenuClick getObjectClick(@NotNull Player player, @NotNull ShopCategory shopCategory) {
        return (p2, type, e) -> {
            if (e.isShiftClick() && e.isRightClick()) {
                shopCategory.clear();
                this.parent.getCategoryMap().remove(shopCategory.getId());
                this.parent.save();
                this.open(p2, 1);
                return;
            }
            shopCategory.getEditor().open(p2, 1);
        };
    }

    @Override
    public boolean cancelClick(@NotNull InventoryClickEvent e, @NotNull SlotType slotType) {
        return true;
    }
}
