package su.nightexpress.ama.arena.editor.supply;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.editor.EditorButtonType;
import su.nexmedia.engine.api.editor.EditorInput;
import su.nexmedia.engine.api.menu.MenuClick;
import su.nexmedia.engine.api.menu.MenuItemType;
import su.nexmedia.engine.editor.AbstractEditorMenuAuto;
import su.nexmedia.engine.editor.EditorManager;
import su.nexmedia.engine.utils.Colorizer;
import su.nexmedia.engine.utils.ItemUtil;
import su.nightexpress.ama.AMA;
import su.nightexpress.ama.arena.supply.ArenaSupplyChest;
import su.nightexpress.ama.arena.supply.ArenaSupplyManager;
import su.nightexpress.ama.config.Lang;
import su.nightexpress.ama.editor.ArenaEditorHub;
import su.nightexpress.ama.editor.ArenaEditorType;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

public class SupplyChestListEditor extends AbstractEditorMenuAuto<AMA, ArenaSupplyManager, ArenaSupplyChest> {

    public SupplyChestListEditor(@NotNull ArenaSupplyManager supplyManager) {
        super(supplyManager.plugin(), supplyManager, ArenaEditorHub.TITLE_SUPPLY_EDITOR, 45);

        EditorInput<ArenaSupplyManager, ArenaEditorType> input = (player, supplyManager1, type, e) -> {
            String msg = e.getMessage();
            if (type == ArenaEditorType.SUPPLY_CHEST_CREATE) {
                if (!supplyManager1.createChest(EditorManager.fineId(Colorizer.strip(msg)))) {
                    EditorManager.error(player, plugin.getMessage(Lang.EDITOR_SUPPLY_CHEST_ERROR_EXISTS).getLocalized());
                    return false;
                }
            }
            return true;
        };

        MenuClick click = (player, type, e) -> {
            if (type instanceof MenuItemType type2) {
                if (type == MenuItemType.RETURN) {
                    supplyManager.getArenaConfig().getEditor().open(player, 1);
                }
                else this.onItemClickDefault(player, type2);
            }
            else if (type instanceof ArenaEditorType type2) {
                if (type2 == ArenaEditorType.SUPPLY_CHEST_CREATE) {
                    EditorManager.startEdit(player, supplyManager, type2, input);
                    EditorManager.prompt(player, plugin.getMessage(Lang.EDITOR_SUPPLY_CHEST_ENTER_ID).getLocalized());
                    player.closeInventory();
                }
            }
        };

        this.loadItems(click);
    }

    @Override
    public void setTypes(@NotNull Map<EditorButtonType, Integer> map) {
        map.put(MenuItemType.PAGE_NEXT, 44);
        map.put(MenuItemType.PAGE_PREVIOUS, 36);
        map.put(MenuItemType.RETURN, 39);
        map.put(ArenaEditorType.SUPPLY_CHEST_CREATE, 41);
    }

    @Override
    public int[] getObjectSlots() {
        return IntStream.range(0, 36).toArray();
    }

    @Override
    @NotNull
    protected List<ArenaSupplyChest> getObjects(@NotNull Player player) {
        return this.parent.getChests().stream().sorted(Comparator.comparing(ArenaSupplyChest::getId)).toList();
    }

    @Override
    @NotNull
    protected ItemStack getObjectStack(@NotNull Player player, @NotNull ArenaSupplyChest supplyChest) {
        ItemStack item = ArenaEditorType.SUPPLY_CHEST_OBJECT.getItem();
        supplyChest.getContainer().ifPresent(container -> item.setType(container.getType()));
        ItemUtil.replace(item, supplyChest.replacePlaceholders());
        return item;
    }

    @Override
    @NotNull
    protected MenuClick getObjectClick(@NotNull Player player, @NotNull ArenaSupplyChest supplyChest) {
        return (player2, type, e) -> {
            if (e.isShiftClick()) {
                if (e.isRightClick()) {
                    this.parent.getChestsMap().remove(supplyChest.getId());
                    supplyChest.clear();
                    this.parent.save();
                    this.open(player2, this.getPage(player2));
                    return;
                }
                return;
            }
            if (e.isRightClick() && supplyChest.getLocation() != null) {
                player2.teleport(supplyChest.getLocation());
                return;
            }
            supplyChest.getEditor().open(player2, 1);
        };
    }

    @Override
    public boolean cancelClick(@NotNull InventoryClickEvent e, @NotNull SlotType slotType) {
        return true;
    }
}
