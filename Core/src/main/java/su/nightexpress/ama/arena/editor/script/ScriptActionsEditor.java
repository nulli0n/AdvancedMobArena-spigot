package su.nightexpress.ama.arena.editor.script;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.menu.AutoPaged;
import su.nexmedia.engine.api.menu.click.ItemClick;
import su.nexmedia.engine.api.menu.impl.EditorMenu;
import su.nexmedia.engine.api.menu.impl.MenuOptions;
import su.nexmedia.engine.api.menu.impl.MenuViewer;
import su.nexmedia.engine.editor.EditorManager;
import su.nexmedia.engine.utils.ItemUtil;
import su.nexmedia.engine.utils.StringUtil;
import su.nightexpress.ama.AMA;
import su.nightexpress.ama.Placeholders;
import su.nightexpress.ama.arena.script.action.*;
import su.nightexpress.ama.arena.script.impl.ArenaScript;
import su.nightexpress.ama.arena.script.impl.ScriptCategory;
import su.nightexpress.ama.config.Lang;
import su.nightexpress.ama.editor.EditorHub;
import su.nightexpress.ama.editor.EditorLocales;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class ScriptActionsEditor extends EditorMenu<AMA, ArenaScript> implements AutoPaged<ScriptPreparedAction> {

    private final ScriptCategory category;

    public ScriptActionsEditor(@NotNull ScriptCategory category, @NotNull ArenaScript script) {
        super(script.plugin(), script, EditorHub.TITLE_SCRIPT_EDITOR, 45);
        this.category = category;

        this.addReturn(39).setClick((viewer, event) -> {
            category.getEditor().openNextTick(viewer, 1);
        });
        this.addNextPage(44);
        this.addPreviousPage(36);

        this.addCreation(EditorLocales.SCRIPT_ACTION_CREATE, 41).setClick((viewer, event) -> {
            EditorManager.suggestValues(viewer.getPlayer(), ScriptActions.getActions().stream().map(ScriptAction::getName).toList(), true);
            this.handleInput(viewer, Lang.EDITOR_ARENA_SCRIPT_ENTER_ACTION_NAME, wrapper -> {
                String id = StringUtil.lowerCaseUnderscore(wrapper.getTextRaw());
                ScriptAction scriptAction = ScriptActions.getByName(id);
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
            return ChatColor.YELLOW.toString() + ChatColor.BOLD + parameter.getName() + ": " + ChatColor.AQUA + action.getParameters().get(parameter, null);
        }).collect(Collectors.joining("\n"));

        ItemUtil.mapMeta(item, meta -> {
            meta.setDisplayName(EditorLocales.SCRIPT_ACTION_OBJECT.getLocalizedName());
            meta.setLore(EditorLocales.SCRIPT_ACTION_OBJECT.getLocalizedLore());
            meta.addItemFlags(ItemFlag.values());
            ItemUtil.replace(meta, str -> str
                .replace(Placeholders.SCRIPT_ACTION_NAME, action.getAction().getName())
                .replace(Placeholders.SCRIPT_ACTION_PARAMS, params));
        });
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
                EditorManager.suggestValues(viewer.getPlayer(), action.getAction().getParameters().stream().map(Parameter::getName).toList(), false);
                this.handleInput(viewer, Lang.EDITOR_ARENA_SCRIPT_ENTER_ACTION_PARAMETER, wrapper -> {
                    String[] input2 = wrapper.getTextRaw().split(" ");
                    if (input2.length < 2) {
                        EditorManager.error(viewer.getPlayer(), plugin.getMessage(Lang.EDITOR_ARENA_SCRIPT_ERROR_INVALID_INPUT).getLocalized());
                        return false;
                    }

                    String paramName = input2[0];
                    String paramValue = Stream.of(input2).skip(1).collect(Collectors.joining(" "));

                    Parameter<?> parameter = Parameters.getByName(paramName).orElse(null);
                    if (parameter == null || !action.getAction().getParameters().contains(parameter)) {
                        EditorManager.error(viewer.getPlayer(), plugin.getMessage(Lang.EDITOR_ARENA_SCRIPT_ERROR_INVALID_PARAMETER).getLocalized());
                        return false;
                    }
                    if (paramValue.equalsIgnoreCase("null")) {
                        action.getParameters().getParams().remove(parameter);
                    }
                    else {
                        action.getParameters().add(parameter, parameter.getParser().apply(paramValue));
                    }
                    this.category.save();
                    return true;
                });
                return;
            }

            if (event.isRightClick()) {
                this.object.getActions().clear();
                this.category.save();
            }
        };
    }
}
