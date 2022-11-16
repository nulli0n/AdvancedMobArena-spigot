package su.nightexpress.ama.arena.editor.wave;

import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.editor.EditorButtonType;
import su.nexmedia.engine.api.editor.EditorInput;
import su.nexmedia.engine.api.menu.IMenuClick;
import su.nexmedia.engine.api.menu.MenuItemType;
import su.nexmedia.engine.editor.AbstractEditorMenuAuto;
import su.nexmedia.engine.editor.EditorManager;
import su.nexmedia.engine.hooks.Hooks;
import su.nexmedia.engine.hooks.external.MythicMobsHook;
import su.nexmedia.engine.utils.ItemUtil;
import su.nexmedia.engine.utils.StringUtil;
import su.nightexpress.ama.AMA;
import su.nightexpress.ama.arena.wave.ArenaWave;
import su.nightexpress.ama.arena.wave.ArenaWaveMob;
import su.nightexpress.ama.config.Lang;
import su.nightexpress.ama.editor.ArenaEditorType;
import su.nightexpress.ama.editor.ArenaEditorUtils;
import su.nightexpress.ama.mob.config.MobConfig;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

public class EditorWaveMobList extends AbstractEditorMenuAuto<AMA, ArenaWave, ArenaWaveMob> {

	EditorWaveMobList(@NotNull ArenaWave arenaWave) {
		super(arenaWave.getArena().plugin(), arenaWave, ArenaEditorUtils.TITLE_WAVE_EDITOR, 45);

		EditorInput<ArenaWave, ArenaEditorType> input = (player, wave, type, e) -> {
			String msg = StringUtil.colorOff(e.getMessage());
			if (type == ArenaEditorType.WAVES_WAVE_MOB_CREATE) {
				if (wave.getMobs().get(msg) != null) {
					EditorManager.error(player, plugin.getMessage(Lang.Editor_Arena_Waves_Error_Mob_Exist).getLocalized());
					return false;
				}

				MobConfig customMob = plugin.getMobManager().getMobById(msg);
				boolean mmValid = Hooks.hasMythicMobs() && MythicMobsHook.getMobConfig(msg) != null;
				if (customMob == null && !mmValid) {
					EditorManager.error(player, plugin.getMessage(Lang.Editor_Arena_Waves_Error_Mob_Invalid).getLocalized());
					return false;
				}

				ArenaWaveMob waveMob = new ArenaWaveMob(wave, msg, 1, 1, 100D);
				wave.getMobs().put(waveMob.getMobId(), waveMob);
			}

			wave.getArenaConfig().getWaveManager().save();
			return true;
		};

		IMenuClick click = (player, type, e) -> {
			if (type instanceof MenuItemType type2) {
				if (type2 == MenuItemType.RETURN) {
					this.parent.getEditor().open(player, 1);
				}
				else super.onItemClickDefault(player, type2);
			}
			else if (type instanceof ArenaEditorType type2) {
				if (type2 == ArenaEditorType.WAVES_WAVE_MOB_CREATE) {
					EditorManager.startEdit(player, arenaWave, type2, input);
					EditorManager.tip(player, plugin.getMessage(Lang.Editor_Arena_Waves_Enter_Mob_Create).getLocalized());
					EditorManager.suggestValues(player, plugin.getMobManager().getSupportedMobIds(), true);
					player.closeInventory();
				}
			}
		};
		
		this.loadItems(click);
	}

	@Override
	public void setTypes(@NotNull Map<EditorButtonType, Integer> map) {
		map.put(ArenaEditorType.WAVES_WAVE_MOB_CREATE, 41);
		map.put(MenuItemType.RETURN, 39);
		map.put(MenuItemType.PAGE_NEXT, 44);
		map.put(MenuItemType.PAGE_PREVIOUS, 36);
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
	protected List<ArenaWaveMob> getObjects(@NotNull Player player) {
		return new ArrayList<>(this.parent.getMobs().values());
	}

	@Override
	@NotNull
	protected ItemStack getObjectStack(@NotNull Player player, @NotNull ArenaWaveMob waveMob) {
		Material material = Material.BAT_SPAWN_EGG;
		MobConfig customMob = plugin.getMobManager().getMobById(waveMob.getMobId());
		if (customMob != null) {
			material = Material.getMaterial(customMob.getEntityType().name() + "_SPAWN_EGG");
			if (customMob.getEntityType() == EntityType.MUSHROOM_COW) material = Material.MOOSHROOM_SPAWN_EGG;
			if (material == null) material = Material.BAT_SPAWN_EGG;
		}

		ItemStack item = ArenaEditorType.WAVES_WAVE_MOB_OBJECT.getItem();
		item.setType(material);
		ItemUtil.replace(item, waveMob.replacePlaceholders());
		return item;
	}

	@Override
	@NotNull
	protected IMenuClick getObjectClick(@NotNull Player player, @NotNull ArenaWaveMob waveMob) {
		EditorInput<ArenaWaveMob, ArenaEditorType> input = (player2, waveMob2, type, e) -> {
			String msg = StringUtil.color(e.getMessage());
			switch (type) {
				case WAVES_WAVE_MOB_CHANGE_AMOUNT -> {
					int value = StringUtil.getInteger(msg, -1);
					if (value < 0) {
						EditorManager.error(player2, EditorManager.ERROR_NUM_NOT_INT);
						return false;
					}
					waveMob2.setAmount(value);
				}
				case WAVES_WAVE_MOB_CHANGE_LEVEL -> {
					int value = StringUtil.getInteger(msg, -1);
					if (value < 0) {
						EditorManager.error(player2, EditorManager.ERROR_NUM_NOT_INT);
						return false;
					}
					waveMob2.setLevel(value);
				}
				case WAVES_WAVE_MOB_CHANGE_CHANCE -> {
					double value = StringUtil.getDouble(msg, -1);
					if (value < 0) {
						EditorManager.error(player2, EditorManager.ERROR_NUM_INVALID);
						return false;
					}
					waveMob2.setChance(value);
				}
				default -> {}
			}

			waveMob2.getArenaWave().getArenaConfig().getWaveManager().save();
			return true;
		};

		return (p2, type, e) -> {
			if (e.isShiftClick() && e.isRightClick()) {
				this.parent.getMobs().remove(waveMob.getMobId());
				this.parent.getArenaConfig().getWaveManager().save();
				this.open(p2, 1);
				return;
			}

			if (e.isLeftClick()) {
				EditorManager.startEdit(p2, waveMob, ArenaEditorType.WAVES_WAVE_MOB_CHANGE_AMOUNT, input);
				EditorManager.tip(p2, plugin.getMessage(Lang.Editor_Arena_Waves_Enter_Mob_Amount).getLocalized());
			}
			else if (e.isRightClick()) {
				EditorManager.startEdit(p2, waveMob, ArenaEditorType.WAVES_WAVE_MOB_CHANGE_LEVEL, input);
				EditorManager.tip(p2, plugin.getMessage(Lang.Editor_Arena_Waves_Enter_Mob_Level).getLocalized());
			}
			else if (e.getClick() == ClickType.DROP) {
				EditorManager.startEdit(p2, waveMob, ArenaEditorType.WAVES_WAVE_MOB_CHANGE_CHANCE, input);
				EditorManager.tip(p2, plugin.getMessage(Lang.Editor_Arena_Waves_Enter_Mob_Chance).getLocalized());
			}
			else return;
			p2.closeInventory();
		};
	}
}
