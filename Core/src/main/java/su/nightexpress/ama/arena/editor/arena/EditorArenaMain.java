package su.nightexpress.ama.arena.editor.arena;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.editor.EditorButtonType;
import su.nexmedia.engine.api.editor.EditorInput;
import su.nexmedia.engine.api.menu.IMenuClick;
import su.nexmedia.engine.api.menu.IMenuItem;
import su.nexmedia.engine.api.menu.MenuItemType;
import su.nexmedia.engine.editor.AbstractEditorMenu;
import su.nexmedia.engine.editor.EditorManager;
import su.nexmedia.engine.utils.ItemUtil;
import su.nexmedia.engine.utils.StringUtil;
import su.nightexpress.ama.AMA;
import su.nightexpress.ama.api.currency.ICurrency;
import su.nightexpress.ama.arena.config.ArenaConfig;
import su.nightexpress.ama.config.Lang;
import su.nightexpress.ama.editor.ArenaEditorType;
import su.nightexpress.ama.editor.ArenaEditorUtils;

import java.util.Map;
import java.util.function.UnaryOperator;

public class EditorArenaMain extends AbstractEditorMenu<AMA, ArenaConfig> {

	public EditorArenaMain(@NotNull AMA plugin, @NotNull ArenaConfig config) {
		super(plugin, config, ArenaEditorUtils.TITLE_ARENA_EDITOR, 45);

		EditorInput<ArenaConfig, ArenaEditorType> input = (player, arenaConfig, type, e) -> {
			String msg = e.getMessage();
			switch (type) {
				case ARENA_CHANGE_NAME -> arenaConfig.setName(msg);
				case ARENA_CHANGE_REQUIREMENT_PAYMENT -> {
					String[] split = StringUtil.colorOff(msg).split(" ");
					if (split.length < 2) return false;

					ICurrency currency = plugin.getCurrencyManager().getCurrency(split[0]);
					if (currency == null) {
						EditorManager.error(player, plugin.getMessage(Lang.ERROR_CURRENCY_INVALID).getLocalized());
						return false;
					}

					double amount = StringUtil.getDouble(split[1], -1D);
					if (amount < 0D) {
						EditorManager.error(player, EditorManager.ERROR_NUM_INVALID);
						return false;
					}

					arenaConfig.getJoinPaymentRequirements().put(currency, amount);
				}
			}
			arenaConfig.save();
			return true;
		};

		IMenuClick click = (player, type, e) -> {
			if (type instanceof MenuItemType type2) {
				if (type2 == MenuItemType.RETURN) {
					plugin.getEditor().getArenaEditor().open(player, 1);
				}
			}
			else if (type instanceof ArenaEditorType type2) {
				switch (type2) {
					case ARENA_CHANGE_ACTIVE -> {
						config.setActive(!config.isActive());
						config.save();
						this.open(player, this.getPage(player));
					}
					case ARENA_CHANGE_NAME -> {
						EditorManager.startEdit(player, config, type2, input);
						EditorManager.tip(player, plugin.getMessage(Lang.EDITOR_ARENA_ENTER_NAME).getLocalized());
						player.closeInventory();
					}
					case ARENA_CHANGE_REQUIREMENT_PERMISSION -> {
						config.setPermissionRequired(!config.isPermissionRequired());
						config.save();
						this.open(player, this.getPage(player));
					}
					case ARENA_CHANGE_REQUIREMENT_PAYMENT -> {
						if (e.isRightClick()) {
							config.getJoinPaymentRequirements().clear();
							config.save();
							this.open(player, this.getPage(player));
							break;
						}
						EditorManager.startEdit(player, config, type2, input);
						EditorManager.suggestValues(player, plugin.getCurrencyManager().getCurrencyIds(), false);
						EditorManager.tip(player, plugin.getMessage(Lang.EDITOR_ARENA_ENTER_JOIN_PAYMENT).getLocalized());
						player.closeInventory();
					}
					case ARENA_SETUP_KIT -> {
						plugin.getArenaSetupManager().getConfigSetupManager().startSetup(player, config);
						player.closeInventory();
					}
					case ARENA_OPEN_REGION_MANAGER -> config.getRegionManager().getEditor().open(player, 1);
					case ARENA_OPEN_SPOT_MANAGER -> config.getSpotManager().getEditor().open(player, 1);
					case ARENA_OPEN_GAMEPLAY_MANAGER -> config.getGameplayManager().getEditor().open(player, 1);
					case ARENA_OPEN_WAVE_MANAGER -> config.getWaveManager().getEditor().open(player, 1);
					case ARENA_OPEN_SHOP_MANAGER -> config.getShopManager().getEditor().open(player, 1);
					case ARENA_OPEN_REWARD_MANAGER -> config.getRewardManager().getEditor().open(player, 1);
					default -> {}
				}
			}
		};
		
		this.loadItems(click);
	}

	@Override
	public void setTypes(@NotNull Map<EditorButtonType, Integer> map) {
		map.put(ArenaEditorType.ARENA_CHANGE_ACTIVE, 4);
		map.put(ArenaEditorType.ARENA_CHANGE_NAME, 6);
		map.put(ArenaEditorType.ARENA_SETUP_KIT, 13);
		map.put(ArenaEditorType.ARENA_CHANGE_REQUIREMENT_PERMISSION, 10);
		map.put(ArenaEditorType.ARENA_CHANGE_REQUIREMENT_PAYMENT, 16);
		map.put(ArenaEditorType.ARENA_OPEN_GAMEPLAY_MANAGER, 21);
		map.put(ArenaEditorType.ARENA_OPEN_REGION_MANAGER, 22);
		map.put(ArenaEditorType.ARENA_OPEN_WAVE_MANAGER, 23);
		map.put(ArenaEditorType.ARENA_OPEN_REWARD_MANAGER, 30);
		map.put(ArenaEditorType.ARENA_OPEN_SHOP_MANAGER, 31);
		map.put(ArenaEditorType.ARENA_OPEN_SPOT_MANAGER, 32);
		map.put(MenuItemType.RETURN, 40);
	}

	@Override
	public boolean cancelClick(@NotNull InventoryClickEvent e, @NotNull SlotType slotType) {
		return true;
	}

	@Override
	public void onItemPrepare(@NotNull Player player, @NotNull IMenuItem menuItem, @NotNull ItemStack item) {
		super.onItemPrepare(player, menuItem, item);

		Enum<?> type = menuItem.getType();
		if (!(type instanceof ArenaEditorType type2)) return;

		UnaryOperator<String> replacer;
		if (type2 == ArenaEditorType.ARENA_OPEN_GAMEPLAY_MANAGER) {
			replacer = this.object.getGameplayManager().replacePlaceholders();
		}
		else if (type2 == ArenaEditorType.ARENA_OPEN_REGION_MANAGER) {
			replacer = this.object.getRegionManager().replacePlaceholders();
		}
		else if (type2 == ArenaEditorType.ARENA_OPEN_SHOP_MANAGER) {
			replacer = this.object.getShopManager().replacePlaceholders();
		}
		else if (type2 == ArenaEditorType.ARENA_OPEN_SPOT_MANAGER) {
			replacer = this.object.getSpotManager().replacePlaceholders();
		}
		else if (type2 == ArenaEditorType.ARENA_OPEN_WAVE_MANAGER) {
			replacer = this.object.getWaveManager().replacePlaceholders();
		}
		else if (type2 == ArenaEditorType.ARENA_OPEN_REWARD_MANAGER) {
			replacer = this.object.getRewardManager().replacePlaceholders();
		}
		else {
			replacer = this.object.replacePlaceholders();
			if (type2 == ArenaEditorType.ARENA_CHANGE_ACTIVE) {
				item.setType(object.isActive() ? (!object.hasProblems() ? Material.LIME_DYE : Material.PINK_DYE) : Material.GRAY_DYE);
			}
		}

		ItemUtil.replace(item, replacer);
	}
}
