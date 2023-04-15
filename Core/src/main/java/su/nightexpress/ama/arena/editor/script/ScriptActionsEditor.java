package su.nightexpress.ama.arena.editor.script;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.editor.EditorButtonType;
import su.nexmedia.engine.api.editor.EditorInput;
import su.nexmedia.engine.api.menu.MenuClick;
import su.nexmedia.engine.api.menu.MenuItemType;
import su.nexmedia.engine.editor.AbstractEditorMenuAuto;
import su.nexmedia.engine.editor.EditorManager;
import su.nexmedia.engine.utils.ItemUtil;
import su.nightexpress.ama.AMA;
import su.nightexpress.ama.Placeholders;
import su.nightexpress.ama.arena.script.action.*;
import su.nightexpress.ama.arena.script.impl.ArenaScript;
import su.nightexpress.ama.arena.script.impl.ScriptCategory;
import su.nightexpress.ama.config.Lang;
import su.nightexpress.ama.editor.ArenaEditorHub;
import su.nightexpress.ama.editor.ArenaEditorType;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class ScriptActionsEditor extends AbstractEditorMenuAuto<AMA, ArenaScript, ScriptPreparedAction> {

    private final ScriptCategory category;

    public ScriptActionsEditor(@NotNull ScriptCategory category, @NotNull ArenaScript script) {
        super(script.plugin(), script, ArenaEditorHub.TITLE_SCRIPT_EDITOR, 45);
        this.category = category;

        EditorInput<ArenaScript, ArenaEditorType> input = (player, script1, type, e) -> {
            String msg = e.getMessage();
            if (type == ArenaEditorType.SCRIPT_ACTION_CREATE) {
                String id = EditorManager.fineId(msg);
                ScriptAction scriptAction = ScriptActions.getByName(id);
                if (scriptAction == null) {
                    EditorManager.error(player, plugin.getMessage(Lang.EDITOR_ARENA_SCRIPT_ERROR_INVALID_ACTION).getLocalized());
                    return false;
                }

                ScriptPreparedAction action = new ScriptPreparedAction(scriptAction, new ParameterResult());
                script1.getActions().add(action);
            }

            script1.getArenaConfig().getScriptManager().save();
            return true;
        };

        MenuClick click = (player, type, e) -> {
            if (type instanceof MenuItemType type2) {
                if (type2 == MenuItemType.RETURN) {
                    category.getEditor().open(player, 1);
                }
                else this.onItemClickDefault(player, type2);
            }
            else if (type instanceof ArenaEditorType type2) {
                if (type2 == ArenaEditorType.SCRIPT_ACTION_CREATE) {
                    EditorManager.startEdit(player, script, type2, input);
                    EditorManager.suggestValues(player, ScriptActions.getActions().stream().map(ScriptAction::getName).toList(), true);
                    EditorManager.prompt(player, plugin.getMessage(Lang.EDITOR_ARENA_SCRIPT_ENTER_ACTION_NAME).getLocalized());
                    player.closeInventory();
                }
            }
        };

        this.loadItems(click);
    }

    @Override
    public void setTypes(@NotNull Map<EditorButtonType, Integer> map) {
        map.put(ArenaEditorType.SCRIPT_ACTION_CREATE, 41);
        map.put(MenuItemType.RETURN, 39);
        map.put(MenuItemType.PAGE_NEXT, 44);
        map.put(MenuItemType.PAGE_PREVIOUS, 36);
    }

    @Override
    protected int[] getObjectSlots() {
        return IntStream.range(0, 36).toArray();
    }

    @Override
    @NotNull
    protected List<ScriptPreparedAction> getObjects(@NotNull Player player) {
        return new ArrayList<>(this.parent.getActions());
    }

    @Override
    @NotNull
    protected ItemStack getObjectStack(@NotNull Player player, @NotNull ScriptPreparedAction action) {
        ItemStack item = ArenaEditorType.SCRIPT_ACTION_OBJECT.getItem();

        String params = action.getAction().getParameters().stream().map(parameter -> {
            return ChatColor.YELLOW.toString() + ChatColor.BOLD + parameter.getName() + ": " + ChatColor.AQUA + action.getParameters().get(parameter, null);
        }).collect(Collectors.joining("\n"));

        ItemUtil.replace(item, str -> str
            .replace(Placeholders.SCRIPT_ACTION_NAME, action.getAction().getName())
            .replace(Placeholders.SCRIPT_ACTION_PARAMS, params));
        return item;
    }

    @Override
    @NotNull
    protected MenuClick getObjectClick(@NotNull Player player, @NotNull ScriptPreparedAction action) {
        return (player2, type, e) -> {
            if (e.isShiftClick() && e.isRightClick()) {
                this.parent.getActions().remove(action);
                this.category.save();
                this.open(player2, this.getPage(player2));
                return;
            }

            if (e.isLeftClick()) {
                EditorInput<ScriptPreparedAction, ArenaEditorType> input = (player3, action1, type2, e2) -> {
                    String[] input2 = e2.getMessage().split(" ");
                    if (input2.length < 2) {
                        EditorManager.error(player3, plugin.getMessage(Lang.EDITOR_ARENA_SCRIPT_ERROR_INVALID_INPUT).getLocalized());
                        return false;
                    }

                    String paramName = input2[0];
                    String paramValue = Stream.of(input2).skip(1).collect(Collectors.joining(" "));

                    Parameter<?> parameter = Parameters.getByName(paramName).orElse(null);
                    if (parameter == null || !action1.getAction().getParameters().contains(parameter)) {
                        EditorManager.error(player3, plugin.getMessage(Lang.EDITOR_ARENA_SCRIPT_ERROR_INVALID_PARAMETER).getLocalized());
                        return false;
                    }
                    action1.getParameters().add(parameter, parameter.getParser().apply(paramValue));
                    this.category.save();
                    return true;
                };

                EditorManager.startEdit(player2, action, ArenaEditorType.SCRIPT_ACTION_OBJECT, input);
                EditorManager.suggestValues(player2, action.getAction().getParameters().stream().map(Parameter::getName).toList(), false);
                EditorManager.prompt(player2, plugin.getMessage(Lang.EDITOR_ARENA_SCRIPT_ENTER_ACTION_PARAMETER).getLocalized());
                player2.closeInventory();
                return;
            }

            if (e.isRightClick()) {
                this.parent.getActions().clear();
                this.category.save();
            }
        };
    }

    @Override
    public boolean cancelClick(@NotNull InventoryClickEvent inventoryClickEvent, @NotNull SlotType slotType) {
        return true;
    }
}
