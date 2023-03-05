package su.nightexpress.ama.arena.editor.wave;

import org.bukkit.Material;
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

public class WavesGradualSettingsEditor extends AbstractEditorMenu<AMA, ArenaWaveManager> {

    public WavesGradualSettingsEditor(@NotNull ArenaWaveManager waveManager) {
        super(waveManager.getArena().plugin(), waveManager, ArenaEditorUtils.TITLE_WAVE_EDITOR, 45);

        EditorInput<ArenaWaveManager, ArenaEditorType> input = (player, waves, type, e) -> {
            String msg = StringUtil.colorOff(e.getMessage());
            switch (type) {
                case WAVES_CHANGE_GRADUAL_FIRST_PERCENT -> {
                    double value = StringUtil.getDouble(msg, 50D);
                    waves.setGradualSpawnPercentFirst(value);
                }
                case WAVES_CHANGE_GRADUAL_NEXT_PERCENT -> {
                    double value = StringUtil.getDouble(msg, 20D);
                    waves.setGradualSpawnNextPercent(value);
                }
                case WAVES_CHANGE_GRADUAL_NEXT_INTERVAL -> {
                    int value = StringUtil.getInteger(msg, 5);
                    waves.setGradualSpawnNextInterval(value);
                }
                case WAVES_CHANGE_GRADUAL_NEXT_KILL_PERCENT -> {
                    double value = StringUtil.getDouble(msg, 10D);
                    waves.setGradualSpawnNextKillPercent(value);
                }
                default -> {}
            }

            waves.save();
            return true;
        };

        MenuClick click = (player, type, e) -> {
            if (type instanceof MenuItemType type2) {
                if (type2 == MenuItemType.RETURN) {
                    waveManager.getEditor().open(player, 1);
                }
            }
            else if (type instanceof ArenaEditorType type2) {
                switch (type2) {
                    case WAVES_CHANGE_GRADUAL_ENABLED -> {
                        waveManager.setGradualSpawnEnabled(!waveManager.isGradualSpawnEnabled());
                        waveManager.save();
                        this.open(player, 1);
                        return;
                    }
                    case WAVES_CHANGE_GRADUAL_FIRST_PERCENT -> EditorManager.tip(player, plugin.getMessage(Lang.Editor_Arena_Waves_Enter_Gradual_First_Percent).getLocalized());
                    case WAVES_CHANGE_GRADUAL_NEXT_PERCENT -> EditorManager.tip(player, plugin.getMessage(Lang.Editor_Arena_Waves_Enter_Gradual_Next_Percent).getLocalized());
                    case WAVES_CHANGE_GRADUAL_NEXT_INTERVAL -> EditorManager.tip(player, plugin.getMessage(Lang.Editor_Arena_Waves_Enter_Gradual_Next_Interval).getLocalized());
                    case WAVES_CHANGE_GRADUAL_NEXT_KILL_PERCENT -> EditorManager.tip(player, plugin.getMessage(Lang.Editor_Arena_Waves_Enter_Gradual_Next_KillPercent).getLocalized());
                    default -> {return;}
                }
                EditorManager.startEdit(player, waveManager, type2, input);
                player.closeInventory();
            }
        };

        this.loadItems(click);
    }

    @Override
    public void setTypes(@NotNull Map<EditorButtonType, Integer> map) {
        map.put(ArenaEditorType.WAVES_CHANGE_GRADUAL_ENABLED, 4);
        map.put(ArenaEditorType.WAVES_CHANGE_GRADUAL_FIRST_PERCENT, 19);
        map.put(ArenaEditorType.WAVES_CHANGE_GRADUAL_NEXT_PERCENT, 21);
        map.put(ArenaEditorType.WAVES_CHANGE_GRADUAL_NEXT_INTERVAL, 23);
        map.put(ArenaEditorType.WAVES_CHANGE_GRADUAL_NEXT_KILL_PERCENT, 25);
        map.put(MenuItemType.RETURN, 40);
    }

    @Override
    public void onItemPrepare(@NotNull Player player, @NotNull MenuItem menuItem, @NotNull ItemStack item) {
        super.onItemPrepare(player, menuItem, item);
        if (menuItem.getType() instanceof ArenaEditorType type2) {
            if (type2 == ArenaEditorType.WAVES_CHANGE_GRADUAL_ENABLED) {
                item.setType(this.object.isGradualSpawnEnabled() ? Material.LIME_DYE : Material.GRAY_DYE);
            }
        }
        ItemUtil.replace(item, this.object.replacePlaceholders());
    }

    @Override
    public boolean cancelClick(@NotNull InventoryClickEvent e, @NotNull SlotType slotType) {
        return true;
    }
}
