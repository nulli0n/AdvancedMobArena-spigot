package su.nightexpress.ama.arena.editor.script;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.menu.AutoPaged;
import su.nexmedia.engine.api.menu.click.ItemClick;
import su.nexmedia.engine.api.menu.impl.EditorMenu;
import su.nexmedia.engine.api.menu.impl.MenuOptions;
import su.nexmedia.engine.api.menu.impl.MenuViewer;
import su.nexmedia.engine.editor.EditorManager;
import su.nexmedia.engine.utils.ItemReplacer;
import su.nightexpress.ama.AMA;
import su.nightexpress.ama.Placeholders;
import su.nightexpress.ama.arena.script.action.Parameter;
import su.nightexpress.ama.arena.script.action.ScriptPreparedAction;
import su.nightexpress.ama.arena.script.impl.ArenaScript;
import su.nightexpress.ama.arena.script.impl.ScriptCategory;
import su.nightexpress.ama.config.Lang;
import su.nightexpress.ama.editor.EditorLocales;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.IntStream;

public class ScriptParametersEditor extends EditorMenu<AMA, ScriptPreparedAction> implements AutoPaged<Parameter<?>> {

    public static final String TITLE = "Script Parameters";

    private final ScriptCategory category;
    private final ArenaScript script;

    public ScriptParametersEditor(@NotNull ScriptCategory category, @NotNull ArenaScript script, @NotNull ScriptPreparedAction action) {
        super(category.plugin(), action, TITLE + " [" + script.getId() + "]", 45);
        this.script = script;
        this.category = category;

        this.addReturn(40).setClick((viewer, event) -> {
            script.getActionsEditor(category).openNextTick(viewer, 1);
        });
        this.addNextPage(44);
        this.addPreviousPage(36);
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
    public List<Parameter<?>> getObjects(@NotNull Player player) {
        return this.object.getAction().getParameters().stream().sorted(Comparator.comparing(Parameter::getName)).toList();
    }

    @Override
    @NotNull
    public ItemStack getObjectStack(@NotNull Player player, @NotNull Parameter<?> parameter) {
        String value = String.valueOf(this.object.getParameters().get(parameter, null));

        ItemStack item = parameter.getIcon();
        ItemReplacer.create(item).readLocale(EditorLocales.SCRIPT_ACTION_PARAMETER_OBJECT).trimmed().hideFlags()
            .replace(Placeholders.SCRIPT_PARAMETER_NAME, parameter.getName())
            .replace(Placeholders.SCRIPT_PARAMETER_VALUE, value)
            .writeMeta();
        return item;
    }

    @Override
    @NotNull
    public ItemClick getObjectClick(@NotNull Parameter<?> parameter) {
        return (viewer, event) -> {
            if (event.isRightClick()) {
                this.object.getParameters().getParams().remove(parameter);
                this.category.save();
                this.openNextTick(viewer, viewer.getPage());
                return;
            }

            if (event.isLeftClick()) {
                EditorManager.suggestValues(viewer.getPlayer(), parameter.getSuggestions(this.script.getArena(), this.object.getParameters()), true);
                this.handleInput(viewer, Lang.EDITOR_ARENA_SCRIPT_ENTER_PARAMETER_VALUE, wrapper -> {
                    String paramValue = wrapper.getTextRaw();
                    this.object.getParameters().add(parameter, parameter.getParser().apply(paramValue));
                    this.category.save();
                    return true;
                });
            }
        };
    }
}
