package su.nightexpress.ama.arena.editor.supply;

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
import su.nightexpress.ama.arena.supply.ArenaSupplyChest;
import su.nightexpress.ama.arena.supply.ArenaSupplyManager;
import su.nightexpress.ama.config.Lang;
import su.nightexpress.ama.editor.EditorHub;
import su.nightexpress.ama.editor.EditorLocales;

import java.util.Comparator;
import java.util.List;
import java.util.stream.IntStream;

public class SupplyChestListEditor extends EditorMenu<AMA, ArenaSupplyManager> implements AutoPaged<ArenaSupplyChest> {

    public SupplyChestListEditor(@NotNull ArenaSupplyManager supplyManager) {
        super(supplyManager.plugin(), supplyManager, EditorHub.TITLE_SUPPLY_EDITOR, 45);

        this.addReturn(39).setClick((viewer, event) -> {
            supplyManager.getArenaConfig().getEditor().openNextTick(viewer, 1);
        });
        this.addNextPage(44);
        this.addPreviousPage(36);

        this.addCreation(EditorLocales.SUPPLY_CHEST_CREATE, 41).setClick((viewer, event) -> {
            this.handleInput(viewer, Lang.EDITOR_SUPPLY_CHEST_ENTER_ID, wrapper -> {
                if (!supplyManager.createChest(StringUtil.lowerCaseUnderscore(wrapper.getTextRaw()))) {
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
    public List<ArenaSupplyChest> getObjects(@NotNull Player player) {
        return this.object.getChests().stream().sorted(Comparator.comparing(ArenaSupplyChest::getId)).toList();
    }

    @Override
    @NotNull
    public ItemStack getObjectStack(@NotNull Player player, @NotNull ArenaSupplyChest supplyChest) {
        ItemStack item = new ItemStack(Material.CHEST);
        supplyChest.getContainer().ifPresent(container -> item.setType(container.getType()));
        ItemUtil.mapMeta(item, meta -> {
            meta.setDisplayName(EditorLocales.SUPPLY_CHEST_OBJECT.getLocalizedName());
            meta.setLore(EditorLocales.SUPPLY_CHEST_OBJECT.getLocalizedLore());
            meta.addItemFlags(ItemFlag.values());
            ItemUtil.replace(meta, supplyChest.replacePlaceholders());
        });
        return item;
    }

    @Override
    @NotNull
    public ItemClick getObjectClick(@NotNull ArenaSupplyChest supplyChest) {
        return (viewer, event) -> {
            Player player = viewer.getPlayer();
            if (event.isShiftClick()) {
                if (event.isRightClick()) {
                    this.object.getChestsMap().remove(supplyChest.getId());
                    supplyChest.clear();
                    this.object.save();
                    this.openNextTick(player, viewer.getPage());
                    return;
                }
                return;
            }
            if (event.isRightClick() && supplyChest.getLocation() != null) {
                player.teleport(supplyChest.getLocation());
                return;
            }
            supplyChest.getEditor().openNextTick(player, 1);
        };
    }
}
