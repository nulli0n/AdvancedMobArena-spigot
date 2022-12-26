package su.nightexpress.ama.arena.editor.game;

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
import su.nexmedia.engine.utils.CollectionsUtil;
import su.nexmedia.engine.utils.ItemUtil;
import su.nexmedia.engine.utils.StringUtil;
import su.nightexpress.ama.AMA;
import su.nightexpress.ama.arena.game.ArenaGameCommand;
import su.nightexpress.ama.config.Lang;
import su.nightexpress.ama.editor.ArenaEditorType;
import su.nightexpress.ama.editor.ArenaEditorUtils;

import java.util.Map;

public class EditorGameCommandSettings extends AbstractEditorMenu<AMA, ArenaGameCommand> {

    public EditorGameCommandSettings(@NotNull ArenaGameCommand gameCommand) {
        super(gameCommand.plugin(), gameCommand, ArenaEditorUtils.TITLE_GAMEPLAY_EDITOR, 45);

        EditorInput<ArenaGameCommand, ArenaEditorType> input = (player, gameCommand2, type, e) -> {
            String msg = StringUtil.color(e.getMessage());

            if (type == ArenaEditorType.GAMEPLAY_AUTO_COMMAND_CHANGE_COMMANDS) {
                gameCommand2.getCommands().add(StringUtil.colorRaw(msg));
            }

            gameCommand2.getArenaConfig().getGameplayManager().save();
            return true;
        };

        MenuClick click = (player, type, e) -> {
            if (type instanceof MenuItemType type2) {
                if (type2 == MenuItemType.RETURN) {
                    gameCommand.getArenaConfig().getGameplayManager().getEditor().getCommandList().open(player, 1);
                }
            }
            else if (type instanceof ArenaEditorType type2) {
                switch (type2) {
                    case GAMEPLAY_AUTO_COMMAND_CHANGE_COMMANDS -> {
                        if (e.isRightClick()) {
                            gameCommand.getCommands().clear();
                            break;
                        }
                        EditorManager.startEdit(player, gameCommand, type2, input);
                        EditorManager.tip(player, plugin.getMessage(Lang.Editor_Reward_Enter_Command).getLocalized());
                        EditorManager.sendCommandTips(player);
                        player.closeInventory();
                        return;
                    }
                    case GAMEPLAY_AUTO_COMMAND_CHANGE_TARGET_TYPE -> gameCommand.setTargetType(CollectionsUtil.switchEnum(gameCommand.getTargetType()));
                    case GAMEPLAY_AUTO_COMMAND_CHANGE_TRIGGERS -> {
                        ArenaEditorUtils.handleTriggersClick(player, gameCommand, type2, e.isRightClick());
                        if (e.isRightClick()) break;
                        return;
                    }
                }
                gameCommand.getArenaConfig().getGameplayManager().save();
                this.open(player, 1);
            }
        };

        this.loadItems(click);
    }

    @Override
    public void setTypes(@NotNull Map<EditorButtonType, Integer> map) {
        map.put(ArenaEditorType.GAMEPLAY_AUTO_COMMAND_CHANGE_TRIGGERS, 11);
        map.put(ArenaEditorType.GAMEPLAY_AUTO_COMMAND_CHANGE_TARGET_TYPE, 13);
        map.put(ArenaEditorType.GAMEPLAY_AUTO_COMMAND_CHANGE_COMMANDS, 15);
        map.put(MenuItemType.RETURN, 40);
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
