package su.nightexpress.ama.arena.editor.supply;

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
import su.nightexpress.ama.arena.supply.SupplyChest;
import su.nightexpress.ama.arena.supply.SupplyManager;
import su.nightexpress.ama.config.Lang;
import su.nightexpress.ama.editor.EditorLocales;

import java.util.Comparator;
import java.util.List;
import java.util.stream.IntStream;

public class SupplyListEditor extends EditorMenu<AMA, SupplyManager> implements AutoPaged<SupplyChest> {

    public SupplyListEditor(@NotNull SupplyManager supplyManager) {
        super(supplyManager.plugin(), supplyManager, "Supplies Editor [" + supplyManager.getArenaConfig().getId() + "]", 45);

        this.addReturn(39).setClick((viewer, event) -> {
            supplyManager.getArenaConfig().getEditor().openNextTick(viewer, 1);
        });
        this.addNextPage(44);
        this.addPreviousPage(36);

        this.addCreation(EditorLocales.SUPPLY_CHEST_CREATE, 41).setClick((viewer, event) -> {
            this.handleInput(viewer, Lang.EDITOR_SUPPLY_CHEST_ENTER_ID, wrapper -> {
                if (!supplyManager.createChest(wrapper.getTextRaw())) {
                    EditorManager.error(viewer.getPlayer(), plugin.getMessage(Lang.EDITOR_SUPPLY_CHEST_ERROR_EXISTS).getLocalized());
                    return false;
                }
                supplyManager.save();
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
    public List<SupplyChest> getObjects(@NotNull Player player) {
        return this.object.getChests().stream().sorted(Comparator.comparing(SupplyChest::getId)).toList();
    }

    @Override
    @NotNull
    public ItemStack getObjectStack(@NotNull Player player, @NotNull SupplyChest supplyChest) {
        ItemStack item = new ItemStack(Material.CHEST);
        supplyChest.getContainer().ifPresent(container -> item.setType(container.getType()));

        ItemReplacer.create(item).readLocale(EditorLocales.SUPPLY_CHEST_OBJECT).trimmed().hideFlags()
            .replace(supplyChest.getPlaceholders())
            .writeMeta();
        return item;
    }

    @Override
    @NotNull
    public ItemClick getObjectClick(@NotNull SupplyChest chest) {
        return (viewer, event) -> {
            Player player = viewer.getPlayer();
            if (event.isShiftClick()) {
                if (event.isRightClick()) {
                    this.object.removeChest(chest);
                    this.openNextTick(player, viewer.getPage());
                    return;
                }
                return;
            }
            if (event.isRightClick() && chest.getLocation() != null) {
                player.teleport(chest.getLocation());
                return;
            }
            chest.getEditor().openNextTick(player, 1);
        };
    }
}
