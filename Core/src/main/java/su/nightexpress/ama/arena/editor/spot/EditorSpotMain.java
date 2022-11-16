package su.nightexpress.ama.arena.editor.spot;

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
import su.nightexpress.ama.arena.spot.ArenaSpot;
import su.nightexpress.ama.config.Lang;
import su.nightexpress.ama.editor.ArenaEditorType;
import su.nightexpress.ama.editor.ArenaEditorUtils;

import java.util.Map;

public class EditorSpotMain extends AbstractEditorMenu<AMA, ArenaSpot> {

	private EditorSpotStateList editorSpotStateList;
	
	public EditorSpotMain(@NotNull ArenaSpot spot) {
		super(spot.plugin(), spot, ArenaEditorUtils.TITLE_SPOT_EDITOR, 27);

		EditorInput<ArenaSpot, ArenaEditorType> input = (player, spot2, type, e) -> {
			String msg = StringUtil.color(e.getMessage());
			if (type == ArenaEditorType.SPOT_CHANGE_NAME) {
				spot2.setName(msg);
			}

			spot2.save();
			return true;
		};

		IMenuClick click = (player, type, e) -> {
			if (type instanceof MenuItemType type2) {
				if (type2 == MenuItemType.RETURN) {
					spot.getArenaConfig().getSpotManager().getEditor().open(player, 1);
				}
			}
			else if (type instanceof ArenaEditorType type2) {
				switch (type2) {
					case SPOT_CHANGE_ACTIVE -> {
						spot.setActive(!spot.isActive());
						spot.save();
						this.open(player, 1);
					}
					case SPOT_CHANGE_NAME -> {
						EditorManager.startEdit(player, spot, type2, input);
						EditorManager.tip(player, plugin.getMessage(Lang.Editor_Spot_Enter_Name).getLocalized());
						player.closeInventory();
					}
					case SPOT_SETUP_KIT -> {
						player.closeInventory();
						plugin.getArenaSetupManager().getSpotSetupManager().startSetup(player, spot);
					}
					case SPOT_CHANGE_STATES -> {
						if (spot.getCuboid().isEmpty()) {
							plugin.getMessage(Lang.Editor_Spot_State_Error_NoCuboid).send(player);
							return;
						}
						this.openStates(player);
					}
					default -> {}
				}
			}
		};

		this.loadItems(click);
	}

	@Override
	public void setTypes(@NotNull Map<EditorButtonType, Integer> map) {
		map.put(ArenaEditorType.SPOT_CHANGE_ACTIVE, 4);
		map.put(ArenaEditorType.SPOT_CHANGE_NAME, 12);
		map.put(ArenaEditorType.SPOT_CHANGE_STATES, 13);
		map.put(ArenaEditorType.SPOT_SETUP_KIT, 14);
		map.put(MenuItemType.RETURN, 22);
	}

	@Override
	public void clear() {
		if (this.editorSpotStateList != null) {
			this.editorSpotStateList.clear();
			this.editorSpotStateList = null;
		}
		super.clear();
	}

	public void openStates(@NotNull Player player) {
		if (this.editorSpotStateList == null) {
			this.editorSpotStateList = new EditorSpotStateList(this.object);
		}
		this.editorSpotStateList.open(player, 1);
	}

	@Override
	public void onItemPrepare(@NotNull Player player, @NotNull IMenuItem menuItem, @NotNull ItemStack item) {
		super.onItemPrepare(player, menuItem, item);
		if (menuItem.getType() instanceof ArenaEditorType type2) {
			if (type2 == ArenaEditorType.SPOT_CHANGE_ACTIVE) {
				item.setType(object.isActive() ? (!object.hasProblems() ? Material.LIME_DYE : Material.PINK_DYE) : Material.GRAY_DYE);
			}
		}
		ItemUtil.replace(item, this.object.replacePlaceholders());
	}

	@Override
	public boolean cancelClick(@NotNull InventoryClickEvent e, @NotNull SlotType slotType) {
		return true;
	}
}
