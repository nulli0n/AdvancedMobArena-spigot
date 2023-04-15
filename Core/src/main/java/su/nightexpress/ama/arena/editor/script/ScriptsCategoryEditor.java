package su.nightexpress.ama.arena.editor.script;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.editor.EditorButtonType;
import su.nexmedia.engine.api.editor.EditorInput;
import su.nexmedia.engine.api.menu.MenuClick;
import su.nexmedia.engine.api.menu.MenuItemType;
import su.nexmedia.engine.editor.AbstractEditorMenuAuto;
import su.nexmedia.engine.editor.EditorManager;
import su.nexmedia.engine.utils.CollectionsUtil;
import su.nexmedia.engine.utils.ItemUtil;
import su.nightexpress.ama.AMA;
import su.nightexpress.ama.Placeholders;
import su.nightexpress.ama.arena.script.action.ScriptPreparedAction;
import su.nightexpress.ama.arena.script.condition.ScriptPreparedCondition;
import su.nightexpress.ama.arena.script.impl.ArenaScript;
import su.nightexpress.ama.arena.script.impl.ScriptCategory;
import su.nightexpress.ama.config.Lang;
import su.nightexpress.ama.editor.ArenaEditorHub;
import su.nightexpress.ama.editor.ArenaEditorType;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class ScriptsCategoryEditor extends AbstractEditorMenuAuto<AMA, ScriptCategory, ArenaScript> {

    public ScriptsCategoryEditor(@NotNull ScriptCategory category) {
        super(category.plugin(), category, ArenaEditorHub.TITLE_SCRIPT_EDITOR, 45);

        EditorInput<ScriptCategory, ArenaEditorType> input = (player, category1, type, e) -> {
            String msg = e.getMessage();
            if (type == ArenaEditorType.SCRIPT_CREATE) {
                category1.createScript(EditorManager.fineId(msg));
            }
            category1.save();
            return true;
        };

        MenuClick click = (player, type, e) -> {
            if (type instanceof MenuItemType type2) {
                if (type2 == MenuItemType.RETURN) {
                    category.getArenaConfig().getScriptManager().getEditor().open(player, 1);
                }
                else this.onItemClickDefault(player, type2);
            }
            else if (type instanceof ArenaEditorType type2) {
                if (type2 == ArenaEditorType.SCRIPT_CREATE) {
                    EditorManager.startEdit(player, category, type2, input);
                    EditorManager.prompt(player, plugin.getMessage(Lang.EDITOR_ARENA_SCRIPT_ENTER_SCRIPT).getLocalized());
                    player.closeInventory();
                }
            }
        };

        this.loadItems(click);
    }

    @Override
    public void setTypes(@NotNull Map<EditorButtonType, Integer> map) {
        map.put(ArenaEditorType.SCRIPT_CREATE, 41);
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
    protected List<ArenaScript> getObjects(@NotNull Player player) {
        return this.parent.getScripts().stream().sorted(Comparator.comparing(ArenaScript::getId)).toList();
    }

    @Override
    @NotNull
    protected ItemStack getObjectStack(@NotNull Player player, @NotNull ArenaScript script) {
        ItemStack item = ArenaEditorType.SCRIPT_OBJECT.getItem();

        String condis = script.getConditions().values()
            .stream().map(list -> list.stream().map(ScriptPreparedCondition::toRaw).map(str -> ChatColor.LIGHT_PURPLE + str).collect(Collectors.joining("\n")))
            .collect(Collectors.joining("\n" + ChatColor.RED + "OR:\n"));

        String actions = script.getActions().stream()
            .map(ScriptPreparedAction::toRaw).map(str -> ChatColor.GOLD + str)
            .collect(Collectors.joining("\n"));

        ItemUtil.replace(item, str -> str
            .replace(Placeholders.SCRIPT_ID, script.getId())
            .replace(Placeholders.SCRIPT_CONDITION_SECTION_CONDITIONS, condis)
            .replace(Placeholders.SCRIPT_ACTION_PARAMS, actions)
            .replace(Placeholders.SCRIPT_EVENT_TYPE, script.getEventType().name()));
        return item;
    }

    @Override
    @NotNull
    protected MenuClick getObjectClick(@NotNull Player player, @NotNull ArenaScript script) {
        return (player2, type, e) -> {
            if (e.isShiftClick() && e.isRightClick()) {
                this.parent.getScriptsMap().remove(script.getId());
                this.parent.save();
                this.open(player2, this.getPage(player2));
                return;
            }

            if (e.isLeftClick()) {
                script.getActionsEditor(this.parent).open(player2, 1);
            }
            else if (e.isRightClick()) {
                script.getConditionsEditor(this.parent).open(player2, 1);
            }
            else if (e.getClick() == ClickType.DROP) {
                script.setEventType(CollectionsUtil.next(script.getEventType()));
                this.parent.save();
                this.open(player2, this.getPage(player2));
            }
        };
    }

    @Override
    public boolean cancelClick(@NotNull InventoryClickEvent inventoryClickEvent, @NotNull SlotType slotType) {
        return true;
    }
}
