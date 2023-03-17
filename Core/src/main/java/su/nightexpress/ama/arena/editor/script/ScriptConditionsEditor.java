package su.nightexpress.ama.arena.editor.script;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.editor.EditorButtonType;
import su.nexmedia.engine.api.editor.EditorInput;
import su.nexmedia.engine.api.menu.AbstractMenu;
import su.nexmedia.engine.api.menu.MenuClick;
import su.nexmedia.engine.api.menu.MenuItemType;
import su.nexmedia.engine.editor.AbstractEditorMenuAuto;
import su.nexmedia.engine.editor.EditorManager;
import su.nexmedia.engine.utils.ItemUtil;
import su.nightexpress.ama.AMA;
import su.nightexpress.ama.Placeholders;
import su.nightexpress.ama.arena.script.condition.ScriptCondition;
import su.nightexpress.ama.arena.script.condition.ScriptConditions;
import su.nightexpress.ama.arena.script.condition.ScriptPreparedCondition;
import su.nightexpress.ama.arena.script.impl.ArenaScript;
import su.nightexpress.ama.arena.script.impl.ScriptCategory;
import su.nightexpress.ama.config.Lang;
import su.nightexpress.ama.editor.ArenaEditorHub;
import su.nightexpress.ama.editor.ArenaEditorType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class ScriptConditionsEditor extends AbstractEditorMenuAuto<AMA, ArenaScript, String> {

    private final ScriptCategory category;

    public ScriptConditionsEditor(@NotNull ScriptCategory category, @NotNull ArenaScript script) {
        super(script.plugin(), script, ArenaEditorHub.TITLE_SCRIPT_EDITOR, 45);
        this.category = category;

        EditorInput<ArenaScript, ArenaEditorType> input = (player, script1, type, e) -> {
            String msg = e.getMessage();
            if (type == ArenaEditorType.SCRIPT_CONDITION_SECTION_CREATE) {
                String id = EditorManager.fineId(msg);
                if (script1.getConditions().containsKey(id)) return true;

                script1.getConditions().put(id, new ArrayList<>());
            }

            category.save();
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
                if (type2 == ArenaEditorType.SCRIPT_CONDITION_SECTION_CREATE) {
                    EditorManager.startEdit(player, script, type2, input);
                    EditorManager.tip(player, plugin.getMessage(Lang.EDITOR_ARENA_SCRIPT_ENTER_CONDITION_SECTION).getLocalized());
                    player.closeInventory();
                }
            }
        };

        this.loadItems(click);
    }

    @Override
    public void setTypes(@NotNull Map<EditorButtonType, Integer> map) {
        map.put(ArenaEditorType.SCRIPT_CONDITION_SECTION_CREATE, 41);
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
    protected List<String> getObjects(@NotNull Player player) {
        return new ArrayList<>(this.parent.getConditions().keySet());
    }

    @Override
    @NotNull
    protected ItemStack getObjectStack(@NotNull Player player, @NotNull String section) {
        ItemStack item = ArenaEditorType.SCRIPT_CONDITION_SECTION_OBJECT.getItem();

        String condis = this.parent.getConditions().getOrDefault(section, Collections.emptyList())
            .stream().map(ScriptPreparedCondition::toRaw).map(str -> ChatColor.GREEN + str)
            .collect(Collectors.joining("\n"));

        ItemUtil.replace(item, str -> str
            .replace(Placeholders.SCRIPT_CONDITION_SECTION_ID, section)
            .replace(Placeholders.SCRIPT_CONDITION_SECTION_CONDITIONS, condis));
        return item;
    }

    @Override
    @NotNull
    protected MenuClick getObjectClick(@NotNull Player player, @NotNull String section) {
        return (player2, type, e) -> {
            if (e.isShiftClick() && e.isRightClick()) {
                this.parent.getConditions().remove(section);
                this.category.save();
                this.open(player2, this.getPage(player2));
                return;
            }

            if (e.isLeftClick()) {
                EditorInput<String, ArenaEditorType> input = (player3, action1, type2, e2) -> {
                    String[] input2 = e2.getMessage().split(" ");
                    if (input2.length < 3) {
                        EditorManager.error(player3, plugin.getMessage(Lang.EDITOR_ARENA_SCRIPT_ERROR_INVALID_INPUT).getLocalized());
                        return false;
                    }

                    String condName = input2[0];
                    String condOper = input2[1];
                    String condValue = input2[2];

                    ScriptCondition<?, ?> parameter = ScriptConditions.getByName(condName);
                    if (parameter == null) {
                        EditorManager.error(player3, plugin.getMessage(Lang.EDITOR_ARENA_SCRIPT_ERROR_INVALID_CONDITION).getLocalized());
                        return false;
                    }

                    Object parsed = parameter.getParser().apply(condValue);

                    ScriptCondition.Operator operator = ScriptCondition.Operator.fromString(condOper).orElse(ScriptCondition.Operator.EQUAL);
                    this.parent.getConditions().computeIfAbsent(section, k -> new ArrayList<>()).add(new ScriptPreparedCondition(parameter, parsed, operator));
                    this.category.save();
                    return true;
                };

                EditorManager.startEdit(player2, section, ArenaEditorType.SCRIPT_CONDITION_SECTION_OBJECT, input);
                EditorManager.suggestValues(player2, ScriptConditions.getConditions().stream().map(ScriptCondition::getName).toList(), false);
                EditorManager.tip(player2, plugin.getMessage(Lang.EDITOR_ARENA_SCRIPT_ENTER_CONDITION_VALUE).getLocalized());
                player2.closeInventory();
                return;
            }

            if (e.isRightClick()) {
                this.parent.getConditions().computeIfAbsent(section, k -> new ArrayList<>()).clear();
                this.category.save();
                this.open(player2, 1);
            }
        };
    }

    @Override
    public boolean cancelClick(@NotNull InventoryClickEvent inventoryClickEvent, @NotNull AbstractMenu.SlotType slotType) {
        return true;
    }
}
