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
import su.nightexpress.ama.arena.region.ArenaRegionWave;
import su.nightexpress.ama.config.Lang;
import su.nightexpress.ama.editor.ArenaEditorType;
import su.nightexpress.ama.editor.ArenaEditorUtils;

import java.util.*;
import java.util.stream.IntStream;

public class EditorRegionWaveList extends AbstractEditorMenuAuto<AMA, ArenaRegion, ArenaRegionWave> {
	
	public EditorRegionWaveList(@NotNull ArenaRegion region) {
		super(region.plugin(), region, ArenaEditorUtils.TITLE_REGION_EDITOR, 45);

		EditorInput<ArenaRegion, ArenaEditorType> input = (player, region2, type, e) -> {
			String msg = StringUtil.color(e.getMessage());
			if (type == ArenaEditorType.REGION_WAVE_CREATE) {
				String id = EditorManager.fineId(msg);
				boolean has = region2.getWaves().stream().anyMatch(wave -> wave.getId().equalsIgnoreCase(id));
				if (has) {
					EditorManager.error(player, plugin.getMessage(Lang.Editor_Region_Wave_Error_Create).getLocalized());
					return false;
				}

				ArenaRegionWave wave = new ArenaRegionWave(region2, id, new HashSet<>(Collections.singletonList(id)), new HashSet<>(), new HashSet<>());
				region2.getWaves().add(wave);
			}
			region2.save();
			return true;
		};

		IMenuClick click = (player, type, e) -> {
			if (type instanceof MenuItemType type2) {
				if (type2 == MenuItemType.RETURN) {
					region.getEditor().open(player, 1);
				}
				else super.onItemClickDefault(player, type2);
			}
			else if (type instanceof ArenaEditorType type2) {
				if (type2 == ArenaEditorType.REGION_WAVE_CREATE) {
					EditorManager.startEdit(player, region, type2, input);
					EditorManager.tip(player, plugin.getMessage(Lang.Editor_Arena_Waves_Enter_Wave_Create).getLocalized());
					EditorManager.suggestValues(player, region.getArenaConfig().getWaveManager().getWaves().keySet(), true);
					player.closeInventory();
				}
			}
		};
		
		this.loadItems(click);
	}

	@Override
	public void setTypes(@NotNull Map<EditorButtonType, Integer> map) {
		map.put(ArenaEditorType.REGION_WAVE_CREATE, 41);
		map.put(MenuItemType.PAGE_NEXT, 44);
		map.put(MenuItemType.PAGE_PREVIOUS, 36);
		map.put(MenuItemType.RETURN, 39);
	}

	@Override
	public boolean cancelClick(@NotNull InventoryClickEvent e, @NotNull SlotType slotType) {
		return true;
	}

	@Override
	public int[] getObjectSlots() {
		return IntStream.range(0, 36).toArray();
	}

	@Override
	@NotNull
	protected List<ArenaRegionWave> getObjects(@NotNull Player player) {
		return new ArrayList<>(this.parent.getWaves());
	}

	@Override
	@NotNull
	protected ItemStack getObjectStack(@NotNull Player player, @NotNull ArenaRegionWave wave) {
		ItemStack item = ArenaEditorType.REGION_WAVE_OBJECT.getItem();
		ItemUtil.replace(item, wave.replacePlaceholders());
		return item;
	}

	@Override
	@NotNull
	protected IMenuClick getObjectClick(@NotNull Player player, @NotNull ArenaRegionWave wave) {
		return (p2, type, e) -> {
			if (e.isShiftClick() && e.isRightClick()) {
				wave.clear();
				this.parent.getWaves().remove(wave);
				this.parent.save();
				this.open(p2, 1);
				return;
			}
			wave.getEditor().open(p2, 1);
		};
	}
}
