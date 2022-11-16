package su.nightexpress.ama.kit.menu;

import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.menu.AbstractMenu;
import su.nexmedia.engine.api.menu.IMenuClick;
import su.nexmedia.engine.api.menu.IMenuItem;
import su.nexmedia.engine.api.menu.MenuItemType;
import su.nightexpress.ama.AMA;
import su.nightexpress.ama.data.ArenaUser;
import su.nightexpress.ama.kit.Kit;

public class KitPreviewMenu extends AbstractMenu<AMA> {

    private final Kit kit;

    public static int[] itemSlots;
    public static int[] armorSlots;

    public KitPreviewMenu(@NotNull Kit kit) {
        super(kit.plugin(), kit.plugin().getKitManager().getConfigPreview(), "");
        this.kit = kit;

        if (itemSlots == null || armorSlots == null) {
            itemSlots = cfg.getIntArray("Item_Slots");
            armorSlots = cfg.getIntArray("Armor_Slots");
        }

        IMenuClick click = (player, type, e) -> {
            if (type == null) return;

            if (type instanceof MenuItemType type2) {
                if (type2 == MenuItemType.CLOSE) {
                    player.closeInventory();
                    return;
                }
                if (type2 == MenuItemType.RETURN) {
                    ArenaUser user = plugin.getUserManager().getUserData(player);
                    if (user.hasKit(kit)) {
                        plugin.getKitManager().getSelectMenu().open(player, 1);
                    }
                    else {
                        plugin.getKitManager().getShopMenu().open(player, 1);
                    }
                }
            }
        };

        for (String sId : cfg.getSection("Content")) {
            IMenuItem menuItem = cfg.getMenuItem("Content." + sId, MenuItemType.class);

            if (menuItem.getType() != null) {
                menuItem.setClick(click);
            }
            this.addItem(menuItem);
        }
    }

    @Override
    public void onPrepare(@NotNull Player player, @NotNull Inventory inventory) {
        ItemStack[] items = kit.getItems();
        ItemStack[] armor = kit.getArmor();

        for (int slot = 0; slot < itemSlots.length; slot++) {
            if (slot >= items.length) break;

            ItemStack item = items[slot];
            if (item == null) continue;

            inventory.setItem(itemSlots[slot], item);
        }

        for (int slot = 0; slot < armorSlots.length; slot++) {
            if (slot >= armor.length) break;

            ItemStack item = armor[slot];
            if (item == null) continue;

            inventory.setItem(armorSlots[slot], item);
        }

        player.playSound(player.getLocation(), Sound.BLOCK_CHEST_OPEN, 1.0f, 1.0f);
    }

    @Override
    public void onReady(@NotNull Player player, @NotNull Inventory inventory) {

    }

    @Override
    public boolean cancelClick(@NotNull InventoryClickEvent e, @NotNull SlotType slotType) {
        return true;
    }
}
