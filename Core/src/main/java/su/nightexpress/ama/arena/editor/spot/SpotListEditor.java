package su.nightexpress.ama.arena.editor.spot;

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
import su.nightexpress.ama.arena.spot.ArenaSpot;
import su.nightexpress.ama.arena.spot.ArenaSpotManager;
import su.nightexpress.ama.config.Lang;
import su.nightexpress.ama.editor.EditorHub;
import su.nightexpress.ama.editor.EditorLocales;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

public class SpotListEditor extends EditorMenu<AMA, ArenaSpotManager> implements AutoPaged<ArenaSpot> {

    public SpotListEditor(@NotNull ArenaSpotManager spotManager) {
        super(spotManager.plugin(), spotManager, EditorHub.TITLE_SPOT_EDITOR, 45);

        this.addReturn(39).setClick((viewer, event) -> {
            spotManager.getArenaConfig().getEditor().openNextTick(viewer, 1);
        });
        this.addNextPage(44);
        this.addPreviousPage(36);

        this.addCreation(EditorLocales.SPOT_CREATE, 41).setClick((viewer, event) -> {
            this.handleInput(viewer, Lang.EDITOR_SPOT_ENTER_ID, wrapper -> {
                if (!spotManager.createSpot(StringUtil.lowerCaseUnderscore(wrapper.getTextRaw()))) {
                    EditorManager.error(viewer.getPlayer(), plugin.getMessage(Lang.EDITOR_SPOT_ERROR_EXISTS).getLocalized());
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
    public List<ArenaSpot> getObjects(@NotNull Player player) {
        return new ArrayList<>(this.object.getSpots());
    }

    @Override
    @NotNull
    public ItemStack getObjectStack(@NotNull Player player, @NotNull ArenaSpot spot) {
        ItemStack item = new ItemStack(Material.END_PORTAL_FRAME);
        ItemUtil.mapMeta(item, meta -> {
            meta.setDisplayName(EditorLocales.SPOT_OBJECT.getLocalizedName());
            meta.setLore(EditorLocales.SPOT_OBJECT.getLocalizedLore());
            meta.addItemFlags(ItemFlag.values());
            ItemUtil.replace(meta, spot.replacePlaceholders());
        });
        return item;
    }

    @Override
    @NotNull
    public ItemClick getObjectClick(@NotNull ArenaSpot spot) {
        return (viewer, event) -> {
            if (event.isShiftClick() && event.isRightClick()) {
                this.object.removeSpot(spot);
                this.openNextTick(viewer, viewer.getPage());
                return;
            }
            spot.getEditor().openNextTick(viewer, 1);
        };
    }
}
