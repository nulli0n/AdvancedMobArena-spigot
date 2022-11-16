package su.nightexpress.ama.editor;

import org.bukkit.event.inventory.InventoryClickEvent;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.editor.EditorButtonType;
import su.nexmedia.engine.api.menu.IMenuClick;
import su.nexmedia.engine.api.menu.MenuItemType;
import su.nexmedia.engine.editor.AbstractEditorMenu;
import su.nightexpress.ama.AMA;
import su.nightexpress.ama.arena.editor.arena.EditorArenaList;
import su.nightexpress.ama.kit.editor.EditorKitList;
import su.nightexpress.ama.mob.editor.EditorMobList;

import java.util.Map;

public class ArenaEditorHub extends AbstractEditorMenu<AMA, AMA> {

    private EditorArenaList arenaEditor;
    private EditorKitList   kitEditor;
    private EditorMobList   mobEditor;

    public ArenaEditorHub(@NotNull AMA plugin) {
        super(plugin, plugin, ArenaEditorUtils.TITLE_EDITOR, 36);

        IMenuClick click = (player, type, e) -> {
            if (type instanceof MenuItemType type2) {
                if (type2 == MenuItemType.CLOSE) {
                    player.closeInventory();
                }
            }
            else if (type instanceof ArenaEditorType type2) {
                switch (type2) {
                    case EDITOR_ARENA -> this.getArenaEditor().open(player, 1);
                    case EDITOR_KITS -> this.getKitEditor().open(player, 1);
                    case EDITOR_MOBS -> this.getMobEditor().open(player, 1);
                    default -> {}
                }
            }
        };

        this.loadItems(click);
    }

    @NotNull
    public EditorArenaList getArenaEditor() {
        if (this.arenaEditor == null) {
            this.arenaEditor = new EditorArenaList(this.plugin);
        }
        return this.arenaEditor;
    }

    @NotNull
    public EditorKitList getKitEditor() {
        if (this.kitEditor == null) {
            this.kitEditor = new EditorKitList(plugin.getKitManager());
        }
        return this.kitEditor;
    }

    @NotNull
    public EditorMobList getMobEditor() {
        if (this.mobEditor == null) {
            this.mobEditor = new EditorMobList(plugin.getMobManager());
        }
        return mobEditor;
    }

    @Override
    public void clear() {
        if (this.arenaEditor != null) {
            this.arenaEditor.clear();
            this.arenaEditor = null;
        }
        if (this.kitEditor != null) {
            this.kitEditor.clear();
            this.kitEditor = null;
        }
        super.clear();
    }

    @Override
    public void setTypes(@NotNull Map<EditorButtonType, Integer> map) {
        map.put(ArenaEditorType.EDITOR_ARENA, 11);
        map.put(ArenaEditorType.EDITOR_KITS, 13);
        map.put(ArenaEditorType.EDITOR_MOBS, 15);
        map.put(MenuItemType.CLOSE, 31);
    }

    @Override
    public boolean cancelClick(@NotNull InventoryClickEvent e, @NotNull SlotType slotType) {
        return true;
    }
}
