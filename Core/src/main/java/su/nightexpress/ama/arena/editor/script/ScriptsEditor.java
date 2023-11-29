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
import su.nexmedia.engine.utils.ItemReplacer;
import su.nexmedia.engine.utils.NumberUtil;
import su.nightexpress.ama.AMA;
import su.nightexpress.ama.Placeholders;
import su.nightexpress.ama.arena.script.ScriptManager;
import su.nightexpress.ama.arena.script.impl.ScriptCategory;
import su.nightexpress.ama.config.Lang;
import su.nightexpress.ama.editor.EditorLocales;

import java.util.Comparator;
import java.util.List;
import java.util.stream.IntStream;

public class ScriptsEditor extends EditorMenu<AMA, ScriptManager> implements AutoPaged<ScriptCategory> {

    private static final String TITLE = "Scripts Editor";

    public ScriptsEditor(@NotNull AMA plugin, @NotNull ScriptManager scriptManager) {
        super(plugin, scriptManager, TITLE + " [" + scriptManager.getArena().getId() + "]", 45);

        this.addReturn(39).setClick((viewer, event) -> {
            scriptManager.getArenaConfig().getEditor().openNextTick(viewer, 1);
        });
        this.addNextPage(44);
        this.addPreviousPage(36);

        this.addCreation(EditorLocales.SCRIPT_CATEGORY_CREATE, 41).setClick((viewer, event) -> {
            this.handleInput(viewer, Lang.EDITOR_ARENA_SCRIPT_ENTER_CATEGORY, wrapper -> {
                scriptManager.createCategory(wrapper.getTextRaw());
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
    public List<ScriptCategory> getObjects(@NotNull Player player) {
        return this.object.getCategories().stream()
            .sorted(Comparator.comparing(ScriptCategory::getPriority, Comparator.reverseOrder()))
            .toList();
    }

    @Override
    @NotNull
    public ItemStack getObjectStack(@NotNull Player player, @NotNull ScriptCategory category) {
        ItemStack item = new ItemStack(Material.COMMAND_BLOCK_MINECART);
        ItemReplacer.create(item).readLocale(EditorLocales.SCRIPT_CATEGORY_OBJECT).trimmed().hideFlags()
            .replace(Placeholders.SCRIPT_CATEGORY_ID, category.getId())
            .replace(Placeholders.SCRIPT_CATEGORY_PRIORITY, NumberUtil.format(category.getPriority()))
            .writeMeta();
        return item;
    }

    @Override
    @NotNull
    public ItemClick getObjectClick(@NotNull ScriptCategory category) {
        return (viewer, event) -> {
            if (event.isShiftClick() && event.isRightClick()) {
                this.object.deleteCategory(category);
                this.openNextTick(viewer, viewer.getPage());
                return;
            }
            if (event.isRightClick()) {
                this.handleInput(viewer, Lang.EDITOR_GENERIC_ENTER_PRIORITY, wrapper -> {
                    category.setPriority(wrapper.asInt());
                    category.save();
                    return true;
                });
                return;
            }
            category.getEditor().openNextTick(viewer, 1);
        };
    }
}
