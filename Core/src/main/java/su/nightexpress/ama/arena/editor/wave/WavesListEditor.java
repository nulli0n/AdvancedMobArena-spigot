package su.nightexpress.ama.arena.editor.wave;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.editor.EditorButtonType;
import su.nexmedia.engine.api.editor.EditorInput;
import su.nexmedia.engine.api.menu.MenuClick;
import su.nexmedia.engine.api.menu.MenuItemType;
import su.nexmedia.engine.editor.AbstractEditorMenuAuto;
import su.nexmedia.engine.editor.EditorManager;
import su.nexmedia.engine.utils.ItemUtil;
import su.nightexpress.ama.AMA;
import su.nightexpress.ama.arena.wave.ArenaWave;
import su.nightexpress.ama.arena.wave.ArenaWaveManager;
import su.nightexpress.ama.config.Lang;
import su.nightexpress.ama.editor.ArenaEditorHub;
import su.nightexpress.ama.editor.ArenaEditorType;

import java.util.*;
import java.util.stream.IntStream;

public class WavesListEditor extends AbstractEditorMenuAuto<AMA, ArenaWaveManager, ArenaWave> {

    public WavesListEditor(@NotNull ArenaWaveManager waveManager) {
        super(waveManager.plugin(), waveManager, ArenaEditorHub.TITLE_WAVE_EDITOR, 45);

        EditorInput<ArenaWaveManager, ArenaEditorType> input = (player, waves, type, e) -> {
            String msg = e.getMessage();
            if (type == ArenaEditorType.WAVES_WAVE_CREATE) {
                String id = EditorManager.fineId(msg);
                if (waves.getWave(id) != null) {
                    EditorManager.error(player, plugin.getMessage(Lang.EDITOR_ARENA_WAVES_ERROR_WAVE_EXISTS).getLocalized());
                    return false;
                }

                ArenaWave wave = new ArenaWave(waves.getArenaConfig(), id, new HashSet<>());
                waves.getWaves().put(id, wave);
            }

            waves.save();
            return true;
        };

        MenuClick click = (player, type, e) -> {
            if (type instanceof MenuItemType type2) {
                if (type2 == MenuItemType.RETURN) {
                    waveManager.getEditor().open(player, 1);
                }
                else super.onItemClickDefault(player, type2);
            }
            else if (type instanceof ArenaEditorType type2) {
                if (type2 == ArenaEditorType.WAVES_WAVE_CREATE) {
                    EditorManager.startEdit(player, waveManager, type2, input);
                    EditorManager.tip(player, plugin.getMessage(Lang.EDITOR_ARENA_WAVES_ENTER_WAVE_ID).getLocalized());
                    player.closeInventory();
                }
            }
        };

        this.loadItems(click);
    }

    @Override
    public void setTypes(@NotNull Map<EditorButtonType, Integer> map) {
        map.put(ArenaEditorType.WAVES_WAVE_CREATE, 41);
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
    protected List<ArenaWave> getObjects(@NotNull Player player) {
        return new ArrayList<>(this.parent.getWaves().values());
    }

    @Override
    @NotNull
    protected ItemStack getObjectStack(@NotNull Player player, @NotNull ArenaWave wave) {
        ItemStack item = ArenaEditorType.WAVES_WAVE_OBJECT.getItem();
        ItemUtil.replace(item, wave.replacePlaceholders());
        return item;
    }

    @Override
    @NotNull
    protected MenuClick getObjectClick(@NotNull Player player, @NotNull ArenaWave wave) {
        return (p2, type, e) -> {
            if (e.isShiftClick() && e.isRightClick()) {
                wave.clear();
                this.parent.getWaves().remove(wave.getId());
                this.parent.save();
                this.open(p2, 1);
                return;
            }
            wave.getEditor().open(p2, 1);
        };
    }

    @Override
    public boolean cancelClick(@NotNull InventoryClickEvent e, @NotNull SlotType slotType) {
        return true;
    }
}
