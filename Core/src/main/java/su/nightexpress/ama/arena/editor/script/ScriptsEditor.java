package su.nightexpress.ama.arena.editor.script;

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
import su.nexmedia.engine.utils.ItemUtil;
import su.nexmedia.engine.utils.StringUtil;
import su.nightexpress.ama.AMA;
import su.nightexpress.ama.Placeholders;
import su.nightexpress.ama.arena.script.ArenaScriptManager;
import su.nightexpress.ama.arena.script.impl.ScriptCategory;
import su.nightexpress.ama.config.Lang;
import su.nightexpress.ama.editor.EditorHub;
import su.nightexpress.ama.editor.EditorLocales;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

public class ScriptsEditor extends EditorMenu<AMA, ArenaScriptManager> implements AutoPaged<ScriptCategory> {

    public ScriptsEditor(@NotNull ArenaScriptManager scriptManager) {
        super(scriptManager.plugin(), scriptManager, EditorHub.TITLE_SCRIPT_EDITOR, 45);

        this.addReturn(39).setClick((viewer, event) -> {
            scriptManager.getArenaConfig().getEditor().openNextTick(viewer, 1);
        });
        this.addNextPage(44);
        this.addPreviousPage(36);

        this.addCreation(EditorLocales.SCRIPT_CATEGORY_CREATE, 41).setClick((viewer, event) -> {
            this.handleInput(viewer, Lang.EDITOR_ARENA_SCRIPT_ENTER_CATEGORY, wrapper -> {
                scriptManager.createCategory(StringUtil.lowerCaseUnderscore(wrapper.getTextRaw()));
                scriptManager.save();
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
        return new ArrayList<>(this.object.getCategories());
    }

    @Override
    @NotNull
    public ItemStack getObjectStack(@NotNull Player player, @NotNull ScriptCategory category) {
        ItemStack item = new ItemStack(Material.COMMAND_BLOCK_MINECART);
        ItemUtil.mapMeta(item, meta -> {
            meta.setDisplayName(EditorLocales.SCRIPT_CATEGORY_OBJECT.getLocalizedName());
            meta.setLore(EditorLocales.SCRIPT_CATEGORY_OBJECT.getLocalizedLore());
            meta.addItemFlags(ItemFlag.values());
            ItemUtil.replace(meta, str -> str.replace(Placeholders.SCRIPT_CATEGORY_ID, category.getId()));
        });
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
            category.getEditor().openNextTick(viewer, 1);
        };
    }
}
