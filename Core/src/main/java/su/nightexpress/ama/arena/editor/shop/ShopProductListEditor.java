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
import su.nightexpress.ama.arena.shop.impl.ShopCategory;
import su.nightexpress.ama.arena.shop.impl.ShopProduct;
import su.nightexpress.ama.config.Lang;
import su.nightexpress.ama.editor.ArenaEditorHub;
import su.nightexpress.ama.editor.ArenaEditorType;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

public class ShopProductListEditor extends AbstractEditorMenuAuto<AMA, ShopCategory, ShopProduct> {

    public ShopProductListEditor(@NotNull ShopCategory shopCategory) {
        super(shopCategory.plugin(), shopCategory, ArenaEditorHub.TITLE_SHOP_EDITOR, 45);

        EditorInput<ShopCategory, ArenaEditorType> input = (player, category, type, e) -> {
            String msg = e.getMessage();
            if (type == ArenaEditorType.SHOP_PRODUCT_CREATE) {
                if (!category.createProduct(EditorManager.fineId(msg))) {
                    EditorManager.error(player, plugin.getMessage(Lang.EDITOR_ARENA_SHOP_ERROR_PRODUCT_EXISTS).getLocalized());
                    return false;
                }
            }
            category.getShopManager().save();
            return true;
        };

        MenuClick click = (player, type, e) -> {
            if (type instanceof MenuItemType type2) {
                if (type2 == MenuItemType.RETURN) {
                    shopCategory.getEditor().open(player, 1);
                }
            }
            else if (type instanceof ArenaEditorType type2) {
                if (type2 == ArenaEditorType.SHOP_PRODUCT_CREATE) {
                    EditorManager.startEdit(player, shopCategory, type2, input);
                    EditorManager.tip(player, plugin.getMessage(Lang.EDITOR_ARENA_SHOP_ENTER_PRODUCT_ID).getLocalized());
                    player.closeInventory();
                }
            }
        };

        this.loadItems(click);
    }

    @Override
    public void setTypes(@NotNull Map<EditorButtonType, Integer> map) {
        map.put(ArenaEditorType.SHOP_PRODUCT_CREATE, 41);
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
    protected List<ShopProduct> getObjects(@NotNull Player player) {
        return new ArrayList<>(this.parent.getProducts());
    }

    @Override
    @NotNull
    protected ItemStack getObjectStack(@NotNull Player player, @NotNull ShopProduct shopProduct) {
        ItemStack object = ArenaEditorType.SHOP_PRODUCT_OBJECT.getItem();
        ItemStack item = new ItemStack(shopProduct.getIcon());
        ItemUtil.mapMeta(item, meta -> {
            meta.setDisplayName(ItemUtil.getItemName(object));
            meta.setLore(ItemUtil.getLore(object));
            ItemUtil.replace(meta, shopProduct.replacePlaceholders());
        });
        return item;
    }

    @Override
    @NotNull
    protected MenuClick getObjectClick(@NotNull Player player, @NotNull ShopProduct shopProduct) {
        return (player1, type, e) -> {
            if (e.isShiftClick() && e.isRightClick()) {
                shopProduct.clear();
                this.parent.getProducts().remove(shopProduct);
                this.parent.getShopManager().save();
                this.open(player1, 1);
                return;
            }
            shopProduct.getEditor().open(player1, 1);
        };
    }

    @Override
    public boolean cancelClick(@NotNull InventoryClickEvent e, @NotNull SlotType slotType) {
        return true;
    }
}
