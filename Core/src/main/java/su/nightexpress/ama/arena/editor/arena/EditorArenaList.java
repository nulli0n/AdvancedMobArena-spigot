package su.nightexpress.ama.arena.editor.arena;

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
import su.nightexpress.ama.arena.AbstractArena;
import su.nightexpress.ama.arena.ArenaManager;
import su.nightexpress.ama.arena.config.ArenaConfig;
import su.nightexpress.ama.config.Lang;
import su.nightexpress.ama.editor.ArenaEditorType;
import su.nightexpress.ama.editor.ArenaEditorUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

public class EditorArenaList extends AbstractEditorMenuAuto<AMA, AMA, AbstractArena> {

    public EditorArenaList(@NotNull AMA plugin) {
        super(plugin, plugin, ArenaEditorUtils.TITLE_ARENA_EDITOR, 45);

        EditorInput<ArenaManager, ArenaEditorType> input = (player, arenaManager, type, e) -> {
            String msg = e.getMessage();
            if (type == ArenaEditorType.ARENA_CREATE) {
                String id = EditorManager.fineId(msg);
                if (arenaManager.getArenaById(id) != null) {
                    EditorManager.error(player, plugin.getMessage(Lang.Editor_Arena_Error_Exist).getLocalized());
                    return false;
                }

                ArenaConfig arenaConfig = new ArenaConfig(plugin, plugin.getDataFolder() + "/arenas/" + id + "/" + id + ".yml");
                arenaConfig.save();
                arenaManager.getArenasMap().put(arenaConfig.getId(), arenaConfig.getArena());
            }
            return true;
        };

        MenuClick click = (player, type, e) -> {
            if (type instanceof MenuItemType type2) {
                if (type2 == MenuItemType.RETURN) {
                    plugin.getEditor().open(player, 1);
                }
                else super.onItemClickDefault(player, type2);
            }
            else if (type instanceof ArenaEditorType type2) {
                if (type2 == ArenaEditorType.ARENA_CREATE) {
                    EditorManager.startEdit(player, plugin.getArenaManager(), type2, input);
                    EditorManager.tip(player, plugin.getMessage(Lang.Editor_Arena_Tip_Create).getLocalized());
                    player.closeInventory();
                }
            }
        };

        this.loadItems(click);
    }

    @Override
    public void setTypes(@NotNull Map<EditorButtonType, Integer> map) {
        map.put(ArenaEditorType.ARENA_CREATE, 41);
        map.put(MenuItemType.RETURN, 39);
        map.put(MenuItemType.PAGE_NEXT, 44);
        map.put(MenuItemType.PAGE_PREVIOUS, 36);
    }

    @Override
    public int[] getObjectSlots() {
        return IntStream.range(0, 36).toArray();
    }

    @Override
    @NotNull
    protected List<AbstractArena> getObjects(@NotNull Player player) {
        return new ArrayList<>(this.plugin.getArenaManager().getArenas());
    }

    @Override
    @NotNull
    protected ItemStack getObjectStack(@NotNull Player player, @NotNull AbstractArena arena) {
        ItemStack item = ArenaEditorType.ARENA_OBJECT.getItem();
        ItemUtil.replace(item, arena.getConfig().replacePlaceholders());
        return item;
    }

    @Override
    @NotNull
    protected MenuClick getObjectClick(@NotNull Player player, @NotNull AbstractArena arena) {
        return (player2, type, e) -> {
            if (e.isShiftClick() && e.isRightClick()) {
                this.plugin.getArenaManager().delete(arena);
                this.open(player2, this.getPage(player2));
                return;
            }
            arena.getConfig().getEditor().open(player2, 1);
        };
    }

    @Override
    public boolean cancelClick(@NotNull InventoryClickEvent e, @NotNull SlotType slotType) {
        return true;
    }
}
