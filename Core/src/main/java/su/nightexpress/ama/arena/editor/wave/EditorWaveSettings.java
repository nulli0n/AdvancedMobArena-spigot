package su.nightexpress.ama.arena.editor.wave;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.editor.EditorButtonType;
import su.nexmedia.engine.api.menu.IMenuClick;
import su.nexmedia.engine.api.menu.IMenuItem;
import su.nexmedia.engine.api.menu.MenuItemType;
import su.nexmedia.engine.editor.AbstractEditorMenu;
import su.nexmedia.engine.utils.ItemUtil;
import su.nightexpress.ama.AMA;
import su.nightexpress.ama.arena.wave.ArenaWave;
import su.nightexpress.ama.editor.ArenaEditorType;
import su.nightexpress.ama.editor.ArenaEditorUtils;

import java.util.Map;

public class EditorWaveSettings extends AbstractEditorMenu<AMA, ArenaWave> {

    private EditorWaveMobList          editorMobs;
    private EditorWaveAmplificatorList editorAmplificators;

    public EditorWaveSettings(@NotNull ArenaWave arenaWave) {
        super(arenaWave.getArena().plugin(), arenaWave, ArenaEditorUtils.TITLE_WAVE_EDITOR, 36);

        IMenuClick click = (player, type, e) -> {
            if (type instanceof MenuItemType type2) {
                if (type2 == MenuItemType.RETURN) {
                    arenaWave.getArenaConfig().getWaveManager().getEditor().getEditorWaveList().open(player, 1);
                }
            }
            else if (type instanceof ArenaEditorType type2) {
                switch (type2) {
                    case WAVES_WAVE_CHANGE_MOBS -> this.getEditorMobs().open(player, 1);
                    case WAVES_WAVE_CHANGE_AMPLIFICATORS -> this.getEditorAmplificators().open(player, 1);
                    default -> {}
                }
            }
        };

        this.loadItems(click);
    }

    @Override
    public void clear() {
        if (this.editorMobs != null) {
            this.editorMobs.clear();
            this.editorMobs = null;
        }
        if (this.editorAmplificators != null) {
            this.editorAmplificators.clear();
            this.editorAmplificators = null;
        }
        super.clear();
    }

    @NotNull
    public EditorWaveMobList getEditorMobs() {
        if (this.editorMobs == null) {
            this.editorMobs = new EditorWaveMobList(this.object);
        }
        return editorMobs;
    }

    @NotNull
    public EditorWaveAmplificatorList getEditorAmplificators() {
        if (this.editorAmplificators == null) {
            this.editorAmplificators = new EditorWaveAmplificatorList(this.object);
        }
        return this.editorAmplificators;
    }

    @Override
    public void setTypes(@NotNull Map<EditorButtonType, Integer> map) {
        map.put(ArenaEditorType.WAVES_WAVE_CHANGE_MOBS, 12);
        map.put(ArenaEditorType.WAVES_WAVE_CHANGE_AMPLIFICATORS, 14);
        map.put(MenuItemType.RETURN, 31);
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
