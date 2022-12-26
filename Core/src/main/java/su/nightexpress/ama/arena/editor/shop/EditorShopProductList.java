package su.nightexpress.ama.arena.editor.shop;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
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
import su.nightexpress.ama.arena.shop.ArenaShopCategory;
import su.nightexpress.ama.arena.shop.ArenaShopProduct;
import su.nightexpress.ama.config.Lang;
import su.nightexpress.ama.editor.ArenaEditorType;
import su.nightexpress.ama.editor.ArenaEditorUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

public class EditorShopProductList extends AbstractEditorMenuAuto<AMA, ArenaShopCategory, ArenaShopProduct> {

    public EditorShopProductList(@NotNull ArenaShopCategory shopCategory) {
        super(shopCategory.plugin(), shopCategory, ArenaEditorUtils.TITLE_SHOP_EDITOR, 45);

        EditorInput<ArenaShopCategory, ArenaEditorType> input = (player, category, type, e) -> {
            String msg = StringUtil.color(e.getMessage());
            if (type == ArenaEditorType.SHOP_PRODUCT_CREATE) {
                String id = EditorManager.fineId(msg);
                boolean hasProduct = category.getProduct(id) != null;
                if (hasProduct) {
                    EditorManager.error(player, plugin.getMessage(Lang.Editor_Arena_Shop_Error_Product_Exist).getLocalized());
                    return false;
                }

                ArenaShopProduct product = new ArenaShopProduct(category, id, plugin.getCurrencyManager().getCurrencyFirst());
                category.getProductsMap().put(product.getId(), product);
            }

            category.getShopManager().save();
            return true;
        };

        MenuClick click = (player, type, e) -> {
            if (type instanceof MenuItemType type2) {
                if (type2 == MenuItemType.RETURN) {
                    shopCategory.getEditor().open(player, 1);
                }
                else this.onItemClickDefault(player, type2);
            }
            else if (type instanceof ArenaEditorType type2) {
                if (type2 == ArenaEditorType.SHOP_PRODUCT_CREATE) {
                    EditorManager.startEdit(player, shopCategory, type2, input);
                    EditorManager.tip(player, plugin.getMessage(Lang.Editor_Arena_Shop_Enter_Product_Create).getLocalized());
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
    protected List<ArenaShopProduct> getObjects(@NotNull Player player) {
        return new ArrayList<>(this.parent.getProducts());
    }

    @Override
    @NotNull
    protected ItemStack getObjectStack(@NotNull Player player, @NotNull ArenaShopProduct shopProduct) {
        ItemStack item = new ItemStack(shopProduct.getIcon());
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return item;

        ItemStack object = ArenaEditorType.SHOP_PRODUCT_OBJECT.getItem();

        meta.setDisplayName(ItemUtil.getItemName(object));
        meta.setLore(ItemUtil.getLore(object));
        item.setItemMeta(meta);
        ItemUtil.replace(item, shopProduct.replacePlaceholders());

        return item;
    }

    @Override
    @NotNull
    protected MenuClick getObjectClick(@NotNull Player player, @NotNull ArenaShopProduct shopProduct) {
        return (p2, type, e) -> {
            if (e.isShiftClick() && e.isRightClick()) {
                shopProduct.clear();
                this.parent.getProducts().remove(shopProduct);
                this.parent.getShopManager().save();
                this.open(p2, 1);
                return;
            }
            shopProduct.getEditor().open(p2, 1);
        };
    }

    @Override
    public boolean cancelClick(@NotNull InventoryClickEvent e, @NotNull SlotType slotType) {
        return true;
    }
}
