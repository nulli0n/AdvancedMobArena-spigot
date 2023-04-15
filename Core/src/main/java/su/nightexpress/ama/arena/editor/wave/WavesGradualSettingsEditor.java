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
import su.nexmedia.engine.utils.Colorizer;
import su.nexmedia.engine.utils.ItemUtil;
import su.nexmedia.engine.utils.StringUtil;
import su.nightexpress.ama.AMA;
import su.nightexpress.ama.arena.wave.ArenaWaveManager;
import su.nightexpress.ama.config.Lang;
import su.nightexpress.ama.editor.ArenaEditorHub;
import su.nightexpress.ama.editor.ArenaEditorType;

import java.util.Map;

public class WavesGradualSettingsEditor extends AbstractEditorMenu<AMA, ArenaWaveManager> {

    public WavesGradualSettingsEditor(@NotNull ArenaWaveManager waveManager) {
        super(waveManager.getArena().plugin(), waveManager, ArenaEditorHub.TITLE_WAVE_EDITOR, 45);

        EditorInput<ArenaWaveManager, ArenaEditorType> input = (player, waves, type, e) -> {
            String msg = e.getMessage();
            switch (type) {
                case WAVES_CHANGE_GRADUAL_FIRST_PERCENT -> waves.setGradualSpawnPercentFirst(StringUtil.getDouble(Colorizer.strip(msg), 0D));
                case WAVES_CHANGE_GRADUAL_NEXT_PERCENT -> waves.setGradualSpawnNextPercent(StringUtil.getDouble(Colorizer.strip(msg), 0D));
                case WAVES_CHANGE_GRADUAL_NEXT_INTERVAL -> waves.setGradualSpawnNextInterval(StringUtil.getInteger(Colorizer.strip(msg), 0));
                case WAVES_CHANGE_GRADUAL_NEXT_KILL_PERCENT -> waves.setGradualSpawnNextKillPercent(StringUtil.getDouble(Colorizer.strip(msg), 0D));
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
                    case WAVES_CHANGE_GRADUAL_FIRST_PERCENT,
                        WAVES_CHANGE_GRADUAL_NEXT_PERCENT,
                        WAVES_CHANGE_GRADUAL_NEXT_KILL_PERCENT -> EditorManager.prompt(player, plugin.getMessage(Lang.EDITOR_GENERIC_ENTER_PERCENT).getLocalized());
                    case WAVES_CHANGE_GRADUAL_NEXT_INTERVAL -> EditorManager.prompt(player, plugin.getMessage(Lang.EDITOR_GENERIC_ENTER_SECONDS).getLocalized());
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
