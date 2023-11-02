package su.nightexpress.ama.kit.editor;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.menu.AutoPaged;
import su.nexmedia.engine.api.menu.click.ItemClick;
import su.nexmedia.engine.api.menu.impl.EditorMenu;
import su.nexmedia.engine.api.menu.impl.MenuOptions;
import su.nexmedia.engine.api.menu.impl.MenuViewer;
import su.nexmedia.engine.editor.EditorManager;
import su.nexmedia.engine.utils.ItemReplacer;
import su.nightexpress.ama.AMA;
import su.nightexpress.ama.config.Lang;
import su.nightexpress.ama.editor.EditorLocales;
import su.nightexpress.ama.kit.Kit;
import su.nightexpress.ama.kit.KitManager;

import java.util.Comparator;
import java.util.List;
import java.util.stream.IntStream;

public class KitsListEditor extends EditorMenu<AMA, KitManager> implements AutoPaged<Kit> {

    private static final String TITLE = "Kit Editor";

    public KitsListEditor(@NotNull KitManager kitManager) {
        super(kitManager.plugin(), kitManager, TITLE + " [" + kitManager.getKits().size() + " kits]", 45);

        this.addReturn(39).setClick((viewer, event) -> {
            this.plugin.getEditor().openNextTick(viewer, 1);
        });
        this.addNextPage(44);
        this.addPreviousPage(36);

        this.addCreation(EditorLocales.KIT_CREATE, 41).setClick((viewer, event) -> {
            this.handleInput(viewer, Lang.EDITOR_KIT_ENTER_ID, wrapper -> {
                if (!kitManager.createKit(wrapper.getTextRaw())) {
                    EditorManager.error(viewer.getPlayer(), plugin.getMessage(Lang.EDITOR_KIT_ERROR_EXIST).getLocalized());
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
    public List<Kit> getObjects(@NotNull Player player) {
        return this.object.getKits().stream().sorted(Comparator.comparing(Kit::getId)).toList();
    }

    @Override
    @NotNull
    public ItemStack getObjectStack(@NotNull Player player, @NotNull Kit kit) {
        ItemStack item = kit.getIcon();
        ItemReplacer.create(item).readLocale(EditorLocales.KIT_OBJECT).trimmed().hideFlags()
            .replace(kit.getPlaceholders())
            .writeMeta();
        return item;
    }

    @Override
    @NotNull
    public ItemClick getObjectClick(@NotNull Kit kit) {
        return (viewer, event) -> {
            if (event.isShiftClick() && event.isRightClick()) {
                if (this.object.deleteKit(kit)) {
                    this.openNextTick(viewer, viewer.getPage());
                }
                return;
            }
            kit.getEditor().openNextTick(viewer, 1);
        };
    }
}
