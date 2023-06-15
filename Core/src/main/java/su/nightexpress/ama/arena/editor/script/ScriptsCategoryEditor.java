package su.nightexpress.ama.arena.editor.script;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.menu.AutoPaged;
import su.nexmedia.engine.api.menu.click.ItemClick;
import su.nexmedia.engine.api.menu.impl.EditorMenu;
import su.nexmedia.engine.api.menu.impl.MenuOptions;
import su.nexmedia.engine.api.menu.impl.MenuViewer;
import su.nexmedia.engine.utils.CollectionsUtil;
import su.nexmedia.engine.utils.ItemUtil;
import su.nexmedia.engine.utils.StringUtil;
import su.nightexpress.ama.AMA;
import su.nightexpress.ama.Placeholders;
import su.nightexpress.ama.arena.script.action.ScriptPreparedAction;
import su.nightexpress.ama.arena.script.condition.ScriptPreparedCondition;
import su.nightexpress.ama.arena.script.impl.ArenaScript;
import su.nightexpress.ama.arena.script.impl.ScriptCategory;
import su.nightexpress.ama.config.Lang;
import su.nightexpress.ama.editor.EditorHub;
import su.nightexpress.ama.editor.EditorLocales;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class ScriptsCategoryEditor extends EditorMenu<AMA, ScriptCategory> implements AutoPaged<ArenaScript> {

    public ScriptsCategoryEditor(@NotNull ScriptCategory category) {
        super(category.plugin(), category, EditorHub.TITLE_SCRIPT_EDITOR, 45);

        this.addReturn(39).setClick((viewer, event) -> {
            category.getArenaConfig().getScriptManager().getEditor().openNextTick(viewer, 1);
        });
        this.addNextPage(44);
        this.addPreviousPage(36);

        this.addCreation(EditorLocales.SCRIPT_CREATE, 41).setClick((viewer, event) -> {
            this.handleInput(viewer, Lang.EDITOR_ARENA_SCRIPT_ENTER_SCRIPT, wrapper -> {
                category.createScript(StringUtil.lowerCaseUnderscore(wrapper.getTextRaw()));
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
    public List<ArenaScript> getObjects(@NotNull Player player) {
        return this.object.getScripts().stream().sorted(Comparator.comparing(ArenaScript::getId)).toList();
    }

    @Override
    @NotNull
    public ItemStack getObjectStack(@NotNull Player player, @NotNull ArenaScript script) {
        ItemStack item = new ItemStack(Material.MAP);

        String condis = script.getConditions().values()
            .stream().map(list -> list.stream().map(ScriptPreparedCondition::toRaw).map(str -> ChatColor.LIGHT_PURPLE + str).collect(Collectors.joining("\n")))
            .collect(Collectors.joining("\n" + ChatColor.RED + "OR:\n"));

        String actions = script.getActions().stream()
            .map(ScriptPreparedAction::toRaw).map(str -> ChatColor.GOLD + str)
            .collect(Collectors.joining("\n"));

        ItemUtil.mapMeta(item, meta -> {
            meta.setDisplayName(EditorLocales.SCRIPT_OBJECT.getLocalizedName());
            meta.setLore(EditorLocales.SCRIPT_OBJECT.getLocalizedLore());
            meta.addItemFlags(ItemFlag.values());
            ItemUtil.replace(meta, str -> str
                .replace(Placeholders.SCRIPT_ID, script.getId())
                .replace(Placeholders.SCRIPT_CONDITION_SECTION_CONDITIONS, condis)
                .replace(Placeholders.SCRIPT_ACTION_PARAMS, actions)
                .replace(Placeholders.SCRIPT_EVENT_TYPE, script.getEventType().name()));
        });

        return item;
    }

    @Override
    @NotNull
    public ItemClick getObjectClick(@NotNull ArenaScript script) {
        return (viewer, event) -> {
            if (event.isShiftClick() && event.isRightClick()) {
                this.object.getScriptsMap().remove(script.getId());
                this.object.save();
                this.openNextTick(viewer, viewer.getPage());
                return;
            }

            if (event.isLeftClick()) {
                script.getActionsEditor(this.object).openNextTick(viewer, 1);
            }
            else if (event.isRightClick()) {
                script.getConditionsEditor(this.object).openNextTick(viewer, 1);
            }
            else if (event.getClick() == ClickType.DROP) {
                script.setEventType(CollectionsUtil.next(script.getEventType()));
                this.object.save();
                this.openNextTick(viewer, viewer.getPage());
            }
        };
    }
}
