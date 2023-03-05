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
import su.nightexpress.ama.AMA;
import su.nightexpress.ama.arena.impl.ArenaConfig;
import su.nightexpress.ama.arena.wave.ArenaWave;
import su.nightexpress.ama.arena.wave.ArenaWaveAmplifier;
import su.nightexpress.ama.config.Lang;
import su.nightexpress.ama.editor.ArenaEditorType;
import su.nightexpress.ama.editor.ArenaEditorUtils;

import java.util.Map;

public class WaveSettingsEditor extends AbstractEditorMenu<AMA, ArenaWave> {

    private WaveMobsEditor editorMobs;

    public WaveSettingsEditor(@NotNull ArenaWave arenaWave) {
        super(arenaWave.getArena().plugin(), arenaWave, ArenaEditorUtils.TITLE_WAVE_EDITOR, 36);

        EditorInput<ArenaWave, ArenaEditorType> input = (player, wave, type, e) -> {
            String msg = e.getMessage();
            ArenaConfig config = wave.getArenaConfig();
            if (type == ArenaEditorType.WAVES_WAVE_CHANGE_AMPLIFIERS) {
                ArenaWaveAmplifier amplifier = config.getWaveManager().getAmplifier(Colorizer.strip(msg));
                if (amplifier == null) {
                    EditorManager.error(player, plugin.getMessage(Lang.Editor_Arena_Waves_Error_Amplificator_Invalid).getLocalized());
                    return false;
                }

                wave.getAmplifiers().add(amplifier.getId());
            }
            config.getWaveManager().save();
            return true;
        };

        MenuClick click = (player, type, e) -> {
            if (type instanceof MenuItemType type2) {
                if (type2 == MenuItemType.RETURN) {
                    arenaWave.getArenaConfig().getWaveManager().getEditor().getEditorWaveList().open(player, 1);
                }
            }
            else if (type instanceof ArenaEditorType type2) {
                switch (type2) {
                    case WAVES_WAVE_CHANGE_MOBS -> this.getEditorMobs().open(player, 1);
                    case WAVES_WAVE_CHANGE_AMPLIFIERS -> {
                        if (e.isRightClick()) {
                            arenaWave.getAmplifiers().clear();
                            arenaWave.getArenaConfig().getWaveManager().save();
                            this.open(player, 1);
                            return;
                        }
                        EditorManager.startEdit(player, arenaWave, type2, input);
                        EditorManager.suggestValues(player, arenaWave.getArenaConfig().getWaveManager().getAmplifiers().keySet(), true);
                        EditorManager.tip(player, plugin.getMessage(Lang.Editor_Arena_Waves_Enter_Amplificator_Create).getLocalized());
                        player.closeInventory();
                    }
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
        super.clear();
    }

    @NotNull
    public WaveMobsEditor getEditorMobs() {
        if (this.editorMobs == null) {
            this.editorMobs = new WaveMobsEditor(this.object);
        }
        return editorMobs;
    }

    @Override
    public void setTypes(@NotNull Map<EditorButtonType, Integer> map) {
        map.put(ArenaEditorType.WAVES_WAVE_CHANGE_MOBS, 12);
        map.put(ArenaEditorType.WAVES_WAVE_CHANGE_AMPLIFIERS, 14);
        map.put(MenuItemType.RETURN, 31);
    }

    @Override
    public void onItemPrepare(@NotNull Player player, @NotNull MenuItem menuItem, @NotNull ItemStack item) {
        super.onItemPrepare(player, menuItem, item);
        ItemUtil.replace(item, this.object.replacePlaceholders());
    }

    @Override
    public boolean cancelClick(@NotNull InventoryClickEvent e, @NotNull SlotType slotType) {
        return true;
    }
}
