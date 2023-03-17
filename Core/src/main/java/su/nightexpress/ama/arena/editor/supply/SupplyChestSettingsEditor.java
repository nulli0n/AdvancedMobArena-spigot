package su.nightexpress.ama.arena.editor.supply;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.editor.EditorButtonType;
import su.nexmedia.engine.api.editor.EditorInput;
import su.nexmedia.engine.api.editor.EditorObject;
import su.nexmedia.engine.api.menu.AbstractMenu;
import su.nexmedia.engine.api.menu.MenuClick;
import su.nexmedia.engine.api.menu.MenuItem;
import su.nexmedia.engine.api.menu.MenuItemType;
import su.nexmedia.engine.editor.AbstractEditorMenu;
import su.nexmedia.engine.editor.EditorManager;
import su.nexmedia.engine.utils.Colorizer;
import su.nexmedia.engine.utils.ItemUtil;
import su.nexmedia.engine.utils.StringUtil;
import su.nightexpress.ama.AMA;
import su.nightexpress.ama.arena.supply.ArenaSupplyChest;
import su.nightexpress.ama.config.Lang;
import su.nightexpress.ama.editor.ArenaEditorHub;
import su.nightexpress.ama.editor.ArenaEditorType;

import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;

public class SupplyChestSettingsEditor extends AbstractEditorMenu<AMA, ArenaSupplyChest> {

    public SupplyChestSettingsEditor(@NotNull ArenaSupplyChest container) {
        super(container.plugin(), container, ArenaEditorHub.TITLE_SUPPLY_EDITOR, 36);

        EditorInput<ArenaSupplyChest, ArenaEditorType> input = (player, container2, type, e) -> {
            String msg = e.getMessage();
            switch (type) {
                case SUPPLY_CHEST_CHANGE_REFILL_AMOUNT_MIN, SUPPLY_CHEST_CHANGE_REFILL_AMOUNT_MAX -> {
                    int value = StringUtil.getInteger(Colorizer.strip(msg), 0);
                    if (type == ArenaEditorType.SUPPLY_CHEST_CHANGE_REFILL_AMOUNT_MIN) {
                        container2.setMinItems(value);
                    }
                    else container2.setMaxItems(value);
                }
            }

            container2.getArenaConfig().getSupplyManager().save();
            return true;
        };

        MenuClick click = (player, type, e) -> {
            if (type instanceof MenuItemType type2) {
                if (type == MenuItemType.RETURN) {
                    container.getArenaConfig().getSupplyManager().getEditor().open(player, 1);
                }
            }
            else if (type instanceof ArenaEditorType type2) {
                switch (type2) {
                    case SUPPLY_CHEST_CHANGE_REFILL_AMOUNT -> {
                        type2 = e.isLeftClick() ? ArenaEditorType.SUPPLY_CHEST_CHANGE_REFILL_AMOUNT_MIN : ArenaEditorType.SUPPLY_CHEST_CHANGE_REFILL_AMOUNT_MAX;
                        EditorManager.startEdit(player, container, type2, input);
                        EditorManager.tip(player, plugin.getMessage(Lang.EDITOR_GENERIC_ENTER_NUMBER).getLocalized());
                        player.closeInventory();
                    }
                    case SUPPLY_CHEST_CHANGE_LOCATION -> {
                        EditorManager.startEdit(player, container, type2, input);
                        EditorManager.tip(player, plugin.getMessage(Lang.EDITOR_SUPPLY_CHEST_SET_CONTAINER).getLocalized());
                        player.closeInventory();
                    }
                    case SUPPLY_CHEST_CHANGE_ITEMS -> new ContainerGUI(container).open(player, 1);
                }
            }
        };

        this.loadItems(click);
    }

    @Override
    public void setTypes(@NotNull Map<EditorButtonType, Integer> map) {
        map.put(ArenaEditorType.SUPPLY_CHEST_CHANGE_ITEMS, 15);
        map.put(ArenaEditorType.SUPPLY_CHEST_CHANGE_LOCATION, 13);
        map.put(ArenaEditorType.SUPPLY_CHEST_CHANGE_REFILL_AMOUNT, 11);
        map.put(MenuItemType.RETURN, 31);
    }

    @Override
    public void onItemPrepare(@NotNull Player player, @NotNull MenuItem menuItem, @NotNull ItemStack item) {
        super.onItemPrepare(player, menuItem, item);
        ItemUtil.replace(item, this.object.replacePlaceholders());
    }

    @Override
    public boolean cancelClick(@NotNull InventoryClickEvent e, @NotNull SlotType slotType) {
        return true;
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onBlockClick(PlayerInteractEvent e) {
        Player player = e.getPlayer();

        EditorObject<?, ?> editor = EditorManager.getEditorInput(player);
        if (editor == null) return;

        Block block = e.getClickedBlock();
        if (block == null) return;

        if (editor.getType() == ArenaEditorType.SUPPLY_CHEST_CHANGE_LOCATION) {
            e.setUseInteractedBlock(Event.Result.DENY);
            e.setUseItemInHand(Event.Result.DENY);

            if (this.object.getArenaConfig().getSupplyManager().getChest(block) != null) return;

            ArenaSupplyChest supplyChest = (ArenaSupplyChest) editor.getObject();
            supplyChest.setLocation(block.getLocation());
            supplyChest.getArenaConfig().getSupplyManager().save();
            EditorManager.endEdit(player);
        }
    }

    static class ContainerGUI extends AbstractMenu<AMA> {

        private final ArenaSupplyChest container;

        public ContainerGUI(@NotNull ArenaSupplyChest container) {
            super(container.plugin(), "Supply Chest Content", 27);
            this.container = container;
        }

        @Override
        public boolean onPrepare(@NotNull Player player, @NotNull Inventory inventory) {
            inventory.setContents(this.container.getItems().toArray(new ItemStack[this.getSize()]));
            return true;
        }

        @Override
        public void onClose(@NotNull Player player, @NotNull InventoryCloseEvent e) {
            Inventory inventory = e.getInventory();
            this.container.setItems(Stream.of(inventory.getContents()).filter(Objects::nonNull).toList());
            this.container.getArenaConfig().getSupplyManager().save();
            this.plugin.runTask(task -> this.container.getEditor().open(player, 1));
            super.onClose(player, e);
        }

        @Override
        public boolean destroyWhenNoViewers() {
            return true;
        }

        @Override
        public boolean cancelClick(@NotNull InventoryClickEvent e, @NotNull SlotType slotType) {
            return false;
        }
    }
}
