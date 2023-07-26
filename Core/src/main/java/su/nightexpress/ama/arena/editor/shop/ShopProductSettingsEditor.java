package su.nightexpress.ama.arena.editor.shop;

import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nexmedia.engine.api.menu.impl.EditorMenu;
import su.nexmedia.engine.api.menu.impl.Menu;
import su.nexmedia.engine.api.menu.impl.MenuViewer;
import su.nexmedia.engine.editor.EditorManager;
import su.nexmedia.engine.utils.ItemUtil;
import su.nightexpress.ama.AMA;
import su.nightexpress.ama.api.currency.Currency;
import su.nightexpress.ama.arena.shop.impl.ShopProduct;
import su.nightexpress.ama.config.Lang;
import su.nightexpress.ama.editor.EditorHub;
import su.nightexpress.ama.editor.EditorLocales;

import java.util.Objects;
import java.util.stream.Stream;

public class ShopProductSettingsEditor extends EditorMenu<AMA, ShopProduct> {

    public ShopProductSettingsEditor(@NotNull ShopProduct product) {
        super(product.getShopCategory().plugin(), product, EditorHub.TITLE_SHOP_EDITOR, 45);

        this.addReturn(40).setClick((viewer, event) -> {
            product.getShopCategory().getEditor().getProductsEditor().openNextTick(viewer, 1);
        });

        this.addItem(Material.NAME_TAG, EditorLocales.SHOP_PRODUCT_NAME, 2).setClick((viewer, event) -> {
            this.handleInput(viewer, Lang.EDITOR_GENERIC_ENTER_NAME, wrapper -> {
                product.setName(wrapper.getText());
                product.getShopCategory().getShopManager().save();
                return true;
            });
        });

        this.addItem(Material.ITEM_FRAME, EditorLocales.SHOP_PRODUCT_ICON, 4).setClick((viewer, event) -> {
            ItemStack cursor = event.getCursor();
            if (cursor == null || cursor.getType().isAir()) return;

            product.setIcon(cursor);
            event.getView().setCursor(null);
            this.save(viewer);
        }).getOptions().addDisplayModifier((viewer, item) -> item.setType(product.getIcon().getType()));

        this.addItem(Material.MAP, EditorLocales.SHOP_PRODUCT_DESCRIPTION, 6).setClick((viewer, event) -> {
            if (event.isRightClick()) {
                product.getDescription().clear();
                this.save(viewer);
                return;
            }
            this.handleInput(viewer, Lang.EDITOR_GENERIC_ENTER_DESCRIPTION, wrapper -> {
                product.getDescription().add(wrapper.getText());
                product.getShopCategory().getShopManager().save();
                return true;
            });
        });

        this.addItem(Material.GOLD_NUGGET, EditorLocales.SHOP_PRODUCT_PRICE, 11).setClick((viewer, event) -> {
            this.handleInput(viewer, Lang.EDITOR_ARENA_SHOP_ENTER_PRODUCT_PRICE, wrapper -> {
                product.setPrice(wrapper.asDouble());
                product.getShopCategory().getShopManager().save();
                return true;
            });
        });

        this.addItem(Material.EMERALD, EditorLocales.SHOP_PRODUCT_CURRENCY, 13).setClick((viewer, event) -> {
            EditorManager.suggestValues(viewer.getPlayer(), plugin.getCurrencyManager().getCurrencyIds(), true);
            this.handleInput(viewer, Lang.EDITOR_GENERIC_ENTER_CURRENCY, wrapper -> {
                Currency currency = plugin.getCurrencyManager().getCurrency(wrapper.getTextRaw());
                if (currency == null) {
                    EditorManager.error(viewer.getPlayer(), plugin.getMessage(Lang.ERROR_CURRENCY_INVALID).getLocalized());
                    return false;
                }
                product.setCurrency(currency);
                product.getShopCategory().getShopManager().save();
                return true;
            });
        });

        this.addItem(Material.ARMOR_STAND, EditorLocales.SHOP_PRODUCT_REQUIRED_KITS, 15).setClick((viewer, event) -> {
            if (event.isRightClick()) {
                product.getAllowedKits().clear();
                this.save(viewer);
                return;
            }
            EditorManager.suggestValues(viewer.getPlayer(), plugin.getKitManager().getKitIds(), true);
            this.handleInput(viewer, Lang.EDITOR_KIT_ENTER_ID, wrapper -> {
                product.getAllowedKits().add(wrapper.getTextRaw().toLowerCase());
                product.getShopCategory().getShopManager().save();
                return true;
            });
        });

        this.addItem(Material.COMMAND_BLOCK, EditorLocales.SHOP_PRODUCT_COMMANDS, 21).setClick((viewer, event) -> {
            if (event.isRightClick()) {
                product.getCommands().clear();
                this.save(viewer);
                return;
            }
            this.handleInput(viewer, Lang.EDITOR_GENERIC_ENTER_COMMAND, wrapper -> {
                product.getCommands().add(wrapper.getText());
                product.getShopCategory().getShopManager().save();
                return true;
            });
        });

        this.addItem(Material.CHEST_MINECART, EditorLocales.SHOP_PRODUCT_ITEMS, 23).setClick((viewer, event) -> {
            new ProductItems(product).openNextTick(viewer, 1);
        });

        this.getItems().forEach(menuItem -> {
            menuItem.getOptions().addDisplayModifier((viewer, item) -> ItemUtil.replace(item, product.replacePlaceholders()));
        });
    }

    private void save(@NotNull MenuViewer viewer) {
        this.object.getShopCategory().getShopManager().save();
        this.openNextTick(viewer, viewer.getPage());
    }

    @Override
    public void onClick(@NotNull MenuViewer viewer, @Nullable ItemStack item, @NotNull SlotType slotType, int slot, @NotNull InventoryClickEvent event) {
        super.onClick(viewer, item, slotType, slot, event);
        if (slotType == SlotType.PLAYER || slotType == SlotType.PLAYER_EMPTY) {
            event.setCancelled(false);
        }
    }

    private static class ProductItems extends Menu<AMA> {

        private final ShopProduct shopProduct;

        public ProductItems(@NotNull ShopProduct shopProduct) {
            super(shopProduct.getShopCategory().getShopManager().plugin(), "Product Items", 27);
            this.shopProduct = shopProduct;
        }

        @Override
        public boolean isPersistent() {
            return false;
        }

        @Override
        public void onReady(@NotNull MenuViewer viewer, @NotNull Inventory inventory) {
            super.onReady(viewer, inventory);
            inventory.setContents(this.shopProduct.getItems().toArray(new ItemStack[this.getOptions().getSize()]));
        }

        @Override
        public void onClick(@NotNull MenuViewer viewer, @Nullable ItemStack item, @NotNull SlotType slotType, int slot, @NotNull InventoryClickEvent event) {
            super.onClick(viewer, item, slotType, slot, event);
            event.setCancelled(false);
        }

        @Override
        public void onClose(@NotNull MenuViewer viewer, @NotNull InventoryCloseEvent event) {
            Inventory inventory = event.getInventory();
            this.shopProduct.setItems(Stream.of(inventory.getContents()).filter(Objects::nonNull).toList());
            this.shopProduct.getShopCategory().getShopManager().save();
            this.shopProduct.getEditor().openNextTick(viewer, 1);
            super.onClose(viewer, event);
        }
    }
}
