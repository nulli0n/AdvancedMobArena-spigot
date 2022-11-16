package su.nightexpress.ama.arena.editor.shop;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.editor.EditorButtonType;
import su.nexmedia.engine.api.editor.EditorInput;
import su.nexmedia.engine.api.menu.AbstractMenu;
import su.nexmedia.engine.api.menu.IMenuClick;
import su.nexmedia.engine.api.menu.IMenuItem;
import su.nexmedia.engine.api.menu.MenuItemType;
import su.nexmedia.engine.editor.AbstractEditorMenu;
import su.nexmedia.engine.editor.EditorManager;
import su.nexmedia.engine.utils.ItemUtil;
import su.nexmedia.engine.utils.StringUtil;
import su.nightexpress.ama.AMA;
import su.nightexpress.ama.api.currency.ICurrency;
import su.nightexpress.ama.arena.shop.ArenaShopProduct;
import su.nightexpress.ama.config.Lang;
import su.nightexpress.ama.editor.ArenaEditorType;
import su.nightexpress.ama.editor.ArenaEditorUtils;

import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;

public class EditorShopProductSettings extends AbstractEditorMenu<AMA, ArenaShopProduct> {

	public EditorShopProductSettings(@NotNull ArenaShopProduct shopProduct) {
		super(shopProduct.getShopCategory().plugin(), shopProduct, ArenaEditorUtils.TITLE_SHOP_EDITOR, 45);

		EditorInput<ArenaShopProduct, ArenaEditorType> input = (player, shopProduct2, type, e) -> {
			String msg = StringUtil.color(e.getMessage());
			switch (type) {
				case SHOP_PRODUCT_CHANGE_NAME -> shopProduct2.setName(msg);
				case SHOP_PRODUCT_CHANGE_DESCRIPTION -> shopProduct2.getDescription().add(msg);
				case SHOP_PRODUCT_CHANGE_CURRENCY -> {
					String curId = StringUtil.colorOff(msg);
					ICurrency currency = plugin.getCurrencyManager().getCurrency(curId);
					if (currency == null) {
						EditorManager.error(player, plugin.getMessage(Lang.ERROR_CURRENCY_INVALID).getLocalized());
						return false;
					}
					shopProduct2.setCurrency(currency);
				}
				case SHOP_PRODUCT_CHANGE_PRICE -> {
					double price = StringUtil.getDouble(msg, 0);
					shopProduct2.setPrice(price);
				}
				case SHOP_PRODUCT_CHANGE_COMMANDS -> shopProduct2.getCommands().add(StringUtil.colorRaw(msg));
				case SHOP_PRODUCT_CHANGE_REQUIRED_KITS -> shopProduct2.getApplicableKits().add(msg);
				default -> {return true;}
			}

			shopProduct2.getShopCategory().getShopManager().save();
			return true;
		};

		IMenuClick click = (player, type, e) -> {
			if (type instanceof MenuItemType type2) {
				if (type2 == MenuItemType.RETURN) {
					shopProduct.getShopCategory().getEditor().getEditorProducts().open(player, 1);
				}
			}
			else if (type instanceof ArenaEditorType type2) {
				switch (type2) {
					case SHOP_PRODUCT_CHANGE_NAME -> {
						EditorManager.startEdit(player, shopProduct, type2, input);
						EditorManager.tip(player, StringUtil.color(plugin.getMessage(Lang.EDITOR_GENERIC_ENTER_NAME).getLocalized()));
						player.closeInventory();
						return;
					}
					case SHOP_PRODUCT_CHANGE_DESCRIPTION -> {
						if (e.isRightClick()) {
							shopProduct.getDescription().clear();
							break;
						}
						EditorManager.startEdit(player, shopProduct, type2, input);
						EditorManager.tip(player, StringUtil.color(plugin.getMessage(Lang.EDITOR_GENERIC_ENTER_DESCRIPTION).getLocalized()));
						player.closeInventory();
						return;
					}
					case SHOP_PRODUCT_CHANGE_PRICE -> {
						EditorManager.startEdit(player, shopProduct, type2, input);
						EditorManager.tip(player, plugin.getMessage(Lang.Editor_Arena_Shop_Enter_Product_Price).getLocalized());
						player.closeInventory();
						return;
					}
					case SHOP_PRODUCT_CHANGE_CURRENCY -> {
						EditorManager.startEdit(player, shopProduct, type2, input);
						EditorManager.suggestValues(player, plugin.getCurrencyManager().getCurrencyIds(), true);
						EditorManager.tip(player, plugin.getMessage(Lang.EDITOR_GENERIC_ENTER_CURRENCY).getLocalized());
						player.closeInventory();
						return;
					}
					case SHOP_PRODUCT_CHANGE_ICON -> {
						ItemStack cursor = e.getCursor();
						if (cursor == null || cursor.getType().isAir()) return;

						shopProduct.setIcon(cursor);
						e.getView().setCursor(null);
					}
					case SHOP_PRODUCT_CHANGE_COMMANDS -> {
						if (e.isRightClick()) {
							shopProduct.getCommands().clear();
							break;
						}
						EditorManager.startEdit(player, shopProduct, type2, input);
						EditorManager.tip(player, plugin.getMessage(Lang.Editor_Arena_Shop_Enter_Product_Command).getLocalized());
						EditorManager.sendCommandTips(player);
						player.closeInventory();
						return;
					}
					case SHOP_PRODUCT_CHANGE_REQUIRED_KITS -> {
						if (e.isRightClick()) {
							shopProduct.getApplicableKits().clear();
							break;
						}
						EditorManager.startEdit(player, shopProduct, type2, input);
						EditorManager.tip(player, plugin.getMessage(Lang.Editor_Arena_Shop_Enter_Product_RequiredKit).getLocalized());
						EditorManager.suggestValues(player, plugin.getKitManager().getKitIds(), true);
						player.closeInventory();
						return;
					}
					case SHOP_PRODUCT_CHANGE_ITEMS -> {
						new ProductItems(shopProduct).open(player, 1);
						return;
					}
					case SHOP_PRODUCT_CHANGE_TRIGGERS_LOCKED, SHOP_PRODUCT_CHANGE_TRIGGERS_UNLOCKED -> {
						ArenaEditorUtils.handleTriggersClick(player, shopProduct, type2, e.isRightClick());
						if (e.isRightClick()) break;
						return;
					}
					default -> {
						return;
					}
				}
				shopProduct.getShopCategory().getShopManager().save();
				this.open(player, 1);
			}
		};
		
		this.loadItems(click);
	}

	@Override
	public void setTypes(@NotNull Map<EditorButtonType, Integer> map) {
		map.put(ArenaEditorType.SHOP_PRODUCT_CHANGE_NAME, 2);
		map.put(ArenaEditorType.SHOP_PRODUCT_CHANGE_DESCRIPTION, 6);
		map.put(ArenaEditorType.SHOP_PRODUCT_CHANGE_ICON, 4);
		map.put(ArenaEditorType.SHOP_PRODUCT_CHANGE_PRICE, 11);
		map.put(ArenaEditorType.SHOP_PRODUCT_CHANGE_CURRENCY, 15);
		map.put(ArenaEditorType.SHOP_PRODUCT_CHANGE_TRIGGERS_LOCKED, 12);
		map.put(ArenaEditorType.SHOP_PRODUCT_CHANGE_TRIGGERS_UNLOCKED, 14);
		map.put(ArenaEditorType.SHOP_PRODUCT_CHANGE_REQUIRED_KITS, 13);
		map.put(ArenaEditorType.SHOP_PRODUCT_CHANGE_COMMANDS, 21);
		map.put(ArenaEditorType.SHOP_PRODUCT_CHANGE_ITEMS, 23);
		map.put(MenuItemType.RETURN, 40);
	}

	@Override
	public void onItemPrepare(@NotNull Player player, @NotNull IMenuItem menuItem, @NotNull ItemStack item) {
		super.onItemPrepare(player, menuItem, item);
		ItemUtil.replace(item, this.object.replacePlaceholders());
	}

	@Override
	public boolean cancelClick(@NotNull InventoryClickEvent e, @NotNull SlotType slotType) {
		return slotType != SlotType.PLAYER && slotType != SlotType.EMPTY_PLAYER;
	}

	static class ProductItems extends AbstractMenu<AMA> {

		private final ArenaShopProduct shopProduct;

		public ProductItems(@NotNull ArenaShopProduct shopProduct) {
			super(shopProduct.getShopCategory().getShopManager().plugin(), "Product Items", 27);
			this.shopProduct = shopProduct;
		}

		@Override
		public void onPrepare(@NotNull Player player, @NotNull Inventory inventory) {
			inventory.setContents(this.shopProduct.getItems().toArray(new ItemStack[this.getSize()]));
		}

		@Override
		public void onReady(@NotNull Player player, @NotNull Inventory inventory) {

		}

		@Override
		public void onClose(@NotNull Player player, @NotNull InventoryCloseEvent e) {
			Inventory inventory = e.getInventory();
			this.shopProduct.setItems(Stream.of(inventory.getContents()).filter(Objects::nonNull).toList());
			this.shopProduct.getShopCategory().getShopManager().save();
			super.onClose(player, e);

			this.plugin.runTask(c -> this.shopProduct.getEditor().open(player, 1), false);
		}

		@Override
		public boolean destroyWhenNoViewers() {
			return true;
		}

		@Override
		public boolean cancelClick(@NotNull InventoryClickEvent e, @NotNull SlotType slotType) {
			return false;
		}
	}
}
