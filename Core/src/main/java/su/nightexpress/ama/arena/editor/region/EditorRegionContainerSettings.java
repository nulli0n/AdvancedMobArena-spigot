package su.nightexpress.ama.arena.editor.region;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.editor.EditorButtonType;
import su.nexmedia.engine.api.editor.EditorInput;
import su.nexmedia.engine.api.menu.AbstractMenu;
import su.nexmedia.engine.api.menu.IMenuClick;
import su.nexmedia.engine.api.menu.IMenuItem;
import su.nexmedia.engine.api.menu.MenuItemType;
import su.nexmedia.engine.editor.AbstractEditorMenu;
import su.nexmedia.engine.editor.EditorManager;
import su.nexmedia.engine.utils.ItemUtil;
import su.nexmedia.engine.utils.StringUtil;
import su.nightexpress.ama.AMA;
import su.nightexpress.ama.arena.region.ArenaRegionContainer;
import su.nightexpress.ama.config.Lang;
import su.nightexpress.ama.editor.ArenaEditorType;
import su.nightexpress.ama.editor.ArenaEditorUtils;

import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;

public class EditorRegionContainerSettings extends AbstractEditorMenu<AMA, ArenaRegionContainer> {

    public EditorRegionContainerSettings(@NotNull ArenaRegionContainer container) {
        super(container.getRegion().plugin(), container, ArenaEditorUtils.TITLE_REGION_EDITOR, 45);

        EditorInput<ArenaRegionContainer, ArenaEditorType> input = (player, container2, type, e) -> {
            String msg = StringUtil.color(e.getMessage());
            switch (type) {
                case REGION_CONTAINER_CHANGE_REFILL_AMOUNT_MIN, REGION_CONTAINER_CHANGE_REFILL_AMOUNT_MAX -> {
                    int value = StringUtil.getInteger(msg, 0);
                    if (type == ArenaEditorType.REGION_CONTAINER_CHANGE_REFILL_AMOUNT_MIN) {
                        container2.setMinItems(value);
                    }
                    else container2.setMaxItems(value);
                }
            }

            container2.getRegion().save();
            return true;
        };

        IMenuClick click = (player, type, e) -> {
            if (type instanceof MenuItemType type2) {
                if (type == MenuItemType.RETURN) {
                    container.getRegion().getEditor().getContainerList().open(player, 1);
                }
            }
            else if (type instanceof ArenaEditorType type2) {
                switch (type2) {
                    case REGION_CONTAINER_CHANGE_REFILL_AMOUNT -> {
                        type2 = e.isLeftClick() ? ArenaEditorType.REGION_CONTAINER_CHANGE_REFILL_AMOUNT_MIN : ArenaEditorType.REGION_CONTAINER_CHANGE_REFILL_AMOUNT_MAX;
                        EditorManager.startEdit(player, container, type2, input);
                        EditorManager.tip(player, plugin.getMessage(Lang.Editor_Region_Container_Enter_Amount).getLocalized());
                        player.closeInventory();
                    }
                    case REGION_CONTAINER_CHANGE_REFILL_TRIGGERS -> {
                        ArenaEditorUtils.handleTriggersClick(player, container, type2, e.isRightClick());
                        if (e.isRightClick()) {
                            container.getRegion().save();
                            this.open(player, 1);
                        }
                    }
                    case REGION_CONTAINER_CHANGE_ITEMS -> new ContainerGUI(container).open(player, 1);
                }
            }
        };

        this.loadItems(click);
    }

    @Override
    public void setTypes(@NotNull Map<EditorButtonType, Integer> map) {
        map.put(ArenaEditorType.REGION_CONTAINER_CHANGE_ITEMS, 15);
        map.put(ArenaEditorType.REGION_CONTAINER_CHANGE_REFILL_AMOUNT, 11);
        map.put(ArenaEditorType.REGION_CONTAINER_CHANGE_REFILL_TRIGGERS, 13);
        map.put(MenuItemType.RETURN, 40);
    }

    @Override
    public void onItemPrepare(@NotNull Player player, @NotNull IMenuItem menuItem, @NotNull ItemStack item) {
        super.onItemPrepare(player, menuItem, item);
        ItemUtil.replace(item, this.object.replacePlaceholders());
    }

    @Override
    public boolean cancelClick(@NotNull InventoryClickEvent e, @NotNull SlotType slotType) {
        return true;
    }


    static class ContainerGUI extends AbstractMenu<AMA> {

        private final ArenaRegionContainer container;

        public ContainerGUI(@NotNull ArenaRegionContainer container) {
            super(container.getRegion().plugin(), "Container Content", 27);
            this.container = container;
        }

        @Override
        public void onPrepare(@NotNull Player player, @NotNull Inventory inventory) {
            inventory.setContents(this.container.getItems().toArray(new ItemStack[this.getSize()]));
        }

        @Override
        public void onReady(@NotNull Player player, @NotNull Inventory inventory) {

        }

        @Override
        public void onClose(@NotNull Player player, @NotNull InventoryCloseEvent e) {
            Inventory inventory = e.getInventory();
            this.container.setItems(Stream.of(inventory.getContents()).filter(Objects::nonNull).toList());
            this.container.getRegion().save();
            super.onClose(player, e);

            this.plugin.runTask(c -> this.container.getEditor().open(player, 1), false);
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
