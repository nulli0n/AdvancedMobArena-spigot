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
import su.nexmedia.engine.utils.ItemReplacer;
import su.nexmedia.engine.utils.ItemUtil;
import su.nightexpress.ama.AMA;
import su.nightexpress.ama.api.currency.Currency;
import su.nightexpress.ama.arena.shop.impl.ShopProduct;
import su.nightexpress.ama.config.Lang;
import su.nightexpress.ama.editor.EditorLocales;

import java.util.Objects;
import java.util.stream.Stream;

public class ProductSettingsEditor extends EditorMenu<AMA, ShopProduct> {

    private static final String TEXTURE_COMMAND = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNmQwZjQwNjFiZmI3NjdhN2Y5MjJhNmNhNzE3NmY3YTliMjA3MDliZDA1MTI2OTZiZWIxNWVhNmZhOThjYTU1YyJ9fX0=";
    private static final String TEXTURE_ITEMS   = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvODM1MWU1MDU5ODk4MzhlMjcyODdlN2FmYmM3Zjk3ZTc5NmNhYjVmMzU5OGE3NjE2MGMxMzFjOTQwZDBjNSJ9fX0=";

    public ProductSettingsEditor(@NotNull AMA plugin, @NotNull ShopProduct product) {
        super(plugin, product, "Product Editor [" + product.getId() + "]", 45);

        this.addReturn(40).setClick((viewer, event) -> {
            product.getCategory().getEditor().getProductsEditor().openNextTick(viewer, 1);
        });

        this.addItem(Material.NAME_TAG, EditorLocales.SHOP_PRODUCT_NAME, 9).setClick((viewer, event) -> {
            this.handleInput(viewer, Lang.EDITOR_GENERIC_ENTER_NAME, wrapper -> {
                product.setName(wrapper.getText());
                product.getCategory().getShopManager().save();
                return true;
            });
        });

        this.addItem(Material.WRITABLE_BOOK, EditorLocales.SHOP_PRODUCT_DESCRIPTION, 11).setClick((viewer, event) -> {
            if (event.isRightClick()) {
                product.getDescription().clear();
                this.save(viewer);
                return;
            }
            this.handleInput(viewer, Lang.EDITOR_GENERIC_ENTER_DESCRIPTION, wrapper -> {
                product.getDescription().add(wrapper.getText());
                product.getCategory().getShopManager().save();
                return true;
            });
        });

        this.addItem(Material.ITEM_FRAME, EditorLocales.SHOP_PRODUCT_ICON, 13).setClick((viewer, event) -> {
            ItemStack cursor = event.getCursor();
            if (cursor == null || cursor.getType().isAir()) return;

            product.setIcon(cursor);
            event.getView().setCursor(null);
            this.save(viewer);
        }).getOptions().addDisplayModifier((viewer, item) -> item.setType(product.getIcon().getType()));

        this.addItem(Material.ARMOR_STAND, EditorLocales.SHOP_PRODUCT_KITS_REQUIRED, 15).setClick((viewer, event) -> {
            if (event.isRightClick()) {
                product.getKitsRequired().clear();
                this.save(viewer);
                return;
            }
            EditorManager.suggestValues(viewer.getPlayer(), plugin.getKitManager().getKitIds(), true);
            this.handleInput(viewer, Lang.EDITOR_KIT_ENTER_ID, wrapper -> {
                product.getKitsRequired().add(wrapper.getTextRaw().toLowerCase());
                product.getCategory().getShopManager().save();
                return true;
            });
        });

        this.addItem(Material.GOLD_INGOT, EditorLocales.SHOP_PRODUCT_PRICE, 17).setClick((viewer, event) -> {
            if (event.isLeftClick()) {
                this.handleInput(viewer, Lang.EDITOR_ARENA_SHOP_ENTER_PRODUCT_PRICE, wrapper -> {
                    product.setPrice(wrapper.asDouble());
                    product.getCategory().getShopManager().save();
                    return true;
                });
            }
            else if (event.isRightClick()) {
                EditorManager.suggestValues(viewer.getPlayer(), plugin.getCurrencyManager().getCurrencyIds(), true);
                this.handleInput(viewer, Lang.EDITOR_GENERIC_ENTER_CURRENCY, wrapper -> {
                    Currency currency = plugin.getCurrencyManager().getCurrency(wrapper.getTextRaw());
                    if (currency == null) {
                        EditorManager.error(viewer.getPlayer(), plugin.getMessage(Lang.ERROR_CURRENCY_INVALID).getLocalized());
                        return false;
                    }
                    product.setCurrency(currency);
                    product.getCategory().getShopManager().save();
                    return true;
                });
            }
        });

        this.addItem(ItemUtil.createCustomHead(TEXTURE_COMMAND), EditorLocales.SHOP_PRODUCT_COMMANDS, 21).setClick((viewer, event) -> {
            if (event.isRightClick()) {
                product.getCommands().clear();
                this.save(viewer);
                return;
            }
            this.handleInput(viewer, Lang.EDITOR_GENERIC_ENTER_COMMAND, wrapper -> {
                product.getCommands().add(wrapper.getText());
                product.getCategory().getShopManager().save();
                return true;
            });
        });

        this.addItem(ItemUtil.createCustomHead(TEXTURE_ITEMS), EditorLocales.SHOP_PRODUCT_ITEMS, 23).setClick((viewer, event) -> {
            new ProductItems(plugin, product).openNextTick(viewer, 1);
        });

        this.getItems().forEach(menuItem -> menuItem.getOptions().addDisplayModifier((viewer, item) -> {
            ItemReplacer.replace(item, product.replacePlaceholders());
        }));
    }

    private void save(@NotNull MenuViewer viewer) {
        this.object.getCategory().getShopManager().save();
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

        private final ShopProduct product;

        public ProductItems(@NotNull AMA plugin, @NotNull ShopProduct product) {
            super(plugin, "Product Items [" + product.getId() + "]", 27);
            this.product = product;
        }

        @Override
        public boolean isPersistent() {
            return false;
        }

        @Override
        public void onReady(@NotNull MenuViewer viewer, @NotNull Inventory inventory) {
            super.onReady(viewer, inventory);
            inventory.setContents(this.product.getItems().toArray(new ItemStack[this.getOptions().getSize()]));
        }

        @Override
        public void onClick(@NotNull MenuViewer viewer, @Nullable ItemStack item, @NotNull SlotType slotType, int slot, @NotNull InventoryClickEvent event) {
            super.onClick(viewer, item, slotType, slot, event);
            event.setCancelled(false);
        }

        @Override
        public void onClose(@NotNull MenuViewer viewer, @NotNull InventoryCloseEvent event) {
            Inventory inventory = event.getInventory();
            this.product.setItems(Stream.of(inventory.getContents()).filter(Objects::nonNull).toList());
            this.product.getCategory().getShopManager().save();
            this.product.getEditor().openNextTick(viewer, 1);
            super.onClose(viewer, event);
        }
    }
}
