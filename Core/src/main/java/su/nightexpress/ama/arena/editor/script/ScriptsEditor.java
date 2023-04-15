package su.nightexpress.ama.arena.editor.script;

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
import su.nightexpress.ama.arena.script.ArenaScriptManager;
import su.nightexpress.ama.arena.script.impl.ScriptCategory;
import su.nightexpress.ama.config.Lang;
import su.nightexpress.ama.editor.ArenaEditorHub;
import su.nightexpress.ama.editor.ArenaEditorType;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

public class ScriptsEditor extends AbstractEditorMenuAuto<AMA, ArenaScriptManager, ScriptCategory> {

    public ScriptsEditor(@NotNull ArenaScriptManager scriptManager) {
        super(scriptManager.plugin(), scriptManager, ArenaEditorHub.TITLE_SCRIPT_EDITOR, 45);

        EditorInput<ArenaScriptManager, ArenaEditorType> input = (player, scriptManager2, type, e) -> {
            String msg = e.getMessage();
            if (type == ArenaEditorType.SCRIPT_CATEGORY_CREATE) {
                scriptManager2.createCategory(EditorManager.fineId(msg));
            }
            scriptManager2.save();
            return true;
        };

        MenuClick click = (player, type, e) -> {
            if (type instanceof MenuItemType type2) {
                if (type2 == MenuItemType.RETURN) {
                    scriptManager.getArenaConfig().getEditor().open(player, 1);
                }
                else this.onItemClickDefault(player, type2);
            }
            else if (type instanceof ArenaEditorType type2) {
                if (type2 == ArenaEditorType.SCRIPT_CATEGORY_CREATE) {
                    EditorManager.startEdit(player, scriptManager, type2, input);
                    EditorManager.prompt(player, plugin.getMessage(Lang.EDITOR_ARENA_SCRIPT_ENTER_CATEGORY).getLocalized());
                    player.closeInventory();
                }
            }
        };

        this.loadItems(click);
    }

    @Override
    public void setTypes(@NotNull Map<EditorButtonType, Integer> map) {
        map.put(ArenaEditorType.SCRIPT_CATEGORY_CREATE, 41);
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
    protected List<ScriptCategory> getObjects(@NotNull Player player) {
        return new ArrayList<>(this.parent.getCategories());
    }

    @Override
    @NotNull
    protected ItemStack getObjectStack(@NotNull Player player, @NotNull ScriptCategory category) {
        ItemStack item = ArenaEditorType.SCRIPT_CATEGORY_OBJECT.getItem();
        ItemUtil.replace(item, str -> str.replace(Placeholders.SCRIPT_CATEGORY_ID, category.getId()));
        return item;
    }

    @Override
    @NotNull
    protected MenuClick getObjectClick(@NotNull Player player, @NotNull ScriptCategory category) {
        return (player2, type, e) -> {
            if (e.isShiftClick() && e.isRightClick()) {
                this.parent.deleteCategory(category);
                this.open(player2, this.getPage(player2));
                return;
            }
            category.getEditor().open(player, 1);
        };
    }

    @Override
    public boolean cancelClick(@NotNull InventoryClickEvent inventoryClickEvent, @NotNull SlotType slotType) {
        return true;
    }
}
