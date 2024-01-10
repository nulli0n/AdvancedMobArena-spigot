package su.nightexpress.ama.arena.editor.region;

import org.bukkit.Material;
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
import su.nightexpress.ama.arena.region.Region;
import su.nightexpress.ama.arena.region.RegionManager;
import su.nightexpress.ama.config.Lang;
import su.nightexpress.ama.editor.EditorLocales;

import java.util.Comparator;
import java.util.List;
import java.util.stream.IntStream;

public class RegionListEditor extends EditorMenu<AMA, RegionManager> implements AutoPaged<Region> {

    public RegionListEditor(@NotNull AMA plugin, @NotNull RegionManager regionManager) {
        super(plugin, regionManager, "Regions Editor [" + regionManager.getRegions().size() + " regions]", 45);

        this.addReturn(39).setClick((viewer, event) -> {
            regionManager.getArenaConfig().getEditor().openNextTick(viewer, 1);
        });
        this.addNextPage(44);
        this.addPreviousPage(36);

        this.addCreation(EditorLocales.REGION_CREATE, 41).setClick((viewer, event) -> {
            this.handleInput(viewer, Lang.EDITOR_REGION_ENTER_ID, wrapper -> {
                if (!regionManager.createRegion(wrapper.getTextRaw())) {
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
    public List<Region> getObjects(@NotNull Player player) {
        return this.object.getRegions().stream().sorted(Comparator.comparing(Region::getId)).toList();
    }

    @Override
    @NotNull
    public ItemStack getObjectStack(@NotNull Player player, @NotNull Region region) {
        Material material = region.isActive() ? (region.hasProblems() ? Material.CRIMSON_FENCE : Material.OAK_FENCE) : Material.DARK_OAK_FENCE;

        ItemStack item = new ItemStack(material);
        ItemReplacer.create(item).readLocale(EditorLocales.REGION_OBJECT).trimmed().hideFlags()
            .replace(region.getPlaceholders())
            .writeMeta();
        return item;
    }

    @Override
    @NotNull
    public ItemClick getObjectClick(@NotNull Region region) {
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
