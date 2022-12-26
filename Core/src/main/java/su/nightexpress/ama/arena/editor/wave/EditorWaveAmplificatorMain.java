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
import su.nightexpress.ama.arena.wave.ArenaWaveAmplificator;
import su.nightexpress.ama.config.Lang;
import su.nightexpress.ama.editor.ArenaEditorType;
import su.nightexpress.ama.editor.ArenaEditorUtils;

import java.util.Map;

public class EditorWaveAmplificatorMain extends AbstractEditorMenu<AMA, ArenaWaveAmplificator> {

    public EditorWaveAmplificatorMain(@NotNull AMA plugin, @NotNull ArenaWaveAmplificator amplificator) {
        super(plugin, amplificator, ArenaEditorUtils.TITLE_WAVE_EDITOR, 36);

        EditorInput<ArenaWaveAmplificator, ArenaEditorType> input = (player, amplificator2, type, e) -> {
            String msg = StringUtil.color(e.getMessage());
            switch (type) {
                case WAVES_WAVE_AMPLIFICATOR_CHANGE_VALUE_LEVEL -> amplificator2.setValueLevel(StringUtil.getInteger(msg, 0, true));
                case WAVES_WAVE_AMPLIFICATOR_CHANGE_VALUE_AMOUNT -> amplificator2.setValueAmount(StringUtil.getInteger(msg, 0, true));
                default -> {
                    return true;
                }
            }

            amplificator2.getArenaWave().getArenaConfig().getWaveManager().save();
            return true;
        };

        MenuClick click = (player, type, e) -> {
            if (type instanceof MenuItemType type2) {
                if (type2 == MenuItemType.RETURN) {
                    amplificator.getArenaWave().getEditor().getEditorAmplificators().open(player, 1);
                }
            }
            else if (type instanceof ArenaEditorType type2) {
                switch (type2) {
                    case WAVES_WAVE_AMPLIFICATOR_CHANGE_VALUES -> {
                        if (e.isRightClick()) type2 = ArenaEditorType.WAVES_WAVE_AMPLIFICATOR_CHANGE_VALUE_AMOUNT;
                        else if (e.isLeftClick()) type2 = ArenaEditorType.WAVES_WAVE_AMPLIFICATOR_CHANGE_VALUE_LEVEL;

                        EditorManager.startEdit(player, amplificator, type2, input);
                        EditorManager.tip(player, plugin.getMessage(Lang.Editor_Arena_Waves_Enter_Amplificator_Value).getLocalized());
                        player.closeInventory();
                    }
                    case WAVES_WAVE_AMPLIFICATOR_CHANGE_TRIGGERS -> {
                        ArenaEditorUtils.handleTriggersClick(player, amplificator, type2, e.isRightClick());
                        if (e.isRightClick()) {
                            amplificator.getArenaWave().getArenaConfig().getWaveManager().save();
                            this.open(player, 1);
                        }
                    }
                    default -> {}
                }
            }
        };

        this.loadItems(click);
    }

    @Override
    public void setTypes(@NotNull Map<EditorButtonType, Integer> map) {
        map.put(ArenaEditorType.WAVES_WAVE_AMPLIFICATOR_CHANGE_TRIGGERS, 11);
        map.put(ArenaEditorType.WAVES_WAVE_AMPLIFICATOR_CHANGE_VALUES, 15);
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
