package su.nightexpress.ama.arena.editor.wave;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.editor.EditorButtonType;
import su.nexmedia.engine.api.editor.EditorInput;
import su.nexmedia.engine.api.menu.MenuClick;
import su.nexmedia.engine.api.menu.MenuItem;
import su.nexmedia.engine.api.menu.MenuItemType;
import su.nexmedia.engine.editor.AbstractEditorMenu;
import su.nexmedia.engine.editor.EditorManager;
import su.nexmedia.engine.utils.ItemUtil;
import su.nexmedia.engine.utils.StringUtil;
import su.nightexpress.ama.AMA;
import su.nightexpress.ama.arena.wave.ArenaWaveManager;
import su.nightexpress.ama.config.Lang;
import su.nightexpress.ama.editor.ArenaEditorType;
import su.nightexpress.ama.editor.ArenaEditorUtils;

import java.util.Map;

public class EditorWaveManager extends AbstractEditorMenu<AMA, ArenaWaveManager> {

    private EditorWaveList    editorWaveList;
    private EditorWaveGradual editorGradual;

    public EditorWaveManager(@NotNull ArenaWaveManager waveManager) {
        super(waveManager.plugin(), waveManager, ArenaEditorUtils.TITLE_WAVE_EDITOR, 54);

        EditorInput<ArenaWaveManager, ArenaEditorType> input = (player, waves, type, e) -> {
            String msg = StringUtil.color(e.getMessage());
            switch (type) {
                case WAVES_CHANGE_DELAY_FIRST -> {
                    int delay = StringUtil.getInteger(msg, 5);
                    waves.setDelayFirst(delay);
                }
                case WAVES_CHANGE_DELAY_DEFAULT -> {
                    int delay = StringUtil.getInteger(msg, 5);
                    waves.setDelayDefault(delay);
                }
                case WAVES_CHANGE_FINAL_WAVE -> {
                    int wave = StringUtil.getInteger(msg, 25);
                    waves.setFinalWave(wave);
                }
                default -> {}
            }

            waves.save();
            return true;
        };

        MenuClick click = (player, type, e) -> {
            if (type instanceof MenuItemType type2) {
                if (type2 == MenuItemType.RETURN) {
                    this.object.getArenaConfig().getEditor().open(player, 1);
                }
            }
            else if (type instanceof ArenaEditorType type2) {
                switch (type2) {
                    case WAVES_CHANGE_DELAY -> {
                        if (e.isLeftClick()) {
                            EditorManager.startEdit(player, waveManager, ArenaEditorType.WAVES_CHANGE_DELAY_FIRST, input);
                            EditorManager.tip(player, plugin.getMessage(Lang.Editor_Arena_Waves_Enter_Delay_First).getLocalized());
                        }
                        else if (e.isRightClick()) {
                            EditorManager.startEdit(player, waveManager, ArenaEditorType.WAVES_CHANGE_DELAY_DEFAULT, input);
                            EditorManager.tip(player, plugin.getMessage(Lang.Editor_Arena_Waves_Enter_Delay_Default).getLocalized());
                        }
                        player.closeInventory();
                    }
                    case WAVES_CHANGE_FINAL_WAVE -> {
                        if (e.isRightClick()) {
                            waveManager.setFinalWave(-1);
                            this.object.save();
                            this.open(player, 1);
                            break;
                        }
                        EditorManager.startEdit(player, waveManager, type2, input);
                        EditorManager.tip(player, plugin.getMessage(Lang.Editor_Arena_Waves_Enter_FinalWave).getLocalized());
                        player.closeInventory();
                    }
                    case WAVES_CHANGE_GRADUAL -> this.getEditorGradual().open(player, 1);
                    case WAVES_CHANGE_WAVES -> this.getEditorWaveList().open(player, 1);
                    default -> {}
                }
            }
        };

        this.loadItems(click);
    }

    @Override
    public void clear() {
        if (this.editorWaveList != null) {
            this.editorWaveList.clear();
            this.editorWaveList = null;
        }
        if (this.editorGradual != null) {
            this.editorGradual.clear();
            this.editorGradual = null;
        }
        super.clear();
    }

    @Override
    public void setTypes(@NotNull Map<EditorButtonType, Integer> map) {
        map.put(ArenaEditorType.WAVES_CHANGE_DELAY, 29);
        map.put(ArenaEditorType.WAVES_CHANGE_FINAL_WAVE, 33);
        map.put(ArenaEditorType.WAVES_CHANGE_WAVES, 31);
        map.put(ArenaEditorType.WAVES_CHANGE_GRADUAL, 13);
        map.put(MenuItemType.RETURN, 49);
    }

    @NotNull
    public EditorWaveList getEditorWaveList() {
        if (this.editorWaveList == null) {
            this.editorWaveList = new EditorWaveList(this.object);
        }
        return this.editorWaveList;
    }

    @NotNull
    public EditorWaveGradual getEditorGradual() {
        if (this.editorGradual == null) {
            this.editorGradual = new EditorWaveGradual(this.object);
        }
        return this.editorGradual;
    }

    @Override
    public boolean cancelClick(@NotNull InventoryClickEvent e, @NotNull SlotType slotType) {
        return true;
    }

    @Override
    public void onItemPrepare(@NotNull Player player, @NotNull MenuItem menuItem, @NotNull ItemStack item) {
        super.onItemPrepare(player, menuItem, item);
        ItemUtil.replace(item, this.object.replacePlaceholders());
    }
}
