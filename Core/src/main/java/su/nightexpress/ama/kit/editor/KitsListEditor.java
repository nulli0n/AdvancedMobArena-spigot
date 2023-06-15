package su.nightexpress.ama.kit.editor;

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
import su.nightexpress.ama.config.Lang;
import su.nightexpress.ama.editor.EditorHub;
import su.nightexpress.ama.editor.EditorLocales;
import su.nightexpress.ama.kit.Kit;
import su.nightexpress.ama.kit.KitManager;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

public class KitsListEditor extends EditorMenu<AMA, KitManager> implements AutoPaged<Kit> {

    public KitsListEditor(@NotNull KitManager kitManager) {
        super(kitManager.plugin(), kitManager, EditorHub.TITLE_KIT_EDITOR, 45);

        this.addReturn(39).setClick((viewer, event) -> {
            this.plugin.getEditor().openNextTick(viewer, 1);
        });
        this.addNextPage(44);
        this.addPreviousPage(36);

        this.addCreation(EditorLocales.KIT_CREATE, 41).setClick((viewer, event) -> {
            this.handleInput(viewer, Lang.EDITOR_KIT_ENTER_ID, wrapper -> {
                String id = StringUtil.lowerCaseUnderscore(wrapper.getTextRaw());
                if (kitManager.getKitById(id) != null) {
                    EditorManager.error(viewer.getPlayer(), plugin.getMessage(Lang.Editor_Kit_Error_Exist).getLocalized());
                    return false;
                }

                Kit kit = new Kit(plugin, plugin.getDataFolder() + "/kits/kits/" + id + ".yml");
                kit.save();
                kitManager.getKitsMap().put(kit.getId(), kit);
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
    public List<Kit> getObjects(@NotNull Player player) {
        return new ArrayList<>(this.object.getKits());
    }

    @Override
    @NotNull
    public ItemStack getObjectStack(@NotNull Player player, @NotNull Kit kit) {
        ItemStack item = new ItemStack(kit.getIcon());
        ItemUtil.mapMeta(item, meta -> {
            meta.setDisplayName(EditorLocales.KIT_OBJECT.getLocalizedName());
            meta.setLore(EditorLocales.KIT_OBJECT.getLocalizedLore());
            meta.addItemFlags(ItemFlag.values());
            ItemUtil.replace(meta, kit.replacePlaceholders());
        });
        return item;
    }

    @Override
    @NotNull
    public ItemClick getObjectClick(@NotNull Kit kit) {
        return (viewer, event) -> {
            if (event.isShiftClick() && event.isRightClick()) {
                if (!kit.getFile().delete()) return;
                kit.clear();
                this.object.getKitsMap().remove(kit.getId());
                this.openNextTick(viewer, viewer.getPage());
                return;
            }
            kit.getEditor().openNextTick(viewer, 1);
        };
    }
}
