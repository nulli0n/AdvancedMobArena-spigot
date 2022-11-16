package su.nightexpress.ama.arena.editor.region;

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
import su.nightexpress.ama.arena.region.ArenaRegion;
import su.nightexpress.ama.arena.region.ArenaRegionWave;
import su.nightexpress.ama.config.Lang;
import su.nightexpress.ama.editor.ArenaEditorType;
import su.nightexpress.ama.editor.ArenaEditorUtils;

import java.util.Map;

public class EditorRegionWaveSettings extends AbstractEditorMenu<AMA, ArenaRegionWave> {
	
	public EditorRegionWaveSettings(@NotNull ArenaRegionWave regionWave) {
		super(regionWave.plugin(), regionWave, ArenaEditorUtils.TITLE_REGION_EDITOR, 45);

		EditorInput<ArenaRegionWave, ArenaEditorType> input = (player, regionWave2, type, e) -> {
			String msg = StringUtil.color(e.getMessage());
			switch (type) {
				case REGION_WAVE_CHANGE_ID -> regionWave2.getArenaWaveIds().add(EditorManager.fineId(msg));
				case REGION_WAVE_CHANGE_SPAWNERS_ADD -> regionWave2.getSpawnerIds().add(EditorManager.fineId(msg));
				default -> {return true;}
			}

			regionWave2.getRegion().save();
			return true;
		};

		IMenuClick click = (player, type, e) -> {
			ArenaRegion region = this.object.getRegion();
			if (type instanceof MenuItemType type2) {
				if (type2 == MenuItemType.RETURN) {
					region.getEditor().getWaveList().open(player, 1);
				}
				return;
			}
			
			if (type instanceof ArenaEditorType type2) {
				switch (type2) {
					case REGION_WAVE_CHANGE_TRIGGERS -> {
						ArenaEditorUtils.handleTriggersClick(player, regionWave, type2, e.isRightClick());
						if (e.isRightClick()) break;
						return;
					}
					case REGION_WAVE_CHANGE_ID -> {
						if (e.isRightClick()) {
							regionWave.getArenaWaveIds().clear();
							break;
						}
						EditorManager.startEdit(player, this.object, type2, input);
						EditorManager.tip(player, plugin.getMessage(Lang.Editor_Region_Wave_Enter_Id).getLocalized());
						EditorManager.suggestValues(player, region.getArenaConfig().getWaveManager().getWaves().keySet(), true);
						player.closeInventory();
						return;
					}
					case REGION_WAVE_CHANGE_SPAWNERS -> {
						if (e.isRightClick()) {
							this.object.getSpawnerIds().clear();
							break;
						}

						EditorManager.startEdit(player, this.object, ArenaEditorType.REGION_WAVE_CHANGE_SPAWNERS_ADD, input);
						EditorManager.tip(player, plugin.getMessage(Lang.Editor_Region_Wave_Enter_SpawnerId).getLocalized());
						EditorManager.suggestValues(player, region.getMobSpawners().keySet(), true);
						player.closeInventory();
						return;
					}
					default -> {return;}
				}
				
				region.save();
				this.open(player, 1);
			}
		};
		
		this.loadItems(click);
	}

	@Override
	public void setTypes(@NotNull Map<EditorButtonType, Integer> map) {
		map.put(ArenaEditorType.REGION_WAVE_CHANGE_ID, 11);
		map.put(ArenaEditorType.REGION_WAVE_CHANGE_TRIGGERS, 13);
		map.put(ArenaEditorType.REGION_WAVE_CHANGE_SPAWNERS, 15);
		map.put(MenuItemType.RETURN, 40);
	}

	@Override
	public void onItemPrepare(@NotNull Player player, @NotNull IMenuItem menuItem, @NotNull ItemStack item) {
		super.onItemPrepare(player, menuItem, item);
		ItemUtil.replace(item, this.object.replacePlaceholders());
	}

	@Override
	public boolean cancelClick(@NotNull InventoryClickEvent e, @NotNull SlotType slotType) {
		return true;
	}
}
