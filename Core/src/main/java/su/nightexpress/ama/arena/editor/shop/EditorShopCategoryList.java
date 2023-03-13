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
import su.nexmedia.engine.utils.StringUtil;
import su.nightexpress.ama.AMA;
import su.nightexpress.ama.arena.shop.impl.ArenaShopCategory;
import su.nightexpress.ama.arena.shop.ArenaShopManager;
import su.nightexpress.ama.config.Lang;
import su.nightexpress.ama.editor.ArenaEditorType;
import su.nightexpress.ama.editor.ArenaEditorUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

public class EditorShopCategoryList extends AbstractEditorMenuAuto<AMA, ArenaShopManager, ArenaShopCategory> {

    public EditorShopCategoryList(@NotNull ArenaShopManager shopManager) {
        super(shopManager.plugin(), shopManager, ArenaEditorUtils.TITLE_SHOP_EDITOR, 45);

        EditorInput<ArenaShopManager, ArenaEditorType> input = (player, shopManager2, type, e) -> {
            String msg = StringUtil.colorOff(e.getMessage());
            if (type == ArenaEditorType.SHOP_CATEGORY_CREATE) {
                String id = EditorManager.fineId(msg);
                boolean hasProduct = shopManager2.getCategory(id) != null;
                if (hasProduct) {
                    EditorManager.error(player, StringUtil.color("&7Category already exist!"));
                    return false;
                }

                ArenaShopCategory category = new ArenaShopCategory(shopManager2.getArenaConfig(), id);
                shopManager2.getCategoryMap().put(category.getId(), category);
            }

            shopManager2.save();
            return true;
        };

        MenuClick click = (player, type, e) -> {
            if (type instanceof MenuItemType type2) {
                if (type2 == MenuItemType.RETURN) {
                    shopManager.getEditor().open(player, 1);
                }
                else this.onItemClickDefault(player, type2);
            }
            else if (type instanceof ArenaEditorType type2) {
                if (type2 == ArenaEditorType.SHOP_CATEGORY_CREATE) {
                    EditorManager.startEdit(player, shopManager, type2, input);
                    EditorManager.tip(player, plugin.getMessage(Lang.Editor_Arena_Shop_Enter_Product_Create).getLocalized());
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
    protected List<ArenaShopCategory> getObjects(@NotNull Player player) {
        return new ArrayList<>(this.parent.getCategories());
    }

    @Override
    @NotNull
    protected ItemStack getObjectStack(@NotNull Player player, @NotNull ArenaShopCategory shopCategory) {
        ItemStack item = ArenaEditorType.SHOP_CATEGORY_OBJECT.getItem();
        item.setType(shopCategory.getIcon().getType());
        ItemUtil.replace(item, shopCategory.replacePlaceholders());
        return item;
    }

    @Override
    @NotNull
    protected MenuClick getObjectClick(@NotNull Player player, @NotNull ArenaShopCategory shopCategory) {
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
