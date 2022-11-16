package su.nightexpress.ama.arena.editor.wave;

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
import su.nightexpress.ama.arena.config.ArenaConfig;
import su.nightexpress.ama.arena.wave.ArenaWave;
import su.nightexpress.ama.arena.wave.ArenaWaveAmplificator;
import su.nightexpress.ama.config.Lang;
import su.nightexpress.ama.editor.ArenaEditorType;
import su.nightexpress.ama.editor.ArenaEditorUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

public class EditorWaveAmplificatorList extends AbstractEditorMenuAuto<AMA, ArenaWave, ArenaWaveAmplificator> {

    EditorWaveAmplificatorList(@NotNull ArenaWave arenaWave) {
        super(arenaWave.getArena().plugin(), arenaWave, ArenaEditorUtils.TITLE_WAVE_EDITOR, 45);

        EditorInput<ArenaWave, ArenaEditorType> input = (player, wave, type, e) -> {
            String msg = StringUtil.colorOff(e.getMessage());
            if (type == ArenaEditorType.WAVES_WAVE_AMPLIFICATOR_CREATE) {
                String id = EditorManager.fineId(msg);
                if (wave.getAmplificators().get(id) != null) {
                    EditorManager.error(player, plugin.getMessage(Lang.Editor_Arena_Waves_Error_Amplificator_Exist).getLocalized());
                    return false;
                }

                ArenaWaveAmplificator amplificator = new ArenaWaveAmplificator(wave, id, new HashSet<>(), 0, 0);
                wave.getAmplificators().put(amplificator.getId(), amplificator);
            }

            wave.getArenaConfig().getWaveManager().save();
            return true;
        };

        IMenuClick click = (player, type, e) -> {
            ArenaConfig config = arenaWave.getArenaConfig();
            if (type instanceof MenuItemType type2) {
                if (type2 == MenuItemType.RETURN) {
                    this.parent.getEditor().open(player, 1);
                }
                else super.onItemClickDefault(player, type2);
            }
            else if (type instanceof ArenaEditorType type2) {
                if (type2 == ArenaEditorType.WAVES_WAVE_AMPLIFICATOR_CREATE) {
                    EditorManager.startEdit(player, arenaWave, type2, input);
                    EditorManager.tip(player, plugin.getMessage(Lang.Editor_Arena_Waves_Enter_Amplificator_Create).getLocalized());
                    player.closeInventory();
                }
            }
        };

        this.loadItems(click);
    }

    @Override
    public void setTypes(@NotNull Map<EditorButtonType, Integer> map) {
        map.put(ArenaEditorType.WAVES_WAVE_AMPLIFICATOR_CREATE, 41);
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
    protected List<ArenaWaveAmplificator> getObjects(@NotNull Player player) {
        return new ArrayList<>(this.parent.getAmplificators().values());
    }

    @Override
    @NotNull
    protected ItemStack getObjectStack(@NotNull Player player, @NotNull ArenaWaveAmplificator amplificator) {
        ItemStack item = ArenaEditorType.WAVES_WAVE_AMPLIFICATOR_OBJECT.getItem();
        ItemUtil.replace(item, amplificator.replacePlaceholders());
        return item;
    }

    @Override
    @NotNull
    protected IMenuClick getObjectClick(@NotNull Player player, @NotNull ArenaWaveAmplificator amplificator) {
        return (p2, type, e) -> {
            if (e.isShiftClick() && e.isRightClick()) {
                amplificator.clear();
                this.parent.getAmplificators().remove(amplificator.getId());
                this.parent.getArenaConfig().getWaveManager().save();
                this.open(p2, 1);
                return;
            }
            amplificator.getEditor().open(p2, 1);
        };
    }

    @Override
    public boolean cancelClick(@NotNull InventoryClickEvent e, @NotNull SlotType slotType) {
        return true;
    }
}
