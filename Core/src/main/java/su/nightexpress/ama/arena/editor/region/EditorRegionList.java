package su.nightexpress.ama.arena.editor.region;

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
import su.nightexpress.ama.arena.region.ArenaRegion;
import su.nightexpress.ama.arena.region.ArenaRegionManager;
import su.nightexpress.ama.config.Lang;
import su.nightexpress.ama.editor.ArenaEditorType;
import su.nightexpress.ama.editor.ArenaEditorUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

public class EditorRegionList extends AbstractEditorMenuAuto<AMA, ArenaRegionManager, ArenaRegion> {
	
	public EditorRegionList(@NotNull ArenaRegionManager regionManager) {
		super(regionManager.plugin(), regionManager, ArenaEditorUtils.TITLE_REGION_EDITOR, 45);

		EditorInput<ArenaRegionManager, ArenaEditorType> input = (player, regionManager2, type, e) -> {
			String msg = StringUtil.color(e.getMessage());
			if (type == ArenaEditorType.REGION_CREATE) {
				String id = EditorManager.fineId(msg);
				if (regionManager2.getRegion(id) != null) {
					EditorManager.error(player, plugin.getMessage(Lang.Editor_Region_Error_Create).getLocalized());
					return false;
				}

				String path = regionManager2.getArenaConfig().getFile().getParentFile().getAbsolutePath() + ArenaRegionManager.DIR_REGIONS + id + ".yml";
				ArenaRegion region = new ArenaRegion(regionManager2.getArenaConfig(), path);
				regionManager2.addRegion(region);
			}
			return true;
		};

		IMenuClick click = (player, type, e) -> {
			if (type instanceof MenuItemType type2) {
				if (type2 == MenuItemType.RETURN) {
					regionManager.getArenaConfig().getEditor().open(player, 1);
				}
				else super.onItemClickDefault(player, type2);
			}
			else if (type instanceof ArenaEditorType type2) {
				if (type2 == ArenaEditorType.REGION_CREATE) {
					EditorManager.startEdit(player, regionManager, type2, input);
					EditorManager.tip(player, plugin.getMessage(Lang.Editor_Region_Enter_Create).getLocalized());
					player.closeInventory();
				}
			}
		};
		
		this.loadItems(click);
	}

	@Override
	public void setTypes(@NotNull Map<EditorButtonType, Integer> map) {
		map.put(ArenaEditorType.REGION_CREATE, 41);
		map.put(MenuItemType.PAGE_NEXT, 44);
		map.put(MenuItemType.PAGE_PREVIOUS, 36);
		map.put(MenuItemType.RETURN, 39);
	}

	@Override
	public int[] getObjectSlots() {
		return IntStream.range(0, 36).toArray();
	}

	@Override
	@NotNull
	protected List<ArenaRegion> getObjects(@NotNull Player player) {
		return new ArrayList<>(this.parent.getRegions());
	}

	@Override
	@NotNull
	protected ItemStack getObjectStack(@NotNull Player player, @NotNull ArenaRegion region) {
		ItemStack icon = ArenaEditorType.REGION_OBJECT.getItem();
		ItemUtil.replace(icon, region.replacePlaceholders());
		return icon;
	}

	@Override
	@NotNull
	protected IMenuClick getObjectClick(@NotNull Player player, @NotNull ArenaRegion region) {
		return (p, type, e) -> {
			if (e.isShiftClick() && e.isRightClick()) {
				if (this.parent.removeRegion(region)) {
					this.open(p, 1);
				}
				return;
			}
			region.getEditor().open(p, 1);
		};
	}

	@Override
	public boolean cancelClick(@NotNull InventoryClickEvent e, @NotNull SlotType slotType) {
		return true;
	}
}
