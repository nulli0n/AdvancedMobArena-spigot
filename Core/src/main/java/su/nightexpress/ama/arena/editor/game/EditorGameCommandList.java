package su.nightexpress.ama.arena.editor.game;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.editor.EditorButtonType;
import su.nexmedia.engine.api.menu.MenuClick;
import su.nexmedia.engine.api.menu.MenuItemType;
import su.nexmedia.engine.editor.AbstractEditorMenuAuto;
import su.nexmedia.engine.utils.ItemUtil;
import su.nightexpress.ama.AMA;
import su.nightexpress.ama.api.arena.type.ArenaTargetType;
import su.nightexpress.ama.arena.game.ArenaGameCommand;
import su.nightexpress.ama.arena.game.ArenaGameplayManager;
import su.nightexpress.ama.editor.ArenaEditorType;
import su.nightexpress.ama.editor.ArenaEditorUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

public class EditorGameCommandList extends AbstractEditorMenuAuto<AMA, ArenaGameplayManager, ArenaGameCommand> {

    public EditorGameCommandList(@NotNull ArenaGameplayManager gameplayManager) {
        super(gameplayManager.plugin(), gameplayManager, ArenaEditorUtils.TITLE_GAMEPLAY_EDITOR, 45);

        MenuClick click = (player, type, e) -> {
            if (type instanceof MenuItemType type2) {
                if (type2 == MenuItemType.RETURN) {
                    gameplayManager.getEditor().open(player, 1);
                }
                else this.onItemClickDefault(player, type2);
            }
            else if (type instanceof ArenaEditorType type2) {
                if (type2 == ArenaEditorType.GAMEPLAY_AUTO_COMMAND_CREATE) {
                    ArenaGameCommand gameCommand = new ArenaGameCommand(
                        gameplayManager.getArenaConfig(), new HashSet<>(),
                        ArenaTargetType.GLOBAL, new ArrayList<>());
                    gameplayManager.getAutoCommands().add(gameCommand);
                    gameplayManager.save();
                    this.open(player, 1);
                }
            }
        };

        this.loadItems(click);
    }

    @Override
    public void setTypes(@NotNull Map<EditorButtonType, Integer> map) {
        map.put(ArenaEditorType.GAMEPLAY_AUTO_COMMAND_CREATE, 41);
        map.put(MenuItemType.PAGE_NEXT, 44);
        map.put(MenuItemType.PAGE_PREVIOUS, 36);
        map.put(MenuItemType.RETURN, 39);
    }

    @Override
    public int[] getObjectSlots() {
        return IntStream.range(0, 36).toArray();
    }

    @Override
    @NotNull
    protected List<ArenaGameCommand> getObjects(@NotNull Player player) {
        return new ArrayList<>(this.parent.getAutoCommands());
    }

    @Override
    @NotNull
    protected ItemStack getObjectStack(@NotNull Player player, @NotNull ArenaGameCommand gameCommand) {
        ItemStack item = ArenaEditorType.GAMEPLAY_AUTO_COMMAND_OBJECT.getItem();
        ItemUtil.replace(item, gameCommand.replacePlaceholders());
        return item;
    }

    @Override
    @NotNull
    protected MenuClick getObjectClick(@NotNull Player player, @NotNull ArenaGameCommand gameCommand) {
        return (player1, type, e) -> {
            if (e.isShiftClick() && e.isRightClick()) {
                gameCommand.clear();
                this.parent.getAutoCommands().remove(gameCommand);
                this.parent.save();
                this.open(player1, this.getPage(player1));
                return;
            }
            gameCommand.getEditor().open(player1, 1);
        };
    }

    @Override
    public boolean cancelClick(@NotNull InventoryClickEvent e, @NotNull SlotType slotType) {
        return true;
    }
}
