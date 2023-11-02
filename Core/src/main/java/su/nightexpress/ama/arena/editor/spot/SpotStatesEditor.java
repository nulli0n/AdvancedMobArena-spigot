package su.nightexpress.ama.arena.editor.spot;

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
import su.nexmedia.engine.utils.StringUtil;
import su.nightexpress.ama.AMA;
import su.nightexpress.ama.arena.spot.ArenaSpot;
import su.nightexpress.ama.arena.spot.SpotState;
import su.nightexpress.ama.config.Lang;
import su.nightexpress.ama.editor.EditorLocales;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

public class SpotStatesEditor extends EditorMenu<AMA, ArenaSpot> implements AutoPaged<SpotState> {

    public SpotStatesEditor(@NotNull ArenaSpot spot) {
        super(spot.plugin(), spot, "Spot States [" + spot.getId() + "]", 45);

        this.addReturn(39).setClick((viewer, event) -> {
            spot.getEditor().openNextTick(viewer, 1);
        });
        this.addNextPage(44);
        this.addPreviousPage(36);

        this.addCreation(EditorLocales.SPOT_STATE_CREATE, 41).setClick((viewer, event) -> {
            this.handleInput(viewer, Lang.EDITOR_SPOT_STATE_ENTER_ID, wrapper -> {
                String id = StringUtil.lowerCaseUnderscore(wrapper.getTextRaw());
                if (spot.getState(id) != null) {
                    EditorManager.error(viewer.getPlayer(), plugin.getMessage(Lang.EDITOR_SPOT_STATE_ERROR_EXISTS).getLocalized());
                    return false;
                }

                SpotState state = new SpotState(spot, id, new ArrayList<>());
                spot.getStates().put(state.getId(), state);
                spot.save();
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
    public List<SpotState> getObjects(@NotNull Player player) {
        return new ArrayList<>(this.object.getStates().values());
    }

    @Override
    @NotNull
    public ItemStack getObjectStack(@NotNull Player player, @NotNull SpotState state) {
        ItemStack item = new ItemStack(Material.ITEM_FRAME);
        ItemReplacer.create(item).readLocale(EditorLocales.SPOT_STATE_OBJECT).trimmed().hideFlags()
            .replace(state.getPlaceholders())
            .writeMeta();
        return item;
    }

    @Override
    @NotNull
    public ItemClick getObjectClick(@NotNull SpotState state) {
        return (viewer, event) -> {
            if (event.isShiftClick()) {
                if (event.isRightClick()) {
                    this.object.getStates().remove(state.getId());
                    this.object.save();
                    this.openNextTick(viewer, viewer.getPage());
                }
                return;
            }

            viewer.getPlayer().closeInventory();
            plugin.getArenaSetupManager().getSpotStateSetupManager().startSetup(viewer.getPlayer(), state);
        };
    }
}
