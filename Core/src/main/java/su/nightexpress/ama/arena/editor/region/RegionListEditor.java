package su.nightexpress.ama.arena.editor.region;

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
import su.nightexpress.ama.arena.region.ArenaRegion;
import su.nightexpress.ama.arena.region.ArenaRegionManager;
import su.nightexpress.ama.config.Lang;
import su.nightexpress.ama.editor.EditorHub;
import su.nightexpress.ama.editor.EditorLocales;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

public class RegionListEditor extends EditorMenu<AMA, ArenaRegionManager> implements AutoPaged<ArenaRegion> {

    public RegionListEditor(@NotNull ArenaRegionManager regionManager) {
        super(regionManager.plugin(), regionManager, EditorHub.TITLE_REGION_EDITOR, 45);

        this.addReturn(39).setClick((viewer, event) -> {
            regionManager.getArenaConfig().getEditor().openNextTick(viewer, 1);
        });
        this.addNextPage(44);
        this.addPreviousPage(36);

        this.addCreation(EditorLocales.REGION_CREATE, 41).setClick((viewer, event) -> {
            this.handleInput(viewer, Lang.EDITOR_REGION_ENTER_ID, wrapper -> {
                if (!regionManager.createRegion(StringUtil.lowerCaseUnderscore(wrapper.getTextRaw()))) {
                    EditorManager.error(viewer.getPlayer(), plugin.getMessage(Lang.EDITOR_REGION_ERROR_EXISTS).getLocalized());
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
    public List<ArenaRegion> getObjects(@NotNull Player player) {
        return new ArrayList<>(this.object.getRegions());
    }

    @Override
    @NotNull
    public ItemStack getObjectStack(@NotNull Player player, @NotNull ArenaRegion region) {
        ItemStack item = new ItemStack(Material.OAK_FENCE);
        ItemUtil.mapMeta(item, meta -> {
            meta.setDisplayName(EditorLocales.REGION_OBJECT.getLocalizedName());
            meta.setLore(EditorLocales.REGION_OBJECT.getLocalizedLore());
            meta.addItemFlags(ItemFlag.values());
            ItemUtil.replace(meta, region.replacePlaceholders());
        });
        return item;
    }

    @Override
    @NotNull
    public ItemClick getObjectClick(@NotNull ArenaRegion region) {
        return (viewer, event) -> {
            if (event.isShiftClick() && event.isRightClick()) {
                if (this.object.removeRegion(region)) {
                    this.openNextTick(viewer, viewer.getPage());
                }
                return;
            }
            region.getEditor().openNextTick(viewer, 1);
        };
    }
}
