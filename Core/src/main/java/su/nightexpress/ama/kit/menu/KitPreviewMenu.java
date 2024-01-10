package su.nightexpress.ama.kit.menu;

import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.menu.MenuItemType;
import su.nexmedia.engine.api.menu.impl.ConfigMenu;
import su.nexmedia.engine.api.menu.impl.MenuViewer;
import su.nightexpress.ama.AMA;
import su.nightexpress.ama.data.impl.ArenaUser;
import su.nightexpress.ama.kit.Kit;

public class KitPreviewMenu extends ConfigMenu<AMA> {

    private final Kit kit;

    public static int[] itemSlots;
    public static int[] armorSlots;

    public KitPreviewMenu(@NotNull Kit kit) {
        super(kit.plugin(), kit.plugin().getKitManager().getConfigPreview());
        this.kit = kit;

        if (itemSlots == null || armorSlots == null) {
            itemSlots = cfg.getIntArray("Item_Slots");
            armorSlots = cfg.getIntArray("Armor_Slots");
        }

        this.registerHandler(MenuItemType.class)
            .addClick(MenuItemType.CLOSE, (viewer, event) -> plugin.runTask(task -> viewer.getPlayer().closeInventory()))
            .addClick(MenuItemType.RETURN, (viewer, event) -> {
                Player player = viewer.getPlayer();
                ArenaUser user = plugin.getUserManager().getUserData(player);
                if (user.hasKit(kit)) {
                    plugin.getKitManager().getSelectMenu().openNextTick(player, 1);
                }
                else {
                    plugin.getKitManager().getShopMenu().openNextTick(player, 1);
                }
            });

        this.load();
    }

    @Override
    public void onReady(@NotNull MenuViewer viewer, @NotNull Inventory inventory) {
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

        Player player = viewer.getPlayer();
        player.playSound(player.getLocation(), Sound.BLOCK_CHEST_OPEN, 1.0f, 1.0f);
    }
}
