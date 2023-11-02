package su.nightexpress.ama.arena.editor.script;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nexmedia.engine.api.menu.AutoPaged;
import su.nexmedia.engine.api.menu.click.ItemClick;
import su.nexmedia.engine.api.menu.impl.EditorMenu;
import su.nexmedia.engine.api.menu.impl.MenuOptions;
import su.nexmedia.engine.api.menu.impl.MenuViewer;
import su.nexmedia.engine.lang.LangManager;
import su.nexmedia.engine.utils.CollectionsUtil;
import su.nexmedia.engine.utils.Colorizer;
import su.nexmedia.engine.utils.ItemReplacer;
import su.nightexpress.ama.AMA;
import su.nightexpress.ama.Placeholders;
import su.nightexpress.ama.arena.script.action.ScriptPreparedAction;
import su.nightexpress.ama.arena.script.condition.ScriptPreparedCondition;
import su.nightexpress.ama.arena.script.impl.ArenaScript;
import su.nightexpress.ama.arena.script.impl.ScriptCategory;
import su.nightexpress.ama.config.Lang;
import su.nightexpress.ama.editor.EditorLocales;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static su.nexmedia.engine.utils.Colors2.*;

public class ScriptsCategoryEditor extends EditorMenu<AMA, ScriptCategory> implements AutoPaged<ArenaScript> {

    public static final String TITLE = "Scripts";

    public ScriptsCategoryEditor(@NotNull ScriptCategory category) {
        super(category.plugin(), category, TITLE + " [" + category.getId() + "]", 45);

        this.addReturn(39).setClick((viewer, event) -> {
            category.getArenaConfig().getScriptManager().getEditor().openNextTick(viewer, 1);
        });
        this.addNextPage(44);
        this.addPreviousPage(36);

        this.addCreation(EditorLocales.SCRIPT_CREATE, 41).setClick((viewer, event) -> {
            this.handleInput(viewer, Lang.EDITOR_ARENA_SCRIPT_ENTER_SCRIPT, wrapper -> {
                category.createScript(wrapper.getTextRaw());
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
        ItemStack item = script.getIcon();

        String condis = script.getConditions().values()
            .stream().map(list -> list.stream().map(ScriptPreparedCondition::toRaw).map(str -> PURPLE + str)
                .collect(Collectors.joining("\n")))
            .collect(Collectors.joining("\n" + RED + BOLD + "   else:\n"));

        String actions = script.getActions().stream()
            .map(ScriptPreparedAction::toRaw).map(str -> ORANGE + str)
            .collect(Collectors.joining("\n"));

        ItemReplacer.create(item).readLocale(EditorLocales.SCRIPT_OBJECT).trimmed().hideFlags()
            .replace(Placeholders.SCRIPT_ID, script.getId())
            .replace(Placeholders.SCRIPT_CONDITION_SECTION_CONDITIONS, Colorizer.apply(condis))
            .replace(Placeholders.SCRIPT_ACTION_PARAMS, Colorizer.apply(actions))
            .replace(Placeholders.SCRIPT_IN_GAME_ONLY, LangManager.getBoolean(script.isInGameOnly()))
            .replace(Placeholders.SCRIPT_EVENT_TYPE, script.getEventType().name())
            .writeMeta();
        return item;
    }

    @Override
    @NotNull
    public ItemClick getObjectClick(@NotNull ArenaScript script) {
        return (viewer, event) -> {
            ItemStack cursor = event.getCursor();
            if (cursor != null && !cursor.getType().isAir()) {
                script.setIcon(cursor);
                this.object.save();
                this.openNextTick(viewer, viewer.getPage());
                event.getView().setCursor(null);
                return;
            }

            if (event.isShiftClick() && event.isRightClick()) {
                this.object.getScriptsMap().remove(script.getId());
                this.object.save();
                this.openNextTick(viewer, viewer.getPage());
                return;
            }
            if (event.isShiftClick() && event.isLeftClick()) {
                script.setInGameOnly(!script.isInGameOnly());
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

    @Override
    public void onClick(@NotNull MenuViewer viewer, @Nullable ItemStack item, @NotNull SlotType slotType, int slot, @NotNull InventoryClickEvent event) {
        super.onClick(viewer, item, slotType, slot, event);
        if (slotType == SlotType.PLAYER || slotType == SlotType.PLAYER_EMPTY) {
            event.setCancelled(false);
        }
    }
}
