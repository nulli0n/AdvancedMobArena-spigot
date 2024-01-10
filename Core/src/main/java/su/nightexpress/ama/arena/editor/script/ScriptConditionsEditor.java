package su.nightexpress.ama.arena.editor.script;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.menu.AutoPaged;
import su.nexmedia.engine.api.menu.click.ItemClick;
import su.nexmedia.engine.api.menu.impl.EditorMenu;
import su.nexmedia.engine.api.menu.impl.MenuOptions;
import su.nexmedia.engine.api.menu.impl.MenuViewer;
import su.nexmedia.engine.editor.EditorManager;
import su.nexmedia.engine.utils.Colorizer;
import su.nexmedia.engine.utils.Colors2;
import su.nexmedia.engine.utils.ItemReplacer;
import su.nexmedia.engine.utils.StringUtil;
import su.nightexpress.ama.AMA;
import su.nightexpress.ama.Placeholders;
import su.nightexpress.ama.arena.script.condition.ScriptCondition;
import su.nightexpress.ama.arena.script.condition.ScriptConditions;
import su.nightexpress.ama.arena.script.condition.ScriptPreparedCondition;
import su.nightexpress.ama.arena.script.impl.ArenaScript;
import su.nightexpress.ama.arena.script.impl.ScriptCategory;
import su.nightexpress.ama.config.Lang;
import su.nightexpress.ama.editor.EditorLocales;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class ScriptConditionsEditor extends EditorMenu<AMA, ArenaScript> implements AutoPaged<String> {

    private static final String TITLE = "Script Conditions";

    private final ScriptCategory category;

    public ScriptConditionsEditor(@NotNull ScriptCategory category, @NotNull ArenaScript script) {
        super(category.plugin(), script, TITLE + " [" + script.getId() + "]", 45);
        this.category = category;

        this.addReturn(39).setClick((viewer, event) -> {
            category.getEditor().openNextTick(viewer, 1);
        });
        this.addNextPage(44);
        this.addPreviousPage(36);

        this.addCreation(EditorLocales.SCRIPT_CONDITION_SECTION_CREATE, 41).setClick((viewer, event) -> {
            this.handleInput(viewer, Lang.EDITOR_ARENA_SCRIPT_ENTER_CONDITION_SECTION, wrapper -> {
                String id = StringUtil.lowerCaseUnderscore(wrapper.getTextRaw());
                if (script.getConditions().containsKey(id)) return true;

                script.getConditions().put(id, new ArrayList<>());
                category.save();
                return true;
            });
        });
    }

    @Override
    public void onPrepare(@NotNull MenuViewer viewer, @NotNull MenuOptions options) {
        super.onPrepare(viewer, options);
        this.getItemsForPage(viewer).forEach(this::addItem);
    }

    @Override
    public int[] getObjectSlots() {
        return IntStream.range(0, 36).toArray();
    }

    @Override
    @NotNull
    public List<String> getObjects(@NotNull Player player) {
        return new ArrayList<>(this.object.getConditions().keySet());
    }

    @Override
    @NotNull
    public ItemStack getObjectStack(@NotNull Player player, @NotNull String section) {
        ItemStack item = new ItemStack(Material.CHAIN_COMMAND_BLOCK);

        String condis = this.object.getConditions().getOrDefault(section, Collections.emptyList())
            .stream().map(ScriptPreparedCondition::toRaw).map(str -> Colors2.GREEN + str)
            .collect(Collectors.joining("\n"));

        ItemReplacer.create(item).readLocale(EditorLocales.SCRIPT_CONDITION_SECTION_OBJECT).trimmed().hideFlags()
            .replace(Placeholders.SCRIPT_CONDITION_SECTION_ID, section)
            .replace(Placeholders.SCRIPT_CONDITION_SECTION_CONDITIONS, Colorizer.apply(condis))
            .writeMeta();
        return item;
    }

    @Override
    @NotNull
    public ItemClick getObjectClick(@NotNull String section) {
        return (viewer, event) -> {
            if (event.isShiftClick() && event.isRightClick()) {
                this.object.getConditions().remove(section);
                this.category.save();
                this.openNextTick(viewer, viewer.getPage());
                return;
            }

            Player player = viewer.getPlayer();
            if (event.isLeftClick()) {
                EditorManager.suggestValues(viewer.getPlayer(), ScriptConditions.getConditions().stream().map(ScriptCondition::getName).toList(), false);
                this.handleInput(viewer, Lang.EDITOR_ARENA_SCRIPT_ENTER_CONDITION_VALUE, wrapper -> {
                    String[] input2 = wrapper.getTextRaw().split(" ");
                    if (input2.length < 3) {
                        EditorManager.error(player, plugin.getMessage(Lang.EDITOR_ARENA_SCRIPT_ERROR_INVALID_INPUT).getLocalized());
                        return false;
                    }

                    String condName = input2[0];
                    String condOper = input2[1];
                    String condValue = input2[2];

                    ScriptCondition<?, ?> parameter = ScriptConditions.getByName(condName);
                    if (parameter == null) {
                        EditorManager.error(player, plugin.getMessage(Lang.EDITOR_ARENA_SCRIPT_ERROR_INVALID_CONDITION).getLocalized());
                        return false;
                    }

                    Object parsed = parameter.getParser().apply(condValue);

                    ScriptCondition.Operator operator = ScriptCondition.Operator.fromString(condOper).orElse(ScriptCondition.Operator.EQUAL);
                    this.object.getConditions().computeIfAbsent(section, k -> new ArrayList<>()).add(new ScriptPreparedCondition(parameter, parsed, operator));
                    this.category.save();
                    return true;
                });
                return;
            }

            if (event.isRightClick()) {
                this.object.getConditions().computeIfAbsent(section, k -> new ArrayList<>()).clear();
                this.category.save();
                this.openNextTick(viewer, viewer.getPage());
            }
        };
    }
}
