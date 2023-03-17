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
import su.nexmedia.engine.utils.Colorizer;
import su.nexmedia.engine.utils.ItemUtil;
import su.nexmedia.engine.utils.StringUtil;
import su.nightexpress.ama.AMA;
import su.nightexpress.ama.arena.wave.ArenaWaveManager;
import su.nightexpress.ama.config.Lang;
import su.nightexpress.ama.editor.ArenaEditorHub;
import su.nightexpress.ama.editor.ArenaEditorType;

import java.util.Map;

public class WaveManagerEditor extends AbstractEditorMenu<AMA, ArenaWaveManager> {

    private WavesListEditor            wavesListEditor;
    private WavesGradualSettingsEditor gradualEditor;

    public WaveManagerEditor(@NotNull ArenaWaveManager waveManager) {
        super(waveManager.plugin(), waveManager, ArenaEditorHub.TITLE_WAVE_EDITOR, 54);

        EditorInput<ArenaWaveManager, ArenaEditorType> input = (player, waves, type, e) -> {
            String msg = e.getMessage();
            switch (type) {
                case WAVES_CHANGE_DELAY_FIRST -> waves.setDelayFirst(StringUtil.getInteger(Colorizer.strip(msg), 0));
                case WAVES_CHANGE_DELAY_DEFAULT -> waves.setDelayDefault(StringUtil.getInteger(Colorizer.strip(msg), 0));
                case WAVES_CHANGE_FINAL_WAVE -> waves.setFinalWave(StringUtil.getInteger(Colorizer.strip(msg), 0));
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
                            EditorManager.tip(player, plugin.getMessage(Lang.EDITOR_GENERIC_ENTER_SECONDS).getLocalized());
                        }
                        else if (e.isRightClick()) {
                            EditorManager.startEdit(player, waveManager, ArenaEditorType.WAVES_CHANGE_DELAY_DEFAULT, input);
                            EditorManager.tip(player, plugin.getMessage(Lang.EDITOR_GENERIC_ENTER_SECONDS).getLocalized());
                        }
                        player.closeInventory();
                    }
                    case WAVES_CHANGE_FINAL_WAVE -> {
                        if (e.isRightClick()) {
                            this.object.setFinalWave(-1);
                            this.object.save();
                            this.open(player, 1);
                            break;
                        }
                        EditorManager.startEdit(player, waveManager, type2, input);
                        EditorManager.tip(player, plugin.getMessage(Lang.EDITOR_GENERIC_ENTER_NUMBER).getLocalized());
                        player.closeInventory();
                    }
                    case WAVES_CHANGE_GRADUAL -> this.getGradualEditor().open(player, 1);
                    case WAVES_CHANGE_WAVES -> this.getWavesListEditor().open(player, 1);
                }
            }
        };

        this.loadItems(click);
    }

    @Override
    public void clear() {
        if (this.wavesListEditor != null) {
            this.wavesListEditor.clear();
            this.wavesListEditor = null;
        }
        if (this.gradualEditor != null) {
            this.gradualEditor.clear();
            this.gradualEditor = null;
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
    public WavesListEditor getWavesListEditor() {
        if (this.wavesListEditor == null) {
            this.wavesListEditor = new WavesListEditor(this.object);
        }
        return this.wavesListEditor;
    }

    @NotNull
    public WavesGradualSettingsEditor getGradualEditor() {
        if (this.gradualEditor == null) {
            this.gradualEditor = new WavesGradualSettingsEditor(this.object);
        }
        return this.gradualEditor;
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
