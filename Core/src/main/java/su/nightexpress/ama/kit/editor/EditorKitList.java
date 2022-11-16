package su.nightexpress.ama.kit.editor;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.editor.EditorButtonType;
import su.nexmedia.engine.api.editor.EditorInput;
import su.nexmedia.engine.api.menu.IMenuClick;
import su.nexmedia.engine.api.menu.MenuItemType;
import su.nexmedia.engine.editor.AbstractEditorMenuAuto;
import su.nexmedia.engine.editor.EditorManager;
import su.nexmedia.engine.utils.ItemUtil;
import su.nexmedia.engine.utils.StringUtil;
import su.nightexpress.ama.AMA;
import su.nightexpress.ama.config.Lang;
import su.nightexpress.ama.editor.ArenaEditorType;
import su.nightexpress.ama.editor.ArenaEditorUtils;
import su.nightexpress.ama.kit.Kit;
import su.nightexpress.ama.kit.KitManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

public class EditorKitList extends AbstractEditorMenuAuto<AMA, KitManager, Kit> {
	
	public EditorKitList(@NotNull KitManager kitManager) {
		super(kitManager.plugin(), kitManager, ArenaEditorUtils.TITLE_KIT_EDITOR, 45);

		EditorInput<KitManager, ArenaEditorType> input = (player, kitManager2, type, e) -> {
			String msg = StringUtil.colorOff(e.getMessage());
			if (type == ArenaEditorType.KIT_CREATE) {
				String id = EditorManager.fineId(msg);
				if (kitManager2.getKitById(id) != null) {
					EditorManager.error(player, plugin.getMessage(Lang.Editor_Kit_Error_Exist).getLocalized());
					return false;
				}

				Kit kit = new Kit(plugin, plugin.getDataFolder() + "/kits/kits/" + id + ".yml");
				kit.save();
				kitManager2.getKitsMap().put(kit.getId(), kit);
				return true;
			}
			return true;
		};

		IMenuClick click = (player, type, e) -> {
			if (type instanceof MenuItemType type2) {
				if (type2 == MenuItemType.RETURN) {
					plugin.getEditor().open(player, 1);
				}
				else this.onItemClickDefault(player, type2);
			}
			else if (type instanceof ArenaEditorType type2) {
				if (type2 == ArenaEditorType.KIT_CREATE) {
					EditorManager.startEdit(player, kitManager, type2, input);
					EditorManager.tip(player, plugin.getMessage(Lang.Editor_Kit_Enter_Create).getLocalized());
					player.closeInventory();
				}
			}
		};
		
		this.loadItems(click);
	}

	@Override
	public void setTypes(@NotNull Map<EditorButtonType, Integer> map) {
		map.put(ArenaEditorType.KIT_CREATE, 41);
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
	protected IMenuClick getObjectClick(@NotNull Player player, @NotNull Kit kit) {
		return (player1, type, e) -> {
			if (e.isShiftClick() && e.isRightClick()) {
				if (!kit.getFile().delete()) return;
				kit.clear();
				this.parent.getKitsMap().remove(kit.getId());
				this.open(player1, this.getPage(player1));
				return;
			}
			kit.getEditor().open(player1, 1);
		};
	}

	@Override
	@NotNull
	protected ItemStack getObjectStack(@NotNull Player player, @NotNull Kit kit) {
		ItemStack item = ArenaEditorType.KIT_OBJECT.getItem();
		item.setType(kit.getIcon().getType());
		ItemUtil.replace(item, kit.replacePlaceholders());
		return item;
	}

	@Override
	@NotNull
	protected List<Kit> getObjects(@NotNull Player player) {
		return new ArrayList<>(this.parent.getKits());
	}

	@Override
	public boolean cancelClick(@NotNull InventoryClickEvent e, @NotNull SlotType slotType) {
		return true;
	}
}
