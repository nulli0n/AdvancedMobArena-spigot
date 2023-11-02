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
import su.nexmedia.engine.utils.ItemReplacer;
import su.nightexpress.ama.AMA;
import su.nightexpress.ama.Placeholders;
import su.nightexpress.ama.arena.script.action.ParameterResult;
import su.nightexpress.ama.arena.script.action.ScriptAction;
import su.nightexpress.ama.arena.script.action.ScriptActions;
import su.nightexpress.ama.arena.script.action.ScriptPreparedAction;
import su.nightexpress.ama.arena.script.impl.ArenaScript;
import su.nightexpress.ama.arena.script.impl.ScriptCategory;
import su.nightexpress.ama.config.Lang;
import su.nightexpress.ama.editor.EditorLocales;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static su.nexmedia.engine.utils.Colors2.GRAY;
import static su.nexmedia.engine.utils.Colors2.YELLOW;

public class ScriptActionsEditor extends EditorMenu<AMA, ArenaScript> implements AutoPaged<ScriptPreparedAction> {

    private static final String TITLE = "Script Actions";

    private final ScriptCategory category;

    public ScriptActionsEditor(@NotNull ScriptCategory category, @NotNull ArenaScript script) {
        super(category.plugin(), script, TITLE + " [" + script.getId() + "]", 45);
        this.category = category;

        this.addReturn(39).setClick((viewer, event) -> {
            category.getEditor().openNextTick(viewer, 1);
        });
        this.addNextPage(44);
        this.addPreviousPage(36);

        this.addCreation(EditorLocales.SCRIPT_ACTION_CREATE, 41).setClick((viewer, event) -> {
            EditorManager.suggestValues(viewer.getPlayer(), ScriptActions.getActions().stream().map(ScriptAction::getName).toList(), true);
            this.handleInput(viewer, Lang.EDITOR_ARENA_SCRIPT_ENTER_ACTION_NAME, wrapper -> {
                ScriptAction scriptAction = ScriptActions.getByName(wrapper.getTextRaw());
                if (scriptAction == null) {
                    EditorManager.error(viewer.getPlayer(), plugin.getMessage(Lang.EDITOR_ARENA_SCRIPT_ERROR_INVALID_ACTION).getLocalized());
                    return false;
                }

                ScriptPreparedAction action = new ScriptPreparedAction(scriptAction, new ParameterResult());
                script.getActions().add(action);

                script.getArenaConfig().getScriptManager().save();
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
    public List<ScriptPreparedAction> getObjects(@NotNull Player player) {
        return new ArrayList<>(this.object.getActions());
    }

    @Override
    @NotNull
    public ItemStack getObjectStack(@NotNull Player player, @NotNull ScriptPreparedAction action) {
        ItemStack item = new ItemStack(Material.COMMAND_BLOCK);

        String params = action.getAction().getParameters().stream().map(parameter -> {
            return YELLOW + "> " + GRAY + parameter.getName() + ": " + YELLOW + action.getParameters().get(parameter, null);
        }).map(Colorizer::apply).collect(Collectors.joining("\n"));

        ItemReplacer.create(item).readLocale(EditorLocales.SCRIPT_ACTION_OBJECT).trimmed().hideFlags()
            .replace(Placeholders.SCRIPT_ACTION_NAME, action.getAction().getName())
            .replace(Placeholders.SCRIPT_ACTION_PARAMS, params)
            .writeMeta();
        return item;
    }

    @Override
    @NotNull
    public ItemClick getObjectClick(@NotNull ScriptPreparedAction action) {
        return (viewer, event) -> {
            if (event.isShiftClick() && event.isRightClick()) {
                this.object.getActions().remove(action);
                this.category.save();
                this.openNextTick(viewer, viewer.getPage());
                return;
            }

            if (event.isLeftClick()) {
                new ScriptParametersEditor(this.category, this.object, action).openNextTick(viewer, 1);
                return;
            }

            if (event.isRightClick()) {
                this.object.getActions().clear();
                this.category.save();
            }
        };
    }
}
