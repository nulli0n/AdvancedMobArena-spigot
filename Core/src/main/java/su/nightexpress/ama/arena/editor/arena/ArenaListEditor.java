package su.nightexpress.ama.arena.editor.arena;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nexmedia.engine.api.menu.AutoPaged;
import su.nexmedia.engine.api.menu.click.ItemClick;
import su.nexmedia.engine.api.menu.impl.EditorMenu;
import su.nexmedia.engine.api.menu.impl.MenuOptions;
import su.nexmedia.engine.api.menu.impl.MenuViewer;
import su.nexmedia.engine.editor.EditorManager;
import su.nexmedia.engine.utils.ItemReplacer;
import su.nexmedia.engine.utils.NumberUtil;
import su.nightexpress.ama.AMA;
import su.nightexpress.ama.Placeholders;
import su.nightexpress.ama.arena.ArenaManager;
import su.nightexpress.ama.arena.impl.Arena;
import su.nightexpress.ama.config.Lang;
import su.nightexpress.ama.editor.EditorLocales;

import java.util.Comparator;
import java.util.List;
import java.util.stream.IntStream;

public class ArenaListEditor extends EditorMenu<AMA, ArenaManager> implements AutoPaged<Arena> {

    private static final String TITLE = "Arena Editor [" + Placeholders.GENERIC_AMOUNT + " arenas]";

    public ArenaListEditor(@NotNull ArenaManager arenaManager) {
        super(arenaManager.plugin(), arenaManager, TITLE, 45);

        this.addReturn(39).setClick((viewer, event) -> {
            this.plugin.getEditor().openNextTick(viewer, 1);
        });
        this.addNextPage(44);
        this.addPreviousPage(36);

        this.addCreation(EditorLocales.ARENA_CREATION, 41).setClick((viewer, event) -> {
            this.handleInput(viewer, Lang.EDITOR_ARENA_ENTER_ID, wrapper -> {
                if (!arenaManager.create(wrapper.getTextRaw())) {
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

        options.setTitle(options.getTitle()
            .replace(Placeholders.GENERIC_AMOUNT, NumberUtil.format(plugin.getArenaManager().getArenas().size()))
        );

        this.getItemsForPage(viewer).forEach(this::addItem);
    }

    @Override
    public int[] getObjectSlots() {
        return IntStream.range(0, 36).toArray();
    }

    @Override
    @NotNull
    public List<Arena> getObjects(@NotNull Player player) {
        return this.plugin.getArenaManager().getArenas().stream().sorted(Comparator.comparing(Arena::getId)).toList();
    }

    @Override
    @NotNull
    public ItemStack getObjectStack(@NotNull Player player, @NotNull Arena arena) {
        ItemStack item = arena.getConfig().getIcon();
        ItemReplacer.create(item).readLocale(EditorLocales.ARENA_OBJECT).trimmed().hideFlags()
            .replace(Placeholders.forArenaAll(arena.getConfig()).replacer())
            .writeMeta();
        return item;
    }

    @Override
    @NotNull
    public ItemClick getObjectClick(@NotNull Arena arena) {
        return (viewer, event) -> {
            ItemStack cursor = event.getCursor();
            if (cursor != null && !cursor.getType().isAir()) {
                arena.getConfig().setIcon(cursor);
                arena.getConfig().save();
                this.openNextTick(viewer, viewer.getPage());
                event.getView().setCursor(null);
                return;
            }

            if (event.isShiftClick() && event.isRightClick()) {
                this.plugin.getArenaManager().delete(arena);
                this.openNextTick(viewer, viewer.getPage());
                return;
            }
            arena.getConfig().getEditor().openNextTick(viewer, 1);
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
