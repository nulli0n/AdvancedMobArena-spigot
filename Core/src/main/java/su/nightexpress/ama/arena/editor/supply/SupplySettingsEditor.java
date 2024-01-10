package su.nightexpress.ama.arena.editor.supply;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nexmedia.engine.api.editor.InputHandler;
import su.nexmedia.engine.api.editor.InputWrapper;
import su.nexmedia.engine.api.manager.EventListener;
import su.nexmedia.engine.api.menu.impl.EditorMenu;
import su.nexmedia.engine.api.menu.impl.Menu;
import su.nexmedia.engine.api.menu.impl.MenuViewer;
import su.nexmedia.engine.editor.EditorManager;
import su.nexmedia.engine.utils.ItemReplacer;
import su.nightexpress.ama.AMA;
import su.nightexpress.ama.arena.supply.SupplyChest;
import su.nightexpress.ama.config.Lang;
import su.nightexpress.ama.editor.EditorLocales;

import java.util.Objects;
import java.util.stream.Stream;

public class SupplySettingsEditor extends EditorMenu<AMA, SupplyChest> implements EventListener {

    public SupplySettingsEditor(@NotNull AMA plugin, @NotNull SupplyChest chest) {
        super(plugin, chest, "Supply Editor [" + chest.getId() + "]", 36);

        this.addReturn(31).setClick((viewer, event) -> {
            chest.getArenaConfig().getSupplyManager().getEditor().openNextTick(viewer, 1);
        });

        this.addItem(Material.REPEATER, EditorLocales.SUPPLY_CHEST_REFILL_AMOUNT, 11).setClick((viewer, event) -> {
            this.handleInput(viewer, Lang.EDITOR_GENERIC_ENTER_NUMBER, wrapper -> {
                if (event.isLeftClick()) {
                    chest.setMinItems(wrapper.asInt());
                }
                else {
                    chest.setMaxItems(wrapper.asInt());
                }
                chest.getArenaConfig().getSupplyManager().save();
                return true;
            });
        });

        this.addItem(Material.COMPASS, EditorLocales.SUPPLY_CHEST_LOCATION, 13).setClick((viewer, event) -> {
            this.handleInput(viewer, Lang.EDITOR_SUPPLY_CHEST_SET_CONTAINER, wrapper -> {
                return wrapper.getTextRaw().equalsIgnoreCase(chest.getId());
            });
        });

        this.addItem(Material.CHEST_MINECART, EditorLocales.SUPPLY_CHEST_ITEMS, 15).setClick((viewer, event) -> {
            new ContainerGUI(plugin, chest).openNextTick(viewer, 1);
        });

        this.getItems().forEach(menuItem -> menuItem.getOptions().addDisplayModifier((viewer, item) -> {
            ItemReplacer.replace(item, chest.replacePlaceholders());
        }));

        this.registerListeners();
    }

    @Override
    public void clear() {
        super.clear();
        this.unregisterListeners();
    }

    @Override
    public void registerListeners() {
        this.plugin.getPluginManager().registerEvents(this, this.plugin);
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onBlockClick(PlayerInteractEvent e) {
        if (e.getAction() != Action.LEFT_CLICK_BLOCK) return;

        Player player = e.getPlayer();

        InputHandler handler = EditorManager.getInputHandler(player);
        if (handler == null) return;

        Block block = e.getClickedBlock();
        if (block == null) return;

        if (handler.handle(new InputWrapper(this.object.getId()))) {
            e.setUseInteractedBlock(Event.Result.DENY);
            e.setUseItemInHand(Event.Result.DENY);

            if (this.object.getArenaConfig().getSupplyManager().getChest(block) != null) return;

            this.object.setLocation(block.getLocation());
            this.object.getArenaConfig().getSupplyManager().save();
            EditorManager.endEdit(player);
        }
    }

    static class ContainerGUI extends Menu<AMA> {

        private final SupplyChest chest;

        public ContainerGUI(@NotNull AMA plugin, @NotNull SupplyChest chest) {
            super(plugin, "Supply Chest Content", 27);
            this.chest = chest;
        }

        @Override
        public void onReady(@NotNull MenuViewer viewer, @NotNull Inventory inventory) {
            super.onReady(viewer, inventory);
            inventory.setContents(this.chest.getItems().toArray(new ItemStack[this.getOptions().getSize()]));
        }

        @Override
        public void onClick(@NotNull MenuViewer viewer, @Nullable ItemStack item, @NotNull SlotType slotType, int slot, @NotNull InventoryClickEvent event) {
            super.onClick(viewer, item, slotType, slot, event);
            event.setCancelled(false);
        }

        @Override
        public void onClose(@NotNull MenuViewer viewer, @NotNull InventoryCloseEvent event) {
            super.onClose(viewer, event);
            Inventory inventory = event.getInventory();
            this.chest.setItems(Stream.of(inventory.getContents()).filter(Objects::nonNull).toList());
            this.chest.getArenaConfig().getSupplyManager().save();
            this.chest.getEditor().openNextTick(viewer, 1);
        }

        @Override
        public boolean isPersistent() {
            return false;
        }
    }
}
