package su.nightexpress.ama.arena.editor.arena;

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
import su.nightexpress.ama.arena.ArenaManager;
import su.nightexpress.ama.arena.impl.Arena;
import su.nightexpress.ama.config.Lang;
import su.nightexpress.ama.editor.EditorHub;
import su.nightexpress.ama.editor.EditorLocales;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

public class ArenaListEditor extends EditorMenu<AMA, ArenaManager> implements AutoPaged<Arena> {

    public ArenaListEditor(@NotNull ArenaManager arenaManager) {
        super(arenaManager.plugin(), arenaManager, EditorHub.TITLE_ARENA_EDITOR, 45);

        this.addReturn(39).setClick((viewer, event) -> {
            this.plugin.getEditor().openNextTick(viewer, 1);
        });
        this.addNextPage(44);
        this.addPreviousPage(36);

        this.addCreation(EditorLocales.ARENA_CREATION, 41).setClick((viewer, event) -> {
            this.handleInput(viewer, Lang.EDITOR_ARENA_ENTER_ID, wrapper -> {
                if (!arenaManager.create(StringUtil.lowerCaseUnderscore(wrapper.getTextRaw()))) {
                    EditorManager.error(viewer.getPlayer(), plugin.getMessage(Lang.EDITOR_ARENA_ERROR_EXISTS).getLocalized());
                    return false;
                }
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
    public List<Arena> getObjects(@NotNull Player player) {
        return new ArrayList<>(this.plugin.getArenaManager().getArenas());
    }

    @Override
    @NotNull
    public ItemStack getObjectStack(@NotNull Player player, @NotNull Arena arena) {
        ItemStack item = new ItemStack(Material.MAP);
        ItemUtil.mapMeta(item, meta -> {
            meta.setDisplayName(EditorLocales.ARENA_OBJECT.getLocalizedName());
            meta.setLore(EditorLocales.ARENA_OBJECT.getLocalizedLore());
            meta.addItemFlags(ItemFlag.values());
            ItemUtil.replace(meta, arena.getConfig().replacePlaceholders());
        });
        return item;
    }

    @Override
    @NotNull
    public ItemClick getObjectClick(@NotNull Arena arena) {
        return (viewer, event) -> {
            Player player = viewer.getPlayer();
            if (event.isShiftClick() && event.isRightClick()) {
                this.plugin.getArenaManager().delete(arena);
                this.openNextTick(viewer, viewer.getPage());
                return;
            }
            arena.getConfig().getEditor().openNextTick(viewer, 1);
        };
    }
}
